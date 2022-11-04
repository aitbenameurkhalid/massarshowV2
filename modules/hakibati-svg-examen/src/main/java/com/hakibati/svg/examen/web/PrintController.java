package com.hakibati.svg.examen.web;

import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.hakibati.svg.examen.db.repo.CentreExamRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.setting.service.excel.ExcelLogWriter;
import com.setting.service.report.AxelorException;
import com.setting.service.report.ReportFactory;
import com.setting.service.report.ReportSettings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class PrintController {
  public void printListFr(ActionRequest request, ActionResponse response) {
    String name = "Badge-Ar";
    try {
      String fileLink =
          ReportFactory.createReport(IReport.BADGE_AR, name)
              //                      .addParam("mrpId", mrp.getId())
              //                                        .addParam("Locale",
              // ReportSettings.getPrintingLocale(null))
              .addFormat(ReportSettings.FORMAT_PDF)
              .generate()
              .getFileLink();

      response.setView(
          ActionView.define(name).add("html", fileLink).param("download", "true").map());

    } catch (Exception e) {
      response.setAlert("error " + e.getMessage());
    }
  }

  @Inject private ExcelLogWriter logWriter;
  @Inject private MetaFiles metaFiles;
  @Inject private CentreExamRepository centreExamRepository;

  public void exportProfPDF(ActionRequest request, ActionResponse response)
      throws AxelorException, IOException {

    File exportFile = File.createTempFile("DATA", ".pdf");
    Document document = new Document();
    PdfPTable pdfPTable = new PdfPTable(1);
    FileOutputStream outStream = new FileOutputStream(exportFile);
    try {
      PdfWriter.getInstance(document, outStream);
      document.open();
      document.add(new Paragraph("1ere element"));
      document.add(new Paragraph("2eme  element"));
      Font font = new Font();
      URL urlFont =
          this.getClass().getClassLoader().getResource("../../lib/font-awesome/fonts/ANDLSO.TTF");
      BaseFont baseFont =
          BaseFont.createFont(urlFont.getPath().replace("%20", " "), BaseFont.IDENTITY_H, true);
      Font font2 = new Font(baseFont, 20);
      font.setSize(20);
      PdfPCell pdfCell;
      pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
      PdfPCell cell = new PdfPCell(new Phrase("gonr arial arab", font2));
      pdfPTable.addCell(cell);
      cell = new PdfPCell(new Phrase("خالد ايت بن عمؤو نصوصا، أي منقولة", font2));
      pdfPTable.addCell(cell);
      cell = new PdfPCell(new Phrase("fhgjhglkjhjghfhf", font2));
      pdfPTable.addCell(cell);
      //   Paragraph paragraph;
      //  paragraph=new Paragraph("khalid ait ben ameur ",new Font(BaseFont.createFont(),10,0));
      //    document.add(paragraph);
      document.add(pdfPTable);
      //            PdfPCell cell = new PdfPCell();
      //            cell.addElement(new Phrase("Created Date : ", grayFont));
      //            cell.addElement(new Phrase(theDate, blackFont));
      //      ClassLoader classLoader = this.getClass().getClassLoader();
      //      excelFile = new File(classLoader.getResource(fileName).getFile());
      URL str = this.getClass().getClassLoader().getResource("../../img/user.png");
      Image image = Image.getInstance(str.getPath().replace("%20", " "));
      document.add(image);
      document.close();
      MetaFile metaFile = metaFiles.upload(exportFile);
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
      //            response.setAlert("pas error");
      //        file.delete();
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      // MetaFiles.getPath(metaFile).toFile().delete();
    } catch (DocumentException e) {
      response.setError(e.toString());
    }
  }
}
