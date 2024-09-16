package com.cs203.cs203system.service;

import com.cs203.cs203system.model.Rules;

import java.util.Optional;

public interface RulesService {

    Rules createRules(Rules rule);

    Optional<Rules> getRulesById(String description);

}
