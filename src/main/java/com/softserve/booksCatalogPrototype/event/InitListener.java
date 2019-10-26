package com.softserve.booksCatalogPrototype.event;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.softserve.booksCatalogPrototype.exception.custom.AuthenticationException;
import com.softserve.booksCatalogPrototype.model.Role;
import com.softserve.booksCatalogPrototype.model.RoleName;
import com.softserve.booksCatalogPrototype.model.User;
import com.softserve.booksCatalogPrototype.repository.RoleRepository;
import com.softserve.booksCatalogPrototype.repository.UserRepository;

@Component
public class InitListener {
    private static final Logger logger = LoggerFactory.getLogger(InitListener.class);

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
    public InitListener(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeSomething(){
    	roleAdminCheck();
	    roleUserCheck();
    	adminCheck();
    }

    private void roleAdminCheck(){
	    if (!roleRepository.findByName(RoleName.ROLE_ADMIN).isPresent()){
		    Role role = new Role(RoleName.ROLE_ADMIN);
		    roleRepository.save(role);
		    logger.info("ROLE_ADMIN was created");
	    }
	    return;
    }

    private void adminCheck(){
	    Role role = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AuthenticationException("Did not find ROLE_ADMIN"));
    	if(!userRepository.findById(ID).isPresent()){
		    Set<Role> roles = new HashSet<>();
		    roles.add(role);
		    String password = passwordEncoder.encode(PASSWORD);
		    User admin = new User(ID, ADMIN_NAME, USERNAME, EMAIL, password, roles);
		    userRepository.save(admin);
		    logger.info("Admin was created");
	    }
	    return;
    }

    private void roleUserCheck(){
	    if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()){
		    Role role = new Role(RoleName.ROLE_USER);
		    roleRepository.save(role);
		    logger.info("ROLE_USER was created");
	    }
	    return;
    }

}
