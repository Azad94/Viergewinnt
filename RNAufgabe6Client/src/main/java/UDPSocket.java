import javax.net.DatagramSocket;
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
    private InetSocketAddress address;

    /**
     * Socket zum erhalten und versenden der Nachrichten
     */
    private DatagramSocket socket;

    private int timeOut;

    public UDPSocket(int port, String host, int timeOut) throws SocketException, UnknownHostException {
        this.port = port;
        this.address = new InetSocketAddress(host,port);
        this.timeOut=timeOut;
        this.socket= new DatagramSocket();
        this.socket.connect(this.address);
    }

    /**
     * Sende einen Strig an den angegebenen Host
     * @param s             String der zu senden ist.
     * @throws IOException  Wirdt eine Exception wenn das senden fehlschlägt
     */
    public void send(String s) throws IOException {
        byte[] bytes = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length,this.address);
        System.out.println(new String(packet.getData()));
        this.socket.send(packet);
        System.out.println("Sende etwas");

    }

    /**
     *Empfängt einen String
     * @param maxBytes          Die maximalgröße des empfangenen Packets
     * @return                  Ein String der den Inhalt des Packets repräsentiert
     * @throws SocketException  Wenn eine verbindung fehlschlägt.
     */
    public byte[] receive(int maxBytes) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[maxBytes],maxBytes);
        this.socket.setSoTimeout(10000);
        this.socket.receive(packet);
        System.out.println(new String(packet.getData()));
        System.out.println("Empfange etwas");
        if (packet.getLength()==0) return new byte[0];
        return packet.getData();
    }

    /**
     * Schließt die verbindung
     */
    public void closeSocket(){
        this.socket.close();
    }


}
