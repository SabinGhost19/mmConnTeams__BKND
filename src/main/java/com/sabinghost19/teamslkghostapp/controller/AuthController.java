package com.sabinghost19.teamslkghostapp.controller;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.*;
import com.sabinghost19.teamslkghostapp.services.LoginService;
import com.sabinghost19.teamslkghostapp.services.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import com.sabinghost19.teamslkghostapp.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@RestController
//@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    public AuthController(RegisterService registerService, LoginService loginService) {
         this.registerService=registerService;
         this.loginService=loginService;
    }
    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginUserReponseDTO>loginUser(@RequestBody LoginUserRequest request){
        if(!this.loginService.verifyUser(request.getEmail(),request.getPassword())){
            //return true
        }
        //return false
        LoginUserReponseDTO loginUserReponseDTO=new LoginUserReponseDTO();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(loginUserReponseDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> registerUser( @RequestPart("userData") RegisterUserRequest request,
                                                                 @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        logger.info("request venit in registerUser");
        logger.info("Requestul este: " + request);

        String pw_hash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        request.setPassword(pw_hash);
        request.setConfirmPassword(pw_hash);
        //salvam userul
        User savedUser=this.registerService.registerUser(request);

        //verificam poza daca este buna
        //dupa care salvam in cloud
       if(profileImage!=null && !profileImage.isEmpty()){
            //save the profile image in cloud or somewhere
          if (!this.registerService.uploadProfileImage(profileImage)) {
              //daca a crapat...handle
          }
        }

       //make response
        //  make here to call another service for generate the token
       RegisterUserResponseDTO registerUserResponseDTO=
                RegisterUserResponseDTO.builder().
                        message("registrated succesfully").
                        success(true).
                        user(new UserDto(savedUser)).
                        build();

        return ResponseEntity.status(HttpStatus.CREATED).body(registerUserResponseDTO);
    }

}
