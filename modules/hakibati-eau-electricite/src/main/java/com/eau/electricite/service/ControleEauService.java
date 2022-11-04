package com.eau.electricite.service;

import com.app.application.db.Etablissement;
import com.app.application.db.EtablissementLiaison;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.hakibati.eau.electricite.db.ControleEau;
import com.hakibati.eau.electricite.db.GestionEauElectricite;
import com.hakibati.eau.electricite.db.repo.ControleEauRepository;
import com.hakibati.eau.electricite.db.repo.GestionEauElectriciteRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ControleEauService {
  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;

  public File getFileListControlEau(String dateDu, String dateFin) throws IOException {
    User user = AuthUtils.getUser();
    DateTimeFormatter formatter0 = DateTimeFormatter.ofPattern("d/MM/yyyy");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

    int rowSheet = 2;
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
    Cell cell = rowHead.createCell(0);
    cell.setCellValue(I18n.get("N°"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(1);
    cell.setCellValue(I18n.get("Nom Etablissemnt"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(2);
    cell.setCellValue(I18n.get("Commune"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(3);
    cell.setCellValue(I18n.get("Gresa"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(4);
    cell.setCellValue(I18n.get("QUAL"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(5);
    cell.setCellValue(I18n.get("COL"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(6);
    cell.setCellValue(I18n.get("PRI"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(7);
    cell.setCellValue(I18n.get("annex"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(8);
    cell.setCellValue(I18n.get("INTERNAT"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(9);
    cell.setCellValue(I18n.get("nombre de structures bénéficiant de ce compteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(10);
    cell.setCellValue(I18n.get("N eleves"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(11);
    cell.setCellValue(I18n.get("N° Compteur Eau"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(12);
    cell.setCellValue(I18n.get("Date Du"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(13);
    cell.setCellValue(I18n.get("Compteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(14);
    cell.setCellValue(I18n.get("Date Au"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(15);
    cell.setCellValue(I18n.get("Compteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(16);
    cell.setCellValue(I18n.get("Consommation"));
    if (user.getEtablissementSelectionnee().getTypeEtablissemnt() == 0)
      cell.setCellValue(I18n.get("Consommation/Jour"));
    cell.setCellStyle(cellStyle);

    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    if (user.getEtablissementSelectionnee().getTypeEtablissemnt() == 1)
      for (EtablissementLiaison etablissementLiaison :
          user.getEtablissementSelectionnee().getEtablissementsAnnexe().stream()
              .filter(EtablissementLiaison::getAccepter)
              .collect(Collectors.toList())) {
        Etablissement etablissement = etablissementLiaison.getEtablissement();
        rowSheet++;
        rowHead = sheet.createRow(rowSheet);
        cell = rowHead.createCell(0);
        cell.setCellValue(rowSheet - 2);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(1);
        cell.setCellValue(etablissement.getName());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(2);
        cell.setCellValue(etablissement.getCommun());
        cell.setCellStyle(cellStyleBody);

        cell = rowHead.createCell(3);
        GestionEauElectricite gestionEauElectricite =
            gestionEauElectriciteRepository
                .all()
                .filter("etablissement = ?", etablissement)
                .fetchOne();
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getCodeGresa());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(4);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getIsqualifiant());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(5);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIscollege());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(6);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIsprimaire());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(7);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIsannexe());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(8);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIsinternat());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(9);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getNombrebatimentsEau());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(10);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getNombreEleves());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(11);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getNumeroContratEau());
        cell.setCellStyle(cellStyleBody);
        String queryDu =
            "SELECT (self.id) FROM ControleEau as self"
                + " WHERE self.etablissement.id ="
                + etablissement.getId()
                + "AND (self.dateControle) <='"
                + dateDu
                + "'"
                + " order by self.dateControle desc ";
        String queryAu =
            "SELECT (self.id) FROM ControleEau as self"
                + " WHERE self.etablissement.id ="
                + etablissement.getId()
                + "AND (self.dateControle) >='"
                + dateDu
                + "'"
                + " order by self.dateControle  ";
        Integer valeurCompteurDu =
            ValeurMoyService.getvaleurMoyCompteurEau(queryDu, queryAu, etablissement, dateDu);

        cell = rowHead.createCell(12);
        cell.setCellValue(dateDu);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(13);
        if (valeurCompteurDu != null) cell.setCellValue(valeurCompteurDu);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(14);
        cell.setCellValue(dateFin);
        cell.setCellStyle(cellStyleBody);
        queryDu =
            "SELECT (self.id) FROM ControleEau as self"
                + " WHERE self.etablissement.id ="
                + etablissement.getId()
                + "AND (self.dateControle) <='"
                + dateFin
                + "'"
                + " order by self.dateControle desc ";
        queryAu =
            "SELECT (self.id) FROM ControleEau as self"
                + " WHERE self.etablissement.id ="
                + etablissement.getId()
                + "AND (self.dateControle) >='"
                + dateFin
                + "'"
                + " order by self.dateControle  ";
        Integer valeurCompteurAu =
            ValeurMoyService.getvaleurMoyCompteurEau(queryDu, queryAu, etablissement, dateFin);

        cell = rowHead.createCell(15);
        if (valeurCompteurAu != null) cell.setCellValue(valeurCompteurAu);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(16);
        if (valeurCompteurDu != null && valeurCompteurAu != null)
          cell.setCellValue(valeurCompteurAu - valeurCompteurDu);
        cell.setCellStyle(cellStyleBody);
      }

    if (user.getEtablissementSelectionnee().getTypeEtablissemnt() == 0) {
      ControleEauRepository controleEauRepository = Beans.get(ControleEauRepository.class);
      LocalDate localDateTemp = null;
      Integer integerTemp = null;
      for (ControleEau controleEau :
          controleEauRepository.all()
              .filter(
                  "etablissement = ? AND dateControle>= ? AND dateControle<= ?",
                  user.getEtablissementSelectionnee(),
                  dateDu,
                  dateFin)
              .fetch().stream()
              .sorted(Comparator.comparing(ControleEau::getDateControle))
              .collect(Collectors.toList())) {
        rowSheet++;
        rowHead = sheet.createRow(rowSheet);
        cell = rowHead.createCell(0);
        cell.setCellValue(rowSheet - 2);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(1);
        cell.setCellValue(controleEau.getEtablissement().getName());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(2);
        cell.setCellValue(controleEau.getEtablissement().getCommun());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(3);
        GestionEauElectricite gestionEauElectricite =
            gestionEauElectriciteRepository
                .all()
                .filter("etablissement = ?", controleEau.getEtablissement())
                .fetchOne();
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getCodeGresa());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(4);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getIsqualifiant());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(5);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIscollege());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(6);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIsprimaire());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(7);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIsannexe());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(8);
        if (gestionEauElectricite != null) cell.setCellValue(gestionEauElectricite.getIsinternat());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(9);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getNombrebatimentsEau());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(10);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getNombreEleves());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(11);
        if (gestionEauElectricite != null)
          cell.setCellValue(gestionEauElectricite.getNumeroContratEau());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(12);
        if (localDateTemp != null) cell.setCellValue(localDateTemp.toString());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(13);
        if (integerTemp != null) cell.setCellValue(integerTemp);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(14);
        cell.setCellValue(controleEau.getDateControle().toString());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(15);
        cell.setCellValue(controleEau.getValeurCompteur());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(16);
        if (localDateTemp != null
            && integerTemp != null
            && controleEau.getDateControle() != localDateTemp) {
          float d = ChronoUnit.DAYS.between(localDateTemp, controleEau.getDateControle());
          Integer c = null;
          c = controleEau.getValeurCompteur() - integerTemp;
          cell.setCellValue(c / d);
        }
        cell.setCellStyle(cellStyleBody);

        localDateTemp = controleEau.getDateControle();
        integerTemp = controleEau.getValeurCompteur();
      }
    }

    for (int col = 1; col < 16; col++) {
      sheet.autoSizeColumn(col);
    }

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }

  public void initialize() throws IOException {
    excelFile = File.createTempFile("ControleEau", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("ControleEau");
  }
}
