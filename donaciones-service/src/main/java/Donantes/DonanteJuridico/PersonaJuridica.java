package Donantes.DonanteJuridico;

import Contacto.DatosDeContacto;
import DTOs.DatosDonante;
import Donantes.Donante;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import Validaciones.Validaciones;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue("JURIDICA")
public class PersonaJuridica extends Donante {
    @Column(name = "razon_social")
    String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_juridico")
    TipoJuridica tipo;

    @Column(name = "rubro")
    String rubro;

    @Column(name = "cuit")
    String cuit;

    protected PersonaJuridica() {}
    public PersonaJuridica(
        String razonSocial,
        String documento,
        DatosDeContacto contacto,
        TipoJuridica tipo,
        String rubro
    ){
        super(contacto);
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro = rubro;
        this.cuit = documento;
    }

    @Override
    protected void refrescarAtributosParticulares(DatosDonante datos, boolean parcial){
        setRazonSocial(Validaciones.actualizar(datos.getNombreRazonSocial(), getRazonSocial(), "La razón social", parcial));
        setTipo(Validaciones.actualizar(datos.getTipoJuridico(), getTipo(), "El tipo jurídico", parcial));
        setRubro(Validaciones.actualizar(datos.getRubro(), getRubro(), "El rubro", parcial));
        setCuit(Validaciones.actualizar(datos.getDocumento(), getCuit(), "El CUIT", parcial));
    }

    @Override
    public TipoPersona getTipoPersona() {
        return TipoPersona.JURIDICA;
    }


    private void setCuit(String documento) {
        this.cuit = documento;
    }

    private void setRubro(String rubro) {
        this.rubro = rubro;
    }

    private void setTipo(TipoJuridica tipoJuridico) {
        this.tipo = tipoJuridico;
    }

    private void setRazonSocial(String nombreRazonSocial) {
        this.razonSocial = nombreRazonSocial;
    }

    @Override
    public String getCaracteristicaDeDiferenciacion(){
        return this.getCuit();
    }

    private String getCuit() {
        return this.cuit;
    }

    public String getRazonSocial() {
        return this.razonSocial;
    }

    public String getRubro() {
        return this.rubro;
    }

    public TipoJuridica getTipo() {
        return this.tipo;
    }

    public String getNombreAMostrar() {
        return getRazonSocial();
    }
}


