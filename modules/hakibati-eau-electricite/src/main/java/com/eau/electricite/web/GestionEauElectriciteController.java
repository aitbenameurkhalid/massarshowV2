package com.eau.electricite.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.eau.electricite.service.*;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.hakibati.eau.electricite.db.GestionEauElectricite;
import com.hakibati.eau.electricite.db.repo.GestionEauElectriciteRepository;
import java.io.File;
import java.io.IOException;

public class GestionEauElectriciteController {
  @Inject private MetaFiles metaFiles;

  @Transactional
  public void OnsaveGestion(ActionRequest request, ActionResponse response) {

    GestionEauElectricite gestionEauElectriciteSelect =
        request.getContext().asType(GestionEauElectricite.class);
    GestionEauElectricite gestionEauElectricite =
        Beans.get(GestionEauElectriciteRepository.class).find(gestionEauElectriciteSelect.getId());
    //    response.setAlert(gestionEauElectricite.getCodeGresa());
    GestionEauElectriciteService.savgestionEauAlectricite(gestionEauElectricite);
  }

  @Transactional
  public void OpenInfoEauElectricite(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    if (gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) gestionEauElectriciteRepository.save(new GestionEauElectricite());

    Long IdParametre =
        gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne()
            .getId();

    if (IdParametre == null) return;

    response.setView(
        ActionView.define(I18n.get("Information"))
            .model("com.hakibati.eau.electricite.db.GestionEauElectricite")
            .add("form", "GestionEauElectricite-form")
            .domain("self.etablissement = " + user.getEtablissementSelectionnee())
            .param("forceEdit", "true")
            .context("_showRecord", IdParametre)
            .map());
  }

  @Transactional
  public void OpenConsommationEau(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    if (gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) gestionEauElectriciteRepository.save(new GestionEauElectricite());

    Long IdParametre =
        gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne()
            .getId();

    if (IdParametre == null) return;
    response.setView(
        ActionView.define(I18n.get("Information"))
            .model("com.hakibati.eau.electricite.db.GestionEauElectricite")
            .add("form", "GestionEau-form")
            .domain("self.etablissement = " + user.getEtablissementSelectionnee())
            .param("forceEdit", "true")
            .context("_showRecord", IdParametre)
            .map());
  }

  @Transactional
  public void OpenControleEau(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    if (gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) gestionEauElectriciteRepository.save(new GestionEauElectricite());

    Long IdParametre =
        gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne()
            .getId();

    if (IdParametre == null) return;
    response.setView(
        ActionView.define(I18n.get("Information"))
            .model("com.hakibati.eau.electricite.db.GestionEauElectricite")
            .add("form", "ControlEau-form")
            .domain("self.etablissement = " + user.getEtablissementSelectionnee())
            .param("forceEdit", "true")
            .context("_showRecord", IdParametre)
            .map());
  }

  @Transactional
  public void OpenConsommationElectricite(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    if (gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) gestionEauElectriciteRepository.save(new GestionEauElectricite());

    Long IdParametre =
        gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne()
            .getId();

    if (IdParametre == null) return;
    response.setView(
        ActionView.define(I18n.get("Information"))
            .model("com.hakibati.eau.electricite.db.GestionEauElectricite")
            .add("form", "GestionElectricite-form")
            .domain("self.etablissement = " + user.getEtablissementSelectionnee())
            .param("forceEdit", "true")
            .context("_showRecord", IdParametre)
            .map());
  }

  @Transactional
  public void OpenControleElectricite(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return;

    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    if (gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) gestionEauElectriciteRepository.save(new GestionEauElectricite());

    Long IdParametre =
        gestionEauElectriciteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne()
            .getId();

    if (IdParametre == null) return;
    response.setView(
        ActionView.define(I18n.get("Information"))
            .model("com.hakibati.eau.electricite.db.GestionEauElectricite")
            .add("form", "ControlElectricite-form")
            .domain("self.etablissement = " + user.getEtablissementSelectionnee())
            .param("forceEdit", "true")
            .context("_showRecord", IdParametre)
            .map());
  }

  public void ExportConsommationEau(ActionRequest request, ActionResponse response)
      throws IOException {
    String dateDu = (String) request.getContext().get("_date_du");
    String dateFin = (String) request.getContext().get("_date_fin");
    ConsommationEauService gestionEauElectriciteService = new ConsommationEauService();
    File file = gestionEauElectriciteService.getFileListConsommationEau(dateDu, dateFin);

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

  public void ExportConsommationElectricite(ActionRequest request, ActionResponse response)
      throws IOException {

    String dateDu = (String) request.getContext().get("_date_du");
    String dateFin = (String) request.getContext().get("_date_fin");
    ConsommationElectriciteService gestionEauElectriciteService =
        new ConsommationElectriciteService();
    File file = gestionEauElectriciteService.getFileListConsommationElectricite(dateDu, dateFin);
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

  public void ExportControleEau(ActionRequest request, ActionResponse response) throws IOException {
    String dateDu = (String) request.getContext().get("_date_du");
    String dateFin = (String) request.getContext().get("_date_fin");
    ControleEauService controleEauService = new ControleEauService();
    File file = controleEauService.getFileListControlEau(dateDu, dateFin);
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

  public void ExportControleElectricite(ActionRequest request, ActionResponse response)
      throws IOException {
    //    response.setAlert("export controle electricite");
    String dateDu = (String) request.getContext().get("_date_du");
    String dateFin = (String) request.getContext().get("_date_fin");
    ControlElectriciteService controlElectriciteService = new ControlElectriciteService();
    File file = controlElectriciteService.getFileListControlElectricite(dateDu, dateFin);
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
