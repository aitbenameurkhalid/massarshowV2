package com.gestion.eleves.repo;

import com.axelor.db.JpaRepository;
import com.axelor.db.Query;
import com.hakibati.gestion.eleves.db.Eleve;

public class EleveRepository extends JpaRepository<Eleve> {

  //    protected EleveRepository(Class<Eleve> modelClass) {
  //        super(modelClass);
  //    }
  public EleveRepository() {
    super(Eleve.class);
  }

  public Eleve findBycodeMassar(String cdMassar) {
    return Query.of(Eleve.class)
        .filter("self.cdMassar = :cdMassar")
        .bind("cdMassar", cdMassar)
        .cacheable()
        .fetchOne();
  }
}
