package com.setting.data.massar;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserMassar {

  @JsonProperty("Login")
  public String login;

  @JsonProperty("UserType")
  public String userType;

  @JsonProperty("Application")
  public String application;

  @JsonProperty("NomArabe")
  public String nomArabe;

  @JsonProperty("NomLatine")
  public String nomLatine;

  @JsonProperty("Code")
  public String code;

  @JsonProperty("CodeProfile")
  public String codeProfile;

  @JsonProperty("CodeEtablissement")
  public String codeEtablissement;

  @JsonProperty("CodeProvince")
  public String codeProvince;

  @JsonProperty("CodeAref")
  public String codeAref;

  @JsonProperty("Categorie")
  public String categorie;

  @JsonProperty("IsNewInscription")
  public String isNewInscription;

  @JsonProperty("Autorization")
  public List<String> autorization;

  public int nbf;
  public int exp;
  public int iat;
  public String iss;
  public String aud;

  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getApplication() {
    return application;
  }

  public void setApplication(String application) {
    this.application = application;
  }

  public String getNomArabe() {
    return nomArabe;
  }

  public void setNomArabe(String nomArabe) {
    this.nomArabe = nomArabe;
  }

  public String getNomLatine() {
    return nomLatine;
  }

  public void setNomLatine(String nomLatine) {
    this.nomLatine = nomLatine;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCodeProfile() {
    return codeProfile;
  }

  public void setCodeProfile(String codeProfile) {
    this.codeProfile = codeProfile;
  }

  public String getCodeEtablissement() {
    return codeEtablissement;
  }

  public void setCodeEtablissement(String codeEtablissement) {
    this.codeEtablissement = codeEtablissement;
  }

  public String getCodeProvince() {
    return codeProvince;
  }

  public void setCodeProvince(String codeProvince) {
    this.codeProvince = codeProvince;
  }

  public String getCodeAref() {
    return codeAref;
  }

  public void setCodeAref(String codeAref) {
    this.codeAref = codeAref;
  }

  public String getCategorie() {
    return categorie;
  }

  public void setCategorie(String categorie) {
    this.categorie = categorie;
  }

  public String getIsNewInscription() {
    return isNewInscription;
  }

  public void setIsNewInscription(String isNewInscription) {
    this.isNewInscription = isNewInscription;
  }

  public List<String> getAutorization() {
    return autorization;
  }

  public void setAutorization(List<String> autorization) {
    this.autorization = autorization;
  }

  public int getNbf() {
    return nbf;
  }

  public void setNbf(int nbf) {
    this.nbf = nbf;
  }

  public int getExp() {
    return exp;
  }

  public void setExp(int exp) {
    this.exp = exp;
  }

  public int getIat() {
    return iat;
  }

  public void setIat(int iat) {
    this.iat = iat;
  }

  public String getIss() {
    return iss;
  }

  public void setIss(String iss) {
    this.iss = iss;
  }

  public String getAud() {
    return aud;
  }

  public void setAud(String aud) {
    this.aud = aud;
  }
}
