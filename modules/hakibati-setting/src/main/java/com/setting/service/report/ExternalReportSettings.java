package com.setting.service.report;

import com.axelor.app.AppSettings;
import com.axelor.app.internal.AppFilter;
import com.axelor.meta.MetaFiles;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalReportSettings extends ReportSettings {
  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected static String BIRT_PATH = "birt";
  protected String url = "";
  protected String birtViewerUrl = null;;

  public ExternalReportSettings(String rptdesign, String outputName) {

    super(rptdesign, outputName);

    this.addAxelorReportPath(rptdesign).addParam("__locale", AppFilter.getLocale().toString());
  }

  @Override
  public ExternalReportSettings generate() throws AxelorException {

    super.generate();

    try {
      this.getUrl();

      String urlNotExist = URLService.notExist(url.toString());
      if (urlNotExist != null) {
        //                throw new AxelorException(
        //                        TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
        //
        // I18n.get(com.axelor.apps.base.exceptions.IExceptionMessage.BIRT_EXTERNAL_REPORT_NO_URL),
        //                        birtViewerUrl);
      }

      final Path tmpFile = MetaFiles.createTempFile(null, "");

      this.output = tmpFile.toFile();

      URLService.fileDownload(this.output, url, "", outputName);

      this.attach();

    } catch (IOException ioe) {
      //            throw new AxelorException(ioe,
      // TraceBackRepository.CATEGORY_CONFIGURATION_ERROR);
    }

    return this;
  }

  public String getUrl() {

    addParam("__format", format);

    for (String param : params.keySet()) {

      try {
        this.url += this.computeParam(param);
      } catch (UnsupportedEncodingException e) {
        logger.error(e.getLocalizedMessage());
      }
    }

    return this.url;
  }

  private String computeParam(String param) throws UnsupportedEncodingException {

    return "&" + param + "=" + URLEncoder.encode(params.get(param).toString(), "UTF-8");
  }

  private ReportSettings addAxelorReportPath(String rptdesign) {

    AppSettings appsSettings = AppSettings.get();

    String defaultUrl = appsSettings.getBaseURL();
    defaultUrl = defaultUrl.substring(0, defaultUrl.lastIndexOf('/'));
    defaultUrl += "/" + BIRT_PATH;

    this.birtViewerUrl = appsSettings.get("axelor.report.engine", defaultUrl);

    String resourcePath = appsSettings.get("axelor.report.resource.path", "report");
    resourcePath = resourcePath.endsWith("/") ? resourcePath : resourcePath + "/";

    this.url += birtViewerUrl + "/frameset?__report=" + resourcePath + rptdesign;
    return this;
  }
}
