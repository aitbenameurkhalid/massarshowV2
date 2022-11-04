package com.emploi.temps.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.emploi.db.*;
import com.hakibati.emploi.db.ResultatFet;
import com.setting.service.excel.XmlRead;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ResultaFetService {

  List<ActivityResulteFet> activityResulteFetList;
  List<ActivityRoom> activityRoomList;
  List<ActivityTime> activityTimeList;
  List<DayFet> dayFetList;
  List<HourFet> hourFetList;
  List<TeacherFet> teacherFetList;
  List<SubjectFet> subjectFetList;
  List<StudentFet> studentFetList;
  List<RoomFet> roomFetList;
  List<DayHourFet> dayHourFetList;
  List<RoomDossier> roomDossierList;
  List<StudentDossier> studentDossierList;

  @Singleton
  @Transactional
  public String importResultaFet(MetaFile metaFile, ResultatFet resultatFet)
      throws ParserConfigurationException, SAXException, IOException {
    File xmlFile = MetaFiles.getPath(metaFile).toFile();
    if (!Files.getFileExtension(xmlFile.getName()).equals("fet")) {
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      return I18n.get("Choisissez un fichier au format fet");
    }
    String fileName = Files.getNameWithoutExtension(xmlFile.getName());
    User user = AuthUtils.getUser();

    //    try {
    DossierImportFet dossierImportFet = new DossierImportFet();
    dossierImportFet.setResultatFet(resultatFet);
    dossierImportFet.setFileName(fileName);
    teacherFetList = resultatFet.getTeachersFet();
    subjectFetList = resultatFet.getSubjectsFet();
    studentFetList = resultatFet.getStudentsFet();
    roomFetList = resultatFet.getRoomsFet();
    dayHourFetList = resultatFet.getDayHoursFet();

    XmlRead xmlRead = new XmlRead();
    dayFetList = new ArrayList<>();
    xmlRead.initialize(metaFile, "Day");
    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names = xmlRead.read(i, new String[] {"Name"});
      if (names[0].equals("")) continue;
      DayFet dayfet = new DayFet();
      dayfet.setName(names[0]);
      dayfet.setNameFet(names[0]);
      dayfet.setCd(i);
      dayfet.setDossierImportFet(dossierImportFet);
      dayFetList.add(dayfet);
    }
    dayFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    dossierImportFet.setDaysFet(dayFetList);
    hourFetList = new ArrayList<>();
    xmlRead.initialize(metaFile, "Hour");
    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names = xmlRead.read(i, new String[] {"Name"});
      if (names[0].equals("")) continue;
      HourFet hourFet = new HourFet();
      hourFet.setName(names[0]);
      hourFet.setNameFet(names[0]);
      hourFet.setCd(i);
      hourFet.setDossierImportFet(dossierImportFet);
      hourFetList.add(hourFet);
    }
    hourFetList.sort((o1, o2) -> o1.getCd().compareTo(o2.getCd()));
    dossierImportFet.setHoursFet(hourFetList);
    for (HourFet hourFet : hourFetList)
      for (DayFet dayFet : dayFetList) {
        if (dayHourFetList.stream()
            .noneMatch(
                e ->
                    e.getDayFet().contains(dayFet.getNameFet())
                        && e.getHourFet().contains(hourFet.getNameFet()))) {
          DayHourFet dayHourFet = new DayHourFet();
          dayHourFet.setDayFet(dayFet.getNameFet());
          dayHourFet.setHourFet(hourFet.getNameFet());
          dayHourFet.setCd(hourFetList.size() * dayFet.getCd() + hourFet.getCd());
          dayHourFet.setRealhour(getRealHour(hourFet, dayFet));
          dayHourFet.setRealDay(getRealDay(dayFet));
          dayHourFet.setResultatFet(resultatFet);
          dayHourFetList.add(dayHourFet);
          resultatFet.getDayHoursFet().add(dayHourFet);
        }
      }

    xmlRead.initialize(metaFile, "Subject");
    //            subjectFetList = new ArrayList<>();
    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names = xmlRead.read(i, new String[] {"Name"});
      if (names[0].equals("")) continue;
      SubjectFet subjectFet = new SubjectFet();
      subjectFet.setName(names[0]);
      subjectFet.setNameFet(names[0]);
      subjectFet.setShortName(names[0]);
      subjectFet.setResultatFet(resultatFet);

      if (subjectFetList.stream().noneMatch(e -> e.getNameFet().equals(subjectFet.getNameFet()))) {
        resultatFet.getSubjectsFet().add(subjectFet);
        subjectFetList.add(subjectFet);
      }
    }
    xmlRead.initialize(metaFile, "Teacher");
    List<TeacherDossier> teacherDossierList = new ArrayList<>();
    TeacherDossier teacherDossier0 = new TeacherDossier();
    TeacherFet teacherFet0 = new TeacherFet();
    teacherFet0.setNameFet("*");
    teacherFet0.setName("*");
    teacherFet0.setResultatFet(resultatFet);
    teacherDossier0.setDossierImportFet(dossierImportFet);
    teacherDossier0.setNameFet("*");
    teacherDossier0.setSubjects("0");
    if (teacherFetList.stream().noneMatch(e -> e.getNameFet().equals("*"))) {
      resultatFet.getTeachersFet().add(teacherFet0);
      teacherFetList.add(teacherFet0);
    }
    teacherDossier0.setTeacherFet(
        teacherFetList.stream()
            .filter(e -> e.getNameFet().equals("*"))
            .collect(Collectors.toList())
            .get(0));
    teacherDossierList.add(teacherDossier0);

    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names = xmlRead.read(i, new String[] {"Name"});
      if (names[0].equals("")) continue;
      TeacherFet teacherFet = new TeacherFet();
      TeacherDossier teacherDossier = new TeacherDossier();
      teacherFet.setName(names[0]);
      teacherFet.setNameFet(names[0]);
      teacherFet.setResultatFet(resultatFet);
      teacherFet.setColor(getcolorAleatoire());
      teacherDossier.setDossierImportFet(dossierImportFet);
      teacherDossier.setNameFet(names[0]);

      if (teacherFetList.stream().noneMatch(e -> e.getNameFet().equals(teacherFet.getNameFet()))) {
        resultatFet.getTeachersFet().add(teacherFet);
        teacherFetList.add(teacherFet);
      }
      teacherDossier.setTeacherFet(
          teacherFetList.stream()
              .filter(e -> e.getNameFet().equals(names[0]))
              .collect(Collectors.toList())
              .get(0));
      teacherDossierList.add(teacherDossier);
    }
    dossierImportFet.setTeacherDossiers(teacherDossierList);

    // importation la list des class eleve
    studentDossierList = new ArrayList<>();
    StudentDossier studentDossier2 = new StudentDossier();
    studentDossier2.setCd(-1);
    studentDossier2.setCdNiveau(-1);
    studentDossier2.setCdGroupe(-1);
    studentDossier2.setCdSubgroupe(-1);
    studentDossier2.setNameFet("*");
    studentDossier2.setName("*");
    studentDossier2.setStudentFet(null);
    studentDossier2.setDossierImportFet(dossierImportFet);
    studentDossierList.add(studentDossier2);

    String nameElement = "";
    int IdElement = 0;
    int IdYear = 0;
    int IdGroupe = 0;

    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document document = documentBuilder.parse(xmlFile);
    NodeList nodeListNiveau = document.getElementsByTagName("Year");
    for (int y = 0; y < nodeListNiveau.getLength(); y++) {
      Element elementYear = (Element) nodeListNiveau.item(y);
      NodeList nodeListYearName = elementYear.getElementsByTagName("Name").item(0).getChildNodes();
      Node nodeYearName = (Node) nodeListYearName.item(0);
      if (nodeYearName.getNodeValue() == null) continue;
      nameElement = nodeYearName.getNodeValue();
      StudentFet studentFet = new StudentFet();
      studentFet.setName(nameElement);
      studentFet.setNameFet(nameElement);
      studentFet.setResultatFet(resultatFet);
      studentFet.setColor(getcolorAleatoire());
      StudentDossier studentDossier = new StudentDossier();
      studentDossier.setNameFet(nameElement);
      studentDossier.setName(nameElement);
      IdElement++;
      IdYear = IdElement;
      studentDossier.setCd(IdElement);
      studentDossier.setCdParent(-1);
      studentDossier.setCdNiveau(y);
      studentDossier.setCdGroupe(-1);
      studentDossier.setCdSubgroupe(-1);
      studentDossier.setDossierImportFet(dossierImportFet);
      if (studentFetList.stream()
          .noneMatch(e -> e.getNameFet().equals(nodeYearName.getNodeValue()))) {
        resultatFet.getStudentsFet().add(studentFet);
        studentFetList.add(studentFet);
      }
      studentDossier.setStudentFet(
          studentFetList.stream()
              .filter(e -> e.getNameFet().equals(nodeYearName.getNodeValue()))
              .collect(Collectors.toList())
              .get(0));
      studentDossierList.add(studentDossier);

      NodeList nodeListClass = nodeListNiveau.item(y).getChildNodes();
      for (int g = 0; g < nodeListClass.getLength(); g++)
        if (nodeListClass.item(g).getNodeName().equals("Group")) {
          Element elementClass = (Element) nodeListClass.item(g);
          NodeList nodeListClassName =
              elementClass.getElementsByTagName("Name").item(0).getChildNodes();
          Node nodeclassName = (Node) nodeListClassName.item(0);
          if (nodeclassName.getNodeValue() == null) continue;
          nameElement = nodeclassName.getNodeValue();
          studentFet = new StudentFet();
          studentFet.setName(nameElement);
          studentFet.setNameFet(nameElement);
          studentFet.setResultatFet(resultatFet);
          studentFet.setColor(getcolorAleatoire());
          studentDossier = new StudentDossier();
          studentDossier.setNameFet(nameElement);
          studentDossier.setName(nameElement);
          IdElement++;
          IdGroupe = IdElement;
          studentDossier.setCd(IdElement);
          studentDossier.setCdParent(IdYear);
          studentDossier.setCdNiveau(y);
          studentDossier.setCdGroupe(g);
          studentDossier.setCdSubgroupe(-1);
          studentDossier.setDossierImportFet(dossierImportFet);

          if (studentFetList.stream()
              .noneMatch(e -> e.getNameFet().equals(nodeclassName.getNodeValue()))) {
            resultatFet.getStudentsFet().add(studentFet);
            studentFetList.add(studentFet);
          }
          studentDossier.setStudentFet(
              studentFetList.stream()
                  .filter(e -> e.getNameFet().equals(nodeclassName.getNodeValue()))
                  .collect(Collectors.toList())
                  .get(0));
          studentDossierList.add(studentDossier);
          NodeList nodeListSubgroup = nodeListClass.item(g).getChildNodes();
          for (int s = 0; s < nodeListSubgroup.getLength(); s++)
            if (nodeListSubgroup.item(s).getNodeName().equals("Subgroup")) {
              Element elementSubgroup = (Element) nodeListSubgroup.item(s);
              NodeList nodeListSubgroupName =
                  elementSubgroup.getElementsByTagName("Name").item(0).getChildNodes();
              Node nodeSubgroupName = (Node) nodeListSubgroupName.item(0);
              if (nodeSubgroupName.getNodeValue() == null) continue;
              nameElement = nodeSubgroupName.getNodeValue();
              studentFet = new StudentFet();
              studentFet.setName(nameElement);
              studentFet.setNameFet(nameElement);
              studentFet.setResultatFet(resultatFet);
              studentFet.setColor(getcolorAleatoire());
              studentDossier = new StudentDossier();
              studentDossier.setNameFet(nameElement);
              studentDossier.setName(nameElement);
              IdElement++;
              studentDossier.setCd(IdElement);
              studentDossier.setCdParent(IdGroupe);
              studentDossier.setCdNiveau(y);
              studentDossier.setCdGroupe(g);
              studentDossier.setCdSubgroupe(s);
              studentDossier.setDossierImportFet(dossierImportFet);
              if (studentFetList.stream()
                  .noneMatch(e -> e.getNameFet().equals(nodeSubgroupName.getNodeValue()))) {
                resultatFet.getStudentsFet().add(studentFet);
                studentFetList.add(studentFet);
              }
              studentDossier.setStudentFet(
                  studentFetList.stream()
                      .filter(e -> e.getNameFet().equals(nodeSubgroupName.getNodeValue()))
                      .collect(Collectors.toList())
                      .get(0));
              studentDossierList.add(studentDossier);
            }
        }
    }
    dossierImportFet.setStudentDossiers(studentDossierList);

    // fin importation list class eleve

    xmlRead.initialize(metaFile, "Room");
    roomDossierList = new ArrayList<>();
    RoomFet roomFet0 = new RoomFet();
    RoomDossier roomDossier0 = new RoomDossier();
    roomFet0.setName("*");
    roomFet0.setNameFet("*");
    roomFet0.setCd(0);
    roomDossier0.setNameFet("*");
    roomDossier0.setName("*");
    roomDossier0.setDossierImportFet(dossierImportFet);
    roomFet0.setResultatFet(resultatFet);
    if (roomFetList.stream().noneMatch(e -> e.getNameFet().equals("*"))) {
      resultatFet.getRoomsFet().add(roomFet0);
      roomFetList.add(roomFet0);
    }
    roomDossier0.setRoomFet(
        roomFetList.stream()
            .filter(e -> e.getNameFet().equals("*"))
            .collect(Collectors.toList())
            .get(0));

    roomDossierList.add(roomDossier0);

    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names = xmlRead.read(i, new String[] {"Name"});
      if (names[0].equals("")) continue;
      RoomFet roomFet = new RoomFet();
      RoomDossier roomDossier = new RoomDossier();
      roomFet.setName(names[0]);
      roomFet.setCd(i + 1);
      roomFet.setNameFet(names[0]);
      roomDossier.setNameFet(names[0]);
      roomDossier.setName(names[0]);
      roomDossier.setDossierImportFet(dossierImportFet);
      roomFet.setResultatFet(resultatFet);

      if (roomFetList.stream().noneMatch(e -> e.getNameFet().equals(roomFet.getNameFet()))) {
        resultatFet.getRoomsFet().add(roomFet);
        roomFetList.add(roomFet);
      }

      roomDossier.setRoomFet(
          roomFetList.stream()
              .filter(e -> e.getNameFet().equals(names[0]))
              .collect(Collectors.toList())
              .get(0));
      roomDossierList.add(roomDossier);
    }
    dossierImportFet.setRoomDossiers(roomDossierList);
    activityRoomList = new ArrayList<>();
    xmlRead.initialize(metaFile, "ConstraintActivityPreferredRoom");
    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names = xmlRead.read(i, new String[] {"Activity_Id", "Room"});
      if (names[0].equals("")) continue;
      if (names[1].equals("")) continue;
      ActivityRoom activityRoom = new ActivityRoom();
      activityRoom.setId(Integer.parseInt(names[0]));
      activityRoom.setRoomFet(getRoom(names[1]));
      activityRoomList.add(activityRoom);
    }

    activityTimeList = new ArrayList<>();
    xmlRead.initialize(metaFile, "ConstraintActivityPreferredStartingTime");
    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names =
          xmlRead.read(i, new String[] {"Activity_Id", "Preferred_Day", "Preferred_Hour"});
      if (names[0].equals("")) continue;
      if (names[1].equals("")) continue;
      if (names[2].equals("")) continue;
      ActivityTime activityTime = new ActivityTime();
      activityTime.setId(Integer.parseInt(names[0]));
      activityTime.setDayFet(getDay(names[1]));
      activityTime.setHourFet(getHour(names[2]));
      activityTimeList.add(activityTime);
      //   setTimeToactivityResulteFetList(names[0],names[1],names[2],resultatFet);
    }

    activityResulteFetList = new ArrayList<>();

    xmlRead.initialize(metaFile, "Activity");
    for (int i = 0; i < xmlRead.getLengthNod(); i++) {
      String[] names =
          xmlRead.read(i, new String[] {"Teacher", "Subject", "Students", "Duration", "Id"});
      String[] namesTeacher2 = xmlRead.read(i, new String[] {"Teacher"}, 1);
      String[] namesStudents2 = xmlRead.read(i, new String[] {"Students"}, 1);
      //      if(names[0].equals("")) continue;
      //      if(names[1].equals("")) continue;
      //      if(names[2].equals("")) continue;
      //      if(names[3].equals("")) continue;
      if (names[4].equals("")) continue;
      ActivityResulteFet activityResulteFet = new ActivityResulteFet();
      activityResulteFet.setDossierImportFet(dossierImportFet);
      activityResulteFet.setTeacherFet(getTeacher(names[0]));
      activityResulteFet.setTeacherFet2(getTeacher(namesTeacher2[0]));
      activityResulteFet.setSubjectFet(getSubject(names[1]));
      activityResulteFet.setStudentDossier(getStudent(names[2]));
      activityResulteFet.setStudentDossier2(getStudent(namesStudents2[0]));
      activityResulteFet.setDuration(Integer.parseInt(names[3]));
      activityResulteFet.setCd(Integer.parseInt(names[4]));

      if (getDayActivityTime(Integer.parseInt(names[4])) != null) {
        activityResulteFet.setDayFet(getDayActivityTime(Integer.parseInt(names[4])));
        activityResulteFet.setHourFet(getHourActivityTime(Integer.parseInt(names[4])));
      }
      activityResulteFet.setRoomDossier(null);
      if (getRoomActivityTime(Integer.parseInt(names[4])) != null) {
        activityResulteFet.setRoomDossier(getRoomActivityTime(Integer.parseInt(names[4])));
      }
      activityResulteFetList.add(activityResulteFet);
    }
    dossierImportFet.setProgressRoom(100 * activityRoomList.size() / activityResulteFetList.size());
    dossierImportFet.setProgressTime(100 * activityTimeList.size() / activityResulteFetList.size());
    dossierImportFet.setDaysFet(dayFetList);
    dossierImportFet.setHoursFet(hourFetList);

    if (activityRoomList.size() == 0) dossierImportFet.setProgressRoom(10);
    if (activityTimeList.size() == 0) dossierImportFet.setProgressTime(10);

    dossierImportFet.setActivityResultesFet(activityResulteFetList);
    resultatFet.getDossiersImportFet().add(dossierImportFet);

    DossierImportFetService dossierImportFetService = new DossierImportFetService();
    dossierImportFetService.RefreshEmploiTempsAll(
        dossierImportFet, false, false, false, false, false, false);

    return null;
  }

  private String getRealHour(HourFet hourFet, DayFet dayFet) {
    if (dayFet.getCd() % 2 == 0) {
      return (hourFet.getCd() + 8) + "-" + (hourFet.getCd() + 9);
    } else {
      return (hourFet.getCd() + 14) + "-" + (hourFet.getCd() + 15);
    }
  }

  private String getRealDay(DayFet dayFet) {
    try {
      if (dayFet.getNameFet().toUpperCase().contains("LUN")) return "LUNDI";
      if (dayFet.getNameFet().toUpperCase().contains("MAR")) return "MARDI";
      if (dayFet.getNameFet().toUpperCase().contains("MER")) return "MERCREDI";
      if (dayFet.getNameFet().toUpperCase().contains("JEU")) return "JEUDI";
      if (dayFet.getNameFet().toUpperCase().contains("VEN")) return "VENDREDI";
      if (dayFet.getNameFet().toUpperCase().contains("SAM")) return "SAMEDI";
      if (dayFet.getNameFet().toUpperCase().contains("DIM")) return "DIMANCHE";
      if (dayFet.getNameFet().contains("نين")) return "الإثنين";
      if (dayFet.getNameFet().contains("ثاء")) return "الثلاثاء";
      if (dayFet.getNameFet().contains("ربع")) return "الأربعاء";
      if (dayFet.getNameFet().contains("خم")) return "الخميس";
      if (dayFet.getNameFet().contains("جم")) return "الجمعة";
      if (dayFet.getNameFet().contains("سبت")) return "السبت";
      if (dayFet.getNameFet().contains("حد")) return "الأحد";

      String dayMorning = "";
      String dayAfternon = "";
      String dayReal = "";
      if (dayFet.getCd() % 2 == 0) {
        dayMorning = dayFet.getNameFet();

        dayAfternon = dayFetList.get(dayFet.getCd() + 1).getNameFet();
      } else {
        dayAfternon = dayFet.getNameFet();
        dayMorning = dayFetList.get(dayFet.getCd() - 1).getNameFet();
      }
      for (int i = 0; i < dayMorning.length(); i++) {
        if (dayMorning.charAt(i) == dayAfternon.charAt(i)) dayReal += dayMorning.charAt(i);
      }
      if (dayReal.equals("")) return dayMorning;
      return dayReal.replace("_", "").replace("-", "").replace(" ", "");
    } catch (Exception e) {
      //      return dayFet.getNameFet();
      return e.getMessage();
    }
  }

  private String getcolorAleatoire() {
    int r = 55 + (int) (Math.random() * (200));
    int g = 55 + (int) (Math.random() * (100));
    int b = 40 + (int) (Math.random() * (200));
    Color randomColor = new Color(r, g, b);
    //    return randomColor.getRGB();
    String hex = String.format("#%02X%02X%02X", r, g, b);
    return hex;
  }

  private DayFet getDayActivityTime(int parseInt) {
    if (activityTimeList.stream().noneMatch(e -> e.getId() == parseInt)) return null;
    return activityTimeList.stream()
        .filter(e -> e.getId() == parseInt)
        .collect(Collectors.toList())
        .get(0)
        .getDayFet();
  }

  private HourFet getHourActivityTime(int parseInt) {
    if (activityTimeList.stream().noneMatch(e -> e.getId() == parseInt)) return null;
    return activityTimeList.stream()
        .filter(e -> e.getId() == parseInt)
        .collect(Collectors.toList())
        .get(0)
        .getHourFet();
  }

  private RoomDossier getRoomActivityTime(int parseInt) {
    if (activityRoomList.stream().noneMatch(e -> e.getId() == parseInt)) return null;
    RoomFet roomFet =
        activityRoomList.stream()
            .filter(e -> e.getId() == parseInt)
            .collect(Collectors.toList())
            .get(0)
            .getRoomFet();
    if (roomDossierList.stream().noneMatch(e -> e.getNameFet().equals(roomFet.getNameFet())))
      return null;
    return roomDossierList.stream()
        .filter(e -> e.getNameFet().equals(roomFet.getNameFet()))
        .collect(Collectors.toList())
        .get(0);
  }

  private DayFet getDay(String name) {
    return dayFetList.stream()
        .filter(e -> e.getNameFet().equals(name))
        .collect(Collectors.toList())
        .get(0);
  }

  private HourFet getHour(String name) {
    return hourFetList.stream()
        .filter(e -> e.getNameFet().equals(name))
        .collect(Collectors.toList())
        .get(0);
  }

  private RoomFet getRoom(String name) {
    if (name == null || name.equals("")) return null;
    return roomFetList.stream()
        .filter(e -> e.getNameFet().equals(name))
        .collect(Collectors.toList())
        .get(0);
  }

  private StudentDossier getStudent(String name) {
    if (name == null || name.equals("")) return null;
    try {
      return studentDossierList.stream()
          .filter(e -> e.getNameFet().equals(name))
          .collect(Collectors.toList())
          .get(0);
    } catch (Exception e) {
      return null;
    }
  }

  private SubjectFet getSubject(String name) {
    if (name == null || name.equals("")) return null;
    return subjectFetList.stream()
        .filter(e -> e.getNameFet().equals(name))
        .collect(Collectors.toList())
        .get(0);
  }

  private TeacherFet getTeacher(String nameFet) {
    if (nameFet == null || nameFet.equals("")) return null;
    return teacherFetList.stream()
        .filter(e -> e.getNameFet().equals(nameFet))
        .collect(Collectors.toList())
        .get(0);
  }
}
