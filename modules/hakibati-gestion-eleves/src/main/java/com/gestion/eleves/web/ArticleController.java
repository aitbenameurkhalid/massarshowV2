package com.gestion.eleves.web;

import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.gestion.eleves.service.ArticleService;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Transactional
public class ArticleController {
  @Inject private MetaFiles metaFiles;

  @Singleton
  @Transactional
  public void exportListArticle(ActionRequest request, ActionResponse response) throws IOException {
    ArticleService articleService = new ArticleService();
    File file = articleService.getFileListInternatExcel();

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

    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
  }

  public void ImporttListArticle(ActionRequest request, ActionResponse response)
      throws IOException {

    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importListArticleFile"))
                        .get("id")
                        .toString()));
    ArticleService articleService = new ArticleService();
    String res = articleService.importListArticleInternat(metaFile);
    response.setAlert(res);
  }

  public void MiseAjourListArticle(ActionRequest request, ActionResponse response) {
    ArticleService articleService = new ArticleService();
    String res = articleService.MiseAjourListArticleInternat();
    response.setAlert(res);
  }
}
