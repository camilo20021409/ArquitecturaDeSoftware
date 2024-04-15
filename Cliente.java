import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {
    private static final String SERVIDOR_IP = "127.0.0.1";
    private static final int PUERTO = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVIDOR_IP, PUERTO);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conexión establecida con el servidor.");

            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
            String opcion;

            do {
                mostrarMenu();
                opcion = teclado.readLine();
                salida.println(opcion);

                switch (opcion) {
                    case "1":
                    case "2":
                    case "3":
                        mostrarRespuesta(entrada);
                        break;
                    case "4":
                        System.out.println("Saliendo del programa.");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } while (!opcion.equals("4"));
        } catch (IOException e) {
            System.out.println("Error de conexión con el servidor: " + e.getMessage());
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n------ Menú ------");
        System.out.println("1. Consultar mesas disponibles");
        System.out.println("2. Reservar mesa");
        System.out.println("3. Liberar mesa");
        System.out.println("4. Salir");
        System.out.print("Ingrese su opción: ");
    }

    private static void mostrarRespuesta(BufferedReader entrada) throws IOException {
        String respuesta;
        while ((respuesta = entrada.readLine()) != null) {
            System.out.println(respuesta);
        }
    }
}
