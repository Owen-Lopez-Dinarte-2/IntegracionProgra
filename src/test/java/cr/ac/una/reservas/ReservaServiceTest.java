
package cr.ac.una.reservas;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cr.ac.una.reservas.exception.ReservaInvalidaException;
import cr.ac.una.reservas.model.Reserva;
import cr.ac.una.reservas.service.ReservaService;
import cr.ac.una.reservas.strategy.ValidacionBasica;
import cr.ac.una.reservas.strategy.ValidacionCapacidad;

class ReservaServiceTest {

    private ReservaService service;

    @BeforeEach
    void setUp() {
        service = new ReservaService();
        service.agregarValidador(new ValidacionBasica());
        service.agregarValidador(new ValidacionCapacidad(30));
    }

    @Test
    void debeAceptarReservaValida() throws Exception {

        Reserva reserva = new Reserva(
                "R-001", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        service.registrarReserva(reserva);

        assertEquals(1, service.listarReservas().size());
    }

    @Test
    void debeRechazarCantidadPersonasCero() {

        Reserva reserva = new Reserva(
                "R-002", "Luis", "Lab 1",
                LocalDate.now(), 10, 12, 0
        );

        assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva)
        );
    }

    @Test
    void debeRechazarHorarioInvertido() {

        Reserva reserva = new Reserva(
                "R-003", "Carlos", "Lab 1",
                LocalDate.now(), 12, 8, 10
        );

        assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva)
        );
    }

    @Test
    void debeRechazarHorasIguales() {

        Reserva reserva = new Reserva(
                "R-004", "Maria", "Lab 1",
                LocalDate.now(), 9, 9, 10
        );

        assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva)
        );
    }

    @Test
    void debeRechazarReservasSuperpuestas()
            throws Exception {

        Reserva primera = new Reserva(
                "R-005", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        Reserva segunda = new Reserva(
                "R-006", "Luis", "Lab 1",
                LocalDate.now(), 9, 11, 15
        );

        service.registrarReserva(primera);

        ReservaInvalidaException error = assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(segunda)
        );

        assertTrue(
                error.getMessage().contains("horario")
        );

        assertEquals(1, service.listarReservas().size());
    }

    @Test
    void debeAceptarHorariosConsecutivos()
            throws Exception {

        Reserva primera = new Reserva(
                "R-007", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        Reserva segunda = new Reserva(
                "R-008", "Luis", "Lab 1",
                LocalDate.now(), 10, 12, 15
        );

        service.registrarReserva(primera);
        service.registrarReserva(segunda);

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeAceptarDiferentesLaboratorios()
            throws Exception {

        Reserva primera = new Reserva(
                "R-009", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        Reserva segunda = new Reserva(
                "R-010", "Luis", "Lab 2",
                LocalDate.now(), 8, 10, 15
        );

        service.registrarReserva(primera);
        service.registrarReserva(segunda);

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeAceptarDiferentesFechas()
            throws Exception {

        LocalDate fecha = LocalDate.now();

        Reserva primera = new Reserva(
                "R-011", "Ana", "Lab 1",
                fecha, 8, 10, 20
        );

        Reserva segunda = new Reserva(
                "R-012", "Luis", "Lab 1",
                fecha.plusDays(1), 8, 10, 15
        );

        service.registrarReserva(primera);
        service.registrarReserva(segunda);

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeAceptarHorarioDeReservaCancelada()
            throws Exception {

        Reserva primera = new Reserva(
                "R-013", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        service.registrarReserva(primera);
        primera.cancelar();

        Reserva segunda = new Reserva(
                "R-014", "Luis", "Lab 1",
                LocalDate.now(), 9, 11, 15
        );

        service.registrarReserva(segunda);

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeRechazarCodigoDuplicado()
            throws Exception {

        Reserva primera = new Reserva(
                "R-015", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        Reserva segunda = new Reserva(
                "R-015", "Luis", "Lab 2",
                LocalDate.now(), 10, 12, 15
        );

        service.registrarReserva(primera);

        assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(segunda)
        );

        assertEquals(1, service.listarReservas().size());
    }
}
