package com.softserve.booksCatalogPrototype.event;

import com.softserve.booksCatalogPrototype.model.Role;
import com.softserve.booksCatalogPrototype.model.RoleName;
import com.softserve.booksCatalogPrototype.model.User;
import com.softserve.booksCatalogPrototype.repository.RoleRepository;
import com.softserve.booksCatalogPrototype.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class AdminInitListener {
    private static final Logger logger = LoggerFactory.getLogger(AdminInitListener.class);

    @Value("${admin.id}")
    private String ID;

    @Value("${admin.name}")
    private String ADMIN_NAME;

    @Value("${admin.username}")
    private String USERNAME;

    @Value("${admin.email}")
    private String EMAIL;

    @Value("${admin.password}")
    private String PASSWORD;

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AdminInitListener(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSomething() throws Exception{
        String password = passwordEncoder.encode(PASSWORD);
        if (roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()){
            if (userRepository.findById(ID).isPresent()){
                logger.info("ROLE_ADMIN and admin user is already present in the database");
                return;
            } else {
                Role role = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new Exception("Did not find a role admin"));
                Set<Role> roles = new HashSet<>();
                roles.add(role);
                User admin = new User(ID, ADMIN_NAME, USERNAME, EMAIL, password, roles);
                userRepository.save(admin);
                logger.info("ROLE_ADMIN is present but user admin was created");
                return;
            }
        } else {
            Role role = new Role(RoleName.ROLE_ADMIN);
            Role saved = roleRepository.save(role);
            if (userRepository.findById(ID).isPresent()){
                User user = userRepository.findById(ID).orElseThrow(() -> new Exception("Did not find a user"));
                Set<Role> roles = new HashSet<>();
                roles.add(saved);
                user.setRoles(roles);
                userRepository.save(user);
                logger.info("ROLE_ADMIN was created but user admin is present");
                return;
            } else {
                Set<Role> roles = new HashSet<>();
                roles.add(saved);
                User admin = new User(ID, ADMIN_NAME, USERNAME, EMAIL, password, roles);
                userRepository.save(admin);
                logger.info("ROLE_ADMIN and user admin were created");
                return;
            }
        }
    }

}
