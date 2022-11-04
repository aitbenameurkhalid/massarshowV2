package com.hakibati.svg.examen.web;

import java.time.LocalTime;

public class CodeContrainte {
  // salles
  public static final Integer minimum_professeurs_homme_salles = 1110;
  public static final Integer minimum_professeurs_femme_salles = 1120;
  public static final Integer minimum_professeurs_qualifie_salles = 1130;
  public static final Integer minimum_professeurs_college_salles = 1140;
  public static final Integer minimum_professeurs_primaire_salles = 1150;
  public static final Integer minimum_professeurs_toutes_salles = 1160;
  public static final Integer maximum_professeurs_toutes_salles = 1170;

  public static final Integer minimum_professeurs_homme_un_salle = 1111;
  public static final Integer minimum_professeurs_femme_un_salle = 1121;
  public static final Integer minimum_professeurs_qualifie_un_salle = 1131;
  public static final Integer minimum_professeurs_college_un_salle = 1141;
  public static final Integer minimum_professeurs_primaire_un_salle = 1151;
  public static final Integer minimum_professeurs_un_salle = 1161;
  public static final Integer maximum_professeurs_un_salle = 1171;

  public static final Integer maximum_garde_meme_salles = 1040;
  public static final Integer maximum_garde_meme_professeur = 1050;
  public static final Integer maximum_garde_meme_matiere_specialise = 1060;
  public static final Integer maximum_garde_matin_apres_midi = 1070;
  public static final Integer maximum_reserve_tous_prof = 1080;
  public static final Integer maximum_jour_tous_prof = 1090;

  public static final Integer maximum_garde_meme_salle_pr_un_prof = 1041;
  public static final Integer maximum_garde_meme_professeur_pr_un_prof = 1051;
  public static final Integer maximum_garde_meme_matiere_specialise_pr_un_prof = 1061;
  public static final Integer maximum_garde_matin_apres_midi_pr_un_prof = 1071;
  public static final Integer maximum_reserve_un_prof = 1081;
  public static final Integer maximum_jour_un_prof = 1091;

  public static final Integer horaires_pas_disponibles_un_professeur = 1011;
  public static final Integer professeurs_permanences = 1021;
  public static final Integer salle_pas_disponibles_un_professeur = 1031;
  public static final Integer maximum_garde_cours_exam_un_prof = 1012;
  public static final Integer maximum_garde_matiere_un_prof = 1013;

  public static final Integer minimum_Pourcentage_reserve_tous_Fillier = 1211;
  public static final Integer maximum_Pourcentage_reserve_tous_Fillier = 1221;
  public static final Integer minimum_reservation_un_Fillier = 1210;
  public static final Integer maximum_reservation_un_Fillier = 1220;

  public static final Integer code_sexe_homme = 3;
  public static final Integer code_sexe_femme = 2;
  public static final Integer code_sexe_sans = 0;

  public static final Integer code_cycle_primaire = 1;
  public static final Integer code_cycle_college = 2;
  public static final Integer code_cycle_qualifie = 3;
  public static final Integer code_cycle_sans = 0;

  public static final Integer code_exam_fonction_Surveillant = 1;
  public static final Integer code_exam_fonction_Adminstration = 2;
  public static final LocalTime time_speration_day = LocalTime.of(13, 00);

  public static final Integer CODE_OF_MODULE_EXAMEN_PERMISSION = 2;
}
