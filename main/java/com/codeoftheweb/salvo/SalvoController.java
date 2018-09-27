package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private SalvoRepository salvoRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @RequestMapping(path = "/players", method = RequestMethod.GET)
    public List<Object> getAllPlayers() {
        return playerRepository.findAll()
                .stream()
                .map(player -> playersDTO(player))
                .collect(Collectors.toList());
    }

    @RequestMapping("/ships")
    public List<Object> getAllShips() {
        return shipRepository.findAll()
                .stream()
                .map(ship -> shipsDTO(ship))
                .collect(Collectors.toList());
    }

    @RequestMapping("/salvoes")
    public List<Object> getAllSalvoes() {
        return salvoRepository.findAll().stream().map(salvo -> salvoDTO(salvo)).collect(Collectors.toList());
    }

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

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(
            @RequestParam String name, @RequestParam String pwd) {

        if (name.isEmpty() || pwd.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        }

        if (playerRepository.findByEmail(name).isPresent()) {
            return new ResponseEntity<>(makeMap("error", "UserName already in use"), HttpStatus.FORBIDDEN);
        }

        playerRepository.save(new Player(name, passwordEncoder.encode(pwd)));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games", method = RequestMethod.GET)
    public Map<String, Object> gameListWithCurrentUserDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Map<String, Object> dto2 = new LinkedHashMap<String, Object>();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            dto.put("player", "Guest");
        } else {
            dto2.put("id", playerRepository.findByEmail(authentication.getName()).get().getId());
            dto2.put("userName", playerRepository.findByEmail(authentication.getName()).get().getEmail());
            dto.put("player", dto2);
        }

        dto.put("games", gameRepository.findAll()
                .stream()
                .map(game -> gamesDTO(game))
                .collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication) {
        if (authentication != null) {
            Map<String, Object> currentUserDTO = gameListWithCurrentUserDTO(authentication);
            Map<String, Object> player_map = (Map<String, Object>) currentUserDTO.get("player");

            Player current_player = playerRepository.getOne((Long) player_map.get("id"));
            Game new_game = new Game(LocalDateTime.now());
            gameRepository.save(new_game);

            GamePlayer new_game_player = new GamePlayer(current_player, new_game);
            new_game_player.setGameState("WAITINGFOROPP");
            gamePlayerRepository.save(new_game_player);

            return new ResponseEntity<>(makeMap("gpid", new_game_player.getId()), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(makeMap("error", "You must login to create a new game."), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> placedShips(@PathVariable long gamePlayerId,
                                                           @RequestBody List<Map<String, Object>> ships,
                                                           Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ResponseEntity<>(makeMap("error", "No user logged in"), HttpStatus.UNAUTHORIZED);
        }

        if (!gamePlayerRepository.findById(gamePlayerId).isPresent()) {
            return new ResponseEntity<>(makeMap("error", "There is no GamePlayer with given ID"),
                    HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        if (!authentication.getName().equals(gamePlayer.getPlayer().getEmail())) {
            return new ResponseEntity<>(makeMap("error", "The current user is not the GamePlayer the ID" +
                    "references"),
                    HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getShips().size() != 0) {
            return new ResponseEntity<>(makeMap("error", "The GamePlayer has ships placed"),
                    HttpStatus.FORBIDDEN);
        }

        GamePlayer opponent = gamePlayer.getGame().getGamePlayers().stream().filter(x -> x != gamePlayer).
                findAny().get();

        for (Map<String, Object> ship : ships) {
            String type = (String) ship.get("shipType");
            List<String> shipLocations = (List<String>) ship.get("shipLocations");
            Ship shipAux = new Ship(gamePlayer, type, shipLocations);
            gamePlayer.addShip(shipAux);
            shipRepository.save(shipAux);
        }

        if (!opponent.getShips().isEmpty() && !gamePlayer.getShips().isEmpty()) {
            gamePlayer.getGame().getGamePlayers().get(0).setGameState("PLAY");
            gamePlayer.getGame().getGamePlayers().get(1).setGameState("WAIT");
        } else {
            gamePlayer.setGameState("WAIT");
        }

        gamePlayerRepository.save(gamePlayer); //PARA QUE SE ACTUALIZE EL REPO
        return new ResponseEntity<>(makeMap("OK", "Ships added"), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/game_view/{gamePlayerId}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, Object>> findGamePlayerView(@PathVariable long gamePlayerId,
                                                                  Authentication authentication) {
        if (authentication != null || !(authentication instanceof AnonymousAuthenticationToken)) {
            GamePlayer game_player = gamePlayerRepository.findById(gamePlayerId).get();
            if (game_player.getPlayer().getEmail().equals(authentication.getName())) {
                return new ResponseEntity<>(game_viewDTO(game_player), HttpStatus.ACCEPTED);
            }
        }
        return new ResponseEntity<>(makeMap("error", "Invalid user"), HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/game/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable long gameId, Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ResponseEntity<>(makeMap("error", "You must login to join a game."), HttpStatus.UNAUTHORIZED);
        }

        if (!gameRepository.findById(gameId).isPresent()) {
            return new ResponseEntity<>(makeMap("error", "No such game"), HttpStatus.FORBIDDEN);
        }

        if (gameRepository.findById(gameId).get().getGamePlayers().size() == 2) {
            return new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
        }

        Map<String, Object> currentUserDTO = gameListWithCurrentUserDTO(authentication);
        Map<String, Object> player_map = (Map<String, Object>) currentUserDTO.get("player");
        Player current_player = playerRepository.getOne((Long) player_map.get("id"));
        Game game = gameRepository.findById(gameId).get();
        game.getGamePlayers().get(0).setGameState("PLACESHIPS"); //CAMBIO DE WAITINGFOROPP A PLACESHIPS

        GamePlayer new_game_player = new GamePlayer(current_player, game);
        new_game_player.setGameState("PLACESHIPS");
        gamePlayerRepository.save(new_game_player);
        return new ResponseEntity<>(makeMap("gpid", new_game_player.getId()), HttpStatus.CREATED);
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> storeSalvo(@PathVariable long gamePlayerId,
                                                          Authentication authentication,
                                                          @RequestBody Map<String, Object> salvo) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return new ResponseEntity<>(makeMap("error", "No user logged in"), HttpStatus.UNAUTHORIZED);
        }

        if (!gamePlayerRepository.findById(gamePlayerId).isPresent()) {
            return new ResponseEntity<>(makeMap("error", "There is no GamePlayer with given ID"),
                    HttpStatus.UNAUTHORIZED);
        }

        GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).get();

        if (!authentication.getName().equals(gamePlayer.getPlayer().getEmail())) {
            return new ResponseEntity<>(makeMap("error", "The current user is not the GamePlayer the ID" +
                    "references"),
                    HttpStatus.UNAUTHORIZED);
        }

        if (gamePlayer.getTurn() > (int) salvo.get("turn")) {
            return new ResponseEntity<>(makeMap("error", "The GamePlayer has submitted a salvo for the turn listed"),
                    HttpStatus.FORBIDDEN);
        }

        GamePlayer opponent = gamePlayer.getGame().getGamePlayers().stream().filter(x -> x !=(gamePlayer)).
                findAny().get();


        Salvo new_salvo = new Salvo(gamePlayer, (List<String>) salvo.get("salvoLocations"), gamePlayer.getTurn());
        gamePlayer.addSalvo(new_salvo);
        gamePlayer.advanceTurn();
        salvoRepository.save(new_salvo);

        setGameStates(gamePlayer, opponent, authentication);

        gamePlayerRepository.save(gamePlayer); // PARA ACTUALIZAR EL REPO
        gamePlayerRepository.save(opponent); // PARA ACTUALIZAR EL REPO
        return new ResponseEntity<>(makeMap("OK", "Salvo added."), HttpStatus.CREATED);
    }

    private void setGameStates(GamePlayer gamePlayer, GamePlayer opponent, Authentication authentication) {
        if (gamePlayer.getTurn() ==  opponent.getTurn()) {
            //Debo verificar si hay un ganador
            ResponseEntity<Map<String, Object>> responseEntity = findGamePlayerView(gamePlayer.getId(), authentication);
            Map<String, Object> game_view = responseEntity.getBody();
            Map<String, Object> hits = (Map<String, Object>) game_view.get("hits");
            List<Map<String, Object>> self_hits = (List<Map<String, Object>>) hits.get("self");
            List<Map<String, Object>> opponent_hits = (List<Map<String, Object>>) hits.get("opponent");

            boolean self_win = verifyAllSunk((Map<String, Object>) opponent_hits.get(opponent_hits.size() - 1));
            boolean opponent_win = verifyAllSunk((Map<String, Object>) self_hits.get(self_hits.size() - 1));
            if (self_win && opponent_win) {
                gamePlayer.setGameState("TIE");
                opponent.setGameState("TIE");
                Score score1 = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), LocalDateTime.now(), 0.5f);
                Score score2 = new Score(opponent.getGame(), opponent.getPlayer(), LocalDateTime.now(), 0.5f);
                scoreRepository.save(score1);
                scoreRepository.save(score2);
            } else if (!self_win && !opponent_win) {
                //Siguen jugando
                gamePlayer.setGameState("WAIT");
                opponent.setGameState("PLAY");
            } else if (self_win) {
                gamePlayer.setGameState("WON");
                opponent.setGameState("LOST");
                Score score1 = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), LocalDateTime.now(), 1.0f);
                Score score2 = new Score(opponent.getGame(), opponent.getPlayer(), LocalDateTime.now(), 0.0f);
                scoreRepository.save(score1);
                scoreRepository.save(score2);
            } else {
                gamePlayer.setGameState("LOST");
                opponent.setGameState("WON");
                Score score1 = new Score(gamePlayer.getGame(), gamePlayer.getPlayer(), LocalDateTime.now(), 0.0f);
                Score score2 = new Score(opponent.getGame(), opponent.getPlayer(), LocalDateTime.now(), 1.0f);
                scoreRepository.save(score1);
                scoreRepository.save(score2);

            }
        } else{
            //Siguen jugando
            gamePlayer.setGameState("WAIT");
            opponent.setGameState("PLAY");
        }
    }

    private boolean verifyAllSunk(Map<String, Object> last_turn) {
        Map<String, Object> damages = (Map<String, Object>) last_turn.get("damages");
        if ( ((int) damages.get("carrier") == 5) && ((int) damages.get("battleship") == 4) &&
                ((int) damages.get("submarine") == 3) && ((int) damages.get("destroyer") == 3) &&
                ((int) damages.get("patrolboat") == 2)) {
            return true;
        }
        return false;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }

    private Map<String, Object> leaderBoardDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        Map<String, Integer> dict = player.countScores();
        dto.put("email", player.getEmail());
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
        dto.put("player", playersDTO(salvo.getGamePlayer().getPlayer()));
        dto.put("locations", salvo.getLocations());

        return dto;
    }

    private Map<String, Object> game_viewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getGame().getId());
        dto.put("creationDate", gamePlayer.getGame().getCreationDate());
        dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(game_player ->
                gamePlayerDTO(game_player)).collect(Collectors.toList()));
        dto.put("ships", gamePlayer.getShips().stream().map(ship -> shipsDTO(ship)).collect(Collectors.toList()));
        dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(game_player ->
                game_player.getSalvoes().stream().map(salvo -> salvoDTO(salvo))).collect(Collectors.toList()));

        Map<String, Object> hits = new LinkedHashMap<>();
        hits.put("self", new ArrayList<>());
        hits.put("opponent", new ArrayList<>());

        if (gamePlayer.getGame().getGamePlayers().stream().filter(x -> x !=(gamePlayer)).
                findAny().isPresent()) {
            GamePlayer opponent = gamePlayer.getGame().getGamePlayers().stream().filter(x -> x !=(gamePlayer)).
                    findAny().get();

            List<Map<String, Object>> hit_self = hitsDTO(opponent, gamePlayer);
            List<Map<String, Object>> hit_opp = hitsDTO(gamePlayer, opponent);
            hits.put("self", hit_self);
            hits.put("opponent", hit_opp);
        }

        dto.put("hits", hits);

        return dto;
    }

    private List<Map<String, Object>> hitsDTO(GamePlayer self, GamePlayer opponent) {
        List<Salvo> my_salvoes = self.getSalvoes();

        List<Map<String, Object>> hit_selfs = new ArrayList<>();

        for (Salvo salvo : my_salvoes) {
            if (salvo.getTurn() < opponent.getTurn()) {
                //Obtengo los hits al barco del oponente si los hubiera
                List<String> hitLocations = salvo.getLocations().stream()
                        .flatMap(salvo_loc -> opponent.getShips().stream()
                                .flatMap(ship -> ship.getLocations().stream()
                                        .filter(ship_loc_opp -> {
                                            return ship_loc_opp.equals(salvo_loc);
                                        })
                                )).collect(Collectors.toList());
                Map<String, Object> aux_DTO = new LinkedHashMap<>();
                aux_DTO.put("hitLocations", hitLocations);

                //Calculo los daños a los barcos del oponente (es acumulativo)
                Map<String, Integer> damages = new LinkedHashMap<>();
                for (Ship ship : opponent.getShips()) {
                    damages.put(ship.getType() + "Hits", (int) ship.getLocations().stream().
                            filter(ship_loc -> hitLocations.contains(ship_loc)).count());
                }

                try {
                    Map<String, Object> last_hit = hit_selfs.get(hit_selfs.size() - 1);
                    Map<String, Integer> last_damages = (Map<String, Integer>) last_hit.get("damages");
                    damages.put("carrier", last_damages.get("carrier") + damages.get("carrierHits"));
                    damages.put("battleship", last_damages.get("battleship") + damages.get("battleshipHits"));
                    damages.put("submarine", last_damages.get("submarine") + damages.get("submarineHits"));
                    damages.put("destroyer", last_damages.get("destroyer") + damages.get("destroyerHits"));
                    damages.put("patrolboat", last_damages.get("patrolboat") + damages.get("patrolboatHits"));
                } catch (Exception e) {
                    //Aun no habia ningun last_hit
                    damages.put("carrier", damages.get("carrierHits"));
                    damages.put("battleship", damages.get("battleshipHits"));
                    damages.put("submarine", damages.get("submarineHits"));
                    damages.put("destroyer", damages.get("destroyerHits"));
                    damages.put("patrolboat", damages.get("patrolboatHits"));
                }

                aux_DTO.put("damages", damages);

                aux_DTO.put("turn", salvo.getTurn());
                aux_DTO.put("missed", salvo.getLocations().size() - hitLocations.size());
                hit_selfs.add(aux_DTO);
            }
        }
        return hit_selfs;
    }

    private Map<String,Object> gamesDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("creationDate", game.getCreationDate());
        dto.put("gamePlayers", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> gamePlayerDTO(gamePlayer)).collect(Collectors.toList()));
        dto.put("scores", game.getGamePlayers().stream().map(gamePlayer -> scoreDTO(gamePlayer.getScore())));

        return dto;
    }

    private Map<String, Object> playersDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("email", player.getEmail());

        return dto;
    }

    private Map<String, Object> gamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playersDTO(gamePlayer.getPlayer()));
        dto.put("joinDate", gamePlayer.getJoinDate());
        dto.put("gameState", gamePlayer.getGameState());
        dto.put("turn", gamePlayer.getTurn());

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