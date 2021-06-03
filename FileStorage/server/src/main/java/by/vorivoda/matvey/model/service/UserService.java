package by.vorivoda.matvey.model.service;

import by.vorivoda.matvey.model.dao.entity.User;
import by.vorivoda.matvey.model.dao.entity.repository.TokenRepository;
import by.vorivoda.matvey.model.dao.entity.repository.UserRepository;
import by.vorivoda.matvey.security.SecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService, DAOService<User> {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean create(User user) {
        logger.info("Trying to create user in repository...");
        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Such user already exists.");
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Creating user " + user + "...");
        userRepository.save(user);
        logger.info("User created.");
        return true;
    }

    @Override
    public User get(Long id) {
        logger.info("Trying to get user by id [" + id + "]...");
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        logger.info("Getting all users");
        return userRepository.findAll();
    }

    @Override
    public boolean update(Long id, User user) {
        logger.info("Trying to update a user by id [" + id + "]...");
        if (delete(id)) {
            create(user);
            logger.info("User updated [" + id + "].");
            return true;
        }
        logger.warn("No such user [" + id + "].");
        return false;
    }

    @Override
    public boolean delete(Long id) {
        logger.info("Trying to delete a user by id [" + id + "]...");
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("User deleted [" + id + "].");
            return true;
        }
        logger.warn("No such user [" + id + "].");
        return false;
    }

    public SecurityToken signIn(String username, String password) {
        logger.info("Trying to sign in user [" + username + "]...");
        User user = (User) loadUserByUsername(username);
        if (user == null) throw new UsernameNotFoundException("No such user (" + username + ")");

        if (passwordEncoder.matches(password, user.getPassword())) {
            if (user.getToken() != null) {
                if(user.getToken().isExpired()) {
                    tokenRepository.delete(user.getToken());
                } else {
                    logger.info("Such user already has a token.");
                    return user.getToken();
                }
            }
            String tokenValue;
            do {
                tokenValue = UUID.randomUUID().toString();
            } while (tokenRepository.findByValue(tokenValue) != null);

            SecurityToken token = new SecurityToken(tokenValue);
            user.setToken(token);
            tokenRepository.save(token);
            userRepository.save(user);
            logger.info("Token generated: " + tokenValue + "...");
            return token;
        }

        logger.warn("Passwords doesn't match.");
        return null;
    }

    public Optional<User> findByToken(String tokenValue) {
        logger.info("Trying to find user by token [" + tokenValue + "]...");
        SecurityToken token = tokenRepository.findByValue(tokenValue);

        if (token == null) {
            logger.warn("No such token [" + tokenValue + "].");
            return Optional.empty();
        }
        if (new Date().getTime() > token.getExpiredDate().getTime()) {
            logger.warn("Token is expired [" + tokenValue + "].");
            return Optional.empty();
        }


        logger.info("Token is correct, user: " + token.getUser() + ".");
        return Optional.of(token.getUser());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Trying to find user by username [" + username + "]...");
        return userRepository.findByUsername(username);
    }
}
