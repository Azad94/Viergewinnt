//package experiment2;

import java.net.*; // we use Sockets

public class FileTransferServerUDPjlibcnds {

    
    public static void main(String args[]) throws Exception{
        // Arguments: port & filename
        int srvPort = Integer.parseInt(args[0]); // server UDP port
        String filename = args[1]; // server Name

	    // Open special datagramm socket from jlibcnds library, do not change this
	    javax.net.DatagramSocket dtgSock;
        dtgSock = new javax.net.DatagramSocket(srvPort);

        byte[] buf = new byte[4000];

	    java.io.FileOutputStream fw = new java.io.FileOutputStream(filename);

	    DatagramPacket packet = new DatagramPacket(buf, buf.length);

	    while (true){
		    dtgSock.receive(packet);
		    System.out.print("*");
		    // if receive an empty packet will indicate end of file
		
		    if (packet.getLength()==0) break;
            fw.write(packet.getData(),0,packet.getLength());
		    //fw.flush();
	    }
	    fw.flush();
	    fw.close();
	    dtgSock.close();	// Close the Socket
    }
}
