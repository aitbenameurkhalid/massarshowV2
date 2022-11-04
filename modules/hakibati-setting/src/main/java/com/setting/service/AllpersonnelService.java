package com.setting.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JPA;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.AllEtabMassar;
import com.hakibati.setting.db.Allpersonnel;
import com.hakibati.setting.db.repo.AllEtabMassarRepository;
import com.hakibati.setting.db.repo.AllpersonnelRepository;
import com.setting.data.massar.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// @Transactional
@Singleton
@Transactional(rollbackOn = {Exception.class})
public class AllpersonnelService {
  //    private Logger log = LoggerFactory.getLogger(AllpersonnelService.class);
  public static List<Allpersonnel> getListPersonnels(String gresa) {
    List<Allpersonnel> allpersonnelList =
        Beans.get(AllpersonnelRepository.class).all().filter("gresa = ? ", gresa).fetch();
    return allpersonnelList;
  }

  public static AllEtabMassar getCurrentEtabMassar(String gresa) {
    AllEtabMassar allEtabMassar =
        Beans.get(AllEtabMassarRepository.class).all().filter("cdETAB = ? ", gresa).fetchOne();
    return allEtabMassar;
  }

  @Transactional
  public String ImportDataProfMassar(
      String loginMassar,
      String passwordMassar,
      String typeEtab,
      boolean ensPrimG,
      boolean ensPrimO,
      boolean ensColG,
      boolean ensColO,
      boolean ensQualG,
      boolean ensQualO,
      boolean ensQualT,
      boolean ensPrescoM,
      boolean ensPrescoC,
      boolean ensBts,
      boolean ensCPGE)
      throws IOException {

    //    List<Allpersonnel> allpersonnelList =
    // Beans.get(AllpersonnelRepository.class).all().fetch();

    //
    //        List<AllEtabMassar> allEtabMassarList0 =
    //                Beans.get(AllEtabMassarRepository.class)
    //                        .all()
    //                        .filter(
    //                                "typeEtab = ? "
    //                                        + "AND( ensPrimG = ? "
    //                                        + "OR ensPrimO = ? )"
    //                                        + "AND ( ensColG = ? "
    //                                        + "OR ensColO = ? )"
    //                                        + "AND ( ensQualG = ? "
    //                                        + "OR ensQualO = ? "
    //                                        + "OR ensQualT = ? )"
    //                                        + "AND( ensPrescoM = ? "
    //                                        + "OR ensPrescoC = ? )"
    //                                        + "AND (ensBts = ? "
    //                                        + "OR ensCPGE = ? )",
    //                                typeEtab,
    //                                ensPrimG,
    //                                ensPrimO,
    //                                ensColG,
    //                                ensColO,
    //                                ensQualG,
    //                                ensQualO,
    //                                ensQualT,
    //                                ensPrescoM,
    //                                ensPrescoC,
    //                                ensBts,
    //                                ensCPGE)
    //                        .fetch();
    String direction = "عمالة: فاس";
    List<AllEtabMassar> allEtabMassarList =
        Beans.get(AllEtabMassarRepository.class).all().filter("direction = ? ", direction).fetch();

    int count = allEtabMassarList.size();
    String cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    if (cookie == null) return "connection failbe";
    for (AllEtabMassar allEtabMassar : allEtabMassarList) {
      List<Allpersonnel> allpersonnelList = allEtabMassar.getAllpersonnels();
      cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
      if (cookie == null) continue;
      String CdEtab = allEtabMassar.getCdETAB();
      System.out.println("CdEtab :: " + CdEtab + "  c::" + count--);
      //    log.info("CdEtab :: " + CdEtab + "  c::" + count);
      DataMassar dataMassAllPersonnelList = getAllPersonnel(cookie, CdEtab);
      if (dataMassAllPersonnelList != null
          && dataMassAllPersonnelList.getData() != null
          && dataMassAllPersonnelList.getData().getAllPersonnel() != null)
        for (AllPersonnelMassar allPersonnelMassar :
            dataMassAllPersonnelList.getData().getAllPersonnel()) {
          //  System.out.println("CdEtab :: " + CdEtab + "  c::" + count);
          try {
            Allpersonnel allpersonnel =
                allpersonnelList.stream()
                    .filter(
                        e ->
                            e.getCode() != null
                                && e.getCode() == allPersonnelMassar.getpPR()
                                && e.getGresa() != null
                                && Objects.equals(e.getGresa(), CdEtab))
                    .findAny()
                    .orElse(null);
            if (allpersonnel == null) {
              allpersonnel = new Allpersonnel();
              allpersonnelList.add(allpersonnel);
              allpersonnel.setCode(allPersonnelMassar.getpPR());
              allpersonnel.setName(allPersonnelMassar.getnOMA());
              allpersonnel.setNameFr(allPersonnelMassar.getnOML());
              allpersonnel.setCin(allPersonnelMassar.getcINA() + allPersonnelMassar.getcINN());
              allpersonnel.setGrade(allPersonnelMassar.getcD_GRADE());
              allpersonnel.setCadre(allPersonnelMassar.getcD_CADRE());
              allpersonnel.setGresa(CdEtab);
              allpersonnel.setEtablissement(allEtabMassar.getNnomA());
              allpersonnel.setDirection(allEtabMassar.getDirection());
              allpersonnel.setAllEtabMassar(allEtabMassar);
              //              JPA.save(allpersonnel);
              //              count++;
            } else {
              allpersonnel.setCode(allPersonnelMassar.getpPR());
              allpersonnel.setName(allPersonnelMassar.getnOMA());
              allpersonnel.setNameFr(allPersonnelMassar.getnOML());
              allpersonnel.setCin(allPersonnelMassar.getcINA() + allPersonnelMassar.getcINN());
              allpersonnel.setGrade(allPersonnelMassar.getcD_GRADE());
              allpersonnel.setCadre(allPersonnelMassar.getcD_CADRE());
              allpersonnel.setGresa(CdEtab);
              allpersonnel.setEtablissement(allEtabMassar.getNnomA());
              allpersonnel.setDirection(allEtabMassar.getDirection());
              allpersonnel.setAllEtabMassar(allEtabMassar);

              //                      Beans.get(AllpersonnelRepository.class).save(allpersonnel);
            }
          } catch (Exception e) {
            //                        log.error(e.getMessage(), e);
          }
        }
      dataMassAllPersonnelList = getPersonnelsByEtabAndCyclel(cookie, CdEtab);

      if (dataMassAllPersonnelList != null
          && dataMassAllPersonnelList.getData() != null
          && dataMassAllPersonnelList.getData().getPersonnelsByEtabAndCycle() != null)
        for (PersonnelsByEtabMassar allPersonnelMassar :
            dataMassAllPersonnelList.getData().getPersonnelsByEtabAndCycle()) {

          //  System.out.println("CdEtab :: " + CdEtab + "  c::" + count);
          try {
            Allpersonnel allpersonnel =
                allpersonnelList.stream()
                    .filter(
                        e ->
                            e.getCode() != null
                                && e.getCode() == allPersonnelMassar.getPpr()
                                && e.getGresa() != null
                                && Objects.equals(e.getGresa(), CdEtab))
                    .findAny()
                    .orElse(null);

            if (allpersonnel == null) {
              allpersonnel = new Allpersonnel();
              allpersonnelList.add(allpersonnel);
              allpersonnel.setCode(allPersonnelMassar.getPpr());
              allpersonnel.setName(allPersonnelMassar.getNomA());
              allpersonnel.setNameFr(allPersonnelMassar.getNomL());
              allpersonnel.setCin(allPersonnelMassar.getCina() + allPersonnelMassar.getCinn());
              allpersonnel.setGresa(CdEtab);
              allpersonnel.setEtablissement(allEtabMassar.getNnomA());
              allpersonnel.setDirection(allEtabMassar.getDirection());
              allpersonnel.setAllEtabMassar(allEtabMassar);
              //              JPA.save(allpersonnel);
              //              count++;
            } else {
              allpersonnel.setCode(allPersonnelMassar.getPpr());
              allpersonnel.setName(allPersonnelMassar.getNomA());
              allpersonnel.setNameFr(allPersonnelMassar.getNomL());
              allpersonnel.setCin(allPersonnelMassar.getCina() + allPersonnelMassar.getCinn());
              allpersonnel.setGresa(CdEtab);
              allpersonnel.setEtablissement(allEtabMassar.getNnomA());
              allpersonnel.setDirection(allEtabMassar.getDirection());
              allpersonnel.setAllEtabMassar(allEtabMassar);
              //                       Beans.get(AllpersonnelRepository.class).save(allpersonnel);
            }
          } catch (Exception e) {
            //                        log.error(e.getMessage(), e);
          }
        }
    }

    //        for (Allpersonnel allpersonnel : allpersonnelList) {
    //            try {
    //                Beans.get(AllpersonnelRepository.class).save(allpersonnel);
    //            JPA.save(allpersonnel);
    //            count--;
    //            if (count % 100 == 1) System.out.println("CdPPR :: " + allpersonnel.getCode() + "
    // c::" + count);
    //            } catch (Exception e) {
    //                log.error(e.getMessage(), e);
    //            }
    //        }
    return "count personnels = " + count;
  }

  private DataMassar getAllPersonnel(String cookie, String CdEtab) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String requestBodyAllPersonnel =
          "{\"operationName\":null,\"variables\":{},\"query\":\"{ allPersonnel(where: {cdEtab: \\\""
              + CdEtab
              + "\\\"}) {"
              + " cdEtab cd_matiere  nOMA  nOML  pPR   pRENOMA   pRENOML cD_CADRE cD_GRADE cINA cINN"
              + " }}\"}";

      String jsonBodyBodyAllPersonnel =
          MassarMenGovService.getDataMassar(cookie, requestBodyAllPersonnel, "formationcontinuegw");
      return mapper.readValue(jsonBodyBodyAllPersonnel, DataMassar.class);
    } catch (Exception e) {
      return new DataMassar();
    }
  }

  private DataMassar getPersonnelsByEtabAndCyclel(String cookie, String CdEtab) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      //      String requestBodyAllPersonnel =
      String requestBodyAllPersonnel =
          "{\n \"operationName\": null,\n  \"variables\": {},\n  \"query\": \"{\\n  personnelsByEtabAndCycle(cdEtab: \\\""
              + CdEtab
              + "\\\", cycle: \\\"\\\") {\\n    idPersonnel\\n    nomA\\n    prenomA\\n    nomL\\n    prenomL\\n    cdMatiere\\n    ppr\\n  cina  cinn  __typename\\n  }\\n}\\n\"\n}";

      String jsonBodyBodyAllPersonnel =
          MassarMenGovService.getDataMassar(cookie, requestBodyAllPersonnel, "formationcontinuegw");
      return mapper.readValue(jsonBodyBodyAllPersonnel, DataMassar.class);
    } catch (Exception e) {
      return new DataMassar();
    }
  }

  private DataMassar getAllEtablissement(String cookie, String CdCom) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String requestBodyallEtabs =
          "{\n"
              + "    \"operationName\": null,\n"
              + "    \"variables\": {},\n"
              + "    \"query\": \"{ allEtabs(where: {cD_COM: \\\""
              + CdCom
              + " \\\" }) {cD_ETAB   nOM_ETABA   nOM_ETABL typeEtab  cD_COM ensBts ensColG  ensColO ensCPGE ensPrescoC ensPrescoM ensPrimG  ensPrimO ensQualG ensQualO ensQualT}}\""
              + "}";
      String jsonBodyBodyallEtabs =
          MassarMenGovService.getDataMassar(cookie, requestBodyallEtabs, "formationcontinuegw");

      return mapper.readValue(jsonBodyBodyallEtabs, DataMassar.class);
    } catch (Exception e) {
      return new DataMassar();
    }
  }

  private DataMassar getAllCommuns(String cookie, String cdProv) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String requestBodyallcommunes =
          "{\n"
              + "    \"operationName\": null,\n"
              + "    \"variables\": {},\n"
              + "    \"query\": \"{\\n  communes(where: {cD_PROV: \\\""
              + cdProv
              + "\\\" }) {cd_com ll_com}}\"}";
      String jsonBodyBodyallcommunes =
          MassarMenGovService.getDataMassar(cookie, requestBodyallcommunes, "formationcontinuegw");
      return mapper.readValue(jsonBodyBodyallcommunes, DataMassar.class);
    } catch (Exception e) {
      return new DataMassar();
    }
  }

  private DataMassar getAllDirection(String cookie) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    try {
      String requestBodyallProv =
          "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  allProv() { cD_PROV lL_PROV lA_PROV }\\n}\\n\"}";
      String jsonBodyBodyallProv =
          MassarMenGovService.getDataMassar(cookie, requestBodyallProv, "formationcontinuegw");
      return mapper.readValue(jsonBodyBodyallProv, DataMassar.class);
    } catch (Exception e) {
      return new DataMassar();
    }
  }

  @Transactional
  public String ImportDataEtabMassar(String loginMassar, String passwordMassar) throws IOException {
    List<AllEtabMassar> allEtabMassarList = Beans.get(AllEtabMassarRepository.class).all().fetch();
    int count = 0;
    String cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    if (cookie == null) return " connection failbe";
    DataMassar dataMassAllAllDirectionList = getAllDirection(cookie);

    if (dataMassAllAllDirectionList != null
        && dataMassAllAllDirectionList.getData() != null
        && dataMassAllAllDirectionList.getData().getAllProv() != null)
      for (DirectionMassar AlldirectionMassar :
          dataMassAllAllDirectionList.getData().getAllProv()) {
        String CdProv = AlldirectionMassar.getcD_PROV();
        //                                String CdProv ="291";
        System.out.println("CdProv :: " + CdProv + "  c::" + count);
        cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
        if (cookie == null) continue;
        DataMassar dataMassAllAllCommunList = getAllCommuns(cookie, CdProv);
        if (dataMassAllAllCommunList != null
            && dataMassAllAllCommunList.getData() != null
            && dataMassAllAllCommunList.getData().getCommunes() != null)
          for (CommuneMassar communeMassar : dataMassAllAllCommunList.getData().getCommunes()) {
            String CdCom = communeMassar.getCd_com();
            //    System.out.println("CdCom :: " + CdCom + "  c::" + count);
            DataMassar dataMassrAllEtabList = getAllEtablissement(cookie, CdCom);
            if (dataMassrAllEtabList != null
                && dataMassrAllEtabList.getData() != null
                && dataMassrAllEtabList.getData().getAllEtabs() != null)
              for (EtablissementMassar AllEtabs : dataMassrAllEtabList.getData().getAllEtabs()) {
                //          System.out.println("CdEtab :: " + AllEtabs.getcD_ETAB() + "  c::" +
                // count);
                count++;
                try {
                  AllEtabMassar allEtabMassar =
                      allEtabMassarList.stream()
                          .filter(
                              e ->
                                  e.getCdETAB() != null
                                      && Objects.equals(e.getCdETAB(), AllEtabs.getcD_ETAB()))
                          .findAny()
                          .orElse(null);

                  if (allEtabMassar == null) {
                    allEtabMassar = new AllEtabMassar();
                    allEtabMassarList.add(allEtabMassar);
                  }
                  allEtabMassar.setCdETAB(AllEtabs.getcD_ETAB());
                  allEtabMassar.setNnomA(AllEtabs.getnOM_ETABA());
                  allEtabMassar.setNomL(AllEtabs.getnOM_ETABL());
                  allEtabMassar.setEnsBts(AllEtabs.isEnsBts());
                  allEtabMassar.setEnsColG(AllEtabs.isEnsColG());
                  allEtabMassar.setEnsColO(AllEtabs.isEnsColO());
                  allEtabMassar.setEnsCPGE(AllEtabs.isEnsCPGE());
                  allEtabMassar.setEnsPrescoC(AllEtabs.isEnsPrescoC());
                  allEtabMassar.setEnsPrescoM(AllEtabs.isEnsPrescoM());
                  allEtabMassar.setEnsPrimG(AllEtabs.isEnsPrimG());
                  allEtabMassar.setEnsPrimO(AllEtabs.isEnsPrimO());
                  allEtabMassar.setEnsQualG(AllEtabs.isEnsQualG());
                  allEtabMassar.setEnsQualO(AllEtabs.isEnsQualO());
                  allEtabMassar.setEnsQualT(AllEtabs.isEnsQualT());
                  allEtabMassar.setTypeEtab(AllEtabs.getTypeEtab());
                  allEtabMassar.setDirection(AlldirectionMassar.getlA_PROV());
                  //   Beans.get(AllEtabMassarRepository.class).save(allEtabMassar);
                } catch (Exception e) {
                  //                                    log.error(e.getMessage(), e);
                }
              }
          }
      }
    count = 0;
    for (AllEtabMassar allEtabMassar : allEtabMassarList) {
      //            try {
      //     Beans.get(AllEtabMassarRepository.class).save(allEtabMassar);
      JPA.save(allEtabMassar);
      count++;
      if (count % 100 == 1)
        System.out.println("save  :: " + allEtabMassar.getCdETAB() + "  c::" + count);
      //            } catch (Exception e) {
      //                log.error(e.getMessage(), e);
      //            }
    }
    return "count etablissemnt = " + count;
  }

  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;

  public File getFileListPersonnelsExcel(String direction) throws IOException {
    //    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    User user = AuthUtils.getUser();

    excelFile = File.createTempFile("List_Personnels", ".xlsx");
    workbook = new XSSFWorkbook();
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();

    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setWrapText(true);
    font.setFontHeightInPoints((short) 10);
    cellStyle.setFont(font);

    CellStyle cellStyleBody = workbook.createCellStyle();
    cellStyleBody.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleBody.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
    cellStyleBody.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyleBody.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyleBody.setBorderRight(CellStyle.BORDER_THIN);
    cellStyleBody.setBorderTop(CellStyle.BORDER_THIN);
    cellStyleBody.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleBody.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyleBody.setFillForegroundColor(IndexedColors.WHITE.getIndex());
    cellStyleBody.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyleBody.setWrapText(true);
    cellStyleBody.setFont(font);

    sheet = workbook.createSheet("DATA");
    int rowSheet = 0;

    Row rowHead = sheet.createRow(rowSheet);
    Cell cell = rowHead.createCell(0);
    cell.setCellValue(I18n.get("user.getEtablissementSelectionnee().getCodeUuid() + " + "i"));
    cell.setCellStyle(cellStyleBody);
    cell = rowHead.createCell(1);
    cell.setCellValue(I18n.get("N°"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(2);
    cell.setCellValue(I18n.get("PPr"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(3);
    cell.setCellValue(I18n.get("CIN"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(4);
    cell.setCellValue(I18n.get("Nom Et Prenom"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(5);
    cell.setCellValue(I18n.get("الاسم الكامل"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(6);
    cell.setCellValue(I18n.get("RIB"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(7);
    cell.setCellValue(I18n.get("bankAgence"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(8);
    cell.setCellValue(I18n.get("cadre"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(9);
    cell.setCellValue(I18n.get("grade"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(10);
    cell.setCellValue(I18n.get("gresaEtab"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(11);
    cell.setCellValue(I18n.get("etablissemnt"));
    cell.setCellStyle(cellStyle);

    List<Allpersonnel> allpersonnelList =
        Beans.get(AllpersonnelRepository.class).all().filter("direction = ? ", direction).fetch();

    for (Allpersonnel personnelList : allpersonnelList) {
      rowSheet++;
      rowHead = sheet.createRow(rowSheet);
      cell = rowHead.createCell(1);
      cell.setCellValue(rowSheet);
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(2);
      if (personnelList.getCode() != null) cell.setCellValue((personnelList.getCode()));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(3);
      if (personnelList.getCin() != null) cell.setCellValue((personnelList.getCin()));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(4);
      if (personnelList.getNameFr() != null) cell.setCellValue((personnelList.getNameFr()));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(5);
      if (personnelList.getName() != null) cell.setCellValue((personnelList.getName()));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(6);
      //            if (personnelList.getRib() != null)
      //                cell.setCellValue(personnelList.getRibtemp());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(7);
      //            if (personnelList.getBankAgence() != null)
      //                cell.setCellValue(personnelList.getBankAgence());
      cell.setCellStyle(cellStyleBody);
      cell = rowHead.createCell(8);
      if (personnelList.getCadre() != null) cell.setCellValue(personnelList.getCadre());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(9);
      if (personnelList.getGrade() != null) cell.setCellValue(personnelList.getGrade());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(10);
      if (personnelList.getGresa() != null) cell.setCellValue(personnelList.getGresa());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(11);
      if (personnelList.getAllEtabMassar().getNomL() != null)
        cell.setCellValue(personnelList.getAllEtabMassar().getNomL());
      cell.setCellStyle(cellStyleBody);
    }
    //        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 3));
    //        sheet.addMergedRegion(new CellRangeAddress(0, 0, 4, 5));
    for (int col = 1; col < 12; col++) {
      sheet.autoSizeColumn(col);
    }

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }
}
