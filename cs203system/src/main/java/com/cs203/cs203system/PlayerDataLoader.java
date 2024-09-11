package com.cs203.cs203system;

import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.PlayerRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * You can use this for testing. I will
 */
@Component
@Order(1)
public class PlayerDataLoader implements CommandLineRunner {

    private final PlayerRepository playerRepository;
    private final Faker faker = new Faker();

    public PlayerDataLoader(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        // Create and save 20 players
        for (int i = 0; i < 200; i++) {
//            Player player = Player.builder()
//                    .name(faker.funnyName().name())
//                    .rating(1000 + random.nextInt(501)) // Random rating between 1000 and 1500
//                    .build();

//            playerRepository.save(player);
        }

        System.out.println("200 players have been inserted into the database!");
    }
}
