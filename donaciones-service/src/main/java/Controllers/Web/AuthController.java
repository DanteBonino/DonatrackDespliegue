package Controllers.Web;

import Contacto.DatosDeContactoBuilder;
import Direccion.Calle;
import Direccion.Direccion;
import Direccion.builders.CalleBuilder;
import Donantes.Donante;
import Donantes.DonanteHumana.DonanteHumanoBuilder;
import Donantes.DonanteHumana.Genero;
import Donantes.DonanteHumana.NombreCompleto;
import Donantes.DonanteJuridico.DonanteJuridicoBuilder;
import Donantes.DonanteJuridico.TipoJuridica;
import EntidadesBeneficiarias.EntidadBeneficiaria;
import EntidadesBeneficiarias.EntidadBeneficiariaBuilder;
import Repositories.Beneficiarios.BeneficiariosRepository;
import Repositories.DatosDeContacto.DatosDeContactoRepository;
import Repositories.Donantes.DonantesRepository;
import Repositories.UsuariosRepository;
import Servidor.ManejadorDeSesiones.DatosSesion;
import Servidor.ManejadorDeSesiones.ManejadorDeSesion;
import ServiceLocator.ServiceLocator;
import UsuarioFinal.Usuario;

import UsuarioFinal.UsuarioBeneficiario;
import UsuarioFinal.UsuarioDonante;
import io.javalin.http.Context;
import io.javalin.validation.ValidationError;
import io.javalin.validation.Validator;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import utils.FechasUtils;
import io.javalin.validation.Validation;


public class AuthController {

  // ===== LOGIN =====

  public void showLogin(Context ctx) {
    String error = getMensajeDeSesionYLimpiar(ctx, "errorLogin");
    Map<String, Object> modelo = new HashMap<>();
    if(!error.isEmpty()){ modelo.put("error", error);}
    modelo.put("titulo", "Iniciar Sesión");
    if ("true".equals(ctx.queryParam("registrado"))) {
      modelo.put("registrado", "Registro exitoso. Ya podés iniciar sesión.");
    }
    ctx.render("login.hbs", modelo);
  }

  public void doLogin(Context ctx) {
    String username = ctx.formParam("username");
    String password = ctx.formParam("password");

    Optional<Usuario> potencialUsuario = UsuariosRepository.getInstance()
        .getByUsename(username)
        .filter(potencialUsuarioEncontrado -> potencialUsuarioEncontrado.verificarContrasenia(password));

    if (potencialUsuario.isEmpty()) {
      ctx.sessionAttribute("errorLogin", "Credenciales inválidas");
      ctx.status(401).redirect("/login");
      return;
    }
    Usuario usuario = potencialUsuario.get();
    usuario.registrarIngreso();
    DatosSesion datosSesion = new DatosSesion(
        usuario.getRol(),
        usuario.getIdDominio(),
        usuario.getIdDeAsociado(),
        usuario.getNombreAMostrar()
    );
    ManejadorDeSesion.iniciarSesion(ctx, datosSesion);
    ctx.redirect("/dashboard");
  }

  public void doLogout(Context ctx) {
    ManejadorDeSesion.cerrarSesion(ctx);
    ctx.redirect("/");
  }

  // ===== REGISTRO =====

  public void showRegistro(Context ctx) {

    ctx.render("registro.hbs",
        Map.of(
            "titulo", "Registro"
        )

    );
  }

  public void showRegistroHumano(Context ctx) {
    Map<String, String> errores = getErrores(ctx);
    Map<String, String> valores = getValores(ctx);
    ctx.render("registroDonanteHumano.hbs",
        Map.of(
            "titulo", "Registro Donante Humano",
            "errores", errores,
            "valores", valores
        ));
  }

  public void doRegistroHumano(Context ctx) {
    Validator<String> usernameValidator = ctx.formParamAsClass("username", String.class)
        .required()
        .check(username -> username.length() >= 4, "El usuario debe tener mínimo 4 caracteres")
        .check(username -> usuariosRepository().estaElUsernameDisponible(username), "El username no está disponible");
    Validator<String> passwordValidator = ctx.formParamAsClass("password", String.class)
        .required()
        .check(password -> password.length() >= 8, "La contraseña debe tener mínimo 8 caracteres");
    Validator<String> nombreValidator = ctx.formParamAsClass("nombre", String.class)
        .required()
        .check(nombre -> !nombre.isEmpty(), "El nombre no puede ser vacío");
    Validator<String> apellidoValidator = ctx.formParamAsClass("apellido", String.class)
        .required()
        .check(apellido -> !apellido.isEmpty(), "El apellido no puede ser vacío");
    Validator<LocalDate> fechaNacimientoValidator = ctx.formParamAsClass("fechaNacimiento", LocalDate.class)
        .required()
        .check(nacimiento -> FechasUtils.esAnteriorA(nacimiento, FechasUtils.haceQuinceAnios()), "Debes tener, al menos, 15 años");
    Validator<String> documentoValidator = ctx.formParamAsClass("documento", String.class)
        .required()
        .check(documento -> documento.length() >= 7, "El documento debe tener al menos 7 dígitos")
        .check(documento -> !DonantesRepository.getInstance().elDocumentoYaEstaEnUso(documento), "El documento ya está en uso");
    Validator<String> emailValidator = ctx.formParamAsClass("email", String.class)
        .required()
        .check(email -> email.length() >= 3, "El mail debe tener al menos 3 caracteres")
        .check(email -> email.contains("@"), "El mail no tiene @")
        .check(email -> !DatosDeContactoRepository.getInstance().elMailYaEstaEnUso(email), "El mail ya está en uso");
    Validator<String> telefonoValidator = ctx.formParamAsClass("telefono", String.class)
        .required()
        .check(telefono -> telefono.length() >= 10, "El telefono debe tener al menos 10 caracteres")
        .check(telefono -> !DatosDeContactoRepository.getInstance().elTelefonoYaEstaEnUso(telefono), "El telefono ya está en uso");
    Validator<String> nombreCalleValidator = ctx.formParamAsClass("calleNombre", String.class)
        .required();
    Validator<Integer> alturaCalleValidator = ctx.formParamAsClass("calleAltura", Integer.class)
        .required()
        .check(altura -> altura > 0, "La altura no puede ser negativa");

    Genero genero = ctx.formParamAsClass("genero", Genero.class).getOrDefault(Genero.X);
    Map<String, List<ValidationError<Object>>> errors = Validation.collectErrors(
        usernameValidator,
        passwordValidator,
        nombreValidator,
        apellidoValidator,
        fechaNacimientoValidator,
        documentoValidator,
        emailValidator,
        telefonoValidator,
        nombreCalleValidator,
        alturaCalleValidator
    );
    Map<String, String> erroresConDetalle = obtenerErroresConDetalle(errors);
    Map<String, Object> valores = Map.of(
        "username", getValue(ctx, "username"),
        "nombre", getValue(ctx, "nombre"),
        "apellido", getValue(ctx, "apellido"),
        "documento", getValue(ctx,"documento"),
        "email", getValue(ctx, "email"),
        "telefono", getValue(ctx, "telefono"),
        "calleNombre", getValue(ctx, "calleNombre"),
        "calleAltura", getValue(ctx, "calleAltura"),
        "genero", genero
    );
    if(!errors.isEmpty()) {
      volver(ctx,
          "/registro/donante-humano",
          erroresConDetalle,
          valores
      );
      return;
    }

    Calle calle = new CalleBuilder()
        .conNombre(nombreCalleValidator.get())
        .conAltura(alturaCalleValidator.get())
        .build();

    Donante donante = new DonanteHumanoBuilder()
        .conNombreCompleto(new NombreCompleto(nombreValidator.get(), apellidoValidator.get()))
        .conDocumento(documentoValidator.get())
        .conCalle(calle)
        .conGenero(genero)
        .conFechaDeNacimiento(fechaNacimientoValidator.get())
        .conContacto(new DatosDeContactoBuilder()
            .conMail(emailValidator.get())
            .conTelefono(ctx.formParam("telefono"))
            .build())
        .build();

    DonantesRepository.getInstance().registrar(donante);
    usuariosRepository().registrar(new UsuarioDonante(usernameValidator.get(), passwordValidator.get(), donante));
    ctx.status(201).redirect("/login?registrado=true");
  }

  private void volver(Context ctx, String pagina, Map<String,String> errores, Map<String, Object> valores) {
    ctx.sessionAttribute("erroresRegistro", errores);
    ctx.sessionAttribute("valoresRegistro", valores);
    ctx.status(400).redirect(pagina);
  }

  private Object getValue(Context ctx, String inputLabel) {
    return ctx.formParam(inputLabel) != null ? ctx.formParam(inputLabel) : "";
  }

  private Map<String, String> obtenerErroresConDetalle( Map<String, List<ValidationError<Object>>> errors){
    return errors.entrySet().stream()
        .filter(entry -> !entry.getValue().isEmpty())
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> {
              var validationError = entry.getValue().get(0);
              return validationError.getMessage();
            })
        );
  }

  public void doRegistroJuridico(Context ctx) {
    Validator<String> usernameValidator = ctx.formParamAsClass("username", String.class)
        .required()
        .check(username -> username.length() >= 4, "El usuario debe tener mínimo 4 caracteres")
        .check(username -> usuariosRepository().estaElUsernameDisponible(username), "El username no está disponible");
    Validator<String> passwordValidator = ctx.formParamAsClass("password", String.class)
        .required()
        .check(password -> password.length() >= 8, "La contraseña debe tener mínimo 8 caracteres");
    Validator<String> razonSocialValidator = ctx.formParamAsClass("razonSocial", String.class)
        .required()
        .check(razonSocial -> !razonSocial.isEmpty(), "La razon social no puede ser vacía");
    Validator<String> rubroValidator = ctx.formParamAsClass("rubro", String.class)
        .required()
        .check(rubro -> !rubro.isEmpty(), "El rubro no puede ser vacío");
    Validator<String> cuitValidator = ctx.formParamAsClass("cuit", String.class)
        .required()
        .check(cuit -> cuit.length() == 11, "El cuit debe tener 11 dígitos")
        .check(cuit -> !DonantesRepository.getInstance().elCuitYaEstaEnUso(cuit), "El cuit ya está en uso");
    Validator<String> emailValidator = ctx.formParamAsClass("email", String.class)
        .required()
        .check(email -> email.length() >= 3, "El mail debe tener al menos 3 caracteres")
        .check(email -> email.contains("@"), "El mail no tiene @")
        .check(email -> !DatosDeContactoRepository.getInstance().elMailYaEstaEnUso(email), "El mail ya está en uso");
    Validator<String> telefonoValidator = ctx.formParamAsClass("telefono", String.class)
        .required()
        .check(telefono -> telefono.length() >= 10, "El telefono debe tener al menos 10 caracteres")
        .check(telefono -> !DatosDeContactoRepository.getInstance().elTelefonoYaEstaEnUso(telefono), "El telefono ya está en uso");
    Validator<TipoJuridica> tipoJuridicaValidator = ctx.formParamAsClass("tipoJuridico", TipoJuridica.class)
        .required();

    Map<String, List<ValidationError<Object>>> errors = Validation.collectErrors(
        usernameValidator,
        passwordValidator,
        razonSocialValidator,
        cuitValidator,
        rubroValidator,
        emailValidator,
        telefonoValidator,
        tipoJuridicaValidator
    );
    Map<String, String> erroresConDetalle = obtenerErroresConDetalle(errors);
    Map<String, Object> valores = Map.of(
        "username", getValue(ctx, "username"),
        "razonSocial", getValue(ctx, "razonSocial"),
        "cuit", getValue(ctx, "cuit"),
        "rubro", getValue(ctx,"rubro"),
        "email", getValue(ctx, "email"),
        "telefono", getValue(ctx, "telefono"),
        "tipoJuridico", getValue(ctx, "tipoJuridico")
    );

    if(!errors.isEmpty()) {
      volver(ctx, "/registro/donante-juridico" ,erroresConDetalle, valores);
      return;
    }
    Donante donante = new DonanteJuridicoBuilder()
        .conRubro(rubroValidator.get())
        .conCuit(cuitValidator.get())
        .conRazonSocial(razonSocialValidator.get())
        .conTipoJuridico(tipoJuridicaValidator.get())
        .conContacto(new DatosDeContactoBuilder()
            .conMail(emailValidator.get())
            .conTelefono(ctx.formParam("telefono"))
            .build())
        .build();

    DonantesRepository.getInstance().registrar(donante);
    usuariosRepository().registrar(new UsuarioDonante(usernameValidator.get(), passwordValidator.get(), donante));
    ctx.status(201).redirect("/login?registrado=true");
  }


  public void showRegistroJuridico(Context ctx) {
    Map<String, String> errores = getErrores(ctx);
    Map<String, String> valores = getValores(ctx);
    ctx.render("registroDonanteJuridico.hbs",
        Map.of(
            "titulo", "Registro Donante Juridico",
            "tipos", TipoJuridica.values(),
            "errores", errores,
            "valores", valores
        ));
  }

  public void showRegristroBeneficiaria(Context ctx) {
    Map<String, String> errores = getErrores(ctx);
    Map<String, String> valores = getValores(ctx);
    ctx.render("registroEntidadBeneficiaria.hbs",
        Map.of(
            "titulo", "Registro Entidad Beneficiaria",
            "errores", errores,
            "valores", valores
        ));
  }

  public void doRegistroBeneficiario(Context ctx) {
    Validator<String> usernameValidator = ctx.formParamAsClass("username", String.class)
        .required()
        .check(username -> username.length() >= 4, "El usuario debe tener mínimo 4 caracteres")
        .check(username -> usuariosRepository().estaElUsernameDisponible(username), "El username no está disponible");
    Validator<String> passwordValidator = ctx.formParamAsClass("password", String.class)
        .required()
        .check(password -> password.length() >= 8, "La contraseña debe tener mínimo 8 caracteres");
    Validator<String> razonSocialValidator = ctx.formParamAsClass("razonSocial", String.class)
        .required()
        .check(razonSocial -> !razonSocial.isEmpty(), "La razon social no puede ser vacía");
    Validator<String> emailValidator = ctx.formParamAsClass("email", String.class)
        .required()
        .check(email -> email.length() >= 3, "El mail debe tener al menos 3 caracteres")
        .check(email -> email.contains("@"), "El mail no tiene @")
        .check(email -> !DatosDeContactoRepository.getInstance().elMailYaEstaEnUso(email), "El mail ya está en uso");
    Validator<String> telefonoValidator = ctx.formParamAsClass("telefono", String.class)
        .required()
        .check(telefono -> telefono.length() >= 10, "El telefono debe tener al menos 10 caracteres")
        .check(telefono -> !DatosDeContactoRepository.getInstance().elTelefonoYaEstaEnUso(telefono), "El telefono ya está en uso");
    Validator<String> nombreCalleValidator = ctx.formParamAsClass("calleNombre", String.class)
        .required();
    Validator<Integer> alturaCalleValidator = ctx.formParamAsClass("calleAltura", Integer.class)
        .required()
        .check(altura -> altura > 0, "La altura no puede ser negativa");

    Genero genero = ctx.formParamAsClass("genero", Genero.class).getOrDefault(Genero.X);
    Map<String, List<ValidationError<Object>>> errors = Validation.collectErrors(
        usernameValidator,
        passwordValidator,
        razonSocialValidator,
        emailValidator,
        telefonoValidator,
        nombreCalleValidator,
        alturaCalleValidator
    );
    Map<String, String> erroresConDetalle = obtenerErroresConDetalle(errors);
    Map<String, Object> valores = Map.of(
        "username", getValue(ctx, "username"),
        "razonSocial", getValue(ctx, "razonSocial"),
        "email", getValue(ctx, "email"),
        "telefono", getValue(ctx, "telefono"),
        "calleNombre", getValue(ctx, "calleNombre"),
        "calleAltura", getValue(ctx, "calleAltura")
    );

    if(!errors.isEmpty()) {
      volver(ctx,
          "/registro/entidad-beneficiaria",
          erroresConDetalle,
          valores
      );
      return;
    }

    Calle calle = new CalleBuilder()
        .conNombre(nombreCalleValidator.get())
        .conAltura(alturaCalleValidator.get())
        .build();

    Direccion direccion;

    try {
      direccion = ServiceLocator.obtenerGeolocalizador().geoLocalizar(calle);
    }catch (Exception e) {
      volver(ctx, "/registro/entidad-beneficiaria", Map.of("direccion", "La dirección no se pudo encontrar, reviséla"), valores);
      return;
    }

    EntidadBeneficiaria entidad = new EntidadBeneficiariaBuilder()
        .conRazonSocial(razonSocialValidator.get())
        .conDireccion(direccion)
        .conContacto(new DatosDeContactoBuilder()
            .conMail(emailValidator.get())
            .conTelefono(telefonoValidator.get())
            .build())
        .conNecesidades(List.of())
        .build();

    BeneficiariosRepository.getInstance().registrar(entidad);
    usuariosRepository().registrar(new UsuarioBeneficiario(usernameValidator.get(), passwordValidator.get(), entidad));
    ctx.status(201).redirect("/login?registrado=true");
  }

  private UsuariosRepository usuariosRepository() {
    return UsuariosRepository.getInstance();
  }

  private Map<String, String> getErrores(Context ctx) {
    return getMapaDeSesionYLimpiar(ctx, "erroresRegistro");
  }

  private Map<String, String> getValores(Context ctx) {
    return getMapaDeSesionYLimpiar(ctx, "valoresRegistro");
  }

  private Map<String, String> getMapaDeSesionYLimpiar(Context ctx, String propiedad) {
    Map<String, String> valores = ctx.sessionAttribute(propiedad);
    ctx.sessionAttribute(propiedad, null);
    if (valores == null) valores = Map.of();
    return valores;
  }

  private String getMensajeDeSesionYLimpiar(Context ctx, String propiedad) {
    String mensaje = ctx.sessionAttribute(propiedad);
    ctx.sessionAttribute(propiedad, null);
    return mensaje != null ? mensaje : "";
  }
}