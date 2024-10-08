package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;

import java.util.List;

public interface EloRecordService {
    void saveEloRecord(Player player, Match match, double oldRating, double newRating, String reason);
    List<EloRecord> findAllEloRecords();
    EloRecord findEloRecordById(Long id);
    void saveEloRecord(EloRecord eloRecord);
    void updateEloRecord(Long id, EloRecord eloRecordDetails);
    void deleteEloRecord(Long id);
}