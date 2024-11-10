//package com.cs203.cs203system;
//
//import com.cs203.cs203system.model.Admin;
//import com.cs203.cs203system.model.Player;
//import com.cs203.cs203system.repository.AdminRepository;
//import com.cs203.cs203system.repository.PlayerRepository;
//import com.cs203.cs203system.repository.UserRepository;
//import net.datafaker.Faker;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//@Component
//@Order(3)
//public class PlayerDataLoader implements CommandLineRunner {
//    private final Faker faker = new Faker();
//
//    private final PlayerRepository playerRepository;
//    private final AdminRepository adminRepository;
//    private final UserRepository userRepository;
//
//    @Autowired
//    public PlayerDataLoader(PlayerRepository playerRepository, AdminRepository adminRepository, UserRepository userRepository) {
//        this.playerRepository = playerRepository;
//        this.adminRepository = adminRepository;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//
//        for (int i = 0; i < 16; i++) {
//            Player player = new Player();
//            player.setName(faker.funnyName().name());
//            String password = "admin";
//            player.setUsername(faker.twitter().userName());
//            player.setEmail(faker.internet().emailAddress());
//            player.setPassword(passwordEncoder.encode(password));
//            playerRepository.save(player);
//        }
//
//        if (!userRepository.existsUserByUsernameIgnoreCase("admin")) {
//            Admin admin = new Admin();
//            String password = "admin";
//            admin.setUsername("admin");
//            admin.setEmail(faker.internet().emailAddress());
//            admin.setPassword(passwordEncoder.encode(password));
//            adminRepository.save(admin);
//        }
//    }
//}
