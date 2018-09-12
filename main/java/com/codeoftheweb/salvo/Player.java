package com.codeoftheweb.salvo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userName;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private List<GamePlayer> gamePlayers;

    @OneToMany( mappedBy = "player", fetch = FetchType.LAZY)
    private List<Score> scores;

    public Player() { }

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayers.add(gamePlayer);
    }

    public long getId() {
        return id;
    }

    @JsonIgnore
    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public Optional<Score> getScore(Game game) {
        return scores.stream().filter(score -> score.getGame().equals(game)).findFirst();
    }

    public Map<String, Integer> getTotalScores() {
        Map<String, Integer> dict = new LinkedHashMap<String, Integer>();
        int won = 0;
        int lost = 0;
        int tied = 0;
        for (Score score: scores) {
            if (score.getScore() == 1.0) {
                won++;
            } else if ( score.getScore() == 0.5) {
                tied++;
            } else {
                lost++;
            }
        }
        dict.put("won", won);
        dict.put("lost", lost);
        dict.put("tied", tied);
        return dict;
    }

    @JsonIgnore
    public double getLeaderboardScore() {
        Map<String, Integer> dict = getTotalScores();
        return dict.get("won") + dict.get("tied") * 0.5;
    }
}
