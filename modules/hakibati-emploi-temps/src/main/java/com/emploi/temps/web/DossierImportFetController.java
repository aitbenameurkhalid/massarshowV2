package com.emploi.temps.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JPA;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.emploi.temps.service.DossierImportFetService;
import com.emploi.temps.service.ExportEmpoiExcel;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.hakibati.emploi.db.DossierImportFet;
import com.hakibati.emploi.db.RoomDossier;
import com.hakibati.emploi.db.StudentDossier;
import com.hakibati.emploi.db.TeacherDossier;
import com.setting.service.PermissionEtablissemntService;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class DossierImportFetController {
  @Transactional
  public void printAll(ActionRequest request, ActionResponse response) throws IOException {
    if (!PermissionEtablissemntService.getvalideModuleEtablissement(
        IReport.CODE_OF_MODULE_EMPLOI_PERMISSION)) {
      PermissionEtablissemntService permissionEtablissemntService =
          new PermissionEtablissemntService();
      permissionEtablissemntService.ShowEtablissementActivation(request, response);
      return;
    }

    User user = AuthUtils.getUser();
    String name = "Badge-Ar";
    DossierImportFet dossierImportFet = request.getContext().asType(DossierImportFet.class);

    Long IdDoss = dossierImportFet.getId();
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    //    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLS;
    //    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOC;
    String UrlLogo = "";
    try {
      UrlLogo =
          Objects.requireNonNull(
                  this.getClass().getClassLoader().getResource("../../img/logo-ar.png"))
              .toString()
              .replace("%20", " ");
    } catch (Exception ignored) {
    }
    String document_imprime_name = (String) request.getContext().get("documentImprime");
    if (document_imprime_name == null) return;

    if (document_imprime_name.equals("1")) {
      document_imprime_name = IReport.print_tables_services_profs_individuelle_1;
      name = "tables_services_profs_individuelle_1";
    }
    if (document_imprime_name.equals("2")) {
      document_imprime_name = IReport.print_tables_services_profs_individuelle_2;
      if (format_print.equals(ReportSettings.FORMAT_PDF))
        document_imprime_name = IReport.print_tables_services_profs_individuelle_2_pdf;

      name = "tables_services_profs_individuelle_2";
    }
    if (document_imprime_name.equals("3")) {
      document_imprime_name = IReport.print_tables_services_profs_individuelle_3;
      name = "tables_services_profs_individuelle_3";
    }
    String TitreTableCollective = "";
    String TableCollectif = "";
    DossierImportFetService dossierImportFetService = new DossierImportFetService();
    //    if (document_imprime_name.equals("4") && format_print.equals(ReportSettings.FORMAT_XLSX))
    // {
    //      printAllExcel(request, response, IReport.TEACHER_TABLE_TYPE);
    //      return;
    //    }
    if (document_imprime_name.equals("4")) {
      printAllExcel(request, response, IReport.TEACHER_TABLE_TYPE);
      return;
      //            document_imprime_name = IReport.print_emploi_temps_collectif_1;
      //            TitreTableCollective = I18n.get("Tables services des Professeurs");
      //            TableCollectif =
      //                dossierImportFetService.ChargeEmploiTempsCollectif(
      //                    dossierImportFet, IReport.TEACHER_TABLE_TYPE);
      //            name = "emploi_temps_Professeurs_collectif_1";
    }
    if (document_imprime_name.equals("5")) {
      document_imprime_name = IReport.print_emploi_temps_student_individuelle_1;
      name = "emploi_temps_eleves_individuelle_1";
    }
    if (document_imprime_name.equals("105")) {
      document_imprime_name = IReport.print_emploi_temps_student_individuelle_1_profs;
      name = "emploi_temps_eleves_individuelle_1_prof";
    }

    if (document_imprime_name.equals("6")) {
      document_imprime_name = IReport.print_emploi_temps_student_individuelle_2;
      if (format_print.equals(ReportSettings.FORMAT_PDF))
        document_imprime_name = IReport.print_emploi_temps_student_individuelle_2_pdf;

      name = "emploi_temps_eleves_individuelle_2";
    }
    if (document_imprime_name.equals("7")) {
      document_imprime_name = IReport.print_emploi_temps_student_individuelle_3;
      name = "emploi_temps_eleves_individuelle_3";
    }
    //    if (document_imprime_name.equals("8")  && format_print.equals(ReportSettings.FORMAT_XLSX))
    // {
    //      printAllExcel(request, response, IReport.STUDENT_TABLE_TYPE);
    //      return;
    //    }
    if (document_imprime_name.equals("8")) {
      printAllExcel(request, response, IReport.STUDENT_TABLE_TYPE);
      return;
      //            document_imprime_name = IReport.print_emploi_temps_collectif_1;
      //            TitreTableCollective = I18n.get("Emploi Du temps des classes");
      //            TableCollectif =
      //                dossierImportFetService.ChargeEmploiTempsCollectif(
      //                    dossierImportFet, IReport.STUDENT_TABLE_TYPE);
      //            name = "emploi_temps_classes_collectif_1";
    }
    if (document_imprime_name.equals("9")) {
      document_imprime_name = IReport.print_emploi_temps_room_individuelle_3;
      if (format_print.equals(ReportSettings.FORMAT_PDF))
        document_imprime_name = IReport.print_emploi_temps_room_individuelle_3_pdf;

      name = "emploi_temps_salles_individuelle_3";
    }

    //    if (document_imprime_name.equals("10") && format_print.equals(ReportSettings.FORMAT_XLSX))
    // {
    //      printAllExcel(request, response, IReport.ROOM_TABLE_TYPE);
    //      return;
    //    }
    if (document_imprime_name.equals("10")) {
      printAllExcel(request, response, IReport.ROOM_TABLE_TYPE);
      return;
      //            document_imprime_name = IReport.print_emploi_temps_collectif_1;
      //            TitreTableCollective = I18n.get("Emploi Du temps des Salles");
      //            TableCollectif =
      //                dossierImportFetService.ChargeEmploiTempsCollectif(
      //                    dossierImportFet, IReport.ROOM_TABLE_TYPE);
      //
      //            name = "emploi_temps_Salles_collectif_1";
    }
    if (document_imprime_name.equals("21")) {
      TitreTableCollective = I18n.get("Table service des professeur par Jour");
      TableCollectif =
          dossierImportFetService.ChargeEmploiTempsCollectifDay(
              dossierImportFet, IReport.TEACHER_TABLE_TYPE, 0, 1);
      for (int i = 2; i < dossierImportFet.getDaysFet().size(); i = i + 2) {
        TableCollectif += "<br>";
        TableCollectif +=
            dossierImportFetService.ChargeEmploiTempsCollectifDay(
                dossierImportFet, IReport.TEACHER_TABLE_TYPE, i, i + 1);
      }

      document_imprime_name = IReport.print_emploi_temps_collectif_day;
      name = "emploi_temps_profes_par_jour";
    }
    if (document_imprime_name.equals("22")) {
      TitreTableCollective = I18n.get("Emploi Du temps des eleves par Jour");
      TableCollectif =
          dossierImportFetService.ChargeEmploiTempsCollectifDay(
              dossierImportFet, IReport.STUDENT_TABLE_TYPE, 0, 1);
      for (int i = 2; i < dossierImportFet.getDaysFet().size(); i = i + 2) {
        TableCollectif += "<br>";
        TableCollectif +=
            dossierImportFetService.ChargeEmploiTempsCollectifDay(
                dossierImportFet, IReport.STUDENT_TABLE_TYPE, i, i + 1);
      }

      document_imprime_name = IReport.print_emploi_temps_collectif_day;
      name = "emploi_temps_eleves_par_jour";
    }
    if (document_imprime_name.equals("23")) {
      TitreTableCollective = I18n.get("Emploi Du temps des Salles par Jour");
      TableCollectif =
          dossierImportFetService.ChargeEmploiTempsCollectifDay(
              dossierImportFet, IReport.ROOM_TABLE_TYPE, 0, 1);
      for (int i = 2; i < dossierImportFet.getDaysFet().size(); i = i + 2) {
        TableCollectif += "<br>";
        TableCollectif +=
            dossierImportFetService.ChargeEmploiTempsCollectifDay(
                dossierImportFet, IReport.ROOM_TABLE_TYPE, i, i + 1);
      }

      document_imprime_name = IReport.print_emploi_temps_collectif_day;
      name = "emploi_temps_salles_par_jour";
    }
    if (document_imprime_name.equals("24")) {
      TitreTableCollective = I18n.get("la table des salles disponible");
      TableCollectif =
          dossierImportFetService.ChargeEmploiTempsFreeRoom(dossierImportFet, false)[0];

      document_imprime_name = IReport.print_emploi_temps_collectif_portrait_1;
      name = "table_salles_libre";
    }

    try {
      String fileLink =
          ReportFactory.createReport(document_imprime_name, name)
              .addParam("IdDoss", IdDoss)
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("TableCollectif", TableCollectif)
              .addParam("TitreTableCollective", TitreTableCollective)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  @Transactional
  public void ChangeSelectTeacher(ActionRequest request, ActionResponse response) {
    Map teacherFetSelect = (Map) request.getContext().get("professeurSelect");
    if (teacherFetSelect == null) return;
    Integer teacherFetId = (Integer) teacherFetSelect.get("id");
    if (teacherFetId == null) return;
    TeacherDossier teacherDossier = JPA.find(TeacherDossier.class, Long.valueOf(teacherFetId));
    if (teacherDossier == null) return;

    DossierImportFetService dossierImportFetService = new DossierImportFetService();
    DossierImportFet dossierImportFet = request.getContext().asType(DossierImportFet.class);
    if (teacherDossier.getNameFet().equals("*")) {
      String resultCollectif =
          dossierImportFetService.ChargeEmploiTempsCollectif(
              dossierImportFet, IReport.TEACHER_TABLE_TYPE);
      response.setValue("resulta", resultCollectif);

    } else {
      String[] result =
          dossierImportFetService.ChargeEmploiTemps(
              dossierImportFet,
              teacherDossier.getTeacherFet().getNameFet(),
              IReport.TEACHER_TABLE_TYPE,
              true,
              true,
              true,
              true,
              false,
              false);
      response.setValue("resulta", result[0]);
    }
  }

  public void ChangeSelectStudent(ActionRequest request, ActionResponse response) {
    Map studentFetSelect = (Map) request.getContext().get("studntSelect");
    if (studentFetSelect == null) return;
    Integer studentFetId = (Integer) studentFetSelect.get("id");
    if (studentFetId == null) return;
    StudentDossier studentDossier = JPA.find(StudentDossier.class, Long.valueOf(studentFetId));
    if (studentDossier == null) return;
    DossierImportFetService dossierImportFetService = new DossierImportFetService();
    DossierImportFet dossierImportFet = request.getContext().asType(DossierImportFet.class);

    if (studentDossier.getCd() >= 0) {
      String[] result =
          dossierImportFetService.ChargeEmploiTemps(
              dossierImportFet,
              studentDossier.getStudentFet().getNameFet(),
              IReport.STUDENT_TABLE_TYPE,
              true,
              true,
              true,
              true,
              false,
              false);

      response.setValue("resulta", result[0]);
    }
    if (studentDossier.getCd() == -1) {
      String resultcollectif =
          dossierImportFetService.ChargeEmploiTempsCollectif(
              dossierImportFet, IReport.STUDENT_TABLE_TYPE);
      response.setValue("resulta", resultcollectif);
    }

    String str;

    str = "";
    str =
        str
            + "<div class=\"class1\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\"> "
            + "<img src=\"img/user.png\" ondragstart=\"dragStart(event)\" ondragend=\"dragEnd(event)\" draggable=\"true\" id=\"1\" />"
            + "    </div> "
            + "<div class=\"class1\" ondrop=\"drop(event)\" ondragover=\"allowDrop(event)\"\">"
            + "</div>"
            + "<style>"
            + "img {\n"
            + "  border-radius: 8px;\n"
            + "  border-radius: 50%;\n"
            + "   padding: 5px;\n"
            + "  width: 150px;\n"
            + "   border:2px solid black;\n"
            + "  \n"
            + "}"
            + ".class1 {\n"
            + "  border-style: solid;\n"
            + "  border-width: 1px;\n"
            + "    width: 400px;\n"
            + "\theight: 300px\n"
            + "}"
            + ".class2 {\n"
            + "  border-style: solid;\n"
            + "  border-width: 5px;\n"
            + "    width: 400px;\n"
            + "\theight: 300px\n"
            + "}"
            + "</style>";

    //

  }

  public void ChangeSelectRoom(ActionRequest request, ActionResponse response) {
    Map roomFetSelect = (Map) request.getContext().get("roomSelect");
    if (roomFetSelect == null) return;
    Integer roomFetId = (Integer) roomFetSelect.get("id");
    if (roomFetId == null) return;
    RoomDossier roomDossier = JPA.find(RoomDossier.class, Long.valueOf(roomFetId));
    if (roomDossier == null) return;
    DossierImportFetService dossierImportFetService = new DossierImportFetService();
    DossierImportFet dossierImportFet = request.getContext().asType(DossierImportFet.class);

    if (roomDossier.getNameFet().equals("*")) {
      String resultcollectif =
          dossierImportFetService.ChargeEmploiTempsCollectif(
              dossierImportFet, IReport.ROOM_TABLE_TYPE);
      response.setValue("resulta", resultcollectif);

    } else {
      String[] result =
          dossierImportFetService.ChargeEmploiTemps(
              dossierImportFet,
              roomDossier.getNameFet(),
              IReport.ROOM_TABLE_TYPE,
              true,
              true,
              true,
              true,
              false,
              false);
      response.setValue("resulta", result[0]);
    }

    String str2;
    str2 =
        "          <button class=\"btn\" ng-click=\"onNew()\" ng-if=\"canNew()\" title=\"{{ 'New' | t}}\">\n"
            + "            <i class=\"fa fa-plus\"></i>\n"
            + "          </button>\n"
            + "          <button class=\"btn\" ng-click=\"onSave()\" ng-if=\"isEditable() && hasButton('save') && (canEdit() || canSave())\"  title=\"{{ 'Save' | t}}\">\n"
            + "            <i class=\"fa fa-floppy-o\"></i>\n"
            + "          </button>\n"
            + "          <button class=\"btn\" ng-click=\"onCancel()\" ng-disabled=\"!canCancel()\" title=\"{{ 'Cancel' | t}}\">\n"
            + "            <i class=\"fa fa-refresh\"></i>\n"
            + "          </button>\n"
            + "          <button class=\"btn\" ng-disabled=\"!canShowAttachments()\" ng-click=\"onShowAttachments()\" ng-class=\"{'has-attachments': record.$attachments}\">\n"
            + "            <i class=\"fa fa-paperclip\"></i><sub ng-if=\"record.$attachments\"> {{record.$attachments}}</sub>\n"
            + "          </button>";
    //    response.setAlert("ChangeSelectRoom");
  }

  @Transactional
  public void SaveEdt(ActionRequest request, ActionResponse response) {
    DossierImportFetService dossierImportFetService = new DossierImportFetService();
    DossierImportFet dossierImportFet = request.getContext().asType(DossierImportFet.class);
    String document_imprime_name = (String) request.getContext().get("documentImprime");
    boolean showRoom = false;
    boolean showSubject = false;
    boolean showStudent = false;
    boolean showColor = false;
    boolean ShortSubject = false;
    boolean showTeacher = false;

    if (document_imprime_name.equals("105")) {
      showTeacher = true;
    }
    dossierImportFetService.RefreshEmploiTempsAll(
        dossierImportFet, showRoom, showSubject, showStudent, showColor, ShortSubject, showTeacher);
  }

  @Inject private MetaFiles metaFiles;

  public void printAllExcel(ActionRequest request, ActionResponse response, int typeTable)
      throws IOException {
    DossierImportFet dossierImportFet = request.getContext().asType(DossierImportFet.class);

    ExportEmpoiExcel exportEmpoiExcel = new ExportEmpoiExcel();
    File file = exportEmpoiExcel.exportTableCollectigExcel(dossierImportFet, typeTable);

    MetaFile metaFile = metaFiles.upload(file);
    response.setView(
        ActionView.define("Data")
            .add(
                "html",
                "ws/rest/com.axelor.meta.db.MetaFile/"
                    + metaFile.getId()
                    + "/content/download?v="
                    + metaFile.getVersion())
            .param("download", "true")
            .map());

    //    response.setCanClose(true);
    //    file.delete();
    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
  }
}
