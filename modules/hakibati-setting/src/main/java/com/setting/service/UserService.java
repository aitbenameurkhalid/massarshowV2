package com.setting.service;

import com.axelor.auth.AuthService;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.Group;
import com.axelor.auth.db.Role;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.GroupRepository;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.repo.ActivationUserRepository;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Singleton
public class UserService {
  //  @Inject UserRepository userRepository;
  public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  public static Long verifierusername(String username) {
    Long count = Beans.get(UserRepository.class).all().filter("code = ?", username).count();
    return count;
  }

  public static boolean validateEmail(String email) {
    try {
      if (!email.toLowerCase().equals(email)) return false;
      Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
      return matcher.matches();
    } catch (Exception e) {
      return false;
    }
  }

  public static int register(Map<String, Object> userData) {
    try {
      String username = userData.get("email").toString();
      String password = userData.get("MotdePasse").toString();
      String passwordconf = userData.get("confirmerMotdePasse").toString();
      String email = userData.get("email").toString();
      String codeActivation = userData.get("codeActivation").toString();
      String codeActivationHashMD5 = MD5.encrypt(codeActivation);
      String oldCodeActivation = ActivationUserService.getCodeOfEmail(email);
      if (!passwordconf.equals(password)) {
        return 0;
      }

      if (!oldCodeActivation.equals(codeActivationHashMD5)) {
        return 0;
      }

      // si user n'existe pas
      if (verifierusername(username) == 0) {
        User user = new User();
        user.setCode(username);
        user.setName(username);
        user.setPassword(AuthService.getInstance().encrypt(password));
        user.setEmail(email);
        user.setGroup(getMemberGroup("userEtablissementZero"));
        UserRepository userRepository = Beans.get(UserRepository.class);
        userRepository.save(user);

      } else {
        Beans.get(UserRepository.class)
            .all()
            .filter("code = ?", username)
            .fetchOne()
            .setPassword(AuthService.getInstance().encrypt(password));
      }
      return 1;
    } catch (Exception e) {
      return 0;
    }
  }

  private static Group getMemberGroup(String s) {
    GroupRepository groupRepository = Beans.get(GroupRepository.class);
    return groupRepository.findByCode(s);
  }

  public static void deletCodeActivation(Map<String, Object> userData) {
    try {
      String email = userData.get("email").toString();
      Beans.get(ActivationUserRepository.class)
          .all()
          .filter("email = ?", email)
          .fetchOne()
          .setCodeActivationHash("activationSucces");
    } catch (Exception e) {

    }
  }

  @CallMethod
  public static boolean HasPermission(String permission) {
    User user = AuthUtils.getUser();

    if (user.getGroup().getCode().equals("admins")) return true;
    if (user.getPermissions().stream()
        .anyMatch(e -> e.getName() != null && e.getName().equals(permission))) {
      return true;
    }
    if (user.getGroup().getPermissions().stream()
        .anyMatch(e -> e.getName() != null && e.getName().equals(permission))) {
      return true;
    }
    for (Role role : user.getRoles()) {
      if (role.getPermissions().stream()
          .anyMatch(e -> e.getName() != null && e.getName().equals(permission))) {
        return true;
      }
    }
    return false;
  }

  public boolean HasPermission() {
    return false;
  }
}
