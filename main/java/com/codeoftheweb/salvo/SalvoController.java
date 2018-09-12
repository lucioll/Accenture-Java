package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @RequestMapping("/players")
    public List<Object> getAllPlayers() {
        return playerRepository.findAll()
            .stream()
            .map(player -> playersDTO(player))
            .collect(Collectors.toList());}


    @Autowired
    private GameRepository gameRepository;

    @RequestMapping("/games")
    public List<Object> getAllGames() {
        return gameRepository.findAll()
                .stream()
                .map(game -> gamesDTO(game))
                .collect(Collectors.toList());
    }

    @Autowired
    private ShipRepository shipRepository;

    @RequestMapping("/ships")
    public List<Object> getAllShips() {
        return shipRepository.findAll()
                .stream()
                .map(ship -> shipsDTO(ship))
                .collect(Collectors.toList());
    }

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @RequestMapping("/game_view/{gamePlayerId}")
    public Map<String, Object> findGamePlayerView(@PathVariable long gamePlayerId) {
        return game_viewDTO(gamePlayerRepository.getOne(gamePlayerId));
    }

    @Autowired
    private SalvoRepository salvoRepository;

    @RequestMapping("/salvoes")
    public List<Object> getAllSalvoes() {
        return salvoRepository.findAll().stream().map(salvo -> salvoDTO(salvo)).collect(Collectors.toList());
    }

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping("/scores")
    public List<Object> getAllScores() {
        return scoreRepository.findAll().stream().map(score -> scoreDTO(Optional.ofNullable(score))).
                collect(Collectors.toList());
    }

    @RequestMapping("/leaderBoard")
    public List<Object> getLeaderBoard() {
        return playerRepository.findAll().stream().map(player -> leaderBoardDTO(player)).
                collect(Collectors.toList());
    }

    private Map<String,Object> leaderBoardDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        Map<String, Integer> dict = player.getTotalScores();
        dto.put("name",player.getUserName());
        dto.put("total", player.getLeaderboardScore());
        dto.put("won", dict.get("won"));
        dto.put("lost", dict.get("lost"));
        dto.put("tied", dict.get("tied"));

        return dto;
    }

    private Map<String, Object> scoreDTO(Optional<Score> score) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        if (score.isPresent()) {
            dto.put("id", score.get().getId());
            dto.put("gameID", score.get().getGame().getId());
            dto.put("playerID", score.get().getPlayer().getId());
            dto.put("finishDate", score.get().getFinishDate());
        } else {
            dto.put("id", null);
            dto.put("gameID", null);
            dto.put("playerID", null);
            dto.put("finishDate", null);
        }
        return dto;
    }

    private Map<String, Object> salvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("turn", salvo.getTurn());
        dto.put("player", salvo.getGamePlayer().getPlayer());
        dto.put("locations", salvo.getLocations());

        return dto;
    }

    private Map<String, Object> game_viewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("creationDate", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(game_player ->
                gamePlayersDTO(game_player)).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> shipsDTO(ship)).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(game_player ->
                game_player.getSalvoes().stream().map(salvo -> salvoDTO(salvo))).collect(Collectors.toList()));
                /*.flatMap(game_player ->
                game_player.getSalvoes().stream().map(salvo -> salvoDTO(salvo)).collect(Collectors.toList())).
                collect(Collectors.toList()));*/
        return dto;
    }

    private Map<String,Object> gamesDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("creationDate", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayersDTO(gamePlayer)).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gamePlayer -> scoreDTO(gamePlayer.getScore())));

        return dto;
    }

    private Map<String, Object> playersDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("userName", player.getUserName());

        return dto;
    }

    private Map<String, Object> gamePlayersDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", gamePlayer.getPlayer());

        return dto;
    }

    private Map<String, Object> shipsDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", ship.getId());
        dto.put("type", ship.getType());
        dto.put("locations", ship.getLocations());

        return dto;
    }
}
