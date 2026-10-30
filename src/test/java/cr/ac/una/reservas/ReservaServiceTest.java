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
import cr.ac.una.reservas.strategy.ValidacionHorario;

class ReservaServiceTest {

    private ReservaService service;

    @BeforeEach
    void setUp() {
        service = new ReservaService();
        service.agregarValidador(new ValidacionBasica());
        service.agregarValidador(new ValidacionCapacidad(30));
        service.agregarValidador(new ValidacionHorario(7, 22));
    }

    private Reserva crearReserva(
            String codigo,
            String solicitante,
            String laboratorio,
            LocalDate fecha,
            int inicio,
            int fin,
            int personas
    ) {
        return new Reserva(
                codigo, solicitante, laboratorio,
                fecha, inicio, fin, personas
        );
    }

    @Test
    void debeAceptarReservaValida() throws Exception {

        service.registrarReserva(
                crearReserva("R-001", "Ana", "Lab 1",
                        LocalDate.now(), 8, 10, 20)
        );

        assertEquals(1, service.listarReservas().size());
    }

    
    
    @Test
    void debeRechazarCantidadPersonasCero() {

        Reserva reserva = crearReserva(
                "R-002", "Luis", "Lab 1",
                LocalDate.now(), 10, 12, 0
        );

        assertThrows(ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva));
    }

    @Test
    void debeRechazarHorarioInvertido() {

        Reserva reserva = crearReserva(
                "R-003", "Carlos", "Lab 1",
                LocalDate.now(), 12, 8, 10
        );

        assertThrows(ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva));
    }

    @Test
    void debeRechazarHorasIguales() {

        Reserva reserva = crearReserva(
                "R-004", "Maria", "Lab 1",
                LocalDate.now(), 9, 9, 10
        );

        assertThrows(ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva));
    }

    @Test
    void debeRechazarReservasSuperpuestas()
            throws Exception {

        Reserva primera = crearReserva(
                "R-005", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        Reserva segunda = crearReserva(
                "R-006", "Luis", "Lab 1",
                LocalDate.now(), 9, 11, 15
        );

        service.registrarReserva(primera);

        ReservaInvalidaException error = assertThrows(
                ReservaInvalidaException.class,
                () -> service.registrarReserva(segunda)
        );

        assertTrue(error.getMessage().contains("horario"));
        assertEquals(1, service.listarReservas().size());
    }

    @Test
    void debeAceptarHorariosConsecutivos()
            throws Exception {

        service.registrarReserva(crearReserva(
                "R-007", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        ));

        service.registrarReserva(crearReserva(
                "R-008", "Luis", "Lab 1",
                LocalDate.now(), 10, 12, 15
        ));

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeAceptarDiferentesLaboratorios()
            throws Exception {

        service.registrarReserva(crearReserva(
                "R-009", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        ));

        service.registrarReserva(crearReserva(
                "R-010", "Luis", "Lab 2",
                LocalDate.now(), 8, 10, 15
        ));

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeAceptarDiferentesFechas()
            throws Exception {

        LocalDate fecha = LocalDate.now();

        service.registrarReserva(crearReserva(
                "R-011", "Ana", "Lab 1",
                fecha, 8, 10, 20
        ));

        service.registrarReserva(crearReserva(
                "R-012", "Luis", "Lab 1",
                fecha.plusDays(1), 8, 10, 15
        ));

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeAceptarHorarioDeReservaCancelada()
            throws Exception {

        Reserva primera = crearReserva(
                "R-013", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        );

        service.registrarReserva(primera);
        primera.cancelar();

        service.registrarReserva(crearReserva(
                "R-014", "Luis", "Lab 1",
                LocalDate.now(), 9, 11, 15
        ));

        assertEquals(2, service.listarReservas().size());
    }

    @Test
    void debeRechazarCodigoDuplicado()
            throws Exception {

        service.registrarReserva(crearReserva(
                "R-015", "Ana", "Lab 1",
                LocalDate.now(), 8, 10, 20
        ));

        Reserva segunda = crearReserva(
                "R-015", "Luis", "Lab 2",
                LocalDate.now(), 10, 12, 15
        );

        assertThrows(ReservaInvalidaException.class,
                () -> service.registrarReserva(segunda));

        assertEquals(1, service.listarReservas().size());
    }

    @Test
    void debeRechazarAntesDeApertura() {

        Reserva reserva = crearReserva(
                "R-016", "Ana", "Lab 1",
                LocalDate.now(), 6, 9, 15
        );

        assertThrows(ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva));
    }

    @Test
    void debeRechazarDespuesDelCierre() {

        Reserva reserva = crearReserva(
                "R-017", "Luis", "Lab 1",
                LocalDate.now(), 21, 23, 15
        );

        assertThrows(ReservaInvalidaException.class,
                () -> service.registrarReserva(reserva));
    }

    @Test
    void debeAceptarHorarioLimite()
            throws Exception {

        service.registrarReserva(crearReserva(
                "R-018", "Ana", "Lab 1",
                LocalDate.now(), 7, 22, 15
        ));

        assertEquals(1, service.listarReservas().size());
    }

    @Test
    void debeRechazarConfiguracionInvalida() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new ValidacionHorario(22, 7)
        );
    }
    

    
    
}
