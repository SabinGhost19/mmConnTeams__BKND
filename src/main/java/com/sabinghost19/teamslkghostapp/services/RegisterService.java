package com.sabinghost19.teamslkghostapp.services;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.NotificationPreferencesDto;
import com.sabinghost19.teamslkghostapp.dto.registerRequest.request.RegisterUserRequest;
import com.sabinghost19.teamslkghostapp.enums.Role;
import com.sabinghost19.teamslkghostapp.enums.Status;
import com.sabinghost19.teamslkghostapp.exceptions.EmailAlreadyExistsException;
import com.sabinghost19.teamslkghostapp.exceptions.PasswordMismatchException;
import com.sabinghost19.teamslkghostapp.model.NotificationPreferences;
import com.sabinghost19.teamslkghostapp.model.User;
import com.sabinghost19.teamslkghostapp.model.UserProfile;
import com.sabinghost19.teamslkghostapp.repository.NotificationPreferencesRepository;
import com.sabinghost19.teamslkghostapp.repository.UserProfileRepository;
import com.sabinghost19.teamslkghostapp.repository.UserRepository;
import io.jsonwebtoken.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

    public void AsignImage(String imageUrl, UUID user_id){
        Optional<UserProfile> profile=this.userProfileRepository.findByUserId(user_id);
        if(profile.isPresent()) {
            profile.get().setProfileImageUrl(imageUrl);
            this.userProfileRepository.save(profile.get());
        }else{
            throw new IOException("User Profile Not Found");
        }
    }

    public User registerUser(RegisterUserRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        List<Role> roles = new ArrayList<>();
        roles.add(request.getRole() != null ? request.getRole() : Role.STUDENT);

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .status("OFFLINE") // Set explicitly
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        // Restul logicii pentru profile și preferințe
        // ...
        User new_user = userRepository.save(savedUser);

        // Restul logicii pentru profile și preferințe
        // ...
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
        return savedUser;
    }


//    public User registerUser(RegisterUserRequest request) {
//        if (!request.getPassword().equals(request.getConfirmPassword())) {
//            throw new PasswordMismatchException("Passwords do not match");
//        }
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new EmailAlreadyExistsException("Email already exists");
//        }
//
//        // Inițializează lista de roluri
//        List<Role> roles = new ArrayList<>();
//        if(request.getRole() == null) {
//            roles.add(Role.STUDENT); // Rol implicit
//        } else {
//            roles.add(request.getRole());
//        }
//
//        User savedUser = User.builder()
//                .firstName(request.getFirstName())
//                .lastName(request.getLastName())
//                .email(request.getEmail())
//                .password(request.getPassword())
//                .status(Status.OFFLINE) // Folosește direct enum-ul
//                .roles(roles)
//                .build();
//        User new_user = userRepository.save(savedUser);
//
//        // Restul logicii pentru profile și preferințe
//        // ...
//        //build based on request and save NotPref
//        NotificationPreferencesDto prefNDTO=request.getNotificationPreferences();
//        NotificationPreferences prefNtoBeSaved=NotificationPreferences.builder().
//                user(new_user).email(prefNDTO.isEmail()).push(prefNDTO.isPush()).
//                desktop(prefNDTO.isDesktop()).build();
//        this.notificationPreferencesRepository.save(prefNtoBeSaved);
//
//
//        UserProfile new_profile=UserProfile.builder()
//                .institution(request.getInstitution())
//                .user(new_user).
//                studyLevel(request.getStudyLevel()).
//                specialization(request.getSpecialization())
//                .year(request.getYear()).
//                group(request.getGroup()).
//                bio(request.getBio()).
//                phoneNumber(request.getPhoneNumber()).
//                termsAccepted(request.isTermsAccepted()).
//                privacyPolicyAccepted(request.isPrivacyPolicyAccepted()).
//                build();
//        this.userProfileRepository.save(new_profile);
//        //make response
//        return new_user;
//
//
//    }


//    public User registerUser(RegisterUserRequest request){
//
//        if (!request.getPassword().equals(request.getConfirmPassword())){
//            //throw error
//            throw new PasswordMismatchException("Passwords do not match");
//        }
//        if (userRepository.existsByEmail(request.getEmail())){
//            //throw error
//            throw new EmailAlreadyExistsException("Email already exists");
//        }
//
//
//
//        User new_user=User.builder().
//                firstName(request.getFirstName()).
//                lastName(request.getLastName()).
//                email(request.getEmail()).
//                password(request.getPassword()).
//                status(Status.OFFLINE)
//                .roles(List.of(request.getRole())).
//                build();
//
//        List<Role> roles = new ArrayList<>();
//        if(request.getRole()==null){
//            //implicit STUDENT classic
//            roles.add(Role.STUDENT);
//            new_user.setRoles(roles);
//        }
//
//        roles.add(request.getRole());
//        new_user.setRoles(roles);
//
//        userRepository.save(new_user);
//
//        //build based on request and save NotPref
//        NotificationPreferencesDto prefNDTO=request.getNotificationPreferences();
//        NotificationPreferences prefNtoBeSaved=NotificationPreferences.builder().
//                user(new_user).email(prefNDTO.isEmail()).push(prefNDTO.isPush()).
//                desktop(prefNDTO.isDesktop()).build();
//        this.notificationPreferencesRepository.save(prefNtoBeSaved);
//
//
//        UserProfile new_profile=UserProfile.builder()
//                .institution(request.getInstitution())
//                .user(new_user).
//                studyLevel(request.getStudyLevel()).
//                specialization(request.getSpecialization())
//                .year(request.getYear()).
//                group(request.getGroup()).
//                bio(request.getBio()).
//                phoneNumber(request.getPhoneNumber()).
//                termsAccepted(request.isTermsAccepted()).
//                privacyPolicyAccepted(request.isPrivacyPolicyAccepted()).
//                build();
//        this.userProfileRepository.save(new_profile);
//        //make response
//        return new_user;
//
//    }

}