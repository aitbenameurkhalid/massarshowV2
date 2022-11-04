package com.setting.service.report;

public class ReportFactory {

  public static ReportSettings createReport(String rptdesign, String outputName) {

    if (ReportSettings.useIntegratedEngine()) {

      return new EmbeddedReportSettings(rptdesign, outputName);

    } else {

      return new ExternalReportSettings(rptdesign, outputName);
    }
  }
}
