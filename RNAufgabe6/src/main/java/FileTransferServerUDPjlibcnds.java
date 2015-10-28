//package experiment2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;

public class FileTransferServerUDPjlibcnds {

    private static final int BUFSIZESEND=508;

    public static void main(String args[]) throws Exception{
        if (args.length!=2){
            System.err.println("Bitte gebe einen Port und einen Dateipfad an in den args!!");
            System.exit(-1);
        }else {
            int port = Integer.parseInt(args[0]);
            String filePath = args[1];
            List<byte[]> recived;
            try (DatagramSocket socket = new DatagramSocket(port)){
                do {
                    recived=receivePackets(socket);
                }while (recived.isEmpty());
                while (true){
                    sendPackets(readData(filePath),socket);
                }
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * Empfängt die packet und wirft ordnet diese in Byte [] ein.
     * @param socket    Socket auf dem die Daten empfangen werden
     * @return          Eine liste an packeten die ampfangen wurden
     *
     */
    public static List<byte[]> receivePackets(DatagramSocket socket) throws IOException {
        List<byte[]> list = new LinkedList<>();
        DatagramPacket packet = new DatagramPacket(new byte[BUFSIZESEND],BUFSIZESEND);
        while (true){
            socket.receive(packet);
            if(packet.getLength()==0) break;
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
    public static void sendPackets(List<byte[]> data,DatagramSocket socket){
        //TODO sende packete
    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     * @param file  Die datei die eingelesen werden soll
     */
    public static List<byte[]> readData(String file){
        //TODO packete lesen und markierung für anfang und ende anbringen
        return new LinkedList<>();
    }

    /**
     * Prüft ob ein Packet eine Fehler rückgabe hat oder ob es eine normale Rückmeldung ist
     * @param data  Das array das geprüft werden soll
     * @return      true wenn es eine Fehlerrückmeldung ist false ansonsten
     */
    private static boolean checkFailure(byte[] data){
        //TODO checke packet auf fehler
        return true;
    }
}
