package com.centre.langue.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.hakibati.centre.langue.db.SuiviFinancierCentre;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SuiviFinancierCentreController {

  public void PrintRecu(ActionRequest request, ActionResponse response)
      throws IOException, ParseException {
    SuiviFinancierCentre suiviFinancierCentre =
        request.getContext().asType(SuiviFinancierCentre.class);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate DatePaimentLocalDate = suiviFinancierCentre.getDatePaiement();
    Long IdEEleveCentre = suiviFinancierCentre.getEleveCentres().getId();
    Date DatePaiment =
        new SimpleDateFormat("yyyy-MM-dd").parse(DatePaimentLocalDate.format(formatter));
    User user = AuthUtils.getUser();
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

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
    String name = "print_reçu_centre_langue";
    try {
      String fileLink =
          ReportFactory.createReport(IReport.print_recu_centre_langue, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("DatePaiment", DatePaiment)
              .addParam("IdEEleveCentre", IdEEleveCentre)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error ") + e.getMessage());
    }
  }

  public void onNewsuiveFinancier(ActionRequest request, ActionResponse response)
      throws IOException {
    LocalDate localDate = LocalDate.now();
    response.setValue("datePaiement", localDate);
    response.setValue("anneePaiement", localDate.getYear());
    response.setValue("moisPaye", localDate.getMonthValue());
    response.setHidden("btn-print-recu", true);
  }
}
