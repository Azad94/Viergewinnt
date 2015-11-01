//package experiment2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class FileTransferClientUDPjlibcnds {
    private static final int timeOut=10000;
    private static final int BUFSIZERECEIVE=11;


    public static void main(String args[]) throws Exception {
        if (args.length != 3) {
            System.err.println("Bitte geben sie Host,Port und filepath ein");
            System.exit(-1);
        } else {
            int port = Integer.valueOf(args[1]);
            String host = args[0];
            String filePath = args[2];
            serverRoutine(port, host, filePath);
        }


    }


    /**
     * Ist für die eigentliche Client Logik zuständig heißt ein und ausgabe
     * @param port  Port für das senden und empfangen von daten
     * @param host  Der host an den wir senden
     * @param file  Die Datei in die wir schreiben werden
     */
    private static void serverRoutine(int port, String host, String file) {
        UDPSocket udp;
        boolean failure;
        List<String> list;
        try {
            udp = new UDPSocket(port, host,timeOut);
            list = new LinkedList<>();
            byte[] bytes = new byte[BUFSIZERECEIVE];
            int packageNumber=0;
            udp.send("");
            while (true){
                try {
                    bytes=udp.receive(BUFSIZERECEIVE);
                    System.out.println(new String(bytes));
                    if (bytes.length==0) break;
                    failure=false;
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    failure=true;
                }
                if ((packageNumber=checkFailure(bytes))>=0&&!failure){
                    if (packageNumber==list.size()) {
                        list.add(encodeData(bytes));
                    }
                    udp.send(0b10000001+"");
                }else{
                    udp.send(0b01111110+"");
                }
            }
            udp.closeSocket();
            writeData(list,file);

            } catch (IOException e) {
                e.printStackTrace();
        }

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
                writer.append(s);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encodiert den Rahmen heißt die flags werden aus dem Rahmen entnommen
     *
     * @param data Der Rahmen der Encodiert wird
     * @return Den Rahmen ohne Flags
     */
    private static String encodeData(byte[] data) {
        StringBuilder builder = new StringBuilder(data.length-3);
        for (int i = 0; i < data.length-4; i++) {
            builder.append(data[i]);
        }
        return builder.toString();
    }

    /**
     * Überprüft ob ein Rahmen nicht richtig übersendet wurde in dem es nur die Flag bytes überprüft
     *
     * @param bytes         Der Rahmen der überprüft wird
     * @return Die nummer des Packets oder 0 wenn das Packet einen fehler enthält
     */
    private static int checkFailure(byte[] bytes) {
        boolean check = false;
        int flagBegin = bytes.length - 3;
        if(((int) bytes[flagBegin]) !=(byte) 0b01111110) check = true;
        if(((int) bytes[++flagBegin]) !=(byte) flagBegin) check = true;

        return check?-1:((int)bytes[bytes.length - 1]);
    }
}
