//package experiment2;

import java.net.*; // we use Sockets
import java.util.List;

public class FileTransferServerUDPjlibcnds {

    private static final int BUFSIZESEND=508;
    private DatagramSocket socket;
    private String file;

    public static void main(String args[]) throws Exception{

    }

    /**
     * Empfängt die packet und wirft ordnet diese in Byte [] ein.
     */
    public static void receivePackets(){

    }

    /**
     * Schickt die packete an eine gewünschte adresse
     * @param data  Die daten die verschickt werdne sollen
     */
    public static void sendPackets(List<byte[]> data){

    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     * @param file  Die datei die eingelesen werden soll
     */
    public static void readData(String file){

    }

    /**
     * Prüft ob ein Packet eine Fehler rückgabe hat oder ob es eine normale Rückmeldung ist
     * @param data  Das array das geprüft werden soll
     * @return      true wenn es eine Fehlerrückmeldung ist false ansonsten
     */
    private static boolean checkFailure(byte[] data){

    }
}
