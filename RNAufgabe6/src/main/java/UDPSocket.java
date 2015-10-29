import javax.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

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

    private int timeOut;

    public UDPSocket(int port, String host,int timeOut) {
        this.port = port;
        this.host = host;
        this.timeOut=timeOut;
    }

    /**
     * Sende einen Strig an den angegebenen Host
     * @param s             String der zu senden ist.
     * @throws IOException  Wirdt eine Exception wenn das senden fehlschlägt
     */
    public void send(String s) throws IOException {
        this.socket = new DatagramSocket();
        byte[] bytes = new byte[s.length()+2];
        int i=0;
        for (String string : s.split("")){
            bytes[i++]=new Byte(string);
        }
        bytes[i++]=new Byte("0b01111110");
        bytes[i]= (byte) s.length();
        InetAddress address = InetAddress.getByName(this.host);
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length,address,this.port);
        this.socket.send(packet);
        this.socket.close();

    }

    /**
     *Empfängt einen String
     * @param maxBytes          Die maximalgröße des empfangenen Packets
     * @return                  Ein String der den Inhalt des Packets repräsentiert
     * @throws SocketException  Wenn eine verbindung fehlschlägt.
     */
    public String receive(int maxBytes) throws IOException {
        this.socket = new DatagramSocket(this.port);
        DatagramPacket packet = new DatagramPacket(new byte[maxBytes],maxBytes);
        this.socket.setSoTimeout(this.timeOut);
        this.socket.receive(packet);
        this.host=packet.getAddress().toString();
        return new String(packet.getData());
    }


}
