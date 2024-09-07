package com.cs203.cs203system;

import com.cs203.cs203system.enums.Role;
import com.cs203.cs203system.model.Team;
import com.cs203.cs203system.model.User;
import com.cs203.cs203system.repository.TeamRepository;
import com.cs203.cs203system.repository.UserRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class UserDataLoader implements CommandLineRunner {
    private final Faker faker = new Faker();

    private final UserRepository userRepository;

    @Autowired
    public UserDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 200; i++) {
            User user = User.builder()
                    .email(faker.internet().emailAddress())
                    .password(faker.internet().password())
                    .username(faker.internet().password())
                    .role(Role.ROLE_MANAGER)
                    .build();

            userRepository.save(user);
        }
    }
}
