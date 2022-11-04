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
import com.hakibati.gestion.eleves.db.AbsenceeleveInternat;
import com.hakibati.gestion.eleves.db.AnneScolaire;
import com.hakibati.gestion.eleves.db.EleveInternat;
import com.hakibati.gestion.eleves.db.Gestioneleves;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Singleton
@Transactional
public class EleveInternatService {
  private Workbook workbook;
  private Sheet sheet;
  private File excelFile;
  private XSSFWorkbook bookXSSF = null;
  private DataFormatter formatter = null;

  public File getFileListInternatExcel(Gestioneleves gestioneleves) throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    int rowSheet = 0;
    this.initialize();
    CellStyle cellStyle = workbook.createCellStyle();
    Font font = workbook.createFont();
    Row rowHead = sheet.createRow(rowSheet);

    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
    cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyle.setBorderRight(CellStyle.BORDER_THIN);
    cellStyle.setBorderTop(CellStyle.BORDER_THIN);
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
    cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyle.setWrapText(true);
    font.setFontHeightInPoints((short) 10);
    cellStyle.setFont(font);

    CellStyle cellStyleBody = workbook.createCellStyle();
    cellStyleBody.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleBody.setVerticalAlignment(CellStyle.ALIGN_GENERAL);
    cellStyleBody.setBorderBottom(CellStyle.BORDER_THIN);
    cellStyleBody.setBorderLeft(CellStyle.BORDER_THIN);
    cellStyleBody.setBorderRight(CellStyle.BORDER_THIN);
    cellStyleBody.setBorderTop(CellStyle.BORDER_THIN);
    cellStyleBody.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyleBody.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyleBody.setFillForegroundColor(IndexedColors.WHITE.getIndex());
    cellStyleBody.setFillPattern(CellStyle.SOLID_FOREGROUND);
    cellStyleBody.setWrapText(true);
    cellStyleBody.setFont(font);

    Cell cell = rowHead.createCell(1);
    cell.setCellValue(I18n.get("N°"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(2);
    cell.setCellValue(I18n.get("code Massar"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(3);
    cell.setCellValue(I18n.get("Date de naissance"));
    cell.setCellStyle(cellStyle);

    cell = rowHead.createCell(4);
    cell.setCellValue(I18n.get("Nom en Arab"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(5);
    cell.setCellValue(I18n.get("Prénom arab"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(6);
    cell.setCellValue(I18n.get("Nom en Français"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(7);
    cell.setCellValue(I18n.get("Prénom enFrançais"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(8);
    cell.setCellValue(I18n.get("Ordre Classe"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(9);
    cell.setCellValue(I18n.get("Genre"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(10);
    cell.setCellValue(I18n.get("Lieu de Naissance arab"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(11);
    cell.setCellValue(I18n.get("etablissement"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(12);
    cell.setCellValue(I18n.get("classe"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(13);
    cell.setCellValue(I18n.get("niveau"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(14);
    cell.setCellValue(I18n.get("Numero Bourse"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(15);
    cell.setCellValue(I18n.get("Date de Bourse"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(16);
    cell.setCellValue(I18n.get("Type De Bourse"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(17);
    cell.setCellValue(I18n.get("Numero Internat"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(18);
    cell.setCellValue(I18n.get("La situation actuelle"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(19);
    cell.setCellValue(I18n.get("Date situation"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(20);
    cell.setCellValue(I18n.get("situation initiale"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(21);
    cell.setCellValue(I18n.get("L'année scolaire"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(22);
    cell.setCellValue(I18n.get("CIN Tuteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(23);
    cell.setCellValue(I18n.get("Nomar Tuteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(24);
    cell.setCellValue(I18n.get("Prenomar Tuteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(25);
    cell.setCellValue(I18n.get("fonction Tuteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(26);
    cell.setCellValue(I18n.get("tel Tuteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(27);
    cell.setCellValue(I18n.get("Adresse Tuteur"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(28);
    cell.setCellValue(I18n.get("CIN Reporter"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(29);
    cell.setCellValue(I18n.get("Nom Reporter"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(30);
    cell.setCellValue(I18n.get("Prénom Reporter"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(31);
    cell.setCellValue(I18n.get("Fonction Reporter"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(32);
    cell.setCellValue(I18n.get("Tel Reporter"));
    cell.setCellStyle(cellStyle);
    cell = rowHead.createCell(33);
    cell.setCellValue(I18n.get("Adresse Reporter"));
    cell.setCellStyle(cellStyle);

    //    //
    //     2       <string name="cdMassar" title="code Massar" />
    //      3  <date name="dateNaissance" title="Date de naissance"/>
    //       4 <string name="nomAr" title="Nom en Arab"/>
    //        5<string name="prenomAr" title="Prénom arab"/>
    // 6        <string name="nomFr" title="Nom en Français"/>
    // 7       <string name="prenomFr" title="Prénom enFrançais"/>
    //  8      <integer name="idclasse" title="Ordre Classe"/>
    //   9     <integer name="sexe" selection="selection-sexe-eleve" title="Genre"/>
    //    10    <string name="lieuNaissanceAr" title="Lieu de Naissance arab"/>
    //      11 <string name="etablissementetudie"/>
    // 12        <string name="classe"/>
    //  13      <string name="niveau"/>
    //
    //    14    <string name="numeroBourse" />
    //      15  <date name="dateBourse" title="Date de Bourse"/>
    //        16<integer name="typeBourse" selection="selection-type-bourse" default="0" />
    // 17        <string name="numeroInternat"/>
    //    18    <integer name="situationActuelle" required="true" default="0" title="La situation
    // actuelle"
    //
    // 19       <date name="dateLaSituationActuelle" title="situation"/>
    //   20     <integer name="situationInitiale" required="true" default="0" title="situation
    // initiale"
    // 21        <string name="anneeScolaire" title="L'année scolaire" />
    //
    //    // information tuteur
    //  22      <string name="cinTuteur" title="CIN Tuteur"/>
    //    23    <string name="nomarTuteur" title="Nom En arab"/>
    //      24  <string name="prenomarTuteur" title="Prénom en Arab"/>
    //        25<string name="fonctionTuteur"/>
    // 26        <string name="telTuteur" title="Telephone Tuteur"/>
    //  27      <string name="adresseTuteur"/>
    //
    //// information Reporter
    // 28        <string name="cinReporter" title="CIN Reporter"/>
    //  29      <string name="nomarReporter" title="Nom Reporter"/>
    //    30    <string name="prenomarReporter" title="Prénom Reporter"/>
    //      31 <string name="fonctionReporter" title="Fonction Reporter"/>
    // 32        <string name="telReporter" title="Tel Reporter"/>
    //  33      <string name="adresseReporter" title="Adresse Reporter"/>
    //

    //    cell = rowHead.createCell(20);
    //    cell.setCellValue(gestioneleves.getEtablissement().getId());

    List<EleveInternat> eleveInternatList =
        gestioneleves.getElevesInternat().stream()
            .filter(e -> e.getArchived() == null || !e.getArchived())
            .collect(Collectors.toList());

    for (EleveInternat eleveInternat :
        eleveInternatList.stream()
            .sorted(Comparator.comparing(EleveInternat::getIdinternat))
            .sorted(Comparator.comparing(EleveInternat::getTypeBourse))
            .collect(Collectors.toList())) {
      rowSheet++;
      rowHead = sheet.createRow(rowSheet);
      cell = rowHead.createCell(1);
      cell.setCellValue(eleveInternat.getIdinternat());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(2);
      cell.setCellValue(eleveInternat.getCdMassar());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(3);
      if (eleveInternat.getDateNaissance() != null)
        cell.setCellValue(eleveInternat.getDateNaissance().format(formatter));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(4);
      cell.setCellValue(eleveInternat.getNomAr());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(5);
      cell.setCellValue(eleveInternat.getPrenomAr());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(6);
      cell.setCellValue(eleveInternat.getNomFr());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(7);
      cell.setCellValue(eleveInternat.getPrenomFr());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(8);
      cell.setCellValue(eleveInternat.getIdclasse());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(9);
      if (eleveInternat.getSexe() == 0) cell.setCellValue(I18n.get("Garçon"));
      if (eleveInternat.getSexe() == 1) cell.setCellValue(I18n.get("Fille"));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(10);
      cell.setCellValue(eleveInternat.getLieuNaissanceAr());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(11);
      cell.setCellValue(eleveInternat.getEtablissementetudie());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(12);
      cell.setCellValue(eleveInternat.getClasse());
      cell.setCellStyle(cellStyleBody);
      cell = rowHead.createCell(13);
      cell.setCellValue(eleveInternat.getNiveau());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(14);
      cell.setCellValue(eleveInternat.getNumeroBourse());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(15);
      if (eleveInternat.getDateBourse() != null)
        cell.setCellValue(eleveInternat.getDateBourse().format(formatter));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(16);
      if (eleveInternat.getTypeBourse() == 0) cell.setCellValue(I18n.get("bourse complète"));
      if (eleveInternat.getTypeBourse() == 1) cell.setCellValue(I18n.get("demi-bourse"));
      if (eleveInternat.getTypeBourse() == 2) cell.setCellValue(I18n.get("وحبة غداء"));
      if (eleveInternat.getTypeBourse() == 3) cell.setCellValue(I18n.get("معلم داخلية"));

      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(17);
      cell.setCellValue(eleveInternat.getNumeroInternat());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(18);
      if (eleveInternat.getSituationActuelle() == 0) cell.setCellValue(I18n.get("Exerce"));
      if (eleveInternat.getSituationActuelle() == 1) cell.setCellValue(I18n.get("Quitte"));
      if (eleveInternat.getSituationActuelle() == 2) cell.setCellValue(I18n.get("Abandonné"));
      if (eleveInternat.getSituationActuelle() == 3) cell.setCellValue(I18n.get("Non inscrit"));
      if (eleveInternat.getSituationActuelle() == 4) cell.setCellValue(I18n.get("Exclus"));
      if (eleveInternat.getSituationActuelle() == 5) cell.setCellValue(I18n.get("Transert Bourse"));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(19);
      if (eleveInternat.getDateLaSituationActuelle() != null)
        cell.setCellValue(eleveInternat.getDateLaSituationActuelle().format(formatter));
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(20);
      cell.setCellValue(eleveInternat.getSituationInitiale());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(21);
      cell.setCellValue(eleveInternat.getAnneeScolaire().getName());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(22);
      cell.setCellValue(eleveInternat.getCinTuteur());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(23);
      cell.setCellValue(eleveInternat.getNomarTuteur());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(24);
      cell.setCellValue(eleveInternat.getPrenomarTuteur());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(25);
      cell.setCellValue(eleveInternat.getFonctionTuteur());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(26);
      cell.setCellValue(eleveInternat.getTelTuteur());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(27);
      cell.setCellValue(eleveInternat.getAdresseTuteur());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(28);
      cell.setCellValue(eleveInternat.getCinReporter());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(29);
      cell.setCellValue(eleveInternat.getNomarReporter());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(30);
      cell.setCellValue(eleveInternat.getPrenomarReporter());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(31);
      cell.setCellValue(eleveInternat.getFonctionReporter());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(32);
      cell.setCellValue(eleveInternat.getTelReporter());
      cell.setCellStyle(cellStyleBody);

      cell = rowHead.createCell(33);
      cell.setCellValue(eleveInternat.getAdresseReporter());
      cell.setCellStyle(cellStyleBody);
    }

    for (int col = 1; col < 35; col++) {
      sheet.autoSizeColumn(col);
    }

    FileOutputStream fout = new FileOutputStream(excelFile);
    workbook.write(fout);
    fout.close();
    return excelFile;
  }

  public String importListEleveInternat(MetaFile metaFile, Gestioneleves gestioneleves)
      throws IOException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    File excelFile = MetaFiles.getPath(metaFile).toFile();
    User user = AuthUtils.getUser();
    int countEleve = 0;
    List<EleveInternat> eleveInternatList = gestioneleves.getElevesInternat();
    List<AnneScolaire> scolaireList = gestioneleves.getAnneScolaire();
    try {
      if (!Files.getFileExtension(excelFile.getName()).equals("xlsx")) {
        MetaFiles.getPath(metaFile).toFile().deleteOnExit();
        return I18n.get("Choisissez un fichier au format xlsx");
      }
      if (!this.initializeXSSF(metaFile, ";")) return I18n.get("Erreur de lecture du fichier");
      String[] strCells;
      String verifieFichierInternat;
      String sheetselectName = "List_Eleves_Internat";

      int m = this.getTotalLinesXSSF(sheetselectName);

      if (m == 0) return I18n.get("Erreur de lecture du fichier");
      for (int maxl = 1; maxl <= m - 1; maxl++) {
        strCells = this.readXSSF(sheetselectName, maxl, 35);
        if (StringUtils.isBlank(strCells[2])) {
          continue;
        }
        countEleve++;

        EleveInternat eleveInternat;
        String strCells2 = strCells[2].toUpperCase();
        if (eleveInternatList.stream()
            .noneMatch(
                e ->
                    e.getCdMassar() != null
                        && (e.getCdMassar().equals(strCells2)
                            || e.getCdMassar().equals(strCells2.toLowerCase())))) {
          eleveInternat = new EleveInternat();
          eleveInternat.setCdMassar(strCells2);
          eleveInternat.setGestioneleves(gestioneleves);
          eleveInternatList.add(eleveInternat);
          gestioneleves.setElevesInternat(eleveInternatList);
        } else {
          eleveInternat =
              eleveInternatList.stream()
                  .filter(
                      e ->
                          e.getCdMassar() != null
                              && (e.getCdMassar().equals(strCells2)
                                  || e.getCdMassar().equals(strCells2.toLowerCase())))
                  .collect(Collectors.toList())
                  .get(0);
        }
        //               if (user != null || countEleve==2) return "khalid ait ben ameur  " +
        // eleveInternat.getNomAr();
        eleveInternat.setArchived(false);
        if (!StringUtils.isBlank(strCells[1]))
          eleveInternat.setIdinternat(Integer.parseInt(strCells[1]));
        if (!StringUtils.isBlank(strCells[3]))
          eleveInternat.setDateNaissance(LocalDate.parse(strCells[3], formatter));

        if (!StringUtils.isBlank(strCells[4])) eleveInternat.setNomAr(strCells[4]);
        if (!StringUtils.isBlank(strCells[5])) eleveInternat.setPrenomAr((strCells[5]));
        if (!StringUtils.isBlank(strCells[6])) eleveInternat.setNomFr(strCells[6].toUpperCase());
        if (!StringUtils.isBlank(strCells[7]))
          eleveInternat.setPrenomFr((strCells[7].toUpperCase()));
        if (!StringUtils.isBlank(strCells[8]))
          eleveInternat.setIdclasse(Integer.parseInt(strCells[8]));
        if (!StringUtils.isBlank(strCells[9])) {
          eleveInternat.setSexe(1);
          if (strCells[9].toUpperCase().contains("G") || strCells[9].toUpperCase().contains("ك"))
            eleveInternat.setSexe(0);
        }
        if (!StringUtils.isBlank(strCells[10])) eleveInternat.setLieuNaissanceAr(strCells[10]);

        if (!StringUtils.isBlank(strCells[11])) eleveInternat.setEtablissementetudie(strCells[11]);
        if (!StringUtils.isBlank(strCells[12])) eleveInternat.setClasse(strCells[12]);
        if (!StringUtils.isBlank(strCells[13])) eleveInternat.setNiveau(strCells[13]);
        if (!StringUtils.isBlank(strCells[14])) eleveInternat.setNumeroBourse(strCells[14]);
        if (!StringUtils.isBlank(strCells[15]))
          eleveInternat.setDateBourse(LocalDate.parse(strCells[15], formatter));
        if (!StringUtils.isBlank(strCells[16])) {
          if (strCells[16].contains("comp") || strCells[16].contains("كام"))
            eleveInternat.setTypeBourse(0);
          if (strCells[16].contains("demi") || strCells[16].contains("نص"))
            eleveInternat.setTypeBourse(1);
          if (strCells[16].contains("غداء") || strCells[16].contains("وج"))
            eleveInternat.setTypeBourse(2);
          if (strCells[16].contains("معلم") || strCells[16].contains("مع"))
            eleveInternat.setTypeBourse(3);
        }
        if (!StringUtils.isBlank(strCells[17])) eleveInternat.setNumeroInternat(strCells[17]);
        if (!StringUtils.isBlank(strCells[18])) {
          if (strCells[18].toUpperCase().contains("EXE") || strCells[18].contains("متمد"))
            eleveInternat.setSituationActuelle(0);
          if (strCells[18].toUpperCase().contains("QUI") || strCells[18].contains("مغا"))
            eleveInternat.setSituationActuelle(1);
          if (strCells[18].toUpperCase().contains("ABA") || strCells[18].contains("نقط"))
            eleveInternat.setSituationActuelle(2);
          if (strCells[18].toUpperCase().contains("NON") || strCells[18].contains("غير"))
            eleveInternat.setSituationActuelle(3);
          if (strCells[18].toUpperCase().contains("EXC") || strCells[18].contains("فصل"))
            eleveInternat.setSituationActuelle(4);
          if (strCells[18].toUpperCase().contains("TRA") || strCells[18].contains("تحويل"))
            eleveInternat.setSituationActuelle(5);
        }
        if (!StringUtils.isBlank(strCells[19]))
          eleveInternat.setDateLaSituationActuelle(LocalDate.parse(strCells[19], formatter));
        //                if (!StringUtils.isBlank(strCells[20]))
        //                    eleveInternat.setSituationInitiale(Integer.parseInt(strCells[20]));

        if (!StringUtils.isBlank(strCells[21])) {
          AnneScolaire anneScolaire;
          String[] finalStrCells = strCells;
          if (scolaireList.stream()
              .noneMatch(e -> e.getName() != null && e.getName().equals(finalStrCells[21]))) {
            anneScolaire = new AnneScolaire();
            anneScolaire.setName(strCells[21]);
            anneScolaire.setGestioneleves(gestioneleves);
            scolaireList.add(anneScolaire);
          } else {
            anneScolaire =
                scolaireList.stream()
                    .filter(e -> e.getName() != null && e.getName().equals(finalStrCells[21]))
                    .collect(Collectors.toList())
                    .get(0);
          }
          String[] parts = strCells[21].split("/");
          anneScolaire.setAnneeMin((long) Integer.parseInt(parts[0]));
          anneScolaire.setAnneeMax((long) Integer.parseInt(parts[1]));
          anneScolaire.setIdAnnee((long) Integer.parseInt(parts[0]) - 2007);
          eleveInternat.setAnneeScolaire(anneScolaire);

        } else {
          eleveInternat.setAnneeScolaire(scolaireList.get(0));
        }

        if (!StringUtils.isBlank(strCells[22])) eleveInternat.setCinTuteur(strCells[22]);
        if (!StringUtils.isBlank(strCells[23])) eleveInternat.setNomarTuteur(strCells[23]);
        if (!StringUtils.isBlank(strCells[24])) eleveInternat.setPrenomarTuteur(strCells[24]);
        if (!StringUtils.isBlank(strCells[25])) eleveInternat.setFonctionTuteur(strCells[25]);
        if (!StringUtils.isBlank(strCells[26])) eleveInternat.setTelTuteur(strCells[26]);
        if (!StringUtils.isBlank(strCells[27])) eleveInternat.setAdresseTuteur(strCells[27]);
        if (!StringUtils.isBlank(strCells[28])) eleveInternat.setCinReporter(strCells[28]);
        if (!StringUtils.isBlank(strCells[29])) eleveInternat.setNomarReporter(strCells[29]);
        if (!StringUtils.isBlank(strCells[30])) eleveInternat.setPrenomarReporter(strCells[30]);
        if (!StringUtils.isBlank(strCells[31])) eleveInternat.setFonctionReporter(strCells[31]);
        if (!StringUtils.isBlank(strCells[32])) eleveInternat.setTelReporter(strCells[32]);
        if (!StringUtils.isBlank(strCells[33])) eleveInternat.setAdresseReporter(strCells[33]);
      }
    } catch (Exception e) {
      return I18n.get("Une erreur s'est produite lors de l'importation du fichier Excel  ")
          + e.getMessage();
    }
    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
    excelFile.delete();
    return I18n.get("La liste des eleves Internat a été importée avec succès") + ": " + countEleve;
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

  public int getTotalLinesXSSF(String sheetName) {

    if (bookXSSF == null || sheetName == null || bookXSSF.getSheet(sheetName) == null) {
      return 0;
    }

    return bookXSSF.getSheet(sheetName).getPhysicalNumberOfRows();
  }

  public File getExcelFile() {
    return excelFile;
  }

  public void initialize() throws IOException {
    excelFile = File.createTempFile("List_Eleves_Internat", ".xlsx");
    workbook = new XSSFWorkbook();
    sheet = workbook.createSheet("List_Eleves_Internat");
  }

  public String MiseAjourDateAbsenceEleveInternat(Gestioneleves gestioneleves, String dateAbsence)
      throws IOException {
    List<EleveInternat> eleveInternatList = gestioneleves.getElevesInternat();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
    LocalDate localDateAbsence = LocalDate.parse(dateAbsence, formatter);
    for (EleveInternat eleveInternat : eleveInternatList) {
      List<AbsenceeleveInternat> absenceeleveInternatList = eleveInternat.getAbsenceeleveInternat();
      if (absenceeleveInternatList.stream()
          .noneMatch(e -> e.getDateabsence().compareTo(localDateAbsence) == 0)) {
        AbsenceeleveInternat absenceeleveInternat = new AbsenceeleveInternat();
        absenceeleveInternat.setEleveInternat(eleveInternat);
        absenceeleveInternat.setDateabsence(localDateAbsence);
        absenceeleveInternatList.add(absenceeleveInternat);
        eleveInternat.setAbsenceeleveInternat(absenceeleveInternatList);
      }
    }
    return "khalid";
  }
}
