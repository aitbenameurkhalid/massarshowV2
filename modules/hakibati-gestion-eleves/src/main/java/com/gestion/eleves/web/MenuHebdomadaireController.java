package com.gestion.eleves.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.hakibati.gestion.eleves.db.MenuHebdomadaire;
import com.hakibati.gestion.eleves.db.repo.MenuHebdomadaireRepository;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class MenuHebdomadaireController {

  public void RecopierMenuHebddomadaire(ActionRequest request, ActionResponse response) {
    Map MenuDuPeriodeSelect = (Map) request.getContext().get("recopierMenuDuPeriode");
    if (MenuDuPeriodeSelect == null) response.setAlert("");
    assert MenuDuPeriodeSelect != null;
    Long MenuDuPeriodeSelectId = (long) (Integer) MenuDuPeriodeSelect.get("id");
    MenuHebdomadaire menuHebdomadaire =
        Beans.get(MenuHebdomadaireRepository.class).find(MenuDuPeriodeSelectId);

    response.setValue("petitdejeunerd", menuHebdomadaire.getPetitdejeunerd());
    response.setValue("dejeunerd", menuHebdomadaire.getDejeunerd());
    response.setValue("dinerd", menuHebdomadaire.getDinerd());

    response.setValue("petitdejeunerl", menuHebdomadaire.getPetitdejeunerl());
    response.setValue("dejeunerl", menuHebdomadaire.getDejeunerl());
    response.setValue("dinerl", menuHebdomadaire.getDinerl());

    response.setValue("petitdejeunerma", menuHebdomadaire.getPetitdejeunerma());
    response.setValue("dejeunerma", menuHebdomadaire.getDejeunerma());
    response.setValue("dinerma", menuHebdomadaire.getDinerma());

    response.setValue("petitdejeunerme", menuHebdomadaire.getPetitdejeunerme());
    response.setValue("dejeunerme", menuHebdomadaire.getDejeunerme());
    response.setValue("dinerme", menuHebdomadaire.getDinerme());

    response.setValue("petitdejeunerj", menuHebdomadaire.getPetitdejeunerj());
    response.setValue("dejeunerj", menuHebdomadaire.getDejeunerj());
    response.setValue("dinerj", menuHebdomadaire.getDinerj());

    response.setValue("petitdejeunerv", menuHebdomadaire.getPetitdejeunerv());
    response.setValue("dejeunerv", menuHebdomadaire.getDejeunerv());
    response.setValue("dinerv", menuHebdomadaire.getDinerv());

    response.setValue("petitdejeuners", menuHebdomadaire.getPetitdejeuners());
    response.setValue("dejeuners", menuHebdomadaire.getDejeuners());
    response.setValue("diners", menuHebdomadaire.getDiners());
  }

  public void PrintMenuHebddomadaire(ActionRequest request, ActionResponse response)
      throws IOException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

    String format_print = (String) request.getContext().get("typeImprime");
    Long menuHebdoId = (long) request.getContext().get("id");

    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLS;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOC;
    String UrlLogo = "";
    try {
      UrlLogo =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/logo-ar.png"))
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }
    String document_imprime_name = IReport.print_menu_hebdomadaire;
    name = "print_menu_hebdomadaire";

    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              //                            .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("menuHebdoId", menuHebdoId)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }
}
