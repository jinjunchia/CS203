package com.cs203.cs203system.dtos;

public class MatchStatsUpdateRequest {
    private Integer punchesPlayer1;
    private Integer punchesPlayer2;
    private Integer dodgesPlayer1;
    private Integer dodgesPlayer2;
    private boolean koByPlayer1;
    private boolean koByPlayer2;

    // Getters and Setters
    public Integer getPunchesPlayer1() {
        return punchesPlayer1;
    }

    public void setPunchesPlayer1(Integer punchesPlayer1) {
        this.punchesPlayer1 = punchesPlayer1;
    }

    public Integer getPunchesPlayer2() {
        return punchesPlayer2;
    }

    public void setPunchesPlayer2(Integer punchesPlayer2) {
        this.punchesPlayer2 = punchesPlayer2;
    }

    public Integer getDodgesPlayer1() {
        return dodgesPlayer1;
    }

    public void setDodgesPlayer1(Integer dodgesPlayer1) {
        this.dodgesPlayer1 = dodgesPlayer1;
    }

    public Integer getDodgesPlayer2() {
        return dodgesPlayer2;
    }

    public void setDodgesPlayer2(Integer dodgesPlayer2) {
        this.dodgesPlayer2 = dodgesPlayer2;
    }

    public boolean isKoByPlayer1() {
        return koByPlayer1;
    }

    public void setKoByPlayer1(boolean koByPlayer1) {
        this.koByPlayer1 = koByPlayer1;
    }

    public boolean isKoByPlayer2() {
        return koByPlayer2;
    }

    public void setKoByPlayer2(boolean koByPlayer2) {
        this.koByPlayer2 = koByPlayer2;
    }
}
