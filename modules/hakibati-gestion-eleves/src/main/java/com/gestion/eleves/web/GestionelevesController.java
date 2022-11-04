package com.gestion.eleves.web;

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
import com.gestion.eleves.service.EleveService;
import com.gestion.eleves.service.GestionelevesService;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.hakibati.gestion.eleves.db.*;
import com.hakibati.gestion.eleves.db.repo.AnneScolaireRepository;
import com.hakibati.gestion.eleves.db.repo.GestionelevesRepository;
import com.setting.service.PermissionEtablissemntService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
public class GestionelevesController {
  @Inject GestionelevesRepository gestionelevesRepository;

  public void OpenGestionEleve(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-exterieur-donnees-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenGestionEleveNotes(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-notes-donnees-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenPrintEleve(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-print-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void deleteAllData(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (!user.getGroup().getCode().equals("admins")
        && !user.getGroup().getCode().equals("etablissementAdmin")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
      return;
    }

    Gestioneleves gestionelevesId = request.getContext().asType(Gestioneleves.class);
    Map AnneScolairSelectMap = (Map) request.getContext().get("anneScolair");
    if (AnneScolairSelectMap == null) return;
    long anneScolaireId = (Integer) AnneScolairSelectMap.get("id");
    AnneScolaire anneScolaireSelect = Beans.get(AnneScolaireRepository.class).find(anneScolaireId);
    Gestioneleves gestioneleves =
        Beans.get(GestionelevesRepository.class).find(gestionelevesId.getId());

    List<NotesEleve> notesEleveLIst =
        gestioneleves.getNotes().stream()
            .filter(
                e ->
                    e.getEleve() != null
                        && e.getEleve().getAnneeScolaire() != null
                        && e.getEleve().getAnneeScolaire() == anneScolaireSelect)
            .collect(Collectors.toList());
    gestioneleves.getNotes().removeAll(notesEleveLIst);

    List<Eleve> eleveList =
        gestioneleves.getEleves().stream()
            .filter(e -> e.getAnneeScolaire() != null && e.getAnneeScolaire() == anneScolaireSelect)
            .collect(Collectors.toList());
    gestioneleves.getEleves().removeAll(eleveList);

    List<AffectationEnseignant> affectationEnseignantList =
        gestioneleves.getAffectationEnseignants().stream()
            .filter(
                e ->
                    e.getClasse() != null
                        && e.getClasse().getAnneeScolaire() != null
                        && e.getClasse().getAnneeScolaire() == anneScolaireSelect)
            .collect(Collectors.toList());
    gestioneleves.getAffectationEnseignants().removeAll(affectationEnseignantList);

    List<Classe> classeList =
        gestioneleves.getClasses().stream()
            .filter(e -> e.getAnneeScolaire() != null && e.getAnneeScolaire() == anneScolaireSelect)
            .collect(Collectors.toList());
    gestioneleves.getClasses().removeAll(classeList);

    List<Niveau> niveauList =
        gestioneleves.getNiveau().stream()
            .filter(e -> e.getAnneeScolaire() != null && e.getAnneeScolaire() == anneScolaireSelect)
            .collect(Collectors.toList());
    gestioneleves.getNiveau().removeAll(niveauList);

    response.setAlert(I18n.get("L'opération a réussi"));
  }

  public void OpenGestionEleveInternat(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-internat-donnees-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenGestionEleveArchive(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-archive-donnees-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenGestionEleveInternatsuivi(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-internat-suivi-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenParametresEleve(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "gestioneleves-exterieur-parametres-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .param("showArchived", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenGestionEleveGroupe(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Distribution Pr groupe"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-exterieur-groupe-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .param("showArchived", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void OpenGestionEleveInternatArchive(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    response.setView(
        ActionView.define(("Gestion fichiers eleves internat archive"))
            .model("com.hakibati.gestion.eleves.db.Gestioneleves")
            .add("form", "eleve-internat-archive-form")
            .domain(
                "self.id = "
                    + gestionelevesRepository
                        .all()
                        .filter("etablissement = ?", user.getEtablissementSelectionnee())
                        .fetchOne()
                        .getId())
            .param("forceEdit", "true")
            .param("showArchived", "true")
            .context(
                "_showRecord",
                gestionelevesRepository
                    .all()
                    .filter("etablissement = ?", user.getEtablissementSelectionnee())
                    .fetchOne()
                    .getId())
            .map());
  }

  public void ImportEleveMassar(ActionRequest request, ActionResponse response) throws IOException {
    if (!PermissionEtablissemntService.getvalideModuleEtablissement(
        IReport.CODE_OF_MODULE_ELEVE_EXTERIEUR_PERMISSION)) {
      MetaFile metaFile =
          Beans.get(MetaFileRepository.class)
              .find(
                  Long.valueOf(
                      ((Map) request.getContext().get("imporelevetFile")).get("id").toString()));
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      MetaFiles.getPath(metaFile).toFile().delete();
      PermissionEtablissemntService permissionEtablissemntService =
          new PermissionEtablissemntService();
      permissionEtablissemntService.ShowEtablissementActivation(request, response);
      return;
    }

    Gestioneleves gestionelevesgetId = request.getContext().asType(Gestioneleves.class);
    Long resultaId = (Long) request.getContext().get("id");
    String typeListImport = (String) request.getContext().get("typeListImport");

    Gestioneleves gestioneleves =
        Beans.get(GestionelevesRepository.class).find(gestionelevesgetId.getId());

    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("imporelevetFile")).get("id").toString()));
    EleveService eleveService = new EleveService();
    String res = eleveService.importListEleveMassar(metaFile, gestioneleves, typeListImport);
    MetaFiles.getPath(metaFile).toFile().delete();
    response.setAlert(res);
  }

  public void ImportTuteurMassar(ActionRequest request, ActionResponse response)
      throws IOException {
    if (!PermissionEtablissemntService.getvalideModuleEtablissement(
        IReport.CODE_OF_MODULE_ELEVE_EXTERIEUR_PERMISSION)) {
      MetaFile metaFile =
          Beans.get(MetaFileRepository.class)
              .find(
                  Long.valueOf(
                      ((Map) request.getContext().get("imporelevetFile")).get("id").toString()));
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      MetaFiles.getPath(metaFile).toFile().delete();
      PermissionEtablissemntService permissionEtablissemntService =
          new PermissionEtablissemntService();
      permissionEtablissemntService.ShowEtablissementActivation(request, response);
      return;
    }

    Gestioneleves gestioneleves = request.getContext().asType(Gestioneleves.class);

    MetaFile metaFile =
        Beans.get(MetaFileRepository.class)
            .find(
                Long.valueOf(
                    ((Map) request.getContext().get("importuteurtFile")).get("id").toString()));
    EleveService eleveService = new EleveService();
    String res = eleveService.importTuteurEleveMassar(metaFile, gestioneleves);
    MetaFiles.getPath(metaFile).toFile().delete();
    response.setAlert(res);
  }

  public void generateGroupe(ActionRequest request, ActionResponse response) throws IOException {
    Map classeMap = (Map) request.getContext().get("_classeGroupe");
    Map niveauMap = (Map) request.getContext().get("_niveauGroupe");
    String maxOrdre = (String) request.getContext().get("_numbre1erG");
    Gestioneleves gestionelevesgetId = request.getContext().asType(Gestioneleves.class);

    int maxG1 = Integer.parseInt(maxOrdre);
    Gestioneleves gestioneleves =
        Beans.get(GestionelevesRepository.class).find(gestionelevesgetId.getId());

    GestionelevesService gestionelevesService = new GestionelevesService();
    String res =
        gestionelevesService.generateGroupeService(classeMap, niveauMap, maxG1, gestioneleves);

    response.setAttr("panel-dashlet-groupe-eleve", "refresh", true);
    //        response.setAlert(res);
  }
}
