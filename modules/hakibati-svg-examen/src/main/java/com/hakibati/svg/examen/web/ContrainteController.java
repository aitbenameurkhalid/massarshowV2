package com.hakibati.svg.examen.web;

import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.Contrainte;
import com.hakibati.svg.examen.db.repo.ContrainteRepository;
import com.setting.service.UserService;

public class ContrainteController {

  //  public void DeleteContrainte(ActionRequest request, ActionResponse response) {
  //    if (!UserService.HasPermission("perm.hakibati.svg.examen.Contrainte.select.etab.rwcde")) {
  //      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
  //    }
  //  }

  @Transactional
  public void DeleteContrainte(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.svg.examen.Contrainte.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Contrainte element = request.getContext().asType(Contrainte.class);
      ContrainteRepository elemengRepository = Beans.get(ContrainteRepository.class);
      element = elemengRepository.find(element.getId());
      if (element != null) {
        elemengRepository.remove(element);
        response.setReload(true);
      }
    }
  }
}
