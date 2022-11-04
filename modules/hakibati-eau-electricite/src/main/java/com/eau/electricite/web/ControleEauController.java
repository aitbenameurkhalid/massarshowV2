package com.eau.electricite.web;

import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.eau.electricite.db.ControleEau;
import com.hakibati.eau.electricite.db.repo.ControleEauRepository;
import com.setting.service.UserService;

public class ControleEauController {
  @Transactional
  public void deleteControleEauSelect(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission("perm.hakibati.eau.electricite.ControleEau.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      ControleEau controleEauSelect = request.getContext().asType(ControleEau.class);
      ControleEauRepository controleEauRepository = Beans.get(ControleEauRepository.class);
      ControleEau controleEau = controleEauRepository.find(controleEauSelect.getId());
      if (controleEau != null) {
        controleEauRepository.remove(controleEau);
        response.setReload(true);
      }
    }
  }
}
