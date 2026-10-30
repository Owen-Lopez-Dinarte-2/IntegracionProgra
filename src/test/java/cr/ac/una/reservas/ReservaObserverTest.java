package cr.ac.una.reservas;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.observer.ResumenObserver;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservaObserverTest {

    private ReservaService service;
    private ResumenObserver resumen;

    @BeforeEach
    void setUp() {
        service = new ReservaService();
        service.agregarValidador(new ValidacionBasica());
        service.agregarValidador(new ValidacionCapacidad(30));

        resumen = new ResumenObserver();
        service.agregarObserver(resumen);
    }

    @Test
    void debeNotificarAlRegistrarReservaValida() throws Exception {
        Reserva reserva = new Reserva(
                "R-001", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        service.registrarReserva(reserva);

        assertEquals(1, service.listarReservas().size());
        assertEquals(1, resumen.getTotalRegistradas());
    }

    @Test
    void noDebeNotificarSiStrategyRechazaReserva() {
        Reserva reserva = new Reserva(
                "R-002", "Luis", "Lab 1",
                LocalDate.now(), 8, 10, 31
        );

        ReservaInvalidaException error = assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva)
        );

        assertTrue(error.getMessage().contains("capacidad"));
        assertEquals(0, service.listarReservas().size());
        assertEquals(0, resumen.getTotalRegistradas());
    }
}