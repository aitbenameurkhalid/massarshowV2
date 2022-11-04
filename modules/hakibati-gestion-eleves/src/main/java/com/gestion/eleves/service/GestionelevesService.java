package com.gestion.eleves.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.google.inject.Inject;
import com.hakibati.gestion.eleves.db.Classe;
import com.hakibati.gestion.eleves.db.Eleve;
import com.hakibati.gestion.eleves.db.Gestioneleves;
import com.hakibati.gestion.eleves.db.Niveau;
import com.hakibati.gestion.eleves.db.repo.ClasseRepository;
import com.hakibati.gestion.eleves.db.repo.GestionelevesRepository;
import com.hakibati.gestion.eleves.db.repo.NiveauRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GestionelevesService {
  @Inject GestionelevesRepository gestionelevesRepository;

  @CallMethod
  public Gestioneleves getCurretGestioneleves() {
    User user = AuthUtils.getUser();

    if (gestionelevesRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) {
      Gestioneleves gestioneleves = new Gestioneleves();
      //            resultatFet.setName(user.getEtablissementSelectionnee().getName());
      Beans.get(GestionelevesRepository.class).save(gestioneleves);
    }
    return gestionelevesRepository
        .all()
        .filter("etablissement = ?", user.getEtablissementSelectionnee())
        .fetchOne();
  }

  public String generateGroupeService(
      Map classeMap, Map niveauMap, int maxGroupeOne, Gestioneleves gestioneleves) {
    //    Map<String, Integer> classeMap,
    //    Map<String, Integer> niveauMap,

    if (niveauMap == null) {
      List<Eleve> eleveList =
          gestioneleves.getEleves().stream()
              .filter(e -> !e.getArchived())
              .collect(Collectors.toList());
      for (Eleve eleve : eleveList) {
        if (eleve.getIdclasse() <= maxGroupeOne) {
          eleve.setIdgroupe(1);
        } else {
          eleve.setIdgroupe(2);
        }
      }
      return "ok tous";
    }
    if (classeMap == null) {
      Integer niveauId = (Integer) niveauMap.get("id");
      Niveau niveau = Beans.get(NiveauRepository.class).find(Long.valueOf(niveauId));
      List<Eleve> eleveList =
          gestioneleves.getEleves().stream()
              .filter(e -> !e.getArchived() && e.getClasse().getNiveau() == niveau)
              .collect(Collectors.toList());
      for (Eleve eleve : eleveList) {
        if (eleve.getIdclasse() <= maxGroupeOne) {
          eleve.setIdgroupe(1);
        } else {
          eleve.setIdgroupe(2);
        }
      }
      return "ok all niveau";
    }
    Integer classeId = (Integer) classeMap.get("id");
    Classe classe = Beans.get(ClasseRepository.class).find(Long.valueOf(classeId));
    List<Eleve> eleveList =
        gestioneleves.getEleves().stream()
            .filter(e -> !e.getArchived() && e.getClasse() == classe)
            .collect(Collectors.toList());
    for (Eleve eleve : eleveList) {
      if (eleve.getIdclasse() <= maxGroupeOne) {
        eleve.setIdgroupe(1);
      } else {
        eleve.setIdgroupe(2);
      }
    }
    return "all classe";
  }

  @CallMethod
  public String UppercaseTransfert(String str) {
    if (str == null) return null;
    return str.toUpperCase();
  }
}
