
package cr.ac.una.reservas.exception;

public class ReservaInvalidaException extends Exception {

    public ReservaInvalidaException(String mensaje) {
        super(mensaje);
    }

    public ReservaInvalidaException(
            String mensaje,
            Throwable causa
    ) {
        super(mensaje, causa);
    }
}
