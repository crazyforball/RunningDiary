package sydney.edu.au.runningdiary.service;

import java.util.List;

import sydney.edu.au.runningdiary.model.User;

/**
 * Created by yang on 10/13/17.
 */

public class UserManager {
    public static boolean validateUser(String username, String password) {
        return User.find(User.class, "username = ? and password = ?", username, password).size() == 1;
    }

    public static boolean saveUser(String username, String password) {
        if (User.find(User.class, "username = ?", username).size() == 0) {
            new User(username, password).save();
            return true;
        }
        return false;
    }

    public static User getUser(String username) {
        List<User> users = User.find(User.class, "username = ?", username);
        if (users.size() == 1) {
            return users.get(0);
        }
        return null;
    }


}
