package com.setting.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.i18n.I18n;
import com.axelor.meta.CallMethod;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.time.LocalDate;

public class PermissionEtablissemntService {
  @CallMethod
  public static boolean getvalideModuleEtablissement(Integer CodeRoleDescribe) {
    User user = AuthUtils.getUser();
    LocalDate Currentdate = LocalDate.now();

    Long etablissementRolsDescribeListCountTousILLIMITE =
        user.getEtablissementSelectionnee().getEtablissementRolsDescribe().stream()
            .filter(
                e ->
                    e.getActif()
                        && e.getRoleDescribes().getCode().equals(-1)
                        && e.getDatePeremption().compareTo(Currentdate) > 0
                        && e.getActif())
            .count();
    if (etablissementRolsDescribeListCountTousILLIMITE > 0) return true;

    Long etablissementRolsDescribeListCountTousLIMITE =
        user.getEtablissementSelectionnee().getEtablissementRolsDescribe().stream()
            .filter(
                e ->
                    e.getDatePeremption().compareTo(Currentdate) > 0
                        && e.getActif()
                        && e.getRoleDescribes().getCode().equals(0))
            .count();

    if (etablissementRolsDescribeListCountTousLIMITE > 0) return true;

    Long etablissementRolsDescribeListCount =
        user.getEtablissementSelectionnee().getEtablissementRolsDescribe().stream()
            .filter(
                e ->
                    e.getDatePeremption().compareTo(Currentdate) > 0
                        && e.getActif()
                        && e.getRoleDescribes().getCode().equals(CodeRoleDescribe))
            .count();
    return (etablissementRolsDescribeListCount > 0);
  }

  //  @CallMethod
  public void ShowEtablissementActivation(ActionRequest request, ActionResponse response) {
    User user = AuthUtils.getUser();
    response.setView(
        ActionView.define(I18n.get("Activation Etablissement"))
            .model("com.app.application.db.Etablissement")
            .add("form", "activation-compte-etablissement-form")
            .domain("self.id = " + user.getEtablissementSelectionnee().getId())
            .param("forceEdit", "true")
            .param("popup", "true")
            .param("show-toolbar", "false")
            .param("show-confirm", "false")
            .param("popup-save", "false")
            .context("_showRecord", user.getEtablissementSelectionnee().getId())
            .map());
  }
}
