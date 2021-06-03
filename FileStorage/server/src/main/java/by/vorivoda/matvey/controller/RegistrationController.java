package by.vorivoda.matvey.controller;

import by.vorivoda.matvey.controller.exception.InvalidRegistrationArgumentsException;
import by.vorivoda.matvey.controller.exception.SuchUserAlreadyExistsException;
import by.vorivoda.matvey.model.dao.entity.User;
import by.vorivoda.matvey.model.GlobalConstants;
import by.vorivoda.matvey.model.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class RegistrationController {

    private final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(GlobalConstants.REGISTRATION_PATH)
    public ResponseEntity<String> addUser(@RequestParam("username") String username,
                                  @RequestParam("password") String password) throws Exception {

        logger.info("Registration of new user...");
        if (userService.loadUserByUsername(username) != null) throw new SuchUserAlreadyExistsException("Such user already exists.");
        String errorsReport = getRegistrationErrorsReport(username, password);
        if (errorsReport.length() > 0) throw new InvalidRegistrationArgumentsException(errorsReport);

        userService.create(new User(username, password));
        logger.info("Registered.");
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    private String getRegistrationErrorsReport(String username, String password) {
        StringBuilder report = new StringBuilder();

        if (!username.matches(GlobalConstants.USERNAME_SYMBOLS_REGEX))
            report.append("Username: invalid symbols (need ")
                    .append(GlobalConstants.USERNAME_SYMBOLS_REGEX)
                    .append(")\n");

        if (username.length() < GlobalConstants.USERNAME_MIN_LENGTH)
            report.append("Username: too short (min. ")
                    .append(GlobalConstants.USERNAME_MIN_LENGTH)
                    .append(")\n");

        if (username.length() > GlobalConstants.USERNAME_MAX_LENGTH)
            report.append("Username: too long (max. ")
                    .append(GlobalConstants.USERNAME_MAX_LENGTH)
                    .append(")\n");

        if (password.length() < GlobalConstants.PASSWORD_MIN_LENGTH)
            report.append("Password: too short (min. ")
                    .append(GlobalConstants.PASSWORD_MIN_LENGTH)
                    .append(")\n");

        if (password.length() > GlobalConstants.PASSWORD_MAX_LENGTH)
            report.append("Password: too long (max. ")
                    .append(GlobalConstants.PASSWORD_MAX_LENGTH)
                    .append(")\n");

        return report.toString();
    }
}
