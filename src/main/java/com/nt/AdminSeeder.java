package com.nt;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.nt.model.Authority;
import com.nt.model.Users;
import com.nt.repositary.IUserRepo;

@Component
public class AdminSeeder implements CommandLineRunner {

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.findByUsername("admin").isEmpty()) {
            Users admin = new Users();
            admin.setEnabled(true);
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setUsername("admin");
            
            Authority auth = new Authority();
            auth.setAuthority("ROLE_ADMIN");
            auth.setUser(admin);
            
            admin.setAuthority(Set.of(auth));
            userRepo.save(admin);
            
            System.out.println("---- ADMIN ACCOUNT CREATED: admin/admin123 ----");
        }
    }
}
