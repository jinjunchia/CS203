package com.cs203.cs203system.enums;

/**
 * Scheduled → Match is set up.
 * In Progress → Players begin the match.
 * Completed → Winner is decided.
 * Forfeited / Disqualified → Match ends early due to a player's action.
 * Postponed / Paused → Match temporarily halted.
 * Cancelled → Match won't happen.
 * Pending Review → Match needs further investigation before declaring a result.
 */
public enum MatchStatus {
//    POSTPONED,
//    FORFEITED,
//    PENDING_REVIEW,
//    TIED,
//    DISQUALIFIED,
//    PAUSED
    PLANNED, ONGOING, COMPLETED, CANCELLED, BYE, WAITING
}
