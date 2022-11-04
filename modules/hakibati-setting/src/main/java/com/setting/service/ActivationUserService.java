package com.setting.service;

import com.axelor.inject.Beans;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
import com.hakibati.setting.db.ActivationUser;
import com.hakibati.setting.db.repo.ActivationUserRepository;

@Transactional
@Singleton
public class ActivationUserService {
  public static String getcodeActivation() {
    int max = 64;
    int min = 0;
    String str =
        "AZERTYUIOPQSDFGHJKLMWXCVBN1234567890@azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLM";
    String res = "";
    for (int i = 0; i < 12; i++) {
      int a = (int) (Math.random() * (max - min));
      char c = str.charAt(a);
      res += c;
    }
    return res;
  }

  @Transactional
  @Singleton
  public static void saveCodeEmailActivation(String email, String codeActivation) {
    String codeActivationHashMD5 = MD5.encrypt(codeActivation);
    if (verifieEmail(email) == 0) {
      ActivationUser activationUser = new ActivationUser();
      activationUser.setCodeActivationHash(codeActivationHashMD5);
      activationUser.setEmail(email);
      Beans.get(ActivationUserRepository.class).save(activationUser);
    } else {
      Beans.get(ActivationUserRepository.class)
          .all()
          .filter("email = ?", email)
          .fetchOne()
          .setCodeActivationHash(codeActivationHashMD5);
      Beans.get(ActivationUserRepository.class)
          .all()
          .filter("email = ?", email)
          .fetchOne()
          .setCodeActivationHash(codeActivationHashMD5);
    }
  }

  public static Long verifieEmail(String email) {
    Long count = Beans.get(ActivationUserRepository.class).all().filter("email = ?", email).count();
    return count;
  }

  public static String getCodeOfEmail(String email) {
    return Beans.get(ActivationUserRepository.class)
        .all()
        .filter("email = ?", email)
        .fetchOne()
        .getCodeActivationHash();
  }
}
