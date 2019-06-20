
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ListIterator;
import javafx.util.Pair;

public class Group implements Serializable {

    private String name;
    private int groupid;
    private static int IDs = 0;
    private ArrayList<Pair> members; //Pair<UserProfile, Boolean = admin or not>

    public Group(String name, ArrayList<UserProfile> members, UserProfile admin) {
        this.name = name;
        this.members = new ArrayList<>();
        this.members.add(new Pair(admin, true));
        for (UserProfile member : members) {
            this.members.add(new Pair(member, false));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return groupid;
    }

    public void generateId() {
        groupid = ++IDs;
    }

    public void setId(int id) {
        this.groupid = id;
    }
//////////////////////////////////////////////////////////////hba

    public int getGroupSize() {
        return members.size();
    }

    public void addClient(UserProfile c) {
        members.add(new Pair(c, false));
    }

    public void removeClient(UserProfile c) {
        for (Pair member : members) {
            if (c.equals(member.getKey())) {
                members.remove(member);
                return;
            }
        }
    }

    public void setAdmins(ArrayList<UserProfile> profiles, ArrayList<Boolean> booleans) {
        for (int i = 0; i < profiles.size(); i++) {
            for (int j = 0; j < members.size(); j++) {
                if (profiles.get(i).equals(members.get(j).getKey())) {
                    members.set(j, new Pair(profiles.get(i), booleans.get(i)));
                }
            }
        }
    }

    public ArrayList<Pair> getMembers() {
        return members;
    }

    public ArrayList<UserProfile> getProfiles() {
        ArrayList<UserProfile> profiles = new ArrayList<>();
        for (Pair member : members) {
            profiles.add((UserProfile) member.getKey());
        }
        return profiles;
    }

    public UserProfile findUser(String username) {
        for (Pair member : members) {
            if (username.equals(((UserProfile) member.getKey()).getUserName())) {
                return (UserProfile) member.getKey();
            }
        }
        return null;
    }

    public UserProfile findUsers(String email) {
        for (Pair member : members) {
            if (email.equals(((UserProfile) member.getKey()).getEmail())) {
                return (UserProfile) member.getKey();
            }
        }
        return null;
    }

}
