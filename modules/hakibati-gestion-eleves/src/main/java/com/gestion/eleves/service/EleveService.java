package com.gestion.eleves.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.gestion.eleves.db.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Singleton
@Transactional
public class EleveService {

  private HSSFWorkbook book = null;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;

  public String importListEleveMassar(
      MetaFile metaFile, Gestioneleves gestioneleves, String typeListImport) throws IOException {

    File excelFile = MetaFiles.getPath(metaFile).toFile();

    int countEleve = 0;
    try {
      if (!Files.getFileExtension(excelFile.getName()).equals("xls")) {
        MetaFiles.getPath(metaFile).toFile().deleteOnExit();
        return I18n.get("Choisissez un fichier au format xls");
      }
      if (!this.initialize(metaFile, ";")) return "Erreur de lecture du fichier";
      List<Niveau> niveauList = gestioneleves.getNiveau();
      List<Classe> classeList = gestioneleves.getClasses();
      List<AnneScolaire> scolaireList = gestioneleves.getAnneScolaire();
      List<Eleve> eleveList = (List<Eleve>) gestioneleves.getEleves();

      String[] strCells;
      String anneeScolairImport;
      boolean langAr = true;
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");

      strCells = this.read(this.getSheetNames()[1], 5, 3);
      anneeScolairImport = strCells[2];
      //      AnneScolaire anneScolaireFind =
      //          Beans.get(AnneScolaireRepository.class)
      //              .findByEtablissemntAndName(
      //                  gestioneleves.getEtablissement().getId(), anneeScolairImport);
      //      if (anneScolaireFind == null) {
      //        anneScolaireFind = new AnneScolaire();
      //        anneScolaireFind.setName(anneeScolairImport);
      //        anneScolaireFind.setGestioneleves(gestioneleves);
      //        scolaireList.add(anneScolaireFind);
      //      }else{
      //
      //      }
      AnneScolaire anneScolaire;
      if (scolaireList.stream()
          .noneMatch(e -> e.getName() != null && e.getName().equals(anneeScolairImport))) {
        anneScolaire = new AnneScolaire();
        anneScolaire.setName(anneeScolairImport);
        try {
          String[] parts = anneeScolairImport.split("/");
          anneScolaire.setAnneeMin((long) Integer.parseInt(parts[0]));
          anneScolaire.setAnneeMax((long) Integer.parseInt(parts[1]));
          anneScolaire.setIdAnnee((long) Integer.parseInt(parts[0]) - 2007);
          //                niveau.setSuffix(parts[0]);
        } catch (Exception e) {
        }
        anneScolaire.setGestioneleves(gestioneleves);
        scolaireList.add(anneScolaire);
        gestioneleves.setAnneScolaireActuele(anneScolaire);
      } else {
        anneScolaire =
            scolaireList.stream()
                .filter(e -> e.getName() != null && e.getName().equals(anneeScolairImport))
                .collect(Collectors.toList())
                .get(0);
      }
      if (typeListImport.equals("0"))
        for (Eleve eleve :
            eleveList.stream()
                .filter(
                    e ->
                        e.getAnneeScolaire() != null
                            && Objects.equals(e.getAnneeScolaire().getId(), anneScolaire.getId()))
                .collect(Collectors.toList())) {
          eleve.setArchived(true);
        }
      if (typeListImport.equals("1"))
        for (Eleve eleve :
            eleveList.stream()
                .filter(
                    e ->
                        e.getAnneeScolaire() != null
                            && e.getAnneeScolaire().getName().equals(anneScolaire.getName())
                            && e.getArchived() != null
                            && !e.getArchived()
                            && e.getSituationActuelle() != null
                            && e.getSituationActuelle() == 0)
                .collect(Collectors.toList())) {
          eleve.setSituationActuelle(-1);
        }

      //      envoyer les encien donne à archive
      if (!StringUtils.isBlank(strCells[2])) {
        //                String finalAnneeScolair = anneeScolair;
        //                AnneScolaire anneScolaireFinal = anneScolaireFind;
        //                for (Niveau niveau :
        //                        niveauList.stream()
        //                                .filter(
        //                                        e ->
        //                                                e.getAnneeScolaire() != null
        //                                                        &&
        // !e.getAnneeScolaire().getName().equals(anneScolaireFinal.getName())
        //                                                        && !e.getArchived())
        //                                .collect(Collectors.toList())) {
        //                    niveau.setArchived(true);
        //                }
        //                for (Classe classe :
        //                        classeList.stream()
        //                                .filter(
        //                                        e ->
        //                                                e.getAnneeScolaire() != null
        //                                                        &&
        // !e.getAnneeScolaire().getName().equals(anneScolaireFinal.getName())
        //                                                        && !e.getArchived())
        //                                .collect(Collectors.toList())) {
        //                    classe.setArchived(true);
        //                }
        //                for (Eleve eleve :
        //                        eleveList.stream()
        //                                .filter(
        //                                        e ->
        //                                                e.getAnneeScolaire() != null
        //                                                        &&
        // !e.getAnneeScolaire().getName().equals(anneScolaireFinal.getName())
        //                                                        && !e.getArchived())
        //                                .collect(Collectors.toList())) {
        //                    eleve.setArchived(true);
        //                }

      }

      for (String sheetselectName : this.getSheetNames()) {

        strCells = this.read(sheetselectName, 3, 28);

        if (strCells[7] != null
            && !strCells[7].equals("")
            && !StringUtils.isBlank(strCells[7])
            && strCells[7].toUpperCase().contains("L")) langAr = false;

        if (strCells[6] != null
            && !strCells[6].equals("")
            && !StringUtils.isBlank(strCells[6])
            && strCells[6].toUpperCase().contains("L")) langAr = false;

        int m = this.getTotalLines(sheetselectName);
        if (langAr) {

          strCells = this.read(sheetselectName, 5, 28);
          //                    anneeScolair = strCells[2];

          strCells = this.read(sheetselectName, 9, 28);
          String strCells19 = strCells[19];
          strCells = this.read(sheetselectName, 10, 28);
          String classCode = strCells[8];

          Niveau niveau;
          if (niveauList.stream()
              .noneMatch(
                  e ->
                      e.getAnneeScolaire() != null
                          && e.getAnneeScolaire() == anneScolaire
                          && e.getName() != null
                          && e.getName().equals(strCells19))) {
            niveau = new Niveau();
            niveau.setName(strCells19);
            if (!StringUtils.isBlank(classCode) && classCode.contains("-")) {
              String[] parts = classCode.split("-");
              niveau.setSuffix(parts[0]);
            }
            niveau.setAnneeScolaire(anneScolaire);
            niveau.setGestioneleves(gestioneleves);
            niveau.setArchived(false);
            niveauList.add(niveau);
            //                        gestioneleves.setNiveau(niveauList);
          } else {
            niveau =
                niveauList.stream()
                    .filter(
                        e ->
                            e.getAnneeScolaire() != null
                                && e.getAnneeScolaire() == anneScolaire
                                && e.getName().equals(strCells19))
                    .collect(Collectors.toList())
                    .get(0);
            //                        niveau.setAnneeScolaire(anneScolaire);
            //                        if (classCode.contains("-")) {
            //                            String[] parts = classCode.split("-");
            //                            niveau.setCode(parts[0]);
            //                        }
            niveau.setArchived(false);
          }

          strCells = this.read(sheetselectName, 10, 28);

          String strCells8 = strCells[8];
          Classe classe;
          if (StringUtils.isBlank(strCells8)) {
            continue;
            // classe = null;
          } else {
            if (classeList.stream()
                .noneMatch(
                    e ->
                        e.getAnneeScolaire() != null
                            && e.getAnneeScolaire() == anneScolaire
                            && e.getName() != null
                            && e.getName().equals(strCells8))) {
              classe = new Classe();
              classe.setName(strCells[8]);
              classe.setAnneeScolaire(anneScolaire);
              classe.setGestioneleves(gestioneleves);
              classe.setArchived(false);
              classe.setNiveau(niveau);
              classeList.add(classe);
              //                        gestioneleves.setClasses(classeList);
            } else {
              classe =
                  classeList.stream()
                      .filter(
                          e ->
                              e.getAnneeScolaire() != null
                                  && e.getAnneeScolaire() == anneScolaire
                                  && e.getName() != null
                                  && e.getName().equals(strCells8))
                      .collect(Collectors.toList())
                      .get(0);
              //                        classe.setAnneeScolaire(anneScolaireFind);
              classe.setArchived(false);
            }
          }
          for (int maxl = 15; maxl <= m - 1; maxl++) {
            strCells = this.read(sheetselectName, maxl, 28);

            if (StringUtils.isBlank(strCells[23])) {
              continue;
            }
            countEleve++;
            Eleve eleve;
            String strCells23 = strCells[23].toUpperCase();
            if (eleveList.stream()
                .noneMatch(
                    e ->
                        e.getAnneeScolaire() == anneScolaire
                            && e.getCdMassar() != null
                            && e.getCdMassar().equals(strCells23))) {
              eleve = new Eleve();
              if (typeListImport.equals("1")) eleve.setSituationInitiale(-1);
              eleve.setGestioneleves(gestioneleves);
              eleveList.add(eleve);
              //                            gestioneleves.setEleves(eleveList);
            } else {
              eleve =
                  eleveList.stream()
                      .filter(
                          e ->
                              e.getAnneeScolaire() == anneScolaire
                                  && e.getCdMassar().equals(strCells23))
                      .collect(Collectors.toList())
                      .get(0);
            }
            eleve.setSituationActuelle(0);
            if (typeListImport.equals("0")) eleve.setSituationInitiale(0);
            eleve.setArchived(false);
            eleve.setIsMassar(true);
            eleve.setCdMassar(strCells[23].toUpperCase());
            eleve.setIdclasse(Integer.parseInt(strCells[26]));
            eleve.setPrenomAr(strCells[12]);
            eleve.setNomAr(strCells[16]);
            eleve.setLieuNaissanceAr(strCells[2]);
            if (!StringUtils.isBlank(strCells[11]) && !strCells[11].contains("ك")) eleve.setSexe(1);
            eleve.setDateNaissance(LocalDate.parse(strCells[5], formatter));
            eleve.setClasse(classe);
            eleve.setNiveauNom(classe.getNiveau().getName());
            eleve.setAnneeScolaire(anneScolaire);
          }
        } else {
          for (int maxl = 17; maxl <= m - 1; maxl++) {
            strCells = this.read(sheetselectName, maxl, 28);

            if (StringUtils.isBlank(strCells[2])) {
              continue;
            }
            countEleve++;
            Eleve eleve;
            String strCells2 = strCells[2].toUpperCase();
            if (eleveList.stream()
                .noneMatch(
                    e ->
                        e.getAnneeScolaire() == anneScolaire
                            && e.getCdMassar().equals(strCells2))) {
              eleve = new Eleve();
              if (typeListImport.equals("1")) eleve.setSituationInitiale(-1);
              eleve.setGestioneleves(gestioneleves);
              eleveList.add(eleve);
              gestioneleves.setEleves(eleveList);
            } else {
              eleve =
                  eleveList.stream()
                      .filter(
                          e ->
                              e.getAnneeScolaire() == anneScolaire
                                  && e.getCdMassar().equals(strCells2))
                      .collect(Collectors.toList())
                      .get(0);
            }
            eleve.setSituationActuelle(0);
            if (typeListImport.equals("0")) eleve.setSituationInitiale(0);
            eleve.setArchived(false);
            eleve.setIsMassar(true);
            eleve.setCdMassar(strCells[2].toUpperCase());
            eleve.setAnneeScolaire(anneScolaire);
            if (!StringUtils.isBlank(strCells[1])) eleve.setIdclasse(Integer.parseInt(strCells[1]));
            if (!StringUtils.isBlank(strCells[11])) eleve.setPrenomFr(strCells[11].toUpperCase());
            if (!StringUtils.isBlank(strCells[8])) eleve.setPrenomFr(strCells[8].toUpperCase());

            if (!StringUtils.isBlank(strCells[6])) eleve.setNomFr(strCells[6]);
            if (!StringUtils.isBlank(strCells[5])) eleve.setNomFr(strCells[5]);

            //                        if (!StringUtils.isBlank(strCells[11]) &&
            // !strCells[11].toUpperCase().contains("G"))
            //                            eleve.setSexe(1);
            //                        if (!StringUtils.isBlank(strCells[14]) &&
            // !strCells[14].toUpperCase().contains("G"))
            //                            eleve.setSexe(1);

            if (!StringUtils.isBlank(strCells[21])) eleve.setLieuNaissanceFr(strCells[21]);
            if (!StringUtils.isBlank(strCells[19])) eleve.setLieuNaissanceFr(strCells[19]);

            if (!StringUtils.isBlank(strCells[17]))
              eleve.setDateNaissance(LocalDate.parse(strCells[17], formatter));
          }
        }
      }
    } catch (Exception e) {
      return I18n.get("Une erreur s'est produite lors de l'importation du fichier Excel")
          + e.getMessage();
    }

    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
    excelFile.delete();
    return I18n.get(
        "La liste des eléves a été importée avec succès : nomber total mettre à jour est "
            + countEleve);
  }

  public String importTuteurEleveMassar(MetaFile metaFile, Gestioneleves gestioneleves)
      throws IOException {
    User user = AuthUtils.getUser();
    File excelFile = MetaFiles.getPath(metaFile).toFile();

    try {
      if (!Files.getFileExtension(excelFile.getName()).equals("xlsx")) {
        MetaFiles.getPath(metaFile).toFile().deleteOnExit();
        return I18n.get("Choisissez un fichier au format xlsx");
      }
      List<Eleve> eleveList = gestioneleves.getEleves();
      //            EleveRepository eleveRepository = Beans.get(EleveRepository.class);
      if (!this.initializeXSSF(metaFile, ";")) return I18n.get("Erreur de lecture du fichier");

      String[] strCells;
      String verifieFichierTuteur;
      String sheetselectName = "Tuteur";

      int m = this.getTotalLinesXSSF(sheetselectName);

      strCells = this.readXSSF(sheetselectName, 7, 28);
      verifieFichierTuteur = strCells[2];
      if (StringUtils.isBlank(verifieFichierTuteur) || !verifieFichierTuteur.contains("رقم"))
        return I18n.get("Erreur de lecture du fichier");

      for (int maxl = 9; maxl <= m - 1; maxl++) {
        strCells = this.readXSSF(sheetselectName, maxl, 30);

        if (StringUtils.isBlank(strCells[2])) {
          continue;
        }
        //

        Eleve eleve;
        String strCells2 = strCells[2];
        if (eleveList.stream().noneMatch(e -> e.getCdMassar().equals(strCells2))) {
          eleve = new Eleve();
          eleve.setGestioneleves(gestioneleves);
          eleveList.add(eleve);
          gestioneleves.setEleves(eleveList);
        } else {
          eleve =
              eleveList.stream()
                  .filter(e -> e.getCdMassar().equals(strCells2))
                  .collect(Collectors.toList())
                  .get(0);
        }
        eleve.setCdMassar(strCells[2]);
        eleve.setPrenomAr(strCells[4]);
        eleve.setNomAr(strCells[3]);
        if (!StringUtils.isBlank(strCells[5]) && strCells[5].contains("ب")) eleve.setTypetuteur(0);
        if (!StringUtils.isBlank(strCells[5]) && strCells[5].contains("م")) eleve.setTypetuteur(1);
        if (!StringUtils.isBlank(strCells[5]) && strCells[5].contains("و")) eleve.setTypetuteur(2);
        eleve.setCinTuteur(strCells[6]);
        eleve.setPrenomarTuteur(strCells[7]);
        eleve.setNomarTuteur(strCells[8]);
        eleve.setPrenomfrTuteur(strCells[9]);
        eleve.setNomfrTuteur(strCells[10]);
        eleve.setFonctionTuteur(strCells[11]);
        eleve.setTelTuteur(strCells[12]);
        eleve.setAdresseTuteur(strCells[13]);

        eleve.setCinPere(strCells[14]);
        eleve.setPrenomarPere(strCells[15]);
        eleve.setNomarPere(strCells[16]);
        eleve.setPrenomfrPere(strCells[17]);
        eleve.setNomfrPere(strCells[18]);
        eleve.setFonctionPere(strCells[19]);
        eleve.setTelPere(strCells[20]);
        eleve.setAdressePere(strCells[21]);

        eleve.setCinMere(strCells[22]);
        eleve.setPrenomarMere(strCells[23]);
        eleve.setNomarMere(strCells[24]);
        eleve.setPrenomfrMere(strCells[25]);
        eleve.setNomfrMere(strCells[26]);
        eleve.setFonctionMere(strCells[27]);
        eleve.setTelMere(strCells[28]);
        eleve.setAdresseMere(strCells[29]);
      }

    } catch (Exception e) {
      return I18n.get("Une erreur s'est produite lors de l'importation du fichier Excel")
          + e.getMessage();
    }
    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
    excelFile.delete();
    return I18n.get("La liste des tuteur a été importée avec succès");
  }

  public boolean initialize(MetaFile input, String separator) {

    if (input == null) {
      return false;
    }

    File inFile = MetaFiles.getPath(input).toFile();
    if (!inFile.exists()) {
      return false;
    }

    try {
      FileInputStream inSteam = new FileInputStream(inFile);
      book = new HSSFWorkbook(inSteam);
      if (book.getNumberOfSheets() == 0) {
        return false;
      }
      formatter = new DataFormatter();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public boolean initializeXSSF(MetaFile input, String separator) {

    if (input == null) {
      return false;
    }
    File inFile = MetaFiles.getPath(input).toFile();
    if (!inFile.exists()) {
      return false;
    }

    try {
      FileInputStream inSteam = new FileInputStream(inFile);
      bookXSSF = new XSSFWorkbook(inSteam);
      if (bookXSSF.getNumberOfSheets() == 0) {
        return false;
      }
      formatter = new DataFormatter();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public String[] read(String sheetName, int index, int headerSize) {

    if (sheetName == null || book == null) {
      return null;
    }

    HSSFSheet sheet = book.getSheet(sheetName);
    if (sheet == null) {
      return null;
    }

    HSSFRow row = sheet.getRow(index);
    if (row == null) {
      return null;
    }

    if (headerSize == 0) {
      headerSize = row.getLastCellNum();
    }

    String[] vals = new String[headerSize];

    for (int i = 0; i < headerSize; i++) {
      Cell cell = row.getCell(i);
      if (cell == null) {
        continue;
      }
      vals[i] = formatter.formatCellValue(cell);
      if (Strings.isNullOrEmpty(vals[i])) {
        vals[i] = null;
      }
    }

    return vals;
  }

  public String[] readXSSF(String sheetName, int index, int headerSize) {

    if (sheetName == null || bookXSSF == null) {
      return null;
    }

    XSSFSheet sheet = bookXSSF.getSheet(sheetName);
    if (sheet == null) {
      return null;
    }

    XSSFRow row = sheet.getRow(index);
    if (row == null) {
      return null;
    }

    if (headerSize == 0) {
      headerSize = row.getLastCellNum();
    }

    String[] vals = new String[headerSize];

    for (int i = 0; i < headerSize; i++) {
      Cell cell = row.getCell(i);
      if (cell == null) {
        continue;
      }
      vals[i] = formatter.formatCellValue(cell);
      if (Strings.isNullOrEmpty(vals[i])) {
        vals[i] = null;
      }
    }

    return vals;
  }

  public String[] getSheetNames() {

    if (book == null) {
      return null;
    }

    String[] sheets = new String[book.getNumberOfSheets()];

    for (int count = 0; count < sheets.length; count++) {
      sheets[count] = book.getSheetName(count);
    }

    return sheets;
  }

  public int getTotalLines(String sheetName) {

    if (book == null || sheetName == null || book.getSheet(sheetName) == null) {
      return 0;
    }

    return book.getSheet(sheetName).getPhysicalNumberOfRows();
  }

  public int getTotalLinesXSSF(String sheetName) {

    if (bookXSSF == null || sheetName == null || bookXSSF.getSheet(sheetName) == null) {
      return 0;
    }

    return bookXSSF.getSheet(sheetName).getPhysicalNumberOfRows();
  }
}
