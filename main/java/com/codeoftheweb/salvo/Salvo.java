package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "locations")
    List<String> locations; //Va a ser una lista de dos strings (2 disparos por turno creo)

    private int turn;

    Salvo() {}

    Salvo(GamePlayer gamePlayer, String location, int turn) {
        this.locations = new ArrayList<String>();
        this.locations.add(location);
        this.turn = turn;
        this.gamePlayer = gamePlayer;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public void addSalvoLocation(String location) {
        this.locations.add(location);
    }

    public void setSalvoLocations(List<String> locations) {
        this.locations = locations;
    }
}
