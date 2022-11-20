<%--

    Axelor Business Solutions

    Copyright (C) 2005-2022 Axelor (<http://axelor.com>).

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="x" uri="WEB-INF/axelor.tld" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page language="java" session="true" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry"%>
<%@ page import="java.util.Set" %>
<%@ page import="org.pac4j.http.client.indirect.FormClient" %>
<%@ page import="com.axelor.inject.Beans" %>
<%@ page import="com.axelor.auth.pac4j.AuthPac4jInfo" %>
<%@ page import="com.axelor.common.HtmlUtils" %>
<%@include file='common.jsp'%>
<%

String errorMsg = T.apply(request.getParameter(FormClient.ERROR_PARAMETER));

String loginRemember = T.apply("Remember me");
String loginSubmit = T.apply("Log in");

String loginUserName = T.apply("Username");
String loginPassword = T.apply("Password");

String warningBrowser = T.apply("Update your browser!");
String warningAdblock = T.apply("Adblocker detected!");
String warningAdblock2 = T.apply("Please disable the adblocker as it may slow down the application.");

String loginWith = T.apply("Log in with %s");

String loginHeader = "/login-header.jsp";
if (pageContext.getServletContext().getResource(loginHeader) == null) {
  loginHeader = null;
}

@SuppressWarnings("all")
Map<String, String> tenants = (Map) session.getAttribute("tenantMap");
String tenantId = (String) session.getAttribute("tenantId");

AuthPac4jInfo authPac4jInfo = Beans.get(AuthPac4jInfo.class);
String callbackUrl = authPac4jInfo.getCallbackUrl();
Set<String> centralClients = authPac4jInfo.getCentralClients();
%>
<!DOCTYPE html>
<html lang="<%= pageLang %>">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="google" content="notranslate">
    <link rel="shortcut icon" href="ico/favicon.ico">
<%--    <x:style src="css/application.login.css" />--%>
<%--    <x:script src="js/application.login.js" />--%>
<%--    <script src="lib/bootstrap5/bootstrap.js"></script>--%>
    <link rel="stylesheet" href="lib/bootstrap5/bootstrap.css">
    <style>
      label{
        color: #616161;
      }
      .hidden{
        display: none;
      }
    </style>
  </head>
  <body>

        <section class="vh-100" style="background-color: #48b9b9;">
          <div class="container py-4 h-100">
            <div class="row d-flex justify-content-center align-items-center h-100">
              <div class="col col-xl-10">
                <div class="card" style="border-radius: 1rem;">
                  <div class="row g-0">
                    <div class="col-md-6 col-lg-5 d-none d-md-block">
                      <img src="./img/main.jpg" alt="login form" class="img-fluid"
                           style="border-radius: 1rem 0 0 1rem;" />
                    </div>
                    <div class="col-md-6 col-lg-7 d-flex align-items-center">
                      <div class="card-body p-4 p-lg-5 text-black">

                        <form action="<%=callbackUrl%>" method="POST">

                          <div class="mx-auto">
                            <div class="h1 fw-bold mb-0 text-center"><img src="<%= appLogo %>" style=" max-width:100%;"/></div>
                          </div>

                          <div id="error-msg" class="alert alert-danger alert-block alert-error text-center mt-1 <%= errorMsg == null ? "hidden" : "" %>">
                            <h4><%= HtmlUtils.escape(errorMsg) %></h4>
                          </div>
                          <div class="form-outline mb-3">
                            <label class="form-label" for="form2Example17">Adresse e-mail</label>
                            <input type="text" id="form2Example17"
                                   class="form-control form-control-lg" name="username" placeholder="<%= loginUserName %>" autofocus="autofocus"/>
                          </div>

                          <div class="form-outline mb-3">
                            <label class="form-label" for="form2Example27">Mot de passe</label>
                            <input type="password" id="form2Example27"
                                   class="form-control form-control-lg" name="password" placeholder="<%= loginPassword %>"/>
                          </div>
                          <div class="custom-control custom-checkbox">
                            <input type="checkbox" class="custom-control-input" id="rememberMe" value="rememberMe" name="rememberMe">
                            <label class="custom-control-label mb-3" for="rememberMe"><%= loginRemember %>
<%--                              | <span class="title"><%= loginRemember %></span>--%>
                            </label>
                          </div>
                          <input type="hidden" name="hash_location" id="hash-location">

                          <div class="pt-1 mb-3">
                            <button class="btn btn-dark btn-lg btn-block" type="submit"><%= loginSubmit %></button>
                          </div>
                        </form>

                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <footer class="container-fluid">
                <p class="text-sm-center"><%= appCopyright %></p>
              </footer>
            </div>
          </div>
        </section>
  </body>
</html>
