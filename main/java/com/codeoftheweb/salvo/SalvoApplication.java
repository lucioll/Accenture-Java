package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

    @Bean
    public CommandLineRunner initData(PlayerRepository repositoryPlayers, GameRepository repositoryGames,
                                      GamePlayerRepository repositoryGamePlayers, ShipRepository repositoryShips,
                                      SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {
            // save a couple of players
            Player player1 = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
            Player player2 = new Player("c.obrian@ctu.gov",  passwordEncoder().encode("42"));
            Player player3 = new Player("kim_bauer@gmail.com", passwordEncoder().encode( "kb"));
            Player player4 = new Player("t.almeida@ctu.gov",  passwordEncoder().encode("mole"));
            repositoryPlayers.save(player1);
            repositoryPlayers.save(player2);
            repositoryPlayers.save(player3);
            repositoryPlayers.save(player4);

            // save a couple of games
            Game game1 = new Game(LocalDateTime.now());
            Game game2 = new Game(LocalDateTime.now().plusHours(1));
            Game game3 = new Game(LocalDateTime.now().plusHours(2));
            Game game4 = new Game(LocalDateTime.now().plusHours(3));
            Game game5 = new Game(LocalDateTime.now().plusHours(4));
            Game game6 = new Game(LocalDateTime.now().plusHours(5));
            Game game7 = new Game(LocalDateTime.now().plusHours(6));
            Game game8 = new Game(LocalDateTime.now().plusHours(7));
            repositoryGames.save(game1);
            repositoryGames.save(game2);
            repositoryGames.save(game3);
            repositoryGames.save(game4);
            repositoryGames.save(game5);
            repositoryGames.save(game6);
            repositoryGames.save(game7);
            repositoryGames.save(game8);


            GamePlayer gamePlayer1 = new GamePlayer(repositoryPlayers.findByEmail("j.bauer@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game1.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer1);
            GamePlayer gamePlayer2 = new GamePlayer(repositoryPlayers.findByEmail("c.obrian@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game1.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer2);

            GamePlayer gamePlayer3 = new GamePlayer(repositoryPlayers.findByEmail("j.bauer@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game2.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer3);
            GamePlayer gamePlayer4 = new GamePlayer(repositoryPlayers.findByEmail("c.obrian@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game2.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer4);

            GamePlayer gamePlayer5 = new GamePlayer(repositoryPlayers.findByEmail("c.obrian@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game3.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer5);
            GamePlayer gamePlayer6 = new GamePlayer(repositoryPlayers.findByEmail("t.almeida@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game3.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer6);

            GamePlayer gamePlayer7 = new GamePlayer(repositoryPlayers.findByEmail("c.obrian@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game4.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer7);
            GamePlayer gamePlayer8 = new GamePlayer(repositoryPlayers.findByEmail("j.bauer@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game4.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer8);

            GamePlayer gamePlayer9 = new GamePlayer(repositoryPlayers.findByEmail("t.almeida@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game5.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer9);
            GamePlayer gamePlayer10 = new GamePlayer(repositoryPlayers.findByEmail("j.bauer@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game5.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer10);

            GamePlayer gamePlayer11 = new GamePlayer(repositoryPlayers.findByEmail("kim_bauer@gmail.com").get(),
                    repositoryGames.findByCreationDate(game6.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer11);

            GamePlayer gamePlayer12 = new GamePlayer(repositoryPlayers.findByEmail("t.almeida@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game7.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer12);

            GamePlayer gamePlayer13 = new GamePlayer(repositoryPlayers.findByEmail("kim_bauer@gmail.com").get(),
                    repositoryGames.findByCreationDate(game8.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer13);
            GamePlayer gamePlayer14 = new GamePlayer(repositoryPlayers.findByEmail("t.almeida@ctu.gov").get(),
                    repositoryGames.findByCreationDate(game8.getCreationDate()));
            repositoryGamePlayers.save(gamePlayer14);

            //------------------------GAME 1---------------------------
            List<String> locations_ship1_jack_game1 = new ArrayList<String>(Arrays.asList("H2","H3","H4"));
            Ship ship1 = new Ship(gamePlayer1, "Destroyer", locations_ship1_jack_game1);
            repositoryShips.save(ship1);
            List<String> locations_ship2_jack_game1 = new ArrayList<String>(Arrays.asList("E1","F1","G1"));
            Ship ship2 = new Ship(gamePlayer1, "Submarine", locations_ship2_jack_game1);
            repositoryShips.save(ship2);
            List<String> locations_ship3_jack_game1 = new ArrayList<String>(Arrays.asList("B4","B5"));
            Ship ship3 = new Ship(gamePlayer1, "Patrol Boat", locations_ship3_jack_game1);
            repositoryShips.save(ship3);

            List<String> locations_ship1_obrian_game1 = new ArrayList<String>(Arrays.asList("B5","C5","D5"));
            Ship ship4 = new Ship(gamePlayer2, "Destroyer", locations_ship1_obrian_game1);
            repositoryShips.save(ship4);
            List<String> locations_ship2_obrian_game1 = new ArrayList<String>(Arrays.asList("F1","F2"));
            Ship ship5 = new Ship(gamePlayer2, "Patrol Boat", locations_ship2_obrian_game1);
            repositoryShips.save(ship5);

            // Save a couple of salvoes
            Salvo salvo1Jack = new Salvo(gamePlayer1, "B5",1);
            salvo1Jack.addSalvoLocation("C5");
            salvo1Jack.addSalvoLocation("F1");
            salvoRepository.save(salvo1Jack);
            Salvo salvo2Jack = new Salvo(gamePlayer1,"F2", 2);
            salvo2Jack.addSalvoLocation("D5");
            salvoRepository.save(salvo2Jack);


            Salvo salvo1Obrian = new Salvo(gamePlayer2,"B4",1);
            salvo1Obrian.addSalvoLocation("B5");
            salvo1Obrian.addSalvoLocation("B6");
            salvoRepository.save(salvo1Obrian);
            Salvo salvo2Obrian = new Salvo(gamePlayer2,"E1",2);
            salvo2Obrian.addSalvoLocation("H3");
            salvo2Obrian.addSalvoLocation("A2");
            salvoRepository.save(salvo2Obrian);


            //Save SCORE
            Score score_winner_game1 = new Score(game1, player1, game1.getCreationDate().plusMinutes(30),1);
            Score score_loser_game1 = new Score(game1, player2, game1.getCreationDate().plusMinutes(30),0);
            scoreRepository.save(score_winner_game1);
            scoreRepository.save(score_loser_game1);


            //-------------GAME 2------------
            List<String> locations_ship1_jack_game2 = new ArrayList<String>(Arrays.asList("B5","C5","D5"));
            Ship ship6 = new Ship(gamePlayer3, "Destroyer", locations_ship1_jack_game2);
            repositoryShips.save(ship6);
            List<String> locations_ship2_jack_game2 = new ArrayList<String>(Arrays.asList("C6","C7"));
            Ship ship7 = new Ship(gamePlayer3, "Patrol Boat", locations_ship2_jack_game2);
            repositoryShips.save(ship7);

            List<String> locations_ship1_obrian_game2 = new ArrayList<String>(Arrays.asList("A2","A3","A4"));
            Ship ship8 = new Ship(gamePlayer4, "Submarine", locations_ship1_obrian_game2);
            repositoryShips.save(ship8);
            List<String> locations_ship2_obrian_game2 = new ArrayList<String>(Arrays.asList("G6","H6"));
            Ship ship9 = new Ship(gamePlayer4, "Patrol Boat", locations_ship2_obrian_game2);
            repositoryShips.save(ship9);

            Salvo salvo1Jack_game2 = new Salvo(gamePlayer3, "A2",1);
            salvo1Jack_game2.addSalvoLocation("A4");
            salvo1Jack_game2.addSalvoLocation("G6");
            salvoRepository.save(salvo1Jack_game2);
            Salvo salvo2Jack_game2 = new Salvo(gamePlayer3,"A3", 2);
            salvo2Jack_game2.addSalvoLocation("H6");
            salvoRepository.save(salvo2Jack_game2);


            Salvo salvo1Obrian_game2 = new Salvo(gamePlayer4,"E1",1);
            salvo1Obrian_game2.addSalvoLocation("H3");
            salvo1Obrian_game2.addSalvoLocation("A2");
            salvoRepository.save(salvo1Obrian_game2);
            Salvo salvo2Obrian_game2 = new Salvo(gamePlayer4,"C5",2);
            salvo2Obrian_game2.addSalvoLocation("C6");
            salvoRepository.save(salvo2Obrian_game2);

            //Save SCORE
            Score score_tie1_game2 = new Score(game2, player1, game2.getCreationDate().plusMinutes(30),0.5f);
            Score score_tie2_game2 = new Score(game2, player2, game2.getCreationDate().plusMinutes(30),0.5f);
            scoreRepository.save(score_tie1_game2);
            scoreRepository.save(score_tie2_game2);

            //---------------------Game 3-----------------
            List<String> locations_ship1_obrian_game3 = new ArrayList<String>(Arrays.asList("B5","C5","D5"));
            Ship ship10 = new Ship(gamePlayer5, "Destroyer", locations_ship1_obrian_game3);
            repositoryShips.save(ship10);
            List<String> locations_ship3_obrian_game3 = new ArrayList<String>(Arrays.asList("C6","C7"));
            Ship ship11 = new Ship(gamePlayer5, "Patrol Boat", locations_ship3_obrian_game3);
            repositoryShips.save(ship11);

            List<String> locations_ship1_almeida_game3 = new ArrayList<String>(Arrays.asList("A2","A3","A4"));
            Ship ship12 = new Ship(gamePlayer6, "Submarine", locations_ship1_almeida_game3);
            repositoryShips.save(ship12);
            List<String> locations_ship2_almeida_game3 = new ArrayList<String>(Arrays.asList("G6","H6"));
            Ship ship13 = new Ship(gamePlayer6, "Patrol Boat", locations_ship2_almeida_game3);
            repositoryShips.save(ship13);

            Salvo salvo1Obrian_game3 = new Salvo(gamePlayer5, "G6",1);
            salvo1Obrian_game3.addSalvoLocation("H6");
            salvo1Obrian_game3.addSalvoLocation("A4");
            salvoRepository.save(salvo1Obrian_game3);
            Salvo salvo2Obrian_game3= new Salvo(gamePlayer5,"A2", 2);
            salvo2Obrian_game3.addSalvoLocation("A3");
            salvo2Obrian_game3.addSalvoLocation("D8");
            salvoRepository.save(salvo2Obrian_game3);

            Salvo salvo1Almeida_game3 = new Salvo(gamePlayer6,"H1",1);
            salvo1Almeida_game3.addSalvoLocation("H2");
            salvo1Almeida_game3.addSalvoLocation("H3");
            salvoRepository.save(salvo1Almeida_game3);
            Salvo salvo2Almeida_game3 = new Salvo(gamePlayer6,"E1",2);
            salvo2Almeida_game3.addSalvoLocation("F2");
            salvo2Almeida_game3.addSalvoLocation("G3");
            salvoRepository.save(salvo2Almeida_game3);


            //Save SCORE
            Score score_winner_game3 = new Score(game3, player1, game3.getCreationDate().plusMinutes(30),1);
            Score score_loser_game3 = new Score(game3, player2, game3.getCreationDate().plusMinutes(30),0);
            scoreRepository.save(score_winner_game3);
            scoreRepository.save(score_loser_game3);

            //-----------------Game4---------------
            List<String> locations_ship1_obrian_game4 = new ArrayList<String>(Arrays.asList("B5","C5","D5"));
            Ship ship14 = new Ship(gamePlayer7, "Destroyer", locations_ship1_obrian_game4);
            repositoryShips.save(ship14);
            List<String> locations_ship2_obrian_game4 = new ArrayList<String>(Arrays.asList("C6","C7"));
            Ship ship15 = new Ship(gamePlayer7, "Patrol Boat", locations_ship2_obrian_game4);
            repositoryShips.save(ship15);

            List<String> locations_ship1_jack_game4 = new ArrayList<String>(Arrays.asList("A2","A3","A4"));
            Ship ship16 = new Ship(gamePlayer8, "Submarine", locations_ship1_jack_game4);
            repositoryShips.save(ship16);
            List<String> locations_ship2_jack_game4 = new ArrayList<String>(Arrays.asList("G6","H6"));
            Ship ship17 = new Ship(gamePlayer8, "Patrol Boat", locations_ship2_jack_game4);
            repositoryShips.save(ship17);

            Salvo salvo1Obrian_game4 = new Salvo(gamePlayer7, "A3",1);
            salvo1Obrian_game4.addSalvoLocation("A4");
            salvo1Obrian_game4.addSalvoLocation("F7");
            salvoRepository.save(salvo1Obrian_game4);
            Salvo salvo2Obrian_game4= new Salvo(gamePlayer7,"A2", 2);
            salvo2Obrian_game4.addSalvoLocation("G6");
            salvo2Obrian_game4.addSalvoLocation("H6");
            salvoRepository.save(salvo2Obrian_game4);

            Salvo salvo1Jack_game4 = new Salvo(gamePlayer8,"B5",1);
            salvo1Jack_game4.addSalvoLocation("C6");
            salvo1Jack_game4.addSalvoLocation("H1");
            salvoRepository.save(salvo1Jack_game4);
            Salvo salvo2Jack_game4 = new Salvo(gamePlayer8,"C5",2);
            salvo2Jack_game4.addSalvoLocation("C7");
            salvo2Jack_game4.addSalvoLocation("D5");
            salvoRepository.save(salvo2Jack_game4);

            Score score_tie1_game4 = new Score(game4, player1, game4.getCreationDate().plusMinutes(30),0.5f);
            Score score_tie2_game4 = new Score(game4, player2, game4.getCreationDate().plusMinutes(30),0.5f);
            scoreRepository.save(score_tie1_game4);
            scoreRepository.save(score_tie2_game4);

            //--------------------------GAME 5--------------------------
            List<String> locations_ship1_almeida_game5 = new ArrayList<String>(Arrays.asList("B5","C5","D5"));
            Ship ship18 = new Ship(gamePlayer9, "Destroyer", locations_ship1_almeida_game5);
            repositoryShips.save(ship18);
            List<String> locations_ship2_almeida_game5 = new ArrayList<String>(Arrays.asList("C6","C7"));
            Ship ship19 = new Ship(gamePlayer9, "Patrol Boat", locations_ship2_almeida_game5);
            repositoryShips.save(ship19);

            List<String> locations_ship1_jack_game5 = new ArrayList<String>(Arrays.asList("A2","A3","A4"));
            Ship ship20 = new Ship(gamePlayer10, "Submarine", locations_ship1_jack_game5);
            repositoryShips.save(ship20);
            List<String> locations_ship2_jack_game5 = new ArrayList<String>(Arrays.asList("G6","H6"));
            Ship ship21 = new Ship(gamePlayer10, "Patrol Boat", locations_ship2_jack_game5);
            repositoryShips.save(ship21);

            Salvo salvo1Almeida_game5 = new Salvo(gamePlayer9, "A1",1);
            salvo1Almeida_game5.addSalvoLocation("A2");
            salvo1Almeida_game5.addSalvoLocation("A3");
            salvoRepository.save(salvo1Almeida_game5);
            Salvo salvo2Almeida_game5= new Salvo(gamePlayer9,"G6", 2);
            salvo2Almeida_game5.addSalvoLocation("G7");
            salvo2Almeida_game5.addSalvoLocation("G8");
            salvoRepository.save(salvo2Almeida_game5);

            Salvo salvo1Jack_game5 = new Salvo(gamePlayer10,"B5",1);
            salvo1Jack_game5.addSalvoLocation("B6");
            salvo1Jack_game5.addSalvoLocation("C7");
            salvoRepository.save(salvo1Jack_game5);
            Salvo salvo2Jack_game5 = new Salvo(gamePlayer8,"C6",2);
            salvo2Jack_game5.addSalvoLocation("D6");
            salvo2Jack_game5.addSalvoLocation("E6");
            salvoRepository.save(salvo2Jack_game5);
            Salvo salvo3Jack_game5 = new Salvo(gamePlayer8,"H1",3);
            salvo3Jack_game5.addSalvoLocation("H8");
            salvoRepository.save(salvo3Jack_game5);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inputEmail-> {
            Optional<Player> player = playerRepository.findByEmail(inputEmail);
            if (player.isPresent()) {
                return new User(player.get().getEmail(), player.get().getPassword(),
                        AuthorityUtils.createAuthorityList("USER"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputEmail);
            }
        });
    }


}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //Who can see what*************
        http.authorizeRequests().antMatchers("/web/games.html").permitAll()
                .antMatchers("/web/styles/games.css").permitAll()
                .antMatchers("/api/games").permitAll()
                .antMatchers("/api/game_view/*").hasAnyAuthority("USER")
                .anyRequest().permitAll();

        http.formLogin()
                .usernameParameter("name")
                .passwordParameter("pwd")
                .loginPage("/api/login");

        http.logout().logoutUrl("/api/logout");

        // turn off checking for CSRF tokens
        http.csrf().disable();

        // if user is not authenticated, just send an authentication failure response
        http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if login is successful, just clear the flags asking for authentication
        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

        // if login fails, just send an authentication failure response
        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

        // if logout is successful, just send a success response
        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}

