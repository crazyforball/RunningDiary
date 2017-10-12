package sydney.edu.au.runningdiary.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yang on 9/29/17.
 */

public class User implements Serializable{
    private String username;
    private String password;
    private ArrayList<Record> history;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.history = new ArrayList<Record>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Record> getHistory() {
        return history;
    }

    public void addHistory(Record record) {
        this.history.add(record);
    }
}
