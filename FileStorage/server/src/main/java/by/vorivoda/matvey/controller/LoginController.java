package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.model.GlobalConstants;
import by.vorivoda.matvey.model.service.UserService;
import by.vorivoda.matvey.security.SecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = GlobalConstants.LOGIN_PATH)
    public ResponseEntity<String> getToken(@RequestHeader("Authorization") String credentials) {
        logger.info("Trying to signing in, credentials: " + credentials + "...");

        credentials = new String(Base64.getDecoder().decode(credentials.substring("Basic".length()).trim()));
        String[] parsed = credentials.split(":", 2);
        logger.debug("Username: " + parsed[0] + ", Password: " + parsed[1]);

        SecurityToken token = userService.signIn(parsed[0], parsed[1]);
        if (token == null) throw new AuthenticationServiceException("Wrong username or password");

        logger.info("Signed in.");
        return new ResponseEntity<>(token.getExpiredDate().getTime() + " " + token.getValue(), HttpStatus.OK);
    }
}