package com.codeoftheweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "game")
    private List<GamePlayer> gamePlayers;

    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "game")
    private List<Score> scores;

    public Game() {
        this.creationDate = LocalDateTime.now();
    }

    public Game(LocalDateTime date) {
        this.creationDate = date;
    }

    public String toString() {
        return creationDate.toString();
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public long getId() {
        return id;
    }

    public void setGamePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    @JsonIgnore
    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

}