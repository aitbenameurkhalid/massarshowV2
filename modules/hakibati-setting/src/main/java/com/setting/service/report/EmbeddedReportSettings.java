package com.setting.service.report; // package com.app.svg.examan.web.report;

import com.axelor.app.internal.AppFilter;
import com.axelor.inject.Beans;
import com.axelor.report.ReportGenerator;
import java.io.IOException;
import java.util.Locale;

import org.eclipse.birt.core.exception.BirtException;

public class EmbeddedReportSettings extends ReportSettings {

  public EmbeddedReportSettings(String rptdesign, String outputName) {

    super(rptdesign, outputName);
  }

  @Override
  public EmbeddedReportSettings generate() throws AxelorException {

    super.generate();

    try {

      final ReportGenerator generator = Beans.get(ReportGenerator.class);

//      this.output = generator.generate(rptdesign, format, params, AppFilter.getLocale());
      this.output = generator.generate(rptdesign, format, params, Locale.FRANCE);

      this.attach();

    } catch (IOException | BirtException e) {
      // throw new AxelorException(e, TraceBackRepository.CATEGORY_CONFIGURATION_ERROR);
    }

    return this;
  }
}
