package com.hakibati.svg.examen.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.DossierExam;
import com.hakibati.svg.examen.db.repo.DossierExamRepository;
import com.setting.service.UserService;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.util.Objects;

public class DossierExamController {
  public void printAll(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

    Long IdDossExam = (Long) request.getContext().get("id");
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;
    String UrlLogo = "";
    String UrlphotoUser = "";
    String UrlphotoUserWoman = "";
    try {
      UrlLogo =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/logo-ar.png"))
              .toString()
              .replace("%20", " ");
      UrlphotoUser =
          Objects.requireNonNull(this.getClass().getClassLoader().getResource("../../img/user.png"))
              .toString()
              .replace("%20", " ");
      UrlphotoUserWoman =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/user-woman.jpg"))
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }
    String document_imprime_name = (String) request.getContext().get("documentImprime");
    if (document_imprime_name == null) return;

    if (document_imprime_name.equals("3")) {
      document_imprime_name = IReport.print_date_time;
      name = "print_date_time";
    }
    if (document_imprime_name.equals("4")) {
      document_imprime_name = IReport.print_date_teacher;
      name = "print_date_teacher";
    }
    if (document_imprime_name.equals("5")) {
      document_imprime_name = IReport.print_date_room;
      name = "print_date_room";
    }
    if (document_imprime_name.equals("6")) {
      document_imprime_name = IReport.print_absence_follow;
      name = "print_absence_follow";
    }
    if (document_imprime_name.equals("9")) {
      document_imprime_name = IReport.Badge_149mm_105mm;
      name = "Badge_149mm_105mm";
    }
    if (document_imprime_name.equals("7")) {
      document_imprime_name = IReport.print_statistiques_teacher;
      name = "print_satistiques_teacher";
    }
    if (document_imprime_name.equals("1")) {
      document_imprime_name = IReport.print_convocation_local;
      name = "print_convocation_local";
    }
    if (document_imprime_name.equals("2")) {
      document_imprime_name = IReport.print_convocation_provincial;
      name = "print_convocation_provincial";
    }
    if (document_imprime_name.equals("8")) {
      document_imprime_name = IReport.Badge_90mm_55mm;
      name = "Badge_86mm_54mm";
    }
    if (document_imprime_name.equals("10")) {
      document_imprime_name = IReport.print_attestation_presence;
      name = "print_attestation_presence";
    }
    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              .addParam("IdDossExam", IdDossExam)
              .addParam("UrlLogo", UrlLogo)
              .addParam("UrlphotoUser", UrlphotoUser)
              .addParam("UrlphotoUserWoman", UrlphotoUserWoman)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  @Transactional
  public void DeleteDossierExam(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.svg.examen.DossierExam.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      DossierExam element = request.getContext().asType(DossierExam.class);
      DossierExamRepository elemengRepository = Beans.get(DossierExamRepository.class);
      element = elemengRepository.find(element.getId());
      if (element != null) {
        elemengRepository.remove(element);
        response.setReload(true);
      }
    }
  }
}
