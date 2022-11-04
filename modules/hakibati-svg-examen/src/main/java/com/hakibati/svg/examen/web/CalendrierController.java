package com.hakibati.svg.examen.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.Calendrier;
import com.hakibati.svg.examen.db.Contrainte;
import com.hakibati.svg.examen.db.repo.CalendrierRepository;
import com.hakibati.svg.examen.db.repo.ContrainteRepository;
import com.setting.service.UserService;
import java.util.List;
import java.util.stream.Collectors;

public class CalendrierController {

  @Transactional
  public void DeleteCalendrier(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();

    if (!UserService.HasPermission("perm.hakibati.svg.examen.Calendrier.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Calendrier element = request.getContext().asType(Calendrier.class);
      CalendrierRepository elemengRepository = Beans.get(CalendrierRepository.class);
      ContrainteRepository contrainteRepository = Beans.get(ContrainteRepository.class);
      List<Calendrier> calendrierList =
          elemengRepository
              .all()
              .filter("etablissement = ? ", user.getEtablissementSelectionnee())
              .fetch();
      Calendrier elementF = element;
      if (calendrierList.stream()
              .filter(
                  e ->
                      e.getDateExamine() != null
                          && e.getDateExamine().compareTo(elementF.getDateExamine()) == 0)
              .count()
          > 1) {
        List<Contrainte> contrainteList =
            contrainteRepository
                .all()
                .filter("etablissement = ? ", user.getEtablissementSelectionnee())
                .fetch();
        for (Contrainte contrainte :
            contrainteList.stream()
                .filter(
                    e ->
                        e.getDateExam() != null
                            && e.getDateExam().compareTo(elementF.getDateExamine()) == 0)
                .collect(Collectors.toList())) contrainteRepository.remove(contrainte);
      }

      element = elemengRepository.find(element.getId());
      if (element != null) {
        elemengRepository.remove(element);

        response.setReload(true);
      }
    }
  }
}
