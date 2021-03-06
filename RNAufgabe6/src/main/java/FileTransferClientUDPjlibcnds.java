//package experiment2;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class FileTransferClientUDPjlibcnds {

    private static final int BUFSIZE = 508;
    private static final int BUFSIZESEND=8;
    private static final int TIMEOUT=100000;

    public static void main(String args[]) throws Exception {
        if (args.length != 3) {
            System.err.println("Bitte gebe einen Port, einen Dateipfad und einen Host an in den args!!");
            System.exit(-1);
        } else {
            int port = Integer.parseInt(args[0]);
            String filePath = args[1];
            String host = args[2];
            System.out.println("Starte Server");
            serverRoutine(port,filePath,host);
        }

    }

    /**
     * Kümmert sich um den Ablauf des servers
     */
    public static void serverRoutine(int port,String filePAth,String host) {
        UDPSocket udp;
        boolean failureReceive;

        List<String> list;
        String buffer="";
        try {
            udp = new UDPSocket(port,host, TIMEOUT);
            try {
                list= readData(filePAth);
                int i=0;
                //sende packete und empfange sie
                while (i<list.size()){
                    udp.send(list.get(i),i);
                    Thread.sleep(500);
                    try {
                        buffer=udp.receive(BUFSIZE);
                        failureReceive=false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        failureReceive=true;
                    }
                    if (!checkFailure(buffer)&&!failureReceive){
                        i++;
                    }
                }
                udp.send("",0);

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                udp.closeSocket();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }


    /**
     * Liest die Daten von einer Datei und speichert diese in einer Liste als byte[]
     *
     *
     * @param file Der Dateipfad zu der Datei die eingelesen werden soll
     */
    private static List<String> readData(String file) {
        List<String> list = new LinkedList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while (true){
                list.add(reader.readLine());
                if (null==((list.get(list.size()-1)))){
                    list.remove(list.size()-1);
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
     */
    private static boolean checkFailure(String s) {
        try {
            if (Integer.parseInt(s)==0b10000001) return false;
            else if (Integer.parseInt(s)==0b01111110) return true;
        }catch (Exception e){
        }
        return true;
    }

}
