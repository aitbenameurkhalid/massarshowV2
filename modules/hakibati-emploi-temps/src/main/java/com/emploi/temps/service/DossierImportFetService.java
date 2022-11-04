package com.emploi.temps.service;

import com.axelor.i18n.I18n;
import com.emploi.temps.web.IReport;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.emploi.db.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DossierImportFetService {
  List<ActivityResulteFet> activityResulteFetList;
  List<DayFet> dayFetList;
  List<HourFet> hourFetList;
  List<DayHourFet> dayHourFetList;
  List<StudentDossier> studentDossierList;
  List<TeacherDossier> teacherDossierList;
  List<RoomDossier> roomDossierList;

  public String ChargeEmploiTempsCollectif(DossierImportFet dossierImportFet, int typeTable) {

    dayFetList = dossierImportFet.getDaysFet();
    hourFetList = dossierImportFet.getHoursFet();
    activityResulteFetList = dossierImportFet.getActivityResultesFet();
    dayHourFetList = dossierImportFet.getResultatFet().getDayHoursFet();
    studentDossierList = dossierImportFet.getStudentDossiers();
    teacherDossierList = dossierImportFet.getTeacherDossiers();
    hourFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    dayFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    studentDossierList.sort(Comparator.comparing(StudentDossier::getCd));
    teacherDossierList.sort(Comparator.comparing(TeacherDossier::getSubjects));
    String dir = I18n.get("ltr");
    int Duration = 1;
    String[] meetingName = new String[2];
    StringBuilder result =
        new StringBuilder("<table dir=\"")
            .append(dir)
            .append(
                "\" style=\" border: 0px solid black; border-collapse: collapse ;width:100%;table-layout: fixed;\">");
    result.append("<thead style=\"display: table-header-group;\">");

    result.append("<tr>");
    result.append("<th colspan=\"4\"></th>");
    for (int d = 0; d < dayFetList.size(); d = d + 2) {
      Duration = 2 * hourFetList.size();
      result
          .append("<th colspan=\"")
          .append(Duration)
          .append(
              "\" style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
          .append(getNameRealDay(dayFetList.get(d).getNameFet()))
          .append("</th>");
    }
    result.append("</tr>");
    result.append("<th colspan=\"4\"></th>");
    for (int d = 0; d < dayFetList.size(); d = d + 2) {
      Duration = hourFetList.size();
      result
          .append("<th colspan=\"")
          .append(Duration)
          .append(
              "\" style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
          .append(I18n.get("Matin"))
          .append("</th>");
      result
          .append("<th colspan=\"")
          .append(Duration)
          .append(
              "\" style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
          .append(I18n.get("Apres-midi"))
          .append("</th>");
    }
    result.append("</tr>");
    result.append("<tr>");
    result.append("<th colspan=\"4\"></th>");
    for (int d = 0; d < dayFetList.size(); d = d + 1) {
      for (int h = 0; h < hourFetList.size(); h++) {
        result
            .append(
                "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
            .append(h + 1)
            .append("</th>");
      }
    }
    result.append("</tr>");
    result.append("</thead>");
    result.append("<tbody>");
    if (typeTable == IReport.TEACHER_TABLE_TYPE)
      for (TeacherDossier teacherDossier :
          teacherDossierList.stream()
              .filter(e -> !e.getNameFet().equals("*"))
              .collect(Collectors.toList())) {
        String teacherStudentRoomName = teacherDossier.getNameFet();
        result.append("<tr style=\"height:50px;\">");
        result
            .append("<th rowspan=\"2\" colspan=\"")
            .append(4)
            .append(
                "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center; \">");
        result.append(teacherDossier.getTeacherFet().getName().replace("_", " "));
        result.append(" " + teacherDossier.getSubjects().replace("_", " "));
        result.append("</th>");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d, h, teacherStudentRoomName, typeTable, false, false, true, true, true, false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            ;
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; writing-mode: vertical-rl;\">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
        result.append("<tr style=\"height:20px;\">");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    true,
                    false,
                    false,
                    false,
                    true,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            ;
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border-bottom: 2px solid black;border-left: 1px solid black;border-right: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
      }

    if (typeTable == IReport.STUDENT_TABLE_TYPE)
      for (StudentDossier studentDossier :
          studentDossierList.stream()
              .filter(e -> e.getCdGroupe() != -1 && e.getCdSubgroupe() == -1)
              .collect(Collectors.toList())) {
        String teacherStudentRoomName = studentDossier.getNameFet();
        result.append("<tr style=\"height:50px;\">");
        result
            .append("<th rowspan=\"2\" colspan=\"")
            .append(4)
            .append(
                "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center; \">");
        result.append(studentDossier.getName().replace("_", " "));
        result.append("</th>");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    false,
                    false,
                    true,
                    true,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; writing-mode: vertical-rl;\">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
        result.append("<tr style=\"height:20px;\">");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    true,
                    false,
                    false,
                    true,
                    false,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);

            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border-bottom: 2px solid black;border-left: 1px solid black;border-right: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
      }
    dossierImportFet.getRoomDossiers().sort((o1, o2) -> o1.getNameFet().compareTo(o2.getNameFet()));
    if (typeTable == IReport.ROOM_TABLE_TYPE)
      for (RoomDossier roomDossier :
          dossierImportFet.getRoomDossiers().stream()
              .filter(e -> !e.getNameFet().equals("*"))
              .collect(Collectors.toList())) {
        String teacherStudentRoomName = roomDossier.getNameFet();
        result.append("<tr style=\"height:50px;\">");
        result
            .append("<th rowspan=\"2\" colspan=\"")
            .append(4)
            .append(
                "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center; \">");
        result.append(roomDossier.getRoomFet().getName().replace("_", " "));
        result.append("<br>");
        result.append("(" + getCountHour(teacherStudentRoomName, typeTable) + ")");
        result.append("</th>");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    false,
                    true,
                    true,
                    false,
                    false);
            // getDuration(d, h, teacherStudentRoomName, typeTable);
            Duration = Integer.parseInt(meetingName[0]);
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; writing-mode: vertical-rl;\">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
        result.append("<tr style=\"height:20px;\">");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    false,
                    false,
                    false,
                    true,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border-bottom: 2px solid black;border-left: 1px solid black;border-right: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
      }

    result.append("</tbody>");
    result.append("</table>");
    return result.toString();
  }

  public String ChargeEmploiTempsCollectifDay(
      DossierImportFet dossierImportFet, int typeTable, int CdDayFirst, int CdDayLast) {

    dayFetList =
        dossierImportFet.getDaysFet().stream()
            .filter(e -> e.getCd() >= CdDayFirst && e.getCd() <= CdDayLast)
            .collect(Collectors.toList());
    hourFetList = dossierImportFet.getHoursFet();
    activityResulteFetList = dossierImportFet.getActivityResultesFet();
    dayHourFetList = dossierImportFet.getResultatFet().getDayHoursFet();
    studentDossierList = dossierImportFet.getStudentDossiers();
    teacherDossierList = dossierImportFet.getTeacherDossiers();
    // roomDossierList = dossierImportFet.getRoomDossiers();
    dossierImportFet.getRoomDossiers().sort(Comparator.comparing(o -> o.getRoomFet().getCd()));
    roomDossierList = dossierImportFet.getRoomDossiers();
    hourFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    dayFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    studentDossierList.sort(Comparator.comparing(StudentDossier::getCd));
    teacherDossierList.sort(Comparator.comparing(TeacherDossier::getSubjects));
    //     roomDossierList.sort(Comparator.comparing(RoomDossier::getName));
    // roomDossierList.sort(Comparator.comparing(o -> o.getRoomFet().getCd()));
    //   roomDossierList.sort((o1,o2)-> o1.getRoomFet().getCd().compareTo(o2.getRoomFet().getCd()));
    //  Collections.sort( roomDossierList ,(o1, o2) ->
    // o1.getRoomFet().getCd().compareTo(o2.getRoomFet().getCd()) );
    String dir = I18n.get("ltr");
    int Duration = 1;
    String[] meetingName = new String[2];
    StringBuilder result =
        new StringBuilder("<table dir=\"")
            .append(dir)
            .append(
                "\" style=\" border: 0px solid black; border-collapse: collapse ;width:100%;table-layout: fixed;\">");
    result.append("<thead style=\"display: table-header-group;\">");

    result.append("<tr>");
    result.append("<th colspan=\"1\"></th>");
    for (int d = 0; d < dayFetList.size(); d = d + 2) {
      Duration = 2 * hourFetList.size();
      result
          .append("<th colspan=\"")
          .append(Duration)
          .append(
              "\" style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
          .append(getNameRealDay(dayFetList.get(d).getNameFet()))
          .append("</th>");
    }
    result.append("</tr>");
    result.append("<th colspan=\"1\"></th>");
    for (int d = 0; d < dayFetList.size(); d = d + 2) {
      Duration = hourFetList.size();
      result
          .append("<th colspan=\"")
          .append(Duration)
          .append(
              "\" style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
          .append(I18n.get("Matin"))
          .append("</th>");
      result
          .append("<th colspan=\"")
          .append(Duration)
          .append(
              "\" style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
          .append(I18n.get("Apres-midi"))
          .append("</th>");
    }
    result.append("</tr>");
    result.append("<tr>");
    result.append("<th colspan=\"1\"></th>");
    for (int d = 0; d < dayFetList.size(); d = d + 1) {
      for (int h = 0; h < hourFetList.size(); h++) {
        result
            .append(
                "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
            .append(h + 1)
            .append("</th>");
      }
    }
    result.append("</tr>");
    result.append("</thead>");
    result.append("<tbody>");
    if (typeTable == IReport.TEACHER_TABLE_TYPE)
      for (TeacherDossier teacherDossier :
          teacherDossierList.stream()
              .filter(e -> !e.getNameFet().equals("*"))
              .collect(Collectors.toList())) {
        String teacherStudentRoomName = teacherDossier.getNameFet();
        result.append("<tr style=\"height:auto;\">");
        result
            .append("<th rowspan=\"2\" colspan=\"")
            .append(1)
            .append(
                "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center; \">");
        result.append(teacherDossier.getTeacherFet().getName().replace("_", " "));
        result.append(" " + teacherDossier.getSubjects().replace("_", " "));
        result.append("</th>");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    true,
                    true,
                    false,
                    false,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            ;
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border: 1px solid black; border-collapse: collapse; border-bottom:0.5px dotted black ;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
        result.append("<tr style=\"height:20px;\">");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            ;
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border-bottom: 2px solid black;border-left: 1px solid black;border-right: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
      }

    if (typeTable == IReport.STUDENT_TABLE_TYPE)
      for (StudentDossier studentDossier :
          studentDossierList.stream()
              .filter(e -> e.getCdGroupe() != -1 && e.getCdSubgroupe() == -1)
              .collect(Collectors.toList())) {
        String teacherStudentRoomName = studentDossier.getNameFet();
        result.append("<tr style=\"height:auto;\">");
        result
            .append("<th rowspan=\"2\" colspan=\"")
            .append(1)
            .append(
                "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center; \">");
        result.append(studentDossier.getName().replace("_", " "));
        result.append("</th>");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    true,
                    false,
                    false,
                    false,
                    true);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border: 1px solid black; border-collapse: collapse; border-bottom:0.5px dotted black ;text-align:center;font-size: 9px;\">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
        result.append("<tr style=\"height:auto;\">");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    true,
                    false,
                    false,
                    false,
                    false,
                    false);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);

            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border-bottom: 2px solid black;border-left: 1px solid black;border-right: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
      }
    dossierImportFet.getRoomDossiers().sort((o1, o2) -> o1.getNameFet().compareTo(o2.getNameFet()));
    if (typeTable == IReport.ROOM_TABLE_TYPE)
      for (RoomDossier roomDossier :
          roomDossierList.stream()
              .filter(e -> !e.getNameFet().equals("*"))
              .collect(Collectors.toList())) {
        String teacherStudentRoomName = roomDossier.getNameFet();
        result.append("<tr style=\"height:auto;\">");
        result
            .append("<th rowspan=\"2\" colspan=\"")
            .append(1)
            .append(
                "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center; \">");
        result.append(roomDossier.getRoomFet().getName().replace("_", " "));
        result.append("</th>");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    true,
                    true,
                    false,
                    false,
                    false);

            Duration = Integer.parseInt(meetingName[0]);
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border: 1px solid black;border-bottom:0.5px dotted black ;  border-collapse: collapse;text-align:center;font-size: 9px;\">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
        result.append("<tr style=\"height:auto;\">");
        for (int d = 0; d < dayFetList.size(); d = d + 1) {
          for (int h = 0; h < hourFetList.size(); h++) {
            meetingName =
                getMeetingName(
                    d,
                    h,
                    teacherStudentRoomName,
                    typeTable,
                    false,
                    false,
                    false,
                    false,
                    false,
                    true);
            Duration =
                Integer.parseInt(
                    meetingName[0]); // getDuration(d, h, teacherStudentRoomName, typeTable);
            result
                .append("<th colspan=\"")
                .append(Duration)
                .append(
                    "\" style=\" border-bottom: 2px solid black;border-left: 1px solid black;border-right: 1px solid black; border-collapse: collapse;text-align:center;font-size: 9px; \">");
            result.append(meetingName[1]);
            result.append("</th>");
            h += Duration - 1;
          }
        }
        result.append("</tr>");
      }

    result.append("</tbody>");
    result.append("</table>");
    return result.toString();
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

  private String getNameRealDay(String nameFet) {
    if (dayHourFetList.stream().noneMatch(e -> e.getDayFet().contains(nameFet))) return nameFet;
    return I18n.get(
        dayHourFetList.stream()
            .filter(e -> e.getDayFet().contains(nameFet))
            .collect(Collectors.toList())
            .get(0)
            .getRealDay());
  }

  public String[] ChargeEmploiTemps(
      DossierImportFet dossierImportFet,
      String teacherStudentRoomName,
      int typeTable,
      boolean showRoom,
      boolean showSubject,
      boolean showStudent,
      boolean showColor,
      boolean ShortSubject,
      boolean showTeacher) {
    try {
      //            teacherStudentRoomName="khalid";
      dayFetList = dossierImportFet.getDaysFet();
      hourFetList = dossierImportFet.getHoursFet();
      activityResulteFetList = dossierImportFet.getActivityResultesFet();
      dayHourFetList = dossierImportFet.getResultatFet().getDayHoursFet();
      studentDossierList = dossierImportFet.getStudentDossiers();
      hourFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
      dayFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
      String dir = I18n.get("ltr");
      int Duration = 1;
      String[] meetingName = new String[2];
      StringBuilder result = new StringBuilder();
      result =
          new StringBuilder("<table dir=\"")
              .append(dir)
              .append(
                  "\" style=\" border: 0px solid black; border-collapse: collapse ;width:100%;table-layout: fixed;\">");
      result.append("<thead>");
      result.append("<tr>");
      result.append("<th></th>");
      for (HourFet hourFet : hourFetList) {
        result
            .append(
                "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
            .append(getHourReal(hourFet.getNameFet(), dayFetList.get(0).getNameFet()))
            .append("</th>");
      }
      result.append("");
      for (HourFet hourFet2 : hourFetList) {
        result
            .append(
                "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
            .append(getHourReal(hourFet2.getNameFet(), dayFetList.get(1).getNameFet()))
            .append("</th>");
      }

      result.append("</tr>").append("</thead>").append("<tbody>");
      for (int d = 0; d < dayFetList.size(); d = d + 2) {
        result.append("<tr>");
        result.append(
            "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;padding: 0px;\">");
        result.append("<p>");
        result.append(getNameRealDay(dayFetList.get(d).getNameFet()));
        result.append("</p>");
        result.append("</th>");
        result.append("");
        for (int h = 0; h < hourFetList.size(); h++) {
          meetingName =
              getMeetingName(
                  d,
                  h,
                  teacherStudentRoomName,
                  typeTable,
                  showRoom,
                  showSubject,
                  showStudent,
                  showColor,
                  ShortSubject,
                  showTeacher);
          Duration =
              Integer.parseInt(
                  meetingName[0]); //  getDuration(d, h, teacherStudentRoomName, typeTable);
          result
              .append("<td colspan=\"")
              .append(Duration)
              .append(
                  "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;padding: 1px;\">")
              .append(meetingName[1])
              .append("</td>");
          h = h + Duration - 1;
        }
        result.append("");
        for (int h = 0; h < hourFetList.size(); h++) {
          meetingName =
              getMeetingName(
                  d + 1,
                  h,
                  teacherStudentRoomName,
                  typeTable,
                  showRoom,
                  showSubject,
                  showStudent,
                  showColor,
                  ShortSubject,
                  showTeacher);
          Duration =
              Integer.parseInt(
                  meetingName[0]); // getDuration(d + 1, h, teacherStudentRoomName, typeTable);
          result
              .append("<td colspan=\"")
              .append(Duration)
              .append(
                  "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;padding: 1px;\">")
              .append(meetingName[1])
              .append("</td>");
          h = h + Duration - 1;
        }
        result.append("</tr>");
      }

      result.append("</tbody>").append("</table>");
      String studentsTeacherTable = "";
      if (typeTable == IReport.TEACHER_TABLE_TYPE)
        studentsTeacherTable = getStudentsTable(teacherStudentRoomName, typeTable);
      if (typeTable == IReport.STUDENT_TABLE_TYPE)
        studentsTeacherTable = getSubjectTeacher(teacherStudentRoomName, typeTable);

      String strSubjects = getSubjects(teacherStudentRoomName, typeTable);

      String[] resultaTotal = new String[3];
      resultaTotal[0] = result.toString();
      resultaTotal[1] = studentsTeacherTable;
      resultaTotal[2] = strSubjects;
      return resultaTotal;

    } catch (Exception e) {
      return new String[] {e.getMessage() + " " + teacherStudentRoomName, e.getMessage(), ""};
    }
  }

  public String[] ChargeEmploiTempsFreeRoom(DossierImportFet dossierImportFet, boolean showColor) {
    try {
      dayFetList = dossierImportFet.getDaysFet();
      hourFetList = dossierImportFet.getHoursFet();
      activityResulteFetList = dossierImportFet.getActivityResultesFet();
      dayHourFetList = dossierImportFet.getResultatFet().getDayHoursFet();
      roomDossierList =
          dossierImportFet.getRoomDossiers().stream()
              .filter(e -> !e.getNameFet().equals("*"))
              .collect(Collectors.toList());
      hourFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
      dayFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
      String dir = I18n.get("ltr");
      StringBuilder result = new StringBuilder();
      result =
          new StringBuilder("<table dir=\"")
              .append(dir)
              .append(
                  "\" style=\" border: 0px solid black; border-collapse: collapse ;width:100%;table-layout: fixed;\">");
      result.append("<thead>");
      result.append("<tr>");
      result.append("<th></th>");
      for (HourFet hourFet : hourFetList) {
        result
            .append(
                "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
            .append(getHourReal(hourFet.getNameFet(), dayFetList.get(0).getNameFet()))
            .append("</th>");
      }
      result.append("");
      for (HourFet hourFet2 : hourFetList) {
        result
            .append(
                "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;\">")
            .append(getHourReal(hourFet2.getNameFet(), dayFetList.get(1).getNameFet()))
            .append("</th>");
      }

      result.append("</tr>").append("</thead>").append("<tbody>");
      for (int d = 0; d < dayFetList.size(); d = d + 2) {
        result.append("<tr>");
        result.append(
            "<th style=\" border: 2px solid black; border-collapse: collapse;text-align:center;padding: 0px;\">");
        result.append("<p>");
        result.append(getNameRealDay(dayFetList.get(d).getNameFet()));
        result.append("</p>");
        result.append("</th>");
        result.append("");
        for (int h = 0; h < hourFetList.size(); h++) {
          result
              .append("<td colspan=\"")
              .append(1)
              .append(
                  "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;padding: 1px;\">")
              .append(getRoomFree(d, h))
              .append("</td>");
        }
        result.append("");
        for (int h = 0; h < hourFetList.size(); h++) {
          result
              .append("<td colspan=\"")
              .append(1)
              .append(
                  "\" style=\" border: 1px solid black; border-collapse: collapse;text-align:center;padding: 1px;\">")
              .append(getRoomFree(d + 1, h))
              .append("</td>");
        }
        result.append("</tr>");
      }

      result.append("</tbody>").append("</table>");

      String[] resultaTotal = new String[3];
      resultaTotal[0] = result.toString();
      resultaTotal[1] = "";
      resultaTotal[2] = "";
      return resultaTotal;

    } catch (Exception e) {
      return new String[] {e.getMessage() + " ", e.getMessage(), ""};
    }
  }

  private String getStudentsTable(String teacherStudentRoomName, int typeTable) {
    try {
      List<StudentDossier> ListstudentsName =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getTeacherFet() != null
                          && e.getStudentDossier() != null
                          && ((e.getTeacherFet2() != null
                                  && e.getTeacherFet2().getNameFet().equals(teacherStudentRoomName))
                              || e.getTeacherFet().getNameFet().equals(teacherStudentRoomName)))
              .collect(
                  Collectors.groupingBy(
                      ActivityResulteFet::getStudentDossier, Collectors.counting()))
              .entrySet()
              .stream()
              .map(ep -> ep.getKey())
              .collect(Collectors.toList());
      List<Integer> ListstudentsSum =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getTeacherFet() != null
                          && e.getStudentDossier() != null
                          && ((e.getTeacherFet2() != null
                                  && e.getTeacherFet2().getNameFet().equals(teacherStudentRoomName))
                              || e.getTeacherFet().getNameFet().equals(teacherStudentRoomName)))
              .collect(
                  Collectors.groupingBy(
                      ActivityResulteFet::getStudentDossier,
                      Collectors.summingInt(ActivityResulteFet::getDuration)))
              .entrySet()
              .stream()
              .map(ep -> ep.getValue())
              .collect(Collectors.toList());
      if (ListstudentsName.size() == 0) return "";
      StringBuilder res =
          new StringBuilder(
              "<table dir=\"rtl\" style=\" border: 0px solid black; border-collapse: collapse ;width:100%;table-layout: fixed;\">");
      res.append("<thead>");
      res.append("<tr>");
      for (StudentDossier studentDossier : ListstudentsName) {
        res.append(
                "<th style=\" border: 1px solid black; border-collapse: collapse;text-align:center;\">")
            .append(studentDossier.getStudentFet().getName())
            .append("</th>");
      }
      res.append("</tr>");
      res.append("<tr>");
      for (Integer item : ListstudentsSum) {
        res.append(
                "<th style=\" border: 1px solid black; border-collapse: collapse;text-align:center;\">")
            .append(item.toString())
            .append("</th>");
      }
      res.append("</tr>");
      res.append("</thead>").append("</table>");

      return res.toString();

    } catch (Exception e) {
      return "";
    }
  }

  private String getSubjectTeacher(String teacherStudentRoomName, int typeTable) {
    try {
      List<SubjectFet> subjectFetListName =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getTeacherFet() != null
                          && e.getStudentDossier() != null
                          && ((e.getStudentDossier2() != null
                                  && e.getStudentDossier2()
                                      .getNameFet()
                                      .equals(teacherStudentRoomName))
                              || e.getStudentDossier().getNameFet().equals(teacherStudentRoomName)))
              .collect(
                  Collectors.groupingBy(ActivityResulteFet::getSubjectFet, Collectors.counting()))
              .entrySet()
              .stream()
              .map(ep -> ep.getKey())
              .collect(Collectors.toList());

      if (subjectFetListName.size() == 0) return "";
      StringBuilder res =
          new StringBuilder(
              "<table dir=\"rtl\" style=\" border: 0px solid black; border-collapse: collapse ;width:100%;table-layout: fixed;\">");
      res.append("<thead>");
      res.append("<tr>");
      for (SubjectFet subjectFet : subjectFetListName) {
        res.append(
                "<th style=\" border: 1px solid black; border-collapse: collapse;text-align:center;\">")
            .append(subjectFet.getName())
            .append("</th>");
      }
      res.append("</tr>");
      res.append("<tr>");
      for (SubjectFet subjectFet : subjectFetListName) {
        ActivityResulteFet activityResulteFetRech =
            activityResulteFetList.stream()
                .filter(
                    e ->
                        e.getTeacherFet() != null
                            && e.getStudentDossier() != null
                            && e.getSubjectFet() == subjectFet
                            && ((e.getStudentDossier2() != null
                                    && e.getStudentDossier2()
                                        .getNameFet()
                                        .equals(teacherStudentRoomName))
                                || e.getStudentDossier()
                                    .getNameFet()
                                    .equals(teacherStudentRoomName)))
                .collect(Collectors.toList())
                .get(0);
        res.append(
                "<th style=\" border: 1px solid black; border-collapse: collapse;text-align:center;\">")
            .append(activityResulteFetRech.getTeacherFet().getName())
            .append("</th>");
      }
      res.append("</tr>");
      res.append("</thead>").append("</table>");

      return res.toString();

    } catch (Exception e) {
      return "";
    }
  }

  private String getSubjects(String teacherStudentRoomName, int typeTable) {
    try {
      List<SubjectFet> Listmatieres =
          activityResulteFetList.stream()
              .filter(
                  e ->
                      e.getTeacherFet() != null
                          && e.getTeacherFet().getNameFet().equals(teacherStudentRoomName))
              .collect(
                  Collectors.groupingBy(ActivityResulteFet::getSubjectFet, Collectors.counting()))
              .entrySet()
              .stream()
              .map(ep -> ep.getKey())
              .collect(Collectors.toList());
      if (Listmatieres.size() == 0) return "";
      StringBuilder res = new StringBuilder(Listmatieres.get(0).getName());
      for (int m = 1; m < Listmatieres.size(); m++) {
        res.append(" - ");
        res.append(Listmatieres.get(m).getName());
      }
      return res.toString();
    } catch (Exception e) {
      return "";
    }
  }

  private String getCountHour(String teacherStudentRoomName, int typeTable) {
    int countHour = 0;

    List<ActivityResulteFet> activityResulteFetRech = null;
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
    return countHour + "";
  }

  private String getRoomFree(int d, int h) {
    StringBuilder str = new StringBuilder();
    int count = 0;
    for (RoomDossier roomDossier : roomDossierList) {
      if (activityResulteFetList.stream()
          .noneMatch(
              e ->
                  e.getRoomDossier() != null
                      && e.getRoomDossier().getNameFet().equals(roomDossier.getNameFet())
                      && e.getDayFet() == dayFetList.get(d)
                      && (e.getHourFet() == hourFetList.get(h)
                          || (h > 0
                              && e.getHourFet() == hourFetList.get(h - 1)
                              && e.getDuration() >= 2)
                          || (h > 1
                              && e.getHourFet() == hourFetList.get(h - 2)
                              && e.getDuration() >= 3)
                          || (h > 2
                              && e.getHourFet() == hourFetList.get(h - 3)
                              && e.getDuration() >= 4)
                          || (h > 3
                              && e.getHourFet() == hourFetList.get(h - 4)
                              && e.getDuration() >= 5)))) {
        str.append(roomDossier.getRoomFet().getName()).append("<br>");
        count++;
      }
    }
    if (count == 0) return I18n.get("aucune salle  disponible");
    if (roomDossierList.size() == count) return I18n.get("Toutes les salles sont disponibles");
    return str.toString();
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
    String[] strings = new String[2];
    strings[0] = "1";
    strings[1] = "";
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
                            && ((e.getHourFet() == hourFetList.get(h))
                                || (h > 0
                                    && e.getDuration() == 2
                                    && e.getHourFet() == hourFetList.get(h - 1)))
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
      //      strings[0]="1";
      //      dureeInitial=1;
      // cas des groupe science maths
      if (dureeInitial == 2 && typeTable == IReport.STUDENT_TABLE_TYPE) {
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
        List<ActivityResulteFet> activityResulteFetListRech2 =
            activityResulteFetList.stream()
                .filter(
                    e ->
                        e.getStudentDossier() != null
                            && e.getDayFet() == dayFetList.get(d)
                            && ((e.getDuration() == 1
                                    && h == 0
                                    && e.getHourFet() == hourFetList.get(h + 1))
                                || (e.getDuration() == 1
                                    && h == hourFetList.size() - 1
                                    && e.getHourFet() == hourFetList.get(h - 1)))
                            && (e.getStudentDossier().getNameFet().equals(teacherStudentRoomName)
                                || e.getStudentDossier().getCdParent().equals(cd)
                                || e.getStudentDossier().getCd().equals(cdParent)))
                .collect(Collectors.toList());

        for (ActivityResulteFet activityResulteFet : activityResulteFetListRech2) {
          dureeInitial = Math.min(dureeInitial, activityResulteFet.getDuration());
        }
        strings[0] = dureeInitial + "";
      }

      StringBuilder str = new StringBuilder();
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
        str.append("<div  style=\"height:100% ;width:100%;background-color:rgb(")
            .append(r)
            .append(", ")
            .append(g)
            .append(", ")
            .append(b)
            .append(");\" ><span>");
        if (typeTable == IReport.STUDENT_TABLE_TYPE
            && activityResulteFet.getStudentDossier() != null
            && activityResulteFet.getStudentDossier().getNameFet().equals(teacherStudentRoomName)) {
          if (showStudent)
            str.append(activityResulteFet.getStudentDossier().getStudentFet().getName())
                .append("<br>");
          if (showSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getName()).append("<br>");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getShortName()).append("<br>");
          if (showRoom && activityResulteFet.getRoomDossier() != null)
            str.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
          if (showTeacher && activityResulteFet.getTeacherFet() != null)
            str.append(activityResulteFet.getTeacherFet().getName());
          if (showTeacher && activityResulteFet.getTeacherFet2() != null)
            str.append(activityResulteFet.getTeacherFet2().getName());
        }

        if (typeTable == IReport.STUDENT_TABLE_TYPE
            && activityResulteFet.getStudentDossier() != null
            && !activityResulteFet
                .getStudentDossier()
                .getNameFet()
                .equals(teacherStudentRoomName)) {
          str.append(
                  activityResulteFet
                      .getStudentDossier()
                      .getStudentFet()
                      .getName()
                      .replace(teacherStudentRoomName, ""))
              .append(" -");
          if (showSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getName()).append(" -");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getShortName()).append(" -");
          if (showRoom && activityResulteFet.getRoomDossier() != null)
            str.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
          if (showTeacher && activityResulteFet.getTeacherFet() != null)
            str.append(activityResulteFet.getTeacherFet().getName());
          if (showTeacher && activityResulteFet.getTeacherFet2() != null)
            str.append(activityResulteFet.getTeacherFet2().getName());
        }
        if (typeTable == IReport.TEACHER_TABLE_TYPE) {
          if (showStudent && activityResulteFet.getStudentDossier() != null)
            str.append(activityResulteFet.getStudentDossier().getStudentFet().getName())
                .append("<br>");
          if (showSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getName()).append("<br>");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getShortName()).append("<br>");
          if (showRoom && activityResulteFet.getRoomDossier() != null)
            str.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
        }
        if (typeTable == IReport.ROOM_TABLE_TYPE) {
          if (showStudent && activityResulteFet.getStudentDossier() != null)
            str.append(activityResulteFet.getStudentDossier().getStudentFet().getName())
                .append("<br>");
          if (showSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getName()).append("<br>");
          if (ShortSubject && activityResulteFet.getSubjectFet() != null)
            str.append(activityResulteFet.getSubjectFet().getShortName()).append("<br>");
          if (showTeacher && activityResulteFet.getTeacherFet() != null)
            str.append(activityResulteFet.getTeacherFet().getName()).append("<br>");

          if (showRoom && activityResulteFet.getRoomDossier() != null)
            str.append(activityResulteFet.getRoomDossier().getRoomFet().getName());
        }

        str.append("</span></div>");
      }
      strings[1] = str.toString();
      //      return str.toString();
      return strings;
    } catch (Exception e) {
      //            return "error:" + e.getMessage();
      //  strings[1]="err";
      return new String[] {"1", "err" + e.toString()};
    }
  }

  @Transactional
  @Singleton
  public void RefreshEmploiTempsAll(
      DossierImportFet dossierImportFet,
      boolean showRoom,
      boolean showSubject,
      boolean showStudent,
      boolean showColor,
      boolean ShortSubject,
      boolean showTeacher) {

    try {
      for (int t = 0; t < dossierImportFet.getTeacherDossiers().size(); t++) {
        TeacherDossier teacherDossier2 = dossierImportFet.getTeacherDossiers().get(t);
        String[] result1 =
            ChargeEmploiTemps(
                dossierImportFet,
                teacherDossier2.getNameFet(),
                IReport.TEACHER_TABLE_TYPE,
                true,
                true,
                true,
                false,
                false,
                false);
        dossierImportFet.getTeacherDossiers().get(t).setTableService(result1[0]);
        dossierImportFet.getTeacherDossiers().get(t).setStudents(result1[1]);
        dossierImportFet.getTeacherDossiers().get(t).setSubjects(result1[2]);
      }
      for (int t = 0; t < dossierImportFet.getStudentDossiers().size(); t++) {
        StudentDossier studentDossier = dossierImportFet.getStudentDossiers().get(t);
        String[] result2 =
            ChargeEmploiTemps(
                dossierImportFet,
                studentDossier.getNameFet(),
                IReport.STUDENT_TABLE_TYPE,
                true,
                true,
                false,
                false,
                false,
                showTeacher);
        dossierImportFet.getStudentDossiers().get(t).setEmploiTemps(result2[0]);
        dossierImportFet.getStudentDossiers().get(t).setTeachers(result2[1]);
      }
      for (int t = 0; t < dossierImportFet.getRoomDossiers().size(); t++) {
        RoomDossier roomDossier = dossierImportFet.getRoomDossiers().get(t);
        String[] result =
            ChargeEmploiTemps(
                dossierImportFet,
                roomDossier.getNameFet(),
                IReport.ROOM_TABLE_TYPE,
                false,
                true,
                true,
                false,
                false,
                true);
        dossierImportFet.getRoomDossiers().get(t).setEmploiTemps(result[0]);
      }

    } catch (Exception ignored) {
    }
  }
}
// style="background-color:powderblue;  border: 1px solid black; border-collapse: collapse;"
//  writing-mode: vertical-rl;
//  text-orientation: upright;
