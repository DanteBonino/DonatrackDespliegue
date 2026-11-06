
import EstadoDonacion.EstadoDonacion;
import ExceptionHandler.TransicionInvalidaDeEstado;
import org.junit.jupiter.api.Test;
import static EstadoDonacion.EstadoDonacion.ASIGNACION_REALIZADA;
import static EstadoDonacion.EstadoDonacion.ENTREGADA;
import static EstadoDonacion.EstadoDonacion.ENTREGA_FALLIDA;
import static EstadoDonacion.EstadoDonacion.EN_DEPOSITO;
import static EstadoDonacion.EstadoDonacion.EN_TRASLADO;
import static EstadoDonacion.EstadoDonacion.LISTA_PARA_ENTREGAR;
import static EstadoDonacion.EstadoDonacion.VENCIDA;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EstadoDonacionTest {

  // ---------- EN DEPOSITO ----------

  @Test
  void enDepositoNoPuedeTransicionarAAsignada() {
    var estado = EN_DEPOSITO;
    EstadoDonacion AsignacionRealizada;
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
  }

  @Test
  void enDepositoPuedeTransicionarAVencida() {
    var estado = EN_DEPOSITO;
    assertDoesNotThrow(() -> estado.validarTransicion(VENCIDA));
  }

  @Test
  void enDepositoNoPuedeTransicionarACualquierOtroEstado() {
    var estado = EN_DEPOSITO;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_DEPOSITO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGA_FALLIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_TRASLADO));
  }

  // ---------- ASIGNADA (ASIGNACION REALIZADA) ----------

  @Test
  void asignadaPuedeTransicionarAEntregable() {
    var estado = ASIGNACION_REALIZADA;
    assertDoesNotThrow(() -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
  }

  @Test
  void asignadaNoPuedeTransicionarACualquierOtroEstado() {
    var estado = ASIGNACION_REALIZADA;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_DEPOSITO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGA_FALLIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(VENCIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_TRASLADO));
  }

  // ---------- ENTREGABLE (LISTA PARA ENTREGAR) ----------

  @Test
  void entregablePuedeTransicionarAEnTraslado() {
    var estado = LISTA_PARA_ENTREGAR;
    assertDoesNotThrow(() -> estado.validarTransicion(EN_TRASLADO));
  }

  @Test
  void entregableNoPuedeTransicionarACualquierOtroEstado() {
    var estado = LISTA_PARA_ENTREGAR;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_DEPOSITO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGA_FALLIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(VENCIDA));
  }

  // ---------- EN TRASLADO ----------

  @Test
  void enTrasladoPuedeTransicionarAEntregada() {
    var estado = EN_TRASLADO;
    assertDoesNotThrow(() -> estado.validarTransicion(ENTREGADA));
  }

  @Test
  void enTrasladoPuedeTransicionarAEntregaFallida() {
    var estado = EN_TRASLADO;
    assertDoesNotThrow(() -> estado.validarTransicion(ENTREGA_FALLIDA));
  }

  @Test
  void enTrasladoNoPuedeTransicionarACualquierOtroEstado() {
    var estado = EN_TRASLADO;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_DEPOSITO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_TRASLADO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(VENCIDA));
  }

  // ---------- ENTREGA FALLIDA ----------

  @Test
  void entregaFallidaPuedeTransicionarAEnDeposito() {
    var estado = ENTREGA_FALLIDA;
    assertDoesNotThrow(() -> estado.validarTransicion(EN_DEPOSITO));
  }

  @Test
  void entregaFallidaPuedeTransicionarAVencida() {
    var estado = ENTREGA_FALLIDA;
    assertDoesNotThrow(() -> estado.validarTransicion(VENCIDA));
  }

  @Test
  void entregaFallidaNoPuedeTransicionarACualquierOtroEstado() {
    var estado = ENTREGA_FALLIDA;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_TRASLADO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGA_FALLIDA));
  }

  // ---------- ESTADOS FINALES ----------

  @Test
  void entregadaNoPuedeTransicionarANingunEstado() {
    var estado = ENTREGADA;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_DEPOSITO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_TRASLADO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGA_FALLIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(VENCIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGADA));
  }

  @Test
  void vencidaNoPuedeTransicionarANingunEstado() {
    var estado = VENCIDA;

    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_DEPOSITO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ASIGNACION_REALIZADA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(LISTA_PARA_ENTREGAR));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(EN_TRASLADO));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGA_FALLIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(VENCIDA));
    assertThrows(TransicionInvalidaDeEstado.class, () -> estado.validarTransicion(ENTREGADA));
  }
}