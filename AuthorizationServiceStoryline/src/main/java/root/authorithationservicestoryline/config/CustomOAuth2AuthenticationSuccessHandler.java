package root.authorithationservicestoryline.config;

import io.jsonwebtoken.Jwt;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import root.authorithationservicestoryline.dto.UserRegistrationDTO;
import root.authorithationservicestoryline.service.UserManagementService;
import root.authorithationservicestoryline.webtoken.JwtService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private JwtService jwtService;

    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;

//    @Value("${spring.security.client.validity.duration.minute:30}")
    private int VALIDITY_DURATION_MINUTE = 30;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

        String email = oauthToken.getPrincipal().getAttribute("email");
        String username = oauthToken.getPrincipal().getAttribute("email");
        String name = oauthToken.getPrincipal().getAttribute("name");
        Integer age = 18;
        String password = oauthToken.getPrincipal().getAttribute("sub");

        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO(
                username, name, age, passwordEncoder.encode(password), email, "google", "USER"
        );

        if (!userManagementService.checkUsername(username)) {
            userManagementService.registerUser(userRegistrationDTO);
        }

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(username));

        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge((int) TimeUnit.MINUTES.toSeconds(VALIDITY_DURATION_MINUTE));


        response.addCookie(tokenCookie);
        response.setHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
        response.getWriter().flush();
    }

}

