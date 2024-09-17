package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;

public interface EloRecordService {
    void saveEloRecord(Player player, Match match, double oldRating, double newRating, String reason);
}