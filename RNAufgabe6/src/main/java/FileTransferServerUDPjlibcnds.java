//package experiment2;

import javax.xml.stream.events.Characters;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

public class FileTransferServerUDPjlibcnds {

    private static final int BUFSIZE = 508;
    private static final int BUFSIZESEND=10;
    private static int port;
    private static InetAddress address;
    private static String filePath;

    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.err.println("Bitte gebe einen Port und einen Dateipfad an in den args!!");
            System.exit(-1);
        } else {
            port = Integer.parseInt(args[0]);
            filePath = args[1];
            serverRoutine();
        }

    }

    /**
     * Kümmert sich um den Ablauf des servers
     */
    public static void serverRoutine() {
        /**
         * TODO Soll sich um den allgemeinen ablauf des Server kümmern. Ideal wäre, dass wenn eine Exception gworfen wird
         * TODO sich dieser nur neustartet oder ähnliches. Heißt der Server soll schon einen gwissen Wiederstand besitzen
         */
    }

    /**
     * Empfängt die packet und wirft ordnet diese in Byte [] ein.
     *
     * @param socket Socket auf dem die Daten empfangen werden
     * @return Eine liste an packeten die ampfangen wurden
     */
    private List<byte[]> receivePackets(DatagramSocket socket) throws IOException {
        List<byte[]> list = new LinkedList<>();
        while (true) {
            DatagramPacket packet = new DatagramPacket(new byte[BUFSIZE], BUFSIZE);
            socket.receive(packet);
            if (packet.getLength() == 0) { 
                port = packet.getPort();
                address = packet.getAddress();
                break;
            } else {
                list.add(packet.getData());
            }
        }
        return list;


    }

    /**
     * Schickt die packete an eine gewünschte adresse
     *
     * @param data   Die daten die verschickt werdne sollen
     * @param socket Socket auf dem die Daten gesendet werden sollen.
     */
    private void sendPackets(List<byte[]> data, DatagramSocket socket) throws IOException, InterruptedException {
        for (byte[] b : data) {
            DatagramPacket packet = new DatagramPacket(b, b.length, address, port);
            socket.send(packet);
            Thread.sleep(100);
        }

    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     *
     * Außerdem wird noch ein Bytestopfen zum ende hinzugefügt
     * Dieser bytestopfen signalisiert das ende eines rahmens
     * Es ist wie folgt zusammengefügt das flag 01111110 zeigt das es einen stopfen gibt
     * danach folgt die länge des Rahmens
     *
     * @param file Die datei die eingelesen werden soll
     */
    private List<byte[]> readData(String file) {
        StringBuilder buffer = new StringBuilder(BUFSIZESEND);
        List<byte[]> list = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            int buf;
            while (true) {

                if ((buf = reader.read()) != Characters.END_DOCUMENT) {
                    buffer.append(buf);
                } else break;
                //-2 weil die letzen beiden bytes für den bytestopfen reserviert sind
                if (buffer.length()==BUFSIZESEND-2){
                    buffer.append(0b01111110);
                    buffer.append(buffer.length());
                    list.add(buffer.toString().getBytes());
                    buffer.delete(0,buffer.length()-1);
                }
            }
            reader.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (list.isEmpty()){
                list.add("failure".getBytes());
            }
        }
        return list;
    }

    /**
     * Prüft ob ein Packet eine Fehler rückgabe hat oder ob es eine normale Rückmeldung ist
     * Ein falsches Packet hat mehr nullen als einsen. 100000001 ist ein falsch packet.
     *
     * @param data Das array das geprüft werden soll
     * @return true wenn es eine Fehlerrückmeldung ist false ansonsten
     */
    private boolean checkFailure(byte[] data) {
        int zeros=0;
        int ones=0;
        for (byte b : data){
            if(b==0){
                zeros++;
            }else ones++;
        }
        if (zeros>ones) return true;
        else return false;
    }
}
