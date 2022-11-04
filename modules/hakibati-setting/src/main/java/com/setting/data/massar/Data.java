package com.setting.data.massar;

import java.util.List;

//          convertit json to java class :  https://json2csharp.com/json-to-pojo
public class Data {
  public List<EtablissementMassar> allEtabs;
  public List<ClassMassar> userClasses;
  public List<AnneeScolaireMassar> allAnneeScolaires;
  public List<NiveauMassar> allNefstat;
  public List<EleveMassar> elevesByNefstatAndClass;
  public List<CycleMassar> cycle;
  public List<MatiereMassar> allMatiere;
  public List<NotesMassar> notes;
  public List<PersonnelsByEtabMassar> personnelsByEtabAndCycle;
  public List<EnseignantMassar> enseignants;
  public List<AffectationEnseignantMassar> affectationEnseignants;
  public List<AllPersonnelMassar> allPersonnel;
  public List<DirectionMassar> allProv;
  public List<CommuneMassar> communes;

  public List<DirectionMassar> getAllProv() {
    return allProv;
  }

  public List<CommuneMassar> getCommunes() {
    return communes;
  }

  public List<AllPersonnelMassar> getAllPersonnel() {
    return allPersonnel;
  }

  public List<AffectationEnseignantMassar> getAffectationEnseignants() {
    return affectationEnseignants;
  }

  public List<EnseignantMassar> getEnseignants() {
    return enseignants;
  }

  public List<PersonnelsByEtabMassar> getPersonnelsByEtabAndCycle() {
    return personnelsByEtabAndCycle;
  }

  public List<NotesMassar> getNotes() {
    return notes;
  }

  public List<MatiereMassar> getAllMatiere() {
    return allMatiere;
  }

  public List<CycleMassar> getCycle() {
    return cycle;
  }

  public List<EleveMassar> getElevesByNefstatAndClass() {
    return elevesByNefstatAndClass;
  }

  public List<EtablissementMassar> getAllEtabs() {
    return allEtabs;
  }

  public List<ClassMassar> getUserClasses() {
    return userClasses;
  }

  public List<AnneeScolaireMassar> getAllAnneeScolaires() {
    return allAnneeScolaires;
  }

  public List<NiveauMassar> getAllNefstat() {
    return allNefstat;
  }
}
