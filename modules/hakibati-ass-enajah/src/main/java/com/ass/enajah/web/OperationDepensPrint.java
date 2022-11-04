package com.ass.enajah.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.hakibati.ass.enajah.db.OperationDepense;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;

public class OperationDepensPrint {
  //  pv_besoines

  public void PrintPvBesoines(ActionRequest request, ActionResponse response) {
    String name = "pv_besoines";
    User user = AuthUtils.getUser();
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    OperationDepense operationDepense = request.getContext().asType(OperationDepense.class);
    Long IdOperation = operationDepense.getId();
    String UrlLogo = "";
    try {
      UrlLogo =
          this.getClass()
              .getClassLoader()
              .getResource("../../img/Logo_enajah.png")
              .toString()
              .replace("%20", " ");
    } catch (Exception e) {
    }
    //  response.setAlert(UrlLogo);

    try {
      String fileLink =
          ReportFactory.createReport(IReport.pv_besoines_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdOperation", IdOperation)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error ") + e.getMessage());
    }
  }

  public void PrintDemmandeDevis(ActionRequest request, ActionResponse response) {

    String name = "Demmande_devis";
    User user = AuthUtils.getUser();

    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    OperationDepense operationDepense = request.getContext().asType(OperationDepense.class);
    Long IdOperation = operationDepense.getId();

    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/Logo_enajah.png")
            .toString()
            .replace("%20", " ");
    try {
      String fileLink =
          ReportFactory.createReport(IReport.Demmande_devis_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdOperation", IdOperation)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintBonCommande(ActionRequest request, ActionResponse response) {

    String name = "BonCommande";
    User user = AuthUtils.getUser();

    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    OperationDepense operationDepense = request.getContext().asType(OperationDepense.class);
    Long IdOperation = operationDepense.getId();

    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/Logo_enajah.png")
            .toString()
            .replace("%20", " ");
    try {
      String fileLink =
          ReportFactory.createReport(IReport.BonCommande_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdOperation", IdOperation)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintBonLivraison(ActionRequest request, ActionResponse response) {

    String name = "Demmande_devis";
    User user = AuthUtils.getUser();

    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    OperationDepense operationDepense = request.getContext().asType(OperationDepense.class);
    Long IdOperation = operationDepense.getId();

    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/Logo_enajah.png")
            .toString()
            .replace("%20", " ");
    try {
      String fileLink =
          ReportFactory.createReport(IReport.BonLivraison_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdOperation", IdOperation)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name)
              .add("html", fileLink)
              .param("download", format_print.equals(ReportSettings.FORMAT_PDF) ? "false" : "true")
              .map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }
}
