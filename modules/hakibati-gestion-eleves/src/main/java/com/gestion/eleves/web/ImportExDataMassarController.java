package com.gestion.eleves.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.hakibati.gestion.eleves.db.*;
import com.hakibati.gestion.eleves.db.repo.AnneScolaireRepository;
import com.hakibati.gestion.eleves.db.repo.GestionelevesRepository;
import com.setting.data.massar.*;
import com.setting.service.MassarMenGovService;
import com.setting.service.PermissionEtablissemntService;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
//import wslite.json.JSONException;

@Transactional
public class ImportExDataMassarController {
  @Inject GestionelevesRepository gestionelevesRepository;

  public void TestConnectionMassar(ActionRequest request, ActionResponse response)
      throws  IOException {
    //          convertit json to java class :  https://json2csharp.com/json-to-pojo
    //        response.setHidden("info-login", false);
    //        response.setHidden("panel-import", false);
    String loginMassar = (String) request.getContext().get("loginMassar");
    String passwordMassar = (String) request.getContext().get("passwordMassar");
    String cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    String infoLoginMassar = MassarMenGovService.GetInfoLoginMassar(cookie);

    if (infoLoginMassar == null) {
      response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
    } else {
      ObjectMapper mapperUserMassar = new ObjectMapper();
      ObjectMapper mapper = new ObjectMapper();
      UserMassar userMassar = mapperUserMassar.readValue(infoLoginMassar, UserMassar.class);

      response.setValue("NomArabe", userMassar.getNomArabe());
      response.setValue("NomLatine", userMassar.getNomLatine());
      response.setValue("CodeEtablissement", userMassar.getCodeEtablissement());
      String requestBodyEtabBycD_ETAB =
          "{\"operationName\":null,\"variables\":{},\"query\":\"fragment EtabFragment on ZEtab {\\n  cD_ETAB\\n  nOM_ETABA\\n  nOM_ETABL\\n  cD_COM\\n  ensPrimG\\n  ensPrimO\\n  ensColG\\n  ensColO\\n  ensQualG\\n  ensQualO\\n  ensQualT\\n  ensPrescoM\\n  ensPrescoC\\n  ensBts\\n  ensCPGE\\n  cD_etabr\\n  __typename\\n}\\n\\n{\\n  allEtabs(where: {cD_ETAB: \\\""
              + userMassar.getCodeEtablissement()
              + "\\\"}) {\\n    ...EtabFragment\\n    __typename\\n  }\\n}\\n\"}";
      String curentEtabData =
          MassarMenGovService.getDataMassar(cookie, requestBodyEtabBycD_ETAB, "evaluationgw");

      String requestBodyEtabBycD_ETABr =
          "{\"operationName\":null,\"variables\":{},\"query\":\"fragment EtabFragment on ZEtab {\\n  cD_ETAB\\n  nOM_ETABA\\n  nOM_ETABL\\n  cD_COM\\n  ensPrimG\\n  ensPrimO\\n  ensColG\\n  ensColO\\n  ensQualG\\n  ensQualO\\n  ensQualT\\n  ensPrescoM\\n  ensPrescoC\\n  ensBts\\n  ensCPGE\\n  cD_etabr\\n  __typename\\n}\\n\\n{\\n  allEtabs(where: {cD_etabr: \\\""
              + userMassar.getCodeEtablissement()
              + "\\\"}) {\\n    ...EtabFragment\\n    __typename\\n  }\\n}\\n\"}";
      String curentEtabrData =
          MassarMenGovService.getDataMassar(cookie, requestBodyEtabBycD_ETABr, "evaluationgw");

      DataMassar dataMassarInfoEtab = mapper.readValue(curentEtabData, DataMassar.class);
      //            DataMassar dataMassarInfoEtabr = mapper.readValue(curentEtabrData,
      // DataMassar.class);

      response.setValue(
          "nOM_ETABA", dataMassarInfoEtab.getData().getAllEtabs().get(0).getnOM_ETABA());
      response.setValue(
          "nOM_ETABL", dataMassarInfoEtab.getData().getAllEtabs().get(0).getnOM_ETABL());
      response.setHidden("info-login", false);
      response.setHidden("panel-import", false);
      // refresh anne scolair
      User user = AuthUtils.getUser();
      Gestioneleves gestioneleves =
          gestionelevesRepository
              .all()
              .filter("etablissement = ?", user.getEtablissementSelectionnee())
              .fetchOne();
      List<AnneScolaire> scolaireList = gestioneleves.getAnneScolaire();
      String requestBodyallAnneeScolaires =
          "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  allAnneeScolaires {\\n    idAnnee\\n    annee\\n    anneeMax\\n    anneeMin\\n    __typename\\n  }\\n}\\n\"}";
      String jsonBodyBodyallAnneeScolaires =
          MassarMenGovService.getDataMassar(cookie, requestBodyallAnneeScolaires, "evaluationgw");
      DataMassar dataMassarnneeScolairesList =
          mapper.readValue(jsonBodyBodyallAnneeScolaires, DataMassar.class);

      for (AnneeScolaireMassar anneeScolaireMassar :
          dataMassarnneeScolairesList.getData().getAllAnneeScolaires()) {
        AnneScolaire anneScolaireFind =
            scolaireList.stream()
                .filter(
                    e -> e.getName() != null && e.getName().equals(anneeScolaireMassar.getAnnee()))
                .findAny()
                .orElse(null);

        if (anneScolaireFind == null) {
          anneScolaireFind = new AnneScolaire();
          scolaireList.add(anneScolaireFind);
        }

        anneScolaireFind.setName(anneeScolaireMassar.getAnnee());
        anneScolaireFind.setAnneeMax((long) anneeScolaireMassar.getAnneeMax());
        anneScolaireFind.setAnneeMin((long) anneeScolaireMassar.getAnneeMin());
        anneScolaireFind.setIdAnnee((long) anneeScolaireMassar.getIdAnnee());
        anneScolaireFind.setGestioneleves(gestioneleves);
      }
    }
  }

  public void ImportAllDataMassar(ActionRequest request, ActionResponse response)
      throws  IOException {
    User user = AuthUtils.getUser();
    if (!PermissionEtablissemntService.getvalideModuleEtablissement(
        IReport.CODE_OF_MODULE_ELEVE_EXTERIEUR_PERMISSION)) {
      PermissionEtablissemntService permissionEtablissemntService =
          new PermissionEtablissemntService();
      permissionEtablissemntService.ShowEtablissementActivation(request, response);
      return;
    }
    String typeImport = (String) request.getContext().get("typeimport");

    Map AnneScolairSelectMap = (Map) request.getContext().get("anneScolair");
    long anneScolaireId = (Integer) AnneScolairSelectMap.get("id");
    AnneScolaire anneScolaireSelect = Beans.get(AnneScolaireRepository.class).find(anneScolaireId);
    StringBuilder strErrorImport = new StringBuilder();
    String loginMassar = (String) request.getContext().get("loginMassar");
    String passwordMassar = (String) request.getContext().get("passwordMassar");
    String cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    String infoLoginMassar = MassarMenGovService.GetInfoLoginMassar(cookie);
    if (infoLoginMassar == null) {
      response.setAlert(I18n.get("A server error occurred. Please contact the administrator."));
      return;
    }
    ObjectMapper mapper = new ObjectMapper();
    UserMassar userMassar = mapper.readValue(infoLoginMassar, UserMassar.class);
    Gestioneleves gestioneleves =
        gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne();

    gestioneleves.setAnneScolaireActuele(anneScolaireSelect);
    List<Classe> classeList = gestioneleves.getClasses();
    List<MatiereEleve> matiereEleveList = gestioneleves.getMatiere();
    List<Niveau> niveauList = gestioneleves.getNiveau();
    List<Eleve> eleveList = gestioneleves.getEleves();
    List<NotesEleve> notesEleveList = gestioneleves.getNotes();
    List<Enseignant> enseignantList = gestioneleves.getEnseignants();
    List<AffectationEnseignant> affectationEnseignantList =
        gestioneleves.getAffectationEnseignants();
    // hassan 2 bni mallal
    //  userMassar.setCodeEtablissement("07698S");
    //        wad amlil wahda
    //    userMassar.setCodeEtablissement("16685K");
    // sidi lmakhfi sidi addi
    //    userMassar.setCodeEtablissement("26267A");
    // hassan 2 tinjdad
    //    userMassar.setCodeEtablissement("09854K");

    // import  niveau in massar
    List<NiveauMassar> niveauMassarList = new ArrayList<>();
    try {

      if (typeImport.contains("classes")) {
        String requestBodyCYCLE =
            "{\n\"operationName\": null,\n\"variables\": {},\n  \"query\": \"{\\n  cycle {\\n    cD_CYCLE\\n    lA_CYCLE\\n    lL_CYCLE\\n    __typename\\n  }\\n}\\n\"\n        }";
        String jsonBodyCYCLE =
            MassarMenGovService.getDataMassar(cookie, requestBodyCYCLE, "evaluationgw");
        DataMassar dataMassarCYCLEList = mapper.readValue(jsonBodyCYCLE, DataMassar.class);

        for (CycleMassar cycleMassar : dataMassarCYCLEList.getData().getCycle()) {
          String codeCycl = cycleMassar.getcD_CYCLE();
          String requestBodyNiveauBycD_CYCLE =
              "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  allNefstat(where: {cD_CYCLE: \\\""
                  + codeCycl
                  + "\\\"}) {\\n    nefstat\\n    cD_CYCLE\\n    libformatAr\\n    libformatFr\\n    id_typeEnseignement\\n    suffix\\n    __typename\\n  }\\n}\\n\"}";
          String jsonBodyNiveauBycD_CYCLE =
              MassarMenGovService.getDataMassar(
                  cookie, requestBodyNiveauBycD_CYCLE, "evaluationgw");
          DataMassar dataMassarNiveauList =
              mapper.readValue(jsonBodyNiveauBycD_CYCLE, DataMassar.class);
          if (dataMassarNiveauList != null
              && dataMassarNiveauList.getData() != null
              && dataMassarNiveauList.getData().getAllNefstat() != null)
            niveauMassarList.addAll(dataMassarNiveauList.getData().getAllNefstat());
        }
      }
    } catch (Exception e) {
    }
    cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    // import matiere
    DataMassar dataMassMatiereList = new DataMassar();
    try {
      if (typeImport.contains("matieres")) {
        String requestBodyallMatiere =
            "{ \"operationName\": null, \"variables\": {}, \"query\": \"{\\n  allMatiere() {\\n    cd_matiere\\n    cd_unite\\n    matiereAr\\n    matiereFr\\n    __typename\\n  }\\n}\\n\"\n}";
        String jsonBodyBodyallMatiere =
            MassarMenGovService.getDataMassar(cookie, requestBodyallMatiere, "evaluationgw");
        dataMassMatiereList = mapper.readValue(jsonBodyBodyallMatiere, DataMassar.class);
      }
    } catch (Exception e) {
    }
    cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    // import Enseignant
    DataMassar dataMassEnseignantList = new DataMassar();
    DataMassar dataMassEnseignantList2 = new DataMassar();
    try {
      if (typeImport.contains("enseignant")) {
        String requestBodyallEnseignant =
            "{\"operationName\": null,\"variables\": {},\"query\": \"{\\n  enseignants(cdEtab: \\\""
                + userMassar.getCodeEtablissement()
                + "\\\") {\\n    idPersonnel\\n    nomAr\\n    nomCompletAr\\n    nomCompletFr\\n    nomFr\\n    cdMatiere\\n    ppr\\n    __typename\\n  }\\n}\\n\"\n}";
        String jsonBodyBodyallEnseignant =
            MassarMenGovService.getDataMassar(cookie, requestBodyallEnseignant, "evaluationgw");
        dataMassEnseignantList = mapper.readValue(jsonBodyBodyallEnseignant, DataMassar.class);
        String requestBodypersonnelsByEtabAndCycle =
            "{\n \"operationName\": null,\n  \"variables\": {},\n  \"query\": \"{\\n  personnelsByEtabAndCycle(cdEtab: \\\""
                + userMassar.getCodeEtablissement()
                + "\\\", cycle: \\\"\\\") {\\n    idPersonnel\\n    nomA\\n    prenomA\\n    nomL\\n    prenomL\\n    cdMatiere\\n    ppr\\n    __typename\\n  }\\n}\\n\"\n}";
        jsonBodyBodyallEnseignant =
            MassarMenGovService.getDataMassar(
                cookie, requestBodypersonnelsByEtabAndCycle, "evaluationgw");
        dataMassEnseignantList2 = mapper.readValue(jsonBodyBodyallEnseignant, DataMassar.class);
      }
    } catch (Exception e) {
    }
    cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    // import class in massar
    Long idAnnee = anneScolaireSelect.getIdAnnee();
    String requestBodyUserClassesBycdEtab =
        "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  userClasses(cdEtab: \\\""
            + userMassar.getCodeEtablissement()
            + "\\\", idAnnee: "
            + idAnnee
            + ") {\\n    idClasse\\n    libelleClasse\\n    nefstat\\n    __typename\\n  }\\n}\\n\"}";
    String jsonBodyUserClassesBycdEtab =
        MassarMenGovService.getDataMassar(cookie, requestBodyUserClassesBycdEtab, "evaluationgw");
    DataMassar dataMassarClassList =
        mapper.readValue(jsonBodyUserClassesBycdEtab, DataMassar.class);

    // import eleve and notes and affectation
    List<EleveMassar> eleveMassarListAll = new ArrayList<>();
    List<EleveMassar> eleveMassarListInscrit = new ArrayList<>();
    List<NotesMassar> notesMassarList = new ArrayList<>();
    List<AffectationEnseignantMassar> affectationEnseignantMassarList = new ArrayList<>();
    for (ClassMassar classMassar : dataMassarClassList.getData().getUserClasses()) {
      // import all eleves
      cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
      if (typeImport.contains("eleves")) {
        String requestBodyelevesByNefstatAndClass =
            "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  elevesByNefstatAndClass(nefstat: \\\""
                + classMassar.getNefstat()
                + "\\\", classe: \\\""
                + classMassar.getIdClasse()
                + "\\\", cdEtab: \\\""
                + userMassar.getCodeEtablissement()
                + " \\\", onlyInscrit: false) {\\n    id_eleve\\n   id_Genre\\n  dateNaisEleve\\n  lieu_naissance_Ar\\n  lieu_naissance_Fr\\n   codeEleve\\n    nomEleveAr\\n    nomEleveFr\\n    prenomEleveAr\\n    prenomEleveFr\\n    scolarite {\\n      id_classe\\n      orderClasse\\n      classe {\\n        libelleClasse\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
        String jsonBodyelevesByNefstatAndClass =
            MassarMenGovService.getDataMassar(
                cookie, requestBodyelevesByNefstatAndClass, "evaluationgw");
        DataMassar dataMassarEleveList =
            mapper.readValue(jsonBodyelevesByNefstatAndClass, DataMassar.class);
        if (dataMassarEleveList != null
            && dataMassarEleveList.getData() != null
            && dataMassarEleveList.getData().getElevesByNefstatAndClass() != null)
          eleveMassarListAll.addAll(dataMassarEleveList.getData().getElevesByNefstatAndClass());
        // import eleves inscrit
        String requestBodyelevesByNefstatAndClassInscrit =
            "{\"operationName\":null,\"variables\":{},\"query\":\"{\\n  elevesByNefstatAndClass(nefstat: \\\""
                + classMassar.getNefstat()
                + "\\\", classe: \\\""
                + classMassar.getIdClasse()
                + "\\\", cdEtab: \\\""
                + userMassar.getCodeEtablissement()
                + " \\\", onlyInscrit: true) {\\n    id_eleve\\n   id_Genre\\n  dateNaisEleve\\n  lieu_naissance_Ar\\n  lieu_naissance_Fr\\n   codeEleve\\n    nomEleveAr\\n    nomEleveFr\\n    prenomEleveAr\\n    prenomEleveFr\\n    scolarite {\\n      id_classe\\n      orderClasse\\n      classe {\\n        libelleClasse\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}";
        String jsonBodyelevesByNefstatAndClassInscrit =
            MassarMenGovService.getDataMassar(
                cookie, requestBodyelevesByNefstatAndClassInscrit, "evaluationgw");
        DataMassar dataMassarEleveListInscrit =
            mapper.readValue(jsonBodyelevesByNefstatAndClassInscrit, DataMassar.class);
        if (dataMassarEleveListInscrit != null
            && dataMassarEleveListInscrit.getData() != null
            && dataMassarEleveListInscrit.getData().getElevesByNefstatAndClass() != null)
          eleveMassarListInscrit.addAll(
              dataMassarEleveListInscrit.getData().getElevesByNefstatAndClass());
      }
      // import notes
      try {
        if (typeImport.contains("notes"))
          for (int idsession = 1; idsession <= 2; idsession++)
            for (int idControlContinu = 1; idControlContinu <= 5; idControlContinu++) {
              cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
              String requestBodyNotes =
                  "{\"operationName\": null,\"variables\": {}, \"query\": \"{\\n  notes(idClasse: \\\""
                      + classMassar.getIdClasse()
                      + "\\\", idSession: "
                      + idsession
                      + ", annee: "
                      + anneScolaireSelect.getIdAnnee()
                      + ", where: { idControlContinu: "
                      + idControlContinu
                      + "} ) {\\n    cdMatiere\\n    coefficient\\n    idClasse\\n    idControlContinu\\n    idSession\\n    idEleve\\n    note\\n    observation\\n    __typename\\n  }\\n}\\n\"\n}";
              String jsonBodyNotes =
                  MassarMenGovService.getDataMassar(cookie, requestBodyNotes, "evaluationgw");
              DataMassar dataMassarNotes = mapper.readValue(jsonBodyNotes, DataMassar.class);
              if (dataMassarNotes != null
                  && dataMassarNotes.getData() != null
                  && dataMassarNotes.getData().getNotes() != null)
                notesMassarList.addAll(dataMassarNotes.getData().getNotes());
            }
      } catch (Exception e) {
      }
      cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
      // import affectationEnseignants
      try {
        if (typeImport.contains("affectation")) {
          String requestBodyaffectationEnseignants =
              "{ \"operationName\": null, \"variables\": {}, \"query\": \"{\\n  affectationEnseignants(classeIds: [\\\""
                  + classMassar.getIdClasse()
                  + "\\\"]) {\\n    cdMatiere\\n    idClasse\\n    idPersonnel\\n    libelleClasse\\n    idAnnee\\n    nefstat\\n    __typename\\n  }\\n}\\n\"\n} ";
          String jsonBodyAffectationEnseignants =
              MassarMenGovService.getDataMassar(
                  cookie, requestBodyaffectationEnseignants, "evaluationgw");
          DataMassar dataMassarAffectationEnseignants =
              mapper.readValue(jsonBodyAffectationEnseignants, DataMassar.class);
          if (dataMassarAffectationEnseignants != null
              && dataMassarAffectationEnseignants.getData() != null
              && dataMassarAffectationEnseignants.getData().getAffectationEnseignants() != null)
            affectationEnseignantMassarList.addAll(
                dataMassarAffectationEnseignants.getData().getAffectationEnseignants());
        }
      } catch (Exception e) {
      }
    }

    // save matiere
    if (typeImport.contains("matieres"))
      if (dataMassMatiereList != null
          && dataMassMatiereList.getData() != null
          && dataMassMatiereList.getData().getAllMatiere() != null)
        for (MatiereMassar matiereMassar : dataMassMatiereList.getData().getAllMatiere()) {
          try {
            MatiereEleve matiereEleve =
                matiereEleveList.stream()
                    .filter(
                        e ->
                            e.getCd_matiere() != null
                                && e.getCd_matiere().equals(matiereMassar.getCd_matiere()))
                    .findAny()
                    .orElse(null);

            if (matiereEleve == null) {
              matiereEleve = new MatiereEleve();
              matiereEleveList.add(matiereEleve);
            }
            matiereEleve.setCd_matiere(matiereMassar.getCd_matiere());
            matiereEleve.setName(matiereMassar.getMatiereAr());
            matiereEleve.setMatiereFr(matiereMassar.getMatiereFr());
            matiereEleve.setGestioneleves(gestioneleves);

          } catch (Exception e) {
            strErrorImport.append("M:").append(matiereMassar.getCd_matiere()).append(";");
          }
        }
    // save enseignant

    if (typeImport.contains("enseignant")) {
      try {
        if (dataMassEnseignantList != null
            && dataMassEnseignantList.getData() != null
            && dataMassEnseignantList.getData().getEnseignants() != null)
          for (EnseignantMassar enseignantMassar :
              dataMassEnseignantList.getData().getEnseignants()) {
            try {
              Enseignant enseignant =
                  enseignantList.stream()
                      .filter(
                          e ->
                              e.getIdPersonnel() != null
                                  && e.getIdPersonnel() == enseignantMassar.getIdPersonnel())
                      .findAny()
                      .orElse(null);

              if (enseignant == null) {
                enseignant = new Enseignant();
                enseignantList.add(enseignant);
              }
              enseignant.setName(enseignantMassar.getNomCompletAr());
              enseignant.setNomCompletFr(enseignantMassar.getNomCompletFr());
              enseignant.setIdPersonnel(enseignantMassar.getIdPersonnel());
              enseignant.setPpr(enseignantMassar.getPpr());
              enseignant.setGestioneleves(gestioneleves);
              MatiereEleve matiereEleve =
                  matiereEleveList.stream()
                      .filter(
                          e ->
                              e.getCd_matiere() != null
                                  && Objects.equals(
                                      e.getCd_matiere(), enseignantMassar.getCdMatiere()))
                      .findAny()
                      .orElse(null);
              enseignant.setMatiere(matiereEleve);
            } catch (Exception e) {
              strErrorImport.append("Ppr:").append(enseignantMassar.getPpr()).append(";");
            }
          }
      } catch (Exception ignored) {
      }
      // tt personne
      try {
        if (dataMassEnseignantList2 != null
            && dataMassEnseignantList2.getData() != null
                & dataMassEnseignantList2.getData().getPersonnelsByEtabAndCycle() != null)
          for (PersonnelsByEtabMassar personnelsByEtabMassar :
              dataMassEnseignantList2.getData().getPersonnelsByEtabAndCycle()) {
            try {
              Enseignant enseignant =
                  enseignantList.stream()
                      .filter(
                          e ->
                              e.getIdPersonnel() != null
                                  && e.getIdPersonnel() == personnelsByEtabMassar.getIdPersonnel())
                      .findAny()
                      .orElse(null);

              if (enseignant == null) {
                enseignant = new Enseignant();
                enseignantList.add(enseignant);
              }
              enseignant.setName(
                  personnelsByEtabMassar.getNomA() + "" + personnelsByEtabMassar.getPrenomA());
              enseignant.setNomCompletFr(
                  personnelsByEtabMassar.getNomL() + " " + personnelsByEtabMassar.getPrenomL());
              enseignant.setIdPersonnel(personnelsByEtabMassar.getIdPersonnel());
              enseignant.setPpr(personnelsByEtabMassar.getPpr());
              enseignant.setGestioneleves(gestioneleves);
              MatiereEleve matiereEleve =
                  matiereEleveList.stream()
                      .filter(
                          e ->
                              e.getCd_matiere() != null
                                  && Objects.equals(
                                      e.getCd_matiere(), personnelsByEtabMassar.getCdMatiere()))
                      .findAny()
                      .orElse(null);
              enseignant.setMatiere(matiereEleve);
            } catch (Exception e) {
              strErrorImport.append("Ppr:").append(personnelsByEtabMassar.getPpr()).append(";");
            }
          }
      } catch (Exception ignored) {
      }
    }

    // save classe and niveau
    try {
      if (typeImport.contains("classes"))
        if (dataMassarClassList != null
            && dataMassarClassList.getData() != null
            && dataMassarClassList.getData().getUserClasses() != null)
          for (ClassMassar classMassar : dataMassarClassList.getData().getUserClasses()) {
            try {
              Classe classe =
                  classeList.stream()
                      .filter(
                          e ->
                              e.getName() != null
                                  && e.getAnneeScolaire() != null
                                  && e.getAnneeScolaire() == anneScolaireSelect
                                  && Objects.equals(e.getName(), classMassar.getLibelleClasse()))
                      .findAny()
                      .orElse(null);
              if (classe == null) {
                classe = new Classe();
                classeList.add(classe);
              }
              classe.setName(classMassar.getLibelleClasse());
              classe.setIdClasse(classMassar.getIdClasse());
              classe.setAnneeScolaire(anneScolaireSelect);
              classe.setGestioneleves(gestioneleves);

              Niveau niveau =
                  niveauList.stream()
                      .filter(
                          e ->
                              e.getAnneeScolaire() != null
                                  && e.getAnneeScolaire() == anneScolaireSelect
                                  && e.getCode() != null
                                  && e.getCode().equals(classMassar.getNefstat()))
                      .findAny()
                      .orElse(null);
              if (niveau == null) {
                niveau = new Niveau();
                niveauList.add(niveau);
                NiveauMassar niveauMassar =
                    niveauMassarList.stream()
                        .filter(
                            e ->
                                e.getNefstat() != null
                                    && e.getNefstat().equals(classMassar.getNefstat()))
                        .findAny()
                        .orElse(null);
                if (niveauMassar == null) {
                  niveauMassar = new NiveauMassar();
                  niveauMassar.setLibformatAr("0");
                  niveauMassar.setLibformatFr("0");
                  niveauMassar.setSuffix("0");
                  niveauMassar.setNefstat("0");
                  niveauMassar.setcD_CYCLE("0");
                }
                niveau.setName(niveauMassar.getLibformatAr());
                niveau.setNameFr(niveauMassar.getLibformatFr());
                niveau.setCode(niveauMassar.getNefstat());
                niveau.setCdCycle(niveauMassar.getcD_CYCLE());
                niveau.setSuffix(niveauMassar.getSuffix());
                niveau.setAnneeScolaire(anneScolaireSelect);
                niveau.setGestioneleves(gestioneleves);
              }
              classe.setNiveau(niveau);
            } catch (Exception e) {
              strErrorImport.append("C:").append(classMassar.getLibelleClasse()).append(";");
            }
          }
    } catch (Exception e) {
    }
    // archive eleve
    if (typeImport.contains("eleves"))
      for (Eleve eleve :
          eleveList.stream()
              .filter(
                  e -> e.getAnneeScolaire() != null && e.getAnneeScolaire() == anneScolaireSelect)
              .collect(Collectors.toList())) {
        eleve.setArchived(true);
      }
    //  save eleve All
    try {
      for (Classe classe :
          classeList.stream()
              .filter(
                  e -> e.getAnneeScolaire() != null && e.getAnneeScolaire() == anneScolaireSelect)
              .collect(Collectors.toList()))
        if (typeImport.contains("eleves"))
          for (EleveMassar eleveMassar :
              eleveMassarListAll.stream()
                  .filter(
                      e ->
                          e.getScolarite().getId_classe() != null
                              && Objects.equals(
                                  e.getScolarite().getId_classe(), classe.getIdClasse()))
                  .collect(Collectors.toList())) {
            try {
              Eleve eleve =
                  eleveList.stream()
                      .filter(
                          e ->
                              e.getAnneeScolaire() != null
                                  && e.getAnneeScolaire() == anneScolaireSelect
                                  && e.getCdMassar().equals(eleveMassar.getCodeEleve()))
                      .findAny()
                      .orElse(null);
              if (eleve == null) {
                eleve = new Eleve();
                eleveList.add(eleve);
              }
              eleve.setIdEleve((long) eleveMassar.getId_eleve());
              eleve.setCdMassar(eleveMassar.getCodeEleve());
              eleve.setNomAr(eleveMassar.getNomEleveAr());
              eleve.setNomFr(eleveMassar.getNomEleveFr());
              eleve.setPrenomAr(eleveMassar.getPrenomEleveAr());
              eleve.setPrenomFr(eleveMassar.getPrenomEleveFr());
              eleve.setIdclasse(eleveMassar.getScolarite().getOrderClasse());
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
              if (eleveMassar.getDateNaisEleve() != null)
                eleve.setDateNaissance(LocalDate.parse(eleveMassar.getDateNaisEleve(), formatter));
              eleve.setSexe(eleveMassar.getIdGenre() - 1);
              eleve.setLieuNaissanceAr(eleveMassar.getLieu_naissance_Ar());
              eleve.setLieuNaissanceFr(eleveMassar.getLieu_naissance_Fr());
              eleve.setAnneeScolaire(anneScolaireSelect);
              eleve.setClasse(classe);
              eleve.setArchived(false);
              eleve.setSituationActuelle(3);
              eleve.setGestioneleves(gestioneleves);
            } catch (Exception e) {
              strErrorImport.append("E:").append(eleveMassar.getCodeEleve()).append(";");
            }
          }
    } catch (Exception e) {
    }
    // save eleves inscrit
    try {
      if (typeImport.contains("eleves"))
        for (EleveMassar eleveMassar : eleveMassarListInscrit) {
          try {
            Eleve eleve =
                eleveList.stream()
                    .filter(
                        e ->
                            e.getAnneeScolaire() != null
                                && e.getAnneeScolaire() == anneScolaireSelect
                                && e.getCdMassar().equals(eleveMassar.getCodeEleve()))
                    .findAny()
                    .orElse(new Eleve());
            eleve.setSituationActuelle(0);
          } catch (Exception e) {
            strErrorImport.append("E2:").append(eleveMassar.getCodeEleve()).append(";");
          }
        }
    } catch (Exception e) {
    }
    // save notes
    try {
      if (typeImport.contains("notes"))
        for (NotesMassar notesMassar : notesMassarList) {
          try {
            NotesEleve notesEleve =
                notesEleveList.stream()
                    .filter(
                        e ->
                            e.getEleve() != null
                                && e.getEleve().getIdEleve() == notesMassar.getIdEleve()
                                && e.getEleve().getAnneeScolaire() == anneScolaireSelect
                                && e.getMatiere() != null
                                && Objects.equals(
                                    e.getMatiere().getCd_matiere(), notesMassar.getCdMatiere())
                                && e.getIdSession() == notesMassar.getIdSession()
                                && e.getIdControlContinu() == notesMassar.getIdControlContinu())
                    .findAny()
                    .orElse(null);

            if (notesEleve == null) {
              notesEleve = new NotesEleve();
              notesEleveList.add(notesEleve);
            }
            notesEleve.setIdControlContinu(notesMassar.getIdControlContinu());
            notesEleve.setCoefficient(notesMassar.getCoefficient());
            notesEleve.setNote(new BigDecimal(notesMassar.getNote()));
            notesEleve.setObservation(notesMassar.getObservation());
            notesEleve.setIdSession(notesMassar.getIdSession());
            notesEleve.setGestioneleves(gestioneleves);
            Eleve eleve =
                eleveList.stream()
                    .filter(
                        e ->
                            e.getIdEleve() != null
                                && e.getAnneeScolaire() != null
                                && e.getAnneeScolaire() == anneScolaireSelect
                                && e.getIdEleve() == notesMassar.getIdEleve())
                    .findAny()
                    .orElse(null);
            notesEleve.setEleve(eleve);
            if (eleve != null) notesEleve.setClasse(eleve.getClasse());
            MatiereEleve matiereEleve =
                matiereEleveList.stream()
                    .filter(
                        e ->
                            e.getCd_matiere() != null
                                && Objects.equals(e.getCd_matiere(), notesMassar.getCdMatiere()))
                    .findAny()
                    .orElse(null);
            notesEleve.setMatiere(matiereEleve);
          } catch (Exception e) {
            strErrorImport.append("N:").append(notesMassar.getIdEleve()).append(";");
          }
        }
    } catch (Exception e) {
    }
    // save affection
    try {
      if (typeImport.contains("affectation"))
        for (AffectationEnseignantMassar affectationEnseignantMassar :
            affectationEnseignantMassarList) {
          try {
            //     affectationEnseignantList
            AffectationEnseignant affectationEnseignant =
                affectationEnseignantList.stream()
                    .filter(
                        e ->
                            e.getClasse() != null
                                && e.getMatiere() != null
                                && Objects.equals(
                                    e.getClasse().getIdClasse(),
                                    affectationEnseignantMassar.getIdClasse())
                                && Objects.equals(
                                    e.getMatiere().getCd_matiere(),
                                    affectationEnseignantMassar.getCdMatiere()))
                    .findAny()
                    .orElse(null);
            if (affectationEnseignant == null) {
              affectationEnseignant = new AffectationEnseignant();
              affectationEnseignantList.add(affectationEnseignant);
            }
            Enseignant enseignant =
                enseignantList.stream()
                    .filter(
                        e ->
                            e.getIdPersonnel() != null
                                && e.getIdPersonnel()
                                    == affectationEnseignantMassar.getIdPersonnel())
                    .findAny()
                    .orElse(null);
            affectationEnseignant.setEnseignant(enseignant);
            Classe classe =
                classeList.stream()
                    .filter(
                        e ->
                            e.getIdClasse() != null
                                && Objects.equals(
                                    e.getIdClasse(), affectationEnseignantMassar.getIdClasse()))
                    .findAny()
                    .orElse(null);
            affectationEnseignant.setClasse(classe);
            MatiereEleve matiereEleve =
                matiereEleveList.stream()
                    .filter(
                        e ->
                            e.getCd_matiere() != null
                                && Objects.equals(
                                    e.getCd_matiere(), affectationEnseignantMassar.getCdMatiere()))
                    .findAny()
                    .orElse(null);
            affectationEnseignant.setMatiere(matiereEleve);
            affectationEnseignant.setGestioneleves(gestioneleves);

          } catch (Exception e) {
            strErrorImport
                .append("Aff:")
                .append(affectationEnseignantMassar.getIdPersonnel())
                .append(";");
          }
        }
    } catch (Exception e) {
    }
    response.setAlert(I18n.get("Importation terminée avec succès :") + strErrorImport);
  }

  public void ExportAllDataMassar(ActionRequest request, ActionResponse response)
      throws  IOException {
    response.setAlert("export");
  }
}
