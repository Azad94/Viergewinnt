//package experiment2;

import java.io.*;
import java.net.*; // we use Sockets
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTransferServerUDPjlibcnds {

    private static final String SOURCESMALL = "/home/sven/IdeaProjects/runServer/src/main/java/source_small.txt";
    private static final String SOURCEBIG = "/home/sven/IdeaProjects/runServer/src/main/java/source_large.txt";

    
    public static void main(String args[]) throws Exception{
        // Arguments: port & filename
        int srvPort = Integer.parseInt(args[0]); // server UDP port
        String filename = args[1]; // server Name
        int counterBitFailure=0;

	    // Open special datagramm socket from jlibcnds library, do not change this
	    javax.net.DatagramSocket dtgSock;
        dtgSock = new javax.net.DatagramSocket(srvPort);

        byte[] buf = new byte[8];
        byte[] buf2 = new byte[8];

	    FileOutputStream fw = new FileOutputStream(filename);
        Writer writer = new FileWriter("messungenPacketeBit.txt",true);
        FileInputStream arrays = new FileInputStream(new File(SOURCESMALL));


	    DatagramPacket packet = new DatagramPacket(buf, buf.length);

	    while (true){
            try {
                dtgSock.setSoTimeout(10000);
                dtgSock.receive(packet);
                // if receive an empty packet will indicate end of file
                if (packet.getLength() == 0) {
                    System.out.println("Leer");
                    break;
                } else {
                    //Data erhalten schreibe in einen String zum vergleichen
                    String data = new String(packet.getData());
                    //Schreibe die daten in eine Datei
                    fw.write(packet.getData());
                    writer.append(new String(packet.getData()));
                    //Schleife um auch nur die gleichen rahmen zu vergleichen z.B. nicht ***** mit 11111
                    //Schreibe daten von der source datei in buf2
                    while ((arrays.read(buf2,0,buf2.length))!=-1){
                        //Wenn der erste Character nicht im Array bu2 auftaucht verschieden rahmen nächste reihe lesen
                        if(new String(buf2).contains(data.charAt(0)+"")){
                            int bitFailure;
                            //Speichere die ausgabe des vergleiches der beiden Bytearrays
                            bitFailure=Tools.countBitErrorsInByteArray(buf2,packet.getData());
                            //Nur wenn es einen Bitfehler gab auch addieren
                            if (bitFailure>0){
                                counterBitFailure+=bitFailure;
                            }
                            //raus aus der schleife rahmen wurde verglichen nächstes packet lesen.
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                writer.flush();
                writer.close();
                break;
            }
            //fw.flush();
	    }
        fw.flush();
	    fw.close();
	    dtgSock.close();	// Close the Socket
        //Dieser block schreibt nur die fehler in eine Datei
        try (Writer writer2 = new FileWriter("messungenBitFailure.txt",true)){
            writer2.append("lost:" + String.valueOf(counterBitFailure) + "\n");
        }catch (EOFException e){
            System.out.println(e.getMessage());
        }

    }
}
