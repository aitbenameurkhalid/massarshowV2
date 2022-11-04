package com.eau.electricite.service;

import com.app.application.db.Etablissement;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.eau.electricite.db.*;
import com.hakibati.eau.electricite.db.repo.*;
import java.util.List;

public class GestionEauElectriciteService {
  @Singleton
  @Transactional
  public static void savgestionEauAlectricite(GestionEauElectricite gestionEauElectricite) {
    ConsommationEauRepository consommationEauRepository =
        Beans.get(ConsommationEauRepository.class);
    ConsommationElectriciteRepository consommationElectriciteRepository =
        Beans.get(ConsommationElectriciteRepository.class);
    ControleEauRepository controleEauRepository = Beans.get(ControleEauRepository.class);
    ControleElectriciteRepository controleElectriciteRepository =
        Beans.get(ControleElectriciteRepository.class);

    List<ConsommationEau> consommationEauList =
        consommationEauRepository
            .all()
            .filter("etablissement = ? ", gestionEauElectricite.getEtablissement())
            .fetch();
    for (ConsommationEau consommationEau : consommationEauList) {
      consommationEau.setCodeGresa(gestionEauElectricite.getCodeGresa());
      consommationEau.setNumeroContratEau(gestionEauElectricite.getNumeroContratEau());
    }
    List<ConsommationElectricite> consommationElectriciteList =
        consommationElectriciteRepository
            .all()
            .filter("etablissement = ? ", gestionEauElectricite.getEtablissement())
            .fetch();
    for (ConsommationElectricite consommationElectricite : consommationElectriciteList) {
      consommationElectricite.setCodeGresa(gestionEauElectricite.getCodeGresa());
      consommationElectricite.setNumeroContratElectricite(
          gestionEauElectricite.getNumeroContratElectricite());
    }

    List<ControleEau> controleEauList =
        controleEauRepository
            .all()
            .filter("etablissement = ? ", gestionEauElectricite.getEtablissement())
            .fetch();
    for (ControleEau controleEau : controleEauList) {
      controleEau.setCodeGresa(gestionEauElectricite.getCodeGresa());
      controleEau.setNumeroContratEau(gestionEauElectricite.getNumeroContratEau());
    }
    List<ControleElectricite> controleElectriciteList =
        controleElectriciteRepository
            .all()
            .filter("etablissement = ? ", gestionEauElectricite.getEtablissement())
            .fetch();
    for (ControleElectricite controleElectricite : controleElectriciteList) {
      controleElectricite.setCodeGresa(gestionEauElectricite.getCodeGresa());
      controleElectricite.setNumeroContratElectricite(
          gestionEauElectricite.getNumeroContratElectricite());
    }
  }

  @CallMethod
  public String getGresa(Etablissement etablissement) {
    User user = AuthUtils.getUser();
    if (etablissement == null) etablissement = user.getEtablissementSelectionnee();
    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    GestionEauElectricite gestionEauElectricite =
        gestionEauElectriciteRepository.findByEtab(etablissement);
    if (gestionEauElectricite == null) return "";
    return gestionEauElectricite.getCodeGresa();
  }

  @CallMethod
  public String getContratEau(Etablissement etablissement) {
    User user = AuthUtils.getUser();
    if (etablissement == null) etablissement = user.getEtablissementSelectionnee();
    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    GestionEauElectricite gestionEauElectricite =
        gestionEauElectriciteRepository.findByEtab(etablissement);
    if (gestionEauElectricite == null) return "";
    return gestionEauElectricite.getNumeroContratEau();
  }

  @CallMethod
  public String getContratElectricite(Etablissement etablissement) {
    User user = AuthUtils.getUser();
    if (etablissement == null) etablissement = user.getEtablissementSelectionnee();
    GestionEauElectriciteRepository gestionEauElectriciteRepository =
        Beans.get(GestionEauElectriciteRepository.class);
    GestionEauElectricite gestionEauElectricite =
        gestionEauElectriciteRepository.findByEtab(etablissement);
    if (gestionEauElectricite == null) return "";
    return gestionEauElectricite.getNumeroContratElectricite();
  }
  //  @CallMethod
  //  public GestionEauElectricite getGestionEauElectricite(Etablissement etablissement) {
  //    GestionEauElectriciteRepository gestionEauElectriciteRepository =
  //            Beans.get(GestionEauElectriciteRepository.class);
  //    return gestionEauElectriciteRepository.findByEtab(etablissement);
  //  }
  //    @CallMethod
  //    public String getGresa() {
  //        return "khald";
  //    }
}
