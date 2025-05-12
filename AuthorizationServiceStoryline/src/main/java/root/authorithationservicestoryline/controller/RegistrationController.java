package root.authorithationservicestoryline.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import root.authorithationservicestoryline.config.MyUserDetailService;
import root.authorithationservicestoryline.dto.LoginDTO;
import root.authorithationservicestoryline.dto.UserRegistrationDTO;
import root.authorithationservicestoryline.dto.UserUpdateDTO;
import root.authorithationservicestoryline.service.UserManagementService;
import root.authorithationservicestoryline.webtoken.JwtService;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/auth")
public class RegistrationController {

    @Autowired
    private UserManagementService userManagementService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailService userDetailService;

    @PostMapping("/register/user")
    public ResponseEntity<?> createUserAndReturnToken(@RequestBody UserRegistrationDTO userRegistrationDTO) {
        try {
            UserRegistrationDTO myUserRegistrationDTO = new UserRegistrationDTO(
                    userRegistrationDTO.username(), userRegistrationDTO.name(),
                    userRegistrationDTO.age(), passwordEncoder.encode(userRegistrationDTO.password()),
                    userRegistrationDTO.registration_account(), userRegistrationDTO.registration_type(),
                    userRegistrationDTO.role()
            );

            ResponseEntity<?> response = userManagementService.registerUser(myUserRegistrationDTO);

            if (response.getStatusCode().is2xxSuccessful()) {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userRegistrationDTO.username(),
                                userRegistrationDTO.password()
                        )
                );

                if (authentication.isAuthenticated()) {
                    String token = jwtService.generateToken(
                            userDetailService.loadUserByUsername(userRegistrationDTO.username())
                    );

                    return ResponseEntity.ok(Map.of(
                            "message", "User registered successfully",
                            "token", token
                    ));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                            "message", "Authentication failed after registration"
                    ));
                }
            }

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Unknown error during user registration",
                    "errorDetails", e.getMessage()
            ));
        }
    }


    @GetMapping("user/{username}")
    public ResponseEntity<?> getUser(@PathVariable("username") String username){
        try {
            UserRegistrationDTO userDTO = userManagementService.getUser(username);
            log.info("User: {}", userDTO);
            return ResponseEntity.status(HttpStatus.OK).body(userDTO);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Unknown error during user registration:"+e.getMessage()));
        }
    }

    @PostMapping("/authenticate/user")
    public String authenticateAndGetToken(@RequestBody LoginDTO loginDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.username(),loginDTO.password()
        ));
        if (authentication.isAuthenticated()){
            return jwtService.generateToken(userDetailService.loadUserByUsername(loginDTO.username()));
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    @PutMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDTO userUpdateDTO){
        try {
            UserUpdateDTO myUserUpdateDTO = new UserUpdateDTO(userUpdateDTO.сurrentUsername(),
                    userUpdateDTO.username(), userUpdateDTO.name(),
                    userUpdateDTO.age()
            );

            ResponseEntity<?> response = userManagementService.updateUser(myUserUpdateDTO);
            updateUsernameInSecurityContext(myUserUpdateDTO.сurrentUsername(), myUserUpdateDTO.username());

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            log.error("Error during user updating: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "message", "Unknown error during user updating",
                    "errorDetails", e.getMessage()
            ));
        }
    }

    public void updateUsernameInSecurityContext(String oldUsername, String newUsername) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            var newAuth = new UsernamePasswordAuthenticationToken(
                    newUsername,
                    authentication.getCredentials(),
                    authentication.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }


}
