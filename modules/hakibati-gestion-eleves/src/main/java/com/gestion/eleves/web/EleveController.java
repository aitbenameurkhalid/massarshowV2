package com.gestion.eleves.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.gestion.eleves.db.AnneScolaire;
import com.hakibati.gestion.eleves.db.Eleve;
import com.hakibati.gestion.eleves.db.repo.EleveRepository;
import com.setting.service.UserService;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.IOException;
import java.util.Objects;

@Singleton
@Transactional
public class EleveController {

  public void printAllList(ActionRequest request, ActionResponse response) throws IOException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";
    AnneScolaire anneScolaire = (AnneScolaire) request.getContext().get("anneScolaireActuele");
    String format_print = (String) request.getContext().get("typeImprime");
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
    String document_imprime_name = (String) request.getContext().get("listImprime");
    if (document_imprime_name == null) return;

    if (document_imprime_name.equals("0")) {
      document_imprime_name = IReport.print_list_eleves_initiale;
      name = "Listes_initiales";
    }
    if (document_imprime_name.equals("1")) {
      document_imprime_name = IReport.print_list_eleves_actuele;
      name = "Listes_des_eleves_actuels";
    }
    if (document_imprime_name.equals("2")) {
      document_imprime_name = IReport.print_list_eleves_actuele_groupe;
      name = "Listes_des_eleves_actuels";
    }

    if (document_imprime_name.equals("3")) {
      document_imprime_name = IReport.print_list_eleves_sortants;
      name = "Listes_des_eleves_sortants";
    }
    if (document_imprime_name.equals("4")) {
      document_imprime_name = IReport.print_list_eleves_abandon;
      name = "Listes_des_eleves_abandon";
    }
    if (document_imprime_name.equals("5")) {
      document_imprime_name = IReport.print_list_eleves_arrevants;
      name = "Listes_des_eleves_Arrivées";
    }
    if (document_imprime_name.equals("6")) {
      document_imprime_name = IReport.print_list_eleves_reintegration;
      name = "Listes_des_eleves_Reintegration";
    }

    if (document_imprime_name.equals("7")) {
      document_imprime_name = IReport.print_list_eleves_non_inscrit;
      name = "Listes_des_eleves_inscrit";
    }

    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              //                            .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("IdAnneeScolaire", anneScolaire.getId())
              //                            .addParam("TitreTableCollective", TitreTableCollective)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  public void printAllDocument(ActionRequest request, ActionResponse response) throws IOException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";
    AnneScolaire anneScolaire = (AnneScolaire) request.getContext().get("anneScolaireActuele");
    String format_print = (String) request.getContext().get("typeImprime");
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
    String document_imprime_name = (String) request.getContext().get("listfeuilleImprime");
    String date_du = (String) request.getContext().get("date_du");
    String date_au = (String) request.getContext().get("date_au");
    if (date_du == null) date_du = "..................";
    if (date_au == null) date_au = "..................";
    //        String  date_du = "..................";
    //        String date_au = "..................";

    if (document_imprime_name == null) return;

    if (document_imprime_name.equals("0")) {
      document_imprime_name = IReport.print_list_absence_hebdomadaire_paysage;
      name = "feuille_absence_hebdomadaire_paysage";
    }
    //    if (document_imprime_name.equals("0")) {
    //      document_imprime_name = IReport. print_list_absence_hebdomadaire_paysage_groupe;
    //      name = "feuille_absence_hebdomadaire_paysage_groupe";
    //    }

    if (document_imprime_name.equals("1")) {
      document_imprime_name = IReport.print_list_absence_hebdomadaire_portrait;
      name = "feuille_absence_hebdomadaire_portrait";
    }
    if (document_imprime_name.equals("2")) {
      document_imprime_name = IReport.print_list_absence_hebdomadaire_demi_1;
      name = "Feuille-absences-demi-hebdomadaire-1";
    }

    if (document_imprime_name.equals("3")) {
      document_imprime_name = IReport.print_list_absence_hebdomadaire_demi_2;
      name = "Feuille-absences-demi-hebdomadaire-2";
    }
    if (document_imprime_name.equals("4")) {
      document_imprime_name = IReport.print_list_absence_hebdomadaire_paysage_groupe;
      name = "feuille_absence_hebdomadaire_paysage_groupe";
    }
    if (document_imprime_name.equals("5")) {
      document_imprime_name = IReport.print_list_notes_eleves_2;
      name = "list_notes_eleves_2";
    }
    if (document_imprime_name.equals("6")) {
      document_imprime_name = IReport.print_list_notes_eleves_3;
      name = "list_notes_eleves_3";
    }
    if (document_imprime_name.equals("7")) {
      document_imprime_name = IReport.print_list_notes_eleves_4;
      name = "list_notes_eleves_4";
    }

    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              .addParam("IdAnneeScolaire", anneScolaire.getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("date_du", date_du)
              .addParam("date_au", date_au)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  public void printAllStatistiques(ActionRequest request, ActionResponse response)
      throws IOException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";
    AnneScolaire anneScolaire = (AnneScolaire) request.getContext().get("anneScolaireActuele");
    String format_print = (String) request.getContext().get("typeImprime");
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
    String document_imprime_name = (String) request.getContext().get("liststatistiques");
    String evaluation = (String) request.getContext().get("evaluation");
    String semistre = (String) request.getContext().get("session");
    Integer evaluationIdMax = Integer.MAX_VALUE;
    Integer evaluationIdMin = Integer.MIN_VALUE;
    Integer semistreIdMax = Integer.MAX_VALUE;
    Integer semistreIdMin = Integer.MIN_VALUE;

    if (evaluation != null) {
      evaluationIdMax = Integer.parseInt(evaluation);
      evaluationIdMin = Integer.parseInt(evaluation);
    }
    if (semistre != null) {
      semistreIdMax = Integer.parseInt(semistre);
      semistreIdMin = Integer.parseInt(semistre);
    }
    //    if (date_du == null) date_du = "..................";
    //    if (date_au == null) date_au = "..................";
    //        String  date_du = "..................";
    //        String date_au = "..................";

    if (document_imprime_name == null) return;
    if (document_imprime_name.equals("0")) {
      document_imprime_name = IReport.print_affectation_classes;
      name = "print_affectation_classes";
    }

    if (document_imprime_name.equals("1")) {
      document_imprime_name = IReport.print_affectation_profs;
      name = "print_affectation_profs";
    }
    if (document_imprime_name.equals("2")) {
      document_imprime_name = IReport.print_mouvemant_eleves;
      name = "print_mouvemant_eleves";
    }
    if (document_imprime_name.equals("3")) {
      document_imprime_name = IReport.print_statistiques_classe_sexe;
      name = "print_statistiques_classe_sexe";
    }

    if (document_imprime_name.equals("4")) {
      document_imprime_name = IReport.print_statistiques_age_sexe;
      name = "print_statistiques_age_sexe";
    }
    if (document_imprime_name.equals("10")) {
      document_imprime_name = IReport.print_investir_evaluation;
      name = "print_investir_evaluation";
    }

    //    if (document_imprime_name.equals("6")) {
    //      document_imprime_name = IReport.print_list_notes_eleves_3;
    //      name = "list_notes_eleves_3";
    //    }
    //    if (document_imprime_name.equals("7")) {
    //      document_imprime_name = IReport.print_list_notes_eleves_4;
    //      name = "list_notes_eleves_4";
    //    }

    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              .addParam("IdAnneeScolaire", anneScolaire.getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("evaluationIdMax", evaluationIdMax)
              .addParam("evaluationIdMin", evaluationIdMin)
              .addParam("semistreIdMax", semistreIdMax)
              .addParam("semistreIdMin", semistreIdMin)
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
  public void ArchivedEleveSelect(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.gestion.eleves.Eleve.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Eleve eleveSelecet = request.getContext().asType(Eleve.class);
      EleveRepository eleveRepository = Beans.get(EleveRepository.class);
      Eleve eleve = eleveRepository.find(eleveSelecet.getId());
      if (eleve != null) {
        //      eleveRepository.remove(eleve);
        eleve.setArchived(true);
        response.setReload(true);
      }
    }
  }

  @Transactional
  public void RestoreEleveSelect(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.gestion.eleves.Eleve.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Eleve eleveSelecet = request.getContext().asType(Eleve.class);
      EleveRepository eleveRepository = Beans.get(EleveRepository.class);
      Eleve eleve = eleveRepository.find(eleveSelecet.getId());
      if (eleve != null) {
        //      eleveRepository.remove(eleve);
        eleve.setArchived(false);
        response.setReload(true);
      }
    }
  }

  @Transactional
  public void DeleteEleveSelect(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.gestion.eleves.Eleve.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Eleve eleveSelecet = request.getContext().asType(Eleve.class);
      EleveRepository eleveRepository = Beans.get(EleveRepository.class);
      Eleve eleve = eleveRepository.find(eleveSelecet.getId());
      if (eleve != null) {
        eleveRepository.remove(eleve);
        response.setReload(true);
      }
    }
  }
}
