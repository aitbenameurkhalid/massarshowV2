package com.emploi.temps.service;

import com.hakibati.emploi.db.DayFet;
import com.hakibati.emploi.db.HourFet;

public class ActivityTime {
  private Integer id;
  private HourFet hourFet;
  private DayFet dayFet;

  public ActivityTime() {}

  public ActivityTime(Integer id, HourFet hourFet, DayFet dayFet) {
    this.id = id;
    this.hourFet = hourFet;
    this.dayFet = dayFet;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public HourFet getHourFet() {
    return hourFet;
  }

  public void setHourFet(HourFet hourFet) {
    this.hourFet = hourFet;
  }

  public DayFet getDayFet() {
    return dayFet;
  }

  public void setDayFet(DayFet dayFet) {
    this.dayFet = dayFet;
  }
}
