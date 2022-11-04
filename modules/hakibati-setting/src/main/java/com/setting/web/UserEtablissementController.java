package com.setting.web;

import com.app.application.db.UserEtablissement;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.setting.service.UserEtablissementService;
import java.io.IOException;

@Singleton
@Transactional
public class UserEtablissementController {
  public void CreeUserQuit(ActionRequest request, ActionResponse response) throws IOException {
    response.setCanClose(true);
    response.setReload(true);
  }

  public void addUserEtab(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    String nomDeUser = (String) request.getContext().get("nomDeUser");

    response.setValue("users", getUser(nomDeUser));
    if (getUser(nomDeUser) == null)
      response.setAlert(I18n.get("Il n'y a aucun utilisateur avec ce nom"));
  }

  private User getUser(String nomDeUser) {
    UserRepository userRepository = Beans.get(UserRepository.class);
    return userRepository.findByCode(nomDeUser);
  }

  public void OnchangePreferenceEtab(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    UserEtablissementService.SetPermissionUser(user);
    response.setReload(true);
  }

  public void onChangePermission(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    UserEtablissement userEtablissement = request.getContext().asType(UserEtablissement.class);
    UserEtablissementService.onChangePermission(userEtablissement, user);
  }

  public void onDeleteUserEtab(ActionRequest request, ActionResponse response) {
    UserEtablissement userEtablissement = request.getContext().asType(UserEtablissement.class);
    User user = AuthUtils.getUser();
    if (userEtablissement.getUsers().getId().equals(user.getId())) return;
    UserEtablissementService.onDeleteUserEtab(userEtablissement);
    response.setReload(true);
  }

  public void IsCurrentUser(ActionRequest request, ActionResponse response) {
    UserEtablissement userEtablissement = request.getContext().asType(UserEtablissement.class);
    User currentuser = AuthUtils.getUser();
    //    response.setHidden(
    //        "panalUserEtabs", userEtablissement.getUsers().getId().equals(currentuser.getId()));
    response.setReadonly(
        "panalUserEtabs", userEtablissement.getUsers().getId().equals(currentuser.getId()));
  }

  public void OnSaveRoleUser(ActionRequest request, ActionResponse response) {
    response.setAlert("on save");
  }
}
