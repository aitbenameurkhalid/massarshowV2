package com.gestion.eleves.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.hakibati.gestion.eleves.db.*;
import com.hakibati.gestion.eleves.db.repo.ArticleRepository;
import com.hakibati.gestion.eleves.db.repo.MenuHebdomadaireRepository;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class CahierJournalierController {
  @Inject ArticleRepository articleRepository;
  @Inject MenuHebdomadaireRepository menuHebdomadaireRepository;

  public void OnNewCahierJournalier(ActionRequest request, ActionResponse response) {
    LocalDate localDateNow = LocalDate.now();
    response.setValue("dateOperation", localDateNow);
    User user = AuthUtils.getUser();
    //        Cahierjournalier cahierJournalier =
    // request.getContext().asType(Cahierjournalier.class);
    //        cahierJournalier=null;
    List<PersonnesnourriesJournalier> personnesnourriesJournalierList = new ArrayList<>();
    PersonnesnourriesJournalier personnesnourriesJournalier = new PersonnesnourriesJournalier();
    try {
      for (int i = 1; i < 6; i++) {
        personnesnourriesJournalier = new PersonnesnourriesJournalier();
        personnesnourriesJournalier.setTypePersonnes(i);
        personnesnourriesJournalier.setCahierjournalier(new Cahierjournalier());
        personnesnourriesJournalierList.add(personnesnourriesJournalier);
      }
      List<Article> articleList =
          articleRepository
              .all()
              .filter("etablissement = ?", user.getEtablissementSelectionnee())
              .fetch();
      MenuHebdomadaire menuHebdomadaireSelect =
          menuHebdomadaireRepository
              .all()
              .filter(
                  "etablissement = ? AND deteDu <=? AND deteAu>= ?",
                  user.getEtablissementSelectionnee(),
                  localDateNow,
                  localDateNow)
              .fetchOne();

      List<OperationJournalier> operationJournalierList = new ArrayList<>();
      OperationJournalier operationJournalier;

      for (Article article : articleList)
        if (article.getIsExerce()) {
          operationJournalier = new OperationJournalier();
          operationJournalier.setArticle(article);
          operationJournalier.setCahierjournalier(new Cahierjournalier());
          operationJournalierList.add(operationJournalier);
        }
      //            response.setValue("dateOperation", localDateNow);
      if (menuHebdomadaireSelect != null) {
        if (localDateNow.getDayOfWeek().getValue() == 1) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerl());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerl());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerl());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : LUNDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 2) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerma());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerma());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerma());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : MARDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 3) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerme());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerme());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerme());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : MERCREDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 4) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerj());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerj());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerj());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : JEUDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 5) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerv());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerv());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerv());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : VENDREDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 6) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeuners());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeuners());
          response.setValue("menudiner", menuHebdomadaireSelect.getDiners());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : SAMEDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 7) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerd());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerd());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerd());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : DIMANCHE"));
        }
      }
      response.setValue("personnesnourriesJournalier", personnesnourriesJournalierList);
      response.setValue("operationsJournalier", operationJournalierList);
    } catch (Exception e) {
      response.setAlert(e.getMessage());
    }
  }

  public void OnMiseAjourCahierJournalier(ActionRequest request, ActionResponse response) {
    LocalDate localDateNow = (LocalDate) request.getContext().get("dateOperation");
    OnMiseAjourCahierJournalierService(request, response, localDateNow);
    //        CahierjournalierService.OnMiseAjourCahierJournalier(request, response, null);
  }

  public void OnMiseAjourCahierJournalierService(
      ActionRequest request, ActionResponse response, LocalDate localDateNow) {
    User user = AuthUtils.getUser();
    Cahierjournalier cahierJournalier = request.getContext().asType(Cahierjournalier.class);
    List<PersonnesnourriesJournalier> personnesnourriesJournalierList = new ArrayList<>();
    PersonnesnourriesJournalier personnesnourriesJournalier = new PersonnesnourriesJournalier();
    try {

      for (int i = 1; i < 6; i++) {
        personnesnourriesJournalier = new PersonnesnourriesJournalier();
        personnesnourriesJournalier.setTypePersonnes(i);
        if (cahierJournalier == null) {
          personnesnourriesJournalier.setCahierjournalier(new Cahierjournalier());
          personnesnourriesJournalierList.add(personnesnourriesJournalier);
        } else {
          int finalI = i;
          if (cahierJournalier.getPersonnesnourriesJournalier().stream()
              .noneMatch(e -> e.getTypePersonnes() != null && e.getTypePersonnes() == finalI)) {
            personnesnourriesJournalier.setCahierjournalier(cahierJournalier);
            personnesnourriesJournalierList.add(personnesnourriesJournalier);
          } else {
            personnesnourriesJournalier =
                cahierJournalier.getPersonnesnourriesJournalier().stream()
                    .filter(e -> e.getTypePersonnes() != null && e.getTypePersonnes() == finalI)
                    .collect(Collectors.toList())
                    .get(0);
            personnesnourriesJournalierList.add(personnesnourriesJournalier);
          }
        }
      }

      List<Article> articleList =
          articleRepository
              .all()
              .filter("etablissement = ?", user.getEtablissementSelectionnee())
              .fetch();
      MenuHebdomadaire menuHebdomadaireSelect =
          menuHebdomadaireRepository
              .all()
              .filter(
                  "etablissement = ? AND deteDu <=? AND deteAu>= ?",
                  user.getEtablissementSelectionnee(),
                  localDateNow,
                  localDateNow)
              .fetchOne();

      List<OperationJournalier> operationJournalierList = new ArrayList<>();
      OperationJournalier operationJournalier;

      for (Article article : articleList)
        if (article.getIsExerce()) {
          operationJournalier = new OperationJournalier();
          operationJournalier.setArticle(article);
          if (cahierJournalier == null) {
            operationJournalier.setCahierjournalier(new Cahierjournalier());
            operationJournalierList.add(operationJournalier);
          } else {
            if (cahierJournalier.getOperationsJournalier().stream()
                .noneMatch(e -> e.getArticle() != null && e.getArticle() == article)) {
              operationJournalier.setCahierjournalier(cahierJournalier);
              operationJournalierList.add(operationJournalier);
            } else {
              operationJournalier =
                  cahierJournalier.getOperationsJournalier().stream()
                      .filter(e -> e.getArticle() != null && e.getArticle() == article)
                      .collect(Collectors.toList())
                      .get(0);
              operationJournalierList.add(operationJournalier);
            }
          }
        }

      response.setValue("dateOperation", localDateNow);
      if (menuHebdomadaireSelect != null) {
        if (localDateNow.getDayOfWeek().getValue() == 1) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerl());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerl());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerl());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : LUNDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 2) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerma());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerma());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerma());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : MARDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 3) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerme());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerme());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerme());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : MERCREDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 4) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerj());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerj());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerj());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : JEUDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 5) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerv());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerv());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerv());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : VENDREDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 6) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeuners());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeuners());
          response.setValue("menudiner", menuHebdomadaireSelect.getDiners());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : SAMEDI"));
        }
        if (localDateNow.getDayOfWeek().getValue() == 7) {
          response.setValue("menupetitdejeuner", menuHebdomadaireSelect.getPetitdejeunerd());
          response.setValue("menudejeuner", menuHebdomadaireSelect.getDejeunerd());
          response.setValue("menudiner", menuHebdomadaireSelect.getDinerd());
          response.setAttr("panelMenu", "title", I18n.get("Menu Du jour : DIMANCHE"));
        }
      }
      response.setValue("personnesnourriesJournalier", personnesnourriesJournalierList);
      response.setValue("operationsJournalier", operationJournalierList);
    } catch (Exception e) {
      response.setAlert(e.getMessage());
    }
  }

  public void printCahierConsommation(ActionRequest request, ActionResponse response)
      throws IOException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

    String format_print = (String) request.getContext().get("typeImprime");
    LocalDate localDateCahieJounalier = (LocalDate) request.getContext().get("dateOperation");

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
    String document_imprime_name = IReport.print_carnet_de_consommation;
    name = "print_carnet_de_consommation";
    ZoneId defaultZoneId = ZoneId.systemDefault();
    Date dateCahierJournalierMin =
        (Date) Date.from(localDateCahieJounalier.atStartOfDay(defaultZoneId).toInstant());
    Date dateCahierJournalierMax =
        (Date) Date.from(localDateCahieJounalier.atStartOfDay(defaultZoneId).toInstant());
    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              //                            .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("dateCahierJournalierMin", dateCahierJournalierMin)
              .addParam("dateCahierJournalierMax", dateCahierJournalierMax)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  public void printRestEnMagasin(ActionRequest request, ActionResponse response)
      throws IOException, ParseException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLS;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOC;

    String dateRestMagasinStr = (String) request.getContext().get("dateRestMagasin");
    if (StringUtils.isBlank(dateRestMagasinStr)) dateRestMagasinStr = "9999-01-01";
    //        String  dateAuStr = (String) request.getContext().get("au");
    //        if (StringUtils.isBlank(dateAuStr)) dateAuStr = "9999-01-01";
    Date dateRestEnMagasin = new SimpleDateFormat("yyyy-MM-dd").parse(dateRestMagasinStr);

    String UrlLogo = "";
    try {
      UrlLogo =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/logo-ar.png"))
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }
    String document_imprime_name = IReport.print_reste_en_magasin;
    name = "print_reste_en_magasin";
    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              //                            .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("dateRestEnMagasin", dateRestEnMagasin)
              .addFormat(format_print)
              .generate()
              .getFileLink();
      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }
  //

  public void printCahierConsommationMany(ActionRequest request, ActionResponse response)
      throws IOException, ParseException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLS;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOC;

    String dateDuCahier = (String) request.getContext().get("dateDuCahier");
    if (StringUtils.isBlank(dateDuCahier)) dateDuCahier = "0001-01-01";
    String dateAuCahier = (String) request.getContext().get("dateAuCahier");
    if (StringUtils.isBlank(dateAuCahier)) dateAuCahier = "9999-01-01";
    Date dateCahierJournalierMin = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuCahier);
    Date dateCahierJournalierMax = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuCahier);

    String UrlLogo = "";
    try {
      UrlLogo =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/logo-ar.png"))
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }
    String document_imprime_name = IReport.print_carnet_de_consommation;
    name = "print_carnet_de_consommation";
    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              //                            .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("dateCahierJournalierMin", dateCahierJournalierMin)
              .addParam("dateCahierJournalierMax", dateCahierJournalierMax)
              .addFormat(format_print)
              .generate()
              .getFileLink();
      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  public void printComptabiliteMatiere(ActionRequest request, ActionResponse response)
      throws IOException, ParseException {
    User user = AuthUtils.getUser();
    String name = "Badge-Ar";

    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLS;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOC;

    String dateDuComptabilite = (String) request.getContext().get("dateDuComptabilite");
    if (StringUtils.isBlank(dateDuComptabilite)) dateDuComptabilite = "0001-01-01";
    String dateAuComptabilite = (String) request.getContext().get("dateAuComptabilite");
    if (StringUtils.isBlank(dateAuComptabilite)) dateAuComptabilite = "9999-01-01";
    Map selectedArticle = (Map) request.getContext().get("article");
    Integer IdArticleMax = Integer.MAX_VALUE;
    Integer IdArticleMin = Integer.MIN_VALUE;
    if (selectedArticle == null) {
      IdArticleMax = Integer.MAX_VALUE;
      IdArticleMin = Integer.MIN_VALUE;
    } else {
      IdArticleMax = (Integer) selectedArticle.get("id");
      IdArticleMin = (Integer) selectedArticle.get("id");
    }

    Date dateDu = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuComptabilite);
    Date dateAu = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuComptabilite);

    String UrlLogo = "";
    try {
      UrlLogo =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/logo-ar.png"))
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }
    String document_imprime_name = IReport.print_comptabilite_matiere_internat;
    name = "print_comptabilite_matiere_internat";
    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              //                            .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("dateDu", dateDu)
              .addParam("dateAu", dateAu)
              .addParam("IdArticleMax", IdArticleMax)
              .addParam("IdArticleMin", IdArticleMin)
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
