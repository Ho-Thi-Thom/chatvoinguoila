package ClientSocket.Client;

import ClientSocket.GUI.DangNhap;
import ClientSocket.GUI.FormChat;
import ClientSocket.GUI.FormCho;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author HO THI THOM
 */
public class Client {

    private String host;
    private int port;
    public static Socket client;
    public static DataInputStream dis;
    public static DataOutputStream dos;
    public static Read_Send read;
    public static DangNhap dangnhap;
    public static FormCho formcho;
    public static FormChat formchat;
    public static String user = null;
    public static String user2name = null;
    public static String idroom = null;
    public static int tmp = -1;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect(String nickname) {
        try {
            client = new Socket(host, port);
            System.out.println("Kết nối thành công ! ");
            dis = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Read_Send(client));
            dos.writeUTF("thamgia");
            dos.writeUTF(nickname);
            tmp = 1;
        } catch (Exception ex) {
            tmp = 0;
        }
    }
public static void hienthiDangNhap() {
        if (dangnhap == null) {
            dangnhap = new DangNhap();
        }
        dangnhap.setVisible(true);
    }

    public static void anDangNhap() {
        if (dangnhap != null) {
            dangnhap.setVisible(false);
        }
    }

    public static void hienthiFormCho() {
        if (formcho == null) {
            formcho = new FormCho();
        }
        formcho.setVisible(true);
    }

    public static void taomoiFormCho() {
        formcho = new FormCho();
        formcho.setVisible(true);
    }

    public static void anFormCho() {
        if (formcho != null) {
            formcho.setVisible(false);
        }
    }

    public static void hienformChat() {
        if (formchat == null) {
            formchat = new FormChat();
        }
        formchat.setVisible(true);
    }

    public static void anformChat() {
        if (formchat != null) {
            formchat.setVisible(false);
        }
    }

    public static void taomoiChat() {
        formchat = new FormChat();
        formchat.setVisible(true);
    }

    public static int play() {
        try {
            dos.writeUTF("PLAY_CHAT");
            System.out.println("Chờ phòng...");
            return 1;
        } catch (IOException ex) {
            return 0;
        }
    }

    public static int cancle() {
        try {
            dos.writeUTF("CANCLE_CHAT");
            System.out.println("Huỷ chờ");
            return 1;
        } catch (IOException ex) {
            return 0;
        }
    }
  
    public static void taomoiDangNhap() {
        DangNhap dangnhap = new DangNhap();
        dangnhap.setVisible(true);
    }

    public static boolean while_true() {
        try {
            while (true) {
                TimeUnit.MILLISECONDS.sleep(500);
                if (Read_Send.tieptuc) {
                    break;
                }
                System.out.println("waitting...");
            }
            Read_Send.tieptuc = false;
        } catch (InterruptedException ex) {
        }
        return true;
    }
}
