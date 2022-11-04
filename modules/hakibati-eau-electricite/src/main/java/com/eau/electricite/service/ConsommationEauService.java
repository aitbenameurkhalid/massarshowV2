package com.eau.electricite.service;

import com.app.application.db.Etablissement;
import com.app.application.db.EtablissementLiaison;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.hakibati.eau.electricite.db.ConsommationEau;
import com.hakibati.eau.electricite.db.GestionEauElectricite;
import com.hakibati.eau.electricite.db.repo.ConsommationEauRepository;
import com.hakibati.eau.electricite.db.repo.GestionEauElectriciteRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ConsommationEauService {
  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;

  public File getFileListConsommationEau(String dateDu, String dateFin) throws IOException {
    User user = AuthUtils.getUser();
    DateTimeFormatter formatter0 = DateTimeFormatter.ofPattern("d/MM/yyyy");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

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
    cell.setCellValue(I18n.get("Contrat Eau"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(5);
    cell.setCellValue(I18n.get("date Du"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(6);
    cell.setCellValue(I18n.get("compteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(7);
    cell.setCellValue(I18n.get("Date fin"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(8);
    cell.setCellValue(I18n.get("compteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(9);
    cell.setCellValue(I18n.get("différence"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(10);
    cell.setCellValue(I18n.get("montant"));
    cell.setCellStyle(cellStyle);

    //    EtablissementRepository etablissementRepository =
    // Beans.get(EtablissementRepository.class);
    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    ConsommationEauRepository consommationEauRepository =
        Beans.get(ConsommationEauRepository.class);

    for (EtablissementLiaison etablissementLiaison :
        user.getEtablissementSelectionnee().getEtablissementsAnnexe().stream()
            .filter(EtablissementLiaison::getAccepter)
            .collect(Collectors.toList())) {
      Etablissement etablissement = etablissementLiaison.getEtablissement();
      rowSheet++;
      rowHead = sheet.createRow(rowSheet);
      cell = rowHead.createCell(0);
      cell.setCellValue(rowSheet);
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
        cell.setCellValue(gestionEauElectricite.getNumeroContratEau());
      cell.setCellStyle(cellStyleBody);
      ConsommationEau consommationEau =
          consommationEauRepository
              .all()
              .filter(
                  "etablissement = ? AND dateDu = ?",
                  etablissement,
                  LocalDate.parse(dateDu, formatter))
              .fetchOne();
      if (consommationEau != null) {
        cell = rowHead.createCell(5);
        cell.setCellValue(consommationEau.getDateDu().toString());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(6);
        cell.setCellValue(consommationEau.getCompteurDu());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(7);
        cell.setCellValue(consommationEau.getDateFin().toString());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(8);
        cell.setCellValue(consommationEau.getCompteurFin());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(9);
        cell.setCellValue(consommationEau.getQuantiteConsommation());
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(10);
        cell.setCellValue(consommationEau.getMontantConsommation());
        cell.setCellStyle(cellStyleBody);
      } else {
        cell = rowHead.createCell(5);
        if (dateDu != null) cell.setCellValue(dateDu.toString());
        cell.setCellStyle(cellStyleBody);
        //            cell = rowHead.createCell(7);
        //            if (dateFin != null)
        //                cell.setCellValue(String.valueOf(LocalDate.parse(dateFin, formatter)));
        //            cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(6);
        cell.setCellValue("compteur du");
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(7);
        if (dateFin != null) cell.setCellValue(dateFin);
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(8);
        cell.setCellValue("conpteur fin");
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(9);
        cell.setCellValue("");
        cell.setCellStyle(cellStyleBody);
        cell = rowHead.createCell(10);
        cell.setCellValue("");
        cell.setCellStyle(cellStyleBody);
      }
    }

    for (int col = 1; col < 11; col++) {
      sheet.autoSizeColumn(col);
    }

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }

  public void initialize() throws IOException {
    excelFile = File.createTempFile("Conso_elect", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("data");
  }
}
