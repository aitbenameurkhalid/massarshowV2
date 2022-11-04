package com.comptabilite.matier.repo;

import com.hakibati.comptabilite.matier.db.*;

public class DateDepartRepository
    extends com.hakibati.comptabilite.matier.db.repo.DateDepartRepository {
  //  public static final Integer select_operation = 2;
  //  public static final String Reporte_operation = "Report";
  //  public static final Boolean is_position_depart = true;
  @Override
  public DateDepart save(DateDepart entity) {
    //  User user = AuthUtils.getUser();
    //    EventOperation eventOperation = new EventOperation();
    //    Integer sumE;
    //    Integer sumS;
    //    String strE =
    //        "select SUM (self.quantite)"
    //            + " FROM TableE as self"
    //            + " WHERE self.etablissement.id = ?1 "
    //            + " AND self.operation.dateDeOperation < ?2"
    //            + " AND self.mConsommable.id = ?3 "
    //            + " AND self.operation.archiveOperation = ?4";
    //
    //    String strS =
    //        "select SUM (self.quantite)"
    //            + " FROM TableS as self"
    //            + " WHERE self.etablissement.id = ?1 "
    //            + " AND self.operation.dateDeOperation < ?2"
    //            + " AND self.mConsommable.id = ?3 "
    //            + " AND self.operation.archiveOperation = ?4";
    //    String str =
    //        "select (self.name) FROM OperationE as self WHERE  year(self.dateDeOperation)= ?1
    // AND self.etablissement = ?2 AND  not( self.name = ?3) order by self.createdOn desc";
    //    List<Mconsommable> mconsommableList =
    //        Beans.get(MconsommableRepository.class)
    //            .all()
    //            .filter("etablissement = ?", user.getEtablissementSelectionnee())
    //            .fetch();
    //
    //    OperationE operatDepart = new OperationE();
    //    operatDepart.setNomeDePersonne(this.Reporte_operation);
    //    operatDepart.setDateDeOperation(entity.getDateDepart());
    //    operatDepart.setStatusSelect(this.select_operation);
    //    operatDepart.setIsPositionDepart(this.is_position_depart);
    //    try {
    //      String numeroOp = eventOperation.getNumeroOperation(operatDepart.getDateDeOperation(),
    // str);
    //      operatDepart.setName(numeroOp);
    //    }catch (Exception e){
    //      operatDepart.setName("0/0000");
    //    }
    //    List<TableE> tableEList = new ArrayList<>();
    //    for (int i = 0; i < mconsommableList.size(); i++) {
    //      TableE tableE = new TableE();
    //      tableE.setmConsommable(mconsommableList.get(i));
    //      tableE.setOperation(operatDepart);
    //      sumE =
    //          eventOperation.getRestMagasin(
    //              entity.getDateDepart(),
    //              user.getEtablissementSelectionnee(),
    //              mconsommableList.get(i),
    //              strE);
    //      sumS =
    //          eventOperation.getRestMagasin(
    //              entity.getDateDepart(),
    //              user.getEtablissementSelectionnee(),
    //              mconsommableList.get(i),
    //              strS);
    //      tableE.setQuantite(sumE - sumS);
    //      tableEList.add(tableE);
    //    }
    //    operatDepart.setTablesEntres(tableEList);
    //
    //    List<OperationE> operationEList =
    //        Beans.get(OperationERepository.class)
    //            .all()
    //            .filter(
    //                "etablissement = ? AND dateDeOperation < ? AND archiveOperation = ?",
    //                user.getEtablissementSelectionnee(),
    //                entity.getDateDepart(),
    //                false)
    //            .fetch();
    //    for (int i = 0; i < operationEList.size(); i++) {
    //      operationEList.get(i).setArchiveOperation(true);
    //    }
    //    List<OperationS> operationSList =
    //        Beans.get(OperationSRepository.class)
    //            .all()
    //            .filter(
    //                "etablissement = ? AND dateDeOperation < ? AND archiveOperation = ?",
    //                user.getEtablissementSelectionnee(),
    //                entity.getDateDepart(),
    //                false)
    //            .fetch();
    //    for (int i = 0; i < operationSList.size(); i++) {
    //      operationSList.get(i).setArchiveOperation(true);
    //    }
    //    Beans.get(OperationERepository.class).save(operatDepart);
    return super.save(entity);
  }
}
