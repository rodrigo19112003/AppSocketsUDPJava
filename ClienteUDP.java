import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class ClienteUDP {
    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
        String mensaje = new String("Dame la hora local");
        String servidor = new String("localhost");

        int puerto = 8080;
        int espera = 5000;

        DatagramSocket socketUDP = new DatagramSocket();
        InetAddress hostServidor = InetAddress.getByName(servidor);
        
        DatagramPacket peticion = new DatagramPacket(mensaje.getBytes(), mensaje.getBytes().length, hostServidor, puerto);

        socketUDP.setSoTimeout(espera);
        System.out.println("Esperamos datos en un maximo de " + espera + " milisegundos...");
        socketUDP.send(peticion);

        try {
            byte[] buffer = new byte[1024];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            socketUDP.receive(respuesta); 

            String strText = new String(respuesta.getData(), 0, respuesta.getLength());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime horaServidor = LocalDateTime.parse(strText, formatter);
            
            System.out.println("Hora del servidor es: " + horaServidor.format(formatter));

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            LocalDateTime horaCliente = LocalDateTime.now();
            long diferenciaMilisegundos = Duration.between(horaServidor, horaCliente).toMillis();
            double diferenciaSegundos = diferenciaMilisegundos / 1000.0;

            System.out.println("Hora del cliente es: " + horaCliente.format(formatter));

            System.out.println("La diferencia entre las dos horas es: " + diferenciaSegundos + " segundos");

            socketUDP.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Tiempo expirado para recibir respuesta: " + e.getMessage());
        }
    }
}


