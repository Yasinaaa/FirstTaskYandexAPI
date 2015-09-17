package springmvc.db.repositories.impl;

import com.kzn.itis.db.model.User;

import java.util.List;


public interface UserRepository {


    void add(User user);
    void remove();
    void update(String newUser, int age);
    List<User> getAllUsers();
    int countOfUsers();
    //m void remove

}
