package com.sabinghost19.teamslkghostapp.services;

import com.sabinghost19.teamslkghostapp.dto.registerRequest.NotificationPreferencesDto;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.RegisterUserRequest;
import com.sabinghost19.teamslkghostapp.exceptions.EmailAlreadyExistsException;
import com.sabinghost19.teamslkghostapp.exceptions.PasswordMismatchException;
import com.sabinghost19.teamslkghostapp.model.NotificationPreferences;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.model.UserProfile;
import com.sabinghost19.teamslkghostapp.repository.NotificationPreferencesRepository;
import com.sabinghost19.teamslkghostapp.repository.UserProfileRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@Service
public class RegisterService {
    private final Logger logger = Logger.getLogger(RegisterService.class.getName());
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final NotificationPreferencesRepository notificationPreferencesRepository;

    @Autowired
    public RegisterService(UserProfileRepository userProfileRepository, UserRepository userRepository, NotificationPreferencesRepository notificationPreferencesRepository) {
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.notificationPreferencesRepository = notificationPreferencesRepository;
    }

    public User registerUser(RegisterUserRequest request){

        if (!request.getPassword().equals(request.getConfirmPassword())){
            //throw error
            throw new PasswordMismatchException("Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            //throw error
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User new_user=User.builder().
                firstName(request.getFirstName()).
                lastName(request.getLastName()).
                email(request.getEmail()).
                password(request.getPassword()).
                role(request.getRole()).
                build();

        userRepository.save(new_user);

        //build based on request and save NotPref
        NotificationPreferencesDto prefNDTO=request.getNotificationPreferences();
        NotificationPreferences prefNtoBeSaved=NotificationPreferences.builder().
                user(new_user).email(prefNDTO.isEmail()).push(prefNDTO.isPush()).
                desktop(prefNDTO.isDesktop()).build();
        this.notificationPreferencesRepository.save(prefNtoBeSaved);


        UserProfile new_profile=UserProfile.builder()
                .institution(request.getInstitution())
                .user(new_user).
                studyLevel(request.getStudyLevel()).
                specialization(request.getSpecialization())
                .year(request.getYear()).
                group(request.getGroup()).
                bio(request.getBio()).
                phoneNumber(request.getPhoneNumber()).
                termsAccepted(request.isTermsAccepted()).
                privacyPolicyAccepted(request.isPrivacyPolicyAccepted()).
                build();
        this.userProfileRepository.save(new_profile);
        //make response
        return new_user;

    }
    public boolean uploadProfileImage(MultipartFile profileImage){
        //if the upload succeded move forward
        return true;
    }

}
