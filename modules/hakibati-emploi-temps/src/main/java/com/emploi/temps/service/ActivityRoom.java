package com.emploi.temps.service;

import com.hakibati.emploi.db.RoomFet;

public class ActivityRoom {
  private Integer id;
  private RoomFet roomFet;

  public ActivityRoom() {}

  public ActivityRoom(Integer id, RoomFet roomFet) {
    this.id = id;
    this.roomFet = roomFet;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RoomFet getRoomFet() {
    return roomFet;
  }

  public void setRoomFet(RoomFet roomFet) {
    this.roomFet = roomFet;
  }
}
