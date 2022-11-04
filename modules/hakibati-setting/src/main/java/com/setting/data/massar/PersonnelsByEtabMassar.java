package com.setting.data.massar;

public class PersonnelsByEtabMassar {

  public int idPersonnel;
  public String nomA;
  public Object prenomA;
  public String cina;
  public String cinn;
  public String nomL;
  public Object prenomL;
  public String cdMatiere;
  public int ppr;
  public String __typename;

  public int getIdPersonnel() {
    return idPersonnel;
  }

  public String getNomA() {
    if (nomA == null) return "";
    return nomA;
  }

  public String getCina() {
    return cina;
  }

  public String getCinn() {
    return cinn;
  }

  public Object getPrenomA() {
    if (prenomA == null) return "";
    return prenomA;
  }

  public String getNomL() {
    if (nomL == null) return "";
    return nomL;
  }

  public Object getPrenomL() {
    if (prenomL == null) return "";
    return prenomL;
  }

  public String getCdMatiere() {
    if (cdMatiere == null) return "";
    return cdMatiere;
  }

  public int getPpr() {
    return ppr;
  }

  public String get__typename() {
    return __typename;
  }
}
