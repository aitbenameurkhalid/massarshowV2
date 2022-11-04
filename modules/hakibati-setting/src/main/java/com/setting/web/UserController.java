package com.setting.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.auth.pac4j.AuthPac4jModule;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.setting.service.ActivationUserService;
import com.setting.service.SendEmailService;
import com.setting.service.UserEtablissementService;
import com.setting.service.UserService;
import java.net.InetAddress;
import java.util.Map;
import org.apache.shiro.session.Session;

public class UserController {
  @Transactional
  @Singleton
  public void register(ActionRequest request, ActionResponse response) {
    Map<String, Object> userData = request.getContext();
    Integer NUMBRE_TEST = 10;
//    String callbackUrl = AuthPac4jModule.getCallbackUrl();
    String callbackUrl = "AuthPac4jModule.getCallbackUrl()";
    String username = userData.get("email").toString();
    String password = userData.get("MotdePasse").toString();

    int res = 0;
    try {
      res = UserService.register(userData);
      if (res == 1) {
        UserService.deletCodeActivation(userData);
        String LINK = callbackUrl + "?username=" + username + "&password=" + password;
        response.setHidden("panalEnregistre", true);
        response.setHidden("dangerFr", true);
        response.setHidden("dangerAr", true);
        response.setHidden("successFr", false);
        response.setHidden("successAr", false);
        response.setHidden("sendredirectConnecte", false);
        response.setValue("sendredirect", LINK);

      } else {
        response.setHidden("panalEnregistre", false);
        response.setHidden("dangerFr", false);
        response.setHidden("dangerAr", false);
        response.setHidden("successFr", true);
        response.setHidden("successAr", true);
      }

    } catch (Exception e) {
      response.setHidden("panalEnregistre", false);
      response.setHidden("dangerFr", false);
      response.setHidden("dangerAr", false);
      response.setHidden("successFr", true);
      response.setHidden("successAr", true);
    }

    //    String callbackUrl = AuthPac4jModule.getCallbackUrl();
    //    String name="khalid";
    //    AuthUtils.getSubject().logout();
    //   // AuthUtils.getSubject().getSession().setTimeout(0);
    //     String fileLink=callbackUrl+ "?username=khalidtinghir@gmail.com&password=123456";
    //    response.setView(ActionView.define(name).add("html", fileLink)..map());
    //    response.setReload(true);
    //   // response.setSignal("refresh-app", true);
  }

  @Transactional
  @Singleton
  public void SendEmail(ActionRequest request, ActionResponse response) {
    String email = (String) request.getContext().get("email");
    if (!UserService.validateEmail(email)) {
      response.setHidden("ErrorEmailFr", false);
      response.setHidden("ErrorEmailAr", false);
      return;
    }
    try {
      String codeActivation = ActivationUserService.getcodeActivation();
      Session session = AuthUtils.getSubject().getSession();
      String host = "http://localhost:8080/HAKIBATI";
      host = InetAddress.getLocalHost().getHostName() + "";
      //      host=session.getHost();
      String link = "<a href=\'http://localhost:8080/HAKIBATI\'>ACTIVAR CUENTA</a>";

      String emailSubjet = "AC" + "TIVATION COMPTE";
      String emailText =
          "<h4> Merci. pour  terminer l'inscription Cliquez  "
              + "<a href=\'"
              + host
              + "/change-password.jsp?_activation_de_user_=dfdkfdfhgfyhgf4___51544554fgghjj4e78h4j5k4h7d4h57i__&_email_user_Register_="
              + email
              + "&_code_user_Register_="
              + codeActivation
              + "\'>ICI</a>"
              + "<br><br><br>"
              + " Ou copiez ce code d'activation:</h4><h3>"
              + codeActivation
              + "</h3>";

      SendEmailService.sendEmail(email, emailSubjet, emailText);
      ActivationUserService.saveCodeEmailActivation(email, codeActivation);
      response.setReadonly("email", true);
      response.setHidden("LabelEmailFr", false);
      response.setHidden("LabelEmailFr", false);
      response.setHidden("ErrorEmailFr", true);
      response.setHidden("ErrorEmailAr", true);
      response.setHidden("messageFr", true);
      response.setHidden("messageAr", true);
      response.setHidden("panalEnregistre", false);
      response.setHidden("btn-send-code", true);
    } catch (Exception e) {
      response.setHidden("ErrorEmailFr", false);
      response.setHidden("ErrorEmailAr", false);
    }
  }

  public void verificationEmail(ActionRequest request, ActionResponse response) {
    String email = (String) request.getContext().get("email");
    if (UserService.validateEmail(email)) {
      response.setValue("emailLabelFalse", "Valide");
      response.setValue("emailLabel", null);
    } else {
      response.setValue("emailLabelFalse", null);
      response.setValue("emailLabel", "n 'st pas valide");
    }
  }

  public void verificationPassword(ActionRequest request, ActionResponse response) {
    String password = (String) request.getContext().get("MotdePasse");
    if (password.length() >= 6) {
      response.setValue("passwordLabelFalse", "success");
      response.setValue("passwordLabel", null);
    } else {
      response.setValue("passwordLabelFalse", null);
      response.setValue("passwordLabel", "n'est pas valide");
    }
  }

  public void verificationConfPassword(ActionRequest request, ActionResponse response) {
    String password = (String) request.getContext().get("MotdePasse");
    String passwordsecond = (String) request.getContext().get("confirmerMotdePasse");
    if (password.length() >= 6 && passwordsecond.equals(password)) {
      response.setValue("passwordconfLabelFalse", "success");
      response.setValue("passwordconfLabel", null);
    } else {
      response.setValue("passwordconfLabelFalse", null);
      response.setValue("passwordconfLabel", "password n'est ");
    }
  }

  public void quitteRegister(ActionRequest request, ActionResponse response) {
    AuthUtils.getSubject().logout();
    AuthUtils.getSubject().getSession().setTimeout(0);
    response.setSignal("refresh-app", true);
  }

  public void onloadRegister(ActionRequest request, ActionResponse response) {
    Session session = AuthUtils.getSubject().getSession();
    String emailRegister = (String) session.getAttribute("_email_user_Register_");
    String codeRegister = (String) session.getAttribute("_code_user_Register_");
    if (emailRegister == null) return;
    if (codeRegister == null) return;
    String test = (String) session.getAttribute("test");
    response.setValue("email", emailRegister);
    response.setValue("codeActivation", codeRegister);

    response.setReadonly("email", true);
    response.setReadonly("codeActivation", true);
    response.setHidden("LabelEmailFr", false);
    response.setHidden("LabelEmailFr", false);
    response.setHidden("ErrorEmailFr", true);
    response.setHidden("ErrorEmailAr", true);
    response.setHidden("messageFr", true);
    response.setHidden("messageAr", true);
    response.setHidden("panalEnregistre", false);
    response.setHidden("btn-send-code", true);
    response.setHidden("LabelEmailFr", true);
    response.setHidden("LabelEmailAr", true);
  }

  @Transactional
  public void OnSavePreferenceUser(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    UserEtablissementService.SetPermissionUser(user);
    response.setReload(true);
  }
}
