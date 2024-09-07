package com.cs203.cs203system;

import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.repository.TeamRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Order(3)
public class TeamDataLoader implements CommandLineRunner {
    private final Faker faker = new Faker();

    private final TeamRepository teamRepository;

    @Autowired
    public TeamDataLoader(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();

        for (int i = 0; i < 200; i++ ) {
            Team team = Team.builder()
                    .name(faker.team().name())
                    .ranking(random.nextInt(32+1)).build();

            teamRepository.save(team);
        }

    }
}
