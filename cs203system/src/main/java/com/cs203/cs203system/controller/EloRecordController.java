package com.cs203.cs203system.controller;

import com.cs203.cs203system.dtos.PlayerUpdateRequest;
import com.cs203.cs203system.model.EloRecord;
import com.cs203.cs203system.model.Player;
import com.cs203.cs203system.service.PlayerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/EloRecord")
public class EloRecordController {

}
