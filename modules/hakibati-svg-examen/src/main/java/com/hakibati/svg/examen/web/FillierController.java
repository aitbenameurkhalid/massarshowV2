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

public class FillierController {
  @Transactional
  public void DeleteFillier(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (!UserService.HasPermission("perm.hakibati.svg.examen.Fillier.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Fillier element = request.getContext().asType(Fillier.class);
      FillierRepository elemengRepository = Beans.get(FillierRepository.class);
      element = elemengRepository.find(element.getId());
      ContrainteRepository contrainteRepository = Beans.get(ContrainteRepository.class);
      ResultatsRepository resultatsRepository = Beans.get(ResultatsRepository.class);
      CalendrierRepository calendrierRepository = Beans.get(CalendrierRepository.class);
      SalleFillierRepository salleFillierRepository = Beans.get(SalleFillierRepository.class);

      List<Contrainte> contrainteList =
          contrainteRepository.all().filter("fillier = ?", element).fetch();
      for (Contrainte contrainte : contrainteList) contrainteRepository.remove(contrainte);
      List<Resultats> resultatsList =
          resultatsRepository
              .all()
              .filter(
                  "etablissement = ? AND fillier= ?", user.getEtablissementSelectionnee(), element)
              .fetch();
      for (Resultats resultats : resultatsList) resultatsRepository.remove(resultats);
      List<SalleFillier> salleFillierList =
          salleFillierRepository.all().filter("fillier = ?", element).fetch();
      for (SalleFillier salleFillier : salleFillierList)
        salleFillierRepository.remove(salleFillier);
      List<Calendrier> calendrierList =
          calendrierRepository
              .all()
              .filter(
                  "etablissement = ? AND fillier= ?", user.getEtablissementSelectionnee(), element)
              .fetch();
      for (Calendrier calendrier : calendrierList) calendrierRepository.remove(calendrier);

      if (element != null) {
        elemengRepository.remove(element);
        response.setReload(true);
      }
    }
  }
}
