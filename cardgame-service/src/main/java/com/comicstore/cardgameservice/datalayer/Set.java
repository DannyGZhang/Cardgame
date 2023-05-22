package com.comicstore.cardgameservice.datalayer;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;


@Entity
@Table(name = "card_game_sets")
@Data
public class Set {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private SetIdentifier setIdentifier;

    private CardIdentifier cardIdentifier;

    @Column(unique = true)
    private String name;

    private LocalDate releaseDate;

    private int numberOfCards;

    public Set() {
        this.setIdentifier = new SetIdentifier();
    }

    public Set(CardIdentifier cardIdentifier, String name, LocalDate releaseDate, int numberOfCards) {
        this.setIdentifier = new SetIdentifier();
        this.cardIdentifier = cardIdentifier;
        this.name = name;
        this.releaseDate = releaseDate;
        this.numberOfCards = numberOfCards;
    }
}
