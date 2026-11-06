package DTOs;

import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Direccion.Calle;
import Direccion.builders.CalleBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.TipoJuridica;
import Donantes.TipoPersona;
import MedioDeNotificacion.TipoDeMedio;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.time.LocalDate;
import java.util.List;

public class DonanteDto implements DatosDonante{
    @CsvBindByName(column = "TipoPersona")
    private TipoPersona tipoPersona;

    @CsvBindByName(column = "Documento")
    private String documento;

    @CsvBindByName(column = "Nombre/RazonSocial")
    private String nombreRazonSocial;

    @CsvBindByName(column = "Email")
    private String email;

    @CsvBindByName(column = "Telefono")
    private String telefono;

    @CsvBindByName(column = "Apellido")
    private String apellido;

    @CsvBindByName(column = "FechaDeNacimiento")
    @CsvDate("yyyy-MM-dd")
    private LocalDate fechaDeNacimiento;

    @CsvBindByName(column = "NombreCalle")
    private String nombreCalle;

    @CsvBindByName(column = "AlturaCalle")
    private int alturaCalle;

    @CsvBindByName(column = "Genero")
    private Genero genero;

    @CsvBindByName(column = "Rubro")
    private String rubro;

    @CsvBindByName(column = "TipoJuridico")
    private TipoJuridica tipoJuridico;

    @CsvBindByName(column = "ID")
    private String id;

    private TipoDeMedio tipoDeMedio;

    public DonanteDto() {
    }


    public TipoPersona getTipoPersona() { return tipoPersona; }

    public String getDocumento() { return documento; }

    public String getNombreRazonSocial() { return nombreRazonSocial; }

    public String getEmail() { return email; }

    public String getTelefono() { return telefono; }

    public String getApellido() {
      return apellido;
    }

    public LocalDate getFechaDeNacimiento() { return fechaDeNacimiento; }

    public String getNombreCalle() { return nombreCalle; }

    public int getAlturaCalle() {
        return alturaCalle;
    }

    public Genero getGenero() { return genero; }

    public String getRubro() { return rubro; }

    public TipoJuridica getTipoJuridico() { return tipoJuridico; }

    public String getId() {
      return this.id;
    }

    private DatosDeContacto getContactoDonante () {
        return new DatosDeContactoBuilder()
            .conMail(email)
            .conTelefono(telefono)
            .conMedioPreferido(tipoDeMedio)
            .build();
    }

    public Calle getCalle() {
        return new Calle(nombreCalle, alturaCalle);
    }

    public TipoDeMedio getTipoDeMedio() {
        return tipoDeMedio;
    }

    public Donante crearInstancia(){
        return Donante.crear(
            this.getTipoPersona(),
            new NombreCompleto(this.getNombreRazonSocial(), this.getApellido()),
            this.getDocumento(),
            getContactoDonante(),
            this.getFechaDeNacimiento(),
            this.getGenero(),
            this.getCalle(),
            this.getRubro(),
            this.getTipoJuridico()
        );
    }
}
