package com.setting.service.excel;

import com.axelor.i18n.I18n;
import java.io.*;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelLogWriter {

  private Workbook workbook;

  private Sheet sheet;

  private File excelFile;

  public void initialize(String sheetsName) throws IOException {
    excelFile = File.createTempFile("DATA", ".xlsx");
    //    String attachmentPath = AppSettings.get().getPath("file.upload.dir", "");
    //  URL url = getClass().getResource("C:/Users/Admin/.axelor/attachments/ListProfs.xlsx");
    //  excelFile =        new
    // File(this.getClass().getClassLoader().getResource("assets\\ListProfs.xlsx").getFile());

    //  excelFile = new File("assets/ListProfs.xlsx");
    //    excelFile =MetaFiles.getPath("assets/ListProfs.xlsx").toFile();

    // FileInputStream inSteam = new FileInputStream(excelFile);

    // InputStream inSteam =
    // getClass().getClassLoader().getResourceAsStream("assets\\ListProfs.xlsx");
    //  workbook = new XSSFWorkbook(inSteam);
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet(sheetsName);
    //    sheet = workbook.getSheet("ListNames");
  }

  public void initialize() throws IOException {
    excelFile = File.createTempFile("Data", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("data");
  }

  public void initialize(String fileName, String sheetsName) throws IOException {
    ClassLoader classLoader = this.getClass().getClassLoader();
    //  URL str = this.getClass().getClassLoader().getResource("assets/ListProfs.xlsx");
    excelFile = File.createTempFile("Data", ".xlsx");

    // excelFile = new File(classLoader.getResource(fileName).getFile());
    File file =
        new File(
            this.getClass().getClassLoader().getResource(fileName).getPath().replace("%20", " "));
    //   File file = new File(str.getFile());
    IOUtils.copy(new FileInputStream(file), new FileOutputStream(excelFile));
    FileInputStream inSteam = new FileInputStream(excelFile);
    workbook = new XSSFWorkbook(inSteam);
    sheet = workbook.getSheet(sheetsName);
  }

  public void writeHeader(String[] header) {
    Row headerRow = sheet.createRow(sheet.getFirstRowNum());
    for (int col = 0; col < header.length; col++) {
      Cell headerCell = headerRow.createCell(col);
      headerCell.setCellValue(header[col]);
    }
  }

  public void writeBody(int row, int col0, String[] body) {
    Row rowbody = sheet.getRow(row);
    try {
      for (int col = col0; col < body.length + col0; col++)
        if (body[col - col0] != null) {
          Cell cell = rowbody.getCell(col);
          cell.setCellValue(I18n.get(body[col - col0]));
        }
    } catch (Exception e) {

    }
  }

  public void createBody(int row, int col0, String[] body) {
    Row rowbody = sheet.createRow(row);
    for (int col = col0; col < body.length + col0; col++) {
      Cell cell = rowbody.createCell(col);
      if (body[col - col0] == null || body[col - col0].equals("")) continue;
      cell.setCellValue(body[col - col0]);
    }
  }

  public void createHead(int row, int col0, String[] body, Integer[] Duration) {
    Row rowbody = sheet.createRow(row);
    for (int col = col0; col < body.length + col0; col++) {
      Cell cell = rowbody.createCell(col);

      if (body[col - col0] == null) {
        cell.setCellStyle(
            setStyleHead(
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.NO_FILL));
        continue;
      }
      if (body[col - col0].equals("")) {
        cell.setCellStyle(
            setStyleHead(
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.NO_FILL));
        continue;
      }
      cell.setCellStyle(
          setStyleHead(
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.SOLID_FOREGROUND));
      cell.setCellValue(body[col - col0]);

      if (Duration[col] != null && Duration[col] > 1)
        sheet.addMergedRegion(new CellRangeAddress(row, row, col, col + Duration[col] - 1));
    }
  }

  public void createHead(int row, int col0, String[] body, Integer[] Duration, Color[] colorslist) {
    Row rowbody = sheet.createRow(row);
    for (int col = col0; col < body.length + col0; col++) {
      Cell cell = rowbody.createCell(col);

      if (body[col - col0] == null) {
        cell.setCellStyle(
            setStyleHead(
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.NO_FILL));
        continue;
      }
      if (body[col - col0].equals("")) {
        cell.setCellStyle(
            setStyleHead(
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.NO_FILL));
        continue;
      }
      cell.setCellStyle(
          setStyleHead(
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.SOLID_FOREGROUND));
      cell.setCellValue(body[col - col0]);

      if (Duration[col] != null && Duration[col] > 1)
        sheet.addMergedRegion(new CellRangeAddress(row, row, col, col + Duration[col] - 1));
    }
  }

  public void createBody(int row, int col0, String[] body, Integer[] Duration) {
    Row rowbody = sheet.createRow(row);
    for (int col = col0; col < body.length + col0; col++) {
      Cell cell = rowbody.createCell(col);

      if (body[col - col0] == null) {
        cell.setCellStyle(
            setStyle(
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.NO_FILL));
        continue;
      }
      if (body[col - col0].equals("")) {
        cell.setCellStyle(
            setStyle(
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.BORDER_THIN,
                CellStyle.ALT_BARS));
        continue;
      }
      cell.setCellStyle(
          setStyle(
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.BORDER_THIN,
              CellStyle.NO_FILL));
      cell.setCellValue(body[col - col0]);

      if (Duration[col] != null && Duration[col] > 1)
        sheet.addMergedRegion(new CellRangeAddress(row, row, col, col + Duration[col] - 1));
    }
  }

  public void AddMergedRegion(int firtRow, int lastRow, int firtCol, int lastCol) {
    sheet.addMergedRegion(new CellRangeAddress(firtRow, lastRow, firtCol, lastCol));
  }

  public void ColumnWidth(int colIndex, int width) {
    sheet.setColumnWidth(colIndex, width);
  }

  // creation  de Figer les volets.
  public void CreateFreezePane(int colSplit, int rowSplit) {
    sheet.createFreezePane(colSplit, rowSplit);
  }

  public void RightToLeft(boolean value) {
    sheet.setRightToLeft(value);
  }

  public void AutoSizeColumn(int col) {
    sheet.autoSizeColumn(col);
  }

  public void AutoSizeRow(int row) {
    //    sheet.;
  }

  private CellStyle setStyle() {
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    cellStyle.setFont(font);
    return cellStyle;
  }

  private CellStyle setStyle(
      short left_border, short right_border, short top_border, short bottom_border, short Pattern) {
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    //    cellStyle.setFont(font);
    cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setBorderBottom(bottom_border);
    cellStyle.setBorderRight(right_border);
    cellStyle.setBorderTop(top_border);
    cellStyle.setBorderLeft(left_border);
    cellStyle.setFillPattern(Pattern);
    cellStyle.setWrapText(true);

    return cellStyle;
  }

  private CellStyle setStyleHead(
      short left_border, short right_border, short top_border, short bottom_border, short Pattern) {
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    font.setBoldweight(Font.BOLDWEIGHT_BOLD);
    font.setColor(Font.COLOR_NORMAL);

    font.setItalic(true);
    cellStyle.setFont(font);
    cellStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setBorderBottom(bottom_border);
    cellStyle.setBorderRight(right_border);
    cellStyle.setBorderTop(top_border);
    cellStyle.setBorderLeft(left_border);
    //        cellStyle.setFillPattern(Pattern);
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyle.setFillPattern(Pattern);

    cellStyle.setWrapText(true);
    return cellStyle;
  }

  public void close() throws IOException {
    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
  }

  public File getExcelFile() {
    return excelFile;
  }

  public void cleanRange(int r1, int c1, int r2, int c2) {
    try {
      for (int row = r1; row <= r2; row++) {
        Row rowbody = sheet.getRow(row);
        for (int col = c1; row <= c2; col++) {
          Cell cell = rowbody.getCell(col);
          cell.setCellValue(I18n.get(""));
        }
      }
    } catch (Exception e) {
    }
  }
}
