package com.setting.service;

import com.app.application.db.Etablissement;
import com.app.application.db.UserEtablissement;
import com.app.application.db.repo.UserEtablissementRepository;
import com.axelor.auth.db.Group;
import com.axelor.auth.db.Role;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.GroupRepository;
import com.axelor.auth.db.repo.RoleRepository;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.inject.Beans;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.UserEtablissementRole;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Singleton
@Transactional
public class UserEtablissementService {
  public static void SetPermissionUser(User user) {
    if (user.getGroup() == null) return;
    if (user.getGroup().getCode().equals("admins")) return;
    if (user.getEtablissementSelectionnee() == null) {
      user.setGroup(getGroupe("userEtablissementZero"));
      user.setRoles(new HashSet<>());
      Beans.get(UserRepository.class).save(user);
      return;
    }

    Etablissement etablissement = user.getEtablissementSelectionnee();
    Beans.get(UserEtablissementRepository.class);
    UserEtablissement userEtablissement =
        Beans.get(UserEtablissementRepository.class)
            .all()
            .filter("users = ? AND etablissements =? ", user, etablissement)
            .fetchOne();
    if (userEtablissement == null) {
      user.setGroup(getGroupe("userEtablissementZero"));
      user.setRoles(new HashSet<>());
      Beans.get(UserRepository.class).save(user);
      return;
    }

    if (userEtablissement != null) {
      Set<Role> roleList = new HashSet<>();
      if (userEtablissement.getFonction().equals("01")
          && etablissement.getTypeEtablissemnt() == 0) {
        user.setGroup(getGroupe("etablissementAdmin"));
      }
      if (userEtablissement.getFonction().equals("01")
          && etablissement.getTypeEtablissemnt() == 1) {
        user.setGroup(getGroupe("directionAdmin"));
      }
      if (userEtablissement.getFonction().equals("01")
          && etablissement.getTypeEtablissemnt() == 10) {
        user.setGroup(getGroupe("centreLangueAdmin"));
      }

      if (userEtablissement.getFonction().equals("02")) {
        if (etablissement.getTypeEtablissemnt() == 0) user.setGroup(getGroupe("etablissementUser"));
        if (etablissement.getTypeEtablissemnt() == 1) user.setGroup(getGroupe("directionUser"));
        if (etablissement.getTypeEtablissemnt() == 10) user.setGroup(getGroupe("centreLangueUser"));

        List<UserEtablissementRole> userEtablissementRoleList =
            userEtablissement.getUserEtablissementRole();
        for (int i = 0; i < userEtablissementRoleList.size(); i++) {
          if (userEtablissementRoleList.get(i).getReadRole())
            roleList.add(userEtablissementRoleList.get(i).getRoleDescribe().getRoleRead());
          if (userEtablissementRoleList.get(i).getWriteRole())
            roleList.add(userEtablissementRoleList.get(i).getRoleDescribe().getRoleWrite());
          if (userEtablissementRoleList.get(i).getDeleteRole())
            roleList.add(userEtablissementRoleList.get(i).getRoleDescribe().getRoleDelete());
        }
        //        if (userEtablissement.getReadSvgExam()) roleList.add(getRole("role.exam.read"));
      }
      user.setEtablissementSelectionnee(etablissement);
      user.setRoles(roleList);
      Beans.get(UserRepository.class).save(user);
    }
  }

  private static Role getRole(String r) {
    RoleRepository roleRepository = Beans.get(RoleRepository.class);
    return roleRepository.findByName(r);
  }

  private static Group getGroupe(String codeDeGroupe) {
    GroupRepository groupRepository = Beans.get(GroupRepository.class);
    return groupRepository.findByCode(codeDeGroupe);
  }

  public static void onChangePermission(UserEtablissement userEtablissement, User user) {
    if (userEtablissement.getUsers().getEtablissementSelectionnee() != null
        && userEtablissement.getUsers().getEtablissementSelectionnee()
            == user.getEtablissementSelectionnee()) {
      userEtablissement.getUsers().setEtablissementSelectionnee(null);
      userEtablissement.getUsers().setGroup(getGroupe("userEtablissementZero"));
      userEtablissement.getUsers().setRoles(new HashSet<>());
      Beans.get(UserRepository.class).save(userEtablissement.getUsers());
    }
  }

  public static void onDeleteUserEtab(UserEtablissement userEtablissement) {
    if (userEtablissement.getUsers().getGroup().getCode().equals("admins")) {
      userEtablissement.getUsers().getEtablissements().remove(userEtablissement);
      return;
    }
    if (userEtablissement.getUsers().getEtablissementSelectionnee() != null
        && userEtablissement.getUsers().getEtablissementSelectionnee()
            == userEtablissement.getEtablissements()) {
      userEtablissement.getUsers().setEtablissementSelectionnee(null);
      userEtablissement.getUsers().setGroup(getGroupe("userEtablissementZero"));
      userEtablissement.getUsers().setRoles(new HashSet<>());
    }
    userEtablissement.getUsers().getEtablissements().remove(userEtablissement);
  }

  public static void setPermissionUserEtablissement(List<UserEtablissement> users) {
    for (int i = 0; i < users.size(); i++)
      UserEtablissementService.SetPermissionUser(users.get(i).getUsers());
  }
}
