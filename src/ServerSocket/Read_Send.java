package ServerSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

/**
 *
 * @author HO_THI_THOM
 */
public class Read_Send implements Runnable {

    private Socket socket;
    private String user = null;
    public String user2 = null;
    private String roomId = null;
    public Read_Send socketclient;
    DataInputStream dis = null;
    DataOutputStream dos = null;
    private boolean co = true;

    public Read_Send(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            while (true) {
                if (!co) {
                    break;
                }
                try {
                    String temp = dis.readUTF();
                    switch (temp) {
                        case "thamgia":
                            thamgia(dis.readUTF());
                            break;
                        case "huydoi":
                            huydoi();
                            break;
                        case "chapnhanchat":
                            chapnhan();
                            break;
                        case "tuchoichat":
                            khongchapnhanchat();
                            break;
                        case "ghepdoi":
                            ghepdoi();
                            break;
                        case "message":
                            message();
                            break;
                        case "huychat":
                            refresh();
                            break;
                        case "bihuychat":
                            clearOldChat();
                            break;
                    }
                } catch (IOException ex) {
                    break;
                }
            }
            cleanup();
            System.out.println("User " + user + " disconected");
            System.out.println("User online: " + Server.users_waitting.size());
            dis.close();
            dos.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("");
        }
    }

    private void cleanup() throws IOException {
        cancleChatWhenUserDisconected();
        Server.users_waitting.remove(user);
        Server.sockets.remove(this);
    }

    private void cancleChatWhenUserDisconected() throws IOException {
        for (Room r : Server.phongdoi) {
            if (r.getUser1().equals(user)) {
                r.setAccept1(0);
                System.out.println("user " + user + " disconnected chat");
                break;
            } else if (r.getUser2().equals(user)) {
                r.setAccept2(0);
                System.out.println("user " + user + " disconnected chat");
                break;
            }
        }
    }
private void thamgia(String username) {
        try {
            boolean flag = true;
            for (String user : Server.users_waitting) {
                if (user.toLowerCase().equals(username.toLowerCase())) {
                    System.out.println("Tài khoản đã đăng nhập");
                    dos.writeUTF("tontai");
                    flag = false;
                    break;
                }
            }
            if (flag) {
                Server.users_waitting.add(username);
                user = username;
                System.out.println(user + " đăng nhập thành công");
                dos.writeUTF("doi");
                ghepdoi();
            } else {
                Server.sockets.remove(this);
                co = false;
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void huydoi() throws IOException {
        Server.users_waitting.remove(user);
        Server.sockets.remove(this);
        System.out.println("huydoi");
        co = false;
    }

    private void ghepdoi() throws IOException {
        int size = Server.users_waitting.size();
        if (size == 2) {
            if (Server.users_waitting.get(0).equals(user)) {
                guiyeucau(Server.users_waitting.get(1));
            } else {
                guiyeucau(Server.users_waitting.get(0));
            }
        } else if (size > 2) {
            Random random = new Random();
            int usr2index;
            do {
                usr2index = random.nextInt(size);
            } while (user.equals(Server.users_waitting.get(usr2index)));
            guiyeucau(Server.users_waitting.get(usr2index));
        }
    }

    private void guiyeucau(String user2) throws IOException {
        Server.users_waitting.remove(user2);
        Server.users_waitting.remove(user);
        Room waittingRoom = new Room();
        String id = "Phòng - " + (new Random().nextInt(1000) + 1000);
        waittingRoom.setRoomID(id);
        System.out.println("Room id: " + id);
        for (Read_Send rs : Server.sockets) {
            if (rs.user.equals(user2)) {
                System.out.println("Gửi accept user " + user2);
                guiyeucauchat(rs, id);
                waittingRoom.setUser2(rs.user);
                waittingRoom.setUser1(user);
            }
        }
        Server.phongdoi.add(waittingRoom);
    }

    private void guiyeucauchat(Read_Send client, String roomId) throws IOException {
        client.dos.writeUTF("yeucauchat");
        client.dos.writeUTF(roomId);
        client.dos.writeUTF(user);
        dos.writeUTF("yeucauchat");
        dos.writeUTF(roomId);
        dos.writeUTF(client.user);
    }

    private void chapnhan() throws IOException {
        roomId = dis.readUTF();
        System.out.println("phòng doi" + roomId);
        // kiểm tra user nào click accept bằng roomid
        for (Room r : Server.phongdoi) {
            if (r.getRoomID().equals(roomId)) {
                int currentUser = 0;
                if (r.getUser1().equals(user)) {
                    currentUser = 1;
                    r.setAccept1(1);
                    user2 = r.getUser2();
                    System.out.println("user " + user + " accept");
                } else {
                    r.setAccept2(1);
                    System.out.println("user " + user + " accept");
                    user2 = r.getUser1();
                }
                //1 trong 2 hủy
                if (r.getAccept1() == 0 || r.getAccept2() == 0) {
                    chapnhan_huy(r);
                }
                //cả 2 chấp nhận
                if (r.getAccept1() == 1 && r.getAccept2() == 1) {
                    chapnhan_chapnhan(r, currentUser);
                }
                break;
            }
        }
    }

    private void chapnhan_huy(Room r) throws IOException {
        if (Server.phongdoi.remove(r) && Server.users_waitting.add(user)) {
            System.out.println("Huỷ room: " + r.getRoomID());
            System.out.println("Cho người dùng " + user + " vào lại hàng chờ");
            dos.writeUTF("doiphuongkhongdongy");
            roomId = null;
            user2 = null;
        }
    }

    private void chapnhan_chapnhan(Room r, int currentUser) throws IOException {
        System.out.println("ca 2 user accept");
        if (currentUser == 1) {
            for (Read_Send client : Server.sockets) {
                if (client.user.equals(r.getUser2())) {
                    loadChat(client);
                    break;
                }
            }
        } else {
            for (Read_Send client : Server.sockets) {
                if (client.user.equals(r.getUser1())) {
                    loadChat(client);
                    break;
                }
            }
        }
        Server.phongdoi.remove(r);
    }

    private void khongchapnhanchat() throws IOException {
        roomId = dis.readUTF();
        // kiểm tra user nào click deny bằng roomid
        for (Room r : Server.phongdoi) {
            if (r.getRoomID().equals(roomId)) {
                if (r.getUser1().equals(user)) {
                    r.setAccept1(0);
                    System.out.println("user " + user + " không chấp nhận phòng chat");

                    // kiểm tra user trước đó nhấn accept
                    if (r.getAccept2() == 1) {
                        for (Read_Send client : Server.sockets) {
                            if (client.user.equals(r.getUser2())) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                Server.phongdoi.remove(r);
                                guidongchat(client);
                                break;
                            }
                        }
                    }
                } else {
                    r.setAccept2(0);
                    System.out.println("user " + user + " không chấp nhận phòng chat");
                    // kiểm tra user trước đó nhấn accept
                    if (r.getAccept1() == 1) {
                        for (Read_Send client : Server.sockets) {
                            if (client.user.equals(r.getUser1())) {
                                System.out.println("Huỷ room: " + r.getRoomID());
                                Server.phongdoi.remove(r);
                                guidongchat(client);
                                break;
                            }
                        }
                    }
                }

                System.out.println("Người dùng " + user + " đã thoát phòng chờ");
                roomId = null;
                Server.users_waitting.add(user);
                ghepdoi();
                if (r.getAccept1() == 0 && r.getAccept2() == 0) {
                    Server.phongdoi.remove(r);
                    System.out.println("Huỷ room: " + r.getRoomID());
                }
                break;
            }
        }
    }

    private void guidongchat(Read_Send client) throws IOException {
        Server.users_waitting.add(client.user);
        System.out.println("Thêm user " + client.user + " vào hàng chờ");
        client.dos.writeUTF("doiphuongkhongdongy");
        client.roomId = null;
    }

    private void loadChat(Read_Send client) throws IOException {
        dos.writeUTF("formchat");
        client.dos.writeUTF("formchat");
        socketclient = client;
        for (Read_Send rs : Server.sockets) {
            if (rs.user.equals(client.user)) {
                rs.socketclient = this;
                break;
            }
        }
        Server.users_chat.add(this);
        Server.users_chat.add(client);
    }

    //
    // IN CHAT
    //
    private void message() throws IOException {
        socketclient.dos.writeUTF("dulieubanchat");
        socketclient.dos.writeUTF(dis.readUTF());
    }

    private void refresh() throws IOException {
        socketclient.dos.writeUTF("doiphuonghuychat");
        clearOldChat();
        ghepdoi();
    }

    private void clearOldChat() throws IOException {
        socketclient = null;
        user2 = null;
        roomId = null;
        Server.users_chat.remove(this);
        Server.users_waitting.add(user);
    }

}
