package com.ressource.humaine.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;
import com.hakibati.ressource.humaine.db.GestionRH;
import com.hakibati.ressource.humaine.db.GrevePersonnel;
import com.hakibati.ressource.humaine.db.GreveRH;
import com.hakibati.ressource.humaine.db.PersonnelRh;
import com.hakibati.ressource.humaine.db.repo.GestionRHRepository;
import java.util.List;

public class GreveRHController {
  @Inject GestionRHRepository gestionRHRepository;

  public void OnLoadGreveRH(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    GreveRH greveRH = request.getContext().asType(GreveRH.class);
    if (greveRH == null) return;
    GestionRH gestionRH = Beans.get(GestionRHRepository.class).find(greveRH.getGestionRH().getId());
    List<PersonnelRh> personnelRhList = gestionRH.getPersonnel();
    List<GrevePersonnel> grevePersonnelList = greveRH.getGrevePersonnel();

    for (PersonnelRh personnelRh : personnelRhList) {
      GrevePersonnel grevePersonnel =
          grevePersonnelList.stream()
              .filter(e -> e.getPersonnelRh() != null && e.getPersonnelRh() == personnelRh)
              .findFirst()
              .orElse(null);
      if (grevePersonnel == null) {
        grevePersonnel = new GrevePersonnel();
        grevePersonnel.setPersonnelRh(personnelRh);
        grevePersonnel.setGreveRH(greveRH);
        grevePersonnelList.add(grevePersonnel);
      }
    }
    response.setValue("grevePersonnel", grevePersonnelList);
  }

  public void OnSelectDateGreveRH(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    GreveRH greveRH = request.getContext().asType(GreveRH.class);
    GestionRH gestionRH =
        gestionRHRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetchOne();
    //        GestionRH gestionRH =
    // Beans.get(GestionRHRepository.class).find(greveRH.getGestionRH().getId());
    List<PersonnelRh> personnelRhList = gestionRH.getPersonnel();
    List<GrevePersonnel> grevePersonnelList = greveRH.getGrevePersonnel();
    for (PersonnelRh personnelRh : personnelRhList) {
      GrevePersonnel grevePersonnel =
          grevePersonnelList.stream()
              .filter(e -> e.getPersonnelRh() != null && e.getPersonnelRh() == personnelRh)
              .findFirst()
              .orElse(null);
      if (grevePersonnel == null) {
        grevePersonnel = new GrevePersonnel();
        grevePersonnel.setPersonnelRh(personnelRh);
        grevePersonnel.setGreveRH(greveRH);
        grevePersonnelList.add(grevePersonnel);
      }
    }
    response.setValue("grevePersonnel", grevePersonnelList);
  }
}
