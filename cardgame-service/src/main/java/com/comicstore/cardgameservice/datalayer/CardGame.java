package com.comicstore.cardgameservice.datalayer;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "card_games")
@Data
public class CardGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private CardIdentifier cardIdentifier;

    @Column(name = "card_game_name", unique = true)
    private String cardGameName;

    private String company;

    private LocalDate releaseDate;

    private Boolean isActive;

    public CardGame() {
        this.cardIdentifier = new CardIdentifier();
    }

    public CardGame(String cardGameName, String company, LocalDate releaseDate, Boolean isActive) {
        this.cardIdentifier = new CardIdentifier();
        this.cardGameName = cardGameName;
        this.company = company;
        this.releaseDate = releaseDate;
        this.isActive = isActive;
    }
}
