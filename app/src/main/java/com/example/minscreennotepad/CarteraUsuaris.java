package com.example.minscreennotepad;

import java.util.ArrayList;
import java.util.List;

public class CarteraUsuaris {

    private List<User> userList;

    public CarteraUsuaris() {
        userList = new ArrayList<>();
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User find(String userName) {
        for (User u: userList) {
            if (u.getUserName().equals(userName)) return  u;
        }
        return null;
    }

    //Retorna TRUE  si l'usuari existeix, FALSE si no existeix.
    public boolean validateUserName(String userName) {
        User user = this.find(userName);
        return  user!=null;
    }

    public boolean signUpUser(User newUser) {
        if(validateUserName(newUser.getUserName())) {
            return false;
        }
        else {
            userList.add(newUser);
            return true;
        }
    }

}
