package com.hakibati.svg.examen.db.repo;

import com.hakibati.svg.examen.db.Prof;
import java.util.Map;

public class ProfRepository extends AbstractProfRepository {

  @Override
  public Map<String, Object> populate(Map<String, Object> json, Map<String, Object> context) {
    if (!context.containsKey("json-enhance")) {
      return json;
    }
    try {
      Long id = (Long) json.get("id");
      Prof prof = find(id);
      json.put("hasImage", prof.getImage() != null);
    } catch (Exception e) {
    }
    return json;
  }
}
