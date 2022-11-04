package com.ressource.humaine.service;

import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.google.inject.persist.Transactional;
import com.hakibati.ressource.humaine.db.*;
import com.hakibati.ressource.humaine.db.repo.*;
import com.ressource.humaine.DataEsaise.*;
import com.setting.data.massar.*;
import com.setting.service.MassarMenGovService;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FileUtils;

public class GestionRHService {

  public UserMassar ConnexionMassar(String loginMassar, String passwordMassar) throws IOException {
    String cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    String infoLoginMassar = MassarMenGovService.GetInfoLoginMassar(cookie);
    if (infoLoginMassar == null) {
      return null;
    } else {
      ObjectMapper mapperUserMassar = new ObjectMapper();
      UserMassar userMassar = mapperUserMassar.readValue(infoLoginMassar, UserMassar.class);
      return userMassar;
    }
  }

  @Transactional
  public boolean ImportDataMassar(String loginMassar, String passwordMassar, GestionRH gestionRH)
      throws IOException {
    List<MatiereRh> matiereRhList = Beans.get(MatiereRhRepository.class).all().fetch();
    List<FonctionRh> fonctionRhList = Beans.get(FonctionRhRepository.class).all().fetch();
    List<CadreRh> cadreRhList = Beans.get(CadreRhRepository.class).all().fetch();
    List<GradeRh> gradeRhList = Beans.get(GradeRhRepository.class).all().fetch();

    List<PersonnelRh> personnelRhList = gestionRH.getPersonnel();
    String cookie = MassarMenGovService.GetCookieMassar(loginMassar, passwordMassar);
    String infoLoginMassar = MassarMenGovService.GetInfoLoginMassar(cookie);
    UserMassar userMassar = new UserMassar();
    ObjectMapper mapper = new ObjectMapper();
    if (infoLoginMassar == null) {
      return false;
    } else {
      userMassar = mapper.readValue(infoLoginMassar, UserMassar.class);
    }

    DataMassar dataMassMatiereList = new DataMassar();
    try {
      String requestBodyallMatiere =
          "{ \"operationName\": null, \"variables\": {}, \"query\": \"{\\n  allMatiere() {\\n    cd_matiere\\n    cd_unite\\n    matiereAr\\n    matiereFr\\n    __typename\\n  }\\n}\\n\"\n}";
      String jsonBodyBodyallMatiere =
          MassarMenGovService.getDataMassar(cookie, requestBodyallMatiere, "evaluationgw");
      dataMassMatiereList = mapper.readValue(jsonBodyBodyallMatiere, DataMassar.class);

    } catch (Exception ignored) {
    }

    DataMassar dataMassAllPersonnelList = new DataMassar();
    DataMassar dataMassEnseignantList = new DataMassar();
    DataMassar dataMassEnseignantList2 = new DataMassar();

    try {
      String requestBodyAllPersonnel =
          "{\"operationName\":null,\"variables\":{},\"query\":\"{ allPersonnel(where: {cdEtab: \\\""
              + userMassar.getCodeEtablissement()
              + "\\\"}) {"
              + " aDRESSE   aDRESSE_ELEC  affectationPrincipale    aFFILIE   aNC_ADM  aNC_ECHELON   aNC_GRADE   aN_NAIS   cdEtab    cD_CADRE  cD_DIPP   cD_DIPS   cD_DISCIP   cD_GRADE   cd_matiere  cD_NATION  cD_POSITION   cD_STATUT   cINA   cINN    cODE_POSTAL   dATEAFFIL   dateModification dATE_POSITION  dATE_REC  dATE_SITFAM   dT_CADRE  dT_DIPPROF   dT_DIPSCOL   dT_ECHELON  dT_GRADE   dT_SITSTAT   dT_TITUL  eCHELON   email  gENRE  idPersonnel   idRefPopulation   id_typeDomaine   jOUR_NAIS   lIEU_NAIS   mODAVGRA  mOIS_NAIS nOMA  nOML  numAFFIL   numImma   pPR   pRENOMA   pRENOML   sIT_FAM  tEL_FIXE  tEL_PORTABLE   vILLE   vILLEFORM  "
              + " }}\"}";
      String jsonBodyBodyAllPersonnel =
          MassarMenGovService.getDataMassar(cookie, requestBodyAllPersonnel, "formationcontinuegw");
      dataMassAllPersonnelList = mapper.readValue(jsonBodyBodyAllPersonnel, DataMassar.class);

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

    } catch (Exception ignored) {
    }

    // save matiere
    try {
      for (MatiereMassar matiereMassar : dataMassMatiereList.getData().getAllMatiere()) {
        try {
          MatiereRh matiereRh =
              matiereRhList.stream()
                  .filter(e -> e.getCd() != null && e.getCd().equals(matiereMassar.getCd_matiere()))
                  .findAny()
                  .orElse(null);

          if (matiereRh == null) {
            matiereRh = new MatiereRh();
            matiereRhList.add(matiereRh);
          }
          matiereRh.setCd(matiereMassar.getCd_matiere());
          matiereRh.setNameAr(matiereMassar.getMatiereAr());
          matiereRh.setNameFr(matiereMassar.getMatiereFr());
        } catch (Exception ignored) {

        }
      }
    } catch (Exception ignored) {

    }
    // save enseignant
    try {
      for (AllPersonnelMassar allPersonnelMassar :
          dataMassAllPersonnelList.getData().getAllPersonnel()) {

        try {
          //   String sin= allPersonnelMassar.getcINA() + allPersonnelMassar.getcINN();
          PersonnelRh personnelRh =
              personnelRhList.stream()
                  .filter(
                      e ->
                          e.getIdPersonnel() != null
                              && e.getIdPersonnel() == allPersonnelMassar.getIdPersonnel())
                  .findAny()
                  .orElse(null);

          if (personnelRh == null) {
            personnelRh = new PersonnelRh();
            personnelRhList.add(personnelRh);
          }

          personnelRh.setAdresse(allPersonnelMassar.getaDRESSE());
          personnelRh.setIdPersonnel(allPersonnelMassar.getIdPersonnel());
          personnelRh.setAdresseElec(allPersonnelMassar.getaDRESSE_ELEC());
          personnelRh.setAffectationPrincipale(allPersonnelMassar.isAffectationPrincipale());
          personnelRh.setAffilie(allPersonnelMassar.isaFFILIE());
          //
          personnelRh.setAncAdm(allPersonnelMassar.getaNC_ADM());
          personnelRh.setAncEchelon(allPersonnelMassar.getaNC_ECHELON());
          personnelRh.setAncGrade(allPersonnelMassar.getaNC_GRADE());
          //
          personnelRh.setAnNais(allPersonnelMassar.getaN_NAIS());
          personnelRh.setCdEtab(allPersonnelMassar.getCdEtab());
          personnelRh.setCdCadre(allPersonnelMassar.getcD_CADRE());
          CadreRh cadreRh =
              cadreRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null
                              && Objects.equals(e.getCd(), allPersonnelMassar.getcD_CADRE()))
                  .findAny()
                  .orElse(null);
          assert cadreRh != null;
          personnelRh.setCadreAr(cadreRh.getNameAr());
          personnelRh.setCadreAr(cadreRh.getNameFr());

          personnelRh.setCdDipp(allPersonnelMassar.getcD_DIPP());
          personnelRh.setCdDips(allPersonnelMassar.getcD_DIPS());
          personnelRh.setCdDiscip(allPersonnelMassar.getcD_DISCIP());
          personnelRh.setCdDips(allPersonnelMassar.getcD_DIPS());
          personnelRh.setCdGrade(allPersonnelMassar.getcD_GRADE());
          GradeRh gradeRh =
              gradeRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null
                              && Objects.equals(e.getCd(), allPersonnelMassar.getcD_GRADE()))
                  .findAny()
                  .orElse(null);
          assert gradeRh != null;
          personnelRh.setGradeAr(gradeRh.getNameAr());
          personnelRh.setGradeAr(gradeRh.getNameFr());

          personnelRh.setCdNation(allPersonnelMassar.getcD_NATION());
          personnelRh.setCdPosition(allPersonnelMassar.getcD_POSITION());
          personnelRh.setCdStatut(allPersonnelMassar.getcD_STATUT());
          personnelRh.setCin(allPersonnelMassar.getcINA() + allPersonnelMassar.getcINN());
          personnelRh.setCodePostal(allPersonnelMassar.getcODE_POSTAL());
          //
          personnelRh.setDateAffil(allPersonnelMassar.getdATEAFFIL());
          personnelRh.setDateModification(allPersonnelMassar.getDateModification());
          personnelRh.setDatePosition(allPersonnelMassar.getdATE_POSITION());
          personnelRh.setDateRec(allPersonnelMassar.getdATE_REC());
          personnelRh.setDateSitfam(allPersonnelMassar.getdATE_SITFAM());
          personnelRh.setDateCadre(allPersonnelMassar.getdT_CADRE());
          personnelRh.setDateDipprof(allPersonnelMassar.getdT_DIPPROF());
          personnelRh.setDateDipscol(allPersonnelMassar.getdT_DIPSCOL());
          personnelRh.setDateEchelon(allPersonnelMassar.getdT_ECHELON());
          personnelRh.setDateGrade(allPersonnelMassar.getdT_GRADE());
          personnelRh.setDateSitstat(allPersonnelMassar.getdT_SITSTAT());
          personnelRh.setDateTitul(allPersonnelMassar.getdT_TITUL());
          //
          personnelRh.setEchelon(allPersonnelMassar.geteCHELON());
          personnelRh.setEmail(allPersonnelMassar.getEmail());
          personnelRh.setGenre(allPersonnelMassar.getgENRE());
          personnelRh.setIdRefpopulation(allPersonnelMassar.getIdRefPopulation());
          personnelRh.setIdTypedomaine(allPersonnelMassar.getId_typeDomaine());
          personnelRh.setJourNais(allPersonnelMassar.getjOUR_NAIS());
          personnelRh.setLieuNais(allPersonnelMassar.getlIEU_NAIS());
          personnelRh.setModavgra(allPersonnelMassar.getmODAVGRA());
          personnelRh.setMoisNais(allPersonnelMassar.getmOIS_NAIS());
          personnelRh.setNomA(allPersonnelMassar.getnOMA());
          personnelRh.setNomL(allPersonnelMassar.getnOML());
          personnelRh.setNumAffil(allPersonnelMassar.getNumAFFIL());
          personnelRh.setNumImma(allPersonnelMassar.getNumImma());
          personnelRh.setPpr(allPersonnelMassar.getpPR());
          personnelRh.setPrenomA(allPersonnelMassar.getpRENOMA());
          personnelRh.setPrenomL(allPersonnelMassar.getpRENOML());
          personnelRh.setSitFam(allPersonnelMassar.getsIT_FAM());
          personnelRh.setTelFixe(allPersonnelMassar.gettEL_FIXE());
          personnelRh.setTelPortable(allPersonnelMassar.gettEL_PORTABLE());
          personnelRh.setVille(allPersonnelMassar.getvILLE());
          personnelRh.setVilleForm(allPersonnelMassar.getvILLEFORM());
          personnelRh.setCdMatiere(allPersonnelMassar.getCd_matiere());

          MatiereRh matiereRh =
              matiereRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null
                              && Objects.equals(e.getCd(), allPersonnelMassar.getCd_matiere()))
                  .findAny()
                  .orElse(null);

          assert matiereRh != null;
          personnelRh.setMatiereAr(matiereRh.getNameAr());
          personnelRh.setMatiereFr(matiereRh.getNameFr());

          personnelRh.setMatiere(matiereRh);
          personnelRh.setGestionRH(gestionRH);
        } catch (Exception e) {
          //
          // strErrorImport.append("Ppr:").append(enseignantMassar.getPpr()).append(";");
        }
      }
    } catch (Exception ignored) {
    }
    //
    //    try {
    //      for (EnseignantMassar enseignantMassar :
    // dataMassEnseignantList.getData().getEnseignants()) {
    //        try {
    //          Personnel personnel =
    //              personnelList.stream()
    //                  .filter(
    //                      e ->
    //                          e.getIdPersonnel() != null
    //                              && e.getIdPersonnel() == enseignantMassar.getIdPersonnel())
    //                  .findAny()
    //                  .orElse(null);
    //
    //          if (personnel == null) {
    //            personnel = new Personnel();
    //            personnelList.add(personnel);
    //          }
    //          personnel.setName(enseignantMassar.getNomCompletAr());
    //          personnel.setNomCompletFr(enseignantMassar.getNomCompletFr());
    //          personnel.setIdPersonnel(enseignantMassar.getIdPersonnel());
    //          personnel.setPpr(enseignantMassar.getPpr());
    //          personnel.setGestionRH(gestionRH);
    //          MatiereRh matiereRh =
    //              matiereRhList.stream()
    //                  .filter(
    //                      e ->
    //                          e.getCd_matiere() != null
    //                              && Objects.equals(e.getCd_matiere(),
    // enseignantMassar.getCdMatiere()))
    //                  .findAny()
    //                  .orElse(null);
    //          personnel.setMatiere(matiereRh);
    //        } catch (Exception e) {
    //          //
    //          // strErrorImport.append("Ppr:").append(enseignantMassar.getPpr()).append(";");
    //        }
    //      }
    //    } catch (Exception ignored) {
    //    }
    // tt personne
    //    try {
    //      for (PersonnelsByEtabMassar personnelsByEtabMassar :
    //          dataMassEnseignantList2.getData().getPersonnelsByEtabAndCycle()) {
    //        try {
    //          Personnel personnel =
    //              personnelList.stream()
    //                  .filter(
    //                      e ->
    //                          e.getIdPersonnel() != null
    //                              && e.getIdPersonnel() ==
    // personnelsByEtabMassar.getIdPersonnel())
    //                  .findAny()
    //                  .orElse(null);
    //
    //          if (personnel == null) {
    //            personnel = new Personnel();
    //            personnelList.add(personnel);
    //          }
    //          personnel.setName(
    //              personnelsByEtabMassar.getNomA() + "" + personnelsByEtabMassar.getPrenomA());
    //          personnel.setNomCompletFr(
    //              personnelsByEtabMassar.getNomL() + " " + personnelsByEtabMassar.getPrenomL());
    //          personnel.setIdPersonnel(personnelsByEtabMassar.getIdPersonnel());
    //          personnel.setPpr(personnelsByEtabMassar.getPpr());
    //          personnel.setGestionRH(gestionRH);
    //          MatiereRh matiereRh =
    //              matiereRhList.stream()
    //                  .filter(
    //                      e ->
    //                          e.getCd_matiere() != null
    //                              && Objects.equals(
    //                                  e.getCd_matiere(), personnelsByEtabMassar.getCdMatiere()))
    //                  .findAny()
    //                  .orElse(null);
    //          personnel.setMatiere(matiereRh);
    //        } catch (Exception e) {
    //          //
    //          //
    // strErrorImport.append("Ppr:").append(personnelsByEtabMassar.getPpr()).append(";");
    //        }
    //      }
    //    } catch (Exception ignored) {
    //    }

    return true;
  }

  //    private Document doc;
  //    private NodeList nodeList;
  public String ImportDataEsaise(MetaFile metaFile, GestionRH gestionRH)
      throws JAXBException, IOException {
    File xmlFile = MetaFiles.getPath(metaFile).toFile();
    if (!Files.getFileExtension(xmlFile.getName()).equals("xml")) {
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      return I18n.get("Choisissez un fichier au format xml");
    }
    List<MatiereRh> matiereRhList = Beans.get(MatiereRhRepository.class).all().fetch();
    List<FonctionRh> fonctionRhList = Beans.get(FonctionRhRepository.class).all().fetch();
    List<CadreRh> cadreRhList = Beans.get(CadreRhRepository.class).all().fetch();
    List<GradeRh> gradeRhList = Beans.get(GradeRhRepository.class).all().fetch();
    List<PersonnelRh> personnelRhList = gestionRH.getPersonnel();

    //   JAXBContext  jaxbContext = JAXBContext.newInstance(DsAgentExport.class);
    //            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    //            DsAgentExport dataEsaise = (DsAgentExport) jaxbUnmarshaller.unmarshal(xmlFile);
    ////            List<Activite> activiteList = dataEsaise.getActivite();

    String xmlString = FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8);
    xmlString = xmlString.replace("xmlns=", "xmlns0=");
    JAXBContext jaxbContext;
    DsAgentExport dsAgentExport = null;
    try {
      jaxbContext = JAXBContext.newInstance(DsAgentExport.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      dsAgentExport = (DsAgentExport) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
    } catch (Exception e) {
      return I18n.get("Choisissez un fichier au format xml");
    }
    if (dsAgentExport == null) return I18n.get("Choisissez un fichier au format xml");

    try {
      List<ActiviteSise> activiteSiseList = dsAgentExport.getActivite();
      for (ActiviteSise activiteSise : activiteSiseList) {
        if (activiteSise.getCINA() == null) continue;
        //                if(activiteSise.getCINN() == 0 ) continue;
        String cin = activiteSise.getCINA() + activiteSise.getCINN();

        try {
          PersonnelRh personnelRh =
              personnelRhList.stream()
                  .filter(e -> e.getCin() != null && Objects.equals(e.getCin(), cin))
                  .findAny()
                  .orElse(null);

          if (personnelRh == null) {
            personnelRh = new PersonnelRh();
            personnelRhList.add(personnelRh);
          }
          personnelRh.setCdEtab(activiteSise.getCD_ETAB());
          personnelRh.setPpr(activiteSise.getPPR());
          personnelRh.setCin(cin);
          personnelRh.setAffectationPrincipale(activiteSise.isACTIV_PRINC());
          personnelRh.setCdFonc(activiteSise.getCD_FONC());
          FonctionRh fonctionRh =
              fonctionRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null && Objects.equals(e.getCd(), activiteSise.getCD_FONC()))
                  .findAny()
                  .orElse(null);
          assert fonctionRh != null;
          personnelRh.setFoncAr(fonctionRh.getNameAr());
          personnelRh.setFoncFr(fonctionRh.getNameFr());
          //                    personnelRh.set(activiteSise.getDT_AFF_ETAB());
          //                    personnelRh.set(activiteSise.getDT_AFF_PROV());
          //                    personnelRh.set(activiteSise.getDT_AFF_REG());
          //                    personnelRh.set(activiteSise.getDT_AFF_POSTE());
          personnelRh.setGestionRH(gestionRH);
        } catch (Exception ignored) {
        }
      }
    } catch (Exception ignored) {
    }

    try {
      List<DataPersonnelSise> dataPersonnelSiseList = dsAgentExport.getDataPersonnelSise();
      for (DataPersonnelSise personnelSise : dataPersonnelSiseList) {
        if (personnelSise.getCINA() == null) continue;
        //                if(personnelSise.getCINN() == 0 ) continue;
        String cin = personnelSise.getCINA() + personnelSise.getCINN();

        try {
          PersonnelRh personnelRh =
              personnelRhList.stream()
                  .filter(e -> e.getCin() != null && Objects.equals(e.getCin(), cin))
                  .findAny()
                  .orElse(null);

          if (personnelRh == null) {
            personnelRh = new PersonnelRh();
            personnelRhList.add(personnelRh);
          }

          personnelRh.setPpr(personnelSise.getPPR());
          personnelRh.setCin(cin);
          personnelRh.setCdPosition("" + personnelSise.getCD_POSITION());
          personnelRh.setCdNation(personnelSise.getCD_NATION());
          personnelRh.setCdDips(personnelSise.getCD_DIPS());
          personnelRh.setCdDipp(personnelSise.getCD_DIPP());
          personnelRh.setNomA(personnelSise.getNOMA());
          personnelRh.setNomL(personnelSise.getNOML());

          personnelRh.setCdCadre(personnelSise.getCD_CADRE());
          CadreRh cadreRh =
              cadreRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null
                              && Objects.equals(e.getCd(), personnelSise.getCD_CADRE()))
                  .findAny()
                  .orElse(null);
          assert cadreRh != null;
          personnelRh.setCadreAr(cadreRh.getNameAr());
          personnelRh.setCadreFr(cadreRh.getNameFr());
          personnelRh.setCdGrade(personnelSise.getCD_GRADE());
          GradeRh gradeRh =
              gradeRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null
                              && Objects.equals(e.getCd(), personnelSise.getCD_GRADE()))
                  .findAny()
                  .orElse(null);
          assert gradeRh != null;
          personnelRh.setGradeAr(gradeRh.getNameAr());
          personnelRh.setGradeFr(gradeRh.getNameFr());
          personnelRh.setCdMatiere(personnelSise.getCD_DISCIP());
          MatiereRh matiereRh =
              matiereRhList.stream()
                  .filter(
                      e ->
                          e.getCd() != null
                              && Objects.equals(e.getCd(), personnelSise.getCD_DISCIP()))
                  .findAny()
                  .orElse(null);

          assert matiereRh != null;
          personnelRh.setMatiereAr(matiereRh.getNameAr());
          personnelRh.setMatiereFr(matiereRh.getNameFr());

          personnelRh.setGestionRH(gestionRH);
        } catch (Exception e) {
        }
      }
    } catch (Exception ignored) {
    }

    return "cin :::" + dsAgentExport.getDataPersonnelSise().get(0).getCINA();
  }
}
