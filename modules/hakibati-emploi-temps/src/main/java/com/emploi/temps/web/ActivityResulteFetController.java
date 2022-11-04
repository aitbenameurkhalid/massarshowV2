package com.emploi.temps.web;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.hakibati.emploi.db.ActivityResulteFet;

public class ActivityResulteFetController {

  public void OnchangeSpacetime(ActionRequest request, ActionResponse response) {
    ActivityResulteFet activityResulteFet = request.getContext().asType(ActivityResulteFet.class);

    response.setAlert("change time speace");
  }
}
