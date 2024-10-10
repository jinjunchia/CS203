package com.cs203.cs203system.enums;

/**
 * The {@code MatchBracket} enum represents the two possible brackets
 * in a double-elimination tournament: UPPER and LOWER.
 * <p>
 * In a double-elimination format, participants compete in either the
 * upper bracket (for players who have not lost any matches) or the
 * lower bracket (for players who have lost one match). This enum
 * helps in identifying which bracket a particular match or participant
 * belongs to during the tournament.
 * </p>
 *
 * <ul>
 *  <li>{@link #UPPER} - Represents the upper bracket.</li>
 *  <li>{@link #LOWER} - Represents the lower bracket.</li>
 * </ul>
 */
public enum MatchBracket {
    /**
     * The upper bracket, typically used for participants who have
     * not lost any matches in a double-elimination tournament.
     */
    UPPER,

    /**
     * The lower bracket, typically used for participants who have
     * lost one match in a double-elimination tournament but are
     * still competing.
     */
    LOWER,

    FINAL,

    GRAND_FINAL
}
