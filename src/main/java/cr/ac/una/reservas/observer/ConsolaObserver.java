package cr.ac.una.reservas.observer;

import cr.ac.una.reservas.model.Reserva;

public class ConsolaObserver implements ReservaObserver {

    @Override
    public void reservaRegistrada(Reserva reserva) {
        System.out.println(
                "[REGISTRO] Reserva creada: "
                + reserva.getCodigo()
        );
    }

    @Override
    public void reservaConfirmada(Reserva reserva) {
        System.out.println(
                "[CONFIRMACION] Reserva confirmada: "
                + reserva.getCodigo()
        );
    }

    @Override
    public void reservaCancelada(Reserva reserva) {
        System.out.println(
                "[CANCELACION] Reserva cancelada: "
                + reserva.getCodigo()
        );
    }
}