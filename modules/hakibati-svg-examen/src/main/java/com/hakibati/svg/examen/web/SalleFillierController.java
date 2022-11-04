package com.hakibati.svg.examen.web;

import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.SalleFillier;
import com.hakibati.svg.examen.db.repo.SalleFillierRepository;
import com.setting.service.UserService;

public class SalleFillierController {

  @Transactional
  public void DeleteSalleFillier(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.svg.examen.SalleFillier.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      SalleFillier element = request.getContext().asType(SalleFillier.class);
      SalleFillierRepository elemengRepository = Beans.get(SalleFillierRepository.class);
      element = elemengRepository.find(element.getId());
      if (element != null) {
        elemengRepository.remove(element);
        response.setReload(true);
      }
    }
  }
}
