import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sven on 02.11.15.
 */
public class Server {

    private static final int BUFSIZERECEIVE = 27;
    private static InetAddress address;
    private static int port;

    public static void main(String args[]){
        if (args.length != 2) {
            System.err.println("Bitte geben sie Port und filepath ein");
            System.exit(-1);
        } else {
            int port = Integer.valueOf(args[0]);
            String filePath = args[1];
            // Open special datagramm socket from jlibcnds library, do not change this
            javax.net.DatagramSocket dtgSock;
            try {
                dtgSock = new javax.net.DatagramSocket(port);
                serverRoutine(filePath,dtgSock,port);
                dtgSock.close();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     *Empfängt einen String
     * @param maxBytes          Die maximalgröße des empfangenen Packets
     * @return                  Ein String der den Inhalt des Packets repräsentiert
     * @throws SocketException  Wenn eine verbindung fehlschlägt.
     */
    public static byte[] receive(int maxBytes, javax.net.DatagramSocket socket) throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[maxBytes],maxBytes);
        socket.setSoTimeout(10000);
        socket.receive(packet);
        address = packet.getAddress();
        port = packet.getPort();
        if (packet.getLength()==0) return new byte[0];
        return packet.getData();
    }



    /**
     * Sende einen Strig an den angegebenen Host
     * @param s             String der zu senden ist.
     * @throws IOException  Wirdt eine Exception wenn das senden fehlschlägt
     */
    public static void send(String s, javax.net.DatagramSocket socket) throws IOException {
        byte[] bytes = s.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes,bytes.length);
        if (address!=null){
            packet.setAddress(address);
            packet.setPort(port);
        }
        else return;
        socket.send(packet);
        System.out.println(new String(packet.getData()));

    }


    /**
     * Ist für die eigentliche Client Logik zuständig heißt ein und ausgabe
     * @param port  Port für das senden und empfangen von daten
     * @param file  Die Datei in die wir schreiben werden
     */
    private static void serverRoutine(String file,javax.net.DatagramSocket socket,int port) {
        boolean failure;
        List<String> list;
        try {
            list = new LinkedList<>();
            byte[] bytes=new byte[BUFSIZERECEIVE];
            int packageNumber=0;
            while (true){
                try {
                    bytes=receive(BUFSIZERECEIVE,socket);
                    failure=false;
                    /*
                    Ein Majority voe stellt fest, ob ein Rahmen richtig Übertragen wird.
                    es wird einfach ein rahmen genommen z.B. 1001 dieses wird dann vorher im client durch
                    Tools.addTwoduplicates erweitert. Der rahmen sieht nur we folgt aus 100110011001.
                     Majorityvote checkt nun auf dieser Seite ob sich bit verschoben haben z.B. 1101_1001_1001.
                     Da Sich an der 2. stelle ein bitfehler eingeschlichen hat wird dieser erkannt in dem einfach geprüft
                     wird welches bit häufiger eintritt dieses wäre hier dann die 0 somit wird die 0 genommen. Das wird
                     alle bits vollzogen. Wie man sieht reicht dies allerdings nicht aus es könnte ja auch passieren,
                     das der Fehler genau anders passiert. Dies wird allerdings durch die Rückwährsfehlerkorrektur aus
                     Aufgabe 6 hoffentlich abgefangen.
                     */
                    bytes=Tools.doMajorityVote(bytes);
                    System.out.println(bytes.length);
                    if (bytes.length==0){
                        System.out.println(bytes.length);
                        for (int j=2;j>0;j--) {
                            //Senden um den client zu beenden
                            send("cleanUp",socket);

                        }
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    failure=true;
                }
                //Wenn kein fehler passiert hinzufügen und ACK senden das erneute senden dient für Aufgabe 6
                if ((packageNumber=checkFailure(bytes))>=0&&!failure){
                    if (packageNumber==list.size()) {
                        System.out.println("Hinzugefügt");
                        System.out.println(new String(bytes));
                        list.add(encodeData(bytes));
                    }
                    //Sende ACK
                    send(0b10000001 + "",socket);
                }else{
                    //Sende false
                    send(0b01111110+"",socket);
                }
            }
            //Schreibe daten in die Datei
            writeData(list,file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);

    }

    /**
     * Schreibt die daten in eine Datei
     *
     * @param data Die daten die geschrieben werden sollen
     */
    private static void writeData(List<String> data, String filePAth) {
        try {
            Writer writer = new FileWriter(new File(filePAth));
            for (String s : data) {
                writer.append(s+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encodiert den Rahmen heißt die flags werden aus dem Rahmen entnommen die an den letzten 4 stellen stehen
     *
     * @param data Der Rahmen der Encodiert wird
     * @return Den Rahmen ohne Flags
     */
    private static String encodeData(byte[] data) {
        return new String(data,0,data.length-4);
    }

    /**
     * Überprüft ob ein Rahmen nicht richtig übersendet wurde in dem es nur die Flag bytes überprüft
     *
     * @param bytes         Der Rahmen der überprüft wird
     * @return Die nummer des Packets oder 0 wenn das Packet einen fehler enthält
     */
    private static int checkFailure(byte[] bytes) {
        //Man sollte vielleicht als erstes auf länge prüfen kann man ja noch einsetzen mache ich Montag
        //TODO erst auf länge prüfen da sonst der rest fehlschlagen könnte
        boolean check = false;
        int parityBit = (int)bytes[bytes.length-1];
        byte[] checkArray = new byte[bytes.length-1];
        //Nur um paritybyte zu bekommen
        for (int i = 0; i < checkArray.length; i++) {
            checkArray[i]=bytes[i];
        }
        //Errechne dieses paritybyte
        bytes = Tools.addParityBytes(checkArray);
        int checkParity = bytes[bytes.length-1];
        //letzen drei zeichen infos über rahmen
        int flagBegin = bytes.length - 4;
        //Escape flagg
        if(((int) bytes[flagBegin]) !=(byte) 0b01111110) check = true;
        //Größe des rahmens prüfen
        if(((int) bytes[flagBegin+1]) !=(byte) flagBegin) check = true;
        //Hier auf parity checken
        if (parityBit != checkParity) check=true;
        //letztes zeichen ist packetnummer
        return check?-1:((int)bytes[bytes.length - 2]);
    }
}
