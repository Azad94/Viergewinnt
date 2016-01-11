import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by sven on 11.01.16.
 */
public class Server {

    public static void main(String args[]){

        int port = Integer.parseInt(args[0]);
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            byte[] bytes = new byte[1000];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = 'a';
            }
            out.writeInt(1000);
            out.write(bytes,0,bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
