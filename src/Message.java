
import java.util.Date;

public class Message {

    private int messageID;
    private static int IDs = 0;
    private UserProfile sender;   // updated from string to userprofile
    private UserProfile reciever; // updated from string to userprofile
    private boolean groupMessage;
    private int groupId;
    private boolean recieved;
    private Object object;
    private messageType type;
    private Date sendDate;
    private Date recieveDate;

    public Message(Message m) {
        this.groupId = m.groupId;
        this.groupMessage = m.groupMessage;
        this.messageID = m.messageID;
        this.object = m.object;
        this.recieveDate = m.recieveDate;
        this.recieved = m.recieved;
        this.reciever = m.reciever;
        this.sendDate = m.sendDate;
        this.sender = m.sender;
        this.type = m.type;
    }

    Message() {

    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public void generateID() {
        messageID = IDs++;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public UserProfile getSender() {
        return sender;
    }

    public void setSender(UserProfile sender) {
        this.sender = sender;
    }

    public UserProfile getReciever() {
        return reciever;
    }

    //The Setters And The Getters
    public void setReciever(UserProfile reciever) {
        this.reciever = reciever;
    }

    public boolean isGroupMessage() {
        return groupMessage;
    }

    public void setGroupMessage(boolean groupMessage) {
        this.groupMessage = groupMessage;
    }

    public boolean isRecieved() {
        return recieved;
    }

    public void setRecieved(boolean recieved) {
        this.recieved = recieved;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public messageType getType() {
        return type;
    }

    public void setType(messageType type) {
        this.type = type;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getRecieveDate() {
        return recieveDate;
    }

    public void setRecieveDate(Date recieveDate) {
        this.recieveDate = recieveDate;
    }

}
