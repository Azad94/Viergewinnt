import java.io.IOException;
import java.net.*;

/**
 * Created by sven on 29.10.15.
 * Erstellt eine verbindung mittels Datagramsocket
 */
public class UDPSocket {

    /**
     * Der port an dem gesendet bzw gelauscht wird
     */
    private int port;

    /**
     * Der host zu dem gesprochen wird
     */
    private String host;

    /**
     * Socket zum erhalten und versenden der Nachrichten
     */
    private DatagramSocket socket;

    public UDPSocket(int port, String host) {
        this.port = port;
        this.host = host;
    }

    /**
     * Sende einen Strig an den angegebenen Host
     * @param s             String der zu senden ist.
     * @throws IOException  Wirdt eine Exception wenn das senden fehlschlägt
     */
    public void send(String s) throws IOException {
        socket = new DatagramSocket();
        byte[] bytes = new byte[s.length()+2];
        int i=0;
        for (String string : s.split("")){
            bytes[i++]=new Byte(string);
        }
        bytes[i++]=new Byte("0b01111110");
        bytes[i]= (byte) s.length();
        InetAddress address = InetAddress.getByName(host);
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length,address,port);
        socket.send(packet);
        socket.close();

    }

    /**
     *Empfängt einen String
     * @param maxBytes          Die maximalgröße des empfangenen Packets
     * @return                  Ein String der den Inhalt des Packets repräsentiert
     * @throws SocketException  Wenn eine verbindung fehlschlägt.
     */
    public String receive(int maxBytes) throws IOException {
        socket = new DatagramSocket(port);
        DatagramPacket packet = new DatagramPacket(new byte[maxBytes],maxBytes);
        socket.receive(packet);
        return new String(packet.getData());
    }


}
