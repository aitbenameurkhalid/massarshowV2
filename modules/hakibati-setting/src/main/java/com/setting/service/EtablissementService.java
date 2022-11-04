package com.setting.service;

import com.app.application.db.Etablissement;
import com.app.application.db.UserEtablissement;
import com.app.application.db.repo.EtablissementRepository;
import com.axelor.auth.db.Group;
import com.axelor.auth.db.User;
import com.axelor.auth.db.repo.GroupRepository;
import com.axelor.auth.db.repo.UserRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.EtablissementRolsDescribe;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Singleton
@Transactional
public class EtablissementService {
  @Transactional
  public static void OnCreateEtablissement(
      String NomDeEtablissement,
      String NomDecommun,
      String NomDedirection,
      String NomDeacademie,
      String Adresse,
      String Tel,
      User user,
      String TypeEtablissement) {

    Etablissement etablissement = new Etablissement();
    etablissement.setName(NomDeEtablissement);
    etablissement.setCommun(NomDecommun);
    etablissement.setDirection(NomDedirection);
    etablissement.setAcademie(NomDeacademie);
    etablissement.setAdresse(Adresse);
    etablissement.setTel(Tel);

    etablissement.setTypeEtablissemnt(Integer.parseInt(TypeEtablissement));
    //        List<UserEtablissement> userEtablissementList = etablissement.getUsers();
    List<UserEtablissement> userEtablissementList = new ArrayList<>();
    UserEtablissement userEtablissement = new UserEtablissement();
    userEtablissement.setEtablissements(etablissement);
    userEtablissement.setUsers(user);
    userEtablissement.setFonction("01");
    userEtablissementList.add(userEtablissement);
    etablissement.setUsers(userEtablissementList);

    if (!user.getGroup().getCode().equals("admins")) {
      if (etablissement.getTypeEtablissemnt() == 0) user.setGroup(getGroupe("etablissementAdmin"));
      if (etablissement.getTypeEtablissemnt() == 1) user.setGroup(getGroupe("directionAdmin"));
      if (etablissement.getTypeEtablissemnt() == 2) user.setGroup(getGroupe("academieAdmin"));

      if (etablissement.getTypeEtablissemnt() == 10) user.setGroup(getGroupe("centreLangueAdmin"));
    }
    Beans.get(EtablissementRepository.class).save(etablissement);
    user.setEtablissementSelectionnee(etablissement);

    Beans.get(UserRepository.class).save(user);
  }

  private static Group getGroupe(String s) {
    GroupRepository groupRepository = Beans.get(GroupRepository.class);
    return groupRepository.findByCode(s);
  }

  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;

  public File getFileListEtabExcel() throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    int rowSheet = 0;
    this.initialize();
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    Row rowHead = sheet.createRow(rowSheet);
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

    Cell cell = rowHead.createCell(1);
    cell.setCellValue(I18n.get("N° Etablissement"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(2);
    cell.setCellValue(I18n.get("Nom Etablissement"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(3);
    cell.setCellValue(I18n.get("Les Utilisateurs"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(4);
    cell.setCellValue(I18n.get("Les Role Describe"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(5);
    cell.setCellValue(I18n.get("Actif"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(6);
    cell.setCellValue(I18n.get("date de péremption"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(7);
    cell.setCellValue(I18n.get("Observation"));
    cell.setCellStyle(cellStyle);

    List<Etablissement> etablissementList = Beans.get(EtablissementRepository.class).all().fetch();

    for (Etablissement etablissement :
        etablissementList.stream()
            .sorted(Comparator.comparing(Etablissement::getId))
            .collect(Collectors.toList())) {
      rowSheet++;
      int RollCoun = etablissement.getEtablissementRolsDescribe().size();
      rowHead = sheet.createRow(rowSheet);
      cell = rowHead.createCell(1);
      cell.setCellValue(etablissement.getId());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(2);
      cell.setCellValue(etablissement.getName());
      cell.setCellStyle(cellStyleBody);
      cell = rowHead.createCell(3);
      //            cell.setCellValue(etablissement.getName());
      cell.setCellStyle(cellStyleBody);
      int rowIndex = 0;
      for (EtablissementRolsDescribe etablissementRolsDescribe :
          etablissement.getEtablissementRolsDescribe()) {
        rowIndex++;
        if (rowIndex > 1) {
          cell = rowHead.createCell(1);
          cell.setCellStyle(cellStyleBody);
          cell = rowHead.createCell(2);
          cell.setCellStyle(cellStyleBody);
          cell = rowHead.createCell(3);
          cell.setCellStyle(cellStyleBody);
        }
        cell = rowHead.createCell(4);
        cell.setCellValue(I18n.get(etablissementRolsDescribe.getRoleDescribes().getName()));
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(5);
        if (etablissementRolsDescribe.getActif() != null)
          cell.setCellValue(etablissementRolsDescribe.getActif().booleanValue());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(6);
        if (etablissementRolsDescribe.getDatePeremption() != null)
          cell.setCellValue(etablissementRolsDescribe.getDatePeremption().format(formatter));
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(7);
        if (etablissementRolsDescribe.getObservation() != null)
          cell.setCellValue(etablissementRolsDescribe.getObservation());
        cell.setCellStyle(cellStyleBody);
        rowSheet++;
        rowHead = sheet.createRow(rowSheet);
      }
      if (RollCoun == 0) {
        cell = rowHead.createCell(4);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(5);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(6);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(7);
        cell.setCellStyle(cellStyleBody);
      }

      if (RollCoun > 0) rowSheet--;
      if (RollCoun > 1) {
        sheet.addMergedRegion(new CellRangeAddress(rowSheet - RollCoun + 1, rowSheet, 1, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowSheet - RollCoun + 1, rowSheet, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(rowSheet - RollCoun + 1, rowSheet, 3, 3));
      }
    }

    for (int col = 1; col < 35; col++) {
      sheet.autoSizeColumn(col);
    }

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }

  public void initialize() throws IOException {
    excelFile = File.createTempFile("Statistiques_Etablissemnt", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("Statistiques_Etablissemnt");
  }
}
