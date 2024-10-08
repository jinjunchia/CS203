package com.cs203.cs203system.enums;

/**
 * The {@code TournamentFormat} enum represents the format of a tournament.
 * <p>
 * This enum defines the structure of the tournament, whether it's Swiss,
 * double-elimination, or a hybrid of both.
 * </p>
 *
 * <ul>
 *  <li>{@link #SWISS} - The tournament follows the Swiss system.</li>
 *  <li>{@link #DOUBLE_ELIMINATION} - The tournament follows a double-elimination format.</li>
 *  <li>{@link #HYBRID} - The tournament uses a hybrid format combining Swiss and double-elimination elements.</li>
 * </ul>
 */
public enum TournamentFormat {
    /**
     * A tournament format where participants play several rounds against opponents with similar records (Swiss system).
     */
    SWISS,

    /**
     * A double-elimination format where participants must lose two matches to be eliminated.
     */
    DOUBLE_ELIMINATION,

    /**
     * A hybrid format combining aspects of both the Swiss and double-elimination tournament systems.
     */
    HYBRID
}
