package com.setting.service.excel;

public class DataReaderFactory {

  public DataReaderService getDataReader(String type) {

    DataReaderService reader = null;

    switch (type) {
      case "xls":
      case "xlsx":
        reader = new ExcelReaderService();
        break;
      case "csv":
        reader = new CSVReaderService();
        break;
      default:
        break;
    }
    return reader;
  }
}
