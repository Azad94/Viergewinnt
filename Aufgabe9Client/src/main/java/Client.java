import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sven on 02.11.15.
 */
public class Client {

    private static final int BUFSIZE = 8;
    private static int TIMEOUT=1000;

    public static void main(String args[]) throws Exception {


        if (args.length != 3) {
            System.err.println("Bitte gebe einen Port, einen Dateipfad und einen Host an in den args!!");
            System.exit(-1);
        } else {
            int port = Integer.parseInt(args[0]);
            String filePath = args[1];
            String host = args[2];
            // Open special datagramm socket from jlibcnds library, do not change this
            javax.net.DatagramSocket dtgSock;
            dtgSock = new javax.net.DatagramSocket();
            InetSocketAddress srvSockAddr = new InetSocketAddress(host, port);
            dtgSock.connect(srvSockAddr);
            System.out.println("Starte Client");
            serverRoutine(filePath, dtgSock, srvSockAddr);
            dtgSock.close();    // Close the Socket
            System.exit(0);
        }

    }

    /**
     * Sendet einen String an den Server
     *
     * @param s             Der String der gesendet werden soll
     * @param packageNumber Die nummer des Packets
     */
    private static void send(String s, int packageNumber, InetSocketAddress address, javax.net.DatagramSocket socket) throws IOException {
        byte[] bytes;
        if (!s.equals("")) {
            int i = 0;
            bytes = new byte[s.length() + 3];
            for (String string : s.split("")) {
                bytes[i++] = (byte) string.charAt(0);
            }
            bytes[i++] = (byte) 0b01111110;
            bytes[i++] = (byte) s.length();
            bytes[i] = (byte) packageNumber;
            bytes= Tools.addParityBytes(bytes);
            bytes = Tools.addTwoDuplicates(bytes);
        } else bytes = new byte[0];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
        System.out.println(new String(packet.getData()));
        socket.send(packet);
        System.out.println("Sende etwas");
    }

    /**
     * Empfängt daten von Server
     *
     * @param maxBytes Die maximalgröße des Rahmens
     * @param socket   Socket
     * @return String represatation des Inhalts des empfangenen Rahmens
     */
    private static String receive(int maxBytes, javax.net.DatagramSocket socket) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[maxBytes], maxBytes);
        socket.setSoTimeout(TIMEOUT);
        socket.receive(packet);
        System.out.println("Empfange etwas");
        System.out.println(new String(packet.getData()));
        if (packet.getLength() == 0) {
            return "";
        } else {
            return new String(packet.getData());
        }
    }

    /**
     * Kümmert sich um den Ablauf des servers
     */
    public static void serverRoutine(String filePath, javax.net.DatagramSocket socket, InetSocketAddress address) {

        boolean failureReceive;
        long send;
        long receive;
        List<String> list;
        String buffer = "";
        try {
            list = readData(filePath);
            int i = 0;
            //sende packete und empfange sie
            while (i != list.size()) {
                send = System.currentTimeMillis();
                send(list.get(i), i, address, socket);
                try {
                    buffer = receive(BUFSIZE, socket);
                    failureReceive = false;
                    receive=System.currentTimeMillis();
                    updateTimeout(send, receive);
                } catch (IOException e) {
                    e.printStackTrace();
                    failureReceive = true;
                }
                if (!checkFailure(buffer) && !failureReceive) {
                    i++;
                }
            }
            buffer="";
            failureReceive=false;
            //Sende leer um den server zu beenden.
            while (!buffer.equals("cleanUP")&&!failureReceive){
                try {
                    buffer = receive(BUFSIZE,socket);
                    failureReceive=false;
                } catch (IOException e) {
                    failureReceive=true;
                }
                send("",0,address, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     *
     * @param file Der Dateipfad zu der Datei die eingelesen werden soll
     */
    private static List<String> readData(String file) {
        List<String> list = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true) {
                list.add(reader.readLine());
                if (null == ((list.get(list.size() - 1)))) {
                    list.remove(list.size() - 1);
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Prüft ob ein Packet eine Fehler rückgabe hat oder ob es eine normale Rückmeldung ist
     * Ein falsches Packet hat mehr nullen als einsen. 100000001 ist ein falsch packet.
     *
     * @param s Das array das geprüft werden soll
     * @return true wenn es eine Fehlerrückmeldung ist false ansonsten
     * */
    private static boolean checkFailure(String s) {
        try {
            if (Integer.parseInt(s.trim()) == 0b10000001) return false;
            else if (Integer.parseInt(s.trim()) == 0b01111110) return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return true;
        }
        return true;
    }

    private static void updateTimeout(long send,long receive){
        TIMEOUT= (int) (receive-send);
    }
}
