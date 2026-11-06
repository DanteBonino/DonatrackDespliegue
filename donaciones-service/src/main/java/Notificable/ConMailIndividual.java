package Notificable;

import java.util.List;

public interface ConMailIndividual extends ConContacto {
  default void setEmail(String email){
    this.getDatosDeContacto().setMails(List.of(email));
  }

  default String getEmail(){
    return this.getDatosDeContacto().getMailsParaNotificar().get(0);
  }
}
