
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;
import javafx.util.Pair;

public class Server {

    private ObjectOutputStream output;
    private ObjectInputStream input;
    private ServerSocket server;

    private ArrayList<UserProfile> list = new ArrayList<>();
    private ArrayList<Message> messagelist;
    private ArrayList<Group> groupsList;

    private Socket connection;
    boolean check = true;
    private String emailsFileLocation = "h" + "angeFile";
    private String hangedMessagesFileLocation = "hangedMessages";
    private String groupsFileLocation = "groupsFile";

    public HashMap<String, HandleThread> onlineUsers = new HashMap<>();

    public Server() throws IOException, ClassNotFoundException {
        emailsFileLocation = "hangeFile";
        hangedMessagesFileLocation = "hangedMessages";
        groupsFileLocation = "groupsFile";

        ObjectInputStream emailStream;
        ObjectInputStream messagesStream;
        ObjectInputStream groupStream;
        File file1 = new File(emailsFileLocation);
        File file2 = new File(hangedMessagesFileLocation);
        File file3 = new File(groupsFileLocation);

        if (!file1.exists()) {
            file1.createNewFile();
        }
        if (!file2.exists()) {
            file2.createNewFile();
        }
        if (!file3.exists()) {
            file3.createNewFile();
        }

        System.out.println("file1" + file1.exists());
        System.out.println("file2" + file2.exists());
        System.out.println("file3" + file3.exists());

        FileInputStream emailFile = new FileInputStream(file1);
        FileInputStream messagesFile = new FileInputStream(file2);
        FileInputStream groupFile = new FileInputStream(file3);

        if (file1.exists()) {
            emailStream = new ObjectInputStream(emailFile);
            list = (ArrayList<UserProfile>) emailStream.readObject(); //nserat
        }

        //  if(file1.exists()) {
        //        emailStream = new ObjectInputStream(emailFile);
        //    list = (ArrayList<UserProfile>) emailStream.readObject(); //nserat
        //   System.out.println("list:"+list.get(0).getEmail());
        //     }
        //  if(file2.exists())
        //   if(file3.exists())
        //System.out.println("akakaka"+emailFile.available());
        /* if(emailFile.available() == 1)
        {

            emailStream = new ObjectInputStream(emailFile);
         //   while(file1.canRead()) {
                list = (ArrayList<UserProfile>) emailStream.readObject(); //nserat
              //  list.add((UserProfile)emailStream.readObject());  //here

            }*/
        //   list.add((UserProfile)emailStream.readObject());  //here
        // }
        /*   if(messagesFile.available() == 1)
        {
            messagesStream = new ObjectInputStream(messagesFile);
            messagelist = (ArrayList<Message>) messagesStream.readObject();
        }
        if(groupFile.available() == 1)
        {
            groupStream = new ObjectInputStream(groupFile);
            groupsList = (ArrayList<Group>) groupStream.readObject();
        }*/
    }

    public boolean addUser(UserProfile user) {
        if (list == null) {
            list.add(user);
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getEmail().equals(user.getEmail())) {
                    return false;
                }
            }
            list.add(user);
        }
        System.out.println("list is: ababa" + list.toString());  ///////////
        return true;
    }

    public boolean checkIfUserNameAvailable(String userName) {
        for (UserProfile er : list) {
            if (userName == er.getName()) {
                return false;
            }
        }
        return true;
    }

    public boolean deleteUser(UserProfile user) {
        if (checkIfUserNameAvailable(user.getName())) {
            return false;
        }
        list.remove(user);
        user = null;
        return true;

    }

    public UserProfile updateUser(UserProfile user) {

        if (checkIfUserNameAvailable(user.getName())) {//h
            return null;
        }
        for (UserProfile er : list) {
            if (er.getEmail() == user.getEmail()) {
                list.remove(er);
                list.add(user);
                return user;
            }
        }
        return null;

    }

    public void rejectRequest(ArrayList<Object> list) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list.get(0));
        arrayList.add(false);
        output.writeObject(arrayList);
        output.flush();
    }

    public void successfulRequest(ArrayList<Object> list) throws IOException {  //HERE WE SEND THE DATA TO CLIENT
        ArrayList<Object> arrayList = new ArrayList<>();
//        UserProfile profile = serilaizeProfileObject(list);
//        addUser(profile);//مالا طعمة
        arrayList.add(list.get(0));
        arrayList.add(true);
        arrayList.add("abc123");//Test Id      // MUST create valid IDs !!
        output.writeObject(arrayList);
        output.flush();
    }

    public void successfulRequestForSignIn(ArrayList<Object> newList, HandleThread thread) throws IOException {

        ArrayList<Object> sendList = new ArrayList<>();

        sendList.add(1);
        sendList.add(true);
        sendList.add(thread.getUserProfile());
        thread.getOutput().writeObject(sendList);
        thread.getOutput().flush();

    }

    public void successfulRequestForFriendList(ArrayList<Object> lists, HandleThread thread) throws IOException {


        ArrayList<Object> arrayList = new ArrayList<>(), tempFriendList = new ArrayList<>();
        arrayList.add(lists.get(0));
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < thread.getUserProfile().getUserFriends().size(); j++) {
                tempFriendList.add(list.get(i));  //// UPDATED: We send the entire user Profile NOW
            }                                     // so when you want to send a message send the entire user again as a Reciever
            if (tempFriendList.size() == 0) {
                arrayList.add(true);
            } else {
                arrayList.add(true);
                arrayList.add(tempFriendList);
            }
        }
        output.writeObject(arrayList);
        output.flush();
    }


    public boolean createGroup(ArrayList<Object> list2, HandleThread thread) {
        String groupName = (String) list2.get(1);
        ArrayList<UserProfile> partners = (ArrayList<UserProfile>) list2.get(2);
        UserProfile admin = thread.userProfile;
        if (partners.size() > 1) {
            Group group = new Group(groupName, partners, admin);
            group.generateId();
            groupsList.add(group);
            return true;
        }
        return false;
    }




    public boolean updateGroup(Group groups) {
        if (checkIfGroupNameAvaillabe(groups.getName())) {
            return false;
        }
        for (Group gr : groupsList) {
            if (gr.getId() == groups.getId()) {
                groupsList.remove(gr);
                groupsList.add(groups);

            }
        }
        return false;
    }

    public boolean checkIfGroupNameAvaillabe(String groupName) {
        for (Group gr : groupsList) {
            if (groupName == gr.getName()) {
                return false;
            }
        }
        return true;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void hangMessage(Message message) {
        if (!message.isGroupMessage()) {
            messagelist.add(message);
        } else {
            for (int i = 0; i < groupsList.size(); i++) {
                if (groupsList.get(i).getId() == message.getGroupId()) {
                    for (int j = 0; j < groupsList.get(i).getMembers().size(); j++) {
                        if (!message.getSender().equals(groupsList.get(i).getMembers().get(j).getKey())) {
                            UserProfile reciever = (UserProfile) groupsList.get(i).getMembers().get(j).getKey();
                            Message newMessage = new Message(message);
                            newMessage.setReciever(reciever);
                            messagelist.add(newMessage);
                        }
                    }
                }
            }
        }
    }

    public void sendUserMessages(ArrayList<Message> messages) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
//        UserProfile profile = serilaizeProfileObject(list);
//        addUser(profile);//مالا طعمة
        arrayList.add(list.get(0));
        arrayList.add(true);
        arrayList.add(messages);
        output.writeObject(arrayList);
        output.flush();
    }

    //    public Message freeMessage(int messageId) {
//
//        for (Message message : messagelist) {
//            if (message.getMessageID() == (messageId)) {
//                messagelist.remove(message);
//                return message;
//            }
//        }
//        return null;
//    }
    public ArrayList<Message> searchInHangedMessages(HandleThread thread) {
        ArrayList<Message> userMessages = new ArrayList<>();
        for (Message message : messagelist) {
            if (message.getReciever().equals(thread.userProfile)) {
                userMessages.add(message);
                messagelist.remove(message);

            }
        }
        return userMessages;
    }

    public UserProfile serilaizeProfileObject(ArrayList<Object> arrayList) {
        UserProfile userProfile = new UserProfile();

        userProfile.setUserName((String) arrayList.get(1));
        userProfile.setEmail((String) arrayList.get(2));
        userProfile.setPassword((String) arrayList.get(3));
        userProfile.setBirthDate((Date) arrayList.get(4));
        userProfile.setJoinDate((Date) arrayList.get(5));
        userProfile.setUserId("abc123");

        return userProfile;
    }

    public UserProfile serilaizeSignInInfo(ArrayList<Object> arrayList) {  //here

        UserProfile userProfile = new UserProfile();
        userProfile.setEmail((String) arrayList.get(1));
        userProfile.setPassword((String) arrayList.get(2));

        return userProfile;

    }

    //    public Group serilaizegroupObject(ArrayList<Object> arrayList) {
//        Group group = new Group();
//
//        group.setName((String) arrayList.get(1));
//        group.setId("aa25");
//
//        return group;
//    }
    public Message serilaizemessageObject(ArrayList<Object> arrayList, HandleThread thread) {
        Message message = new Message();
        message.generateID();
        message.setSender(thread.userProfile);
        message.setType((messageType) arrayList.get(1));
        message.setObject(arrayList.get(2)); // the message
        message.setGroupMessage((boolean) arrayList.get(3));
        if (message.isGroupMessage()) {
            message.setGroupId((int) arrayList.get(4));
        } else {
            message.setReciever((UserProfile) arrayList.get(4));
        }

        return message;
    }

    public UserProfile searchInUsersList(String Email) {
        System.out.println("list is:" + list);
        System.out.println("size is:" + list.size());
        //System.out.println("list[2]:"+list.get(2).getEmail());
        int i = 0;
        for (UserProfile profile : list) {
            System.out.println("Email is:" + list.get(i).getEmail());
            if (profile.getEmail().equals(Email)) {
                return profile;
            }
        }
        return null;
    }

    public void handleRequest(ArrayList<Object> list2, HandleThread thread) throws IOException {
        int type = (int) list2.get(0);
        switch (type) {
            case 0://Add user
            {
                UserProfile userProfile = serilaizeProfileObject(list2);
                if (!addUser(userProfile)) {
                    rejectRequest(list2);
                } else {
                    thread.setUserProfile(userProfile);
                    successfulRequest(list2);
                }
                break;
            }
            case 1://sign in
            {
                try {
                    //   System.out.println("abababa");
                    UserProfile userProfile = serilaizeSignInInfo(list2);
                    UserProfile profile = logIn(userProfile);
                    if (profile != null) {
                        successfulRequestForSignIn(list2, thread);
                        thread.setUserProfile(profile);
                        //   onlineUsers.put((String) list2.get(1),thread);
                    } else {
                        rejectRequest(list2);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //  successfulRequest(list2);

                break;
            }
            case 2: //DELETE_USER
            {
                deleteUser(thread.getUserProfile());
                successfulRequest(list2);
                break;
            }
            case 3: //UPDATE_USER
            {
                UserProfile userProfile = serilaizeProfileObject(list2);
                UserProfile user = updateUser(userProfile);
                if (user != null) {
                    thread.setUserProfile(user);
                    successfulRequest(list2);
                } else {
                    rejectRequest(list2);
                }
                break;
            }
            case 4: //Send The list user
            {

                successfulRequestForFriendList(list2, thread);

                break;
            }
            case 5: //Edit Info of the User
            {
                if (list != null) {
                    UserProfile profile = CheckTheUserList(list2);
                    if (profile != null) {
                        successfulRequestForEditInfo(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 6: {  //To Add Friend
                if (list != null) {
                    boolean bool = CheckUserListFriend(list2, thread);
                    if (bool) {
                        successfulRequestForAddFriend(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 7: // To Remove Friend
            {
                if (list != null) {
                    boolean bool = CheckUserListFriendOfUser(list2, thread);

                    if (bool) {
                        successfulRequestForRemoveFriend(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 8: // Block Friend
            {
                if (list != null) {
                    boolean bool = CheckUserListFriendOfUserToBlock(list2);

                    if (bool) {
                        successfulRequestForBlockFriend(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }

                break;
            }
            case 9: //To Send Story Of ListFriend
            {
                break;
            }

            case 10: //To UnBlock The User
            {
                if (list != null) {
                    boolean bool = CheckUserListFriendOfUserToRemoveBlock(list2);

                    if (bool) {
                        successfulRequestForBlockFriend(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }

                break;
            }
            case 11: //To Send The Specific List Friend To user :)
            {
                if (list != null) {
                    SuccessfulUserListFriendSpecific(list2);
                } else {
                    rejectRequest(list2);
                }

                break;
            }
            case 12: // create Group // data: groupName, partnersName ; where partnerName count > 1
            {
                if (list != null) {
                    if (createGroup(list2, thread)) {
                        successfulRequest(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 13: // add Partners  // data : groupID, partnersname;
            {
                if (list != null) {
                    if (addPartnersToGroup(list2, thread)) {
                        successfulRequest(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 14: // delete Partners  // data : groupID, partnersname;
            {
                if (list != null) {
                    if (removePartnersFromGroup(list2, thread)) {
                        successfulRequest(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 15: // leave Groupe // data: groupID
            {
                if (list != null) {
                    leaveGroup(list2, thread);
                    successfulRequest(list2);
                }
                break;
            }
            case 16: // delete Group // data: groupID
            {
                if (list != null) {
                    if (deleteGroup(list2, thread)) {
                        successfulRequest(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 17: // set Admins // Data: groupID , partnersName, boolean array(setAdmins or removeAdmins)
            {
                if (list != null) {
                    if (setGroupAdmins(list2, thread)) {
                        successfulRequest(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 18: //SEND_MESSAGE   //data: MessageType<messageType>, Message<Object>, GroupMessage<Boolean>, Reciver<UserProfile> (if GroupMessage = False), GroupID<int> (if GroupMessage = True)
            {
                Message message = serilaizemessageObject(list2, thread);
                hangMessage(message);
                successfulRequest(list2);
                break;
            }
            case 19: //CHECK_HANGED_MESSAGES
            {
//                Message message = serilaizemessageObject(list2);
                ArrayList<Message> userMessages = searchInHangedMessages(thread);
                if (userMessages.size() == 0) {
                    rejectRequest(list2);
                } else {
                    sendUserMessages(userMessages);

                }
                break;
            }
            /*  last case !!!
            case 12:
            {
                String senderEmail;
                String recieverEmail;
                Object messageContent;

                for(String email:onlineUsers.keySet())
                {
                    if(email.equals(recieverEmail))
                }
            }



           /* case 9: //DELETE_GROUP
            {
                Group group = serilaizegroupObject(list2);
                if (!deleteGroup(group))
                    rejectRequest(list2);
                else
                    successfulRequest(list2);
                break;
            }
            case 10: //UPDATE_GROUP
            {
                Group group = serilaizegroupObject(list2);
                if (!updateGroup(group))
                    rejectRequest(list2);
                else
                    successfulRequest(list2);
                break;
            }*/
 /*
            case 13: //CREATE_GROUP
            {
                Group group = serilaizegroupObject(list2);
                if (!createGroup(group))
                    rejectRequest(list2);
                else
                    successfulRequest(list2);
                break;            /*
            case 12:
            {
                String senderEmail;
                String recieverEmail;
                Object messageContent;

                for(String email:onlineUsers.keySet())
                {
                    if(email.equals(recieverEmail))
                }
            }



           /* case 9: //DELETE_GROUP
            {
                Group group = serilaizegroupObject(list2);
                if (!deleteGroup(group))
                    rejectRequest(list2);
                else
                    successfulRequest(list2);
                break;
            }
            case 10: //UPDATE_GROUP
            {
                Group group = serilaizegroupObject(list2);
                if (!updateGroup(group))
                    rejectRequest(list2);
                else
                    successfulRequest(list2);
                break;
            }*/
 /*
            case 13: //CREATE_GROUP
            {
                Group group = serilaizegroupObject(list2);
                if (!createGroup(group))
                    rejectRequest(list2);
                else
                    successfulRequest(list2);
                break;
            }*/
        }
    }

    private void SuccessfulUserListFriendSpecific(ArrayList<Object> list2) throws IOException {

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list2.get(0));
        arrayList.add(true);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEmail().equals(list2.get(1)) && list.get(i).getUserFriends() != null) {
                for (int j = 0; j < list.get(i).getUserFriends().size(); j++) {
                    for (int k = 0; k < list.size(); k++) {
                        if (list.get(i).getUserFriends().get(j).equals(list.get(k).getEmail())) {
                            arrayList.add(list.get(k).getName());
                            arrayList.add(list.get(k).getStory());
                        }
                    }

                }
            }
        }
        System.out.println("The User List Specific is :" + arrayList);
        output.writeObject(arrayList);
        output.flush();

    }

    private boolean CheckUserListFriendOfUserToRemoveBlock(ArrayList<Object> list2) {
        return true;
    }

    private void successfulRequestForBlockFriend(ArrayList<Object> list2) throws IOException {

        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list2.get(0));
        arrayList.add(true);
        output.writeObject(arrayList);
        output.flush();
    }

    private boolean CheckUserListFriendOfUserToBlock(ArrayList<Object> list2) {
        boolean bool = false;
        for (int i = 0; i < list.size(); i++) {
            if (list2.get(1).equals(list.get(i).getEmail())) {
                for (int j = 0; j < list.get(i).getUserFriends().size(); j++) {
                    if (list.get(i).getUserFriends().get(j).equals(list2.get(2)) && !list.get(i).searchInBlockList((String) list2.get(2))) {
                        list.get(i).blockUser((String) list2.get(2));
                        list.get(i).getUserFriends().remove(j);
                        bool = true;
                        return bool;
                    }
                }
                System.out.println("The Block List of the User is :" + list.get(i).getBlockList());
                System.out.println("The UserList Friend After Block is :" + list.get(i).getUserFriends());
            }
        }
        return bool;
    }

    private void successfulRequestForRemoveFriend(ArrayList<Object> list2) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list2.get(0));
        arrayList.add(true);
        output.writeObject(arrayList);
        output.flush();

    }

    private boolean CheckUserListFriendOfUser(ArrayList<Object> list2, HandleThread thread) {
        boolean bool = false;
        System.out.println("The User Friends is :" + thread.getUserProfile().getUserFriends());
        for (int j = 0; j < thread.getUserProfile().getUserFriends().size(); j++) {
            if (list2.get(1).equals(thread.getUserProfile().getUserFriends().get(j))) {
                thread.getUserProfile().getUserFriends().remove(j);  // same note as the prev function (about pointers)
                bool = true;
                break;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEmail().equals(list2.get(1))) {
                for (int j = 0; j < list.get(i).getUserFriends().size(); j++) {
                    if (list.get(i).getUserFriends().get(j).equals(thread.getUserProfile().getEmail())) {
                        list.get(i).getUserFriends().remove(j);
                    }
                }
            }
        }
        return bool;
    }

    private void successfulRequestForEditInfo(ArrayList<Object> list2) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list2.get(0));
        arrayList.add(true);
        arrayList.add(list2.get(1)); // useless
        output.writeObject(arrayList);
        output.flush();

    }

    public UserProfile CheckTheUserList(ArrayList<Object> list2) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEmail().equals(list2.get(1))) {
                list.get(i).setName((String) list2.get(2));
                list.get(i).setStory((String) list2.get(3));
                return list.get(i);
            }
        }
        return null;
    }

    public void successfulRequestForAddFriend(ArrayList<Object> list2) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list2.get(0));
        arrayList.add(true);
        output.writeObject(arrayList);
        output.flush();
    }

    public boolean CheckUserListFriend(ArrayList<Object> list2, HandleThread thread) {
        boolean ans = false;
        System.out.println("The list friend is :" + thread.getUserProfile().getUserFriends());
        boolean bool = thread.getUserProfile().searchInFriendList((String) list2.get(2));
        boolean bool2 = thread.getUserProfile().searchInBlockList((String) list2.get(2));
        if (bool && !bool2) {
            System.out.println("This Friend is already exist");
//            ans = false;

        } else { // if the user in the main list has not updated : update thread.getUserProfile() in main list
            for (int j = 0; j < list.size(); j++) { //  becasuse we assumed that they have the same pointer
                if (list2.get(2).equals(list.get(j).getEmail())) {
                    thread.getUserProfile().addFriend(list.get(j));
                    //   System.out.println("The userFriend is :" + list.get(j).getUserFriends());
                    System.out.println("The friend added");
                    ans = true;
                    break;
                }
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getEmail().equals(list2.get(1))) {
                    list.get(i).addFriend(thread.getUserProfile());
                }
            }
        }
        return ans;
    }

    public String getEmailsFileLocation() {
        return emailsFileLocation;
    }

    public void setEmailsFileLocation(String emailsFileLocation) {
        this.emailsFileLocation = emailsFileLocation;
    }

    public String getHangedMessagesFileLocation() {
        return hangedMessagesFileLocation;
    }

    public void setHangedMessagesFileLocation(String hangedMessagesFileLocation) {
        this.hangedMessagesFileLocation = hangedMessagesFileLocation;
    }

    public ObjectOutputStream getOutputStream() {
        return output;
    }

    public ObjectInputStream getInputStream() {
        return input;
    }

    //Set Up and run the server
    public void startRunning() throws IOException {
//<<<<<<< HEAD
        server = new ServerSocket(6790, 101);
//=======
        //       server = new ServerSocket(6790, 100);
//>>>>>>> origin/master
        while (true) {
            try {
                WaitForConnection(); //wait someone to connect with me
                //           SetUpStream();// after one connect with me I Will setup my Stream InputStream and OutPutStream and setup connection
                //             WhileChatting(); // the programme that will send and receive message
            } catch (EOFException eof) {
                eof.printStackTrace();
                System.out.println("Stop:" + "\n" + "The Server End up the Connection...");
            }/*  finally {
                CloseCrap();
            }*/

        }
    }

    //wait for connection then display connection information
    private void WaitForConnection() throws IOException {
        System.out.println("Waiting for someone to connect...");
        connection = server.accept(); // to accept any one want to chat with you
        HandleThread thread = new HandleThread(connection);
        onlineUsers.put(thread.getUserProfile().getEmail(), thread);

    }

    //make the stream to send and receive the message
    private void SetUpStream() throws IOException {

        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        output.writeObject(null);
        System.out.println("The Stream Is Ready");

    }

    //The function will execute during the chat
    private void WhileChatting() throws IOException, ClassNotFoundException {
        System.out.println("Begin Chating");

        /*    FileInputStream fis = new FileInputStream("C:\\Users\\Mohamad Nsearaty\\Desktop\\One Piece wallpapers\\[Al3asq] One Piece - 876 [h264 1080p 10bit].mkv_snapshot_21.31_[2019.03.23_16.05.42].jpg");
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        output.writeObject(buffer);
        output.flush();*/
        //final String message[] = {""};
        ArrayList<Object> recieveMessage;
        input.readObject();
        // do { //have a conversation
        recieveMessage = (ArrayList<Object>) input.readObject();
        System.out.println(recieveMessage);
        //if(recieveMessage!=)
        //{
        //      handleRequest(recieveMessage);
        // }

        //} while (!message[0].equals("CLIENT - END")); // the while will excite until any one type END then the chat will stop here we deal with Just String
    }

    public UserProfile logIn(UserProfile user) throws Exception {

        for (int i = 0; i < list.size(); i++) {

            if (user.getEmail().equals(list.get(i).getEmail())) {
                if (user.getUserPassword().equals(list.get(i).getPassword())) {
                    System.out.println("Welcome, " + user);
                    return list.get(i);
                } else {
                    System.out.println("Login Failed");
                    return null;
                }
            }
        }
        return null;
    }

    //Send Message to client
    public void SendMessage(String message) {
        try {
            //   System.out.println("SendMessage() was called");
            System.out.println("Server - " + message);
            output.writeObject("SERVER - " + message); //send message throw the output stream
            output.flush();
            ShowMessage("\n SERVER -" + message);
        } catch (IOException e) { // if we can't send message to reason
            e.printStackTrace();
            System.out.println("Something IS Wrong With Sending Message");

        }

    }

    //Update Chat Window
    private void ShowMessage(final String Text) {
    }

    private void AbleToType(final boolean type) { // if it true the user can type and if it false the user can't type if there is no one connect with him
        //type on the chat list if  type = true
        //make the Edit text UserText.setEditable(type);

    }

    //Close Streams and Socket after you done The Chatting
    private void CloseCrap() {
        ShowMessage("\n Closing Connection...\n");
        // here shut the able to write message
        try {
            output.close();
            input.close();
            connection.close();
            File emailFile = new File(emailsFileLocation);
            if (!emailFile.exists()) {
                emailFile.createNewFile();
            }

            File hangeFile = new File(hangedMessagesFileLocation);
            if (!hangeFile.exists()) {
                hangeFile.createNewFile();
            }

            File groupFile = new File(groupsFileLocation);
            if (!groupFile.exists()) {
                groupFile.createNewFile();
            }

            FileOutputStream hangeStream = new FileOutputStream(hangeFile);
            FileOutputStream usersStream = new FileOutputStream(emailFile);
            FileOutputStream groupStream = new FileOutputStream(groupFile);

            ObjectOutputStream hangedMessagesWriter = new ObjectOutputStream(hangeStream);
            ObjectOutputStream usersWriter = new ObjectOutputStream(usersStream);
            ObjectOutputStream groupWriter = new ObjectOutputStream(groupStream);

            hangedMessagesWriter.writeObject(messagelist);
            usersWriter.writeObject(list);
            groupWriter.writeObject(groupsList);

            hangedMessagesWriter.flush();
            usersWriter.flush();
            groupWriter.flush();

            hangedMessagesWriter.close();
            usersWriter.close();
            groupWriter.close();

            hangeStream.close();
            usersStream.close();
            groupStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean addPartnersToGroup(ArrayList<Object> list2, HandleThread thread) {
        int groupID = (int) list2.get(1);
        ArrayList<UserProfile> partners = (ArrayList<UserProfile>) list2.get(2);
        UserProfile user = thread.userProfile;
        for (int i = 0; i < groupsList.size(); i++) {
            if (groupsList.get(i).getId() == groupID) {
                for (int j = 0; j < groupsList.get(i).getMembers().size(); j++) {
                    if (user.equals(groupsList.get(i).getMembers().get(j).getKey())) {
                        if ((boolean) groupsList.get(i).getMembers().get(j).getValue() == true) {
                            for (int k = 0; k < partners.size(); k++) {
                                groupsList.get(i).getMembers().add(new Pair(partners.get(k), false));
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean removePartnersFromGroup(ArrayList<Object> list2, HandleThread thread) {
        int groupID = (int) list2.get(1);
        ArrayList<UserProfile> partners = (ArrayList<UserProfile>) list2.get(2);
        UserProfile user = thread.userProfile;
        for (int i = 0; i < groupsList.size(); i++) {
            if (groupsList.get(i).getId() == groupID) {
                for (int j = 0; j < groupsList.get(i).getMembers().size(); j++) {
                    if (user.equals(groupsList.get(i).getMembers().get(j).getKey())) {
                        if ((boolean) groupsList.get(i).getMembers().get(j).getValue() == true) {
                            for (int k = 0; k < partners.size(); k++) {
                                for (int l = 0; l < groupsList.get(i).getMembers().size(); l++) {
                                    if (partners.get(k).equals(groupsList.get(i).getMembers().get(l).getKey())) {
                                        groupsList.get(i).getMembers().remove(l);
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void leaveGroup(ArrayList<Object> list2, HandleThread thread) {
        int groupID = (int) list2.get(1);
        UserProfile user = thread.userProfile;
        for (int i = 0; i < groupsList.size(); i++) {
            if (groupsList.get(i).getId() == groupID) {
                for (int j = 0; j < groupsList.get(i).getMembers().size(); j++) {
                    if (user.equals(groupsList.get(i).getMembers().get(j).getKey())) {
                        groupsList.get(i).getMembers().remove(j);
                    }
                }
            }
        }
    }

    private boolean deleteGroup(ArrayList<Object> list2, HandleThread thread) {
        int groupID = (int) list2.get(1);
        UserProfile user = thread.userProfile;
        for (int i = 0; i < groupsList.size(); i++) {
            if (groupsList.get(i).getId() == groupID) {
                for (int j = 0; j < groupsList.get(i).getMembers().size(); j++) {
                    if (user.equals(groupsList.get(i).getMembers().get(j).getKey())) {
                        if ((boolean) groupsList.get(i).getMembers().get(j).getValue() == true) {
                            groupsList.remove(i);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean setGroupAdmins(ArrayList<Object> list2, HandleThread thread) {
        int groupID = (int) list2.get(1);
        ArrayList<UserProfile> partners = (ArrayList<UserProfile>) list2.get(2);
        ArrayList<Boolean> isAdmins = (ArrayList<Boolean>) list2.get(3);
        UserProfile user = thread.userProfile;
        for (int i = 0; i < groupsList.size(); i++) {
            if (groupsList.get(i).getId() == groupID) {
                for (int j = 0; j < groupsList.get(i).getMembers().size(); j++) {
                    if (user.equals(groupsList.get(i).getMembers().get(j).getKey())) {
                        if ((boolean) groupsList.get(i).getMembers().get(j).getValue() == true) {
                            for (int k = 0; k < partners.size(); k++) {
                                for (int l = 0; l < groupsList.get(i).getMembers().size(); l++) {
                                    if (partners.get(k).equals(groupsList.get(i).getMembers().get(l).getKey())) {
                                        groupsList.get(i).getMembers().set(l, new Pair(partners.get(k), isAdmins.get(k)));
                                    }
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public class HandleThread extends Thread {

        private UserProfile userProfile;
        ;
        private ObjectOutputStream output;
        private ObjectInputStream input;

        public UserProfile getUserProfile() {
            return userProfile;
        }

        public void setUserProfile(UserProfile userProfile) {
            this.userProfile = userProfile;
        }

        public ObjectOutputStream getOutput() {
            return output;
        }

        public void setOutput(ObjectOutputStream output) {
            this.output = output;
        }

        public ObjectInputStream getInput() {
            return input;
        }

        public void setInput(ObjectInputStream input) {
            this.input = input;
        }

        HandleThread(Socket clientSocket) {
            try {
                input = new ObjectInputStream(clientSocket.getInputStream());
                output = new ObjectOutputStream(clientSocket.getOutputStream());
                System.out.println(input.readObject());//////////  why????  possible of lose information // check what you are sending
                ArrayList<Object> arrayList = (ArrayList<Object>) input.readObject();
                handleRequest(arrayList, this);
                start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            ArrayList<Object> arrayList = new ArrayList<>();

            try {
                while (true) {
                    arrayList = (ArrayList<Object>) input.readObject();
                    handleRequest(arrayList, this);
                }
            } catch (EOFException e) {
                onlineUsers.remove(userProfile.getEmail());
                System.out.println(userProfile.getEmail() + " Is Offline");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
