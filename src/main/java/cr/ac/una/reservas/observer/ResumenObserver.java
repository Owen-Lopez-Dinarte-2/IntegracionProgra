package cr.ac.una.reservas.observer;

import cr.ac.una.reservas.model.Reserva;

public class ResumenObserver implements ReservaObserver {

    private int totalRegistradas;

    @Override
    public void reservaRegistrada(Reserva reserva) {
        totalRegistradas++;

        System.out.println(
                "[RESUMEN] Total de reservas registradas: "
                + totalRegistradas
        );
    }

    public int getTotalRegistradas() {
        return totalRegistradas;
    }
}