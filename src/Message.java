import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Message implements Serializable {

    public Message()
    {

    }
    public Message(ArrayList<Object> list)
    {
        setSenderEmail((String) list.get(1));
        setRecieverEmail((String) list.get(2));
        setObject(list.get(3));
        setSendDate((Date) list.get(4));
    }
    private String messageID;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    private String senderEmail;
    private String recieverEmail;
    private boolean groupMessage;
    private Object object;
    private messageType type;
    private Date sendDate;
    private Date recieveDate;


    //The Setters And The Getters


    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecieverEmail() {
        return recieverEmail;
    }

    public void setRecieverEmail(String recieverEmail) {
        this.recieverEmail = recieverEmail;
    }

    public boolean isGroupMessage () {
        return groupMessage;
    }

        public void setGroupMessage ( boolean groupMessage){
        this.groupMessage = groupMessage;
    }

        public Object getObject () {
        return object;
    }

        public void setObject (Object object){
        this.object = object;
    }

        public messageType getType () {
        return type;
    }

        public void setType (messageType type){
        this.type = type;
    }

        public Date getSendDate () {
        return sendDate;
    }

        public void setSendDate (Date sendDate){
        this.sendDate = sendDate;
    }

        public Date getRecieveDate () {
        return recieveDate;
    }

        public void setRecieveDate (Date recieveDate){
        this.recieveDate = recieveDate;
    }

}
