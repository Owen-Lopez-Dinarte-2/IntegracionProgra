package cr.ac.una.reservas.service;

import java.util.ArrayList;
import java.util.List;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.model.EstadoReserva;
import cr.ac.una.reservas.observer.ReservaObserver;
import cr.ac.una.reservas.strategy.ValidadorReserva;

public class ReservaService {

    private final List<Reserva> reservas;
    private final List<ValidadorReserva> validadores;
    private final List<ReservaObserver> observers;

    public ReservaService() {
        reservas = new ArrayList<>();
        validadores = new ArrayList<>();
        observers = new ArrayList<>();
    }

    public void agregarValidador(ValidadorReserva validador) {
        validadores.add(validador);
    }

    public void agregarObserver(ReservaObserver observer) {
        observers.add(observer);
    }

    public void registrarReserva(Reserva reserva)
            throws ReservaInvalidaException {

        if (reserva == null) {
            throw new ReservaInvalidaException(
                    "La reserva no puede ser nula"
            );
        }

        for (ValidadorReserva validador : validadores) {
            validador.validar(reserva);
        }

        if (reserva.getCodigo() == null
                || reserva.getCodigo().isBlank()) {
            throw new ReservaInvalidaException(
                    "El codigo es obligatorio"
            );
        }

        if (reserva.getLaboratorio() == null
                || reserva.getLaboratorio().isBlank()) {
            throw new ReservaInvalidaException(
                    "El laboratorio es obligatorio"
            );
        }

        if (reserva.getFecha() == null) {
            throw new ReservaInvalidaException(
                    "La fecha es obligatoria"
            );
        }

        if (reserva.getHoraInicio() >= reserva.getHoraFinal()) {
            throw new ReservaInvalidaException(
                    "La hora final debe ser posterior "
                    + "a la hora inicial"
            );
        }

        validarCodigoDuplicado(reserva);
        validarSuperposicion(reserva);

        reservas.add(reserva);
        notificarRegistro(reserva);
    }

    public void confirmarReserva(String codigo)
            throws ReservaInvalidaException {

        Reserva reserva = buscarReserva(codigo);

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ReservaInvalidaException(
                    "Solo se puede confirmar una reserva pendiente"
            );
        }

        reserva.confirmar();
        notificarConfirmacion(reserva);
    }

    public void cancelarReserva(String codigo)
            throws ReservaInvalidaException {

        Reserva reserva = buscarReserva(codigo);

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new ReservaInvalidaException(
                    "La reserva ya esta cancelada"
            );
        }

        reserva.cancelar();
        notificarCancelacion(reserva);
    }

    private Reserva buscarReserva(String codigo)
            throws ReservaInvalidaException {

        if (codigo == null || codigo.isBlank()) {
            throw new ReservaInvalidaException(
                    "El codigo es obligatorio"
            );
        }

        for (Reserva reserva : reservas) {
            if (reserva.getCodigo().equalsIgnoreCase(codigo)) {
                return reserva;
            }
        }

        throw new ReservaInvalidaException(
                "No existe una reserva con ese codigo"
        );
    }

    private void validarCodigoDuplicado(Reserva nueva)
            throws ReservaInvalidaException {

        for (Reserva existente : reservas) {
            if (existente.getCodigo()
                    .equalsIgnoreCase(nueva.getCodigo())) {

                throw new ReservaInvalidaException(
                        "Ya existe una reserva con ese codigo"
                );
            }
        }
    }

    private void validarSuperposicion(Reserva nueva)
            throws ReservaInvalidaException {

        for (Reserva existente : reservas) {

            if (existente.getEstado() == EstadoReserva.CANCELADA) {
                continue;
            }

            boolean mismoLaboratorio = existente.getLaboratorio()
                    .equalsIgnoreCase(nueva.getLaboratorio());

            boolean mismaFecha = existente.getFecha()
                    .equals(nueva.getFecha());

            boolean horariosCruzan =
                    nueva.getHoraInicio() < existente.getHoraFinal()
                    && nueva.getHoraFinal() > existente.getHoraInicio();

            if (mismoLaboratorio && mismaFecha && horariosCruzan) {
                throw new ReservaInvalidaException(
                        "El laboratorio ya esta reservado "
                        + "en ese horario"
                );
            }
        }
    }

    private void notificarRegistro(Reserva reserva) {
        for (ReservaObserver observer : observers) {
            observer.reservaRegistrada(reserva);
        }
    }

    private void notificarConfirmacion(Reserva reserva) {
        for (ReservaObserver observer : observers) {
            observer.reservaConfirmada(reserva);
        }
    }

    private void notificarCancelacion(Reserva reserva) {
        for (ReservaObserver observer : observers) {
            observer.reservaCancelada(reserva);
        }
    }

    public List<Reserva> listarReservas() {
        return List.copyOf(reservas);
    }
}