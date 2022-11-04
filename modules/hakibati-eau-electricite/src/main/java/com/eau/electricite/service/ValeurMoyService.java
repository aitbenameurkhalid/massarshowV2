package com.eau.electricite.service;

import com.app.application.db.Etablissement;
import com.axelor.db.JPA;
import com.axelor.inject.Beans;
import com.hakibati.eau.electricite.db.ControleEau;
import com.hakibati.eau.electricite.db.ControleElectricite;
import com.hakibati.eau.electricite.db.repo.ControleEauRepository;
import com.hakibati.eau.electricite.db.repo.ControleElectriciteRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ValeurMoyService {

  public static Integer getvaleurMoyCompteurEau(
      String quey1, String quey2, Etablissement etablissement, String dateSelect) {
    Long idControl = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

    try {
      idControl = (Long) JPA.em().createQuery(quey1).getResultList().get(0);
      if (idControl == null) return null;
      ControleEau controleEau1 = Beans.get(ControleEauRepository.class).find(idControl);

      idControl = (Long) JPA.em().createQuery(quey2).getResultList().get(0);
      if (idControl == null) return null;
      ControleEau controleEau2 = Beans.get(ControleEauRepository.class).find(idControl);
      if (controleEau1 == null || controleEau2 == null) return null;
      int Diffvalue1_2 = (controleEau2.getValeurCompteur() - controleEau1.getValeurCompteur());
      Long DiffDate1_x =
          ChronoUnit.DAYS.between(
              controleEau1.getDateControle(), LocalDate.parse(dateSelect, formatter));
      if (DiffDate1_x == 0) return controleEau1.getValeurCompteur();
      Long DiffDate1_2 =
          ChronoUnit.DAYS.between(controleEau1.getDateControle(), controleEau2.getDateControle());
      if (DiffDate1_2 == 0) return controleEau1.getValeurCompteur();
      Integer res =
          Math.round((controleEau1.getValeurCompteur() + Diffvalue1_2 * DiffDate1_x / DiffDate1_2));
      return res;

    } catch (Exception e) {
      return null;
    }
  }

  public static Integer getvaleurMoyCompteurElectricite(
      String quey1, String quey2, Etablissement etablissement, String dateSelect) {
    Long idControl = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

    try {
      idControl = (Long) JPA.em().createQuery(quey1).getResultList().get(0);
      if (idControl == null) return null;
      ControleElectricite controle1 =
          Beans.get(ControleElectriciteRepository.class).find(idControl);

      idControl = (Long) JPA.em().createQuery(quey2).getResultList().get(0);
      if (idControl == null) return null;
      ControleElectricite controle2 =
          Beans.get(ControleElectriciteRepository.class).find(idControl);
      if (controle1 == null || controle2 == null) return null;
      int Diffvalue1_2 = (controle2.getValeurCompteur() - controle1.getValeurCompteur());
      Long DiffDate1_x =
          ChronoUnit.DAYS.between(
              controle1.getDateControle(), LocalDate.parse(dateSelect, formatter));
      if (DiffDate1_x == 0) return controle1.getValeurCompteur();
      Long DiffDate1_2 =
          ChronoUnit.DAYS.between(controle1.getDateControle(), controle2.getDateControle());
      if (DiffDate1_2 == 0) return controle1.getValeurCompteur();
      Integer res =
          Math.round((controle1.getValeurCompteur() + Diffvalue1_2 * DiffDate1_x / DiffDate1_2));
      return res;

    } catch (Exception e) {
      return null;
    }
  }
}
