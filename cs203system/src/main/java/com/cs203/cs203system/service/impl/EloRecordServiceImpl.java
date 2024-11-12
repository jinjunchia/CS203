package com.cs203.cs203system.service.impl;

import com.cs203.cs203system.exceptions.NotFoundException;
import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import com.cs203.cs203system.service.EloRecordService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing Elo records.
 *
 * This service provides methods to create, update, retrieve, and delete Elo records,
 * which track rating changes for players based on match results.
 */
@Service
public class EloRecordServiceImpl implements EloRecordService {

    private final EloRecordRepository eloRecordRepository;

    /**
     * Constructs an EloRecordServiceImpl with the necessary dependency.
     *
     * @param eloRecordRepository the repository for accessing Elo records
     */
    @Autowired
    public EloRecordServiceImpl(EloRecordRepository eloRecordRepository) {
        this.eloRecordRepository = eloRecordRepository;
    }

    /**
     * Retrieves all Elo records.
     *
     * @return a list of all {@link EloRecord} entities.
     */
    @Override
    public List<EloRecord> findAllEloRecords() {
        return eloRecordRepository.findAll();
    }

    /**
     * Retrieves all Elo records for a specific player.
     *
     * @param playerId the ID of the player whose Elo records are to be retrieved
     * @return a list of {@link EloRecord} entities associated with the player
     */
    @Override
    public List<EloRecord> findAllEloRecordsForPlayer(Long playerId) {
        return eloRecordRepository.findByPlayer_Id(playerId);
    }

    /**
     * Retrieves an Elo record by its ID.
     *
     * @param id the ID of the Elo record to retrieve
     * @return the {@link EloRecord} entity with the specified ID
     * @throws NotFoundException if the Elo record with the given ID does not exist
     */
    @Override
    public EloRecord findEloRecordById(Long id) {
        Optional<EloRecord> eloRecord = eloRecordRepository.findById(id);
        return eloRecord.orElseThrow(() -> new NotFoundException("Elo Record not found for id: " + id));
    }

    /**
     * Saves a new Elo record.
     *
     * @param eloRecord the {@link EloRecord} entity to save
     */
    @Override
    @Transactional
    public void saveEloRecord(EloRecord eloRecord) {
        eloRecordRepository.save(eloRecord);
    }

    /**
     * Updates an existing Elo record by its ID.
     *
     * @param id the ID of the Elo record to update
     * @param eloRecordDetails the details to update in the Elo record
     * @throws NotFoundException if the Elo record with the given ID does not exist
     */
    @Override
    @Transactional
    public void updateEloRecord(Long id, EloRecord eloRecordDetails) {
        EloRecord eloRecord = findEloRecordById(id);

        eloRecord.setPlayer(eloRecordDetails.getPlayer());
        eloRecord.setMatch(eloRecordDetails.getMatch());
        eloRecord.setOldRating(eloRecordDetails.getOldRating());
        eloRecord.setNewRating(eloRecordDetails.getNewRating());
        eloRecord.setChangeReason(eloRecordDetails.getChangeReason());
        eloRecord.setDate(eloRecordDetails.getDate());

        eloRecordRepository.save(eloRecord);
    }

    /**
     * Deletes an Elo record by its ID.
     *
     * @param id the ID of the Elo record to delete
     * @throws NotFoundException if the Elo record with the given ID does not exist
     */
    @Override
    @Transactional
    public void deleteEloRecord(Long id) {
        EloRecord eloRecord = findEloRecordById(id);
        eloRecordRepository.delete(eloRecord);
    }

    /**
     * Creates and saves a new Elo record based on a player's match result.
     *
     * This method tracks changes in the player's Elo rating by saving the old and
     * new ratings, as well as the reason for the change and the associated match.
     *
     * @param player the player whose Elo record is being saved
     * @param match the match associated with the Elo change
     * @param oldRating the player's Elo rating before the match
     * @param newRating the player's Elo rating after the match
     * @param reason the reason for the Elo change
     */
    @Override
    @Transactional
    public void saveEloRecord(Player player, Match match, double oldRating, double newRating, String reason) {
        EloRecord record = EloRecord.builder()
                .player(player)
                .match(match)
                .oldRating(oldRating)
                .newRating(newRating)
                .changeReason(reason)
                .date(LocalDateTime.now())
                .build();

        eloRecordRepository.save(record);
    }
}

