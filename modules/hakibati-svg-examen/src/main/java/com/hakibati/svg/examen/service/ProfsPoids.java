package com.hakibati.svg.examen.service;

import com.hakibati.svg.examen.db.Prof;

public class ProfsPoids {
  private Prof prof;
  private Integer poids;
  private Integer maxSalle;
  private Integer Minmatiere;
  private Integer maxMatinSoir;
  private Integer maxMemeSalle;
  private Integer maxMemeAmi;
  private Integer maxJourDsExam;
  private Integer maxResrve;
  private Integer maxGarde;

  public ProfsPoids() {}

  public ProfsPoids(Prof prof, Integer poids, Integer maxSalle, Integer minmatiere) {
    this.prof = prof;
    this.poids = poids;
    this.maxSalle = maxSalle;
    Minmatiere = minmatiere;
  }

  public Integer getMaxGarde() {
    return maxGarde;
  }

  public void setMaxGarde(Integer maxGarde) {
    this.maxGarde = maxGarde;
  }

  public Integer getMaxResrve() {
    return maxResrve;
  }

  public void setMaxResrve(Integer maxResrve) {
    this.maxResrve = maxResrve;
  }

  public Integer getMaxMemeSalle() {
    return maxMemeSalle;
  }

  public Integer getMaxJourDsExam() {
    return maxJourDsExam;
  }

  public void setMaxJourDsExam(Integer maxJourDsExam) {
    this.maxJourDsExam = maxJourDsExam;
  }

  public void setMaxMemeSalle(Integer maxMemeSalle) {
    this.maxMemeSalle = maxMemeSalle;
  }

  public Integer getMaxMemeAmi() {
    return maxMemeAmi;
  }

  public void setMaxMemeAmi(Integer maxMemeAmi) {
    this.maxMemeAmi = maxMemeAmi;
  }

  public Integer getMaxMatinSoir() {
    return maxMatinSoir;
  }

  public void setMaxMatinSoir(Integer maxMatinSoir) {
    this.maxMatinSoir = maxMatinSoir;
  }

  public Prof getProf() {
    return prof;
  }

  public void setProf(Prof prof) {
    this.prof = prof;
  }

  public Integer getPoids() {
    return poids;
  }

  public void setPoids(Integer poids) {
    this.poids = poids;
  }

  public Integer getMaxSalle() {
    return maxSalle;
  }

  public void setMaxSalle(Integer maxSalle) {
    this.maxSalle = maxSalle;
  }

  public Integer getMinmatiere() {
    return Minmatiere;
  }

  public void setMinmatiere(Integer minmatiere) {
    Minmatiere = minmatiere;
  }
}
