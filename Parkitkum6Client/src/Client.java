import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by sven on 11.01.16.
 */
public class Client {

    public static void main(String args[]){
        int port = Integer.parseInt(args[1]);
        String name = args[0];
        try {
            Socket socket = new Socket(name,port);
            DataInputStream reader = new DataInputStream(socket.getInputStream());
            int len = reader.readInt();
            byte[] bytes = new byte[len];
            if (len>0) {
                reader.readFully(bytes);
                System.out.println(new String(bytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
