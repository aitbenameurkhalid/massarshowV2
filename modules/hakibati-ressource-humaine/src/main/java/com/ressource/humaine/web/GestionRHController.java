package com.ressource.humaine.web;

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
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.hakibati.ressource.humaine.db.GestionRH;
import com.hakibati.ressource.humaine.db.repo.GestionRHRepository;
import com.ressource.humaine.service.GestionRHService;
import com.setting.data.massar.UserMassar;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class GestionRHController {
  @Inject GestionRHRepository gestionRHRepository;

  @Transactional
  public void OpenGestionPersonnel(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionRHRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      GestionRH gestionRH = new GestionRH();
      Beans.get(GestionRHRepository.class).save(gestionRH);
    }

    GestionRH gestionRH =
        gestionRHRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne();

    response.setView(
        ActionView.define(("Données des personnels"))
            .model("com.hakibati.ressource.humaine.db.GestionRH")
            .add("form", "personnel-donnees-form")
            .domain("self.id = " + gestionRH.getId())
            .param("forceEdit", "true")
            .context("_showRecord", gestionRH.getId())
            .map());
  }

  @Transactional
  public void OpenGestionGrevePersonnel(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionRHRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      GestionRH gestionRH = new GestionRH();
      Beans.get(GestionRHRepository.class).save(gestionRH);
    }

    GestionRH gestionRH =
        gestionRHRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne();

    response.setView(
        ActionView.define(("Données des personnels"))
            .model("com.hakibati.ressource.humaine.db.GestionRH")
            .add("form", "personnel-greve-form")
            .domain("self.id = " + gestionRH.getId())
            .param("forceEdit", "true")
            .context("_showRecord", gestionRH.getId())
            .map());
  }

  @Transactional
  public void ConnectionMassar(ActionRequest request, ActionResponse response) throws IOException {
    String loginMassar = (String) request.getContext().get("loginMassar");
    String passwordMassar = (String) request.getContext().get("passwordMassar");

    GestionRHService gestionRHService = new GestionRHService();
    UserMassar userMassar = gestionRHService.ConnexionMassar(loginMassar, passwordMassar);
    if (userMassar != null) {
      response.setValue("NomArabe", userMassar.getNomArabe());
      response.setValue("NomLatine", userMassar.getNomLatine());
      response.setValue("CodeEtablissement", userMassar.getCodeEtablissement());
      response.setHidden("info-login", false);
      response.setHidden("panel-import", false);
    } else {
      response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
    }
  }

  @Transactional
  public void ImportDataMassar(ActionRequest request, ActionResponse response) throws IOException {
    GestionRH gestionRhId = request.getContext().asType(GestionRH.class);
    GestionRH gestionRH = Beans.get(GestionRHRepository.class).find(gestionRhId.getId());

    String loginMassar = (String) request.getContext().get("loginMassar");
    String passwordMassar = (String) request.getContext().get("passwordMassar");

    GestionRHService gestionRHService = new GestionRHService();
    UserMassar userMassar = gestionRHService.ConnexionMassar(loginMassar, passwordMassar);
    if (userMassar != null) {
      response.setValue("NomArabe", userMassar.getNomArabe());
      response.setValue("NomLatine", userMassar.getNomLatine());
      response.setValue("CodeEtablissement", userMassar.getCodeEtablissement());
      response.setHidden("info-login", false);
      response.setHidden("panel-import", false);

      boolean res = gestionRHService.ImportDataMassar(loginMassar, passwordMassar, gestionRH);

    } else {
      response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
    }
  }

  @Transactional
  public void ImportDataEsaise(ActionRequest request, ActionResponse response)
      throws IOException, ParserConfigurationException, SAXException, JAXBException {
    User user = AuthUtils.getUser();
    GestionRH gestionRhId = request.getContext().asType(GestionRH.class);
    GestionRH gestionRH = Beans.get(GestionRHRepository.class).find(gestionRhId.getId());

    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importEsaiseFile")).get("id").toString()));
    GestionRHService gestionRHService = new GestionRHService();

    String res = gestionRHService.ImportDataEsaise(metaFile, gestionRH);
    MetaFiles.getPath(metaFile).toFile().delete();
    response.setAlert(res);
  }

  public void PrintDocumentsGreve(ActionRequest request, ActionResponse response)
      throws ParseException {

    GestionRH gestionRH = request.getContext().asType(GestionRH.class);
    //    LocalDate dateGreveMin =gestionRH.getDateGreveMin();
    //    LocalDate dateGreveMax =gestionRH.getDateGreveMax();

    //    String dateDuCahier = (String) request.getContext().get("dateDuCahier");
    //    if (StringUtils.isBlank(dateDuCahier)) dateDuCahier = "0001-01-01";
    //    String dateAuCahier = (String) request.getContext().get("dateAuCahier");
    //    if (StringUtils.isBlank(dateAuCahier)) dateAuCahier = "9999-01-01";
    //        Date dateGreveMin = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuCahier);
    //        Date dateGreveMax = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuCahier);

    Date dateGreveMin =
        new SimpleDateFormat("yyyy-MM-dd").parse(gestionRH.getDateGreveMin().toString());
    Date dateGreveMax =
        new SimpleDateFormat("yyyy-MM-dd").parse(gestionRH.getDateGreveMax().toString());

    User user = AuthUtils.getUser();
    String listCocumentImprimeGreve = (String) request.getContext().get("listCocumentImprimeGreve");
    if (listCocumentImprimeGreve == null) listCocumentImprimeGreve = "0";

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
              .getResource("../../img/logo-ar.png")
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }

    String name = "print_rh_list_greve_interrogation";
    String reportName = IReport.print_rh_list_greve_interrogation;

    if (listCocumentImprimeGreve.equals("0")) {
      name = "print_rh_list_greve_interrogation";
      reportName = IReport.print_rh_list_greve_interrogation;
    }
    if (listCocumentImprimeGreve.equals("1")) {
      name = "print_rh_list_personnel_greve";
      reportName = IReport.print_rh_list_personnel_greve;
    }
    try {
      String fileLink =
          ReportFactory.createReport(reportName, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("dateGreveMax", dateGreveMax)
              .addParam("dateGreveMin", dateGreveMin)
              .addParam("dateGreveMax", dateGreveMax)
              .addParam("dateGreveMaxStr", gestionRH.getDateGreveMax().toString())
              .addParam("dateGreveMinStr", gestionRH.getDateGreveMin().toString())
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error ") + e.getMessage());
    }
  }
}
