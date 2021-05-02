package com.example.minscreennotepad;

import java.util.ArrayList;
import java.util.List;

public class CarteraUsuaris {

    private List<User> userList;

    /**
     * Constructor de CarteraUsuaris
     */
    public CarteraUsuaris() {
        userList = new ArrayList<>();
    }

    /**
     * Getter de la lista de usuarios
     * @return Lista de Users
     */
    public List<User> getUserList() {
        return userList;
    }

    /**
     * Setter de la lista de usuarios
     * @param userList nueva Lista de usuarios
     */
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    /**
     * Encuentra un usuario en la lista a partir de su nombre
     * @param userName String con el nombre del usuario
     * @return  Usuario encontrado o "null" si no lo encuentra
     */
    public User find(String userName) {
        for (User u: userList) {
            if (u.getUserName().equals(userName)) return  u;
        }
        return null;
    }

    /**
     * Verifica si un nombre de usuario ya está en uso.
     * @param userName String del nombre de usuario a verificar
     * @return true, si está en uso, false si no lo está
     */
    public boolean validateUserName(String userName) {
        User user = this.find(userName);
        return  user!=null;
    }

    /**
     * Registra un nuevo usuario y lo añade a la lista
     * @param newUser Usuario a registrar
     * @return  true si se ha podido registrar, false si ha habido algun problema
     */
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
