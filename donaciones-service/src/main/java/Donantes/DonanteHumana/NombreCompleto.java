package Donantes.DonanteHumana;

public class NombreCompleto {
  private String nombre;
  private String apellido;

  public NombreCompleto(String unNombre, String unApellido){
    this.nombre = unNombre;
    this.apellido = unApellido;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

  public String getNombreCompleto(){
    return this.getNombre() + " " + this.getApellido();
  }
}
