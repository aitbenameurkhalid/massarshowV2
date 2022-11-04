package com.ressource.humaine.DataEsaise;

import java.util.List;
import javax.xml.bind.annotation.*;

// @XmlRootElement(name = "DsAgentExport")
// @XmlAccessorType(XmlAccessType.FIELD)
// @XmlAccessorType(XmlAccessType.PROPERTY)
// @XmlType(propOrder={"activite"})
// @XmlType(name = "DsAgentExport")
// @XmlType(name = "DsAgentExport", propOrder = {
//        "activite"})

// @XmlType(propOrder={"activite"})
// @XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DsAgentExport")
public class DsAgentExport {
  private List<ActiviteSise> activite;
  private List<DataPersonnelSise> dataPersonnelSise;
  private List<RDiscipSise> rDiscipSise;

  @XmlElement(name = "ACTIVITE")
  public List<ActiviteSise> getActivite() {
    return activite;
  }

  public void setActivite(List<ActiviteSise> activite) {
    this.activite = activite;
  }

  @XmlElement(name = "DATAIDENTIFPERSONNEL")
  public List<DataPersonnelSise> getDataPersonnelSise() {
    return dataPersonnelSise;
  }

  public void setDataPersonnelSise(List<DataPersonnelSise> dataPersonnelSise) {
    this.dataPersonnelSise = dataPersonnelSise;
  }

  @XmlElement(name = "R_Discip")
  public List<RDiscipSise> getrDiscipSise() {
    return rDiscipSise;
  }

  public void setrDiscipSise(List<RDiscipSise> rDiscipSise) {
    this.rDiscipSise = rDiscipSise;
  }

  //    public List<R_SitFam> R_SitFam;
  //    public List<R_Position> R_Position;
  //    public List<R_NATION> R_NATION;
  //    public List<R_MODEAFFE> R_MODEAFFE;
  //    public List<R_CYCLE> R_CYCLE;
  //    public List<R_NIVEAUENS> R_NIVEAUENS;
  //    public List<R_FILIERE> R_FILIERE;
  //    public List<R_DipSCol> R_DipSCol;
  //    public List<R_DipProf> R_DipProf;
  //    public List<R_CADRE> R_CADRE;
  //    public List<R_Statut> R_Statut;
  //    public List<R_MOD_ACCES_GRADE> R_MOD_ACCES_GRADE;
  //    public List<R_DIVISION> R_DIVISION;
  //    public List<R_SERVICE> R_SERVICE;
  //    public List<R_FONCT> R_FONCT;
  //    public List<R_CentreForm> R_CentreForm;
  //    public List<R_GRADE> R_GRADE;

}

//    public class R_SitFam {
//        public String Sit_Fam;
//        public String LL_SitFam;
//    }
//
//    public class R_Position {
//        public int CD_Position;
//        public String LL_POSITION;
//        public Object LA_POSITION;
//    }
//
//    public class R_NATION {
//        public int CD_NATION;
//        public String LL_NATION;
//        public String LA_NATION;
//    }
//
//    public class R_MODEAFFE {
//        public int CD_MODAFFE;
//        public String LL_MODEAFFE;
//    }
//
//    public class R_CYCLE {
//        public int CD_CYCLE;
//        public String LL_CYCLE;
//        public String LA_CYCLE;
//        public int flag;
//    }
//
//    public class R_NIVEAUENS {
//        public int CD_NIVENS;
//        public int CD_CYCLE;
//        public String LL_NIVENS;
//    }
//

//
//    public class R_FILIERE {
//        public int CD_FILIERE;
//        public String LIB_Filière;
//    }
//
//    public class R_DipSCol {
//        public int CD_DIPS;
//        public String LL_DIPS;
//        public String LA_DIPS;
//    }
//
//    public class R_DipProf {
//        public int CD_DIPP;
//        public String LL_DIPP;
//        public String LA_DIPP;
//    }
//
//    public class R_CADRE {
//        public LL_CADRE LL_CADRE;
//        public LA_CADRE LA_CADRE;
//        public int CD_CATEG;
//        public int CD_CADRE;
//        public int CD_CORPS;
//    }
//
//    public class LL_CADRE {
//        public String space;
//    }
//
//    public class LA_CADRE {
//        public String space;
//    }
//
//    public class R_Statut {
//        public int CD_Statut;
//        public String LL_STATUT;
//    }
//
//    public class R_MOD_ACCES_GRADE {
//        public int MODAVGRA;
//        public String DESLMODAV;
//    }
//
//    public class R_DIVISION {
//        public int CD_DIVISION;
//        public int CD_ENTADM;
//        public String LL_DIVISION;
//    }
//
//    public class R_SERVICE {
//        public int CD_SERVICE;
//        public int CD_DIVISION;
//        public String LL_SERVICE;
//    }
//
//    public class R_FONCT {
//        public int CD_Fonc;
//        public String LL_FONC;
//        public Object Flag;
//        public String LA_Fonc;
//    }
//
//    public class R_CentreForm {
//        public String NOM_ETABL;
//    }
//
//    public class R_GRADE {
//        public int CD_GRADE;
//        public int CD_CATEG;
//        public int CD_CORPS;
//        public int CD_CADRE;
//        public String LL_GRADE;
//        public Object LA_GRADE;
//        public int ECHELLE;
//    }
