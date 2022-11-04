package com.centre.langue.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.hakibati.centre.langue.db.GroupeCentre;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;

public class GroupeCentreController {
  public void PrintListEtudiant(ActionRequest request, ActionResponse response) {

    GroupeCentre groupeCentre = request.getContext().asType(GroupeCentre.class);
    //        Integer IdGroupeMax =(Integer) request.getContext().get("id");
    //        Integer IdGroupeMin =(Integer) request.getContext().get("id");
    Integer IdGroupeMax = (int) (long) groupeCentre.getId();
    Integer IdGroupeMin = (int) (long) groupeCentre.getId();

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
    String name = "print_list_etudiant_centre_langue";
    try {
      String fileLink =
          ReportFactory.createReport(IReport.print_list_eleve_centre, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdGroupeMin", IdGroupeMin)
              .addParam("IdGroupeMax", IdGroupeMax)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error ") + e.getMessage());
    }
  }
}
