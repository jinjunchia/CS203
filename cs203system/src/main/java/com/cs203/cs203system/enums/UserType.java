package com.cs203.cs203system.enums;

/**
 * The {@code UserType} enum defines the different roles or types of users
 * within the tournament management system.
 * <p>
 * This enum helps in categorizing users based on their roles, such as
 * administrators who manage the system and players who participate in the tournament.
 * </p>
 *
 * <ul>
 *  <li>{@link #ROLE_ADMIN} - Represents a user with administrative privileges who can manage and control the tournament system.</li>
 *  <li>{@link #ROLE_PLAYER} - Represents a player who is participating in the tournament.</li>
 * </ul>
 */
public enum UserType {

    /**
     * Represents an administrative user with special privileges to manage and control the tournament system.
     */
    ROLE_ADMIN,

    /**
     * Represents a player who is participating in the tournament.
     */
    ROLE_PLAYER
}
