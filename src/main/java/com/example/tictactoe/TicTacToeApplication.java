package com.example.tictactoe;

import com.example.tictactoe.entity.GameFactory;
import com.example.tictactoe.entity.GameRepository;
import com.example.tictactoe.infra.SingleGameSocketHandler;
import com.example.tictactoe.usecase.CreateGameUsecase;
import com.example.tictactoe.infra.InMemoryGameRepository;
import com.example.tictactoe.infra.ListGamesSocketHandler;
import com.example.tictactoe.usecase.JoinGameUsecase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TicTacToeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicTacToeApplication.class, args);
    }

    @Bean
    public GameFactory gameFactory(GameRepository gameRepository) {
        return new GameFactory(gameRepository);
    }

    @Bean
    public GameRepository gameRepository() {
        return new InMemoryGameRepository();
    }

    @Bean
    public CreateGameUsecase createGameUsecase(GameFactory gameFactory, ListGamesSocketHandler socketHandler) {
        return new CreateGameUsecase(gameFactory, socketHandler);
    }

    @Bean
    public ListGamesSocketHandler listGamesSocketHandler(GameRepository gameRepository,
                                                         ObjectMapper objectMapper) {
        return new ListGamesSocketHandler(gameRepository, objectMapper);
    }

    @Bean
    public JoinGameUsecase joinGameUsecase(GameRepository gameRepository,
                                           ListGamesSocketHandler listGamesSocketHandler,
                                           SingleGameSocketHandler singleGameSocketHandler) {
        return new JoinGameUsecase(gameRepository,
                listGamesSocketHandler,
                singleGameSocketHandler);
    }


}
