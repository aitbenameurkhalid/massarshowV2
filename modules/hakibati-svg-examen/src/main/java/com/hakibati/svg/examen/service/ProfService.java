package com.hakibati.svg.examen.service;

import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.CallMethod;
import com.axelor.meta.MetaFiles;
import com.axelor.meta.db.MetaFile;
import com.google.common.io.Files;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.*;
import com.hakibati.setting.db.repo.CycleRepository;
import com.hakibati.setting.db.repo.FonctionExamanRepository;
import com.hakibati.setting.db.repo.SexeRepository;
import com.hakibati.svg.examen.db.CentreExam;
import com.hakibati.svg.examen.db.Matiere;
import com.hakibati.svg.examen.db.Prof;
import com.hakibati.svg.examen.db.repo.CentreExamRepository;
import com.hakibati.svg.examen.db.repo.MatiereRepository;
import com.hakibati.svg.examen.db.repo.ProfRepository;
import com.setting.service.excel.ExcelLogWriter;
import com.setting.service.excel.ExcelReaderService;
import com.setting.service.excel.XmlRead;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

public class ProfService {

  String USER_ADMIN_GROUP = "admins";

  public String importProfExcel(MetaFile metaFile) throws IOException {
    User user = AuthUtils.getUser();
    Matiere matiere =
        Beans.get(MatiereRepository.class)
            .all()
            .filter("etablissement = ? AND name = ?", user.getEtablissementSelectionnee(), "sans")
            .fetchOne();
    if (matiere == null) {
      matiere = new Matiere();
      matiere.setName("sans");
      Beans.get(MatiereRepository.class).save(matiere);
    }

    String resulta = "";
    File excelFile = MetaFiles.getPath(metaFile).toFile();
    if (!Files.getFileExtension(excelFile.getName()).equals("xlsx")) {
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      return I18n.get("Choisissez un fichier au format xlsx");
    }

    ExcelReaderService excelReaderService = new ExcelReaderService();
    excelReaderService.initialize(metaFile, ";");

    int m = excelReaderService.getTotalLines("ListNames");
    //  response.setAlert(""+m);
    try {
      for (int maxl = 4; maxl <= m - 1; maxl++) {
        String[] strCells = excelReaderService.read("ListNames", maxl, 9);
        if (StringUtils.isBlank(strCells[3])) {
          continue;
        }
        Prof prof = new Prof();
        Prof profFind =
            Beans.get(ProfRepository.class)
                .all()
                .filter(
                    "id = ? AND etablissement.id= ?",
                    strCells[1],
                    +user.getEtablissementSelectionnee().getId())
                .fetchOne();
        if (profFind != null) {
          prof = profFind;
        }
        prof.setName(strCells[3]);
        if (!StringUtils.isBlank(strCells[4])) prof.setDoti(Integer.parseInt(strCells[4]));

        Sexe sexeFind =
            Beans.get(SexeRepository.class).all().filter("name = ?", strCells[5]).fetchOne();
        if (sexeFind != null) {
          prof.setSexe(sexeFind);
        } else {
          prof.setSexe(this.getNewSexe());
        }
        Matiere matiereFind =
            Beans.get(MatiereRepository.class)
                .all()
                .filter(
                    "name = ?  AND etablissement.id= ?",
                    strCells[6],
                    user.getEtablissementSelectionnee().getId())
                .fetchOne();
        if (matiereFind != null) {
          prof.setMatiere(matiereFind);
        } else {
          prof.setMatiere(this.getNewMatiere());
        }
        Cycle cycleFind =
            Beans.get(CycleRepository.class).all().filter("name = ?", strCells[7]).fetchOne();
        if (cycleFind != null) {
          prof.setCycle(cycleFind);
        } else {
          prof.setCycle(this.getNewCycle());
        }
        FonctionExaman fonctionExamanFind =
            Beans.get(FonctionExamanRepository.class)
                .all()
                .filter("name = ?", strCells[8])
                .fetchOne();
        if (fonctionExamanFind != null) {
          prof.setFonctionExaman(fonctionExamanFind);
        } else {
          prof.setFonctionExaman(this.getNewFonction());
        }
        Beans.get(ProfRepository.class).save(prof);
      }
    } catch (Exception e) {
      return I18n.get("Une erreur s'est produite lors de l'importation du fichier Excel")
          + e.getMessage();
    }

    MetaFiles.getPath(metaFile).toFile().deleteOnExit();
    //  response.setAlert("la list des profs  est  inmprt avec succes");
    String strrNotif =
        FileUtils.readFileToString(excelFile, StandardCharsets.UTF_8)
            .replaceAll("(\r\n|\n\r|\r|\n)", "<br />");

    excelFile.delete();
    return I18n.get("La liste des enseignants a été importée avec succès");
  }

  @Singleton
  @Transactional
  public String importProfEsise(MetaFile metaFile) {
    File xmlFile = MetaFiles.getPath(metaFile).toFile();
    if (!Files.getFileExtension(xmlFile.getName()).equals("xml")) {
      MetaFiles.getPath(metaFile).toFile().deleteOnExit();
      return I18n.get("Choisissez un fichier au format XML");
    }
    User user = AuthUtils.getUser();

    Matiere matiere =
        Beans.get(MatiereRepository.class)
            .all()
            .filter("etablissement = ? AND name = ?", user.getEtablissementSelectionnee(), "sans")
            .fetchOne();
    if (matiere == null) {
      matiere = new Matiere();
      matiere.setName("sans");
      Beans.get(MatiereRepository.class).save(matiere);
    }

    try {
      XmlRead xmlRead = new XmlRead();
      xmlRead.initialize(metaFile, "DATAIDENTIFPERSONNEL");

      for (int i = 0; i < xmlRead.getLengthNod(); i++) {
        try {
          String[] names =
              xmlRead.read(
                  i, new String[] {"PPR", "NOML", "NOMA", "GENRE", "CD_CADRE", "CD_DISCIP"});
          Prof prof = new Prof();
          Prof profFind =
              Beans.get(ProfRepository.class)
                  .all()
                  .filter(
                      "name = ? AND etablissement.id= ?",
                      names[2],
                      +user.getEtablissementSelectionnee().getId())
                  .fetchOne();
          if (profFind != null) {
            prof = profFind;
          }

          prof.setName(names[2]);
          prof.setDoti(Integer.parseInt(names[0]));

          Sexe sexeFind =
              Beans.get(SexeRepository.class)
                  .all()
                  .filter("code = ?", (names[3]).matches(".*F.*") ? "2" : "3")
                  .fetchOne();
          if (sexeFind != null) {
            prof.setSexe(sexeFind);
          } else {
            prof.setSexe(this.getNewSexe());
          }
          if (StringUtils.isBlank(names[5]) || names[5] == null || names[5] == "")
            names[5] = "0000";
          Matiere matiereFind =
              Beans.get(MatiereRepository.class)
                  .all()
                  .filter(
                      "code = ? AND etablissement.id= ?",
                      Integer.parseInt(names[5]),
                      user.getEtablissementSelectionnee().getId())
                  .fetchOne();
          if (matiereFind != null) {
            prof.setMatiere(matiereFind);
          } else {
            prof.setMatiere(this.getNewMatiere());
          }
          Cycle cycleFind =
              Beans.get(CycleRepository.class)
                  .all()
                  .filter(
                      "code = ?",
                      (("110902 110202").matches(".*" + names[4] + ".*")
                          ? "2"
                          : ("110903 110201").matches(".*" + names[4] + ".*")
                              ? "3"
                              : (("110901 110204").matches(".*" + names[4] + ".*") ? "1" : "0")))
                  .fetchOne();
          if (cycleFind != null) {
            prof.setCycle(cycleFind);
          } else {
            prof.setCycle(this.getNewCycle());
          }
          FonctionExaman fonctionExamanFind =
              Beans.get(FonctionExamanRepository.class).all().fetchOne();
          if (fonctionExamanFind != null) {
            prof.setFonctionExaman(fonctionExamanFind);
          } else {
            prof.setFonctionExaman(this.getNewFonction());
          }
          Beans.get(ProfRepository.class).save(prof);
        } catch (Exception e) {
          continue;
          //  return I18n.get("Une erreur s'est produite lors de l'importation du fichier Esise") +
          // e.getMessage();
        }
      }

    } catch (Exception e) {
      return I18n.get("Une erreur s'est produite lors de l'importation du fichier Esise")
          + e.getMessage();
    }
    return I18n.get("La liste des profs a été importée avec succès");
  }

  public String AjoutProfs(int minProf, int maxProf, String prefixe) {
    User user = AuthUtils.getUser();
    Matiere matiere =
        Beans.get(MatiereRepository.class)
            .all()
            .filter("etablissement = ? AND name = ?", user.getEtablissementSelectionnee(), "sans")
            .fetchOne();
    if (matiere == null) {
      matiere = new Matiere();
      matiere.setName("sans");
      Beans.get(MatiereRepository.class).save(matiere);
    }

    ProfRepository profRepository = Beans.get(ProfRepository.class);
    try {
      for (int i = minProf; i <= maxProf; i++) {
        Prof prof = new Prof();
        prof.setName(prefixe + "-" + +i);
        prof.setFonctionExaman(getNewFonction());
        prof.setSexe(getNewSexe());
        prof.setCycle(getNewCycle());
        prof.setMatiere(getNewMatiere());
        prof.setDoti(0);
        profRepository.save(prof);
      }
      return I18n.get("opération réussie");
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  @CallMethod
  public Sexe getNewSexe() {
    Sexe sexe = Beans.get(SexeRepository.class).all().filter("name = ?", "Sans").fetchOne();
    if (sexe != null) return sexe;
    return Beans.get(SexeRepository.class).all().fetchOne();
  }

  @CallMethod
  public Matiere getNewMatiere() {
    User user = AuthUtils.getUser();
    Matiere matiere =
        Beans.get(MatiereRepository.class)
            .all()
            .filter("name = ? AND etablissement = ?", "sans", user.getEtablissementSelectionnee())
            .fetchOne();
    if (matiere != null) return matiere;
    matiere = new Matiere();
    matiere.setName("sans");
    Beans.get(MatiereRepository.class).save(matiere);

    matiere =
        Beans.get(MatiereRepository.class)
            .all()
            .filter("name = ? AND etablissement = ?", "sans", user.getEtablissementSelectionnee())
            .fetchOne();
    if (matiere != null) return matiere;
    return null;
  }

  @CallMethod
  public FonctionExaman getNewFonction() {
    FonctionExaman fonctionExaman =
        Beans.get(FonctionExamanRepository.class).all().filter("code = ?", 1).fetchOne();
    if (fonctionExaman != null) return fonctionExaman;
    return Beans.get(FonctionExamanRepository.class).all().fetchOne();
  }

  @CallMethod
  public Cycle getNewCycle() {
    Cycle cycle =
        Beans.get(CycleRepository.class).all().filter("name = ?", "Qualifiant").fetchOne();
    if (cycle != null) return cycle;
    return Beans.get(CycleRepository.class).all().fetchOne();
  }

  @CallMethod
  public Cycle getNewCycle(Integer i) {
    Cycle cycle = Beans.get(CycleRepository.class).all().filter("code = ?", i).fetchOne();
    if (cycle != null) return cycle;
    return Beans.get(CycleRepository.class).all().fetchOne();
  }

  public File exportProfExcel() throws IOException {
    ExcelLogWriter logWriter = new ExcelLogWriter();
    User user = AuthUtils.getUser();
    List<Prof> profList =
        Beans.get(ProfRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    List<CentreExam> centreExamList =
        Beans.get(CentreExamRepository.class)
            .all()
            .filter("etablissement = ? ", user.getEtablissementSelectionnee())
            .fetch();
    logWriter.initialize("../../assets/ListProfs2.xlsx", "ListNames");
    if (centreExamList.size() > 0) {
      logWriter.writeBody(0, 3, new String[] {centreExamList.get(0).getAcademie()});
      logWriter.writeBody(0, 8, new String[] {centreExamList.get(0).getDirection()});
      logWriter.writeBody(1, 3, new String[] {centreExamList.get(0).getCommun()});
      logWriter.writeBody(1, 8, new String[] {centreExamList.get(0).getName()});
    }
    for (int i = 0; i < profList.size(); i++) {

      try {
        if (i < 200)
          logWriter.writeBody(
              4 + i,
              1,
              new String[] {
                String.valueOf(profList.get(i).getId()),
                String.valueOf(i + 1),
                profList.get(i).getName(),
                String.valueOf(profList.get(i).getDoti()),
                profList.get(i).getSexe().getName(),
                profList.get(i).getMatiere().getName(),
                profList.get(i).getCycle().getName(),
                profList.get(i).getFonctionExaman().getName()
              });
        else
          logWriter.createBody(
              4 + i,
              1,
              new String[] {
                String.valueOf(profList.get(i).getId()),
                String.valueOf(i + 1),
                profList.get(i).getName(),
                String.valueOf(profList.get(i).getDoti()),
                profList.get(i).getSexe().getName(),
                profList.get(i).getMatiere().getName(),
                profList.get(i).getCycle().getName(),
                profList.get(i).getFonctionExaman().getName()
              });
      } catch (Exception e) {
      }
    }

    for (int i = 0; i < Beans.get(SexeRepository.class).all().fetch().size(); i++) {
      if (i < 100)
        logWriter.writeBody(
            i,
            16,
            new String[] {
              I18n.get(Beans.get(SexeRepository.class).all().fetch().get(i).getName())
            });
      else
        logWriter.createBody(
            i,
            16,
            new String[] {
              I18n.get(Beans.get(SexeRepository.class).all().fetch().get(i).getName())
            });
    }
    for (int i = 0;
        i
            < Beans.get(MatiereRepository.class)
                .all()
                .filter("etablissement = ? ", user.getEtablissementSelectionnee())
                .fetch()
                .size();
        i++) {
      if (i < 100)
        logWriter.writeBody(
            i,
            17,
            new String[] {Beans.get(MatiereRepository.class).all().fetch().get(i).getName()});
      else
        logWriter.createBody(
            i,
            17,
            new String[] {Beans.get(MatiereRepository.class).all().fetch().get(i).getName()});
    }

    for (int i = 0; i < Beans.get(CycleRepository.class).all().fetch().size(); i++) {
      if (i < 100)
        logWriter.writeBody(
            i, 18, new String[] {Beans.get(CycleRepository.class).all().fetch().get(i).getName()});
      else
        logWriter.createBody(
            i, 18, new String[] {Beans.get(CycleRepository.class).all().fetch().get(i).getName()});
    }
    for (int i = 0; i < Beans.get(FonctionExamanRepository.class).all().fetch().size(); i++) {
      if (i < 100)
        logWriter.writeBody(
            i,
            19,
            new String[] {
              Beans.get(FonctionExamanRepository.class).all().fetch().get(i).getName()
            });
      else
        logWriter.createBody(
            i,
            19,
            new String[] {
              Beans.get(FonctionExamanRepository.class).all().fetch().get(i).getName()
            });
    }

    logWriter.close();

    File file = logWriter.getExcelFile();
    return file;
  }

  public String AjoutProfsMassarshow(
      List<Allpersonnel> allpersonnelList, AllEtabMassar allEtabMassar) {
    User user = AuthUtils.getUser();
    Integer CycleCode = 0;
    if (allEtabMassar != null) {
      if (allEtabMassar.getEnsQualO() || allEtabMassar.getEnsQualG() || allEtabMassar.getEnsQualT())
        CycleCode = 3;
      if (allEtabMassar.getEnsColG() || allEtabMassar.getEnsColO()) CycleCode = 2;
      if (allEtabMassar.getEnsPrimG() || allEtabMassar.getEnsPrimO()) CycleCode = 1;
    }
    List<Prof> profList =
        Beans.get(ProfRepository.class)
            .all()
            .filter("etablissement = ?", user.getEtablissementSelectionnee())
            .fetch();
    for (Allpersonnel allpersonnel : allpersonnelList) {
      Prof prof =
          profList.stream()
              .filter(
                  e -> e.getDoti() != null && Objects.equals(e.getDoti(), allpersonnel.getCode()))
              .findAny()
              .orElse(null);
      if (prof == null) prof = new Prof();
      prof.setDoti(allpersonnel.getCode());
      prof.setName(allpersonnel.getName());
      prof.setFonctionExaman(getNewFonction());
      prof.setSexe(getNewSexe());
      prof.setCycle(getNewCycle(CycleCode));
      prof.setMatiere(getNewMatiere());
      prof.setEtablissementDeOrigine(allpersonnel.getEtablissement());
      prof.setIsEtab(false);
      Beans.get(ProfRepository.class).save(prof);
    }
    return (I18n.get("Opération terminée"));
  }
}
