package com.example.tictactoe.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GameFactory {

    private GameRepository gameRepository;

    public Game createNewGame(Player player) {
        Game game = new Game(player);
        gameRepository.save(game);
        return game;
    }
}
