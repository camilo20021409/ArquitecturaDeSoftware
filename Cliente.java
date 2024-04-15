import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente {
    private final String HOST = "localhost";
    private final int PUERTO = 1234;

    public void iniciarCliente() {
        try (Socket socket = new Socket(HOST, PUERTO);
             ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {
    
            System.out.println("Cliente conectado...");
    
            while (true) {
                System.out.println("\n1. Ver estado de las mesas");
                System.out.println("2. Reservar una mesa");
                System.out.println("3. Liberar una mesa");
                System.out.println("4. Salir");
                System.out.print("Selecciona una opción: ");
                int opcion = scanner.nextInt();
    
                if (opcion == 4) {
                    salida.writeObject("salir");
                    salida.writeInt(0);
                    break;
                }
    
                int numeroMesa = 0;
                if (opcion == 2 || opcion == 3) {
                    System.out.print("Ingresa el número de la mesa: ");
                    numeroMesa = scanner.nextInt();
                }
    
                switch (opcion) {
                    case 1:
                        salida.writeObject("ver");
                        salida.writeInt(numeroMesa);
                        salida.flush();
                        break;
                    case 2:
                        salida.writeObject("reservar");
                        salida.writeInt(numeroMesa);
                        salida.flush();
                        break;
                    case 3:
                        salida.writeObject("liberar");
                        salida.writeInt(numeroMesa);
                        salida.flush();
                        break;
                }
                List<Mesa> mesas = (List<Mesa>) entrada.readObject();
                for (Mesa mesa : mesas) {
                    System.out.println("Mesa " + mesa.getNumero() + ": " + (mesa.isReservada() ? "Reservada" : "Libre"));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        Cliente cliente = new Cliente();
        cliente.iniciarCliente();
    }
}