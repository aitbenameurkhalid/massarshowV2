package com.setting.web;

import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.setting.service.AllpersonnelService;
import java.io.File;
import java.io.IOException;

public class AllpersonnelController {

  @Transactional
  public void ImportDataMassar(ActionRequest request, ActionResponse response) throws IOException {
    String typeImport = (String) request.getContext().get("typeimport");

    boolean ensPrimG = false;
    boolean ensPrimO = false;
    boolean ensColG = false;
    boolean ensColO = false;
    boolean ensQualG = false;
    boolean ensQualO = false;
    boolean ensQualT = false;
    boolean ensPrescoM = false;
    boolean ensPrescoC = false;
    boolean ensBts = false;
    boolean ensCPGE = false;

    if (typeImport.contains("ensPrimG")) ensPrimG = true;
    if (typeImport.contains("ensPrimO")) ensPrimO = true;

    if (typeImport.contains("ensColG")) ensColG = true;
    if (typeImport.contains("ensColO")) ensColO = true;

    if (typeImport.contains("ensQualG")) ensQualG = true;
    if (typeImport.contains("ensQualO")) ensQualO = true;
    if (typeImport.contains("ensQualT")) ensQualT = true;

    if (typeImport.contains("ensPrescoM")) ensPrescoM = true;
    if (typeImport.contains("ensPrescoC")) ensPrescoC = true;
    if (typeImport.contains("ensBts")) ensBts = true;
    if (typeImport.contains("ensCPGE")) ensCPGE = true;

    String typeEtab = "";
    if (typeImport.contains("Public")) typeEtab = "Public";
    if (typeImport.contains("ENF")) typeEtab = "ENF";
    if (typeImport.contains("Prive")) typeEtab = "Prive";
    if (typeImport.contains("PART")) typeEtab = "PART";
    if (typeImport.contains("NS")) typeEtab = "NS";
    //        response.setAlert(typeImport);
    AllpersonnelService allpersonnelService = new AllpersonnelService();
    String res =
        allpersonnelService.ImportDataProfMassar(
            "ait.ben.ameur.khalid@taalim.ma",
            "k1280909@",
            typeEtab,
            ensPrimG,
            ensPrimO,
            ensColG,
            ensColO,
            ensQualG,
            ensQualO,
            ensQualT,
            ensPrescoM,
            ensPrescoC,
            ensBts,
            ensCPGE);

    response.setAlert(res);
  }

  @Transactional
  public void ImportDataEtabMassar(ActionRequest request, ActionResponse response)
      throws IOException {
    AllpersonnelService allpersonnelService = new AllpersonnelService();
    String res =
        allpersonnelService.ImportDataEtabMassar("ait.ben.ameur.khalid@taalim.ma", "k1280909@");
    response.setAlert(res);
    //    response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
  }

  //  ExporDataMassarExcel
  @Inject private MetaFiles metaFiles;

  public void ExporDataMassarExcel(ActionRequest request, ActionResponse response)
      throws IOException {
    AllpersonnelService allpersonnelService = new AllpersonnelService();
    String direction = "إقليم: الخميسات";
    File file = allpersonnelService.getFileListPersonnelsExcel(direction);

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
}
