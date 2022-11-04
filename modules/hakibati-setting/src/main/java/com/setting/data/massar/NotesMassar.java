package com.setting.data.massar;

public class NotesMassar {
  public String cdMatiere;
  public int coefficient;
  public String idClasse;
  public int idControlContinu;
  public int idSession;
  public int idEleve;
  public double note;
  public String observation;
  public String __typename;

  public String getCdMatiere() {
    return cdMatiere;
  }

  public int getCoefficient() {
    return coefficient;
  }

  public String getIdClasse() {
    return idClasse;
  }

  public int getIdControlContinu() {
    return idControlContinu;
  }

  public int getIdSession() {
    return idSession;
  }

  public int getIdEleve() {
    return idEleve;
  }

  public double getNote() {
    return note;
  }

  public String getObservation() {
    if (observation == null) return "";
    return observation;
  }

  public String get__typename() {
    return __typename;
  }
}
