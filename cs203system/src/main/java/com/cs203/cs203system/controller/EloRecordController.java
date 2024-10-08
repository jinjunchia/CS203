package com.cs203.cs203system.controller;
import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.service.EloRecordService;
import com.cs203.cs203system.service.EloRecordServiceImpl;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

//CRUD
@RestController
@RequestMapping("/elo-records")
public class EloRecordController {
    private final EloRecordService eloRecordService;

    @Autowired
    public EloRecordController(EloRecordService eloRecordService) {
        this.eloRecordService = eloRecordService;
    }

    @GetMapping
    public ResponseEntity<List<EloRecord>> getAllEloRecords() {
        List<EloRecord> eloRecords = eloRecordService.findAllEloRecords();
        return new ResponseEntity<>(eloRecords, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<EloRecord> getEloRecordById(@PathVariable Long id){
        EloRecord eloRecord = eloRecordService.findEloRecordById(id);
        return new ResponseEntity<>(eloRecord, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EloRecord> createEloRecord(@RequestBody EloRecord eloRecord){
        eloRecordService.saveEloRecord(eloRecord);
        return new ResponseEntity<>(eloRecord, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EloRecord> updateEloRecord(@PathVariable Long id, @RequestBody EloRecord eloRecordDetails){
        eloRecordService.updateEloRecord(id, eloRecordDetails);
        return new ResponseEntity<>(eloRecordDetails, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEloRecord(@PathVariable Long id){
        eloRecordService.deleteEloRecord(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
