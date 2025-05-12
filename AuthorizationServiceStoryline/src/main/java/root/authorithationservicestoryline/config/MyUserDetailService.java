package root.authorithationservicestoryline.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import root.authorithationservicestoryline.dto.UserRegistrationDTO;
import root.authorithationservicestoryline.service.UserManagementService;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserManagementService userManagementService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRegistrationDTO userDTO = userManagementService.getUser(username);
        if(userDTO != null){
            return User.builder()
                    .username(userDTO.username())
                    .password(userDTO.password())
                    .roles(getRoles(userDTO))
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    private String[] getRoles(UserRegistrationDTO userDTO) {
        if(userDTO.role()==null){
            return new String[]{"USER"};
        }
        return userDTO.role().split(",");
    }
}
