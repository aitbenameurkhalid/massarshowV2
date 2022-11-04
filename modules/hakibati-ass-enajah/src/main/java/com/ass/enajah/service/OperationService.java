package com.ass.enajah.service;

import com.app.application.db.Etablissement;
import com.axelor.db.JPA;
import com.google.inject.Inject;
import com.hakibati.ass.enajah.db.OperationDepense;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationService {
  Logger log = LoggerFactory.getLogger(getClass());

  public String getNumeroOperation(
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

  @Inject
  public static String getSomme(OperationDepense operationDepense, String str) {

    Query q;
    q = JPA.em().createQuery(str);
    q.setParameter(1, operationDepense.getId());
    q.setMaxResults(1);
    BigDecimal somme = (BigDecimal) q.getSingleResult();

    if (somme == null) {
      somme = BigDecimal.ZERO;
    }
    return somme + "";
  }
}
