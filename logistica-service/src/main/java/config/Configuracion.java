package config;
import utility.Entorno;

public class Configuracion {
  public String getUrlDonacionesService() { return Entorno.requerida("DONACIONES_SERVICE_URL"); }
  public String getUrlPlanificadorExterno() { return Entorno.requerida("PLANIFICADOR_EXTERNO_URL"); }
  public String getDireccionDeposito() { return Entorno.requerida("DEPOSITO_DIRECCION"); }
  public double getDepositoLatitud() { return Double.parseDouble(Entorno.requerida("DEPOSITO_LATITUD")); }
  public double getDepositoLongitud() { return Double.parseDouble(Entorno.requerida("DEPOSITO_LONGITUD")); }
}