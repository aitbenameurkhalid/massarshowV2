package com.comptabilite.matier.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.comptabilite.matier.service.OperationComptabiliteService;
import com.google.inject.persist.Transactional;
import com.hakibati.comptabilite.matier.db.*;
import com.hakibati.comptabilite.matier.db.repo.DateDepartRepository;
import com.hakibati.comptabilite.matier.db.repo.MconsommableRepository;
import com.hakibati.comptabilite.matier.db.repo.OperationERepository;
import com.hakibati.comptabilite.matier.db.repo.OperationSRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OperationEController {
  //  @Inject
  //  private OperationERepository operationERepository;

  public void getNumeroOperationE(ActionRequest request, ActionResponse response) {

    OperationE operationE = request.getContext().asType(OperationE.class);
    if (operationE.getName() == null) {
      String str = null;
      str =
          "select (self.name) FROM OperationE as self WHERE  year(self.dateDeOperation)= ?1  AND self.id <> ?2 AND self.etablissement= ?3 AND  not( self.name = ?4) order by self.createdOn desc";
      OperationComptabiliteService eventOperation = new OperationComptabiliteService();
      String numero =
          eventOperation.getNumeroOperationE(
              operationE.getDateDeOperation(),
              operationE.getId(),
              operationE.getEtablissement(),
              str);
      response.setValue("name", numero);
    }
  }

  public void getNumeroOperationS(ActionRequest request, ActionResponse response) {

    OperationS operationS = request.getContext().asType(OperationS.class);
    if (operationS.getName() == null) {
      String str = null;
      str =
          "select (self.name) FROM OperationS as self WHERE  year(self.dateDeOperation)= ?1  AND self.id <> ?2 AND self.etablissement= ?3 AND  not( self.name = ?4) order by self.createdOn desc";
      OperationComptabiliteService eventOperation = new OperationComptabiliteService();
      String numero =
          eventOperation.getNumeroOperationE(
              operationS.getDateDeOperation(),
              operationS.getId(),
              operationS.getEtablissement(),
              str);
      response.setValue("name", numero);
    }
  }

  @CallMethod
  public LocalDate getCurretDate() {
    LocalDate localDate = LocalDate.now();
    return localDate;
  }

  @CallMethod
  public String getNomPer() {
    LocalDate localDate = LocalDate.now();
    return "khalid";
  }

  public void getCurretDate0(ActionRequest request, ActionResponse response) {
    LocalDate localDate = LocalDate.now();
    //  response.setValue("dateDeOperation", localDate);
    // response.setValue("nomeDePersonne","khalid");
    response.setAlert("cur date");
  }

  @Transactional
  public void deleteOperationE(ActionRequest request, ActionResponse response) {
    OperationE operationE = request.getContext().asType(OperationE.class);
    OperationERepository operationERepository = Beans.get(OperationERepository.class);
    OperationE e = operationERepository.find(operationE.getId());
    if (e != null && e.getStatusSelect() == 1) {
      operationERepository.remove(e);
      response.setCanClose(true);
      response.setView(
          ActionView.define(I18n.get("opération des  entrées"))
              .model("com.hakibati.comptabilite.matier.db.OperationE")
              .add("grid", "OperationE-grid")
              .add("form", "OperationE-form")
              .map());
    }
  }

  @Transactional
  public void deleteOperationS(ActionRequest request, ActionResponse response) {
    OperationS operationS = request.getContext().asType(OperationS.class);
    OperationSRepository operationSRepository = Beans.get(OperationSRepository.class);
    OperationS e = operationSRepository.find(operationS.getId());
    if (e != null && e.getStatusSelect() == 1) {
      operationSRepository.remove(e);
      response.setCanClose(true);
      response.setView(
          ActionView.define(I18n.get("opération des  sorties"))
              .model("com.hakibati.comptabilite.matier.db.OperationS")
              .add("grid", "OperationS-grid")
              .add("form", "OperationS-form")
              .map());
    }
  }

  @Transactional
  public void OpenUDateDepart(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;
    DateDepartRepository dateDepartRepository = Beans.get(DateDepartRepository.class);

    if (dateDepartRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) dateDepartRepository.save(new DateDepart());

    response.setView(
        ActionView.define(I18n.get("GENERER REPORT"))
            .model("com.hakibati.comptabilite.matier.db.DateDepart")
            .add("form", "MiseEnEtatIni-form")
            .domain("self.etablissement.id = " + user.getEtablissementSelectionnee().getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                dateDepartRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  @Transactional
  public void OnChangeTableE(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    TableE tableE = request.getContext().asType(TableE.class);
    String strE =
        "select SUM (self.quantite)"
            + " FROM TableE as self"
            + " WHERE self.etablissement.id = ?1 "
            + " AND self.mConsommable.id = ?2 "
            + " AND self.operation.archiveOperation = ?3";

    String strS =
        "select SUM (self.quantite)"
            + " FROM TableS as self"
            + " WHERE self.etablissement.id = ?1 "
            + " AND self.mConsommable.id = ?2 "
            + " AND self.operation.archiveOperation = ?3";
    OperationComptabiliteService eventOperation = new OperationComptabiliteService();
    //  LocalDate localDate = LocalDate.now();
    Integer sumE =
        eventOperation.getRestMagasinTotal(
            user.getEtablissementSelectionnee(), tableE.getmConsommable(), strE);
    Integer sumS =
        eventOperation.getRestMagasinTotal(
            user.getEtablissementSelectionnee(), tableE.getmConsommable(), strS);
    response.setValue("restEnMagasin", sumE - sumS + "");
  }

  @Transactional
  public void OnChangeTableS(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    TableS tableS = request.getContext().asType(TableS.class);
    String strE =
        "select SUM (self.quantite)"
            + " FROM TableE as self"
            + " WHERE self.etablissement.id = ?1 "
            + " AND self.mConsommable.id = ?2 "
            + " AND self.operation.archiveOperation = ?3";

    String strS =
        "select SUM (self.quantite)"
            + " FROM TableS as self"
            + " WHERE self.etablissement.id = ?1 "
            + " AND self.mConsommable.id = ?2 "
            + " AND self.operation.archiveOperation = ?3";
    OperationComptabiliteService eventOperation = new OperationComptabiliteService();
    //  LocalDate localDate = LocalDate.now();
    Integer sumE =
        eventOperation.getRestMagasinTotal(
            user.getEtablissementSelectionnee(), tableS.getmConsommable(), strE);
    Integer sumS =
        eventOperation.getRestMagasinTotal(
            user.getEtablissementSelectionnee(), tableS.getmConsommable(), strS);
    response.setValue("restEnMagasin", sumE - sumS + "");
  }

  @Transactional
  public void creePositionDepart(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    Integer select_operation = 2;
    String Reporte_operation = "Report";
    Boolean is_position_depart = true;
    DateDepart entity = request.getContext().asType(DateDepart.class);
    OperationS ss = null;
    ss =
        Beans.get(OperationSRepository.class)
            .all()
            .filter(
                "etablissement = ? AND statusSelect = ? AND archiveOperation = ?",
                user.getEtablissementSelectionnee(),
                1,
                false)
            .fetchOne();
    OperationE ee = null;
    ee =
        Beans.get(OperationERepository.class)
            .all()
            .filter(
                "etablissement = ? AND statusSelect = ? AND archiveOperation = ?",
                user.getEtablissementSelectionnee(),
                1,
                false)
            .fetchOne();
    if (ss != null || ee != null) {
      response.setError(I18n.get("L'opération a échoué car il y a des Operations sont en cours"));
      return;
    }
    OperationComptabiliteService eventOperation = new OperationComptabiliteService();
    Integer sumE;
    Integer sumS;
    String strE =
        "select SUM (self.quantite)"
            + " FROM TableE as self"
            + " WHERE self.etablissement.id = ?1 "
            + " AND self.operation.dateDeOperation < ?2"
            + " AND self.mConsommable.id = ?3 "
            + " AND self.operation.archiveOperation = ?4";

    String strS =
        "select SUM (self.quantite)"
            + " FROM TableS as self"
            + " WHERE self.etablissement.id = ?1 "
            + " AND self.operation.dateDeOperation < ?2"
            + " AND self.mConsommable.id = ?3 "
            + " AND self.operation.archiveOperation = ?4";
    String str =
        "select (self.name) FROM OperationE as self WHERE  year(self.dateDeOperation)= ?1   AND self.etablissement = ?2 AND  not( self.name = ?3) order by self.createdOn desc";
    List<Mconsommable> mconsommableList =
        Beans.get(MconsommableRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    OperationE operatDepart = new OperationE();
    operatDepart.setNomeDePersonne(Reporte_operation);
    operatDepart.setDateDeOperation(entity.getDateDepart());
    operatDepart.setStatusSelect(select_operation);
    operatDepart.setIsPositionDepart(is_position_depart);
    try {
      String numeroOp = eventOperation.getNumeroOperation(operatDepart.getDateDeOperation(), str);
      operatDepart.setName(numeroOp);
    } catch (Exception e) {
      operatDepart.setName("0/" + entity.getDateDepart().getYear());
    }
    List<TableE> tableEList = new ArrayList<>();
    for (int i = 0; i < mconsommableList.size(); i++) {
      TableE tableE = new TableE();
      tableE.setmConsommable(mconsommableList.get(i));
      tableE.setOperation(operatDepart);
      sumE =
          eventOperation.getRestMagasin(
              entity.getDateDepart(),
              user.getEtablissementSelectionnee(),
              mconsommableList.get(i),
              strE);
      sumS =
          eventOperation.getRestMagasin(
              entity.getDateDepart(),
              user.getEtablissementSelectionnee(),
              mconsommableList.get(i),
              strS);
      tableE.setQuantite(sumE - sumS);
      tableEList.add(tableE);
    }
    operatDepart.setTablesEntres(tableEList);

    List<OperationE> operationEList =
        Beans.get(OperationERepository.class)
            .all()
            .filter(
                "etablissement = ? AND dateDeOperation < ? AND archiveOperation = ?",
                user.getEtablissementSelectionnee(),
                entity.getDateDepart(),
                false)
            .fetch();
    for (int i = 0; i < operationEList.size(); i++) {
      operationEList.get(i).setArchiveOperation(true);
      operationEList.get(i).setStatusSelect(4);
    }
    List<OperationS> operationSList =
        Beans.get(OperationSRepository.class)
            .all()
            .filter(
                "etablissement = ? AND dateDeOperation < ? AND archiveOperation = ?",
                user.getEtablissementSelectionnee(),
                entity.getDateDepart(),
                false)
            .fetch();
    for (int i = 0; i < operationSList.size(); i++) {
      operationSList.get(i).setArchiveOperation(true);
      operationSList.get(i).setStatusSelect(4);
    }
    Beans.get(OperationERepository.class).save(operatDepart);
    response.setAlert(I18n.get("L'opération a réussi"));
  }

  @Transactional
  public void AnnuleOperationE(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    String str =
        "select (self.name) FROM OperationS as self WHERE  year(self.dateDeOperation)= ?1   AND self.etablissement = ?2 AND  not( self.name = ?3) order by self.createdOn desc";
    OperationComptabiliteService eventOperation = new OperationComptabiliteService();
    OperationE operationE = request.getContext().asType(OperationE.class);
    OperationS s = new OperationS();
    s.setStatusSelect(2);
    s.setDateDeOperation(LocalDate.now());
    try {
      String numeroOp = eventOperation.getNumeroOperation(s.getDateDeOperation(), str);
      s.setName(numeroOp);
    } catch (Exception e) {
      s.setName("1/" + s.getDateDeOperation().getYear());
    }
    s.setNomeDePersonne(I18n.get("Operation Entree Annule ") + operationE.getName());
    List<TableS> tableSList = new ArrayList<>();
    if (operationE.getTablesEntres() != null)
      for (int i = 0; i < operationE.getTablesEntres().size(); i++) {
        TableS tableS = new TableS();
        tableS.setOperation(s);
        tableS.setmConsommable(operationE.getTablesEntres().get(i).getmConsommable());
        tableS.setQuantite(operationE.getTablesEntres().get(i).getQuantite());
        tableSList.add(tableS);
      }
    s.setTablesSorties(tableSList);
    Beans.get(OperationSRepository.class).save(s);
  }

  @Transactional
  public void AnnuleOperationS(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    String str =
        "select (self.name) FROM OperationE as self WHERE  year(self.dateDeOperation)= ?1   AND self.etablissement = ?2 AND  not( self.name = ?3) order by self.createdOn desc";
    OperationComptabiliteService eventOperation = new OperationComptabiliteService();
    OperationS operationS = request.getContext().asType(OperationS.class);
    OperationE e = new OperationE();
    e.setStatusSelect(2);
    e.setDateDeOperation(LocalDate.now());
    try {
      String numeroOp = eventOperation.getNumeroOperation(e.getDateDeOperation(), str);
      e.setName(numeroOp);
    } catch (Exception ee) {
      e.setName("1/" + e.getDateDeOperation().getYear());
    }
    e.setNomeDePersonne(I18n.get("Operation Entree Annule ") + operationS.getName());
    List<TableE> tableEList = new ArrayList<>();
    if (operationS.getTablesSorties() != null)
      for (int i = 0; i < operationS.getTablesSorties().size(); i++) {
        TableE tableE = new TableE();
        tableE.setOperation(e);
        tableE.setmConsommable(operationS.getTablesSorties().get(i).getmConsommable());
        tableE.setQuantite(operationS.getTablesSorties().get(i).getQuantite());
        tableEList.add(tableE);
      }
    e.setTablesEntres(tableEList);
    Beans.get(OperationERepository.class).save(e);
  }
}
