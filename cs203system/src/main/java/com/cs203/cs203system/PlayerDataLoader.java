package com.cs203.cs203system;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Order(3)
public class PlayerDataLoader implements CommandLineRunner {
    private final Faker faker = new Faker();

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerDataLoader(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        for (int i = 0; i < 100; i++ ) {
            Player player = new Player();
            player.setName(faker.funnyName().name());
            String password = "admin";
            if (i == 0) {
                player.setUsername("admin");
            } else {
                player.setUsername(faker.funnyName().name());
            }
            player.setEmail(faker.internet().emailAddress());
            player.setPassword(passwordEncoder.encode(password));
            playerRepository.save(player);
        }
    }
}
