package com.cs203.cs203system.service;

import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Match;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.repository.EloRecordRepository;
import com.cs203.cs203system.service.impl.EloRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class EloRecordServiceTest {
    @Mock
    private EloRecordRepository eloRecordRepository;
    @InjectMocks
    private EloRecordServiceImpl eloRecordService;
    private EloRecord eloRecord;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Player player = new Player();
        Match match = new Match();

        eloRecord = EloRecord.builder()
                .id(1L)
                .player(player)
                .match(match)
                .oldRating(1200.0)
                .newRating(1250.0)
                .changeReason("Won the match")
                .date(LocalDateTime.now())
                .build();
    }

    @Test
    void findAllEloRecords_ShouldReturnAllRecords() {
        when(eloRecordRepository.findAll()).thenReturn(List.of(eloRecord));

        List<EloRecord> records = eloRecordService.findAllEloRecords();

        assertEquals(1, records.size());
        verify(eloRecordRepository, times(1)).findAll();
    }

    @Test
    void findEloRecordById_ShouldReturnRecord_WhenRecordExists() {
        when(eloRecordRepository.findById(1L)).thenReturn(Optional.of(eloRecord));

        EloRecord foundRecord = eloRecordService.findEloRecordById(1L);

        assertNotNull(foundRecord);
        assertEquals(eloRecord.getId(), foundRecord.getId());
        verify(eloRecordRepository, times(1)).findById(1L);
    }

    @Test
    void findEloRecordById_ShouldThrowException_WhenRecordNotFound() {
        when(eloRecordRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            eloRecordService.findEloRecordById(1L);
        });

        assertEquals("Elo Record not found for id: 1", exception.getMessage());
        verify(eloRecordRepository, times(1)).findById(1L);
    }

    @Test
    void saveEloRecord_SavingRecord_ShouldSaveRecord() {

        when(eloRecordRepository.save(any(EloRecord.class))).thenReturn(eloRecord);

        eloRecordService.saveEloRecord(eloRecord);

        verify(eloRecordRepository, times(1)).save(eloRecord);
    }

    @Test
    void updateEloRecord_UpdatingEloRecord_ShouldUpdateExistingEloRecord() {
        when(eloRecordRepository.findById(1L)).thenReturn(Optional.of(eloRecord));

        EloRecord updatedDetails = EloRecord.builder()
                .player(new Player())
                .match(new Match())
                .oldRating(1300.0)
                .newRating(1350.0)
                .changeReason("Updated")
                .date(LocalDateTime.now())
                .build();

        eloRecordService.updateEloRecord(1L, updatedDetails);

        verify(eloRecordRepository, times(1)).save(eloRecord);
        assertEquals(1300, eloRecord.getOldRating());
        assertEquals(1350, eloRecord.getNewRating());
        assertEquals("Updated", eloRecord.getChangeReason());
    }

    @Test
    void deleteEloRecord_DeletingEloRecord_ShouldDeleteEloRecord() {
        when(eloRecordRepository.findById(1L)).thenReturn(Optional.of(eloRecord));

        eloRecordService.deleteEloRecord(1L);

        verify(eloRecordRepository, times(1)).delete(eloRecord);
    }
}
