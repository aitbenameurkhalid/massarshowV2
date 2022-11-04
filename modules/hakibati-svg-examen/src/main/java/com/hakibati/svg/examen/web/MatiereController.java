package com.hakibati.svg.examen.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.*;
import com.hakibati.svg.examen.db.repo.*;
import com.setting.service.UserService;
import java.util.List;

public class MatiereController {

  @Transactional
  public void DeleteMatiere(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (!UserService.HasPermission("perm.hakibati.svg.examen.Matiere.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Matiere element = request.getContext().asType(Matiere.class);
      MatiereRepository elemengRepository = Beans.get(MatiereRepository.class);
      element = elemengRepository.find(element.getId());
      ContrainteRepository contrainteRepository = Beans.get(ContrainteRepository.class);
      ResultatsRepository resultatsRepository = Beans.get(ResultatsRepository.class);
      CalendrierRepository calendrierRepository = Beans.get(CalendrierRepository.class);
      ProfRepository profRepository = Beans.get(ProfRepository.class);
      List<Contrainte> contrainteList =
          contrainteRepository.all().filter("matiere = ?", element).fetch();
      for (Contrainte contrainte : contrainteList) contrainteRepository.remove(contrainte);

      List<Resultats> resultatsList =
          resultatsRepository
              .all()
              .filter(
                  "etablissement = ? AND matiere= ?", user.getEtablissementSelectionnee(), element)
              .fetch();
      for (Resultats resultats : resultatsList) resultatsRepository.remove(resultats);
      List<Calendrier> calendrierList =
          calendrierRepository
              .all()
              .filter(
                  "etablissement = ? AND matiere= ?", user.getEtablissementSelectionnee(), element)
              .fetch();
      for (Calendrier calendrier : calendrierList) calendrierRepository.remove(calendrier);

      List<Prof> profList =
          profRepository
              .all()
              .filter(
                  "etablissement = ? AND matiere= ?", user.getEtablissementSelectionnee(), element)
              .fetch();
      ProfController profController = new ProfController();
      for (Prof prof : profList) {
        profController.DeleteProf(prof);
      }

      if (element != null) {
        elemengRepository.remove(element);
        response.setReload(true);
      }
    }
  }
}
