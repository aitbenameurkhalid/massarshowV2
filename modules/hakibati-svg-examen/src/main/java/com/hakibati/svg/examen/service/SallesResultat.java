package com.hakibati.svg.examen.service;

import com.hakibati.svg.examen.db.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SallesResultat {
  private Integer Id;
  private Integer IdS;
  private Prof prof;
  private Prof profAmi;
  private Salles salles;
  private Matiere matiere;
  private Fillier fillier;
  private LocalDate dateExam;
  private LocalTime Du;
  private LocalTime Au;
  private Integer svgIndice;
  private List<ProfsPoids> profsPoidsList;
  private Integer MinHomme;
  private Integer MinFemme;

  private boolean force;

  public SallesResultat() {
    this.prof = null;
    this.profAmi = null;
    IdS = 0;
    Id = -1;
  }

  public Integer getId() {
    return Id;
  }

  public Prof getProfAmi() {
    return profAmi;
  }

  public void setProfAmi(Prof profAmi) {
    this.profAmi = profAmi;
  }

  public void setId(Integer id) {
    Id = id;
  }

  public Integer getIdS() {
    return IdS;
  }

  public void setIdS(Integer idS) {
    IdS = idS;
  }

  public Prof getProf() {
    return prof;
  }

  public void setProf(Prof prof) {
    this.prof = prof;
  }

  public Salles getSalles() {
    return salles;
  }

  public void setSalles(Salles salles) {
    this.salles = salles;
  }

  public Matiere getMatiere() {
    return matiere;
  }

  public void setMatiere(Matiere matiere) {
    this.matiere = matiere;
  }

  public Fillier getFillier() {
    return fillier;
  }

  public void setFillier(Fillier fillier) {
    this.fillier = fillier;
  }

  public LocalDate getDateExam() {
    return dateExam;
  }

  public void setDateExam(LocalDate dateExam) {
    this.dateExam = dateExam;
  }

  public LocalTime getDu() {
    return Du;
  }

  public void setDu(LocalTime du) {
    Du = du;
  }

  public LocalTime getAu() {
    return Au;
  }

  public void setAu(LocalTime au) {
    Au = au;
  }

  public Integer getSvgIndice() {
    return svgIndice;
  }

  public void setSvgIndice(Integer svgIndice) {
    this.svgIndice = svgIndice;
  }

  public List<ProfsPoids> getProfsPoidsList() {
    return profsPoidsList;
  }

  public void setProfsPoidsList(List<ProfsPoids> profsPoidsList) {
    this.profsPoidsList = profsPoidsList;
  }

  public Integer getMinHomme() {
    return MinHomme;
  }

  public void setMinHomme(Integer minHomme) {
    MinHomme = minHomme;
  }

  public Integer getMinFemme() {
    return MinFemme;
  }

  public void setMinFemme(Integer minFemme) {
    MinFemme = minFemme;
  }

  public boolean isForce() {
    return force;
  }

  public void setForce(boolean force) {
    this.force = force;
  }
}
