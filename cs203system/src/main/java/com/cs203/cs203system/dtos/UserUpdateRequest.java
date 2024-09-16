package com.cs203.cs203system.dtos;

import com.cs203.cs203system.model.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Optional;

@Data
public class UserUpdateRequest implements Serializable {

    Optional<String> name = Optional.empty();

    Optional<Integer> id = Optional.empty();

//    Optional<User> findUserByEmailName(String email);
//
//    Optional<String> findUserEmail(String email);

}
