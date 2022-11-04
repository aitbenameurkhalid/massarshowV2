package com.hakibati.svg.examen.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.axelor.meta.db.repo.MetaFileRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.AllEtabMassar;
import com.hakibati.setting.db.Allpersonnel;
import com.hakibati.svg.examen.db.Contrainte;
import com.hakibati.svg.examen.db.Prof;
import com.hakibati.svg.examen.db.Resultats;
import com.hakibati.svg.examen.db.repo.ContrainteRepository;
import com.hakibati.svg.examen.db.repo.ProfRepository;
import com.hakibati.svg.examen.db.repo.ResultatsRepository;
import com.hakibati.svg.examen.service.ProfService;
import com.setting.service.AllpersonnelService;
import com.setting.service.UserService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

@Singleton
@Transactional
public class ProfController {
  private Logger log = LoggerFactory.getLogger(ProfController.class);

  String USER_ADMIN_GROUP = "admins";

  @Transactional
  public void deleteAllTeacher(ActionRequest request, ActionResponse response) {
    //        try {
    User user = AuthUtils.getUser();
    List<Contrainte> contrainteList =
        Beans.get(ContrainteRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    for (Contrainte contrainte : contrainteList)
      Beans.get(ContrainteRepository.class).remove(contrainte);
    List<Resultats> resultatsList =
        Beans.get(ResultatsRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();
    for (Resultats resultats : resultatsList)
      Beans.get(ResultatsRepository.class).remove(resultats);

    List<Prof> profListCurrent =
        Beans.get(ProfRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    for (Prof prof : profListCurrent) {
      Beans.get(ProfRepository.class).remove(prof);
    }
    //      response.setSignal("refresh-app", true);
    response.setAlert(I18n.get("Opération terminée"));

    //        } catch (Exception e) {
    //            response.setCanClose(true);
    //        }
  }

  public void ImportMassarshow(ActionRequest request, ActionResponse response) {
    String gresa = (String) request.getContext().get("_gresa");
    List<Allpersonnel> allpersonnelList = AllpersonnelService.getListPersonnels(gresa);
    AllEtabMassar allEtabMassar = AllpersonnelService.getCurrentEtabMassar(gresa);
    if (allpersonnelList.size() == 0) {
      response.setAlert(I18n.get("Aucun résultat trouvé"));
      return;
    }
    ProfService profService = new ProfService();
    String res = profService.AjoutProfsMassarshow(allpersonnelList, allEtabMassar);
    response.setAlert(I18n.get("Opération terminée"));
  }

  public void OnAjouteProf(ActionRequest request, ActionResponse response) {

    try {
      int maxProf = (int) request.getContext().get("_au");
      int minProf = (int) request.getContext().get("_du");
      String prefixe = (String) request.getContext().get("_prefixe");
      ProfService profService = new ProfService();
      String res = profService.AjoutProfs(minProf, maxProf, prefixe);
      response.setAlert(res);
    } catch (Exception e) {
      response.setAlert("error = " + e.getMessage());
    }
    response.setAlert(I18n.get("Opération terminée"));
    //    response.setSignal("refresh-app", true);
  }

  public void importProfExcel(ActionRequest request, ActionResponse response) throws IOException {
    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(((Map) request.getContext().get("importFile")).get("id").toString()));
    ProfService profService = new ProfService();
    String res = profService.importProfExcel(metaFile);
    MetaFiles.getPath(metaFile).toFile().delete();
    response.setAlert(res);
    response.setCanClose(true);
    response.setReload(true);
    //    response.setSignal("refresh-app", true);
    response.setAlert(I18n.get("Opération terminée"));
  }

  @Singleton
  @Transactional
  public void importProfEsise(ActionRequest request, ActionResponse response)
      throws ParserConfigurationException, SAXException {
    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importFileEsise")).get("id").toString()));

    ProfService profService = new ProfService();
    String res = profService.importProfEsise(metaFile);
    MetaFiles.getPath(metaFile).toFile().delete();
    response.setAlert(res);
    //    response.setSignal("refresh-app", true);
    response.setAlert(I18n.get("Opération terminée"));
  }

  @Inject private MetaFiles metaFiles;

  public void exportProfExcel(ActionRequest request, ActionResponse response) throws IOException {
    ProfService profService = new ProfService();
    File file = profService.exportProfExcel();

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

    response.setCanClose(true);
    file.delete();
    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
  }

  @Transactional
  public void DeleteProf(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (!UserService.HasPermission("perm.hakibati.svg.examen.Prof.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
      return;
    }
    Prof element = request.getContext().asType(Prof.class);

    DeleteProf(element);
    response.setReload(true);
  }

  @Transactional
  void DeleteProf(Prof element) {
    User user = AuthUtils.getUser();
    //            Prof element = request.getContext().asType(Prof.class);
    ProfRepository elemengRepository = Beans.get(ProfRepository.class);
    element = elemengRepository.find(element.getId());
    ContrainteRepository contrainteRepository = Beans.get(ContrainteRepository.class);
    ResultatsRepository resultatsRepository = Beans.get(ResultatsRepository.class);
    List<Contrainte> contrainteList =
        contrainteRepository.all().filter("prof = ?", element).fetch();
    for (Contrainte contrainte : contrainteList) contrainteRepository.remove(contrainte);

    contrainteList =
        contrainteRepository
            .all()
            .filter(
                "etablissement = ? AND (contrainte.code = ? OR contrainte.code = ? OR contrainte.code = ? OR contrainte.code = ? )",
                user.getEtablissementSelectionnee(),
                CodeContrainte.professeurs_permanences,
                CodeContrainte.horaires_pas_disponibles_un_professeur,
                CodeContrainte.salle_pas_disponibles_un_professeur,
                CodeContrainte.maximum_garde_matiere_un_prof)
            .fetch();

    for (Contrainte contrainte : contrainteList) contrainte.getProfs().remove(element);

    List<Resultats> resultatsList =
        resultatsRepository
            .all()
            .filter("etablissement = ? AND prof= ?", user.getEtablissementSelectionnee(), element)
            .fetch();
    for (Resultats resultats : resultatsList) resultats.setProf(null);
    if (element != null) {
      elemengRepository.remove(element);
      //                response.setReload(true);
    }
  }
}
