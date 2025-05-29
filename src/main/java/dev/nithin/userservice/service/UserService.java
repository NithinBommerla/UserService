package dev.nithin.userservice.service;

import dev.nithin.userservice.exception.UserAlreadyExistsException;
import dev.nithin.userservice.exception.UserNotFoundException;
import dev.nithin.userservice.model.Token;
import dev.nithin.userservice.model.User;

public interface UserService {
    public User signup(String name, String email, String password) throws UserAlreadyExistsException;

    public Token login(String email, String password) throws UserNotFoundException;

    public void logout(String tokenValue);

    public User validateToken(String tokenValue) throws UserNotFoundException;
}
