package com.hakibati.svg.examen.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.*;
import com.hakibati.svg.examen.db.repo.*;
import com.hakibati.svg.examen.service.CentreExamService;
import com.hakibati.svg.examen.service.Generate;
import com.setting.service.PermissionEtablissemntService;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Transactional(rollbackOn = {Exception.class})
// @Transactional
public class CentreExamController {

  @Inject MatiereRepository matiereRepository;
  @Inject SalleFillierRepository salleFillierRepository;
  @Inject ContrainteRepository contrainteRepository;
  @Inject FillierRepository fillierRepository;
  @Inject CalendrierRepository calendrierRepository;
  @Inject DossierExamRepository dossierExamRepository;
  @Inject ResultatsRepository resultatsRepository;
  @Inject CentreExamRepository centreExamRepository;

  //  @Transactional(rollbackOn = {Exception.class})

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void url(ActionRequest request, ActionResponse response) {
    Prof metaFile =
        Beans.get(ProfRepository.class)
            .find(Long.valueOf(((Map) request.getContext().get("image")).get("id").toString()));
    //    File excelFile = MetaFiles.getPath(metaFile).toFile();
    Path file = MetaFiles.getPath(metaFile.getImage().toString());
    response.setError(file.getFileName() + " && " + file.getFileSystem());
  }

  public void OpenCenterExam(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (centreExamRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      CentreExam centreExam = new CentreExam();
      centreExam.setName(user.getEtablissementSelectionnee().getName());
      Beans.get(CentreExamRepository.class).save(centreExam);
    }

    response.setView(
        ActionView.define(("Centre Examan"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-form")
            .domain(
                "self.id = "
                    + centreExamRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context(
                "_showRecord",
                centreExamRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenMatiere(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("Centre Examan"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-matiere-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenFillier(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("Fillier"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-Fillier-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenSalles(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("Salles"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-Salles-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenSalleFillier(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("SalleFillier"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-SalleFillier-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenProf(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("Profs"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-Prof-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenCalendrier(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("Calendrier"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-Calendrier-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenPermanence(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("Permanence"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-Permanence-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenTempsNonDesponible(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("TempsNonDesponible"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-TempsNonDesponible-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenSalleNonDesponible(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("SalleNonDesponible"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-SalleNonDesponible-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenMaxFillierMatiere(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("MaxFillierMatiere"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-MaxFillierMatiere-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteProfs(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("ContrainteProfs"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteProfs-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteProf(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("ContrainteProf"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteProf-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteSalles(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("ContrainteSalles"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteSalles-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteSalle(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("ContrainteSalle"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteSalle-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteFilliers(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("ContrainteFilliers"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteFilliers-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteFillier(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("ContrainteFillier"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteFillier-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  public void OpenContrainteAll(ActionRequest request, ActionResponse response) {
    CentreExam CurrentCentreExam = getCurrentCentreExam();
    response.setView(
        ActionView.define(("OpenContrainteAll"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "CentreExam-ContrainteAll-form")
            .domain("self.id = " + CurrentCentreExam.getId())
            .param("forceEdit", "true")
            .param("show-toolbar", "false")
            .context("_showRecord", CurrentCentreExam.getId())
            .map());
  }

  private CentreExam getCurrentCentreExam() {
    User user = AuthUtils.getUser();
    if (centreExamRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      CentreExam centreExam = new CentreExam();
      centreExam.setName(user.getEtablissementSelectionnee().getName());
      Beans.get(CentreExamRepository.class).save(centreExam);
    }
    MiseAjourContraint();
    return centreExamRepository
        .all()
        .filter("etablissement = ?", user.getEtablissementSelectionnee())
        .fetchOne();
  }

  @Transactional
  private void MiseAjourContraint() {
    CentreExamService.MiseAjourContrainteProf();
    CentreExamService.MiseAjourContrainteSalle();
    CentreExamService.MiseAjourContrainteFillier();

    CentreExamService.MiseAjourContrainteCollectif();
  }

  public void OpenCenterExamGenerate(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (centreExamRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) Beans.get(CentreExamRepository.class).save(new CentreExam());
    response.setView(
        ActionView.define(("Centre Examan"))
            .model("com.hakibati.svg.examen.db.CentreExam")
            .add("form", "generation-form")
            .domain(
                "self.id = "
                    + centreExamRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("show-toolbar", "false")
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                centreExamRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void Generate(ActionRequest request, ActionResponse response) throws IOException {
    //    PermissionEtablissemntService permissionEtablissemntService =
    //        new PermissionEtablissemntService();
    if (PermissionEtablissemntService.getvalideModuleEtablissement(
        CodeContrainte.CODE_OF_MODULE_EXAMEN_PERMISSION)) {
      Long centerId = (Long) request.getContext().get("id");
      Generate generate = new Generate();
      String res = generate.generate(centerId, request, response);
      response.setReload(true);
    } else {
      PermissionEtablissemntService permissionEtablissemntService =
          new PermissionEtablissemntService();
      permissionEtablissemntService.ShowEtablissementActivation(request, response);
    }
  }

  public void checkedGenerat(ActionRequest request, ActionResponse response) throws IOException {
    response.setAlert("vous vous demare le generat");
    response.setReload(true);
  }

  @Transactional
  public void CreatNewProjet(ActionRequest request, ActionResponse response, String DemoEtab)
      throws IOException {

    Long centerId = (Long) request.getContext().get("id");
    User user = AuthUtils.getUser();
    //    try {
    // remove dossier examen
    //      CentreExam centreExamCurrent = Beans.get(CentreExamRepository.class).find(centerId);
    //    @Inject DossierExamRepository dossierExamRepository;

    //      for (DossierExam dossierExam : centreExamCurrent.getDossierExam()) {
    //        centreExamCurrent.removeDossierExam(dossierExam);
    //      }

    //    remove resultat
    List<Resultats> resultatsList =
        resultatsRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();
    for (Resultats element : resultatsList) {
      Beans.get(ResultatsRepository.class).remove(element);
    }
    //  remove dossier examen
    List<DossierExam> dossierExamList =
        dossierExamRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();
    for (DossierExam element : dossierExamList) {
      Beans.get(DossierExamRepository.class).remove(element);
    }

    List<Calendrier> calendrierListCurrent =
        calendrierRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    for (Calendrier calendrier : calendrierListCurrent) {
      Beans.get(CalendrierRepository.class).remove(calendrier);
    }
    // remove salleFilier
    List<SalleFillier> salleFillierListCurrent =
        salleFillierRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    for (SalleFillier salleFillier : salleFillierListCurrent) {
      Beans.get(SalleFillierRepository.class).remove(salleFillier);
    }
    // remove les contraintes
    List<Contrainte> contrainteListCurrent =
        contrainteRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    for (Contrainte contrainte : contrainteListCurrent) {
      Beans.get(ContrainteRepository.class).remove(contrainte);
    }
    // add matiere
    //    List<Calendrier> calendrierListCollege =
    //            calendrierRepository.all().filter("etablissement.name = ?", DemoEtab).fetch();

    //    List<Matiere> matiereListCollege =
    //          matiereRepository.all().filter("etablissement.name = ?", DemoEtab).fetch();

    List<Matiere> matiereListCurrent =
        matiereRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    //      for (Matiere matiere : matiereListCollege) {
    //        if (matiereListCurrent.stream().noneMatch(e -> e.getName().equals(matiere.getName())))
    // {
    //          Matiere matierecurrent = new Matiere();
    //          matierecurrent.setName(matiere.getName());
    //          matierecurrent.setCode(matiere.getCode());
    //          Beans.get(MatiereRepository.class).save(matierecurrent);
    //        }
    //      }
    //      List<Fillier> fillierListCollege =
    //          fillierRepository.all().filter("etablissement.name = ?", DemoEtab).fetch();
    List<Fillier> fillierListCurrent =
        fillierRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    //      for (Fillier fillier : fillierListCollege) {
    //        if (fillierListCurrent.stream().noneMatch(e -> e.getName().equals(fillier.getName())))
    // {
    //          Fillier fillierCurrent = new Fillier();
    //          fillierCurrent.setName(fillier.getName());
    //          fillierCurrent.setCode(fillier.getCode());
    //          Beans.get(FillierRepository.class).save(fillierCurrent);
    //        }
    //      }

    List<Calendrier> calendrierListCollege =
        calendrierRepository.all().filter("etablissement.name = ?", DemoEtab).fetch();

    for (Calendrier calendrier : calendrierListCollege) {
      if (matiereListCurrent.stream()
          .noneMatch(e -> e.getName().equals(calendrier.getMatiere().getName()))) {
        Matiere matierecurrent = new Matiere();
        matierecurrent.setName(calendrier.getMatiere().getName());
        matierecurrent.setCode(calendrier.getMatiere().getCode());
        matiereListCurrent.add(matierecurrent);
        Beans.get(MatiereRepository.class).save(matierecurrent);
      }
      if (fillierListCurrent.stream()
          .noneMatch(e -> e.getName().equals(calendrier.getFillier().getName()))) {
        Fillier fillierCurrent = new Fillier();
        fillierCurrent.setName(calendrier.getFillier().getName());
        fillierCurrent.setCode(calendrier.getFillier().getCode());
        fillierListCurrent.add(fillierCurrent);
        Beans.get(FillierRepository.class).save(fillierCurrent);
      }

      Calendrier calendrierCurrent = new Calendrier();
      calendrierCurrent.setAu(calendrier.getAu());
      calendrierCurrent.setDu(calendrier.getDu());
      calendrierCurrent.setFillier(
          fillierRepository
              .all()
              .filter(
                  "etablissement = ? AND name = ?",
                  user.getEtablissementSelectionnee(),
                  calendrier.getFillier().getName())
              .fetchOne());
      calendrierCurrent.setMatiere(
          matiereRepository
              .all()
              .filter(
                  "etablissement = ? AND name = ?",
                  user.getEtablissementSelectionnee(),
                  calendrier.getMatiere().getName())
              .fetchOne());
      calendrierCurrent.setDateExamine(calendrier.getDateExamine());
      Beans.get(CalendrierRepository.class).save(calendrierCurrent);
    }
    response.setAlert(I18n.get("Ce processus est terminé"));
    response.setReload(true);
    //    } catch (Exception e) {
    //      response.setError(e.getMessage());
    //    }
  }

  @Transactional(rollbackOn = {Exception.class})
  public void CreatNewProjetLycee(ActionRequest request, ActionResponse response)
      throws IOException {
    if (AuthUtils.getUser().getGroup().getCode().equals("demo")) {
      response.setAlert(I18n.get("Désolé, vous n'avez pas la permission de le faire"));
    } else {
      CreatNewProjet(request, response, "_DemoLycee_");
    }
  }

  @Transactional(rollbackOn = {Exception.class})
  public void CreatNewProjetCollege(ActionRequest request, ActionResponse response)
      throws IOException {
    if (AuthUtils.getUser().getGroup().getCode().equals("demo")) {
      response.setAlert(I18n.get("Désolé, vous n'avez pas la permission de le faire"));
    } else {
      CreatNewProjet(request, response, "_DemoCollege_");
    }
  }

  public void MiseAjourConvocationP(ActionRequest request, ActionResponse response)
      throws IOException {
    try {
      CentreExam centreExam =
          centreExamRepository.all().filter("etablissement.name = ?", "_DemoLycee_").fetchOne();
      response.setValue("enteteCovocationP", centreExam.getEnteteCovocationP());
      response.setValue("piedCovocationP", centreExam.getPiedCovocationP());
    } catch (Exception e) {
      response.setError(e.getMessage());
    }
  }

  public void MiseAjourConvocationL(ActionRequest request, ActionResponse response)
      throws IOException {
    try {
      CentreExam centreExam =
          centreExamRepository.all().filter("etablissement.name = ?", "_DemoLycee_").fetchOne();
      response.setValue("enteteCovocationL", centreExam.getEnteteCovocationL());
      response.setValue("piedCovocationL", centreExam.getPiedCovocationL());
    } catch (Exception e) {
      response.setError(e.getMessage());
    }
  }
}
