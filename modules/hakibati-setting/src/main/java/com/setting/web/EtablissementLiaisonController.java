package com.setting.web;

import com.app.application.db.Etablissement;
import com.app.application.db.repo.EtablissementRepository;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class EtablissementLiaisonController {
  public void addEtabPrincipal(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    Long codeEtab = Long.parseLong((String) request.getContext().get("codeEtab"));
    Etablissement etablissement = getEtablissemnt(codeEtab);
    //        user.getGroup().getCode().equals("admins");
    if (etablissement == null)
      response.setAlert(I18n.get("Il n'y a pas d'établissement avec ce code"));
    if (!user.getGroup().getCode().equals("admins")) {
      assert etablissement != null;
      if (etablissement.getTypeEtablissemnt()
          <= user.getEtablissementSelectionnee().getTypeEtablissemnt())
        response.setAlert(I18n.get("Il n'y a pas d'établissement avec ce code"));
    }
    response.setValue("etablissementPrincipal", etablissement);
  }

  private Etablissement getEtablissemnt(Long codeEtab) {
    EtablissementRepository etablissementRepository = Beans.get(EtablissementRepository.class);
    return etablissementRepository.find(codeEtab);
  }
}
