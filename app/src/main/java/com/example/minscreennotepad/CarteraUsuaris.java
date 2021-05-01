package com.example.minscreennotepad;

import java.util.ArrayList;
import java.util.List;

public class CarteraUsuaris {

    private List<Usuari> userList;

    public CarteraUsuaris() {
        userList = new ArrayList<>();
    }

    public List<Usuari> getUserList() {
        return userList;
    }

    public void setUserList(List<Usuari> userList) {
        this.userList = userList;
    }

    public Usuari find(String userName) {
        for (Usuari u: userList) {
            if (u.getUserName().equals(userName)) return  u;
        }
        return null;
    }

    //Retorna TRUE  si l'usuari existeix, FALSE si no existeix.
    public boolean validateUserName(String userName) {
        Usuari user = this.find(userName);
        return  user!=null;
    }

    public boolean signUpUser(Usuari newUser) {
        if(validateUserName(newUser.getUserName())) {
            return false;
        }
        else {
            userList.add(newUser);
            return true;
        }
    }

}
