package com.cs203.cs203system;

import com.cs203.cs203system.enums.TournamentFormat;
import com.cs203.cs203system.enums.TournamentStatus;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.repository.TournamentRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Component
@Order(4)
public class TournamentDataLoader implements CommandLineRunner {
    private final Faker faker = new Faker();

    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentDataLoader(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    public static LocalDate getRandomDateBetween(LocalDate startDate, LocalDate endDate) {
        Random random = new Random();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate); // Get the days between start and end dates
        long randomDays = random.nextLong(daysBetween); // Generate a random number of days within that range
        return startDate.plusDays(randomDays); // Add random days to start date
    }

    @Override
    public void run(String... args) {
        LocalDate startDate = LocalDate.of(1950, 1, 1);
        LocalDate endDate = LocalDate.of(2027, 12, 31);

        for (int i = 0; i < 1; i++) {
            LocalDate randomDate = getRandomDateBetween(startDate, endDate);

            Tournament tournament = Tournament.builder()
                    .startDate(randomDate)
                    .status(TournamentStatus.ONGOING)
                    .name("World Boxing Championship (2024)")
                    .description("The Ultimate Challenge Cup 2024 is an annual, high-stakes " +
                            "tournament that brings together competitors from across the globe " +
                            "to test their skills in an electrifying series of matches. From " +
                            "seasoned professionals to rising stars, this competition showcases " +
                            "talent, strategy, and sportsmanship at its finest. Competitors " +
                            "will face off in both single and team events, battling through " +
                            "intense rounds for the coveted title and ultimate bragging rights.")
                    .location(faker.address().city())
                    .format(TournamentFormat.DOUBLE_ELIMINATION)
                    .status(TournamentStatus.SCHEDULED)
                    .build();

            tournamentRepository.save(tournament);
        }
    }
}
