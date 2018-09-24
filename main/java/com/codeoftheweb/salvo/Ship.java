package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.List;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name="locations")
    private List<String> locations;

    private String type;

    public Ship() { }

    public Ship(String type, List<String> locations) {
        this.type = type;
        this.locations  = locations;
    }

    public Ship(GamePlayer gamePlayer, String type, List<String> locations) {
        this.gamePlayer = gamePlayer;
        this.type = type;
        this.locations  = locations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
}
