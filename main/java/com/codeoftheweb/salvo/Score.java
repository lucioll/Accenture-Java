package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    private float score;

    private LocalDateTime finishDate;

    public Score(){}

    public Score(Game game, Player player, LocalDateTime finishTime, float point) {
        this.game = game;
        this.player = player;
        this.finishDate = finishTime;
        this.score = point;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }


    public Player getPlayer() {
        return player;
    }

    public LocalDateTime getFinishDate() {
        return finishDate;
    }
}
