package com.setting.service.report;

// import com.axelor.apps.tool.exception.IExceptionMessage;
// import com.axelor.apps.tool.file.FileTool;

import com.axelor.i18n.I18n;
import com.google.common.base.Strings;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.net.URLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class URLService {

  static final int size = 1024;

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /**
   * Test la validité d'une url.
   *
   * @param url L'URL à tester.
   * @return
   */
  public static String notExist(String url) {

    if (Strings.isNullOrEmpty(url)) {
      return I18n.get(IExceptionMessage.URL_SERVICE_1);
    }

    try {
      URL fileURL = new URL(url);
      fileURL.openConnection().connect();
      return null;
    } catch (java.net.MalformedURLException ex) {
      ex.printStackTrace();
      return String.format(I18n.get(IExceptionMessage.URL_SERVICE_2), url);
    } catch (IOException ex) {
      ex.printStackTrace();
      return String.format(I18n.get(IExceptionMessage.URL_SERVICE_3), url);
    }
  }

  public static void fileUrl(
      File file, String fAddress, String localFileName, String destinationDir) throws IOException {

    try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
      int byteRead;
      int byteWritten = 0;
      byte[] buf = new byte[size];
      URL url = new URL(fAddress);
      URLConnection urlConnection = url.openConnection();
      InputStream inputStream = urlConnection.getInputStream();

      while ((byteRead = inputStream.read(buf)) != -1) {
        outputStream.write(buf, 0, byteRead);
        byteWritten += byteRead;
      }

      LOG.info("Downloaded Successfully.");
      LOG.debug("No of bytes", byteWritten);

    } catch (IOException ex) {

      LOG.error(ex.getMessage());
    }
  }

  public static File fileDownload(String fAddress, String destinationDir, String fileName)
      throws IOException {

    int slashIndex = fAddress.lastIndexOf('/');
    int periodIndex = fAddress.lastIndexOf('.');

    if (periodIndex >= 1 && slashIndex >= 0 && slashIndex < fAddress.length() - 1) {
      LOG.debug("Downloading file {} from {} to {}", fileName, fAddress, destinationDir);

      File file = FileTool.create(destinationDir, fileName);

      fileUrl(file, fAddress, fileName, destinationDir);

      return file;

    } else {
      LOG.error("Destination path or filename is not well formatted.");
      return null;
    }
  }

  public static void fileDownload(
      File file, String fAddress, String destinationDir, String fileName) throws IOException {

    int slashIndex = fAddress.lastIndexOf('/');
    int periodIndex = fAddress.lastIndexOf('.');

    if (periodIndex >= 1 && slashIndex >= 0 && slashIndex < fAddress.length() - 1) {
      LOG.debug("Downloading file {} from {} to {}", fileName, fAddress, destinationDir);
      fileUrl(file, fAddress, fileName, destinationDir);
    } else {
      LOG.error("Destination path or filename is not well formatted.");
    }
  }
}
