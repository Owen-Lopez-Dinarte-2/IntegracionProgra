
package cr.ac.una.reservas.strategy;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;

public class ValidacionHorario implements ValidadorReserva {

    private final int horaApertura;
    private final int horaCierre;

    public ValidacionHorario(
            int horaApertura,
            int horaCierre
    ) {
        if (horaApertura < 0
                || horaCierre > 24
                || horaApertura >= horaCierre) {
            throw new IllegalArgumentException(
                    "El horario configurado no es valido"
            );
        }

        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    @Override
    public void validar(Reserva reserva)
            throws ReservaInvalidaException {

        if (reserva == null) {
            throw new ReservaInvalidaException(
                    "La reserva no puede ser nula"
            );
        }

        if (reserva.getHoraInicio() < horaApertura) {
            throw new ReservaInvalidaException(
                    "La reserva inicia antes de "
                    + "la apertura del laboratorio"
            );
        }

        if (reserva.getHoraFinal() > horaCierre) {
            throw new ReservaInvalidaException(
                    "La reserva termina despues "
                    + "del cierre del laboratorio"
            );
        }

        if (reserva.getHoraInicio()
                >= reserva.getHoraFinal()) {
            throw new ReservaInvalidaException(
                    "La hora final debe ser posterior "
                    + "a la hora inicial"
            );
        }
    }
}
