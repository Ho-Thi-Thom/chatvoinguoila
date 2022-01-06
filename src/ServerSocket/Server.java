package ServerSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author HO_THI_THOM
 */
public class Server {

    private static ServerSocket server = null;
    public static ArrayList<String> users_waitting = new ArrayList<>(); // danh sách user chờ
    public static ArrayList<Room> phongdoi = new ArrayList<>();  // danh sách phòng chờ 
    public static ArrayList<Read_Send> sockets = new ArrayList<>(); // danh sách kết nối client server
    public static ArrayList<Read_Send> users_chat = new ArrayList<>();  // danh sách kết nối chat
    public void Server(int port, int sothread) throws IOException, Exception {
        ExecutorService executor = Executors.newFixedThreadPool(sothread);
        try {
            server = new ServerSocket(port);
            System.out.println("Server waiting for connection at port " + port);
            System.out.println("Waiting for client...");
            while (true) {
                Read_Send client = new Read_Send(server.accept());
                executor.execute(client);
                sockets.add(client);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            if (server != null) {
                server.close();
            }
        }
    }

    public static void main(String[] args) throws IOException, Exception {
//        new Server(5000,5);
        Server server = new Server();
        server.Server(5000, 5);
    }
}
