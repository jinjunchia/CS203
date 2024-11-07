package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Admin;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.model.Tournament;
import com.cs203.cs203system.service.impl.MailServiceImpl;
import com.cs203.cs203system.service.impl.MatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailServiceImpl mailService;

    @Mock
    private MatchServiceImpl matchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void sendEmail_ShouldSendEmailWithGivenDetails() {
        // Arrange
        String toEmail = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        mailService.sendEmail(toEmail, subject, body);

        // Assert: Verify mailSender's send method was called with the expected message
        verify(mailSender, times(1)).send(argThat((SimpleMailMessage message) ->
                message.getTo()[0].equals(toEmail) &&
                        message.getSubject().equals(subject) &&
                        message.getText().equals(body)
        ));
    }



    @Test
    void sendUpcomingMatchReminders_ShouldSendRemindersForUpcomingMatches() {
        // Arrange
        Player player1 = new Player();
        player1.setName("Connor McGregor");
        player1.setEmail("connor@example.com");

        Player player2 = new Player();
        player2.setName("Khabib Nurmagomedov");
        player2.setEmail("khabib@example.com");

        Tournament tournament = new Tournament();
        tournament.setName("Spring Championship");
        tournament.setLocation("Main Arena");

        Match match = new Match();
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        LocalDateTime dateTime = LocalDateTime.of(2025, 10, 1, 14, 30, 0);
        match.setMatchDate(dateTime.plusDays(1)); // Match scheduled for tomorrow
        match.setTournament(tournament);

        List<Match> upcomingMatches = Collections.singletonList(match);
        when(matchService.getMatchesOneDayBeforeMatch()).thenReturn(upcomingMatches);

        // Act
        mailService.sendUpcomingMatchReminders();

        // Assert for Player 1
        verify(mailSender, times(1)).send(argThat((SimpleMailMessage message) ->
                message.getTo()[0].equals("connor@example.com") &&
                        message.getSubject().equals("Upcoming Match Reminder") &&
                        message.getText().contains("Hello " + player1.getName())
        ));

        // Assert for Player 2
        verify(mailSender, times(1)).send(argThat((SimpleMailMessage message) ->
                message.getTo()[0].equals("khabib@example.com") &&
                        message.getSubject().equals("Upcoming Match Reminder") &&
                        message.getText().contains("Hello " + player2.getName())
        ));
    }
}