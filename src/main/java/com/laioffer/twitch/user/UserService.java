package com.laioffer.twitch.user;




import com.laioffer.twitch.db.UserRepository;
import com.laioffer.twitch.db.entity.UserEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    //dependencies:
    private final UserDetailsManager userDetailsManager; //appConfig;
    private final PasswordEncoder passwordEncoder;//appConfig;
    private final UserRepository userRepository;

    //Constructor:
    public UserService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    @Transactional
    public void register(String username, String password, String firstName, String lastName) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();//来自spring package;.roles是权限设置,写到database的authorities里面;
        //目前firstName,lastName是空的;
        //先创建user的object,再写进去firstName,lastName;
        userDetailsManager.createUser(user);//到这里firstName和lastName都没写进来，user details不管具体名字、性别等等;
        userRepository.updateNameByUsername(username, firstName, lastName);//相当于repository和spring user details协作;
        //也可以创建authority repository把这些硬写进去;
    }


    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

