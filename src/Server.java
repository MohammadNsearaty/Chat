import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

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

        if (!file1.exists())
            file1.createNewFile();
        if (!file2.exists())
            file2.createNewFile();
        if (!file3.exists())
            file3.createNewFile();

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
                if (list.get(i).getEmail().equals(user.getEmail()))
                    return false;
            }
            list.add(user);
        }
        System.out.println("list is: ababa" + list.toString());  ///////////
        return true;
    }

    public boolean checkIfUserNameAvailable(String userName) {
        for (UserProfile er : list) {
            if (userName == er.getName())
                return false;
        }
        return true;
    }

    public boolean deleteUser(UserProfile user) {
        if (checkIfUserNameAvailable(user.getName()))
            return false;
        list.remove(user);
        return true;

    }

    public boolean updateUser(UserProfile user) {
        if (checkIfUserNameAvailable(user.getName()))
            return false;
        for (UserProfile er : list) {
            if (er.getUserId() == user.getUserId()) {
                list.remove(er);
                list.add(user);
            }
        }
        return false;


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
        UserProfile profile = serilaizeProfileObject(list);
        addUser(profile);
        arrayList.add(list.get(0));
        arrayList.add(true);
        arrayList.add("abc123");//Test Id
        output.writeObject(arrayList);
        output.flush();
    }

    public void successfulRequestForSignIn(ArrayList<Object> lists) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(lists.get(0));
        arrayList.add(true);
        arrayList.add(lists.get(1));
        for(int i =0 ; i<list.size();i++){
            if(list.get(i).getEmail().equals(lists.get(1))){
                arrayList.add(list.get(i).getName());
            }
        }
        output.writeObject(arrayList);
        output.flush();
    }

    public void successfulRequestForFriendList(ArrayList<Object> lists) throws IOException {


        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(lists.get(0));
        arrayList.add(true);
        arrayList.add((int) list.size());
        for (int i = 0; i < list.size(); i++) {
            if (!lists.get(1).equals(list.get(i).getEmail())) {
                arrayList.add(list.get(i).getName());
                arrayList.add(list.get(i).getEmail());
            }
        }

        output.writeObject(arrayList);
        output.flush();
    }

    public boolean createGroup(Group group) {
        if (!checkIfGroupNameAvaillabe(group.getName()))
            return false;
        groupsList.add(group);
        return true;
    }

    public boolean deleteGroup(Group group) {
        if (checkIfGroupNameAvaillabe(group.getName()))
            return false;
        groupsList.remove(group);
        return true;
    }

    public boolean updateGroup(Group group) {
        if (checkIfGroupNameAvaillabe(group.getName()))
            return false;
        for (Group gr : groupsList) {
            if (gr.getId() == group.getId()) {
                groupsList.remove(gr);
                groupsList.add(group);
            }
        }
        return false;
    }

    public boolean checkIfGroupNameAvaillabe(String groupName) {
        for (Group gr : groupsList) {
            if (groupName == gr.getName())
                return false;
        }
        return true;
    }

    public void hangMessage(Message message) {
        messagelist.add(message);
    }

    public void hangUserMessages(ArrayList<Message> messages) {
        messagelist = messages;
    }

    public Message freeMessage(String messageId) {

        for (Message message : messagelist) {
            if (message.getMessageID().equals(messageId)) {
                messagelist.remove(message);
                return message;
            }
        }
        return null;
    }


    public ArrayList<Message> searchInHangedMessages(String userId) {
        ArrayList<Message> userMessages = new ArrayList<>();
        for (Message message : messagelist)
            if (message.getRecieverID().equals(userId))
                userMessages.add(freeMessage(message.getMessageID()));
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


    public Group serilaizegroupObject(ArrayList<Object> arrayList) {
        Group group = new Group();

        group.setName((String) arrayList.get(1));
        group.setId("aa25");


        return group;
    }

    public Message serilaizemessageObject(ArrayList<Object> arrayList) {
        Message message = new Message();

        message.setType((messageType) arrayList.get(1));
        message.setMessageID("25");


        return message;
    }

    public UserProfile searchInUsersList(String Email) {
        System.out.println("list is:" + list);
        System.out.println("size is:" + list.size());
        //System.out.println("list[2]:"+list.get(2).getEmail());
        int i = 0;
        for (UserProfile profile : list) {
            System.out.println("Email is:" + list.get(i).getEmail());
            if (profile.getEmail().equals(Email))
                return profile;
        }
        return null;
    }

    public void handleRequest(ArrayList<Object> list2) throws IOException {
        int type = (int) list2.get(0);
        switch (type) {
            case 0://Add user
            {
                UserProfile userProfile = serilaizeProfileObject(list2);
                if (!addUser(userProfile)) {
                    rejectRequest(list2);
                } else
                    successfulRequest(list2);
                break;
            }
            case 1://sign in
            {
                try {
                    System.out.println("abababa");
                    UserProfile userProfile = serilaizeSignInInfo(list2);
                    boolean bool = logIn(userProfile);
                    if (bool) {
                        successfulRequestForSignIn(list2);
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
                UserProfile userProfile = serilaizeProfileObject(list2);
                if (!deleteUser(userProfile)) {
                    rejectRequest(list2);
                } else
                    successfulRequest(list2);
                break;
            }
            case 3: //UPDATE_USER
            {
                UserProfile userProfile = serilaizeProfileObject(list2);
                updateUser(userProfile);
                successfulRequest(list2);
                break;
            }
            case 4: //Send The list user
            {
                if (list != null) {   //check here the condistion
                    successfulRequestForFriendList(list2);
                } else {
                    rejectRequest(list2);
                }

                break;
            }
            case 5:  //Edit Info of the User
            {
                if (list != null) {
                    boolean bool = CheckTheUserList(list2);
                    if (bool) {
                        successfulRequestForEditInfo(list2);
                    } else {
                        rejectRequest(list2);
                    }
                }
                break;
            }
            case 6: {  //To Add Friend
                if (list != null) {
                    boolean bool = CheckUserListFriend(list2);
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
                    boolean bool = CheckUserListFriendOfUser(list2);

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

            // case 20

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
            }
            case 11: //SEND_MESSAGE
            {
                Message message = serilaizemessageObject(list2);
                hangMessage((message));
                successfulRequest(list2);
                break;
            }
            case 12: //CHECK_HANGED_MESSAGES
            {
                Message message = serilaizemessageObject(list2);
                ArrayList<Message> userMessages = searchInHangedMessages(message.getMessageID());
                if (userMessages.size() == 0)
                    rejectRequest(list2);
                else {
                    hangUserMessages(userMessages);
                    successfulRequest(list2);
                }
                break;

            }
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

    private boolean CheckUserListFriendOfUser(ArrayList<Object> list2) {
        boolean bool = false;
        for (int i = 0; i < list.size(); i++) {
            if (list2.get(1).equals(list.get(i).getEmail())) {
                System.out.println("The User Friends is :" + list.get(i).getUserFriends());
                for (int j = 0; j < list.get(i).getUserFriends().size(); j++) {

                    if (list2.get(2).equals(list.get(i).getUserFriends().get(j))) {
                        list.get(i).getUserFriends().remove(j);
                        bool = true;
                        break;
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
        arrayList.add(list2.get(1));
        output.writeObject(arrayList);
        output.flush();

    }

    public boolean CheckTheUserList(ArrayList<Object> list2) {
        boolean bool = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getEmail().equals(list2.get(1))) {
                list.get(i).setStory((String) list2.get(3));
                list.get(i).setName((String) list2.get(2));
                bool = true;
            } else {
                System.out.println("Login Failed");
                bool = false;
            }

        }
        return bool;
    }

    public void successfulRequestForAddFriend(ArrayList<Object> list2) throws IOException {
        ArrayList<Object> arrayList = new ArrayList<>();
        arrayList.add(list2.get(0));
        arrayList.add(true);
        output.writeObject(arrayList);
        output.flush();
    }

    public boolean CheckUserListFriend(ArrayList<Object> list2) {
        boolean ans = false;
        for (int i = 0; i < list.size(); i++) {

            if (list2.get(1).equals(list.get(i).getEmail())) {
                System.out.println("The list friend is :" + list.get(i).getUserFriends());
                boolean bool = list.get(i).searchInFriendList((String) list2.get(2));
                boolean bool2 = list.get(i).searchInBlockList((String) list2.get(2));
                System.out.println("The User Block List :"+list.get(i).getBlockList());
                if (bool && !bool2) {
                    System.out.println("This Friend is already exist");
                    ans = false;


                } else if(!bool){
                    for (int j = 0; j < list.size(); j++) {
                        if (list2.get(2).equals(list.get(j).getEmail())) {
                            list.get(i).addFriend(list.get(j));
                            //   System.out.println("The userFriend is :" + list.get(j).getUserFriends());
                            System.out.println("The friend added");
                            ans = true;
                            break;

                        }
                    }

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
                WaitForConnection();  //wait someone to connect with me
                SetUpStream();// after one connect with me I Will setup my Stream InputStream and OutPutStream and setup connection
                WhileChatting(); // the programme that will send and receive message
            } catch (EOFException eof) {
                eof.printStackTrace();
                System.out.println("Stop:" + "\n" + "The Server End up the Connection...");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                CloseCrap();
            }

        }
    }

    //wait for connection then display connection information
    private void WaitForConnection() throws IOException {
        System.out.println("Waiting for someone to connect...");

        connection = server.accept(); // to accept any one want to chat with you

        System.out.println("connected");
      /*  ///****
        try {
            ArrayList<Object> LogInfo;
            input.readObject();
            LogInfo = (ArrayList<Object>) input.readObject();
            System.out.println(LogInfo);
            UserProfile userProfile = serilaizeProfileObject(LogInfo);
            logIn(userProfile);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       */ ///*****
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
        handleRequest(recieveMessage);
        // }

        //} while (!message[0].equals("CLIENT - END")); // the while will excite until any one type END then the chat will stop here we deal with Just String

    }

    public boolean logIn(UserProfile user) throws Exception {

        boolean bool = false;

        for (int i = 0; i < list.size(); i++) {


            if (user.getEmail().equals(list.get(i).getEmail())) {
                if (user.getUserPassword().equals(list.get(i).getPassword())) {
                    System.out.println("Welcome, " + user);
                    bool = true;
                } else {
                    System.out.println("Login Failed");
                    bool = false;
                }
            }
        }
        return bool;
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
            emailFile.createNewFile();

            File hangeFile = new File(hangedMessagesFileLocation);
            hangeFile.createNewFile();

            File groupFile = new File(groupsFileLocation);
            groupFile.createNewFile();


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


}