package com.hakibati.svg.examen.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.hakibati.setting.db.Listcontraint;
import com.hakibati.setting.db.repo.ListcontraintRepository;
import com.hakibati.svg.examen.db.Calendrier;
import com.hakibati.svg.examen.db.Contrainte;
import com.hakibati.svg.examen.db.Prof;
import com.hakibati.svg.examen.db.SalleFillier;
import com.hakibati.svg.examen.db.repo.CalendrierRepository;
import com.hakibati.svg.examen.db.repo.ContrainteRepository;
import com.hakibati.svg.examen.db.repo.ProfRepository;
import com.hakibati.svg.examen.db.repo.SalleFillierRepository;
import com.hakibati.svg.examen.web.CodeContrainte;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CentreExamService {

  public static Integer MiseAjourContrainteCollectif() {
    User user = AuthUtils.getUser();

    List<Contrainte> contrainteList =
        Beans.get(ContrainteRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    Integer profisseurs = 0;
    Integer salles = 10;
    Integer Filliers = 25;

    List<Listcontraint> listcontraintListPrincipal =
        Beans.get(ListcontraintRepository.class)
            .all()
            .filter(
                "typeContrainte = ? Or typeContrainte = ? Or typeContrainte = ? ",
                profisseurs,
                salles,
                Filliers)
            .fetch();

    //  Listcontraint contraintPrincipal=
    // Beans.get(ListcontraintRepository.class).findByCode(CodeContrainte.horaires_pas_disponibles_un_professeur);
    for (Listcontraint contraintPrincipal : listcontraintListPrincipal) {
      Contrainte contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(e.getContrainte().getId(), contraintPrincipal.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraintPrincipal);
        if (Objects.equals(contraintPrincipal.getTypeContrainte(), profisseurs))
          contrainte.setValeur(10);
        if (Objects.equals(contraintPrincipal.getTypeContrainte(), salles)) contrainte.setValeur(0);
        if (Objects.equals(
            contraintPrincipal.getCode(), CodeContrainte.maximum_professeurs_toutes_salles))
          contrainte.setValeur(2);

        if (Objects.equals(contraintPrincipal.getTypeContrainte(), Filliers))
          contrainte.setValeur(2);

        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
    }
    return listcontraintListPrincipal.size();
  }

  public static Integer MiseAjourContrainteProf() {
    User user = AuthUtils.getUser();
    List<Calendrier> calendrierList =
        Beans.get(CalendrierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<Contrainte> contrainteList =
        Beans.get(ContrainteRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<SalleFillier> salleFillierList =
        Beans.get(SalleFillierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<Prof> profList =
        Beans.get(ProfRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();

    Listcontraint contraint_horaires_pas_disponibles_un_professeur =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.horaires_pas_disponibles_un_professeur);
    Listcontraint contraintPermanance =
        Beans.get(ListcontraintRepository.class).findByCode(CodeContrainte.professeurs_permanences);
    Listcontraint contraintSalleNonDisponible =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.salle_pas_disponibles_un_professeur);
    Listcontraint contraint_maximum_garde_matiere_un_prof =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_garde_matiere_un_prof);

    for (Calendrier calendrier : calendrierList) {
      Contrainte contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_horaires_pas_disponibles_un_professeur.getId())
                          && e.getDateExam() != null
                          && e.getDateExam().compareTo(calendrier.getDateExamine()) == 0
                          && e.getMatin() != null
                          && e.getMatin())
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_horaires_pas_disponibles_un_professeur);
        contrainte.setApresMidi(false);
        contrainte.setMatin(true);
        contrainte.setDateExam(calendrier.getDateExamine());
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_horaires_pas_disponibles_un_professeur.getId())
                          && e.getDateExam() != null
                          && e.getDateExam().compareTo(calendrier.getDateExamine()) == 0
                          && e.getApresMidi() != null
                          && e.getApresMidi())
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_horaires_pas_disponibles_un_professeur);
        contrainte.setApresMidi(true);
        contrainte.setMatin(false);
        contrainte.setDateExam(calendrier.getDateExamine());
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(e.getContrainte().getId(), contraintPermanance.getId())
                          && e.getMatiere() != null
                          && Objects.equals(e.getMatiere().getId(), calendrier.getMatiere().getId())
                          && e.getFillier() != null
                          && Objects.equals(
                              e.getFillier().getId(), calendrier.getFillier().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraintPermanance);
        contrainte.setMatiere(calendrier.getMatiere());
        contrainte.setFillier(calendrier.getFillier());
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
      List<SalleFillier> salleFillierListTemp =
          salleFillierList.stream()
              .filter(
                  e ->
                      e.getFillier() != null
                          && Objects.equals(
                              e.getFillier().getId(), calendrier.getFillier().getId()))
              .collect(Collectors.toList());
      for (SalleFillier salleFillier : salleFillierListTemp) {
        contrainte =
            contrainteList.stream()
                .filter(
                    e ->
                        e.getContrainte() != null
                            && Objects.equals(
                                e.getContrainte().getId(), contraintSalleNonDisponible.getId())
                            && e.getMatiere() != null
                            && Objects.equals(
                                e.getMatiere().getId(), calendrier.getMatiere().getId())
                            && e.getFillier() != null
                            && Objects.equals(
                                e.getFillier().getId(), calendrier.getFillier().getId())
                            && e.getSalle() != null
                            && Objects.equals(
                                e.getSalle().getId(), salleFillier.getSalles().getId()))
                .findFirst()
                .orElse(null);
        if (contrainte == null) {
          contrainte = new Contrainte();
          contrainte.setContrainte(contraintSalleNonDisponible);
          contrainte.setMatiere(calendrier.getMatiere());
          contrainte.setFillier(calendrier.getFillier());
          contrainte.setSalle(salleFillier.getSalles());
          Beans.get(ContrainteRepository.class).save(contrainte);
          contrainteList.add(contrainte);
        }
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_garde_matiere_un_prof.getId())
                          && e.getMatiere() != null
                          && Objects.equals(e.getMatiere().getId(), calendrier.getMatiere().getId())
                          && e.getFillier() != null
                          && Objects.equals(
                              e.getFillier().getId(), calendrier.getFillier().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_garde_matiere_un_prof);
        contrainte.setMatiere(calendrier.getMatiere());
        contrainte.setFillier(calendrier.getFillier());
        contrainte.setValeur(0);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
    }

    Listcontraint contraintMaximum_garde_cours_exam_un_prof =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_garde_cours_exam_un_prof);
    Listcontraint contraint_maximum_garde_meme_salle_pr_un_prof =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_garde_meme_salle_pr_un_prof);
    Listcontraint contraint_maximum_garde_meme_professeur_pr_un_prof =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_garde_meme_professeur_pr_un_prof);
    Listcontraint contraint_maximum_garde_meme_matiere_specialise_pr_un_prof =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_garde_meme_matiere_specialise_pr_un_prof);
    Listcontraint contraint_maximum_garde_matin_apres_midi_pr_un_prof =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_garde_matin_apres_midi_pr_un_prof);
    Listcontraint contraint_maximum_reserve_un_prof =
        Beans.get(ListcontraintRepository.class).findByCode(CodeContrainte.maximum_reserve_un_prof);
    Listcontraint contraint_maximum_jour_un_prof =
        Beans.get(ListcontraintRepository.class).findByCode(CodeContrainte.maximum_jour_un_prof);

    for (Prof prof : profList) {
      Contrainte contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraintMaximum_garde_cours_exam_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraintMaximum_garde_cours_exam_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(10);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_garde_meme_salle_pr_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_garde_meme_salle_pr_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(20);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_garde_meme_professeur_pr_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_garde_meme_professeur_pr_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(20);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_garde_meme_matiere_specialise_pr_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_garde_meme_matiere_specialise_pr_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(20);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_garde_matin_apres_midi_pr_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_garde_matin_apres_midi_pr_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(10);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(), contraint_maximum_reserve_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_reserve_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(20);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(), contraint_maximum_jour_un_prof.getId())
                          && e.getProf() != null
                          && Objects.equals(e.getProf().getId(), prof.getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_jour_un_prof);
        contrainte.setProf(prof);
        contrainte.setValeur(20);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
    }

    return 0;
  }

  public static Integer MiseAjourContrainteSalle() {
    User user = AuthUtils.getUser();
    List<Calendrier> calendrierList =
        Beans.get(CalendrierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<Contrainte> contrainteList =
        Beans.get(ContrainteRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<SalleFillier> salleFillierList =
        Beans.get(SalleFillierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();

    Listcontraint contraint_minimum_professeurs_homme_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_professeurs_homme_un_salle);
    Listcontraint contraint_minimum_professeurs_femme_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_professeurs_femme_un_salle);
    Listcontraint contraint_minimum_professeurs_qualifie_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_professeurs_qualifie_un_salle);
    Listcontraint contraint_minimum_professeurs_college_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_professeurs_college_un_salle);
    Listcontraint contraint_minimum_professeurs_primaire_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_professeurs_primaire_un_salle);
    Listcontraint contraint_minimum_professeurs_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_professeurs_un_salle);
    Listcontraint contraint_maximum_professeurs_un_salle =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_professeurs_un_salle);

    for (SalleFillier salleFillier : salleFillierList) {
      Contrainte contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_professeurs_homme_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_professeurs_homme_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_professeurs_femme_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_professeurs_femme_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        contrainte.setValeur(0);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_professeurs_qualifie_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_professeurs_qualifie_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        contrainte.setValeur(0);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_professeurs_college_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_professeurs_college_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        contrainte.setValeur(0);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_professeurs_primaire_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_professeurs_primaire_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        contrainte.setValeur(0);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_professeurs_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_professeurs_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        contrainte.setValeur(2);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_professeurs_un_salle.getId())
                          && e.getSalle() != null
                          && Objects.equals(e.getSalle().getId(), salleFillier.getSalles().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_professeurs_un_salle);
        contrainte.setSalle(salleFillier.getSalles());
        contrainte.setValeur(2);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
    }

    return 0;
  }

  public static Integer MiseAjourContrainteFillier() {
    User user = AuthUtils.getUser();
    List<Calendrier> calendrierList =
        Beans.get(CalendrierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<Contrainte> contrainteList =
        Beans.get(ContrainteRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<SalleFillier> salleFillierList =
        Beans.get(SalleFillierRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();

    Listcontraint contraint_minimum_reservation_un_Fillier =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.minimum_reservation_un_Fillier);
    Listcontraint contraint_maximum_reservation_un_Fillier =
        Beans.get(ListcontraintRepository.class)
            .findByCode(CodeContrainte.maximum_reservation_un_Fillier);

    for (SalleFillier calendrier : salleFillierList) {

      Contrainte contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_minimum_reservation_un_Fillier.getId())
                          && e.getFillier() != null
                          && Objects.equals(
                              e.getFillier().getId(), calendrier.getFillier().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_minimum_reservation_un_Fillier);
        contrainte.setFillier(calendrier.getFillier());
        contrainte.setValeur(2);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }

      contrainte =
          contrainteList.stream()
              .filter(
                  e ->
                      e.getContrainte() != null
                          && Objects.equals(
                              e.getContrainte().getId(),
                              contraint_maximum_reservation_un_Fillier.getId())
                          && e.getFillier() != null
                          && Objects.equals(
                              e.getFillier().getId(), calendrier.getFillier().getId()))
              .findFirst()
              .orElse(null);
      if (contrainte == null) {
        contrainte = new Contrainte();
        contrainte.setContrainte(contraint_maximum_reservation_un_Fillier);
        contrainte.setFillier(calendrier.getFillier());
        contrainte.setValeur(2);
        Beans.get(ContrainteRepository.class).save(contrainte);
        contrainteList.add(contrainte);
      }
    }

    return 0;
  }
}
