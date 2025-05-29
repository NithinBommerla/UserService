package dev.nithin.userservice.service;

import dev.nithin.userservice.exception.UserAlreadyExistsException;
import dev.nithin.userservice.exception.UserNotFoundException;
import dev.nithin.userservice.model.Token;
import dev.nithin.userservice.model.User;
import dev.nithin.userservice.repository.TokenRepository;
import dev.nithin.userservice.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class UserStorageService implements UserService {

    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    TokenRepository tokenRepository;

    public UserStorageService(UserRepository userRepository, TokenRepository tokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User signup(String name, String email, String password) throws UserAlreadyExistsException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            throw new UserAlreadyExistsException("User with this email ("+email+") already exists, Try logging in instead.");
            // return null;
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public Token login(String email, String password) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            throw new UserNotFoundException("User with this email ("+email+") does not exist, please sign up first.");
        }
        User user = userOptional.get();
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException("Incorrect password for user with email ("+email+").");
        }
        Token token = new Token();
        token.setUser(user);
        token.setTokenValue(RandomStringUtils.randomAlphanumeric(128));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 30);
        Date date = calendar.getTime();

        token.setExpiresAt(date);
        return tokenRepository.save(token);
    }

    @Override
    public void logout(String tokenValue){
        /*
        Logout is simply deleting the token from the database.
        This will make the token invalid for future requests.
         */
        Optional<Token> tokenOptional = tokenRepository.findByTokenValueAndIsDeleted(tokenValue, false);
        if(tokenOptional.isPresent()) {
            Token token = tokenOptional.get();
            token.setDeleted(true);
            tokenRepository.save(token);
        }
        // else, do nothing as the token is already deleted or does not exist
    }

    @Override
    public User validateToken(String tokenValue) throws UserNotFoundException {
        /*
        Valid Token: 1. should exist in the database
                      2. should not be expired
                      3. should not be deleted
         */
    Optional<Token> tokenOptional = tokenRepository.findByTokenValueAndIsDeletedAndExpiresAtGreaterThan
            (tokenValue, false, new Date());
    if(tokenOptional.isEmpty()) {
        // return null;
        throw new UserNotFoundException("Invalid or expired token.");
    }
    Token token = tokenOptional.get();
    return token.getUser();
    }
}
