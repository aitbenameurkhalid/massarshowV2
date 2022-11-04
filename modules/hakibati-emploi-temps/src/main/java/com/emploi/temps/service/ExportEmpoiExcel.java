package com.emploi.temps.service;

import com.axelor.i18n.I18n;
import com.emploi.temps.web.IReport;
import com.hakibati.emploi.db.*;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportEmpoiExcel {
  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;

  List<ActivityResulteFet> activityResulteFetList;
  List<DayFet> dayFetList;
  List<HourFet> hourFetList;
  List<DayHourFet> dayHourFetList;
  List<StudentDossier> studentDossierList;
  List<TeacherDossier> teacherDossierList;
  List<RoomDossier> roomDossierList;

  public void initialize() throws IOException {
    excelFile = File.createTempFile("Data", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("data");
  }

  public File getExcelFile() {
    return excelFile;
  }

  public File exportTableCollectigExcel(DossierImportFet dossierImportFet, int typeTable)
      throws IOException {
    dayFetList = dossierImportFet.getDaysFet();
    hourFetList = dossierImportFet.getHoursFet();
    activityResulteFetList = dossierImportFet.getActivityResultesFet();
    dayHourFetList = dossierImportFet.getResultatFet().getDayHoursFet();
    studentDossierList =
        dossierImportFet.getStudentDossiers().stream()
            .filter(
                e ->
                    !e.getNameFet().equals("*")
                        && e.getCdGroupe() != -1
                        && e.getCdSubgroupe() == -1)
            .collect(Collectors.toList());

    teacherDossierList =
        dossierImportFet.getTeacherDossiers().stream()
            .filter(e -> !e.getNameFet().equals("*"))
            .collect(Collectors.toList());

    roomDossierList =
        dossierImportFet.getRoomDossiers().stream()
            .filter(e -> !e.getNameFet().equals("*"))
            .collect(Collectors.toList());
    hourFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    dayFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    studentDossierList.sort(Comparator.comparing(StudentDossier::getCd));
    teacherDossierList.sort(Comparator.comparing(TeacherDossier::getSubjects));
    roomDossierList.sort(Comparator.comparing(o -> o.getRoomFet().getCd()));
    String dir = I18n.get("ltr");

    String[][] result = new String[hourFetList.size()][3];
    Color[] colorList = new Color[hourFetList.size()];
    Integer[] durationList = new Integer[hourFetList.size()];
    int Duration = 1;
    int rowSheet = 0;
    this.initialize();

    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    //    HSSFCellStyle hssfCellStyle = (HSSFCellStyle) workbook.createCellStyle();
    rowSheet = 0;
    Row rowHead = sheet.createRow(rowSheet);
    Cell cell = rowHead.createCell(2 * (hourFetList.size() + 1) + 2);
    if (typeTable == IReport.TEACHER_TABLE_TYPE)
      cell.setCellValue(I18n.get(IReport.emploi_du_temps_toutes_professeur));
    if (typeTable == IReport.STUDENT_TABLE_TYPE)
      cell.setCellValue(I18n.get(IReport.emploi_du_temps_toutes_classes));
    if (typeTable == IReport.ROOM_TABLE_TYPE)
      cell.setCellValue(I18n.get(IReport.emploi_du_temps_toutes_salles));

    font.setFontHeightInPoints((short) 20);
    cellStyle.setFont(font);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
    cell.setCellStyle(cellStyle);
    sheet.addMergedRegion(
        new CellRangeAddress(
            rowSheet,
            rowSheet + 1,
            2 * (hourFetList.size() + 1) + 2,
            (dayFetList.size() - 2) * (hourFetList.size() + 1)));

    cell = rowHead.createCell(2);
    cell.setCellValue(dossierImportFet.getEtablissement().getAcademie().toString());
    sheet.addMergedRegion(
        new CellRangeAddress(rowSheet, rowSheet, 2, 2 * (hourFetList.size() + 1)));
    font = workbook.createFont();
    font.setFontHeightInPoints((short) 14);
    cellStyle.setFont(font);
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell((dayFetList.size() - 2) * (hourFetList.size() + 1) + 2);
    cell.setCellValue(dossierImportFet.getEtablissement().getCommun().toString());
    sheet.addMergedRegion(
        new CellRangeAddress(
            rowSheet,
            rowSheet,
            (dayFetList.size() - 2) * (hourFetList.size() + 1) + 2,
            dayFetList.size() * (hourFetList.size() + 1)));
    cell.setCellStyle(cellStyle);

    rowSheet++;
    rowHead = sheet.createRow(rowSheet);
    cell = rowHead.createCell(2);
    cell.setCellValue(dossierImportFet.getEtablissement().getDirection().toString());
    sheet.addMergedRegion(
        new CellRangeAddress(rowSheet, rowSheet, 2, 2 * (hourFetList.size() + 1)));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell((dayFetList.size() - 2) * (hourFetList.size() + 1) + 2);
    cell.setCellValue(dossierImportFet.getEtablissement().getName().toString());
    sheet.addMergedRegion(
        new CellRangeAddress(
            rowSheet,
            rowSheet,
            (dayFetList.size() - 2) * (hourFetList.size() + 1) + 2,
            dayFetList.size() * (hourFetList.size() + 1)));
    cell.setCellStyle(cellStyle);
    // creation day real  name
    rowSheet++;
    rowHead = sheet.createRow(rowSheet);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cell = rowHead.createCell(0);
    if (typeTable == IReport.TEACHER_TABLE_TYPE) cell.setCellValue(I18n.get("professeurs"));
    if (typeTable == IReport.STUDENT_TABLE_TYPE) cell.setCellValue(I18n.get("classes"));
    if (typeTable == IReport.ROOM_TABLE_TYPE) cell.setCellValue(I18n.get("salles"));
    cell.setCellStyle(cellStyle);

    sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 2, 0, 0));
    cell = rowHead.createCell(1);
    cell.setCellValue(I18n.get("H"));
    cell.setCellStyle(cellStyle);
    sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 2, 1, 1));
    cell = rowHead.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
    cell.setCellValue(I18n.get("H"));
    cell.setCellStyle(cellStyle);
    sheet.addMergedRegion(
        new CellRangeAddress(
            rowSheet,
            rowSheet + 2,
            dayFetList.size() * (hourFetList.size() + 1) + 1,
            dayFetList.size() * (hourFetList.size() + 1) + 1));
    cell = rowHead.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
    if (typeTable == IReport.TEACHER_TABLE_TYPE) cell.setCellValue(I18n.get("professeurs"));
    if (typeTable == IReport.STUDENT_TABLE_TYPE) cell.setCellValue(I18n.get("classes"));
    if (typeTable == IReport.ROOM_TABLE_TYPE) cell.setCellValue(I18n.get("salles"));
    cell.setCellStyle(cellStyle);
    sheet.addMergedRegion(
        new CellRangeAddress(
            rowSheet,
            rowSheet + 2,
            dayFetList.size() * (hourFetList.size() + 1) + 2,
            dayFetList.size() * (hourFetList.size() + 1) + 2));

    cellStyle = workbook.createCellStyle();
    font = workbook.createFont();
    font.setFontHeightInPoints((short) 16);
    cellStyle.setFont(font);
    //        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    //        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    //        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    //        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    for (int d = 0; d < dayFetList.size(); d = d + 2) {
      cell = rowHead.createCell(d * (hourFetList.size() + 1) + 2);
      cell.setCellValue(getNameRealDay(dayFetList.get(d).getNameFet()));
      cell.setCellStyle(cellStyle);
      sheet.addMergedRegion(
          new CellRangeAddress(
              rowSheet,
              rowSheet,
              d * (hourFetList.size() + 1) + 2,
              d * (hourFetList.size() + 1) + 2 + hourFetList.size() * 2));
    }

    //   creation semi day real name
    rowSheet++;
    rowHead = sheet.createRow(rowSheet);
    cellStyle = workbook.createCellStyle();
    font = workbook.createFont();
    font.setFontHeightInPoints((short) 14);
    cellStyle.setFont(font);
    //        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    //        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    //        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    //        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    for (int d = 0; d < dayFetList.size(); d = d + 2) {
      cell = rowHead.createCell(d * (hourFetList.size() + 1) + 2);
      cell.setCellValue(I18n.get("Matin"));
      sheet.addMergedRegion(
          new CellRangeAddress(
              rowSheet,
              rowSheet,
              d * (hourFetList.size() + 1) + 2,
              d * (hourFetList.size() + 1) + 1 + hourFetList.size()));
      cell.setCellStyle(cellStyle);
      cell = rowHead.createCell((d + 1) * (hourFetList.size() + 1) + 2);
      cell.setCellValue(I18n.get("Apres-midi"));
      sheet.addMergedRegion(
          new CellRangeAddress(
              rowSheet,
              rowSheet,
              (d + 1) * (hourFetList.size() + 1) + 2,
              (d + 1) * (hourFetList.size() + 1) + 1 + hourFetList.size()));
      cell.setCellStyle(cellStyle);
    }
    //        creation des seance
    rowSheet++;
    rowHead = sheet.createRow(rowSheet);
    cellStyle = workbook.createCellStyle();
    font = workbook.createFont();
    font.setFontHeightInPoints((short) 10);
    cellStyle.setFont(font);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

    for (int d = 0; d < dayFetList.size(); d++) {
      for (int h = 0; h < hourFetList.size(); h++) {
        cell = rowHead.createCell(h + (d * (hourFetList.size() + 1)) + 2);
        cell.setCellValue(
            I18n.get(getHourReal(hourFetList.get(h).getNameFet(), dayFetList.get(d).getNameFet())));
        cell.setCellStyle(cellStyle);
      }
    }

    // creation de corps table salles
    CellStyle cellStyleTop = workbook.createCellStyle();
    CellStyle cellStyleEmpty = workbook.createCellStyle();
    CellStyle cellStyleBottom = workbook.createCellStyle();
    CellStyle cellStyleName = workbook.createCellStyle();

    Font fontName = workbook.createFont();
    Font fontEmpty = workbook.createFont();
    Font fontTop = workbook.createFont();
    Font fontBottom = workbook.createFont();

    fontName.setFontHeightInPoints((short) 13);
    cellStyleName.setFont(fontName);
    cellStyleName.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyleName.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyleName.setBorderRight(CellStyle.BORDER_THIN);
    cellStyleName.setBorderTop(CellStyle.BORDER_THIN);
    cellStyleName.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleName.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyleName.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyleName.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyleName.setWrapText(true);
    fontEmpty.setFontHeightInPoints((short) 10);
    cellStyleEmpty.setFont(fontEmpty);
    cellStyleEmpty.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyleEmpty.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyleEmpty.setBorderRight(CellStyle.BORDER_THIN);
    cellStyleEmpty.setBorderTop(CellStyle.BORDER_THIN);
    cellStyleEmpty.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleEmpty.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    //        cellStyleEmpty.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyleEmpty.setFillPattern(CellStyle.ALT_BARS);

    fontTop.setFontHeightInPoints((short) 10);
    cellStyleTop.setFont(fontTop);
    cellStyleTop.setBorderBottom(CellStyle.BORDER_HAIR);
    cellStyleTop.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyleTop.setBorderRight(CellStyle.BORDER_THIN);
    cellStyleTop.setBorderTop(CellStyle.BORDER_THIN);
    cellStyleTop.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleTop.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyleTop.setWrapText(true);
    //        cellStyleTop.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    //        cellStyleTop.setFillPattern(CellStyle.SOLID_FOREGROUND);

    fontBottom.setFontHeightInPoints((short) 10);
    cellStyleBottom.setFont(fontBottom);
    cellStyleBottom.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyleBottom.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyleBottom.setBorderRight(CellStyle.BORDER_THIN);
    cellStyleBottom.setBorderTop(CellStyle.BORDER_HAIR);
    cellStyleBottom.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleBottom.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyleBottom.setWrapText(true);
    //        cellStyleBottom.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    //        cellStyleBottom.setFillPattern(CellStyle.SOLID_FOREGROUND);

    Row row1, row2;
    rowSheet--;
    if (typeTable == IReport.ROOM_TABLE_TYPE)
      for (RoomDossier roomDossier : roomDossierList) {
        rowSheet++;
        rowSheet++;
        row1 = sheet.createRow(rowSheet);
        row2 = sheet.createRow(rowSheet + 1);
        String teacherStudentRoomName = roomDossier.getNameFet();

        cell = row1.createCell(0);
        cell.setCellValue(roomDossier.getRoomFet().getName().replace("_", " "));
        cell.setCellStyle(cellStyleName);
        cell = row2.createCell(0);
        cell.setCellStyle(cellStyleName);

        cell = row1.createCell(1);
        cell.setCellValue(getCountHour(teacherStudentRoomName, typeTable));
        cell.setCellStyle(cellStyleTop);
        cell = row2.createCell(1);
        cell.setCellStyle(cellStyleTop);

        sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 1, 0, 0));
        sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 1, 1, 1));

        cell = row1.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
        cell.setCellValue(roomDossier.getRoomFet().getName().replace("_", " "));
        cell.setCellStyle(cellStyleName);
        cell = row2.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
        cell.setCellStyle(cellStyleName);
        cell = row1.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
        cell.setCellValue(getCountHour(teacherStudentRoomName, typeTable));
        cell.setCellStyle(cellStyleTop);
        cell = row2.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
        cell.setCellStyle(cellStyleTop);
        sheet.addMergedRegion(
            new CellRangeAddress(
                rowSheet,
                rowSheet + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 2,
                dayFetList.size() * (hourFetList.size() + 1) + 2));
        sheet.addMergedRegion(
            new CellRangeAddress(
                rowSheet,
                rowSheet + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 1));

        durationList[0] = 1;
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            String[] meetingName =
                getMeetingName(
                    d, h, teacherStudentRoomName, typeTable, false, true, true, false, true, true);
            Duration = Integer.parseInt(meetingName[0]);
            String str1 =
                meetingName[
                    1]; // getMeetingName(d, h, teacherStudentRoomName, typeTable, false, false,
            // true, false, false);
            String str2 =
                meetingName[
                    2]; // getMeetingName(d, h, teacherStudentRoomName, typeTable, false, false,
            // false, false, true);
            cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2);
            cell.setCellValue(str1);
            cell.setCellStyle(cellStyleTop);
            if (str1.equals("")) cell.setCellStyle(cellStyleEmpty);
            if (Duration > 1)
              sheet.addMergedRegion(
                  new CellRangeAddress(
                      rowSheet,
                      rowSheet,
                      h + (d * (hourFetList.size() + 1)) + 2,
                      h + (d * (hourFetList.size() + 1)) + 1 + Duration));

            cell = row2.createCell(h + (d * (hourFetList.size() + 1)) + 2);
            cell.setCellValue(str2);
            cell.setCellStyle(cellStyleBottom);
            if (str2.equals("")) cell.setCellStyle(cellStyleEmpty);
            if (Duration > 1)
              sheet.addMergedRegion(
                  new CellRangeAddress(
                      rowSheet + 1,
                      rowSheet + 1,
                      h + (d * (hourFetList.size() + 1)) + 2,
                      h + (d * (hourFetList.size() + 1)) + 1 + Duration));
            for (int i = 1; i < Duration; i++) {
              cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2 + i);
              cell.setCellStyle(cellStyleTop);
              cell = row2.createCell(h + (d * (hourFetList.size() + 1)) + 2 + i);
              cell.setCellStyle(cellStyleBottom);
            }
            h += Duration - 1;
          }
        }
      }
    if (typeTable == IReport.TEACHER_TABLE_TYPE)
      for (TeacherDossier teacherDossier : teacherDossierList) {
        rowSheet++;
        rowSheet++;
        row1 = sheet.createRow(rowSheet);
        row2 = sheet.createRow(rowSheet + 1);
        String teacherStudentRoomName = teacherDossier.getNameFet();

        cell = row1.createCell(0);
        cell.setCellValue(teacherDossier.getTeacherFet().getName().replace("_", " "));
        cell.setCellStyle(cellStyleName);
        cell = row2.createCell(0);
        cell.setCellStyle(cellStyleName);

        sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 1, 0, 0));
        cell = row1.createCell(1);
        cell.setCellValue(getCountHour(teacherStudentRoomName, typeTable));
        cell.setCellStyle(cellStyleTop);
        cell = row2.createCell(1);
        cell.setCellStyle(cellStyleTop);
        sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 1, 1, 1));

        cell = row1.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
        cell.setCellValue(teacherDossier.getTeacherFet().getName().replace("_", " "));
        cell.setCellStyle(cellStyleName);
        cell = row2.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
        cell.setCellStyle(cellStyleName);
        cell = row1.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
        cell.setCellValue(getCountHour(teacherStudentRoomName, typeTable));
        cell.setCellStyle(cellStyleTop);
        cell = row2.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
        cell.setCellStyle(cellStyleTop);
        sheet.addMergedRegion(
            new CellRangeAddress(
                rowSheet,
                rowSheet + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 2,
                dayFetList.size() * (hourFetList.size() + 1) + 2));
        sheet.addMergedRegion(
            new CellRangeAddress(
                rowSheet,
                rowSheet + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 1));

        durationList[0] = 1;
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            String[] meetingName =
                getMeetingName(
                    d, h, teacherStudentRoomName, typeTable, true, true, true, true, true, true);
            Duration = Integer.parseInt(meetingName[0]);
            String str1 = meetingName[1];
            String str2 = meetingName[2];
            cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2);
            cell.setCellValue(str1);
            cell.setCellStyle(cellStyleTop);
            if (str1.equals("")) cell.setCellStyle(cellStyleEmpty);
            if (Duration > 1)
              sheet.addMergedRegion(
                  new CellRangeAddress(
                      rowSheet,
                      rowSheet,
                      h + (d * (hourFetList.size() + 1)) + 2,
                      h + (d * (hourFetList.size() + 1)) + 1 + Duration));

            cell = row2.createCell(h + (d * (hourFetList.size() + 1)) + 2);
            cell.setCellValue(str2);
            cell.setCellStyle(cellStyleBottom);
            if (str2.equals("")) cell.setCellStyle(cellStyleEmpty);
            if (Duration > 1)
              sheet.addMergedRegion(
                  new CellRangeAddress(
                      rowSheet + 1,
                      rowSheet + 1,
                      h + (d * (hourFetList.size() + 1)) + 2,
                      h + (d * (hourFetList.size() + 1)) + 1 + Duration));
            for (int i = 1; i < Duration; i++) {
              cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2 + i);
              cell.setCellStyle(cellStyleTop);
              cell = row2.createCell(h + (d * (hourFetList.size() + 1)) + 2 + i);
              cell.setCellStyle(cellStyleBottom);
            }
            h += Duration - 1;
          }
        }
      }
    if (typeTable == IReport.STUDENT_TABLE_TYPE)
      for (StudentDossier studentDossier : studentDossierList) {
        rowSheet++;
        rowSheet++;
        row1 = sheet.createRow(rowSheet);
        row2 = sheet.createRow(rowSheet + 1);
        String teacherStudentRoomName = studentDossier.getNameFet();
        cell = row1.createCell(0);
        cell.setCellValue(studentDossier.getStudentFet().getName().replace("_", " "));
        cell.setCellStyle(cellStyleName);
        cell = row2.createCell(0);
        cell.setCellStyle(cellStyleName);

        sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 1, 0, 0));
        cell = row1.createCell(1);
        cell.setCellValue(getCountHour(teacherStudentRoomName, typeTable));
        cell.setCellStyle(cellStyleTop);
        cell = row2.createCell(1);
        cell.setCellStyle(cellStyleTop);
        sheet.addMergedRegion(new CellRangeAddress(rowSheet, rowSheet + 1, 1, 1));

        cell = row1.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
        cell.setCellValue(studentDossier.getStudentFet().getName().replace("_", " "));
        cell.setCellStyle(cellStyleName);
        cell = row2.createCell(dayFetList.size() * (hourFetList.size() + 1) + 2);
        cell.setCellStyle(cellStyleName);
        cell = row1.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
        cell.setCellValue(getCountHour(teacherStudentRoomName, typeTable));
        cell.setCellStyle(cellStyleTop);
        cell = row2.createCell(dayFetList.size() * (hourFetList.size() + 1) + 1);
        cell.setCellStyle(cellStyleTop);
        sheet.addMergedRegion(
            new CellRangeAddress(
                rowSheet,
                rowSheet + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 2,
                dayFetList.size() * (hourFetList.size() + 1) + 2));
        sheet.addMergedRegion(
            new CellRangeAddress(
                rowSheet,
                rowSheet + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 1,
                dayFetList.size() * (hourFetList.size() + 1) + 1));

        durationList[0] = 1;
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            String[] meetingName =
                getMeetingName(
                    d, h, teacherStudentRoomName, typeTable, true, true, true, true, true, true);
            Duration = Integer.parseInt(meetingName[0]);
            String str1 = meetingName[1];
            String str2 = meetingName[2];
            cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2);
            cell.setCellValue(str1);
            cell.setCellStyle(cellStyleTop);
            if (str1.equals("")) cell.setCellStyle(cellStyleEmpty);
            if (Duration > 1)
              sheet.addMergedRegion(
                  new CellRangeAddress(
                      rowSheet,
                      rowSheet,
                      h + (d * (hourFetList.size() + 1)) + 2,
                      h + (d * (hourFetList.size() + 1)) + 1 + Duration));

            cell = row2.createCell(h + (d * (hourFetList.size() + 1)) + 2);
            cell.setCellValue(str2);
            cell.setCellStyle(cellStyleBottom);
            if (str2.equals("")) cell.setCellStyle(cellStyleEmpty);
            if (Duration > 1)
              sheet.addMergedRegion(
                  new CellRangeAddress(
                      rowSheet + 1,
                      rowSheet + 1,
                      h + (d * (hourFetList.size() + 1)) + 2,
                      h + (d * (hourFetList.size() + 1)) + 1 + Duration));
            for (int i = 1; i < Duration; i++) {
              cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2 + i);
              cell.setCellStyle(cellStyleTop);
              cell = row2.createCell(h + (d * (hourFetList.size() + 1)) + 2 + i);
              cell.setCellStyle(cellStyleBottom);
            }
            h += Duration - 1;
          }
        }
      }
    rowSheet++;
    rowSheet++;
    row1 = sheet.createRow(rowSheet);
    cell = row1.createCell(0);
    cell.setCellValue(I18n.get(IReport.nombre_global));
    cell.setCellStyle(cellStyleName);
    cell = row1.createCell(1);
    cell.setCellValue(getCountHour("", 0));
    cell.setCellStyle(cellStyleTop);

    for (int d = 0; d < dayFetList.size(); d = d + 1) {
      for (int h = 0; h < hourFetList.size(); h++) {
        cell = row1.createCell(h + (d * (hourFetList.size() + 1)) + 2);
        cell.setCellValue(getCountMeeting(d, h, typeTable));
        cell.setCellStyle(cellStyleTop);
      }
    }

    //        sheet.autoSizeColumn(0);
    sheet.setColumnWidth(0, 6000);
    sheet.setColumnWidth(1, 1000);
    sheet.setColumnWidth(dayFetList.size() * (hourFetList.size() + 1) + 1, 1000);
    sheet.setColumnWidth(dayFetList.size() * (hourFetList.size() + 1) + 2, 6000);

    for (int d = 0; d < dayFetList.size(); d++) {
      for (int h = 0; h < hourFetList.size(); h++) {
        int col = h + (d * (hourFetList.size() + 1)) + 2;
        //        logWriter.AutoSizeColumn(col);
      }
    }
    // merge  col separation
    for (int d = 1; d < dayFetList.size(); d = d + 1) {
      Duration = hourFetList.size() + 1;
      sheet.setColumnWidth((d * Duration) + 1, 100);
      //       sheet.setColumnWidth(((d + 1) * Duration) + 1, 150);
      //      sheet.addMergedRegion(
      //          new CellRangeAddress(2, rowSheet, (d * Duration) + 1, (d * Duration) + 1));

      //      sheet.addMergedRegion(
      //          new CellRangeAddress(3, rowSheet, ((d + 1) * Duration) + 1, ((d + 1) * Duration) +
      // 1));
    }

    sheet.setRightToLeft(dir.equals("rtl"));
    sheet.createFreezePane(2, 5);

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }

  private String getNameRealDay(String nameFet) {
    if (dayHourFetList.stream().noneMatch(e -> e.getDayFet().contains(nameFet))) return nameFet;
    return I18n.get(
        dayHourFetList.stream()
            .filter(e -> e.getDayFet().contains(nameFet))
            .collect(Collectors.toList())
            .get(0)
            .getRealDay());
  }

  private String getHourReal(String nameHourFet, String nameDayFet) {
    if (dayHourFetList.stream()
        .noneMatch(e -> e.getHourFet().contains(nameHourFet) && e.getDayFet().contains(nameDayFet)))
      return nameHourFet;
    return dayHourFetList.stream()
        .filter(e -> e.getHourFet().contains(nameHourFet) && e.getDayFet().contains(nameDayFet))
        .collect(Collectors.toList())
        .get(0)
        .getRealhour();
  }

  private int getCountHour(String teacherStudentRoomName, int typeTable) {
    int countHour = 0;

    List<ActivityResulteFet> activityResulteFetRech = activityResulteFetList;
    if (typeTable == IReport.ROOM_TABLE_TYPE) {
      activityResulteFetRech =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getRoomDossier() != null
                          && e.getRoomDossier().getNameFet().equals(teacherStudentRoomName))
              .collect(Collectors.toList());
    }
    if (typeTable == IReport.STUDENT_TABLE_TYPE) {
      activityResulteFetRech =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getStudentDossier() != null
                          && (e.getStudentDossier().getNameFet().equals(teacherStudentRoomName)
                              || (e.getStudentDossier2() != null
                                  && e.getStudentDossier2()
                                      .getNameFet()
                                      .equals(teacherStudentRoomName))))
              .collect(Collectors.toList());
    }
    if (typeTable == IReport.TEACHER_TABLE_TYPE) {
      activityResulteFetRech =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getTeacherFet() != null
                          && (e.getTeacherFet().getNameFet().equals(teacherStudentRoomName)
                              || (e.getTeacherFet2() != null
                                  && e.getTeacherFet2()
                                      .getNameFet()
                                      .equals(teacherStudentRoomName))))
              .collect(Collectors.toList());
    }

    assert activityResulteFetRech != null;
    for (ActivityResulteFet activityResulteFet : activityResulteFetRech) {
      countHour += activityResulteFet.getDuration();
    }
    return countHour;
  }

  private String[] getMeetingName(
      int d,
      int h,
      String teacherStudentRoomName,
      int typeTable,
      boolean showRoom,
      boolean showSubject,
      boolean showStudent,
      boolean showColor,
      boolean ShortSubject,
      boolean showTeacher) {
    String[] strings = new String[3];
    strings[0] = "1";
    strings[1] = "";
    strings[2] = "";
    if (teacherStudentRoomName.isEmpty()) return strings;
    try {
      List<ActivityResulteFet> activityResulteFetListRech = new ArrayList<>();
      if (typeTable == IReport.TEACHER_TABLE_TYPE) {
        activityResulteFetListRech =
            activityResulteFetList.stream()
                .filter(
                    e ->
                        e.getTeacherFet() != null
                            && e.getDayFet() == dayFetList.get(d)
                            && e.getHourFet() == hourFetList.get(h)
                            && e.getTeacherFet().getNameFet().equals(teacherStudentRoomName))
                .collect(Collectors.toList());
        if (activityResulteFetListRech.size() == 0)
          activityResulteFetListRech =
              activityResulteFetList.stream()
                  .filter(
                      e ->
                          e.getTeacherFet2() != null
                              && e.getDayFet() == dayFetList.get(d)
                              && e.getHourFet() == hourFetList.get(h)
                              && e.getTeacherFet2().getNameFet().equals(teacherStudentRoomName))
                  .collect(Collectors.toList());
      }
      if (typeTable == IReport.STUDENT_TABLE_TYPE) {
        Integer cd =
            studentDossierList.stream()
                .filter(e -> e.getNameFet().equals(teacherStudentRoomName))
                .collect(Collectors.toList())
                .get(0)
                .getCd();
        Integer cdParent =
            studentDossierList.stream()
                .filter(e -> e.getNameFet().equals(teacherStudentRoomName))
                .collect(Collectors.toList())
                .get(0)
                .getCdParent();

        activityResulteFetListRech =
            activityResulteFetList.stream()
                .filter(
                    e ->
                        e.getStudentDossier() != null
                            && e.getDayFet() == dayFetList.get(d)
                            && e.getHourFet() == hourFetList.get(h)
                            && (e.getStudentDossier().getNameFet().equals(teacherStudentRoomName)
                                || e.getStudentDossier().getCdParent().equals(cd)
                                || e.getStudentDossier().getCd().equals(cdParent)))
                .collect(Collectors.toList());
      }

      if (typeTable == IReport.ROOM_TABLE_TYPE)
        activityResulteFetListRech =
            activityResulteFetList.stream()
                .filter(
                    e ->
                        e.getRoomDossier() != null
                            && e.getDayFet() == dayFetList.get(d)
                            && e.getHourFet() == hourFetList.get(h)
                            && e.getRoomDossier().getNameFet().equals(teacherStudentRoomName))
                .collect(Collectors.toList());

      if (activityResulteFetListRech.size() == 0) return strings;

      int dureeInitial = 100;
      for (ActivityResulteFet activityResulteFet : activityResulteFetListRech) {
        dureeInitial = Math.min(dureeInitial, activityResulteFet.getDuration());
      }
      if (dureeInitial != 100) strings[0] = dureeInitial + "";

      StringBuilder strL1 = new StringBuilder();
      StringBuilder strL2 = new StringBuilder();
      for (ActivityResulteFet activityResulteFet : activityResulteFetListRech) {
        //                Color randomColor = new Color(124444);
        Color randomColor = Color.decode("#FFFFFF");
        if (activityResulteFet.getStudentDossier() != null
            && showColor
            && typeTable == IReport.TEACHER_TABLE_TYPE)
          randomColor =
              Color.decode(activityResulteFet.getStudentDossier().getStudentFet().getColor());
        if (activityResulteFet.getTeacherFet() != null
            && showColor
            && typeTable == IReport.STUDENT_TABLE_TYPE)
          randomColor = Color.decode(activityResulteFet.getTeacherFet().getColor());

        int r = randomColor.getRed();
        int g = randomColor.getGreen();
        int b = randomColor.getBlue();
        if (activityResulteFet.getStudentDossier() != null
            && activityResulteFet.getTeacherFet() != null
            && showColor
            && typeTable == IReport.ROOM_TABLE_TYPE) {
          r =
              (Color.decode(activityResulteFet.getTeacherFet().getColor()).getRed()
                      + Color.decode(
                              activityResulteFet.getStudentDossier().getStudentFet().getColor())
                          .getRed())
                  / 2;
          b =
              (Color.decode(activityResulteFet.getTeacherFet().getColor()).getBlue()
                      + Color.decode(
                              activityResulteFet.getStudentDossier().getStudentFet().getColor())
                          .getBlue())
                  / 2;
          g =
              (Color.decode(activityResulteFet.getTeacherFet().getColor()).getGreen()
                      + Color.decode(
                              activityResulteFet.getStudentDossier().getStudentFet().getColor())
                          .getGreen())
                  / 2;
        }

        strL1.append("");
        strL2.append("");
        if (typeTable == IReport.STUDENT_TABLE_TYPE
            && activityResulteFet.getStudentDossier() != null
            && activityResulteFet.getStudentDossier().getNameFet().equals(teacherStudentRoomName)) {
          //                    if (showStudent)
          //
          // str.append(activityResulteFet.getStudentDossier().getStudentFet().getName())
          //                                .append("<br>");
          //                    if (showSubject && activityResulteFet.getSubjectFet() != null)
          //
          // str.append(activityResulteFet.getSubjectFet().getName()).append("<br>");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            strL1.append(activityResulteFet.getSubjectFet().getShortName()).append("");
          if (showRoom && activityResulteFet.getRoomDossier() != null)
            strL2.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
        }

        if (typeTable == IReport.STUDENT_TABLE_TYPE
            && activityResulteFet.getStudentDossier() != null
            && !activityResulteFet
                .getStudentDossier()
                .getNameFet()
                .equals(teacherStudentRoomName)) {
          //                    str.append(
          //                            activityResulteFet
          //                                    .getStudentDossier()
          //                                    .getStudentFet()
          //                                    .getName()
          //                                    .replace(teacherStudentRoomName, ""))
          //                            .append(" -");
          //                    if (showSubject && activityResulteFet.getSubjectFet() != null)
          //
          // str.append(activityResulteFet.getSubjectFet().getName()).append(" -");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            strL1.append(activityResulteFet.getSubjectFet().getShortName()).append("-");
          if (showRoom && activityResulteFet.getRoomDossier() != null)
            strL2.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
        }
        if (typeTable == IReport.TEACHER_TABLE_TYPE) {
          if (showStudent && activityResulteFet.getStudentDossier() != null)
            strL1
                .append(activityResulteFet.getStudentDossier().getStudentFet().getName())
                .append("");
          //                    if (showSubject && activityResulteFet.getSubjectFet() != null)
          //
          // str.append(activityResulteFet.getSubjectFet().getName()).append("<br>");
          //                    if (ShortSubject && activityResulteFet.getSubjectFet() != null)
          //
          // str.append(activityResulteFet.getSubjectFet().getShortName()).append("<br>");
          if (showRoom && activityResulteFet.getRoomDossier() != null)
            strL2.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
        }
        if (typeTable == IReport.ROOM_TABLE_TYPE) {
          if (showStudent && activityResulteFet.getStudentDossier() != null)
            strL1.append(activityResulteFet.getStudentDossier().getStudentFet().getName());

          //                    if (showSubject && activityResulteFet.getSubjectFet() != null)
          //
          // str.append(activityResulteFet.getSubjectFet().getName()).append("<br>");
          if (showTeacher && activityResulteFet.getTeacherFet() != null)
            strL2.append(activityResulteFet.getTeacherFet().getName()).append(" ");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            strL2.append(activityResulteFet.getSubjectFet().getShortName());
        }
      }
      strings[1] = strL1.toString();
      strings[2] = strL2.toString();
      return strings;
    } catch (Exception e) {
      return new String[] {"1", "err", "err"};
    }
  }

  private int getCountMeeting(int d, int h, int typeTable) {
    int count = 0;
    if (typeTable == IReport.ROOM_TABLE_TYPE) {
      count =
          (int)
              activityResulteFetList.stream()
                  .filter(
                      e ->
                          e.getRoomDossier() != null
                              && e.getDayFet() == dayFetList.get(d)
                              && e.getHourFet() == hourFetList.get(h))
                  .count();
      if (h >= 1)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getRoomDossier() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 1)
                                && e.getDuration() >= 2)
                    .count();
      if (h >= 2)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getRoomDossier() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 2)
                                && e.getDuration() >= 3)
                    .count();
      if (h >= 3)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getRoomDossier() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 3)
                                && e.getDuration() >= 4)
                    .count();
    }
    if (typeTable == IReport.TEACHER_TABLE_TYPE) {
      count =
          (int)
              activityResulteFetList.stream()
                  .filter(
                      e ->
                          e.getTeacherFet() != null
                              && e.getDayFet() == dayFetList.get(d)
                              && e.getHourFet() == hourFetList.get(h))
                  .count();
      if (h >= 1)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getTeacherFet() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 1)
                                && e.getDuration() >= 2)
                    .count();
      if (h >= 2)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getTeacherFet() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 2)
                                && e.getDuration() >= 3)
                    .count();
      if (h >= 3)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getTeacherFet() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 3)
                                && e.getDuration() >= 4)
                    .count();
    }
    if (typeTable == IReport.STUDENT_TABLE_TYPE) {
      count =
          (int)
              activityResulteFetList.stream()
                  .filter(
                      e ->
                          e.getStudentDossier() != null
                              && e.getDayFet() == dayFetList.get(d)
                              && e.getHourFet() == hourFetList.get(h))
                  .count();
      if (h >= 1)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getStudentDossier() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 1)
                                && e.getDuration() >= 2)
                    .count();
      if (h >= 2)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getStudentDossier() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 2)
                                && e.getDuration() >= 3)
                    .count();
      if (h >= 3)
        count +=
            (int)
                activityResulteFetList.stream()
                    .filter(
                        e ->
                            e.getStudentDossier() != null
                                && e.getDayFet() == dayFetList.get(d)
                                && e.getHourFet() == hourFetList.get(h - 3)
                                && e.getDuration() >= 4)
                    .count();
    }
    return count;
  }
}
