package com.example.tictactoe.infra;

import com.example.tictactoe.entity.GameRepository;
import com.example.tictactoe.presentation.GamePresentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Component
public class SingleGameSocketHandler extends AbstractWebSocketHandler {

    static Map<String, List<WebSocketSession>> sessions = new HashMap<>();
    @Autowired
    GameRepository gameApplication;
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI requestUri = Objects.requireNonNull(session.getUri());
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(requestUri).build().getQueryParams();

        String gameId = Optional.ofNullable(params.getFirst("gameId")).orElseThrow(
                () -> new IllegalArgumentException("client should send the gameId when connecting to the server"));

        sessions.putIfAbsent(gameId, new ArrayList<>());
        sessions.get(gameId).add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.forEach((s, webSocketSessions) -> webSocketSessions.remove(session));
    }

    public void broadcast(String gameId, Map<String, Object> data) {
        List<WebSocketSession> webSocketSessions = Optional.ofNullable(sessions.get(gameId))
                .orElseThrow(() -> new IllegalArgumentException(String.format("unknown gameId %s", gameId)));
        webSocketSessions.forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(data)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}
