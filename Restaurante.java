import java.io.*;
import java.net.*;
import java.util.*;

public class Restaurante {
    private List<Mesa> mesas;

    public Restaurante() {
        mesas = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            mesas.add(new Mesa(i));
        }
        cargarEstadoMesas();
    }

    public void cargarEstadoMesas() {
        File archivo = new File("mesas.txt");
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                mesas = (List<Mesa>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            guardarEstadoMesas();
        }
    }

    public void guardarEstadoMesas() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("mesas.txt"))) {
            oos.writeObject(mesas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void iniciarServidor() {
        final int PUERTO = 1234;
    
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado...");
    
            while (true) {
                try (Socket socket = servidor.accept();
                        ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream())) {
    
                    System.out.println("Cliente conectado...");
                    salida.writeObject(mesas);
                    salida.reset();
    
                    String operacion;
                    while (!(operacion = (String) entrada.readObject()).equals("salir")) {
                        int numeroMesa = entrada.readInt();
                        switch (operacion) {
                            case "ver":
                                break;
                            case "reservar":
                                mesas.get(numeroMesa - 1).reservar();
                                guardarEstadoMesas();
                                break;
                            case "liberar":
                                mesas.get(numeroMesa - 1).liberar();
                                guardarEstadoMesas();
                                break;
                        }
    

                        salida.writeObject(mesas);
                        salida.reset();
                    }
    
                    System.out.println("Cliente desconectado...");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
