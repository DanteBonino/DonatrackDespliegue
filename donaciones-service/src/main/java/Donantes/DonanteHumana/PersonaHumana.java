package Donantes.DonanteHumana;

import Contacto.DatosDeContacto;
import DTOs.DatosDonante;
import Direccion.Calle;
import Direccion.Direccion;
import Donantes.Donante;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import Validaciones.Validaciones;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Entity
@DiscriminatorValue("HUMANA")
public class PersonaHumana extends Donante {
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "fecha_de_nacimiento")
    private LocalDate fechaDeNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "genero")
    private Genero genero;

    @Embedded
    private Calle calle;

    @Column(name = "documento")
    private String documento;

    protected PersonaHumana() {}
    public PersonaHumana(
        String nombre,
        String apellido,
        String documento,
        DatosDeContacto contacto,
        LocalDate fechaDeNacimiento,
        Genero genero,
        Calle calle
    ){
        super(contacto);
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.genero = genero;
        this.calle = calle;
    }


    @Override
    protected void refrescarAtributosParticulares(DatosDonante datos, boolean parcial){
        setNombre(Validaciones.actualizar(datos.getNombreRazonSocial(), getNombre(), "El nombre", parcial));
        setApellido(Validaciones.actualizar(datos.getApellido(), getApellido(), "El apellido", parcial));
        setDocumento(Validaciones.actualizar(datos.getDocumento(), getDocumento(), "El documento", parcial));
        setGenero(Validaciones.actualizar(datos.getGenero(), getGenero(), "El género", parcial));
        setFechaDeNacimiento(Validaciones.actualizar(datos.getFechaDeNacimiento(), getFechaDeNacimiento(), "La fecha de nacimiento", parcial));
    }

    @Override
    public TipoPersona getTipoPersona() {
        return TipoPersona.HUMANA;
    }


    @Override
    public String getCaracteristicaDeDiferenciacion(){
        return this.getDocumento();
    }

    public String getDocumento() {
        return this.documento;
    }

    private void setGenero(Genero genero) {
        this.genero = genero;
    }

    private void setFechaDeNacimiento(LocalDate unaFecha){
        this.fechaDeNacimiento = unaFecha;
    }

    private void setCalle(Calle calle){
        this.calle = calle;
    }

    private void setNombre(String unNombre){
        this.nombre = unNombre;
    }

    private void setApellido(String unApellido){
        this.apellido = unApellido;
    }

    private void setDocumento(String unDocumento){
        documento = unDocumento;
    }

    public String getNombre(){
        return nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public LocalDate getFechaDeNacimiento() {
        return this.fechaDeNacimiento;
    }

    public Genero getGenero() {
        return this.genero;
    }

    public Calle getCalle() {
        return this.calle;
    }

    public String getNombreAMostrar() {
        return this.getNombreCompleto();
    }

    public String getNombreCompleto() {
        return getNombre() + " " + getApellido();
    }

    public String getNumeroTelefonicoBase() {
        return getTelefono().getNumero();
    }

}




