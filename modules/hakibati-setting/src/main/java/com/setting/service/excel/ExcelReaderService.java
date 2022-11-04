package com.setting.service.excel;

import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.base.Strings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReaderService implements DataReaderService {

  private XSSFWorkbook book = null;
  private DataFormatter formatter = null;

  @Override
  public boolean initialize(MetaFile input, String separator) {

    if (input == null) {
      return false;
    }

    File inFile = MetaFiles.getPath(input).toFile();
    if (!inFile.exists()) {
      return false;
    }

    try {
      FileInputStream inSteam = new FileInputStream(inFile);
      book = new XSSFWorkbook(inSteam);
      if (book.getNumberOfSheets() == 0) {
        return false;
      }
      formatter = new DataFormatter();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  @Override
  public String[] read(String sheetName, int index, int headerSize) {

    if (sheetName == null || book == null) {
      return null;
    }

    XSSFSheet sheet = book.getSheet(sheetName);
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

  @Override
  public String[] getSheetNames() {

    if (book == null) {
      return null;
    }

    String[] sheets = new String[book.getNumberOfSheets()];

    for (int count = 0; count < sheets.length; count++) {
      sheets[count] = book.getSheetName(count);
    }

    return sheets;
  }

  @Override
  public int getTotalLines(String sheetName) {

    if (book == null || sheetName == null || book.getSheet(sheetName) == null) {
      return 0;
    }

    return book.getSheet(sheetName).getPhysicalNumberOfRows();
  }
}
