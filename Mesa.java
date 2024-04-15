import java.io.Serializable;

public class Mesa implements Serializable {
    private int numero;
    private boolean reservada;

    public Mesa(int numero) {
        this.numero = numero;
        this.reservada = false;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isReservada() {
        return reservada;
    }

    public void reservar() {
        this.reservada = true;
    }

    public void liberar() {
        this.reservada = false;
    }
}
