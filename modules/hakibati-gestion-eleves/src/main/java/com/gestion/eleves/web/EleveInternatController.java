package com.gestion.eleves.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Response;
import com.gestion.eleves.service.EleveInternatService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.hakibati.gestion.eleves.db.AnneScolaire;
import com.hakibati.gestion.eleves.db.Eleve;
import com.hakibati.gestion.eleves.db.EleveInternat;
import com.hakibati.gestion.eleves.db.Gestioneleves;
import com.hakibati.gestion.eleves.db.repo.AnneScolaireRepository;
import com.hakibati.gestion.eleves.db.repo.EleveInternatRepository;
import com.hakibati.gestion.eleves.db.repo.EleveRepository;
import com.hakibati.gestion.eleves.db.repo.GestionelevesRepository;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Transactional
public class EleveInternatController {
  @Inject private MetaFiles metaFiles;

  public void exportListInternatExcel(ActionRequest request, ActionResponse response)
      throws IOException {
    Gestioneleves gestionelevesgetId = request.getContext().asType(Gestioneleves.class);
    Long resultaId = (Long) request.getContext().get("id");
    Gestioneleves gestioneleves =
        Beans.get(GestionelevesRepository.class).find(gestionelevesgetId.getId());
    EleveInternatService eleveInternatService = new EleveInternatService();
    File file = eleveInternatService.getFileListInternatExcel(gestioneleves);

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

  public void importListInternatExcel(ActionRequest request, ActionResponse response)
      throws IOException {

    Gestioneleves gestionelevesgetId = request.getContext().asType(Gestioneleves.class);
    Long resultaId = (Long) request.getContext().get("id");
    Gestioneleves gestioneleves =
        Beans.get(GestionelevesRepository.class).find(gestionelevesgetId.getId());

    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importListInternatFile"))
                        .get("id")
                        .toString()));
    EleveInternatService eleveInternatService = new EleveInternatService();
    String res = eleveInternatService.importListEleveInternat(metaFile, gestioneleves);
    response.setAlert(res);
  }

  @Transactional
  public void ArchivedEleveInternatArchevid(ActionRequest request, ActionResponse response) {
    EleveInternat eleveInternatSelecet = request.getContext().asType(EleveInternat.class);
    EleveInternatRepository eleveInternatRepository = Beans.get(EleveInternatRepository.class);
    EleveInternat eleveInternat = eleveInternatRepository.find(eleveInternatSelecet.getId());
    if (eleveInternat != null) {
      //      eleveRepository.remove(eleve);
      eleveInternat.setArchived(true);
      response.setReload(true);
    }
  }

  @Transactional
  public void ArchivedEleveInternatRestaurer(ActionRequest request, ActionResponse response) {
    EleveInternat eleveInternatSelecet = request.getContext().asType(EleveInternat.class);
    EleveInternatRepository eleveInternatRepository = Beans.get(EleveInternatRepository.class);
    EleveInternat eleveInternat = eleveInternatRepository.find(eleveInternatSelecet.getId());
    if (eleveInternat != null) {
      //      eleveRepository.remove(eleve);
      eleveInternat.setArchived(false);
      response.setReload(true);
    }
  }

  @Transactional
  public void ArchivedEleveInternatSupprimer(ActionRequest request, ActionResponse response) {
    EleveInternat eleveInternatSelecet = request.getContext().asType(EleveInternat.class);
    EleveInternatRepository eleveInternatRepository = Beans.get(EleveInternatRepository.class);
    EleveInternat eleveInternat = eleveInternatRepository.find(eleveInternatSelecet.getId());
    if (eleveInternat != null) {
      eleveInternatRepository.remove(eleveInternat);
      response.setReload(true);
    }
  }

  public void RemplaceEleveParMassar(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    EleveInternat eleveInternatSelecet = request.getContext().asType(EleveInternat.class);
    EleveRepository eleveRepository = Beans.get(EleveRepository.class);

    Eleve eleve =
        eleveRepository.findByEtablissemntAndMassarAndAnnee(
            user.getEtablissementSelectionnee().getId(),
            eleveInternatSelecet.getCdMassar(),
            eleveInternatSelecet.getAnneeScolaire().getName());
    if (eleve == null || (eleve.getArchived() != null && eleve.getArchived())) {
      eleve = eleveRepository.findBycdMassar(eleveInternatSelecet.getCdMassar());
    }
    if (eleve == null || (eleve.getArchived() != null && eleve.getArchived())) {
      response.setAlert(
          "Aucun élève ne s'est inscrit à ce Massar: " + eleveInternatSelecet.getCdMassar());
    } else {
      response.setValue("nomAr", eleve.getNomAr());
      response.setValue("prenomAr", eleve.getPrenomAr());
      response.setValue("nomFr", eleve.getNomFr());
      response.setValue("prenomFr", eleve.getPrenomFr());
      response.setValue("sexe", eleve.getSexe());
      //      response.setValue("anneeScolaire", eleve.getAnneeScolaire().getName());
      response.setValue("dateNaissance", eleve.getDateNaissance());
      response.setValue("lieuNaissanceAr", eleve.getLieuNaissanceAr());
      response.setValue("classe", eleve.getClasse().getName());
      response.setValue("idclasse", eleve.getIdclasse());
      response.setValue("etablissementetudie", eleve.getEtablissement().getName());
      response.setNotify(I18n.get("Mise à jour terminée"));
    }
  }

  //  @CallMethod
  //  public Response RecharcheEleveParMassar(String CdMassar, Long anneScolaireId) {
  //    //    Map AnneScolairSelectMap = (Map) request.getContext().get("anneScolair");
  //    //    long anneScolaireId = (Integer) AnneScolairSelectMap.get("id");
  //    final ActionResponse response = new ActionResponse();
  //    response.setAlert("annn="+anneScolaireId);
  //
  //    if(anneScolaireId!= null){
  //      return response;
  //          }
  //
  //        AnneScolaire anneScolaireSelect =
  //     Beans.get(AnneScolaireRepository.class).find(anneScolaireId);
  //
  //
  //
  //
  //
  //
  //    User user = AuthUtils.getUser();
  //    EleveRepository eleveRepository = Beans.get(EleveRepository.class);
  //    Eleve eleve =
  //        eleveRepository.findByEtablissemntAndMassarAndAnnee(
  //            user.getEtablissementSelectionnee().getId(), CdMassar,anneScolaireSelect.getName());
  //    if (eleve == null || (eleve.getArchived() != null && eleve.getArchived())) {
  //      eleve = eleveRepository.findBycdMassar(CdMassar);
  //    }
  //
  //    try {
  //      if (eleve == null || (eleve.getArchived() != null && eleve.getArchived())) {
  //        throw new IllegalArgumentException("Aucun élève ne s'est inscrit à ce Massar: " +
  // CdMassar);
  //      }
  //      String text =
  //          "<li>"
  //              + eleve.getCdMassar()
  //              + "</li>"
  //              + "<li>"
  //              + eleve.getNomAr()
  //              + "</li>"
  //              + "<li>"
  //              + eleve.getPrenomAr()
  //              + "</li>"
  //              + "<li>"
  //              + eleve.getClasse().getName()
  //              + "</li>"
  //              + "<li>"
  //              + eleve.getEtablissement().getName()
  //              + "</li>";
  //
  //      text = "<ul>" + text + "</ul>";
  //      String alertInstall = I18n.get("Tu veux dire l'éleve ? : <p>%s</p> Voulez-vous continuer
  // ?");
  //      Map<String, String> data = Maps.newHashMap();
  //      data.put("alert", String.format(alertInstall, text));
  //      response.setData(ImmutableList.of(data));
  //
  //    } catch (Exception e) {
  //      response.setException(e);
  //    }
  //    return response;
  //  }
  public Response RecharcheEleveParMassar(ActionRequest request, ActionResponse response) {
    EleveInternat eleveInternat = request.getContext().asType(EleveInternat.class);
    AnneScolaire anneScolaireSelect =
        Beans.get(AnneScolaireRepository.class).find(eleveInternat.getAnneeScolaire().getId());
    User user = AuthUtils.getUser();
    EleveRepository eleveRepository = Beans.get(EleveRepository.class);
    Eleve eleve =
        eleveRepository.findByEtablissemntAndMassarAndAnnee(
            user.getEtablissementSelectionnee().getId(),
            eleveInternat.getCdMassar(),
            anneScolaireSelect.getName());
    if (eleve == null || (eleve.getArchived() != null && eleve.getArchived())) {
      eleve = eleveRepository.findBycdMassar(eleveInternat.getCdMassar());
    }

    try {
      if (eleve == null || (eleve.getArchived() != null && eleve.getArchived())) {
        throw new IllegalArgumentException(
            "Aucun élève ne s'est inscrit à ce Massar: " + eleveInternat.getCdMassar());
      }
      String text =
          "<li>"
              + eleve.getCdMassar()
              + "</li>"
              + "<li>"
              + eleve.getNomAr()
              + "</li>"
              + "<li>"
              + eleve.getPrenomAr()
              + "</li>"
              + "<li>"
              + eleve.getClasse().getName()
              + "</li>"
              + "<li>"
              + eleve.getEtablissement().getName()
              + "</li>";

      text = "<ul>" + text + "</ul>";
      String alertInstall = I18n.get("Tu veux dire l'éleve ? : <p>%s</p> Voulez-vous continuer ?");
      Map<String, String> data = Maps.newHashMap();
      data.put("alert", String.format(alertInstall, text));
      response.setData(ImmutableList.of(data));

    } catch (Exception e) {
      response.setException(e);
    }
    return response;
  }

  public void MiseAjouteDateAbsence(ActionRequest request, ActionResponse response)
      throws IOException {
    User user = AuthUtils.getUser();
    String _date_absence = (String) request.getContext().get("_date_absence");
    Gestioneleves gestionelevesgetId = request.getContext().asType(Gestioneleves.class);
    Gestioneleves gestioneleves =
        Beans.get(GestionelevesRepository.class).find(gestionelevesgetId.getId());
    EleveInternatService eleveInternatService = new EleveInternatService();
    String str =
        eleveInternatService.MiseAjourDateAbsenceEleveInternat(gestioneleves, _date_absence);
    //        response.setAlert("mise a jour date =" + _date_absence +" "+ str);
  }

  public void printAllList(ActionRequest request, ActionResponse response) throws IOException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

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
      document_imprime_name = IReport.print_list_eleves_internat_initial;
      name = "print_list_eleves_internat_initial";
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
              //                            .addParam("TableCollectif", TableCollectif)
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
      document_imprime_name = IReport.print_list_eleves_internat_absence_hebdomadaire;
      name = "print_list_eleves_internat_absence_hebdomadaire";
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
              //                            .addParam("IdDoss", IdDoss)
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
}
