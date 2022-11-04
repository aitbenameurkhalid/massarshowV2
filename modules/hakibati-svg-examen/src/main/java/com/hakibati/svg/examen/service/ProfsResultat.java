package com.hakibati.svg.examen.service;

import com.hakibati.svg.examen.db.Prof;
import com.hakibati.svg.examen.db.Resultats;
import java.util.List;

public class ProfsResultat {
  private Prof prof;
  private List<Resultats> resultatsList;
  private List<ProfsPoids> resultatsListInterditPoids;

  public ProfsResultat() {}

  public ProfsResultat(
      Prof prof, List<Resultats> resultatsList, List<ProfsPoids> resultatsListInterditPoids) {
    this.prof = prof;
    this.resultatsList = resultatsList;
    this.resultatsListInterditPoids = resultatsListInterditPoids;
  }

  public List<ProfsPoids> getResultatsListInterditPoids() {
    return resultatsListInterditPoids;
  }

  public void setResultatsListInterditPoids(List<ProfsPoids> resultatsListInterditPoids) {
    this.resultatsListInterditPoids = resultatsListInterditPoids;
  }

  public Prof getProf() {
    return prof;
  }

  public void setProf(Prof prof) {
    this.prof = prof;
  }

  public List<Resultats> getResultatsList() {
    return resultatsList;
  }

  public void setResultatsList(List<Resultats> resultatsList) {
    this.resultatsList = resultatsList;
  }
}
