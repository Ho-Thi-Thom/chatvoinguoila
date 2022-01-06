package ServerSocket;

/**
 *
 * @author HO_THI_THOM
 */
public class Room {
    private String roomID;
    private String user1;
    private String user2;
    private int Accept1;
    private int Accept2;

    public Room() {
       Accept1 = -1;
       Accept2 = -1;
    }

    public Room(String user1, String user2, String roomId) {
        this.user1 = user1;
        this.user2 = user2;
        this.roomID = roomId;
        Accept1 = -1;
        Accept2 = -1;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public int getAccept1() {
        return Accept1;
    }

    public void setAccept1(int Accept1) {
        this.Accept1 = Accept1;
    }

    public int getAccept2() {
        return Accept2;
    }

    public void setAccept2(int Accept2) {
        this.Accept2 = Accept2;
    }

    
}
