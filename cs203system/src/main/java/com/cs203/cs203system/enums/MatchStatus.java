package com.cs203.cs203system.enums;

/**
 * The {@code MatchStatus} enum represents the various states that a match
 * can be in during a tournament.
 * <p>
 * This enum helps to track the current status of a match, whether it's scheduled
 * to be played, already completed, cancelled, or waiting for an opponent.
 * It also accounts for matches where one team advances automatically (BYE).
 * </p>
 *
 * <ul>
 *  <li>{@link #SCHEDULED} - The match is scheduled but has not yet taken place.</li>
 *  <li>{@link #COMPLETED} - The match has been played and finished.</li>
 *  <li>{@link #CANCELLED} - The match has been cancelled and will not take place.</li>
 *  <li>{@link #BYE} - The match is a "bye", meaning a participant advances without a match.</li>
 *  <li>{@link #WAITING} - The match is waiting for a participant or another condition to be fulfilled before being scheduled.</li>
 * </ul>
 */
public enum MatchStatus {

    /**
     * The match is scheduled but has not yet been played.
     */
    SCHEDULED,

    /**
     * The match has been completed and the results are finalized.
     */
    COMPLETED,

    /**
     * The match has been completed and results are yet to be placed into database
     */
    PENDING,

    /**
     * The match has been cancelled and will not take place.
     */
    CANCELLED,

    /**
     * The match is a "bye", meaning a participant advances without playing a match.
     */
    BYE,

    /**
     * The match is waiting for other conditions to be met, such as determining
     * the opponent or scheduling.
     */
    WAITING
}
