package com.ass.enajah.service;

import com.axelor.meta.CallMethod;
import java.time.LocalDate;

public class DateService {
  @CallMethod
  public LocalDate getCurretDate() {
    LocalDate localDate = LocalDate.now();
    return localDate;
  }

  @CallMethod
  public String getCurretAnnee() {
    LocalDate localDate = LocalDate.now();
    return localDate.getYear() + "";
  }
}
