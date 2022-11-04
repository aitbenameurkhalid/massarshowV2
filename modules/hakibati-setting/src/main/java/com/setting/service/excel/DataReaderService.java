package com.setting.service.excel;

import com.axelor.meta.db.MetaFile;

public interface DataReaderService {

  /**
   * Initialize the input file.
   *
   * @param input
   * @return
   */
  public boolean initialize(MetaFile input, String separator);

  /**
   * Returns record/row of a particular line.
   *
   * @param key
   * @param index
   * @param headerSize
   * @return
   */
  public String[] read(String sheetName, int index, int headerSize);

  /**
   * Returns total number of lines.
   *
   * <p>// * @param key
   *
   * @return
   */
  public int getTotalLines(String sheetName);

  /**
   * Returns name of sheets.
   *
   * @return
   */
  public String[] getSheetNames();
}
