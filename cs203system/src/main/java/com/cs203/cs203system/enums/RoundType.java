package com.cs203.cs203system.enums;

/**
 * The {@code RoundType} enum represents the different types of rounds
 * that can occur in a tournament.
 * <p>
 * These rounds define how matches are categorized within the tournament structure.
 * </p>
 *
 * <ul>
 *  <li>{@link #UPPER} - A round in the upper bracket.</li>
 *  <li>{@link #LOWER} - A round in the lower bracket.</li>
 *  <li>{@link #FINAL} - The final round of the tournament.</li>
 *  <li>{@link #SWISS} - A round in a Swiss format tournament.</li>
 *  <li>{@link #DOUBLE_ELIMINATION} - A round in a double-elimination format.</li>
 * </ul>
 */
public enum RoundType {
    /**
     * A round in the upper bracket.
     */
    UPPER,

    /**
     * A round in the lower bracket.
     */
    LOWER,

    /**
     * The final round of the tournament, typically deciding the overall winner.
     */
    FINAL,

    /**
     * A round in a Swiss format tournament, where participants are paired against others with similar records.
     */
    SWISS,

    /**
     * A round in a double-elimination format, where players must lose twice to be eliminated.
     */
    DOUBLE_ELIMINATION
}
