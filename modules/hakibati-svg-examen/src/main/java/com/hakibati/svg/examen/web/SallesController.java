package com.hakibati.svg.examen.web;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JPA;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.persist.Transactional;
import com.hakibati.svg.examen.db.*;
import com.hakibati.svg.examen.db.repo.ContrainteRepository;
import com.hakibati.svg.examen.db.repo.ResultatsRepository;
import com.hakibati.svg.examen.db.repo.SalleFillierRepository;
import com.hakibati.svg.examen.db.repo.SallesRepository;
import com.setting.service.UserService;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @Transactional
public class SallesController {
  private Logger log = LoggerFactory.getLogger(SallesController.class);

  @Transactional
  public void OnAjouteSalleCollectif(ActionRequest request, ActionResponse response) {
    // response.setError("refresh-app");
    try {
      int maxSalle = (int) request.getContext().get("_au");
      int minSalle = (int) request.getContext().get("_du");
      Map fillierSelect = (Map) request.getContext().get("fillier");
      Integer fillierId = (Integer) fillierSelect.get("id");
      Fillier fillier = JPA.find(Fillier.class, Long.valueOf(fillierId));
      for (int i = minSalle; i <= maxSalle; i++) {
        User user = AuthUtils.getUser();
        String SalleName = i + "";
        if (i < 10) SalleName = "0" + i;
        Salles salle =
            Beans.get(SallesRepository.class)
                .all()
                .filter(
                    "etablissement = ? AND name = ?",
                    user.getEtablissementSelectionnee(),
                    SalleName)
                .fetchOne();
        if (salle == null) {
          salle = new Salles();
          salle.setName(SalleName);
        }
        Beans.get(SallesRepository.class).save(salle);
        SalleFillier salleFillier =
            Beans.get(SalleFillierRepository.class)
                .all()
                .filter(
                    "etablissement = ? AND fillier = ? AND salles = ?",
                    user.getEtablissementSelectionnee(),
                    fillier,
                    salle)
                .fetchOne();
        if (salleFillier == null) {
          salleFillier = new SalleFillier();
          salleFillier.setFillier(fillier);
          salleFillier.setSalles(salle);
        }
        Beans.get(SalleFillierRepository.class).save(salleFillier);
      }
      //      response.setAlert(I18n.get("Opération terminée"));
      response.setNotify(I18n.get("Ajout réussi"));

    } catch (Exception e) {
      response.setAlert("error = " + e.getMessage());
    }
    //    response.setSignal("refresh-app", true);
  }

  @Transactional
  public void DeleteSalles(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    if (!UserService.HasPermission("perm.hakibati.svg.examen.Salles.select.etab.rwcde")) {
      response.setAlert(I18n.get("Vous n'avez pas l'autorisation"));
    } else {
      Salles element = request.getContext().asType(Salles.class);
      SallesRepository elemengRepository = Beans.get(SallesRepository.class);
      element = elemengRepository.find(element.getId());
      ContrainteRepository contrainteRepository = Beans.get(ContrainteRepository.class);
      ResultatsRepository resultatsRepository = Beans.get(ResultatsRepository.class);
      SalleFillierRepository salleFillierRepository = Beans.get(SalleFillierRepository.class);

      List<Contrainte> contrainteList =
          contrainteRepository.all().filter("salle = ?", element).fetch();
      for (Contrainte contrainte : contrainteList) contrainteRepository.remove(contrainte);

      List<Resultats> resultatsList =
          resultatsRepository
              .all()
              .filter(
                  "etablissement = ? AND salles= ?", user.getEtablissementSelectionnee(), element)
              .fetch();
      for (Resultats resultats : resultatsList) resultatsRepository.remove(resultats);

      List<SalleFillier> salleFillierList =
          salleFillierRepository.all().filter("salles = ?", element).fetch();
      for (SalleFillier salleFillier : salleFillierList)
        salleFillierRepository.remove(salleFillier);

      if (element != null) {
        elemengRepository.remove(element);
        response.setReload(true);
      }
    }
  }
}
