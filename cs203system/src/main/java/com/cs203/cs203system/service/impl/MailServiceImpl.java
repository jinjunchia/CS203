//package com.cs203.cs203system.service.impl;
//
//import com.cs203.cs203system.model.Match;
//import com.cs203.cs203system.service.MailService;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Service
//public class MailServiceImpl implements MailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private MatchServiceImpl matchService;
//
//    public void sendEmail(String toEmail, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom("jinjunchia99@gmail.com");
//        message.setTo(toEmail);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//    }
//
//    @Scheduled(cron = "0 0 8 * * ?") // sends every day at 8am
//    public void sendUpcomingMatchReminders() {
//        List<Match> upcomingMatches = matchService.getMatchesOneDayBeforeMatch();
//
//        for (Match match : upcomingMatches) {
//            String matchDate = match.getMatchDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
//            String matchTime = match.getMatchDate().format(DateTimeFormatter.ofPattern("HH:mm"));
//
//            String lineSeparator = System.lineSeparator();
//
//            String body1 = String.join(lineSeparator,
//                    "Hello " + match.getPlayer1().getName() + ",",
//                    "",
//                    "We hope this message finds you well! This is a friendly reminder that you have an upcoming match scheduled as part of " + match.getTournament().getName() + ".",
//                    "",
//                    "Match Details:",
//                    "\t•\tDate: " + matchDate,
//                    "\t•\tTime: " + matchTime,
//                    "\t•\tLocation: " + match.getTournament().getLocation(),
//                    "\t•\tOpponent: " + match.getPlayer2().getName(),
//                    "",
//                    "Please be sure to arrive on time and prepared. Here are a few tips to ensure you’re ready:",
//                    "\t1.\tReview the tournament rules and guidelines.",
//                    "\t2.\tCheck your equipment and ensure everything is in order.",
//                    "\t3.\tAim to arrive at least 30-45 minutes early to allow for setup and warm-up.",
//                    "",
//                    "We wish you the best of luck in your match. Let’s make this tournament an unforgettable experience!",
//                    "",
//                    "Best regards,",
//                    "TournaX"
//            );
//
//            String body2 = String.join(lineSeparator,
//                    "Hello " + match.getPlayer2().getName() + ",",
//                    "",
//                    "We hope this message finds you well! This is a friendly reminder that you have an upcoming match scheduled as part of " + match.getTournament().getName() + ".",
//                    "",
//                    "Match Details:",
//                    "\t•\tDate: " + matchDate,
//                    "\t•\tTime: " + matchTime,
//                    "\t•\tLocation: " + match.getTournament().getLocation(),
//                    "\t•\tOpponent: " + match.getPlayer1().getName(),
//                    "",
//                    "Please be sure to arrive on time and prepared. Here are a few tips to ensure you’re ready:",
//                    "\t1.\tReview the tournament rules and guidelines.",
//                    "\t2.\tCheck your equipment and ensure everything is in order.",
//                    "\t3.\tAim to arrive at least 30-45 minutes early to allow for setup and warm-up.",
//                    "",
//                    "We wish you the best of luck in your match. Let’s make this tournament an unforgettable experience!",
//                    "",
//                    "Best regards,",
//                    "TournaX"
//            );
//
//            sendEmail(match.getPlayer1().getEmail(), "Upcoming Match Reminder", body1);
//            sendEmail(match.getPlayer2().getEmail(), "Upcoming Match Reminder", body2);
//        }
//    }
//}