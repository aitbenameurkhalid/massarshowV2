package com.comptabilite.matier.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.hakibati.comptabilite.matier.db.OperationE;
import com.hakibati.comptabilite.matier.db.OperationS;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class OperationPrintController {
  public void PrintMatiereArticale(ActionRequest request, ActionResponse response) {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    String name = "PrintMatiereArticale";
    User user = AuthUtils.getUser();
    Integer IdArticleMax = Integer.MAX_VALUE;
    Integer IdArticleMin = Integer.MIN_VALUE;
    Integer IdTypeArticleMax = Integer.MAX_VALUE;
    Integer IdTypeArticleMin = Integer.MIN_VALUE;
    Map selectedArticle = (Map) request.getContext().get("ParArticle");
    if (selectedArticle == null) {
      IdArticleMax = Integer.MAX_VALUE;
      IdArticleMin = Integer.MIN_VALUE;
    } else {
      IdArticleMax = (Integer) selectedArticle.get("id");
      IdArticleMin = (Integer) selectedArticle.get("id");
    }
    Map selectedTypeArticle = (Map) request.getContext().get("ParType");
    if (selectedTypeArticle == null) {
      IdTypeArticleMax = Integer.MAX_VALUE;
      IdTypeArticleMin = Integer.MIN_VALUE;
    } else {
      IdTypeArticleMax = (Integer) selectedTypeArticle.get("id");
      IdTypeArticleMin = (Integer) selectedTypeArticle.get("id");
    }

    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.ARTICLE_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdArticleMin", IdArticleMin)
              .addParam("IdArticleMax", IdArticleMax)
              .addParam("IdTypeArticleMax", IdTypeArticleMax)
              .addParam("IdTypeArticleMin", IdTypeArticleMin)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintListMagasin(ActionRequest request, ActionResponse response) {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    String name = "PrintListMagasin";
    User user = AuthUtils.getUser();
    Integer IdArticleMax = Integer.MAX_VALUE;
    Integer IdArticleMin = Integer.MIN_VALUE;
    Integer IdTypeArticleMax = Integer.MAX_VALUE;
    Integer IdTypeArticleMin = Integer.MIN_VALUE;
    Map selectedArticle = (Map) request.getContext().get("ParArticle");
    if (selectedArticle == null) {
      IdArticleMax = Integer.MAX_VALUE;
      IdArticleMin = Integer.MIN_VALUE;
    } else {
      IdArticleMax = (Integer) selectedArticle.get("id");
      IdArticleMin = (Integer) selectedArticle.get("id");
    }
    Map selectedTypeArticle = (Map) request.getContext().get("ParType");
    if (selectedTypeArticle == null) {
      IdTypeArticleMax = Integer.MAX_VALUE;
      IdTypeArticleMin = Integer.MIN_VALUE;
    } else {
      IdTypeArticleMax = (Integer) selectedTypeArticle.get("id");
      IdTypeArticleMin = (Integer) selectedTypeArticle.get("id");
    }
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.MAGASIN_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("IdArticleMin", IdArticleMin)
              .addParam("IdArticleMax", IdArticleMax)
              .addParam("IdTypeArticleMax", IdTypeArticleMax)
              .addParam("IdTypeArticleMin", IdTypeArticleMin)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error ") + e.getMessage());
    }
  }

  public void PrintoperationEsingle(ActionRequest request, ActionResponse response) {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    OperationE operationE = request.getContext().asType(OperationE.class);
    String name = "PrintoperationEsingle";
    User user = AuthUtils.getUser();
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.OperationE_report, name)
              .addParam("IdReport", operationE.getId())
              .addParam("UrlLogo", UrlLogo)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error ") + e.getMessage());
    }
  }

  public void PrintoperationSsingle(ActionRequest request, ActionResponse response) {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    OperationS operationS = request.getContext().asType(OperationS.class);
    String name = "PrintoperationSsingle";
    User user = AuthUtils.getUser();
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.OperationS_report, name)
              .addParam("IdReport", operationS.getId())
              .addParam("UrlLogo", UrlLogo)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintBilanEntrees(ActionRequest request, ActionResponse response)
      throws ParseException {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    String dateDuStr = null;
    String dateAuStr = null;
    Integer IdArticleMax = Integer.MAX_VALUE;
    Integer IdArticleMin = Integer.MIN_VALUE;
    Integer IdTypeArticleMax = Integer.MAX_VALUE;
    Integer IdTypeArticleMin = Integer.MIN_VALUE;
    Integer IdPrevenanceMax = Integer.MAX_VALUE;
    Integer IdPrevenanceMin = Integer.MIN_VALUE;

    dateDuStr = (String) request.getContext().get("du");
    if (StringUtils.isBlank(dateDuStr)) dateDuStr = "0001-01-01";
    dateAuStr = (String) request.getContext().get("au");
    if (StringUtils.isBlank(dateAuStr)) dateAuStr = "9999-01-01";
    Map selectedArticle = (Map) request.getContext().get("article");
    if (selectedArticle == null) {
      IdArticleMax = Integer.MAX_VALUE;
      IdArticleMin = Integer.MIN_VALUE;
    } else {
      IdArticleMax = (Integer) selectedArticle.get("id");
      IdArticleMin = (Integer) selectedArticle.get("id");
    }
    Map selectedTypeArticle = (Map) request.getContext().get("type");
    if (selectedTypeArticle == null) {
      IdTypeArticleMax = Integer.MAX_VALUE;
      IdTypeArticleMin = Integer.MIN_VALUE;
    } else {
      IdTypeArticleMax = (Integer) selectedTypeArticle.get("id");
      IdTypeArticleMin = (Integer) selectedTypeArticle.get("id");
    }
    Map selectedpervenance = (Map) request.getContext().get("pervenance");
    if (selectedpervenance == null) {
      IdPrevenanceMax = Integer.MAX_VALUE;
      IdPrevenanceMin = Integer.MIN_VALUE;
    } else {
      IdPrevenanceMax = (Integer) selectedpervenance.get("id");
      IdPrevenanceMin = (Integer) selectedpervenance.get("id");
    }

    Date dateDu = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuStr);
    Date dateAu = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuStr);

    String name = "PrintBilanEntrees";
    User user = AuthUtils.getUser();
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.bilan_entrees_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("DateDu", dateDu)
              .addParam("DateAu", dateAu)
              .addParam("IdArticleMin", IdArticleMin)
              .addParam("IdArticleMax", IdArticleMax)
              .addParam("IdTypeArticleMax", IdTypeArticleMax)
              .addParam("IdTypeArticleMin", IdTypeArticleMin)
              .addParam("IdPrevenanceMax", IdPrevenanceMax)
              .addParam("IdPrevenanceMin", IdPrevenanceMin)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintBilanSorties(ActionRequest request, ActionResponse response)
      throws ParseException {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    String dateDuStr = null;
    String dateAuStr = null;
    Integer IdArticleMax = Integer.MAX_VALUE;
    Integer IdArticleMin = Integer.MIN_VALUE;
    Integer IdTypeArticleMax = Integer.MAX_VALUE;
    Integer IdTypeArticleMin = Integer.MIN_VALUE;
    Integer IdBeneficieMax = Integer.MAX_VALUE;
    Integer IdBeneficieMin = Integer.MIN_VALUE;

    dateDuStr = (String) request.getContext().get("du");
    if (StringUtils.isBlank(dateDuStr)) dateDuStr = "0001-01-01";
    dateAuStr = (String) request.getContext().get("au");
    if (StringUtils.isBlank(dateAuStr)) dateAuStr = "9999-01-01";
    Map selectedArticle = (Map) request.getContext().get("article");
    if (selectedArticle == null) {
      IdArticleMax = Integer.MAX_VALUE;
      IdArticleMin = Integer.MIN_VALUE;
    } else {
      IdArticleMax = (Integer) selectedArticle.get("id");
      IdArticleMin = (Integer) selectedArticle.get("id");
    }
    Map selectedTypeArticle = (Map) request.getContext().get("type");
    if (selectedTypeArticle == null) {
      IdTypeArticleMax = Integer.MAX_VALUE;
      IdTypeArticleMin = Integer.MIN_VALUE;
    } else {
      IdTypeArticleMax = (Integer) selectedTypeArticle.get("id");
      IdTypeArticleMin = (Integer) selectedTypeArticle.get("id");
    }
    Map selectedbeneficie = (Map) request.getContext().get("beneficie");
    if (selectedbeneficie == null) {
      IdBeneficieMax = Integer.MAX_VALUE;
      IdBeneficieMin = Integer.MIN_VALUE;
    } else {
      IdBeneficieMax = (Integer) selectedbeneficie.get("id");
      IdBeneficieMin = (Integer) selectedbeneficie.get("id");
    }

    Date dateDu = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuStr);
    Date dateAu = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuStr);

    String name = "PrintBilanSorties";
    User user = AuthUtils.getUser();
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.bilan_sorties_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("DateDu", dateDu)
              .addParam("DateAu", dateAu)
              .addParam("IdArticleMin", IdArticleMin)
              .addParam("IdArticleMax", IdArticleMax)
              .addParam("IdTypeArticleMax", IdTypeArticleMax)
              .addParam("IdTypeArticleMin", IdTypeArticleMin)
              .addParam("IdBeneficieMax", IdBeneficieMax)
              .addParam("IdBeneficieMin", IdBeneficieMin)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintOperationEarchif(ActionRequest request, ActionResponse response)
      throws ParseException {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    String dateDuStr = null;
    String dateAuStr = null;
    dateDuStr = (String) request.getContext().get("du");
    if (StringUtils.isBlank(dateDuStr)) dateDuStr = "0001-01-01";
    dateAuStr = (String) request.getContext().get("au");
    if (StringUtils.isBlank(dateAuStr)) dateAuStr = "9999-01-01";

    Date dateDu = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuStr);
    Date dateAu = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuStr);

    String name = "PrintBilanSorties";
    User user = AuthUtils.getUser();
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.operationE_archif_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("DateDu", dateDu)
              .addParam("dateAu", dateAu)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }

  public void PrintOperationSarchif(ActionRequest request, ActionResponse response)
      throws ParseException {
    String format_print = (String) request.getContext().get("typeImprime");
    if (format_print == null) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("pdf")) format_print = ReportSettings.FORMAT_PDF;
    if (format_print.equals("xlsx")) format_print = ReportSettings.FORMAT_XLSX;
    if (format_print.equals("docx")) format_print = ReportSettings.FORMAT_DOCX;

    String dateDuStr = null;
    String dateAuStr = null;
    dateDuStr = (String) request.getContext().get("du");
    if (StringUtils.isBlank(dateDuStr)) dateDuStr = "0001-01-01";
    dateAuStr = (String) request.getContext().get("au");
    if (StringUtils.isBlank(dateAuStr)) dateAuStr = "9999-01-01";

    Date dateDu = new SimpleDateFormat("yyyy-MM-dd").parse(dateDuStr);
    Date dateAu = new SimpleDateFormat("yyyy-MM-dd").parse(dateAuStr);

    String name = "PrintBilanSorties";
    User user = AuthUtils.getUser();
    String UrlLogo =
        this.getClass()
            .getClassLoader()
            .getResource("../../img/logo-ar.png")
            .toString()
            .replace("%20", " ");

    try {
      String fileLink =
          ReportFactory.createReport(IReport.operationS_archif_report, name)
              .addParam("IdEtablissement", user.getEtablissementSelectionnee().getId())
              .addParam("UrlLogo", UrlLogo)
              .addParam("DateDu", dateDu)
              .addParam("dateAu", dateAu)
              .addFormat(format_print)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "false").map());

    } catch (Exception e) {
      response.setAlert(I18n.get("error") + e.getMessage());
    }
  }
}
