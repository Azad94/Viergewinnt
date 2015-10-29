//package experiment2;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

public class FileTransferServerUDPjlibcnds {

    private static final int BUFSIZE = 508;
    private static final int BUFSIZESEND=8;
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
         * 
         */
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
    private List<String> readData(String file) {
        StringBuilder buffer = new StringBuilder(BUFSIZESEND);
        List<String> list = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true){
                for (int i = 0; i < BUFSIZESEND; i++) {
                    buffer.append(reader.read());
                }
                if (buffer.toString()==""){
                    break;
                }
                list.add(buffer.toString());
                buffer.delete(0,buffer.length());
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
