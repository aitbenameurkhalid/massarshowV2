package com.gestion.eleves.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.gestion.eleves.db.Article;
import com.hakibati.gestion.eleves.db.repo.ArticleRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Singleton
@Transactional
public class ArticleService {
  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;
  //    @Inject
  //    ArticleRepository articleRepository;

  public File getFileListInternatExcel() throws IOException {
    User user = AuthUtils.getUser();

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
    cell.setCellValue(I18n.get("N°"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(2);
    cell.setCellValue(I18n.get("Article"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(3);
    cell.setCellValue(I18n.get("Unite"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(4);
    cell.setCellValue(I18n.get("Prix Unite"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(5);
    cell.setCellValue(I18n.get("typeArticle"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(6);
    cell.setCellValue(I18n.get("etablissement"));
    cell.setCellStyle(cellStyle);

    List<Article> articleList =
        Beans.get(ArticleRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();

    for (Article article :
        articleList.stream()
            .sorted(Comparator.comparing(Article::getOrdre))
            .collect(Collectors.toList())) {
      rowSheet++;
      rowHead = sheet.createRow(rowSheet);
      cell = rowHead.createCell(1);
      cell.setCellValue(article.getOrdre());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(2);
      cell.setCellValue(article.getName());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(3);
      cell.setCellValue(article.getUnite());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(4);
      cell.setCellValue(article.getPrixUnite().toString());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(5);
      if (article.getTypeArticle() != null) cell.setCellValue(article.getTypeArticle().getName());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(6);
      cell.setCellValue(article.getEtablissement().getName());
      cell.setCellStyle(cellStyleBody);
    }

    for (int col = 1; col < 35; col++) {
      sheet.autoSizeColumn(col);
    }

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }

  public String importListArticleInternat(MetaFile metaFile) throws IOException {
    File excelFile = MetaFiles.getPath(metaFile).toFile();
    User user = AuthUtils.getUser();
    int countArticle = 0;

    List<Article> articleList =
        Beans.get(ArticleRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    try {
      if (!Files.getFileExtension(excelFile.getName()).equals("xlsx")) {
        MetaFiles.getPath(metaFile).toFile().deleteOnExit();
        return I18n.get("Choisissez un fichier au format xlsx");
      }
      if (!this.initializeXSSF(metaFile, ";")) return I18n.get("Erreur de lecture du fichier");
      String[] strCells;

      String sheetselectName = "List_ARTICLE_Internat";

      int m = this.getTotalLinesXSSF(sheetselectName);

      if (m == 0) return I18n.get("Erreur de lecture du fichier");
      for (int maxl = 1; maxl <= m - 1; maxl++) {
        strCells = this.readXSSF(sheetselectName, maxl, 35);
        if (StringUtils.isBlank(strCells[2])) {
          continue;
        }
        countArticle++;
        Article article;
        String strCells2 = strCells[2].toUpperCase();
        if (articleList.stream()
            .noneMatch(
                e ->
                    e.getName() != null
                        && (e.getName().equals(strCells2)
                            || e.getName().equals(strCells2.toLowerCase())))) {
          article = new Article();
          article.setName(strCells2);

        } else {
          article =
              articleList.stream()
                  .filter(
                      e ->
                          e.getName() != null
                              && (e.getName().equals(strCells2)
                                  || e.getName().equals(strCells2.toLowerCase())))
                  .collect(Collectors.toList())
                  .get(0);
        }

        article.setIsExerce(true);
        if (!StringUtils.isBlank(strCells[1])) article.setOrdre(Integer.parseInt(strCells[1]));
        if (!StringUtils.isBlank(strCells[3])) article.setUnite(strCells[3]);
        if (!StringUtils.isBlank(strCells[4])) {
          String strCells4 = strCells[4].replace(",", ".");
          BigDecimal prinUnite = BigDecimal.valueOf(Double.parseDouble(strCells4));
          article.setPrixUnite(prinUnite);
        }
        //
        // article.setPrixUnite(BigDecimal(BigDecimal.valueOf(Double.valueOf(strCells[4])));
        //                BigDecimal bigDecimal = new BigDecimal("123");
        //                article.setPrixUnite(bigDecimal);
        //                BigDecimal bigDecimal2 = BigDecimal.valueOf(Double.valueOf(strCells[4]));

        Beans.get(ArticleRepository.class).save(article);
      }
    } catch (Exception e) {
      excelFile.delete();
      return I18n.get("Une erreur s'est produite lors de l'importation du fichier Excel  ")
          + e.getMessage();
    }
    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
    excelFile.delete();
    return I18n.get("La liste des Article Internat a été importée avec succès")
        + ": "
        + countArticle;
  }

  public String MiseAjourListArticleInternat() {
    User user = AuthUtils.getUser();
    List<Article> articleList =
        Beans.get(ArticleRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();
    List<Article> articleListDemo =
        Beans.get(ArticleRepository.class)
            .all()
            .filter("etablissement.name = ?", "_DemoLycee_")
            .fetch();

    for (Article articleDemo : articleListDemo) {
      Article article;
      String strCells2 = articleDemo.getName();
      if (articleList.stream()
          .noneMatch(
              e ->
                  e.getName() != null
                      && (e.getName().equals(strCells2)
                          || e.getName().equals(strCells2.toLowerCase())))) {
        article = new Article();
        article.setName(strCells2);
      } else {
        article =
            articleList.stream()
                .filter(
                    e ->
                        e.getName() != null
                            && (e.getName().equals(strCells2)
                                || e.getName().equals(strCells2.toLowerCase())))
                .collect(Collectors.toList())
                .get(0);
      }

      article.setIsExerce(articleDemo.getIsExerce());
      article.setOrdre(articleDemo.getOrdre());
      article.setPrixUnite(articleDemo.getPrixUnite());
      Beans.get(ArticleRepository.class).save(article);
    }
    return I18n.get("Mise à jour des articles Internat terminée");
  }

  public boolean initializeXSSF(MetaFile input, String separator) {

    if (input == null) {
      return false;
    }
    File inFile = MetaFiles.getPath(input).toFile();
    if (!inFile.exists()) {
      return false;
    }

    try {
      FileInputStream inSteam = new FileInputStream(inFile);
      bookXSSF = new XSSFWorkbook(inSteam);
      if (bookXSSF.getNumberOfSheets() == 0) {
        return false;
      }
      formatter = new DataFormatter();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public String[] readXSSF(String sheetName, int index, int headerSize) {

    if (sheetName == null || bookXSSF == null) {
      return null;
    }

    XSSFSheet sheet = bookXSSF.getSheet(sheetName);
    if (sheet == null) {
      return null;
    }

    XSSFRow row = sheet.getRow(index);
    if (row == null) {
      return null;
    }

    if (headerSize == 0) {
      headerSize = row.getLastCellNum();
    }

    String[] vals = new String[headerSize];

    for (int i = 0; i < headerSize; i++) {
      Cell cell = row.getCell(i);
      if (cell == null) {
        continue;
      }
      vals[i] = formatter.formatCellValue(cell);
      if (Strings.isNullOrEmpty(vals[i])) {
        vals[i] = null;
      }
    }

    return vals;
  }

  public int getTotalLinesXSSF(String sheetName) {

    if (bookXSSF == null || sheetName == null || bookXSSF.getSheet(sheetName) == null) {
      return 0;
    }

    return bookXSSF.getSheet(sheetName).getPhysicalNumberOfRows();
  }

  public File getExcelFile() {
    return excelFile;
  }

  public void initialize() throws IOException {
    excelFile = File.createTempFile("List_ARTICLE_Internat", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("List_ARTICLE_Internat");
  }
}
