package com.springapp.mvc.service;

/**
 * Created by yasina on 08.05.15.
 */

import com.springapp.mvc.models.User;
import com.springapp.mvc.repository.UserRepositoryImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class UserManager {

    private static List<User> userList;
    private UserRepositoryImpl userRepository;



    public List<User> getBrandList() {

        try {
            userList = userRepository.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return userList;

    }


}