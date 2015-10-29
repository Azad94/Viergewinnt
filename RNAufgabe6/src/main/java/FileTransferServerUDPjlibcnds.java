//package experiment2;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FileTransferServerUDPjlibcnds {

    private static final int BUFSIZE = 508;
    private static final int BUFSIZESEND=8;

    public static void main(String args[]) throws Exception {
        if (args.length != 3) {
            System.err.println("Bitte gebe einen Port und einen Dateipfad an in den args!!");
            System.exit(-1);
        } else {
            int port = Integer.parseInt(args[0]);
            String filePath = args[1];
            String host = args[2];
            serverRoutine(port,filePath,host);
        }

    }

    /**
     * Kümmert sich um den Ablauf des servers
     */
    public static void serverRoutine(int port,String filePAth,String host) {
        UDPSocket udp = new UDPSocket(port,host,500);
        List<String> list;
        String buffer="";
        try {
            list= readData(filePAth);
            //Empfange leeres Packet vom client
            while (buffer.isEmpty()){
                buffer=udp.receive(1);
            }
            int i=0;
            while (i<list.size()-1){
                udp.send(list.get(i));
                buffer=udp.receive(BUFSIZE);
                while (checkFailure(buffer.getBytes())){
                    udp.send(list.get(i));
                    buffer = udp.receive(BUFSIZE);
                }
                i++;
            }
            udp.send("");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     *
     *
     * @param file Der Dateipfad zu der Datei die eingelesen werden soll
     */
    private static List<String> readData(String file) {
        StringBuilder buffer = new StringBuilder(BUFSIZESEND);
        List<String> list = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true){
                for (int i = 0; i < BUFSIZESEND; i++) {
                    buffer.append(reader.read());
                }
                if ("".equals(buffer.toString())){
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
    private static boolean checkFailure(byte[] data) {
        int zeros=0;
        int ones=0;
        for (byte b : data){
            if(b==0){
                zeros++;
            }else ones++;
        }
        return zeros > ones;
    }
}
