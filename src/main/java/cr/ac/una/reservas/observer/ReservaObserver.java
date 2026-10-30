package cr.ac.una.reservas.observer;

import cr.ac.una.reservas.model.Reserva;

public interface ReservaObserver {

    void reservaRegistrada(Reserva reserva);

    default void reservaConfirmada(Reserva reserva) {
    }

    default void reservaCancelada(Reserva reserva) {
    }
}