//package experiment2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class FileTransferClientUDPjlibcnds {


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


    private static void serverRoutine(int port, String host, String file) {

    }

    /**
     * Schreibt die daten in eine Datei
     *
     * @param data Die daten die geschrieben werden sollen
     */
    private static void writeData(List<byte[]> data, String filePAth) {
        try {
            Writer writer = new FileWriter(new File(filePAth));
            for (byte[] b : data) {
                writer.append(new String(b));
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
     * @param packageNumber Welches packet wird Überprüft anzahl der übertragenden packete
     * @return Die nummer des Packets oder 0 wenn das Packet einen fehler enthält
     */
    private static int checkFailure(byte[] bytes, int packageNumber) {
        boolean check = false;
        int flagBegin = bytes.length - 3;
        if(((int) bytes[flagBegin]) != 0b01111110) check = true;
        if(((int) bytes[++flagBegin]) != flagBegin) check = true;
        if(packageNumber != ((int) bytes[++flagBegin])) check = true;

        return check?0:((byte) bytes[bytes.length - 1]);
    }
}
