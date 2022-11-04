package com.comptabilite.matier.service;

import com.app.application.db.Etablissement;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.db.JPA;
import com.hakibati.comptabilite.matier.db.Mconsommable;
import java.time.LocalDate;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationComptabiliteService {
  Logger log = LoggerFactory.getLogger(getClass());

  public String getNumeroOperationE(
      LocalDate dateOperation, Long id, Etablissement etab, String str) {
    String code = null;

    Query q;
    try {
      q = JPA.em().createQuery(str);
      q.setParameter(1, dateOperation.getYear());
      q.setParameter(2, id);
      q.setParameter(3, etab);
      q.setParameter(4, "");
      q.setMaxResults(1);
      code = (String) q.getSingleResult();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (code == null) {
      return "1/" + dateOperation.getYear();
    }
    String[] s = code.split("/", 2);
    return (Long.valueOf(s[0]) + 1) + "/" + dateOperation.getYear();
  }

  public Integer getRestMagasin(String strS) {
    String code = null;
    Integer quantiteE = 0;
    Integer quantiteS = 0;
    Query q;
    try {
      q = JPA.em().createQuery(strS);
      //      q.setParameter(1, etab);
      //      q.setParameter(2, dateDeDepart);
      //      q.setParameter(3, mconsommable);
      q.setMaxResults(1);
      quantiteE = (Integer) q.getSingleResult();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return quantiteE - quantiteS;
  }

  public Integer getRestMagasin(
      LocalDate dateDeDepart, Etablissement etab, Mconsommable mconsommable, String strE) {
    javax.persistence.Query q = JPA.em().createQuery(strE, Long.class);
    q.setParameter(1, etab.getId());
    q.setParameter(2, dateDeDepart);
    q.setParameter(3, mconsommable.getId());
    q.setParameter(4, false);
    Long sum = (Long) q.getSingleResult();

    if (sum == null) return 0;
    return sum.intValue();
  }

  public Integer getRestMagasinTotal(Etablissement etab, Mconsommable mconsommable, String strE) {
    javax.persistence.Query q = JPA.em().createQuery(strE, Long.class);
    q.setParameter(1, etab.getId());
    q.setParameter(2, mconsommable.getId());
    q.setParameter(3, false);
    Long sum = (Long) q.getSingleResult();

    if (sum == null) return 0;
    return sum.intValue();
  }

  public String getNumeroOperation(LocalDate dateOperation, String str) {
    User user = AuthUtils.getUser();
    javax.persistence.Query q = JPA.em().createQuery(str, String.class);
    q.setParameter(1, dateOperation.getYear());
    q.setParameter(2, user.getEtablissementSelectionnee());
    q.setParameter(3, "");
    q.setMaxResults(1);

    String code = (String) q.getSingleResult();

    if (code == null) {
      return "1/" + dateOperation.getYear();
    }
    String[] s = code.split("/", 2);
    return (Long.valueOf(s[0]) + 1) + "/" + dateOperation.getYear();
  }
}
