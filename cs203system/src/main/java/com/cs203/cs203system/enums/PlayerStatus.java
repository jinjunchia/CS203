package com.cs203.cs203system.enums;

/**
 * The {@code PlayerStatus} enum represents the status of a player in a tournament.
 * <p>
 * This enum tracks whether a player is still qualified to compete or has been eliminated.
 * </p>
 *
 * <ul>
 *  <li>{@link #QUALIFIED} - The player is still qualified to compete in the tournament.</li>
 *  <li>{@link #ELIMINATED} - The player has been eliminated from the tournament.</li>
 * </ul>
 */
public enum PlayerStatus {
    /**
     * The player is qualified and still competing in the tournament.
     */
    QUALIFIED,

    /**
     * The player has been eliminated and is no longer competing in the tournament.
     */
    ELIMINATED
}
