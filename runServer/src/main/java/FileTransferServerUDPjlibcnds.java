//package experiment2;

import java.io.*;
import java.net.*; // we use Sockets

public class FileTransferServerUDPjlibcnds {


	private static final String SOURCESMALL = "/home/sven/IdeaProjects/runServer/src/main/java/source_small.txt";
	private static final String SOURCEBIG = "/home/sven/IdeaProjects/runServer/src/main/java/source_large.txt";


	public static void main(String args[]) throws Exception{
		// Arguments: port & filename
		int srvPort = Integer.parseInt(args[0]); // server UDP port
		String filename = args[1]; // server Name
		int counterEntry=0;
		int counterFile = 0;

		// Open special datagramm socket from jlibcnds library, do not change this
		javax.net.DatagramSocket dtgSock;
		dtgSock = new javax.net.DatagramSocket(srvPort);

		byte[] buf = new byte[4000];

		java.io.FileOutputStream fw = new java.io.FileOutputStream(filename);
		Writer writer = new FileWriter("messungenPackete.txt",true);

		DatagramPacket packet = new DatagramPacket(buf, buf.length);

		while (true){
			try {
				dtgSock.setSoTimeout(10000);
				dtgSock.receive(packet);
				// if receive an empty packet will indicate end of file
				String data = new String(packet.getData());
				System.out.printf(data);
				if (packet.getLength() == 0) {
					System.out.println("Leer");
					break;
				} else {
					fw.write(packet.getData(), 0, packet.getLength());
					String s = new String(packet.getData());
					writer.append(s);
					counterEntry++;
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
				writer.flush();
				writer.close();
				break;
			}
			//fw.flush();
		}
		File file = new File(SOURCESMALL);
		byte[] by = new byte[8];
		FileInputStream fileInputStream = new FileInputStream(file);
		int len=0;
		while ((len=(fileInputStream.read(by,0,by.length)))!=-1){
			counterFile++;
		}
		fw.flush();
		fw.close();
		dtgSock.close();	// Close the Socket
		try (Writer writer2 = new FileWriter("messungen.txt",true)){
			writer2.append("lost:" + String.valueOf(counterFile - counterEntry) + "\n");
		}catch (EOFException e){
			System.out.println(e.getMessage());
		}

	}
}
