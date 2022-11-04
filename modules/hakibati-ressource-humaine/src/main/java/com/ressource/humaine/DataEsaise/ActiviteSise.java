package com.ressource.humaine.DataEsaise;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ACTIVITE")
public class ActiviteSise {

  private int CD_ACTIVITES;
  private int CD_MODAFFE;
  private String CD_FONC;
  private String CD_ETAB;
  private int PPR;
  private String CINA;
  private int CINN;
  private String CD_CYCLE;
  private boolean ACTIV_PRINC;
  private String DATEAFFECT;
  private String DT_AFF_ETAB;
  private String DT_AFF_PROV;
  private String DT_AFF_REG;
  private String DT_AFF_POSTE;
  private String DT_DEB_EXERCICE;
  private String DT_FIN_EXERCICE;

  public void setCD_ETAB(String CD_ETAB) {
    this.CD_ETAB = CD_ETAB;
  }

  public String getCD_ETAB() {
    return CD_ETAB;
  }

  public int getCD_ACTIVITES() {
    return CD_ACTIVITES;
  }

  public void setCD_ACTIVITES(int CD_ACTIVITES) {
    this.CD_ACTIVITES = CD_ACTIVITES;
  }

  public int getCD_MODAFFE() {
    return CD_MODAFFE;
  }

  public void setCD_MODAFFE(int CD_MODAFFE) {
    this.CD_MODAFFE = CD_MODAFFE;
  }

  public String getCD_FONC() {
    return CD_FONC;
  }

  public void setCD_FONC(String CD_FONC) {
    this.CD_FONC = CD_FONC;
  }

  public int getPPR() {
    return PPR;
  }

  public void setPPR(int PPR) {
    this.PPR = PPR;
  }

  public String getCINA() {
    return CINA;
  }

  public void setCINA(String CINA) {
    this.CINA = CINA;
  }

  public int getCINN() {
    return CINN;
  }

  public void setCINN(int CINN) {
    this.CINN = CINN;
  }

  public String getCD_CYCLE() {
    return CD_CYCLE;
  }

  public void setCD_CYCLE(String CD_CYCLE) {
    this.CD_CYCLE = CD_CYCLE;
  }

  public boolean isACTIV_PRINC() {
    return ACTIV_PRINC;
  }

  public void setACTIV_PRINC(boolean ACTIV_PRINC) {
    this.ACTIV_PRINC = ACTIV_PRINC;
  }

  public String getDATEAFFECT() {
    return DATEAFFECT;
  }

  public void setDATEAFFECT(String DATEAFFECT) {
    this.DATEAFFECT = DATEAFFECT;
  }

  public String getDT_AFF_ETAB() {
    return DT_AFF_ETAB;
  }

  public void setDT_AFF_ETAB(String DT_AFF_ETAB) {
    this.DT_AFF_ETAB = DT_AFF_ETAB;
  }

  public String getDT_AFF_PROV() {
    return DT_AFF_PROV;
  }

  public void setDT_AFF_PROV(String DT_AFF_PROV) {
    this.DT_AFF_PROV = DT_AFF_PROV;
  }

  public String getDT_AFF_REG() {
    return DT_AFF_REG;
  }

  public void setDT_AFF_REG(String DT_AFF_REG) {
    this.DT_AFF_REG = DT_AFF_REG;
  }

  public String getDT_AFF_POSTE() {
    return DT_AFF_POSTE;
  }

  public void setDT_AFF_POSTE(String DT_AFF_POSTE) {
    this.DT_AFF_POSTE = DT_AFF_POSTE;
  }

  public String getDT_DEB_EXERCICE() {
    return DT_DEB_EXERCICE;
  }

  public void setDT_DEB_EXERCICE(String DT_DEB_EXERCICE) {
    this.DT_DEB_EXERCICE = DT_DEB_EXERCICE;
  }

  public String getDT_FIN_EXERCICE() {
    return DT_FIN_EXERCICE;
  }

  public void setDT_FIN_EXERCICE(String DT_FIN_EXERCICE) {
    this.DT_FIN_EXERCICE = DT_FIN_EXERCICE;
  }
}
