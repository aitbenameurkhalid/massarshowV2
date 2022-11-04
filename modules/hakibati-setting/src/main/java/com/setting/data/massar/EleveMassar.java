package com.setting.data.massar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EleveMassar {
  public int id_eleve;
  public String codeEleve;
  public String nomEleveAr;
  public String nomEleveFr;
  public String prenomEleveAr;
  public String prenomEleveFr;
  public ScolariteMassar scolarite;
  public String __typename;

  public Date dateNaisEleve;
  public String lieu_naissance_Fr;
  public String lieu_naissance_Ar;
  public int id_Genre;

  public String getDateNaisEleve() {
    if (dateNaisEleve == null) return null;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return dateFormat.format(dateNaisEleve);

    //        return dateNaisEleve;
  }

  public String getLieu_naissance_Fr() {
    if (lieu_naissance_Fr == null) return "";
    return lieu_naissance_Fr;
  }

  public String getLieu_naissance_Ar() {
    if (lieu_naissance_Ar == null) return "";
    return lieu_naissance_Ar;
  }

  public int getIdGenre() {
    return id_Genre;
  }

  public int getId_eleve() {
    return id_eleve;
  }

  public String getCodeEleve() {
    return codeEleve;
  }

  public String getNomEleveAr() {
    return nomEleveAr;
  }

  public String getNomEleveFr() {
    return nomEleveFr;
  }

  public String getPrenomEleveAr() {
    return prenomEleveAr;
  }

  public String getPrenomEleveFr() {
    return prenomEleveFr;
  }

  public ScolariteMassar getScolarite() {
    return scolarite;
  }

  public String get__typename() {
    return __typename;
  }
}
