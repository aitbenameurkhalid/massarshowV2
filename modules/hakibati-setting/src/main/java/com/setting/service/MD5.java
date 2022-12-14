package com.setting.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
  public static String encrypt(String passwordToHash) {
    String generatedPassword = "5f4dcc3b5aa765d61d8327deb882cf99";
    try {
      // Create MessageDigest instance for MD5
      MessageDigest md = MessageDigest.getInstance("MD5");
      // Add password bytes to digest
      md.update(passwordToHash.getBytes());
      // Get the hash's bytes
      byte[] bytes = md.digest();
      // This bytes[] has bytes in decimal format;
      // Convert it to hexadecimal format
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      // Get complete hashed password in hex format
      generatedPassword = sb.toString();

    } catch (NoSuchAlgorithmException e) {
    }
    return generatedPassword;
  }
}
