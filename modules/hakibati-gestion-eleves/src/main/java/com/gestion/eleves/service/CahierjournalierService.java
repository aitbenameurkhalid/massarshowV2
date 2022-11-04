package com.gestion.eleves.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.CallMethod;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.hakibati.gestion.eleves.db.*;
import com.hakibati.gestion.eleves.db.repo.ArticleRepository;
import com.hakibati.gestion.eleves.db.repo.MenuHebdomadaireRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CahierjournalierService {
  @Inject ArticleRepository articleRepository;
  @Inject MenuHebdomadaireRepository menuHebdomadaireRepository;

  public void OnMiseAjourCahierJournalier(
      ActionRequest request, ActionResponse response, LocalDate localDate) {

    User user = AuthUtils.getUser();
    //        Cahierjournalier cahierJournalier =
    // request.getContext().asType(Cahierjournalier.class);
    LocalDate localDateNow = (LocalDate) request.getContext().get("dateOperation");
    if (localDate != null) localDateNow = localDate;
    List<PersonnesnourriesJournalier> personnesnourriesJournalierList = new ArrayList<>();
    PersonnesnourriesJournalier personnesnourriesJournalier = new PersonnesnourriesJournalier();
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
  }

  public Cahierjournalier getNewCahierJournallier() {
    return new Cahierjournalier();
  }

  @CallMethod
  public LocalDate getCurretDate() {
    LocalDate localDate = LocalDate.now();
    return localDate;
  }
}
