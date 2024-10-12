package com.cs203.cs203system.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Represents an Admin user in the system.
 * The Admin class extends the {@link User} class and includes additional functionality,
 * such as managing a set of tournaments.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@DiscriminatorValue("ROLE_ADMIN")
public class Admin extends User {
    /**
     * The set of tournaments managed by the Admin.
     * The tournaments are mapped by the "admin" field in the {@link Tournament} class,
     * with cascading and orphan removal enabled to manage persistence.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tournament> tournaments = new LinkedHashSet<>();
}
