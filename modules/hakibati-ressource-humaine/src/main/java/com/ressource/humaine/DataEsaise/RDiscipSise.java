package com.ressource.humaine.DataEsaise;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "R_Discip")
public class RDiscipSise {
  private int CD_Discip;
  private String LL_DISCIP;
  private String LA_DISCIP;

  public int getCD_Discip() {
    return CD_Discip;
  }

  public void setCD_Discip(int CD_Discip) {
    this.CD_Discip = CD_Discip;
  }

  public String getLL_DISCIP() {
    return LL_DISCIP;
  }

  public void setLL_DISCIP(String LL_DISCIP) {
    this.LL_DISCIP = LL_DISCIP;
  }

  public String getLA_DISCIP() {
    return LA_DISCIP;
  }

  public void setLA_DISCIP(String LA_DISCIP) {
    this.LA_DISCIP = LA_DISCIP;
  }
}
