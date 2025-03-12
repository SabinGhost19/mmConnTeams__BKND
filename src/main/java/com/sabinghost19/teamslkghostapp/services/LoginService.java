package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(LoginService.class.getName());

    @Autowired
    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean verifyUser(String email, String candidate_password) {
        Optional<User> userBD=this.userRepository.findByEmail(email);
        if(!userBD.isPresent()){
            //user is not present
            //give error
            logger.info("USER AUTH FAILED....");
            return false;
        }
        if (!BCrypt.checkpw(candidate_password, userBD.get().getPassword())) {
            System.out.println("It does not match");
            logger.info("USER AUTH FAILED....");
            return false;
        }
        logger.info("USER IS AUTHENTICATED....");
        return true;
    }
}
