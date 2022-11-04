package com.ass.enajah.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.inject.Beans;
import com.hakibati.ass.enajah.db.AssEnajahInfo;
import com.hakibati.ass.enajah.db.repo.AssEnajahInfoRepository;

public class AssInfoService {
  public Long getCurretAssInfo() {
    User user = AuthUtils.getUser();
    if (user.getEtablissementSelectionnee() == null) return null;
    AssEnajahInfoRepository assEnajahInfoRepository = Beans.get(AssEnajahInfoRepository.class);
    if (assEnajahInfoRepository
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch()
            .size()
        == 0) assEnajahInfoRepository.save(new AssEnajahInfo());

    return assEnajahInfoRepository
        .all()
        .filter("etablissement = ?", user.getEtablissementSelectionnee())
        .fetchOne()
        .getId();
  }
}
