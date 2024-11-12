package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.EloRecordResponseDto;
import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.dtos.EloRecordResponseMapper;
import com.cs203.cs203system.service.EloRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for managing Elo Records.
 * Provides CRUD operations for Elo Records.
 */
@RestController
@RequestMapping("/api/elo-records")
public class EloRecordController {
    private final EloRecordService eloRecordService;
    private final EloRecordResponseMapper eloRecordResponseMapper;

    /**
     * Constructs an EloRecordController with the required service.
     *
     * @param eloRecordService the service for EloRecord operations
     */
    @Autowired
    public EloRecordController(EloRecordService eloRecordService,
                               EloRecordResponseMapper eloRecordResponseMapper) {
        this.eloRecordService = eloRecordService;
        this.eloRecordResponseMapper = eloRecordResponseMapper;
    }

    /**
     * Retrieves all Elo Records.
     *
     * @return ResponseEntity containing a list of EloRecords and an HTTP status of OK.
     */
    @Operation(summary = "Get all Elo Records", description = "Retrieves a list of all Elo Records.")
    @ApiResponse(responseCode = "200", description = "List of Elo Records retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EloRecord.class)))
    @GetMapping
    public ResponseEntity<List<EloRecord>> getAllEloRecords() {
        List<EloRecord> eloRecords = eloRecordService.findAllEloRecords();
        return new ResponseEntity<>(eloRecords, HttpStatus.OK);
    }

    /**
     * Retrieves an Elo Record by its ID.
     *
     * @param id The ID of the Elo Record to be retrieved.
     * @return ResponseEntity containing the EloRecord and an HTTP status of OK.
     */
    @Operation(summary = "Get Elo Record by ID", description = "Retrieves a specific Elo Record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elo Record retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EloRecord.class))),
            @ApiResponse(responseCode = "404", description = "Elo Record not found",
                    content = @Content)
    })

    /**
     * Retrieves an Elo record by its ID.
     *
     * This method fetches a specific Elo record based on the provided ID and returns it as a response.
     *
     * @param id the ID of the Elo record to retrieve
     * @return a {@link ResponseEntity} containing the {@link EloRecordResponseDto} of the specified Elo record
     *         and an HTTP 200 OK status
     */
    @GetMapping("/{id}")
    public ResponseEntity<EloRecordResponseDto> getEloRecordById(@PathVariable Long id) {
        EloRecord eloRecord = eloRecordService.findEloRecordById(id);
        return new ResponseEntity<>(eloRecordResponseMapper.toDto(eloRecord), HttpStatus.OK);
    }

    /**
     * Retrieves all Elo records for a specific player.
     *
     * This method fetches all Elo records associated with the specified player ID and returns them as a list.
     *
     * @param playerId the ID of the player whose Elo records are to be retrieved
     * @return a {@link ResponseEntity} containing a list of {@link EloRecordResponseDto} for the player's Elo records
     *         and an HTTP 200 OK status
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<EloRecordResponseDto>> getAllEloRecordsForPlayer(@PathVariable Long playerId) {
        List<EloRecord> eloRecord = eloRecordService.findAllEloRecordsForPlayer(playerId);
        return new ResponseEntity<>(eloRecord
                .stream()
                .map(eloRecordResponseMapper::toDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * Creates a new Elo Record.
     *
     * @param eloRecord The Elo Record to be created.
     * @return ResponseEntity containing the created EloRecord and an HTTP status of CREATED.
     */
    @Operation(summary = "Create a new Elo Record", description = "Creates a new Elo Record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Elo Record created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EloRecord.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<EloRecordResponseDto> createEloRecord(@RequestBody EloRecord eloRecord){
        eloRecordService.saveEloRecord(eloRecord);
        return new ResponseEntity<>(eloRecordResponseMapper.toDto(eloRecord), HttpStatus.CREATED);
    }

    /**
     * Updates an existing Elo Record by its ID.
     *
     * @param id The ID of the Elo Record to be updated.
     * @param eloRecordDetails The updated details of the Elo Record.
     * @return ResponseEntity containing the updated EloRecord and an HTTP status of OK.
     */
    @Operation(summary = "Update an Elo Record", description = "Updates an existing Elo Record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Elo Record updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = EloRecord.class))),
            @ApiResponse(responseCode = "404", description = "Elo Record not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EloRecordResponseDto> updateEloRecord(@PathVariable Long id, @RequestBody EloRecord eloRecordDetails){
        eloRecordService.updateEloRecord(id, eloRecordDetails);
        return new ResponseEntity<>(eloRecordResponseMapper.toDto(eloRecordDetails), HttpStatus.OK);
    }

    /**
     * Deletes an Elo Record by its ID.
     *
     * @param id The ID of the Elo Record to be deleted.
     * @return ResponseEntity with an HTTP status of NO_CONTENT.
     */
    @Operation(summary = "Delete an Elo Record", description = "Deletes an existing Elo Record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Elo Record deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Elo Record not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEloRecord(@PathVariable Long id){
        eloRecordService.deleteEloRecord(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
