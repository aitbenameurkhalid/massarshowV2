package com.eau.electricite.web;

import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.eau.electricite.db.ControleElectricite;
import com.hakibati.eau.electricite.db.repo.ControleElectriciteRepository;
import com.setting.service.UserService;

public class ControlElectriciteController {
  @Transactional
  public void deleteControleELectriciteSelect(ActionRequest request, ActionResponse response) {
    if (!UserService.HasPermission(
        "perm.hakibati.eau.electricite.ControleElectricite.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      ControleElectricite controleElectriciteSelect =
          request.getContext().asType(ControleElectricite.class);
      ControleElectriciteRepository electriciteRepository =
          Beans.get(ControleElectriciteRepository.class);
      ControleElectricite controleElectricite =
          electriciteRepository.find(controleElectriciteSelect.getId());
      if (controleElectricite != null) {
        electriciteRepository.remove(controleElectricite);
        response.setReload(true);
      }
    }
  }
}
