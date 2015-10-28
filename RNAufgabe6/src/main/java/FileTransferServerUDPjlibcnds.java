//package experiment2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

public class FileTransferServerUDPjlibcnds {

    private static final int BUFSIZESEND=508;
    private static int port;
    private static InetAddress address;

    public static void main(String args[]) throws Exception{
        if (args.length!=2){
            System.err.println("Bitte gebe einen Port und einen Dateipfad an in den args!!");
            System.exit(-1);
        }else {
            port = Integer.parseInt(args[0]);
            String filePath = args[1];
            serverRoutine();
        }

    }

    /**
     * Kümmert sich um den Ablauf des servers
     */
    public static void serverRoutine(){

    }

    /**
     * Empfängt die packet und wirft ordnet diese in Byte [] ein.
     * @param socket    Socket auf dem die Daten empfangen werden
     * @return          Eine liste an packeten die ampfangen wurden
     *
     */
    private List<byte[]> receivePackets(DatagramSocket socket) throws IOException {
        List<byte[]> list = new LinkedList<>();
        while (true){
            DatagramPacket packet = new DatagramPacket(new byte[BUFSIZESEND],BUFSIZESEND);
            socket.receive(packet);
            if(packet.getLength()==0){
                port = packet.getPort();
                address = packet.getAddress();
                break;
            }
            else {
                list.add(packet.getData());
            }
        }
        return list;


    }

    /**
     * Schickt die packete an eine gewünschte adresse
     * @param data      Die daten die verschickt werdne sollen
     * @param socket    Socket auf dem die Daten gesendet werden sollen.
     */
    private  void sendPackets(List<byte[]> data,DatagramSocket socket) throws IOException {
        for (byte[] b : data){
            DatagramPacket packet = new DatagramPacket(b,b.length, address,port);
            socket.send(packet);
        }

    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     * @param file  Die datei die eingelesen werden soll
     */
   private List<byte[]> readData(String file){
        //TODO packete lesen und markierung für anfang und ende anbringen
        return new LinkedList<>();
    }

    /**
     * Prüft ob ein Packet eine Fehler rückgabe hat oder ob es eine normale Rückmeldung ist
     * @param data  Das array das geprüft werden soll
     * @return      true wenn es eine Fehlerrückmeldung ist false ansonsten
     */
    private boolean checkFailure(byte[] data){
        //TODO checke packet auf fehler
        return true;
    }
}
