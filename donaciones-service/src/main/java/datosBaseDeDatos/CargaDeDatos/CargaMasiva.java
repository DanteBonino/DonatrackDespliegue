package datosBaseDeDatos.CargaDeDatos;

import Bienes.Bien;
import Bienes.Categoria.Categoria;
import Bienes.EstadoDeUso;
import Bienes.SubCategoria.SubCategoria;
import Contacto.DatosDeContacto;
import Contacto.DatosDeContactoBuilder;
import Direccion.*;
import Direccion.builders.CalleBuilder;
import Direccion.builders.DireccionBuilder;
import Direccion.builders.PosicionBuilder;
import Donaciones.DonacionIndependiente;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.DonanteJuridicoBuilder;
import Donantes.DonanteJuridico.TipoJuridica;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import EstadoDonacion.EstadoDonacion;
import MedioDeNotificacion.TipoDeMedio;
import Necesidades.Necesidad;
import Notificaciones.Notificacion;
import ObserversCambioDeEstado.EntregaConExito.ComprobanteDeEntrega;
import Repositories.Administradores.AdministradoresRepository;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.Categorias.CategoriasRepository;
import Repositories.Donaciones.DonacionesRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.Notificaciones.NotificacionesRepository;
import Repositories.SubCategorias.SubCategoriasRepository;
import Repositories.UsuariosRepository;
import Usuario.Administrador;
import UsuarioFinal.UsuarioAdministrador;
import UsuarioFinal.UsuarioBeneficiario;
import UsuarioFinal.UsuarioDonante;
import datosBaseDeDatos.ModificadorBaseDeDatos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import utils.FabricaDeBienes;
import utils.FabricaDeCondicionesDeAceptacion;
import utils.FabricaDeDonacionesIndependientes;
import utils.FabricaDeNecesidades;
import utils.FabricaDeNotificaciones;
import utils.FabricaDeSubCategorias;

public class CargaMasiva extends ModificadorBaseDeDatos {

  private static final LocalDate VENC_FUTURO  = LocalDate.of(2027, 1, 31);
  private static final LocalDate VENC_TOMATE  = LocalDate.of(2026, 11, 20);
  private static final LocalDate VENC_PASADO  = LocalDate.of(2026, 10, 15);

  private static final EstadoDonacion DEP   = EstadoDonacion.EN_DEPOSITO;
  private static final EstadoDonacion PEND  = EstadoDonacion.PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION;
  private static final EstadoDonacion ASIG  = EstadoDonacion.ASIGNACION_REALIZADA;
  private static final EstadoDonacion LISTA = EstadoDonacion.LISTA_PARA_ENTREGAR;
  private static final EstadoDonacion TRAS  = EstadoDonacion.EN_TRASLADO;
  private static final EstadoDonacion ENT   = EstadoDonacion.ENTREGADA;
  private static final EstadoDonacion FALL  = EstadoDonacion.ENTREGA_FALLIDA;
  private static final EstadoDonacion VENC  = EstadoDonacion.VENCIDA;

  public static void main(String[] args) {
    new CargaMasiva().ejecutar();
  }

  @Override
  public void ejecutar() {
    super.ejecutar();
    if (BeneficiariosRepository.getInstance().cantidadInstancias() > 0) {
      System.out.println("Ya existen entidades beneficiarias en la base. Abortando carga.");
      return;
    }

    cargarCategoriasSiFaltan();
    SubCategoriasRepository subRepo = SubCategoriasRepository.getInstance();
    SubCategoria remera       = subRepo.findByNombre("remera");
    SubCategoria pantalon     = subRepo.findByNombre("pantalon");
    SubCategoria campera      = subRepo.findByNombre("campera");
    SubCategoria arroz        = subRepo.findByNombre("arroz");
    SubCategoria fideosSecos  = subRepo.findByNombre("fideossecos");
    SubCategoria tomate       = subRepo.findByNombre("tomate");
    SubCategoria mesa         = subRepo.findByNombre("mesa");
    SubCategoria silla        = subRepo.findByNombre("silla");

    List<EntidadBeneficiaria> entidades =
        crearEntidades(remera, pantalon, campera, arroz, fideosSecos, tomate, mesa, silla);

    List<Donante> donantes = crearDonantes();

    List<DonacionIndependiente> donaciones =
        crearDonaciones(donantes, entidades,
            remera, pantalon, campera, arroz, fideosSecos, tomate, mesa, silla);

    List<Notificacion> notificaciones =
        crearNotificaciones(donantes.get(0), entidades.get(0));
    List<String> usernamesDonantes = List.of(
        "ana",
        "bruno",
        "carla",
        "diego",
        "elena",
        "facundo",
        "gabriela",
        "elSol",
        "manosUnidas",
        "textilDelSur"
    );
    List<String> usernamesEntidadesBeneficiarias = List.of(
        "comedorPorUnFuturoMasUnido",
        "hogarSanJose",
        "escuelaRural",
        "centroComunitarioEsperanza"
    );
    Administrador admin = new Administrador(
        new DatosDeContactoBuilder()
            .conTelefono("1140811648")
            .conMail("dantebonino3@gmail.com")
            .build());
    withTransaction(() -> {
      BeneficiariosRepository.getInstance().registrarInstancias(entidades);
      DonantesRepository.getInstance().registrarInstancias(donantes);
      DonacionesRepository.getInstance().registrarInstancias(donaciones);
      notificaciones.forEach(n -> NotificacionesRepository.getInstance().registrar(n));
      for (int i = 0; i < donantes.size(); i++) {
        var unDonante = donantes.get(i);
        UsuariosRepository.getInstance().registrar(
            new UsuarioDonante(usernamesDonantes.get(i), "test1234", unDonante)
        );
      }
      for (int i = 0; i < entidades.size(); i++) {
        var entidadBeneficiaria = entidades.get(i);
        UsuariosRepository.getInstance().registrar(
            new UsuarioBeneficiario(usernamesEntidadesBeneficiarias.get(i), "test1234", entidadBeneficiaria)
        );
      }
      AdministradoresRepository.getInstance().registrar(admin);
      UsuariosRepository.getInstance().registrar(new UsuarioAdministrador("testAdmin", "test1234", admin));
    });

    System.out.println("=== Carga inicial completada ===");
    System.out.println("  Entidades beneficiarias : " + entidades.size());
    System.out.println("  Donantes                : " + donantes.size());
    System.out.println("  Donaciones              : " + donaciones.size());
    System.out.println("  Notificaciones          : " + notificaciones.size());
    System.out.println("  Usuarios de login       : 3");
    System.out.println("    user: testdonante@donatrack.com / pass: test");
    System.out.println("    user: testentidadbeneficiaria@donatrack.com / pass: test");
    System.out.println("    user: testadmin@donatrack.com / pass: test");
  }

  private void cargarCategoriasSiFaltan() {
    try {
      new CargarCategoriasYSubCategorias().cargar();
    } catch (Exception e) {
      System.out.println("Categorías/subcategorías ya existían, se continúa.");
    }

    SubCategoriasRepository subRepo = SubCategoriasRepository.getInstance();
    asegurarSubcategoria(subRepo, "tomate", "ALIMENTO",   true);
    asegurarSubcategoria(subRepo, "silla",  "MOBILIARIO", false);
  }

  private void asegurarSubcategoria(SubCategoriasRepository subRepo,
                                    String nombre,
                                    String nombreCategoria,
                                    boolean esPerecedera) {
    try {
      subRepo.findByNombre(nombre);
      return;
    } catch (Exception noExiste) {
      // La creamos a continuación.
    }

    Categoria categoria = CategoriasRepository.getInstance()
        .obtenerUnoSegun("nombre", nombreCategoria);

    SubCategoria nueva = esPerecedera
        ? FabricaDeSubCategorias.getSubCategoriaPerecedera(nombre, categoria)
        : FabricaDeSubCategorias.getSubCategoriaNoPerecedera(nombre, categoria);

    withTransaction(() -> subRepo.registrar(nueva));
    System.out.println("Subcategoría creada: " + nombre);
  }

  private List<EntidadBeneficiaria> crearEntidades(
      SubCategoria remera, SubCategoria pantalon, SubCategoria campera,
      SubCategoria arroz, SubCategoria fideosSecos, SubCategoria tomate,
      SubCategoria mesa, SubCategoria silla) {

    // E01 — Comedor por un futuro más unido: 8 necesidades, todas con condición "Basica"
    // para que acepte cualquier donación vigente de esa subcategoría
    EntidadBeneficiaria e01 = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Comedor por un futuro más unido")
        .conDireccion(direccion("Av. Rivadavia", 4500, -34.6085, -58.3838))
        .conContacto(contacto("testentidadbeneficiaria@donatrack.com", "1150000001", TipoDeMedio.MAIL))
        .conNecesidades(List.of(
            FabricaDeNecesidades.necesidadVigenteDe(remera,      1000),
            FabricaDeNecesidades.necesidadVigenteDe(pantalon,     800),
            FabricaDeNecesidades.necesidadVigenteDe(arroz,       1000),
            FabricaDeNecesidades.necesidadVigenteDe(campera,      600),
            FabricaDeNecesidades.necesidadVigenteDe(fideosSecos,  900),
            FabricaDeNecesidades.necesidadVigenteDe(tomate,       400),
            FabricaDeNecesidades.necesidadVigenteDe(mesa,         150),
            FabricaDeNecesidades.necesidadVigenteDe(silla,        400)
        ))
        .build();

    // E02 — Hogar San José: 2 necesidades (campera USADO + mesa NUEVO).
    EntidadBeneficiaria e02 = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Hogar San José")
        .conDireccion(direccion("Mitre", 1234, -34.6150, -58.3750))
        .conContacto(contacto("hogarsanjose@test.org", "1150000002", TipoDeMedio.WPP))
        .conNecesidades(List.of(
            necesidadEnEstadoUsado(campera, 500),
            FabricaDeNecesidades.necesidadEnEstadoNuevo(mesa, 200)
        ))
        .build();

    // E03 — Escuela Rural N° 12: 1 necesidad (remera Basica).
    EntidadBeneficiaria e03 = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Escuela Rural N° 12")
        .conDireccion(direccion("Ruta 8", 120, -34.5000, -58.7000))
        .conContacto(contacto("escuelarural12@test.org", "1150000003", TipoDeMedio.SMS))
        .conNecesidades(List.of(
            FabricaDeNecesidades.necesidadVigenteDe(remera, 600)
        ))
        .build();

    // E04 — Centro Comunitario Esperanza: 2 necesidades (arroz + tomate).
    EntidadBeneficiaria e04 = new EntidadBeneficiariaBuilder()
        .conRazonSocial("Centro Comunitario Esperanza")
        .conDireccion(direccion("Belgrano", 2200, -34.6200, -58.4000))
        .conContacto(contacto("esperanza@test.org", "1150000004", TipoDeMedio.MAIL))
        .conNecesidades(List.of(
            FabricaDeNecesidades.necesidadVigenteDe(arroz, 700),
            FabricaDeNecesidades.necesidadVigenteDe(tomate, 300)
        ))
        .build();

    return List.of(e01, e02, e03, e04);
  }

  private Necesidad necesidadEnEstadoUsado(SubCategoria sub, int cantidad) {
    return FabricaDeNecesidades.nuevaNecesidadExtraordinariaConCantidaRequeridaExacta(
        FabricaDeCondicionesDeAceptacion.Usado(sub), cantidad);
  }

  private List<Donante> crearDonantes() {
    Donante d01 = humana("Ana", "Gómez", "30111222", LocalDate.of(1985, 3, 12),
        Genero.FEMENINO, "Av. Rivadavia", 1001,
        "testdonante@donatrack.com", "1100000001", TipoDeMedio.MAIL);

    Donante d02 = humana("Bruno", "Díaz", "32444555", LocalDate.of(1990, 7, 25),
        Genero.MASCULINO, "Av. Corrientes", 2450,
        "bruno.diaz@test.org", "1100000002", TipoDeMedio.WPP);

    Donante d03 = humana("Carla", "Fernández", "33888999", LocalDate.of(1992, 11, 3),
        Genero.FEMENINO, "Mitre", 780,
        "carla.fernandez@test.org", "1100000003", TipoDeMedio.SMS);

    Donante d04 = humana("Diego", "Sosa", "28999000", LocalDate.of(1978, 1, 19),
        Genero.MASCULINO, "Belgrano", 1450,
        "diego.sosa@test.org", "1100000004", TipoDeMedio.MAIL);

    Donante d05 = humana("Elena", "Ruiz", "35666777", LocalDate.of(1995, 5, 30),
        Genero.FEMENINO, "San Martín", 620,
        "elena.ruiz@test.org", "1100000005", TipoDeMedio.WPP);

    Donante d06 = humana("Facundo", "López", "31222333", LocalDate.of(1988, 9, 14),
        Genero.MASCULINO, "Sarmiento", 990,
        "facundo.lopez@test.org", "1100000006", TipoDeMedio.MAIL);

    Donante d07 = humana("Gabriela", "Torres", "27000111", LocalDate.of(1975, 12, 8),
        Genero.FEMENINO, "Alsina", 330,
        "gabriela.torres@test.org", "1100000007", TipoDeMedio.SMS);

    Donante d08 = juridica("Supermercado El Sol S.A.", "30-71234567-8",
        TipoJuridica.EMPRESA, "ALIMENTOS",
        "donaciones@elsol.test", "1140000008", TipoDeMedio.MAIL);

    Donante d09 = juridica("Fundación Manos Unidas", "30-70987654-3",
        TipoJuridica.ONG, "ASISTENCIA SOCIAL",
        "contacto@manosunidas.test", "1140000009", TipoDeMedio.MAIL);

    Donante d10 = juridica("Textil del Sur S.R.L.", "30-70111222-4",
        TipoJuridica.EMPRESA, "INDUMENTARIA",
        "donaciones@textildelsur.test", "1140000010", TipoDeMedio.WPP);

    return List.of(d01, d02, d03, d04, d05, d06, d07, d08, d09, d10);
  }

  private Donante humana(String nombre, String apellido, String documento, LocalDate nacimiento,
                         Genero genero, String calle, int altura,
                         String mail, String telefono, TipoDeMedio medio) {
    return new DonanteHumanoBuilder()
        .conNombreCompleto(new NombreCompleto(nombre, apellido))
        .conDocumento(documento)
        .conFechaDeNacimiento(nacimiento)
        .conGenero(genero)
        .conCalle(calle(calle, altura))
        .conContacto(contacto(mail, telefono, medio))
        .build();
  }

  private Donante juridica(String razonSocial, String cuit, TipoJuridica tipo, String rubro,
                           String mail, String telefono, TipoDeMedio medio) {
    return new DonanteJuridicoBuilder()
        .conRazonSocial(razonSocial)
        .conCuit(cuit)
        .conTipoJuridico(tipo)
        .conRubro(rubro)
        .conContacto(contacto(mail, telefono, medio))
        .build();
  }

  private Direccion direccion(String calle, int altura, double lat, double lon) {
    return new DireccionBuilder()
        .conCalle(new CalleBuilder().conNombre(calle).conAltura(altura).build())
        .conPosicion(new PosicionBuilder().conLatitud(lat).conLongitud(lon).build())
        .build();
  }

  private DatosDeContacto contacto(String mail, String telefono, TipoDeMedio medio) {
    return new DatosDeContactoBuilder()
        .conMail(mail)
        .conTelefono(telefono)
        .conMedioPreferido(medio)
        .build();
  }

  private List<Notificacion> crearNotificaciones(Donante donante, EntidadBeneficiaria entidad) {
    DatosDeContacto contactoDonante = donante.getDatosDeContacto();
    DatosDeContacto contactoEntidad = entidad.getDatosDeContacto();

    List<Notificacion> notifs = new ArrayList<>();

    // ── D01 (Ana Gómez) ─────────────────────────────────────────
    Notificacion d1 = FabricaDeNotificaciones.notificacionViaMail(
        "¡Bienvenida a DonaTrack, Ana! Tu cuenta fue creada con éxito.", contactoDonante);
    d1.pasarAEntregada();
    notifs.add(d1);

    Notificacion d2 = FabricaDeNotificaciones.notificacionViaMail(
        "Tu donación de 2 mesas fue recibida por Comedor Los Pibes. ¡Gracias!", contactoDonante);
    d2.pasarAEntregada();
    notifs.add(d2);

    Notificacion d3 = FabricaDeNotificaciones.notificacionViaMail(
        "Tu donación de camperas está en camino a Comedor Los Pibes.", contactoDonante);
    d3.pasarAEntregada();
    notifs.add(d3);

    Notificacion d4 = FabricaDeNotificaciones.notificacionViaMail(
        "Tu donación de arroz fue asignada a Comedor Los Pibes.", contactoDonante);
    notifs.add(d4);

    Notificacion d5 = FabricaDeNotificaciones.notificacionViaMail(
        "Tu donación de 1 silla no pudo ser recibida por Comedor Los Pibes.", contactoDonante);
    notifs.add(d5);

    // ── E01 (Comedor Los Pibes) ─────────────────────────────────
    Notificacion e1 = FabricaDeNotificaciones.notificacionViaMail(
        "Se te asignó una nueva donación de alimentos.", contactoEntidad);
    e1.pasarAEntregada();
    notifs.add(e1);

    Notificacion e2 = FabricaDeNotificaciones.notificacionViaMail(
        "Recibiste la donación de mesas. Confirmá la recepción en el sistema.", contactoEntidad);
    e2.pasarAEntregada();
    notifs.add(e2);

    Notificacion e3 = FabricaDeNotificaciones.notificacionViaMail(
        "Una donación de indumentaria está en camino hacia tu entidad.", contactoEntidad);
    e3.pasarAEntregada();
    notifs.add(e3);

    Notificacion e4 = FabricaDeNotificaciones.notificacionViaMail(
        "Se registró una nueva necesidad de remeras en tu entidad.", contactoEntidad);
    notifs.add(e4);

    Notificacion e5 = FabricaDeNotificaciones.notificacionViaMail(
        "Se registró una nueva necesidad de sillas en tu entidad.", contactoEntidad);
    notifs.add(e5);

    return notifs;
  }

  private List<DonacionIndependiente> crearDonaciones(
      List<Donante> donantes, List<EntidadBeneficiaria> entidades,
      SubCategoria remera, SubCategoria pantalon, SubCategoria campera,
      SubCategoria arroz, SubCategoria fideosSecos, SubCategoria tomate,
      SubCategoria mesa, SubCategoria silla) {

    var specs = List.of(
        // ───────────────── D01 (Ana Gómez) — 30 donaciones, ENT→E01 ─────────────────
        new Spec(0, "REMERA_NUEVA",    5,  DEP,   -1, null,        null),
        new Spec(0, "REMERA_NUEVA",    3,  PEND,  -1, null,        null),
        new Spec(0, "PANTALON_NUEVO",  2,  ASIG,   0, null,        null),
        new Spec(0, "ARROZ_VIGENTE",  10,  LISTA,  0, null,        null),
        new Spec(0, "CAMPERA_NUEVA",   4,  TRAS,   0, "ENT-001",   null),
        new Spec(0, "MESA_NUEVA",      2,  ENT,    0, "AAA-111",   null),
        new Spec(0, "SILLA_NUEVA",     6,  ENT,    0, "AAA-112",   null),
        new Spec(0, "FIDEOS_VIGENTE",  8,  FALL,   0, "ENT-003",   "Dirección incorrecta"),
        new Spec(0, "TOMATE_VENCIDO",  5,  VENC,  -1, null,        null),
        new Spec(0, "PANTALON_USADO",  3,  DEP,   -1, null,        null),
        new Spec(0, "REMERA_NUEVA",   15,  ASIG,   0, null,        null),
        new Spec(0, "ARROZ_VIGENTE",  20,  LISTA,  0, null,        null),
        new Spec(0, "PANTALON_NUEVO", 12,  TRAS,   0, "ENT-101",   null),
        new Spec(0, "CAMPERA_USADA",   5,  ENT,    0, "AAA-113",   null),
        new Spec(0, "FIDEOS_VIGENTE", 18,  ENT,    0, "AAA-114",   null),
        new Spec(0, "SILLA_USADA",     4,  FALL,   0, "ENT-102",   "Sin transporte disponible"),
        new Spec(0, "MESA_NUEVA",      3,  DEP,   -1, null,        null),
        new Spec(0, "REMERA_USADA",    7,  PEND,  -1, null,        null),
        new Spec(0, "TOMATE_VIGENTE",  6,  ASIG,   0, null,        null),
        new Spec(0, "ARROZ_VIGENTE",  15,  DEP,   -1, null,        null),
        new Spec(0, "CAMPERA_NUEVA",   8,  LISTA,  0, null,        null),
        new Spec(0, "PANTALON_USADO", 10,  TRAS,   0, "ENT-103",   null),
        new Spec(0, "SILLA_NUEVA",     9,  ENT,    0, "AAA-115",   null),
        new Spec(0, "FIDEOS_VENCIDO",  5,  VENC,  -1, null,        null),
        new Spec(0, "REMERA_NUEVA",   20,  ASIG,   0, null,        null),
        new Spec(0, "ARROZ_VIGENTE",  25,  ENT,    0, "AAA-116",   null),
        new Spec(0, "PANTALON_NUEVO",  6,  LISTA,  0, null,        null),
        new Spec(0, "MESA_NUEVA",      4,  TRAS,   0, "ENT-104",   null),
        new Spec(0, "CAMPERA_USADA",   3,  FALL,   0, "ENT-105",   "Rechazada por la entidad"),
        new Spec(0, "TOMATE_VIGENTE", 12,  DEP,   -1, null,        null),

        // ───────────────── D02 (Bruno Díaz) — 8 donaciones ─────────────────
        new Spec(1, "ARROZ_VIGENTE",  20,  DEP,   -1, null,        null),
        new Spec(1, "PANTALON_NUEVO",  4,  PEND,  -1, null,        null),
        new Spec(1, "REMERA_NUEVA",   10,  ASIG,   1, null,        null),
        new Spec(1, "FIDEOS_VIGENTE", 12,  LISTA,  1, null,        null),
        new Spec(1, "SILLA_USADA",     5,  TRAS,   1, "ENT-002",   null),
        new Spec(1, "ARROZ_VIGENTE",  30,  ENT,    0, "BBB-221",   null), // E01 tiene arroz
        new Spec(1, "CAMPERA_USADA",   3,  ENT,    0, "BBB-222",   null), // E01 tiene campera
        new Spec(1, "MESA_NUEVA",      1,  FALL,   0, "ENT-004",   "Entidad cerrada"),

        // ───────────────── D03 (Carla Fernández) — 8 donaciones ─────────────────
        new Spec(2, "CAMPERA_NUEVA",   6,  DEP,   -1, null,        null),
        new Spec(2, "SILLA_NUEVA",     4,  PEND,  -1, null,        null),
        new Spec(2, "ARROZ_VIGENTE",  25,  ASIG,   0, null,        null),
        new Spec(2, "REMERA_NUEVA",   15,  LISTA,  1, null,        null),
        new Spec(2, "PANTALON_USADO",  7,  TRAS,   1, "ENT-003",   null),
        new Spec(2, "FIDEOS_VIGENTE", 20,  ENT,    0, "CCC-331",   null), // E01 tiene fideos
        new Spec(2, "TOMATE_VIGENTE", 10,  ENT,    0, "CCC-332",   null), // E01 tiene tomate
        new Spec(2, "SILLA_USADA",     2,  FALL,   0, "ENT-005",   "Sin transporte disponible"),

        // ───────────────── D04 (Diego Sosa) — 8 donaciones ─────────────────
        new Spec(3, "REMERA_NUEVA",   12,  DEP,   -1, null,        null),
        new Spec(3, "CAMPERA_USADA",   5,  PEND,  -1, null,        null),
        new Spec(3, "PANTALON_NUEVO",  9,  ASIG,   0, null,        null),
        new Spec(3, "ARROZ_VIGENTE",  40,  LISTA,  0, null,        null),
        new Spec(3, "MESA_NUEVA",      4,  TRAS,   1, "ENT-004",   null),
        new Spec(3, "SILLA_NUEVA",    10,  ENT,    0, "DDD-441",   null), // FIX: E01 tiene silla
        new Spec(3, "REMERA_NUEVA",   25,  ENT,    2, "DDD-442",   null), // E03 tiene remera
        new Spec(3, "PANTALON_USADO",  6,  FALL,   0, "ENT-006",   "No había responsable"),

        // ───────────────── D05 (Elena Ruiz) — 8 donaciones ─────────────────
        new Spec(4, "PANTALON_USADO", 10,  DEP,   -1, null,        null),
        new Spec(4, "MESA_NUEVA",      2,  PEND,  -1, null,        null),
        new Spec(4, "CAMPERA_NUEVA",   8,  ASIG,   2, null,        null),
        new Spec(4, "SILLA_USADA",     6,  LISTA,  2, null,        null),
        new Spec(4, "ARROZ_VIGENTE",  18,  TRAS,   0, "ENT-005",   null),
        new Spec(4, "PANTALON_NUEVO", 12,  ENT,    0, "EEE-551",   null), // FIX: E01 tiene pantalon
        new Spec(4, "CAMPERA_USADA",   4,  ENT,    0, "EEE-552",   null), // FIX: E01 tiene campera
        new Spec(4, "REMERA_NUEVA",   20,  FALL,   0, "ENT-007",   "Donación incompleta"),

        // ───────────────── D06 (Facundo López) — 8 donaciones ─────────────────
        new Spec(5, "CAMPERA_USADA",   7,  DEP,   -1, null,        null),
        new Spec(5, "ARROZ_VIGENTE",  22,  PEND,  -1, null,        null),
        new Spec(5, "FIDEOS_VIGENTE", 30,  ASIG,   2, null,        null),
        new Spec(5, "TOMATE_VIGENTE", 12,  LISTA,  3, null,        null),
        new Spec(5, "REMERA_NUEVA",   18,  TRAS,   2, "ENT-006",   null),
        new Spec(5, "MESA_NUEVA",      3,  ENT,    1, "FFF-661",   null), // E02 tiene mesa NUEVO
        new Spec(5, "SILLA_USADA",     8,  ENT,    0, "FFF-662",   null), // FIX: E01 tiene silla
        new Spec(5, "CAMPERA_NUEVA",   5,  FALL,   3, "ENT-008",   "Rechazada por la entidad"),

        // ───────────────── D07 (Gabriela Torres) — 8 donaciones ─────────────────
        new Spec(6, "SILLA_USADA",     9,  DEP,   -1, null,        null),
        new Spec(6, "REMERA_USADA",   10,  PEND,  -1, null,        null),
        new Spec(6, "ARROZ_VIGENTE",  35,  ASIG,   1, null,        null),
        new Spec(6, "PANTALON_NUEVO", 14,  LISTA,  1, null,        null),
        new Spec(6, "CAMPERA_USADA",   6,  TRAS,   2, "ENT-007",   null),
        new Spec(6, "FIDEOS_VIGENTE", 25,  ENT,    0, "GGG-771",   null), // FIX: E01 tiene fideos
        new Spec(6, "TOMATE_VIGENTE", 15,  ENT,    3, "GGG-772",   null), // E04 tiene tomate
        new Spec(6, "SILLA_NUEVA",     4,  FALL,   1, "ENT-009",   "Camión averiado"),

        // ───────────────── D08 (Supermercado El Sol) — 7 donaciones ─────────────────
        new Spec(7, "ARROZ_VIGENTE",  50,  DEP,   -1, null,        null),
        new Spec(7, "FIDEOS_VIGENTE", 40,  PEND,  -1, null,        null),
        new Spec(7, "TOMATE_VIGENTE", 20,  ASIG,   3, null,        null),
        new Spec(7, "ARROZ_VIGENTE",  60,  LISTA,  1, null,        null),
        new Spec(7, "SILLA_NUEVA",    12,  TRAS,   1, "ENT-008",   null),
        new Spec(7, "ARROZ_VIGENTE",  80,  ENT,    3, "HHH-881",   null), // FIX: E04 tiene arroz
        new Spec(7, "FIDEOS_VIGENTE", 50,  ENT,    0, "HHH-882",   null), // FIX: E01 tiene fideos

        // ───────────────── D09 (Fundación Manos Unidas) — 8 donaciones ─────────────────
        new Spec(8, "REMERA_NUEVA",   30,  DEP,   -1, null,        null),
        new Spec(8, "PANTALON_USADO", 15,  PEND,  -1, null,        null),
        new Spec(8, "CAMPERA_NUEVA",  10,  ASIG,   3, null,        null),
        new Spec(8, "REMERA_USADA",   20,  LISTA,  2, null,        null),
        new Spec(8, "PANTALON_NUEVO", 16,  TRAS,   1, "ENT-009",   null),
        new Spec(8, "MESA_NUEVA",      5,  ENT,    0, "III-991",   null), // FIX: E01 tiene mesa
        new Spec(8, "SILLA_USADA",    10,  ENT,    0, "III-992",   null), // FIX: E01 tiene silla
        new Spec(8, "ARROZ_VIGENTE",  45,  FALL,   2, "ENT-011",   "Faltante de documentación"),

        // ───────────────── D10 (Textil del Sur) — 7 donaciones ─────────────────
        new Spec(9, "PANTALON_NUEVO", 25,  DEP,   -1, null,        null),
        new Spec(9, "SILLA_NUEVA",     8,  PEND,  -1, null,        null),
        new Spec(9, "REMERA_NUEVA",   40,  ASIG,   2, null,        null),
        new Spec(9, "CAMPERA_USADA",   9,  LISTA,  1, null,        null),
        new Spec(9, "FIDEOS_VIGENTE", 55,  TRAS,   2, "ENT-010",   null),
        new Spec(9, "PANTALON_USADO", 18,  ENT,    0, "JJJ-101",   null), // FIX: E01 tiene pantalon
        new Spec(9, "ARROZ_VIGENTE", 100,  ENT,    0, "JJJ-102",   null)  // E01 tiene arroz
    );

    return specs.stream()
        .map(spec -> construirDonacion(spec, donantes, entidades,
            remera, pantalon, campera, arroz, fideosSecos, tomate, mesa, silla))
        .toList();
  }

  private DonacionIndependiente construirDonacion(
      Spec spec, List<Donante> donantes, List<EntidadBeneficiaria> entidades,
      SubCategoria remera, SubCategoria pantalon, SubCategoria campera,
      SubCategoria arroz, SubCategoria fideosSecos, SubCategoria tomate,
      SubCategoria mesa, SubCategoria silla) {

    Donante donante = donantes.get(spec.donante());
    Bien bien = crearBien(spec.bien(), remera, pantalon, campera,
        arroz, fideosSecos, tomate, mesa, silla);

    DonacionIndependiente donacion = FabricaDeDonacionesIndependientes.unaDonacionSinObservers(
        bien, spec.cantidad(), donante, EstadoDonacion.EN_DEPOSITO);

    EntidadBeneficiaria entidad = spec.entidad() >= 0 ? entidades.get(spec.entidad()) : null;
    return aplicarEstado(donacion, spec.estado(), entidad, spec.patente(), spec.justificacion());
  }

  private DonacionIndependiente aplicarEstado(DonacionIndependiente d, EstadoDonacion estado,
                                              EntidadBeneficiaria entidad,
                                              String idEntrega, String justificacion) {
    switch (estado) {
      case EN_DEPOSITO -> { }
      case PENDIENTE_DE_CONFIRMACION_DE_ASIGNACION -> d.enSolicitudDeAsignacion();
      case ASIGNACION_REALIZADA -> {
        d.enSolicitudDeAsignacion();
        d.asignarA(entidad);
      }
      case LISTA_PARA_ENTREGAR -> {
        d.enSolicitudDeAsignacion();
        d.asignarA(entidad);
        d.alistarParaEntregar();
      }
      case EN_TRASLADO -> {
        d.enSolicitudDeAsignacion();
        d.asignarA(entidad);
        d.alistarParaEntregar();
        d.iniciarTraslado(idEntrega);
      }
      case ENTREGADA -> {
        d.enSolicitudDeAsignacion();
        d.asignarA(entidad);
        d.alistarParaEntregar();
        d.iniciarTraslado(idEntrega);
        d.marcarComoEntregada(new ComprobanteDeEntrega(LocalDateTime.now(), idEntrega));
      }
      case ENTREGA_FALLIDA -> {
        d.enSolicitudDeAsignacion();
        d.asignarA(entidad);
        d.alistarParaEntregar();
        d.iniciarTraslado(idEntrega);
        d.marcarComoFallida(justificacion);
      }
      case VENCIDA -> d.vencer();
      default -> throw new IllegalArgumentException("Estado no soportado en carga: " + estado);
    }
    return d;
  }

  private Bien crearBien(String tipo,
                         SubCategoria remera, SubCategoria pantalon, SubCategoria campera,
                         SubCategoria arroz, SubCategoria fideosSecos, SubCategoria tomate,
                         SubCategoria mesa, SubCategoria silla) {
    return switch (tipo) {
      case "REMERA_NUEVA"   -> FabricaDeBienes.unNoPerecedero(remera,   BigDecimal.ONE, EstadoDeUso.NUEVO);
      case "REMERA_USADA"   -> FabricaDeBienes.unNoPerecedero(remera,   BigDecimal.ONE, EstadoDeUso.USADO);
      case "PANTALON_NUEVO" -> FabricaDeBienes.unNoPerecedero(pantalon, BigDecimal.ONE, EstadoDeUso.NUEVO);
      case "PANTALON_USADO" -> FabricaDeBienes.unNoPerecedero(pantalon, BigDecimal.ONE, EstadoDeUso.USADO);
      case "CAMPERA_NUEVA"  -> FabricaDeBienes.unNoPerecedero(campera,  BigDecimal.ONE, EstadoDeUso.NUEVO);
      case "CAMPERA_USADA"  -> FabricaDeBienes.unNoPerecedero(campera,  BigDecimal.ONE, EstadoDeUso.USADO);
      case "MESA_NUEVA"     -> FabricaDeBienes.unNoPerecedero(mesa,     BigDecimal.ONE, EstadoDeUso.NUEVO);
      case "SILLA_NUEVA"    -> FabricaDeBienes.unNoPerecedero(silla,    BigDecimal.ONE, EstadoDeUso.NUEVO);
      case "SILLA_USADA"    -> FabricaDeBienes.unNoPerecedero(silla,    BigDecimal.ONE, EstadoDeUso.USADO);
      case "ARROZ_VIGENTE"  -> FabricaDeBienes.unBienPerecedero(arroz,       BigDecimal.valueOf(0.5), VENC_FUTURO);
      case "ARROZ_VENCIDO"  -> FabricaDeBienes.unBienPerecedero(arroz,       BigDecimal.valueOf(0.5), VENC_PASADO);
      case "FIDEOS_VIGENTE" -> FabricaDeBienes.unBienPerecedero(fideosSecos, BigDecimal.ONE,           VENC_FUTURO);
      case "FIDEOS_VENCIDO" -> FabricaDeBienes.unBienPerecedero(fideosSecos, BigDecimal.ONE,           VENC_PASADO);
      case "TOMATE_VIGENTE" -> FabricaDeBienes.unBienPerecedero(tomate,      BigDecimal.ONE,           VENC_TOMATE);
      case "TOMATE_VENCIDO" -> FabricaDeBienes.unBienPerecedero(tomate,      BigDecimal.ONE,           VENC_PASADO);
      default -> throw new IllegalArgumentException("Bien desconocido: " + tipo);
    };
  }
  private Calle calle(String nombre, int altura) {
    return new CalleBuilder().conNombre(nombre).conAltura(altura).build();
  }

  private record Spec(
      int donante,
      String bien,
      int cantidad,
      EstadoDonacion estado,
      int entidad,
      String patente,
      String justificacion
  ) {}
}
