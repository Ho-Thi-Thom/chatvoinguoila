package ClientSocket.Client;

import ClientSocket.GUI.FormChat;
import ClientSocket.GUI.FormCho;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author HO_THI_THOM
 */
public class Read_Send implements Runnable {

    private Socket client;
    public static int key;
    public static boolean tieptuc;
    private DataInputStream dis = null;
    private DataOutputStream dos = null;

    public Read_Send(Socket client) throws IOException {
        this.client = client;
        tieptuc = false;
        dis = new DataInputStream(client.getInputStream());
        dos = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    switch (Client.dis.readUTF()) {
                        case "tontai":
                            key = 0;
                            tieptuc = true;
                            break;
                        case "doi":
                            key = 1;
                            tieptuc = true;
                            break;
                        case "yeucauchat":
                            yn_yeucau();
                            break;
                        case "doiphuongkhongdongy":
                            doiphuongkhongdongy();
                            break;
                        case "formchat":
                            loadchat();
                            break;
                        case "dulieubanchat":
                            message();
                            break;
                        case "doiphuonghuychat":
                            doiphuonghuychat();
                            break;
                    }
                } catch (Exception ex) {
                    break;
                }
            }
            dis.close();
            dos.close();
            client.close();
        } catch (IOException ex) {
            Logger.getLogger(Read_Send.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void yn_yeucau() {
        try {
            String roomId = Client.dis.readUTF();
            String username2 = Client.dis.readUTF();
            System.out.println("Có người kết nối chat !");
            System.out.println("ID Phòng: " + roomId);

            TimeUnit.MILLISECONDS.sleep(4000);
            FormCho.flag = true;
            int temp = JOptionPane.showConfirmDialog(null, "Bạn có muốn chat với " + username2 + " không ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (temp == JOptionPane.YES_OPTION) {
                dos.writeUTF("chapnhanchat");
                System.out.println("Chấp nhận vào phòng");
                FormCho.btnHuy.setEnabled(false);
                Client.user2name = username2;
                Client.idroom = roomId;
            } else {
                dos.writeUTF("tuchoichat");
                System.out.println("Từ chối vào phòng");
                FormCho.flag = false;
                FormCho.waitting();
            }
            dos.writeUTF(roomId);
        } catch (IOException ex) {
        } catch (InterruptedException ex) {
            Logger.getLogger(Read_Send.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doiphuongkhongdongy() throws IOException {
        FormCho.btnHuy.setEnabled(true);
        FormCho.flag = false;
        FormCho.thGian = 0;
        FormCho.waitting();
        JOptionPane.showMessageDialog(null, "Phòng chat bị huỷ do đối phương không chấp nhận !");
        dos.writeUTF("ghepdoi");
    }

    private void loadchat() throws IOException {
        Client.anFormCho();
        Client.taomoiChat();
    }

    private void message() throws IOException {
        FormChat.txtAreaNoiDung.append(Client.user2name + ": " + Client.dis.readUTF() + "\n");
    }

    private void doiphuonghuychat() throws IOException {
        Client.dos.writeUTF("bihuychat");
        JOptionPane.showMessageDialog(null, "Bạn của bạn đã thoát phòng chat!");
        Client.dos.writeUTF("ghepdoi");
        Client.anformChat();
        Client.taomoiFormCho();
    }
}
