package com.ass.enajah.web;

import com.ass.enajah.service.AssInfoService;
import com.ass.enajah.service.ConvertNumber;
import com.ass.enajah.service.OperationService;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.ass.enajah.db.OperationDepense;

public class OperationDepensController {
  @Transactional
  public void OpenAssEnajahInfo(ActionRequest request, ActionResponse response) {
    AssInfoService assInfoService = new AssInfoService();
    Long idAssEnajah = assInfoService.getCurretAssInfo();
    if (idAssEnajah == null) return;
    response.setView(
        ActionView.define(I18n.get("Information"))
            .model("com.hakibati.ass.enajah.db.AssEnajahInfo")
            .add("form", "AssEnajahInfo-form")
            .domain("self.id = " + idAssEnajah)
            .param("forceEdit", "true")
            .context("_showRecord", idAssEnajah)
            .map());
  }

  public void GetnumeroOepartion(ActionRequest request, ActionResponse response) {
    OperationDepense operationDepense = request.getContext().asType(OperationDepense.class);
    if (operationDepense.getName() == null) {
      String str = null;
      str =
          "select (self.name) FROM OperationDepense as self WHERE  year(self.dateDeOperation)= ?1  AND self.id <> ?2 AND self.etablissement= ?3 AND  not( self.name = ?4) order by self.createdOn desc";
      OperationService operationService = new OperationService();
      String numero =
          operationService.getNumeroOperation(
              operationDepense.getDateDeOperation(),
              operationDepense.getId(),
              operationDepense.getEtablissement(),
              str);
      response.setValue("name", numero);
    }
  }

  public void GetSomme(ActionRequest request, ActionResponse response) {

    OperationDepense operationDepense = request.getContext().asType(OperationDepense.class);
    String str = null;
    str = "select SUM (self.totalTTC) FROM TableDepense as self WHERE self.operation.id = ?1 ";
    String somme = OperationService.getSomme(operationDepense, str);

    response.setValue("somme", somme);
    response.setValue("sommeLettreAr", ConvertNumber.AR(somme, "درهم", "سنتيم"));
    response.setValue("sommeLettreFr", ConvertNumber.FR(somme, "Dirham(s)", "Centime(s)"));
  }
}
