package com.setting.service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class MassarMenGovService {

  public static String InfoLoginMassar(String loginMassar, String passwordMassar)
      throws IOException {
    String cookie = GetCookieMassar(loginMassar, passwordMassar);
    if (cookie == null) {
      return null;
    }
    return GetInfoLoginMassar(cookie);
  }

  public static String InfoLoginMassar2(String loginMassar, String passwordMassar)
      throws IOException {

    return "";
  }

  public static String DataMassar(String loginMassar, String passwordMassar) throws IOException {

    String cookie = GetCookieMassar(loginMassar, passwordMassar);
    if (cookie == null) {
      return null;
    }

    String requestBodyallCycles =
        "{\n  allCycles {\n    cD_CYCLE\n    lA_CYCLE\n    lL_CYCLE\n    __typename\n  }\n}\n";
    String dateallCycles = getDataMassar(cookie, requestBodyallCycles, "evaluationgw");

    String requestBodyallTypeEnseignement =
        "{\n  allTypeEnseignement {\n    id_typeEnseignement\n    typeEnseignementAr\n    typeEnseignementFr\n    __typename\n  }\n}\n";
    String datallTypeEnseignement =
        getDataMassar(cookie, requestBodyallTypeEnseignement, "evaluationgw");
    // info user criteire
    String requestBodyuserCriteria =
        "{\n  userCriteria {\n    isAdmin\n    orgCriteria {\n      key\n      value\n      __typename\n    }\n    __typename\n  }\n}\n";
    String datalluserCriteria = getDataMassar(cookie, requestBodyuserCriteria, "evaluationgw");

    String requestBodyallSession =
        "{\n  allSession {\n    id_session\n    libelleSessionAr\n    libelleSessionFr\n    __typename\n  }\n}\n";
    String dataallSession = getDataMassar(cookie, requestBodyallSession, "evaluationgw");

    String requestBodyEtabBycD_etabr =
        "{\"operationName\":null,\"variables\":{},\"query\":\"fragment EtabFragment on ZEtab {\\n  cD_ETAB\\n  nOM_ETABA\\n  nOM_ETABL\\n  cD_COM\\n  ensPrimG\\n  ensPrimO\\n  ensColG\\n  ensColO\\n  ensQualG\\n  ensQualO\\n  ensQualT\\n  ensPrescoM\\n  ensPrescoC\\n  ensBts\\n  ensCPGE\\n  cD_etabr\\n  __typename\\n}\\n\\n{\\n  allEtabs(where: {cD_etabr: \\\"11565V\\\"}) {\\n    ...EtabFragment\\n    __typename\\n  }\\n}\\n\"}";
    String dataEtabBycD_etabr = getDataMassar(cookie, requestBodyEtabBycD_etabr, "evaluationgw");

    String requestBodyEtabBycD_ETAB =
        "{\"operationName\":null,\"variables\":{},\"query\":\"fragment EtabFragment on ZEtab {\\n  cD_ETAB\\n  nOM_ETABA\\n  nOM_ETABL\\n  cD_COM\\n  ensPrimG\\n  ensPrimO\\n  ensColG\\n  ensColO\\n  ensQualG\\n  ensQualO\\n  ensQualT\\n  ensPrescoM\\n  ensPrescoC\\n  ensBts\\n  ensCPGE\\n  cD_etabr\\n  __typename\\n}\\n\\n{\\n  allEtabs(where: {cD_ETAB: \\\"11565V\\\"}) {\\n    ...EtabFragment\\n    __typename\\n  }\\n}\\n\"}";
    String dataEtabBycD_ETAB = getDataMassar(cookie, requestBodyEtabBycD_ETAB, "evaluationgw");

    String requestBodyListEleve = "";
    String dateListEleve = getDataMassar(cookie, requestBodyListEleve, "evaluationgw");

    return getDataMassar(cookie, requestBodyListEleve, "evaluationgw");
  }

  public static String GetCookieMassar(String login, String password) throws IOException {
    try {
      //    private static String getCookieMassar(String login, String password) throws IOException
      // {
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

      String strCokie = con.getHeaderField("Set-Cookie");
      if (con.getResponseCode() == 302) return strCokie;
      else return null;
    } catch (Exception e) {
      return null;
    }
  }

  public static String GetInfoLoginMassar(String strCookie) throws IOException {
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
      //            return "[" + response.toString() + "]";
      return response.toString();
    } else {
      return null;
    }
  }

  public static String getDataMassar(String strCookie, String requestBody, String gw)
      throws IOException {
    URL obj = new URL("https://prod.men.gov.ma/" + gw + "/");
    //      URL obj = new URL("https://prod.men.gov.ma/evaluationgw/");
    //        URL obj = new URL("https://prod.men.gov.ma/mobilegw/");
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setRequestMethod("POST");
    //         requestBody = "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n
    // exportNotesCanvasData(criteria: {cdMatieres: [\\\"0011\\\"], classe:
    // \\\"b9ef4096-768a-43a4-9495-6d21316878f0\\\", idControlContinus: [2], idSession: 1, nefstat:
    // \\\"2A31101110\\\", cdEtab: \\\"11565V\\\", onlyInscrit: true}) {\\n    id_eleve\\n
    // codeEleve\\n    nomEleveAr\\n    nomEleveFr\\n    prenomEleveAr\\n    prenomEleveFr\\n
    // dateNaisEleve\\n    hash {\\n      key\\n      value\\n      __typename\\n    }\\n
    // __typename\\n  }\\n}\\n\"}";

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
    con.setRequestProperty("Cookie", strCookie);
    con.setDoInput(true);
    con.setDoOutput(true);
    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
      wr.write(postData);
    }
    int responseCode = con.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) { // success
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      //            System.out.println(response.toString());
      //            return "[" + response.toString() + "]";
      return response.toString();
    } else {
      //            System.out.println("GET request not worked");
      return null;
    }
  }
}
