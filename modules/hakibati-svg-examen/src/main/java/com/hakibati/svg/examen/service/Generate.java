package com.hakibati.svg.examen.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.*;
import com.hakibati.svg.examen.db.repo.*;
import com.hakibati.svg.examen.web.CodeContrainte;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
@Transactional
public class Generate {
  String USER_ADMIN_GROUP = "admins";
  List<SallesResultat> sallesResultatList;
  List<Contrainte> contrainteList;
  List<SalleFillier> salleFillierList;
  List<Prof> profList;
  int MoyenneSallesParProf;
  User user;

  public String generate(Long centerId, ActionRequest request, ActionResponse response)
      throws IOException {

    CentreExam centreExam = Beans.get(CentreExamRepository.class).find(centerId);
    List<DossierExam> dossierExamList = centreExam.getDossierExam();
    DossierExam dossierExam = new DossierExam();
    dossierExam.setProgress(100);
    dossierExam.setEtatDosExam(1);

    chargeContraintList();
    chargeProfList();
    chargeSallesResultatList();
    chargeSallesResultatListReservation();
    chargeSallesResultatListPermanence();
    calculerMoyenneSallesParProf();
    String n = VerificationPossibliteGenerate();
    if (n != null) {
      dossierExam.setErreurDosExam(n);
      dossierExam.setProgress(15);
      dossierExam.setEtatDosExam(2);

    } else {

      Integer m = RemplirSallesResultatList();
      if (m >= 0) {
        String err = "";
        err =
            err
                + " <p><span style='font-weight: bold;'>il faut vérifier les contraintes de la salle:</span> "
                + sallesResultatList.get(m).getSalles().getName()
                + "</p><p><span style='font-weight: bold;'>et les contraintes des  professeurs suivant :</span></p>";
        for (ProfsPoids profsPoids : sallesResultatList.get(m).getProfsPoidsList()) {
          err = err + "<p>-" + profsPoids.getProf().getName() + "</p>";
        }

        dossierExam.setErreurDosExam(err);
        dossierExam.setProgress(30);
        dossierExam.setEtatDosExam(2);
      }
      if (m < -1) {
        String err =
            " <span style='font-weight: bold;'>Limite du nombre de réserve non respectée</span> ";
        dossierExam.setErreurDosExam(err);
        dossierExam.setProgress(74);
        dossierExam.setEtatDosExam(1);
      }
      egaliteSallesResultatList();
    }
    dossierExam.setResultats(getListReultats(dossierExam));
    dossierExam.setCenter(centreExam);

    dossierExamList.add(dossierExam);
    centreExam.setDossierExam(dossierExamList);
    Beans.get(CentreExamRepository.class).save(centreExam);

    return "resultat ";
  }

  private String VerificationPossibliteGenerate() {

    if (getDoubleContrainte())
      return " <span style='font-weight: bold;'>Il y a une contrainte qui se répète plusieurs fois</span> ";
    return null;
  }

  private void egaliteSallesResultatList() {
    boolean resultaEgalite;
    MoyenneSallesParProf--;
    for (Prof prof : profList) {
      List<SallesResultat> sallesResultatListRegalite = getlistsallesProfGarde(prof);
      if (sallesResultatListRegalite.size() > MoyenneSallesParProf) {
        for (int s = 0; s < sallesResultatListRegalite.size(); s++) {
          Integer p = sallesResultatListRegalite.get(s).getId();
          sallesResultatList.get(p).setProf(null);
          resultaEgalite = getSalleResultaInput1(p, 2);
          if (resultaEgalite) break;
          sallesResultatList.get(p).setProf(prof);
        }
      }
    }

    //    MoyenneSallesParProf--;
    //    for (Prof prof : profList) {
    //      List<SallesResultat> sallesResultatListRegalite = getlistsallesProfGarde(prof);
    //      if (sallesResultatListRegalite.size() > MoyenneSallesParProf) {
    //        for (int s = 0; s < sallesResultatListRegalite.size(); s++) {
    //
    //          Integer p = sallesResultatListRegalite.get(s).getId();
    //          sallesResultatList.get(p).setProf(null);
    //          resultaEgalite = getSalleResultaInput1(p, 3);
    //          if (resultaEgalite) break;
    //          sallesResultatList.get(p).setProf(prof);
    //        }
    //      }
    //    }

  }

  private void calculerMoyenneSallesParProf() {
    MoyenneSallesParProf =
        (int)
            ((sallesResultatList.stream().filter(SallesResultat::isForce).count() / profList.size())
                + 1.5);
    //    MoyenneSallesParProf=8;
  }

  public List<Resultats> getListReultats(DossierExam dossierExam) {
    user = AuthUtils.getUser();
    List<Resultats> resultatsList = new ArrayList<>();

    for (int j = 0; j < sallesResultatList.size(); j++) {
      Resultats resultats = new Resultats();

      resultats.setSalles(sallesResultatList.get(j).getSalles());
      resultats.setMatiere(sallesResultatList.get(j).getMatiere());
      resultats.setFillier(sallesResultatList.get(j).getFillier());
      resultats.setAu(sallesResultatList.get(j).getAu());
      resultats.setDu(sallesResultatList.get(j).getDu());
      resultats.setDateExamine(sallesResultatList.get(j).getDateExam());
      resultats.setSvgIndice(sallesResultatList.get(j).getSvgIndice());
      resultats.setDossierExam(dossierExam);
      resultats.setProf(sallesResultatList.get(j).getProf());
      resultatsList.add(resultats);
    }

    return resultatsList;
  }

  public void chargeSallesResultatList() {
    sallesResultatList = new ArrayList<>();
    user = AuthUtils.getUser();
    Integer ids = 0;
    salleFillierList =
        Beans.get(SalleFillierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();

    List<Calendrier> calendrierList;
    for (int i = 0; i < salleFillierList.size(); i++) {
      calendrierList =
          Beans.get(CalendrierRepository.class)
              .all()
              .filter(
                  "etablissement = ? AND fillier.id= ?",
                  user.getEtablissementSelectionnee(),
                  salleFillierList.get(i).getFillier().getId())
              .fetch();

      Integer sallMax = getContraintSallesMax(salleFillierList.get(i).getSalles());
      Integer sallMin = getContraintSallesMin(salleFillierList.get(i).getSalles());
      Integer sallMinhomme = getContraintSallesHomme(salleFillierList.get(i).getSalles());
      Integer sallMinFemme = getContraintSallesFemme(salleFillierList.get(i).getSalles());
      Integer sallPrimair = getContraintSallesPrimaire(salleFillierList.get(i).getSalles());
      Integer sallCollege = getContraintSallesCollege(salleFillierList.get(i).getSalles());
      Integer sallQualifie = getContraintSallesQualifiant(salleFillierList.get(i).getSalles());

      for (Calendrier calendrier : calendrierList) {
        ids++;
        for (int s = 1; s <= sallMax; s++) {

          SallesResultat sallesResultat = new SallesResultat();
          sallesResultat.setProfsPoidsList(
              getProfsPoids(
                  salleFillierList.get(i).getSalles(),
                  calendrier.getMatiere(),
                  s <= sallQualifie,
                  sallQualifie < s && s <= sallQualifie + sallCollege,
                  sallQualifie + sallCollege < s && s <= sallQualifie + sallCollege + sallPrimair,
                  calendrier.getDateExamine(),
                  calendrier.getDu(),
                  calendrier.getAu(),
                  calendrier.getFillier()));
          sallesResultat.setMinFemme(sallMinFemme);
          sallesResultat.setMinHomme(sallMinhomme);
          sallesResultat.setSalles(salleFillierList.get(i).getSalles());
          sallesResultat.setMatiere(calendrier.getMatiere());
          sallesResultat.setFillier(calendrier.getFillier());
          sallesResultat.setAu(calendrier.getAu());
          sallesResultat.setDu(calendrier.getDu());
          sallesResultat.setDateExam(calendrier.getDateExamine());
          sallesResultat.setSvgIndice(s);
          sallesResultat.setForce(s <= sallMin);
          if (s <= sallMin) sallesResultat.setIdS(ids);

          sallesResultatList.add(sallesResultat);
        }
      }
    }
  }

  public void chargeSallesResultatListReservation() {
    user = AuthUtils.getUser();
    List<Calendrier> calendrierList;
    Salles salle =
        Beans.get(SallesRepository.class)
            .all()
            .filter(
                "etablissement = ? AND isReserve = ?", user.getEtablissementSelectionnee(), true)
            .fetchOne();
    if (salle == null) {
      salle = new Salles();
      salle.setName(I18n.get("الإحتياط"));
      salle.setIsReserve(true);
      Beans.get(SallesRepository.class).save(salle);
    }

    calendrierList =
        Beans.get(CalendrierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    int idsR = sallesResultatList.size();
    for (Calendrier calendrier : calendrierList) {
      idsR++;
      Integer sallMax = getContraintSallesReservationMax(calendrier.getFillier());
      Integer sallMin = getContraintSallesReservationMin(calendrier.getFillier());

      for (int s = 1; s <= sallMax; s++) {
        SallesResultat sallesResultat = new SallesResultat();
        sallesResultat.setProfsPoidsList(
            getProfsPoids(
                salle,
                calendrier.getMatiere(),
                false,
                false,
                false,
                calendrier.getDateExamine(),
                calendrier.getDu(),
                calendrier.getAu(),
                calendrier.getFillier()));
        sallesResultat.setMinFemme(0);
        sallesResultat.setMinHomme(0);
        sallesResultat.setSalles(salle);
        sallesResultat.setMatiere(calendrier.getMatiere());
        sallesResultat.setFillier(calendrier.getFillier());
        sallesResultat.setAu(calendrier.getAu());
        sallesResultat.setDu(calendrier.getDu());
        sallesResultat.setDateExam(calendrier.getDateExamine());
        sallesResultat.setSvgIndice(s);
        sallesResultat.setForce(s <= sallMin);
        if (s <= sallMin) sallesResultat.setIdS(idsR);

        sallesResultatList.add(sallesResultat);
      }
    }
  }

  public void chargeSallesResultatListPermanence() {
    user = AuthUtils.getUser();
    List<Contrainte> contrainteListRech;
    Salles salle =
        Beans.get(SallesRepository.class)
            .all()
            .filter(
                "etablissement = ? AND isPermanence = ?", user.getEtablissementSelectionnee(), true)
            .fetchOne();
    List<Calendrier> calendrierList =
        Beans.get(CalendrierRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();

    if (salle == null) {
      salle = new Salles();
      salle.setName(I18n.get("المداومة"));
      salle.setIsPermanence(true);
      Beans.get(SallesRepository.class).save(salle);
    }
    contrainteListRech =
        contrainteList.stream()
            .filter(e -> e.getContrainte().getCode().equals(CodeContrainte.professeurs_permanences))
            .collect(Collectors.toList());

    for (Contrainte contrainte : contrainteListRech) {
      List<Calendrier> calendrierListRech =
          calendrierList.stream()
              .filter(
                  e ->
                      e.getMatiere() == contrainte.getMatiere()
                          && e.getFillier() == contrainte.getFillier())
              .collect(Collectors.toList());

      if (calendrierListRech.size() > 0) {
        for (Prof prof : contrainte.getProfs()) {
          SallesResultat sallesResultat = new SallesResultat();
          sallesResultat.setProfsPoidsList(null);
          sallesResultat.setMinFemme(0);
          sallesResultat.setMinHomme(0);
          sallesResultat.setSalles(salle);
          sallesResultat.setMatiere(contrainte.getMatiere());
          sallesResultat.setFillier(contrainte.getFillier());
          sallesResultat.setAu(calendrierListRech.get(0).getAu());
          sallesResultat.setDu(calendrierListRech.get(0).getDu());
          sallesResultat.setDateExam(calendrierListRech.get(0).getDateExamine());
          sallesResultat.setSvgIndice(1);
          sallesResultat.setForce(true);
          sallesResultat.setIdS(-3);
          //         sallesResultat.setProf(contrainte.getProf());
          sallesResultat.setProf(prof);
          sallesResultatList.add(sallesResultat);
        }
      }
    }
  }

  public void chargeProfList() {
    profList =
        Beans.get(ProfRepository.class)
            .all()
            .filter(
                "etablissement = ? AND fonctionExaman.code = ?",
                user.getEtablissementSelectionnee(),
                CodeContrainte.code_exam_fonction_Surveillant)
            .fetch();
  }

  public void chargeContraintList() {
    user = AuthUtils.getUser();
    contrainteList =
        Beans.get(ContrainteRepository.class)
            .all()
            .filter("etablissement = ? AND active = ?", user.getEtablissementSelectionnee(), true)
            .fetch();
  }

  public Integer getContraintSallesMax(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte().getCode().equals(CodeContrainte.maximum_professeurs_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());
    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.maximum_professeurs_toutes_salles))
            .collect(Collectors.toList());
    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 2;
  }

  public int getContraintSallesReservationMax(Fillier fillier) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_reservation_un_Fillier)
                        && e.getFillier() == fillier)
            .collect(Collectors.toList());
    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    int percentage = 0;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.maximum_Pourcentage_reserve_tous_Fillier))
            .collect(Collectors.toList());
    if (contrainteListserch.size() > 0) percentage = contrainteListserch.get(0).getValeur();
    long Total = salleFillierList.stream().filter(e -> e.getFillier() == fillier).count();
    return (int) Math.round((Total * percentage) / 50);
  }

  public Integer getContraintSallesReservationMin(Fillier fillier) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.minimum_reservation_un_Fillier)
                        && e.getFillier() == fillier)
            .collect(Collectors.toList());
    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_Pourcentage_reserve_tous_Fillier))
            .collect(Collectors.toList());
    Integer percentage = 0;
    if (contrainteListserch.size() > 0) percentage = contrainteListserch.get(0).getValeur();
    long Total = salleFillierList.stream().filter(e -> e.getFillier() == fillier).count();
    return (int) Math.round((Total * percentage) / 50);
  }

  public Integer getContraintSallesMin(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte().getCode().equals(CodeContrainte.minimum_professeurs_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_professeurs_toutes_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 2;
  }

  public Integer getContraintSallesHomme(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.minimum_professeurs_homme_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_professeurs_homme_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 0;
  }

  public Integer getContraintSallesFemme(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.minimum_professeurs_femme_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_professeurs_femme_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 0;
  }

  public Integer getContraintSallesPrimaire(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.minimum_professeurs_primaire_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_professeurs_primaire_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 0;
  }

  public Integer getContraintSallesCollege(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.minimum_professeurs_college_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_professeurs_college_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 0;
  }

  public Integer getContraintSallesQualifiant(Salles salle) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.minimum_professeurs_qualifie_un_salle)
                        && e.getSalle() == salle)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.minimum_professeurs_qualifie_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return 0;
  }

  private Integer getContraintProfMatiereSpecialise(Prof prof) {
    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_garde_meme_matiere_specialise_pr_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) {
      return contrainteListserch.get(0).getValeur();
    }
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.maximum_garde_meme_matiere_specialise))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return -1;
  }

  private Integer getContraintProfMatinSoir(Prof prof) {

    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_garde_matin_apres_midi_pr_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());
    //  Set<Prof> profSet=  contrainteListserch.get(0).getProfs();
    //      profSet.contains(prof);
    if (contrainteListserch.size() > 0) {
      return contrainteListserch.get(0).getValeur();
    }
    Contrainte contrainte =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_garde_matin_apres_midi_pr_un_prof)
                        && e.getProf() == prof)
            .findFirst()
            .orElse(null);
    //    contrainte.getProfs().contains(prof);

    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.maximum_garde_matin_apres_midi))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return -1;
  }

  private Integer getContraintProfMaxAmi(Prof prof) {

    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_garde_meme_professeur_pr_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0 && contrainteListserch.get(0).getValeur() > 0) {
      return contrainteListserch.get(0).getValeur();
    }
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                        .getCode()
                        .equals(CodeContrainte.maximum_garde_meme_professeur))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0 && contrainteListserch.get(0).getValeur() > 0)
      return contrainteListserch.get(0).getValeur();
    return -1;
  }

  private Integer getContraintProfMaxMemeSalle(Prof prof) {

    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_garde_meme_salle_pr_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0 && contrainteListserch.get(0).getValeur() > 0) {
      return contrainteListserch.get(0).getValeur();
    }
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e -> e.getContrainte().getCode().equals(CodeContrainte.maximum_garde_meme_salles))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0 && contrainteListserch.get(0).getValeur() > 0)
      return contrainteListserch.get(0).getValeur();
    return -1;
  }

  private Integer getContraintProfMaxJourDsExam(Prof prof) {

    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte().getCode().equals(CodeContrainte.maximum_jour_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) {
      return contrainteListserch.get(0).getValeur();
    }
    contrainteListserch =
        contrainteList.stream()
            .filter(e -> e.getContrainte().getCode().equals(CodeContrainte.maximum_jour_tous_prof))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return -1;
  }

  private Integer getContraintProfMaxReserve(Prof prof) {

    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte().getCode().equals(CodeContrainte.maximum_reserve_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) {
      return contrainteListserch.get(0).getValeur();
    }
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e -> e.getContrainte().getCode().equals(CodeContrainte.maximum_reserve_tous_prof))
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) return contrainteListserch.get(0).getValeur();
    return -1;
  }

  private Integer getContraintProfMaxGarde(Prof prof) {

    List<Contrainte> contrainteListserch;
    contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.maximum_garde_cours_exam_un_prof)
                        && e.getProf() == prof)
            .collect(Collectors.toList());

    if (contrainteListserch.size() > 0) {
      return contrainteListserch.get(0).getValeur();
    }

    return Integer.MAX_VALUE;
  }

  //  private boolean getContraintProfHorairesDisponible0(
  //      Prof prof, LocalDate DateExam, LocalTime Du, LocalTime Au) {
  //    List<Contrainte> contrainteListserch =
  //        contrainteList.stream()
  //            .filter(
  //                e ->
  //                    e.getContrainte()
  //                            .getCode()
  //                            .equals(CodeContrainte.horaires_pas_disponibles_un_professeur)
  //                        && e.getProf() == prof
  //                        && e.getDateExam().compareTo(DateExam) == 0
  //                        && (e.getMatin() && Du.compareTo(CodeContrainte.time_speration_day) < 0
  //                            || e.getApresMidi()
  //                                && Au.compareTo(CodeContrainte.time_speration_day) > 0))
  //            .collect(Collectors.toList());
  //
  //    if (contrainteListserch.size() > 0) {
  //      return true;
  //    }
  //
  //    return false;
  //  }
  private boolean getContraintProfHorairesDisponible(
      Prof prof, LocalDate DateExam, LocalTime Du, LocalTime Au) {
    Contrainte contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.horaires_pas_disponibles_un_professeur)
                        && e.getDateExam().compareTo(DateExam) == 0
                        && (e.getMatin() && Du.compareTo(CodeContrainte.time_speration_day) < 0
                            || e.getApresMidi()
                                && Au.compareTo(CodeContrainte.time_speration_day) > 0))
            .findFirst()
            .orElse(null);

    return contrainteListserch != null && contrainteListserch.getProfs().contains(prof);
  }

  //  private boolean getContraintProfSalleDisponible(Prof prof, Salles salle, Fillier fillier) {
  //    List<Contrainte> contrainteListserch =
  //        contrainteList.stream()
  //            .filter(
  //                e ->
  //                    e.getContrainte()
  //                            .getCode()
  //                            .equals(CodeContrainte.salle_pas_disponibles_un_professeur)
  //                        && e.getProf() == prof
  //                        && e.getFillier() == fillier
  //                        && e.getSalle() == salle)
  //            .collect(Collectors.toList());
  //
  //    if (contrainteListserch.size() > 0) {
  //      return true;
  //    }
  //
  //    return false;
  //  }

  private boolean getContraintProfSalleDisponible(Prof prof, Salles salle, Fillier fillier) {
    Contrainte contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte()
                            .getCode()
                            .equals(CodeContrainte.salle_pas_disponibles_un_professeur)
                        && e.getFillier() == fillier
                        && e.getSalle() == salle)
            .findFirst()
            .orElse(null);

    if (contrainteListserch != null && contrainteListserch.getProfs().contains(prof)) {
      return true;
    }

    return false;
  }

  //  private boolean getContraintProfMatiereDisponible(Prof prof, Matiere matiere, Fillier fillier)
  // {
  //    List<Contrainte> contrainteListserch =
  //        contrainteList.stream()
  //            .filter(
  //                e ->
  //
  // e.getContrainte().getCode().equals(CodeContrainte.maximum_garde_matiere_un_prof)
  //                        && e.getProf() == prof
  //                        && e.getFillier() == fillier
  //                        && e.getMatiere() == matiere)
  //            .collect(Collectors.toList());
  //
  //    if (contrainteListserch.size() > 0 && contrainteListserch.get(0).getValeur() == 0) {
  //      return true;
  //    }
  //
  //    return false;
  //  }

  private boolean getContraintProfMatiereDisponible(Prof prof, Matiere matiere, Fillier fillier) {
    Contrainte contrainteListserch =
        contrainteList.stream()
            .filter(
                e ->
                    e.getContrainte().getCode().equals(CodeContrainte.maximum_garde_matiere_un_prof)
                        && e.getFillier() == fillier
                        && e.getMatiere() == matiere)
            .findFirst()
            .orElse(null);

    if (contrainteListserch != null && contrainteListserch.getProfs().contains(prof)) {
      return true;
    }
    return false;
  }

  private List<ProfsPoids> getProfsPoids(
      Salles salles,
      Matiere matiere,
      Boolean isQual,
      Boolean isCol,
      Boolean ispri,
      LocalDate dateExam,
      LocalTime Du,
      LocalTime Au,
      Fillier fillier) {
    List<ProfsPoids> profsPoidsList = new ArrayList<>();

    for (Prof prof : profList) {
      ProfsPoids profsPoids = new ProfsPoids();
      int test = 1;

      if (matiere == prof.getMatiere() && getContraintProfMatiereSpecialise(prof) == 0) {
        continue;
      }
      if (isQual
          && (prof.getCycle().getCode().equals(CodeContrainte.code_cycle_college)
              || prof.getCycle().getCode().equals(CodeContrainte.code_cycle_primaire))) {
        continue;
      }
      if (isCol
          && (prof.getCycle().getCode().equals(CodeContrainte.code_cycle_qualifie)
              || prof.getCycle().getCode().equals(CodeContrainte.code_cycle_primaire))) {
        continue;
      }
      if (ispri
          && (prof.getCycle().getCode().equals(CodeContrainte.code_cycle_qualifie)
              || prof.getCycle().getCode().equals(CodeContrainte.code_cycle_college))) {
        continue;
      }
      if (getContraintProfHorairesDisponible(prof, dateExam, Du, Au)) {
        continue;
      }
      if (getContraintProfSalleDisponible(prof, salles, fillier)) {
        continue;
      }
      if (getContraintProfMatiereDisponible(prof, matiere, fillier)) {
        continue;
      }

      if (test == 1) {
        profsPoids.setMaxMatinSoir(getContraintProfMatinSoir(prof));
        profsPoids.setMaxMemeAmi(getContraintProfMaxAmi(prof));
        profsPoids.setMaxMemeSalle(getContraintProfMaxMemeSalle(prof));
        profsPoids.setMaxJourDsExam(getContraintProfMaxJourDsExam(prof));
        profsPoids.setMaxResrve(getContraintProfMaxReserve(prof));
        profsPoids.setMaxGarde(getContraintProfMaxGarde(prof));
        profsPoids.setProf(prof);
        profsPoids.setPoids(0);
        profsPoidsList.add(profsPoids);
      }
    }
    Collections.shuffle(profsPoidsList);
    return profsPoidsList;
  }

  private Integer RemplirSallesResultatList() {
    Integer reserve = -1;
    for (int i = 0; i < sallesResultatList.size(); i++) {
      sallesResultatList.get(i).setId(i);
    }

    for (int i = 0; i < sallesResultatList.size(); i++) {
      if (sallesResultatList.get(i).isForce() && sallesResultatList.get(i).getProf() == null) {
        boolean res = true;
        for (int max = 1; max < 6 + reserve; max++) {
          res = getSalleResultaInput1(i, max);
          if (res) break;
        }
        if (!res && !sallesResultatList.get(i).getSalles().getIsReserve()) return i;
        if (!res) reserve = -3;
      }
    }
    return reserve;
  }

  private boolean getSalleResultaInput1(int i, int max) {
    if (sallesResultatList.get(i).getProfsPoidsList() == null) return false;
    List<ProfsPoids> profsPoidsList = sallesResultatList.get(i).getProfsPoidsList();
    for (ProfsPoids profsPoids : profsPoidsList) {
      Prof prof = profsPoids.getProf();
      int test = 1;
      //            if(profsPoids.getPoids()>10*max) continue;

      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_homme)
          && sallesResultatList.get(i).getMinFemme() >= getcountPourFemme(i)) {
        continue;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_femme)
          && sallesResultatList.get(i).getMinHomme() >= getcountPourHomme(i)) {
        continue;
      }
      if (getPofDoubleInSalle(
              prof,
              sallesResultatList.get(i).getFillier(),
              sallesResultatList.get(i).getMatiere(),
              sallesResultatList.get(i).getSalles())
          > 0) continue;

      if (getlistsallesProfGarde(prof).size()
          >= Math.min(profsPoids.getMaxGarde(), MoyenneSallesParProf)) {
        test = 5;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getDu().compareTo(CodeContrainte.time_speration_day) > 0
          && getlistsallesProfMatin(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 11;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getAu().compareTo(CodeContrainte.time_speration_day) < 0
          && getlistsallesProfSoir(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 12;
      }
      if (getlistSallesProfTimeAndDay(
                  prof,
                  sallesResultatList.get(i).getDateExam(),
                  sallesResultatList.get(i).getDu(),
                  sallesResultatList.get(i).getAu())
              .size()
          > 0) {
        test = 13;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getMaxMemeSalle() != -1
          && getlistSallesProfMaxMemeSalle(
                      prof,
                      sallesResultatList.get(i).getFillier(),
                      sallesResultatList.get(i).getSalles())
                  .size()
              >= profsPoids.getMaxMemeSalle()) {
        if (test == 13 || test == 12 || test == 11) continue;
        test = 6;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && sallesResultatList.get(i).getSvgIndice() <= 2
          && profsPoids.getMaxMemeAmi() != -1
          && getlistSallesProfMaxMemeAmi(
                      prof,
                      sallesResultatList.get(i).getProfAmi(),
                      sallesResultatList.get(i).getFillier())
                  .size()
              >= profsPoids.getMaxMemeAmi()) {
        if (test == 13 || test == 12 || test == 11 || test == 6) continue;
        test = 7;
      }
      if (profsPoids.getMaxJourDsExam() != -1
          && getlistsallesProfCountDay(prof, sallesResultatList.get(i).getDateExam())
              >= profsPoids.getMaxJourDsExam()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7) continue;
        test = 14;
      }
      if (profsPoids.getMaxResrve() != -1
          && sallesResultatList.get(i).getSalles().getIsReserve()
          && getlistsallesProfReserve(prof, i).size() >= profsPoids.getMaxResrve()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7 || test == 14)
          continue;
        test = 15;
      }

      if (test == 1) {
        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        return true;
      }
      if (test > 1 && max > 1) {

        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        boolean resulta = true;
        List<SallesResultat> sallesResultatListEchange =
            getsallesResultatListEchange(prof, i, test);
        for (int m = 0; m < sallesResultatListEchange.size(); m++) {
          int id = sallesResultatListEchange.get(m).getId();
          sallesResultatList.get(id).setProf(null);
          resulta = getSalleResultaInput2(id, max);
          if (test < 10 && resulta) return true;
          if (test < 10) sallesResultatList.get(id).setProf(prof);
          if (test > 10 && !resulta) {
            sallesResultatList.get(id).setProf(prof);
            break;
          }
        }
        if (resulta) return true;
        sallesResultatList.get(i).setProf(null);
      }
    }

    return false;
  }

  private boolean getSalleResultaInput2(int i, int max) {
    if (sallesResultatList.get(i).getProfsPoidsList() == null) return false;
    List<ProfsPoids> profsPoidsList = sallesResultatList.get(i).getProfsPoidsList();
    for (ProfsPoids profsPoids : profsPoidsList) {
      Prof prof = profsPoids.getProf();
      int test = 1;
      //            if(profsPoids.getPoids()>10*max) continue;

      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_homme)
          && sallesResultatList.get(i).getMinFemme() >= getcountPourFemme(i)) {
        continue;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_femme)
          && sallesResultatList.get(i).getMinHomme() >= getcountPourHomme(i)) {
        continue;
      }
      if (getPofDoubleInSalle(
              prof,
              sallesResultatList.get(i).getFillier(),
              sallesResultatList.get(i).getMatiere(),
              sallesResultatList.get(i).getSalles())
          > 0) continue;

      if (getlistsallesProfGarde(prof).size()
          >= Math.min(profsPoids.getMaxGarde(), MoyenneSallesParProf)) {
        test = 5;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getDu().compareTo(CodeContrainte.time_speration_day) > 0
          && getlistsallesProfMatin(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 11;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getAu().compareTo(CodeContrainte.time_speration_day) < 0
          && getlistsallesProfSoir(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 12;
      }
      if (getlistSallesProfTimeAndDay(
                  prof,
                  sallesResultatList.get(i).getDateExam(),
                  sallesResultatList.get(i).getDu(),
                  sallesResultatList.get(i).getAu())
              .size()
          > 0) {
        test = 13;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getMaxMemeSalle() != -1
          && getlistSallesProfMaxMemeSalle(
                      prof,
                      sallesResultatList.get(i).getFillier(),
                      sallesResultatList.get(i).getSalles())
                  .size()
              >= profsPoids.getMaxMemeSalle()) {
        if (test == 13 || test == 12 || test == 11) continue;
        test = 6;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && sallesResultatList.get(i).getSvgIndice() <= 2
          && profsPoids.getMaxMemeAmi() != -1
          && getlistSallesProfMaxMemeAmi(
                      prof,
                      sallesResultatList.get(i).getProfAmi(),
                      sallesResultatList.get(i).getFillier())
                  .size()
              >= profsPoids.getMaxMemeAmi()) {
        if (test == 13 || test == 12 || test == 11 || test == 6) continue;
        test = 7;
      }
      if (profsPoids.getMaxJourDsExam() != -1
          && getlistsallesProfCountDay(prof, sallesResultatList.get(i).getDateExam())
              >= profsPoids.getMaxJourDsExam()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7) continue;
        test = 14;
      }
      if (profsPoids.getMaxResrve() != -1
          && sallesResultatList.get(i).getSalles().getIsReserve()
          && getlistsallesProfReserve(prof, i).size() >= profsPoids.getMaxResrve()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7 || test == 14)
          continue;
        test = 15;
      }

      if (test == 1) {
        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        return true;
      }
      if (test > 1 && max > 2) {

        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        boolean resulta = true;
        List<SallesResultat> sallesResultatListEchange =
            getsallesResultatListEchange(prof, i, test);
        for (int m = 0; m < sallesResultatListEchange.size(); m++) {
          int id = sallesResultatListEchange.get(m).getId();
          sallesResultatList.get(id).setProf(null);
          resulta = getSalleResultaInput3(id, max);
          if (test < 10 && resulta) return true;
          if (test < 10) sallesResultatList.get(id).setProf(prof);
          if (test > 10 && !resulta) {
            sallesResultatList.get(id).setProf(prof);
            break;
          }
        }
        if (resulta) return true;
        sallesResultatList.get(i).setProf(null);
      }
    }
    return false;
  }

  private boolean getSalleResultaInput3(int i, int max) {
    if (sallesResultatList.get(i).getProfsPoidsList() == null) return false;
    List<ProfsPoids> profsPoidsList = sallesResultatList.get(i).getProfsPoidsList();
    for (ProfsPoids profsPoids : profsPoidsList) {
      Prof prof = profsPoids.getProf();
      int test = 1;
      //            if(profsPoids.getPoids()>10*max) continue;

      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_homme)
          && sallesResultatList.get(i).getMinFemme() >= getcountPourFemme(i)) {
        continue;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_femme)
          && sallesResultatList.get(i).getMinHomme() >= getcountPourHomme(i)) {
        continue;
      }
      if (getPofDoubleInSalle(
              prof,
              sallesResultatList.get(i).getFillier(),
              sallesResultatList.get(i).getMatiere(),
              sallesResultatList.get(i).getSalles())
          > 0) continue;

      if (getlistsallesProfGarde(prof).size()
          >= Math.min(profsPoids.getMaxGarde(), MoyenneSallesParProf)) {
        test = 5;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getDu().compareTo(CodeContrainte.time_speration_day) > 0
          && getlistsallesProfMatin(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 11;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getAu().compareTo(CodeContrainte.time_speration_day) < 0
          && getlistsallesProfSoir(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 12;
      }
      if (getlistSallesProfTimeAndDay(
                  prof,
                  sallesResultatList.get(i).getDateExam(),
                  sallesResultatList.get(i).getDu(),
                  sallesResultatList.get(i).getAu())
              .size()
          > 0) {
        test = 13;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getMaxMemeSalle() != -1
          && getlistSallesProfMaxMemeSalle(
                      prof,
                      sallesResultatList.get(i).getFillier(),
                      sallesResultatList.get(i).getSalles())
                  .size()
              >= profsPoids.getMaxMemeSalle()) {
        if (test == 13 || test == 12 || test == 11) continue;
        test = 6;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && sallesResultatList.get(i).getSvgIndice() <= 2
          && profsPoids.getMaxMemeAmi() != -1
          && getlistSallesProfMaxMemeAmi(
                      prof,
                      sallesResultatList.get(i).getProfAmi(),
                      sallesResultatList.get(i).getFillier())
                  .size()
              >= profsPoids.getMaxMemeAmi()) {
        if (test == 13 || test == 12 || test == 11 || test == 6) continue;
        test = 7;
      }
      if (profsPoids.getMaxJourDsExam() != -1
          && getlistsallesProfCountDay(prof, sallesResultatList.get(i).getDateExam())
              >= profsPoids.getMaxJourDsExam()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7) continue;
        test = 14;
      }
      if (profsPoids.getMaxResrve() != -1
          && sallesResultatList.get(i).getSalles().getIsReserve()
          && getlistsallesProfReserve(prof, i).size() >= profsPoids.getMaxResrve()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7 || test == 14)
          continue;
        test = 15;
      }

      if (test == 1) {
        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        return true;
      }
      if (test > 1 && max > 3) {

        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        boolean resulta = true;
        List<SallesResultat> sallesResultatListEchange =
            getsallesResultatListEchange(prof, i, test);
        for (int m = 0; m < sallesResultatListEchange.size(); m++) {
          int id = sallesResultatListEchange.get(m).getId();
          sallesResultatList.get(id).setProf(null);
          resulta = getSalleResultaInput4(id, max);
          if (test < 10 && resulta) return true;
          if (test < 10) sallesResultatList.get(id).setProf(prof);
          if (test > 10 && !resulta) {
            sallesResultatList.get(id).setProf(prof);
            break;
          }
        }
        if (resulta) return true;
        sallesResultatList.get(i).setProf(null);
      }
    }
    return false;
  }

  private boolean getSalleResultaInput4(int i, int max) {
    if (sallesResultatList.get(i).getProfsPoidsList() == null) return false;
    List<ProfsPoids> profsPoidsList = sallesResultatList.get(i).getProfsPoidsList();
    for (ProfsPoids profsPoids : profsPoidsList) {
      Prof prof = profsPoids.getProf();
      int test = 1;
      //            if(profsPoids.getPoids()>10*max) continue;

      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_homme)
          && sallesResultatList.get(i).getMinFemme() >= getcountPourFemme(i)) {
        continue;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getProf().getSexe().getCode().equals(CodeContrainte.code_sexe_femme)
          && sallesResultatList.get(i).getMinHomme() >= getcountPourHomme(i)) {
        continue;
      }
      if (getPofDoubleInSalle(
              prof,
              sallesResultatList.get(i).getFillier(),
              sallesResultatList.get(i).getMatiere(),
              sallesResultatList.get(i).getSalles())
          > 0) continue;

      if (getlistsallesProfGarde(prof).size()
          >= Math.min(profsPoids.getMaxGarde(), MoyenneSallesParProf)) {
        test = 5;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getDu().compareTo(CodeContrainte.time_speration_day) > 0
          && getlistsallesProfMatin(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 11;
      }
      if (profsPoids.getMaxMatinSoir() == 0
          && sallesResultatList.get(i).getAu().compareTo(CodeContrainte.time_speration_day) < 0
          && getlistsallesProfSoir(prof, sallesResultatList.get(i).getDateExam()).size() > 0) {
        test = 12;
      }
      if (getlistSallesProfTimeAndDay(
                  prof,
                  sallesResultatList.get(i).getDateExam(),
                  sallesResultatList.get(i).getDu(),
                  sallesResultatList.get(i).getAu())
              .size()
          > 0) {
        test = 13;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && profsPoids.getMaxMemeSalle() != -1
          && getlistSallesProfMaxMemeSalle(
                      prof,
                      sallesResultatList.get(i).getFillier(),
                      sallesResultatList.get(i).getSalles())
                  .size()
              >= profsPoids.getMaxMemeSalle()) {
        if (test == 13 || test == 12 || test == 11) continue;
        test = 6;
      }
      if (!sallesResultatList.get(i).getSalles().getIsReserve()
          && sallesResultatList.get(i).getSvgIndice() <= 2
          && profsPoids.getMaxMemeAmi() != -1
          && getlistSallesProfMaxMemeAmi(
                      prof,
                      sallesResultatList.get(i).getProfAmi(),
                      sallesResultatList.get(i).getFillier())
                  .size()
              >= profsPoids.getMaxMemeAmi()) {
        if (test == 13 || test == 12 || test == 11 || test == 6) continue;
        test = 7;
      }
      if (profsPoids.getMaxJourDsExam() != -1
          && getlistsallesProfCountDay(prof, sallesResultatList.get(i).getDateExam())
              >= profsPoids.getMaxJourDsExam()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7) continue;
        test = 14;
      }
      if (profsPoids.getMaxResrve() != -1
          && sallesResultatList.get(i).getSalles().getIsReserve()
          && getlistsallesProfReserve(prof, i).size() >= profsPoids.getMaxResrve()) {
        if (test == 13 || test == 12 || test == 11 || test == 6 || test == 7 || test == 14)
          continue;
        test = 15;
      }

      if (test == 1) {
        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        return true;
      }
      if (test > 1 && max > 4) {

        sallesResultatList.get(i).setProf(prof);
        sallesResultatList.get(getIndiceAmi(i)).setProfAmi(prof);
        boolean resulta = true;
        List<SallesResultat> sallesResultatListEchange =
            getsallesResultatListEchange(prof, i, test);
        for (int m = 0; m < sallesResultatListEchange.size(); m++) {
          int id = sallesResultatListEchange.get(m).getId();
          sallesResultatList.get(id).setProf(null);
          resulta = getSalleResultaInput5(id, max);
          if (test < 10 && resulta) return true;
          if (test < 10) sallesResultatList.get(id).setProf(prof);
          if (test > 10 && !resulta) {
            sallesResultatList.get(id).setProf(prof);
            break;
          }
        }
        if (resulta) return true;
        sallesResultatList.get(i).setProf(null);
      }
    }
    return false;
  }

  private boolean getSalleResultaInput5(int i, int max) {
    return false;
  }

  private List<SallesResultat> getsallesResultatListEchange(Prof prof, int i, int test) {
    if (test == 5) return getlistsallesProfGarde(prof);
    if (test == 11) return getlistsallesProfMatin(prof, sallesResultatList.get(i).getDateExam());
    if (test == 12) return getlistsallesProfSoir(prof, sallesResultatList.get(i).getDateExam());
    if (test == 13)
      return getlistSallesProfTimeAndDay(
          prof,
          sallesResultatList.get(i).getDateExam(),
          sallesResultatList.get(i).getDu(),
          sallesResultatList.get(i).getAu());
    if (test == 6)
      return getlistSallesProfMaxMemeSalle(
          prof, sallesResultatList.get(i).getFillier(), sallesResultatList.get(i).getSalles());
    if (test == 7)
      return getlistSallesProfMaxMemeAmi(
          prof, sallesResultatList.get(i).getProfAmi(), sallesResultatList.get(i).getFillier());

    if (test == 15) return getlistsallesProfReserve(prof, i);
    if (test == 14) {
      List<SallesResultat> sallesResultatListseach =
          sallesResultatList.stream().filter(e -> e.getProf() == prof).collect(Collectors.toList());

      for (SallesResultat resultat : sallesResultatListseach) {
        List<SallesResultat> sallesResultatListseach2 =
            sallesResultatList.stream()
                .filter(
                    e ->
                        e.getProf() == prof
                            && e.getDateExam().compareTo(resultat.getDateExam()) == 0)
                .collect(Collectors.toList());
        if (sallesResultatListseach2.size() == 1) return sallesResultatListseach2;
      }
    }
    return null;
  }

  private Integer getIndiceAmi(int i) {
    //    Integer indexsvgAmi2=sallesResultatList.get(i).getSvgIndice();
    // Integer indexsvgAmi2=1;
    if (sallesResultatList.get(i).getSvgIndice() == 1) {
      List<SallesResultat> sallesResultatListRech =
          sallesResultatList.stream()
              .filter(
                  e ->
                      e.getSvgIndice().equals(2)
                          && e.getIdS().equals(sallesResultatList.get(i).getIdS())
                          && e.getFillier() == sallesResultatList.get(i).getFillier())
              .collect(Collectors.toList());
      if (sallesResultatListRech.size() > 0) return sallesResultatListRech.get(0).getId();
    }
    if (sallesResultatList.get(i).getSvgIndice() == 2) {
      List<SallesResultat> sallesResultatListRech =
          sallesResultatList.stream()
              .filter(
                  e ->
                      e.getSvgIndice().equals(1)
                          && e.getIdS().equals(sallesResultatList.get(i).getIdS())
                          && e.getFillier() == sallesResultatList.get(i).getFillier())
              .collect(Collectors.toList());
      if (sallesResultatListRech.size() > 0) return sallesResultatListRech.get(0).getId();
    }

    return i;
  }

  private int getPofDoubleInSalle(Prof prof, Fillier fillier, Matiere matiere, Salles salle) {

    int countProfSall =
        (int)
            sallesResultatList.stream()
                .filter(
                    e ->
                        e.getSalles() == salle
                            && e.getProf() == prof
                            && e.getFillier() == fillier
                            && e.getMatiere() == matiere)
                .count();

    return countProfSall;
  }

  private int getcountPourHomme(int i) {
    Integer ids = sallesResultatList.get(i).getIdS();
    int countPourHomme =
        (int)
            sallesResultatList.stream()
                .filter(
                    e ->
                        e.getIdS().equals(ids)
                            && (e.getProf() == null
                                || e.getProf()
                                    .getSexe()
                                    .getCode()
                                    .equals(CodeContrainte.code_sexe_homme)))
                .count();
    return countPourHomme;
  }

  private int getcountPourFemme(int i) {
    Integer ids = sallesResultatList.get(i).getIdS();
    int countPourFemme =
        (int)
            sallesResultatList.stream()
                .filter(
                    e ->
                        e.getIdS().equals(ids)
                            && (e.getProf() == null
                                || e.getProf()
                                    .getSexe()
                                    .getCode()
                                    .equals(CodeContrainte.code_sexe_femme)))
                .count();
    return countPourFemme;
  }

  private List<SallesResultat> getlistsallesProfGarde(Prof prof) {
    return sallesResultatList.stream()
        .filter(e -> e.getProf() == prof)
        .collect(Collectors.toList());
  }

  private List<SallesResultat> getlistsallesProfMatin(Prof prof, LocalDate dateExam) {
    return sallesResultatList.stream()
        .filter(
            e ->
                e.getProf() == prof
                    && e.getAu().compareTo(CodeContrainte.time_speration_day) < 0
                    && e.getDateExam().compareTo(dateExam) == 0)
        .collect(Collectors.toList());
  }

  private List<SallesResultat> getlistsallesProfSoir(Prof prof, LocalDate dateExam) {
    return sallesResultatList.stream()
        .filter(
            e ->
                e.getProf() == prof
                    && e.getDu().compareTo(CodeContrainte.time_speration_day) > 0
                    && e.getDateExam().compareTo(dateExam) == 0)
        .collect(Collectors.toList());
  }

  private List<SallesResultat> getlistSallesProfTimeAndDay(
      Prof prof, LocalDate dateExam, LocalTime Du, LocalTime Au) {
    return sallesResultatList.stream()
        .filter(
            e ->
                e.getProf() != null
                    && e.getProf().getId().equals(prof.getId())
                    && e.getDateExam().compareTo(dateExam) == 0
                    && e.getDu().compareTo(Au) <= 0
                    && e.getAu().compareTo(Du) >= 0)
        .collect(Collectors.toList());
  }

  private List<SallesResultat> getlistSallesProfMaxMemeSalle(
      Prof prof, Fillier fillier, Salles salles) {
    return sallesResultatList.stream()
        .filter(e -> e.getProf() == prof && e.getFillier() == fillier && e.getSalles() == salles)
        .collect(Collectors.toList());
  }

  private List<SallesResultat> getlistSallesProfMaxMemeAmi(
      Prof prof, Prof profAmi, Fillier fillier) {
    return sallesResultatList.stream()
        .filter(e -> e.getProf() == prof && e.getProfAmi() == profAmi && e.getFillier() == fillier)
        .collect(Collectors.toList());
  }

  private Integer getlistsallesProfCountDay(Prof prof, LocalDate dateExam) {

    if (sallesResultatList.stream()
        .anyMatch(e -> e.getProf() == prof && e.getDateExam().compareTo(dateExam) == 0)) return 0;

    List<SallesResultat> listProfGard =
        sallesResultatList.stream().filter(e -> e.getProf() == prof).collect(Collectors.toList());

    Map<Object, Long> countMap =
        listProfGard.stream()
            .collect(Collectors.groupingBy(SallesResultat::getDateExam, Collectors.counting()));
    return countMap.size();
  }

  private List<SallesResultat> getlistsallesProfReserve(Prof prof, int i) {
    return sallesResultatList.stream()
        .filter(e -> e.getProf() == prof && e.getSalles().getIsReserve())
        .collect(Collectors.toList());
  }

  private Boolean getDoubleContrainte() {
    Map<Object, Long> ContraintMap =
        contrainteList.stream()
            .collect(
                Collectors.groupingBy(
                    e ->
                        Arrays.asList(
                            e.getContrainte(),
                            e.getProf(),
                            e.getMatiere(),
                            e.getFillier(),
                            e.getSalle(),
                            e.getDateExam(),
                            e.getMatin(),
                            e.getApresMidi()),
                    Collectors.counting()));

    Long countMap = ContraintMap.entrySet().stream().filter(m -> m.getValue() >= 2).count();
    //        Map<Object, Long> countMap3
    // =countMap.entrySet().stream().filter(m->m.getValue()>=2).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    //      Contrainte contrainte= (Contrainte) countMap3.entrySet().stream().findFirst().get();
    //        if(contrainte !=null) return  contrainte.getContrainte().getName();
    return countMap > 0;
  }

  private Boolean getCompareProfSalle() {
    List<SallesResultat> sallesResultatListRecherche =
        sallesResultatList.stream()
            .filter(
                e ->
                    !e.getSalles().getIsReserve()
                        && e.isForce()
                        && !e.getSalles().getIsPermanence())
            .collect(Collectors.toList());
    Set<Prof> mergeProf = new HashSet<Prof>();
    for (SallesResultat sallesResultat : sallesResultatListRecherche) {
      for (ProfsPoids profsPoids : sallesResultat.getProfsPoidsList()) {
        mergeProf.add(profsPoids.getProf());
      }
    }
    return false;
  }
}
