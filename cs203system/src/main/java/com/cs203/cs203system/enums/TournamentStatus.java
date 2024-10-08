package com.cs203.cs203system.enums;

/**
 * The {@code TournamentStatus} enum represents the current status of a tournament.
 * <p>
 * This enum tracks whether the tournament is scheduled, currently ongoing, or has been completed.
 * </p>
 *
 * <ul>
 *  <li>{@link #SCHEDULED} - The tournament is scheduled to take place but has not yet started.</li>
 *  <li>{@link #ONGOING} - The tournament is currently in progress.</li>
 *  <li>{@link #COMPLETED} - The tournament has concluded.</li>
 * </ul>
 */
public enum TournamentStatus {
    /**
     * The tournament is scheduled to take place but has not yet started.
     */
    SCHEDULED,

    /**
     * The tournament is currently ongoing.
     */
    ONGOING,

    /**
     * The tournament has been completed.
     */
    COMPLETED
}
