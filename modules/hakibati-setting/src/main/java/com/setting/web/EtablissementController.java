package com.setting.web;

import com.app.application.db.Etablissement;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.setting.service.EtablissementService;
import com.setting.service.MassarMenGovService;
import com.setting.service.UserEtablissementService;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import wslite.json.JSONArray;
//import wslite.json.JSONException;
//import wslite.json.JSONObject;

@Singleton
public class EtablissementController {
  @Transactional
  public void OnCreateEtablissement(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    String NomDeEtablissement = (String) request.getContext().get("NomDeEtablissement");
    String NomDecommun = (String) request.getContext().get("NomDecommun");
    String NomDedirection = (String) request.getContext().get("NomDedirection");
    String NomDeacademie = (String) request.getContext().get("NomDeacademie");
    String Adresse = (String) request.getContext().get("Adresse");
    String Tel = (String) request.getContext().get("Tel");

    String TypeEtablissement = (String) request.getContext().get("typeEtab");
    if (TypeEtablissement == null) TypeEtablissement = "0";
    EtablissementService.OnCreateEtablissement(
        NomDeEtablissement,
        NomDecommun,
        NomDedirection,
        NomDeacademie,
        Adresse,
        Tel,
        user,
        TypeEtablissement);
    response.setSignal("refresh-app", true);
    response.setCanClose(true);
  }

  public void OpenUtilisateur(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    response.setView(
        ActionView.define(I18n.get("les utilisateurs"))
            .model("com.app.application.db.Etablissement")
            .add("form", "utilisateurs-form")
            .domain("self.id = " + user.getEtablissementSelectionnee().getId())
            .param("forceEdit", "true")
            .context("_showRecord", user.getEtablissementSelectionnee().getId())
            .context("_userId", user.getId())
            .map());
  }

  public void OpenInfoEtab(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    response.setView(
        ActionView.define(I18n.get("les utilisateurs"))
            .model("com.app.application.db.Etablissement")
            .add("form", "InfoEtablissemnt-form")
            .domain("self.id = " + user.getEtablissementSelectionnee().getId())
            .param("forceEdit", "true")
            .context("_showRecord", user.getEtablissementSelectionnee().getId())
            .map());
  }

  public void TestConnectionMassar(ActionRequest request, ActionResponse response)
      throws  IOException {
//    String loginMassar = (String) request.getContext().get("loginMassar");
//    String passwordMassar = (String) request.getContext().get("passwordMassar");
//
//    String cookie = getCookieMassar(loginMassar, passwordMassar);
//    if (cookie == null) {
//      response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
//    } else {
//      String responseIngoLoginMassar0 = getInfoLoginMassar(cookie);
//      byte[] byteResponseInfoLogin = responseIngoLoginMassar0.getBytes(StandardCharsets.UTF_8);
//      String responseIngoLoginMassar = new String(byteResponseInfoLogin, "UTF-8");
//      //        response.setAlert(responseIngoLoginMassar);
//      JSONArray array = new JSONArray(responseIngoLoginMassar);
//      JSONObject object = array.getJSONObject(0);
//      response.setValue("Login", object.getString("Login"));
//      response.setValue("UserType", object.getString("UserType"));
//      response.setValue("Application", object.getString("Application"));
//      response.setValue("NomArabe", object.getString("NomArabe"));
//      response.setValue("NomLatine", object.getString("NomLatine"));
//      response.setValue("CodeProfile", object.getString("CodeProfile"));
//      //            response.setValue("CodeEtablissement", object.getString("CodeEtablissement"));
//      String data = getMassarListEleve(cookie);
//      byte[] byteData = data.getBytes(StandardCharsets.UTF_8);
//      data = new String(byteData, "UTF-8");

//      response.setValue("CodeEtablissement", data);
//    }
  }

  public void ImportationDataMassar(ActionRequest request, ActionResponse response)
      throws  IOException {
//    String loginMassar = (String) request.getContext().get("loginMassar");
//    String passwordMassar = (String) request.getContext().get("passwordMassar");
//
//    String infoLoginMassar = MassarMenGovService.InfoLoginMassar(loginMassar, passwordMassar);
//    if (infoLoginMassar == null) {
//      response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
//    } else {
//      //            String responseIngoLoginMassar0 = getInfoLoginMassar(cookie);
//      //            byte[] byteResponseInfoLogin =
//      // responseIngoLoginMassar0.getBytes(StandardCharsets.UTF_8);
//      //            String responseIngoLoginMassar = new String(byteResponseInfoLogin, "UTF-8");
//      JSONArray array = new JSONArray(infoLoginMassar);
//      JSONObject object = array.getJSONObject(0);
//      response.setValue("Login", object.getString("Login"));
//      response.setValue("UserType", object.getString("UserType"));
//      response.setValue("Application", object.getString("Application"));
//      response.setValue("NomArabe", object.getString("NomArabe"));
//      response.setValue("NomLatine", object.getString("NomLatine"));
//      response.setValue("CodeProfile", object.getString("CodeProfile"));
//      response.setValue("CodeEtablissement", object.getString("CodeEtablissement"));
//    }
  }

  @Transactional
  public void OnAppRefresh(ActionRequest request, ActionResponse response) {
    response.setSignal("refresh-app", true);
  }

  @Transactional
  public void OnSaveEtablissement(ActionRequest request, ActionResponse response) {
    Etablissement etablissement = request.getContext().asType(Etablissement.class);
    UserEtablissementService.setPermissionUserEtablissement(etablissement.getUsers());
    //  response.setAlert("savvvvvvvvve etab");
  }

  @Inject private MetaFiles metaFiles;

  public void ExportSuiviEtablissemnts(ActionRequest request, ActionResponse response)
      throws IOException {
    //      response.setAlert("export Statistiques etab ");

    EtablissementService etablissementService = new EtablissementService();
    File file = etablissementService.getFileListEtabExcel();

    MetaFile metaFile = metaFiles.upload(file);
    response.setView(
        ActionView.define("Data")
            .add(
                "html",
                "ws/rest/com.axelor.meta.db.MetaFile/"
                    + metaFile.getId()
                    + "/content/download?v="
                    + metaFile.getVersion())
            .param("download", "true")
            .map());

    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
  }

  private String getCookieMassar(String login, String password) throws IOException {
    //        String password = "k1280909@";
    //        String login = "ait.ben.ameur.khalid@taalim.ma";
    URL obj = new URL("https://prod.men.gov.ma/Account/Login");
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("POST");
    String requestBody =
        "ApplicationCode=SGS&Login="
            + login
            + "&Password="
            + password
            + "&RedirectUrl=https://evaluationmassar.men.gov.ma&RememberMe=false&__RequestVerificationToken=CfDJ8DlIt-o3AgVPpnyRnRzjELi1R2L60VQUXnBULpkjEa32umzy4WN6CnZmhm7XhlcgtjxuDuEoy_WjjUWpT2LN6PAuMA6Cq64jpXXa1I75uDxLy75iTyznHL-MbXG1l7abK9wM76QhogKWxz0b4gdMG9s";

    byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
    int postdaralength = postData.length;
    con.setDoOutput(true);
    con.setInstanceFollowRedirects(false);
    con.setRequestProperty("Content-Length", Integer.toString(postdaralength));
    con.setRequestProperty(
        "sec-ch-ua",
        "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
    con.setRequestProperty("sec-ch-ua-mobile", "?0");
    con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
    con.setRequestProperty("Upgrade-Insecure-Requests", "1");
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    con.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
    con.setRequestProperty(
        "Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
    con.setRequestProperty("Sec-Fetch-Site", "same-origin");
    con.setRequestProperty("Sec-Fetch-Mode", "navigate");
    con.setRequestProperty("Sec-Fetch-User", "?1");
    con.setRequestProperty("Sec-Fetch-Dest", "document");
    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
      wr.write(postData);
    }
    //        int responseCode = con.getResponseCode();
    //        System.out.println("GET Response Code :: " + responseCode);
    //        System.out.println("All-Header :: " + con.getHeaderFields().toString());
    //        System.out.println("Set-Cookie :: " + con.getHeaderField("Set-Cookie"));

    String strCokie = con.getHeaderField("Set-Cookie");

    if (con.getResponseCode() == 302) return strCokie;
    else return null;
  }

  private String getInfoLoginMassar(String strCookie) throws IOException {
    URL obj = new URL("https://prod.men.gov.ma/account/GetConnectedUser");
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty(
        "sec-ch-ua",
        "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
    con.setRequestProperty("Accept", "application/json, text/plain, */*");
    con.setRequestProperty("sec-ch-ua-mobile", "?0");
    con.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
    con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
    con.setRequestProperty("Sec-Fetch-Site", "same-site");
    con.setRequestProperty("Sec-Fetch-Mode", "cors");
    con.setRequestProperty("Sec-Fetch-Dest", "empty");
    con.setRequestProperty("Accept-Charset", "UTF-8");
    con.setRequestProperty("Cookie", strCookie);
    int responseCode = con.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) { // success
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      return "[" + response.toString() + "]";
    } else {
      return null;
    }
  }

  private String getMassarListEleve(String strCookie) throws IOException {
    URL obj = new URL("https://prod.men.gov.ma/evaluationgw/");
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("POST");
    String requestBody =
        "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  exportNotesCanvasData(criteria: {cdMatieres: [\\\"0011\\\"], classe: \\\"b9ef4096-768a-43a4-9495-6d21316878f0\\\", idControlContinus: [2], idSession: 1, nefstat: \\\"2A31101110\\\", cdEtab: \\\"11565V\\\", onlyInscrit: true}) {\\n    id_eleve\\n    codeEleve\\n    nomEleveAr\\n    nomEleveFr\\n    prenomEleveAr\\n    prenomEleveFr\\n    dateNaisEleve\\n    hash {\\n      key\\n      value\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
    byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
    int postdaralength = postData.length;
    con.setDoOutput(true);
    con.setInstanceFollowRedirects(false);
    con.setRequestProperty("Content-Length", Integer.toString(postdaralength));
    con.setRequestProperty(
        "sec-ch-ua",
        "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"96\", \"Google Chrome\";v=\"96\"");
    con.setRequestProperty("Accept", "application/json, text/plain, */*");
    con.setRequestProperty("Content-Type", "application/json");
    con.setRequestProperty("sec-ch-ua-mobile", "?0");
    con.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
    con.setRequestProperty("sec-ch-ua-platform", "\"Windows\"");
    con.setRequestProperty("Sec-Fetch-Site", "same-site");
    con.setRequestProperty("Sec-Fetch-Mode", "cors");
    con.setRequestProperty("Sec-Fetch-Dest", "empty");
    con.setRequestProperty("Accept-Charset", "UTF-8");
    con.setRequestProperty("Cookie", strCookie);
    con.setDoInput(true);
    con.setDoOutput(true);
    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
      wr.write(postData);
    }
    int responseCode = con.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) { // success
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      //            System.out.println(response.toString());
      return "[" + response.toString() + "]";
    } else {
      //            System.out.println("GET request not worked");
      return null;
    }
  }
}
