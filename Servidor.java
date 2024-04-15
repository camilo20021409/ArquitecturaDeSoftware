import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Servidor {
    private static final int PUERTO = 12345;
    private static final String ARCHIVO_MESAS = "mesas.txt";
    private static Map<Integer, Boolean> mesas;

    public static void main(String[] args) {
        cargarMesas();
        imprimirEstadoMesas();

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor en línea. Esperando conexiones de clientes...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado desde " + socket.getInetAddress().getHostAddress());
                new Thread(new ManejadorCliente(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private static void cargarMesas() {
        mesas = new HashMap<>();
        File archivo = new File(ARCHIVO_MESAS);
        if (archivo.exists()) {
            cargarMesasDesdeArchivo();
        } else {
            System.out.println("Archivo de mesas no encontrado. Inicializando mesas...");
            inicializarMesas();
            guardarMesasEnArchivo();
        }
    }

    private static void inicializarMesas() {
        for (int i = 1; i <= 10; i++) {
            mesas.put(i, true); // Todas las mesas inicialmente están disponibles
        }
    }

    private static void cargarMesasDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_MESAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                int numero = Integer.parseInt(partes[0]);
                boolean disponible = Boolean.parseBoolean(partes[1]);
                mesas.put(numero, disponible);
            }
        } catch (IOException e) {
            System.out.println("Error al cargar las mesas desde el archivo: " + e.getMessage());
        }
    }

    private static void guardarMesasEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_MESAS))) {
            for (Map.Entry<Integer, Boolean> entry : mesas.entrySet()) {
                pw.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar las mesas en el archivo: " + e.getMessage());
        }
    }

    private static void imprimirEstadoMesas() {
        System.out.println("Estado inicial de las mesas:");
        for (Map.Entry<Integer, Boolean> entry : mesas.entrySet()) {
            System.out.println("Mesa " + entry.getKey() + ": " + (entry.getValue() ? "Disponible" : "Reservada"));
        }
    }

    // Métodos para reservar, liberar, verificar disponibilidad, etc., omitidos por brevedad

    static synchronized boolean verificarDisponibilidad(int numeroMesa) {
        return mesas.getOrDefault(numeroMesa, false);
    }

    static synchronized void reservarMesa(int numeroMesa) {
        if (mesas.containsKey(numeroMesa)) {
            mesas.put(numeroMesa, false); // Marcar la mesa como no disponible
            guardarMesasEnArchivo(); // Guardar el cambio en el archivo
            System.out.println("La mesa " + numeroMesa + " ha sido reservada.");
        } else {
            System.out.println("La mesa " + numeroMesa + " no existe.");
        }
    }

    static synchronized void liberarMesa(int numeroMesa) {
        if (mesas.containsKey(numeroMesa)) {
            mesas.put(numeroMesa, true); // Marcar la mesa como disponible
            guardarMesasEnArchivo(); // Guardar el cambio en el archivo
            System.out.println("La mesa " + numeroMesa + " ha sido liberada.");
        } else {
            System.out.println("La mesa " + numeroMesa + " no existe.");
        }
    }

    public static synchronized Map<Integer, Boolean> obtenerEstadoMesas() {
        return mesas;
    }
}

class ManejadorCliente implements Runnable {
    private final Socket socket;

    ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)) {

            String opcion;
            while ((opcion = entrada.readLine()) != null) {
                switch (opcion) {
                    case "consultar":
                        consultarMesas(salida);
                        break;
                    case "reservar":
                        reservarMesa(entrada, salida);
                        break;
                    case "liberar":
                        liberarMesa(entrada, salida);
                        break;
                    case "salir":
                        return;
                    default:
                        salida.println("Opción no válida.");
                }
            }
        } catch (IOException e) {
            System.out.println("Error de entrada/salida con el cliente: " + e.getMessage());
        }
    }

    private void consultarMesas(PrintWriter salida) {
        StringBuilder respuesta = new StringBuilder("Mesas disponibles:\n");
        Map<Integer, Boolean> mesas = Servidor.obtenerEstadoMesas(); // Obtener el estado de las mesas
        for (Map.Entry<Integer, Boolean> entry : mesas.entrySet()) {
            if (entry.getValue()) {
                respuesta.append("Mesa ").append(entry.getKey()).append("\n");
            }
        }
        salida.println(respuesta.toString());
    }

    private void reservarMesa(BufferedReader entrada, PrintWriter salida) throws IOException {
        int numeroMesa = Integer.parseInt(entrada.readLine());
        if (Servidor.verificarDisponibilidad(numeroMesa)) {
            Servidor.reservarMesa(numeroMesa);
            salida.println("Mesa reservada correctamente.");
        } else {
            salida.println("La mesa no está disponible para reservar.");
        }
    }

    private void liberarMesa(BufferedReader entrada, PrintWriter salida) throws IOException {
        int numeroMesa = Integer.parseInt(entrada.readLine());
        if (!Servidor.verificarDisponibilidad(numeroMesa)) {
            Servidor.liberarMesa(numeroMesa);
            salida.println("Mesa liberada correctamente.");
        } else {
            salida.println("La mesa ya está disponible.");
        }
    }
}
