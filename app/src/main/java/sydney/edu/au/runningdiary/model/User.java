package sydney.edu.au.runningdiary.model;

import com.orm.SugarRecord;

/**
 * Created by yang on 9/29/17.
 */

public class User extends SugarRecord<User> {
    private String username;
    private String password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

}
