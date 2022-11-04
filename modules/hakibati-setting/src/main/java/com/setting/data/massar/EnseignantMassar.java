package com.setting.data.massar;

public class EnseignantMassar {
  public int idPersonnel;
  public String nomAr;
  public String nomCompletAr;
  public String nomCompletFr;
  public String nomFr;
  public String cdMatiere;
  public int ppr;
  public String __typename;

  public String get__typename() {
    return __typename;
  }

  public int getIdPersonnel() {
    //    if(idPersonnel == null) return 0;
    return idPersonnel;
  }

  public String getNomAr() {
    if (nomAr == null) return "";
    return nomAr;
  }

  public String getNomCompletAr() {
    if (nomCompletAr == null) return "";
    return nomCompletAr;
  }

  public String getNomCompletFr() {
    if (nomCompletFr == null) return "";
    return nomCompletFr;
  }

  public String getNomFr() {
    if (nomFr == null) return "";
    return nomFr;
  }

  public String getCdMatiere() {
    if (cdMatiere == null) return "";
    return cdMatiere;
  }

  public int getPpr() {
    //    if(ppr== null) return 0;
    return ppr;
  }
}
