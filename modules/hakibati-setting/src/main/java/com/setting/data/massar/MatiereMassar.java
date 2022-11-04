package com.setting.data.massar;

public class MatiereMassar {
  public String cd_matiere;
  public Object cd_unite;
  public String matiereAr;
  public String matiereFr;
  public String __typename;

  public String getCd_matiere() {
    if (cd_matiere == null) return "";
    return cd_matiere;
  }

  public Object getCd_unite() {
    return cd_unite;
  }

  public String getMatiereAr() {
    if (matiereAr == null) return "";
    return matiereAr;
  }

  public String getMatiereFr() {
    if (matiereFr == null) return "";
    return matiereFr;
  }

  public String get__typename() {
    return __typename;
  }
}
