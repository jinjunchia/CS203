package com.cs203.cs203system.enums;

/**
 * The {@code PlayerBracket} enum represents the different brackets
 * a player can be in during a double-elimination tournament.
 * <p>
 * Players in a double-elimination tournament can either be in the upper
 * bracket (for players who have not yet lost), the lower bracket (for players
 * who have lost once but are still competing), or transitioning from the upper
 * to the lower bracket.
 * </p>
 *
 * <ul>
 *  <li>{@link #UPPER} - The player is in the upper bracket, having not lost any matches.</li>
 *  <li>{@link #LOWER} - The player is in the lower bracket, having lost one match.</li>
 *  <li>{@link #UPPER_TO_LOWER} - The player is transitioning from the upper bracket to the lower bracket after their first loss.</li>
 * </ul>
 */
public enum PlayerBracket {
    /**
     * The player is in the upper bracket, having not lost any matches.
     */
    UPPER,

    /**
     * The player is in the lower bracket, having lost one match but still eligible to compete.
     */
    LOWER,

    /**
     * The player has moved from the upper bracket to the lower bracket after their first loss.
     */
    UPPER_TO_LOWER
}
