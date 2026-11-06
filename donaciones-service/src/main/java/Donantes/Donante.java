package Donantes;


import Actualizable.Actualizable;
import Contacto.DatosDeContacto;
import DTOs.DatosDonante;
import Direccion.Calle;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.DonanteJuridicoBuilder;
import Donantes.DonanteJuridico.TipoJuridica;
import Eliminable.Eliminable;
import MedioDeNotificacion.TipoDeMedio;
import Notificable.ConContacto;
import Notificable.ConMailIndividual;
import Notificable.ConMedioPreferencial;
import Notificable.ConTelefono;
import Persistible.Persistible;
import Usuario.ConSesion;
import Validaciones.Validaciones;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Donantes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_donante")
abstract public class Donante extends Persistible implements Actualizable <DatosDonante>, Eliminable, ConMailIndividual, ConTelefono, ConMedioPreferencial {

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_datos_de_contacto")
    private DatosDeContacto datosDeContacto;

    @Column(name = "esta_eliminado")
    private boolean estaEliminado = false;

    protected Donante() {
    }
    public Donante(DatosDeContacto contacto) {
        this.datosDeContacto = contacto;
    }

    public void actualizarDesde(DatosDonante datos, boolean parcial) {
        this.setEmail(Validaciones.actualizar(datos.getEmail(), this.getEmail(), "El email", parcial));
        this.setTelefono(Validaciones.actualizar(datos.getTelefonoSeteable(), this.getTelefono(), "El teléfono", parcial));
        this.setMedioPreferido(Validaciones.actualizar(datos.getTipoDeMedio(), this.getMedioDeNotificacionPreferencial(), "Medio de notificacion preferencial", parcial));
        this.refrescarAtributosParticulares(datos, parcial);
    }

    public void refrescar(DatosDonante datos) {
        actualizarDesde(datos, false);
    }

    abstract protected void refrescarAtributosParticulares(DatosDonante datos, boolean parcial);

    public Boolean esIgualA(Donante otroDonante){
        return this.getCaracteristicaDeDiferenciacion().equals(otroDonante.getCaracteristicaDeDiferenciacion());
    }

    abstract public String getCaracteristicaDeDiferenciacion();

    public DatosDeContacto getDatosDeContacto() {
        return this.datosDeContacto;
    }




    public static Donante crear(TipoPersona tipoPersona, NombreCompleto nombre, String documento,
                                DatosDeContacto contacto, LocalDate fechaDeNacimiento, Genero genero,
                                Calle calle, String rubro, TipoJuridica tipoJuridico) {

        return switch (tipoPersona) {
            case HUMANA ->
                new DonanteHumanoBuilder()
                    .conCalle(calle)
                    .conNombreCompleto(nombre)
                    .conFechaDeNacimiento(fechaDeNacimiento)
                    .conGenero(genero)
                    .conDocumento(documento)
                    .conContacto(contacto)
                    .build();
            case JURIDICA ->
                new DonanteJuridicoBuilder()
                    .conContacto(contacto)
                    .conRazonSocial(nombre.getNombre())
                    .conCuit(documento)
                    .conTipoJuridico(tipoJuridico)
                    .conRubro(rubro)
                    .build();
        };
    }

    public abstract TipoPersona getTipoPersona();

    public boolean estaEliminado() {
        return this.estaEliminado;
    }

    public void marcarComoEliminado() {
        this.estaEliminado = true;
    }

    public abstract String getNombreAMostrar();
}

