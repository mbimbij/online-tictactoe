var gameData;

const gameId = new URLSearchParams(window.location.search).get('gameId');
var playerId = Cookies.get('playerId');

const socket = new WebSocket("ws://localhost:8080/singleGame?gameId=" + gameId);
socket.binaryType = "arraybuffer";

const startGameElem = document.getElementById("startGame");
const ongoingGameState = document.getElementById("ongoingGameState");
const playerAssignmentElem = document.getElementById("playerAssignment");
const resultPopupElem = $('#resultPopup');

const currentPlayerElem = document.getElementById("currentPlayer");

const endGameStates = ["X_WINS", "O_WINS", "DRAW"]
socket.onmessage = function (event) {
    var data = JSON.parse(event.data);

    var opCode = data['opCode'];
    if (opCode === 'gameUpdated') {
        gameData = data.game;
        updateGameDisplayFromGameData();
    }

};
$.post("http://localhost:8080/gameData/" + gameId, function (data) {
    gameData = data;
    updateGameDisplayFromGameData();
}).fail(function () {
    alert("error getting game information data");

});

resultPopupElem.on('hidden.bs.modal', function (e) {
    ackEndGameThenGamesList();
});

function startGame(gameId) {
    $.post("http://localhost:8080/startGame/" + gameId)
        .fail(function () {
            alert("error starting game" + gameId);
        });
}

function redrawGrid() {
    if (gameData.grid !== null) {
        var mark;
        for (let i = 0; i < 9; i++) {
            mark = gameData.grid[i];
            mark = mark === "NONE" ? "" : mark;
            document.getElementsByClassName("box")[i].innerHTML = mark;
        }
    }
}

function isGameOver() {
    return endGameStates.indexOf(gameData.gameState) !== -1;
}

function displayGameOverAndRedirect() {
    if (gameData.gameState === "DRAW") {
        $('#result').text("draw, let's play another one !");
    } else if ((gameData.gameState === "X_WINS" && getAssignment(playerId) === "X") || (gameData.gameState === "O_WINS" && getAssignment(playerId) === "O")) {
        $('#result').text("congratulations, you won ! Let's keep that streak going.");
    } else {
        $('#result').text("sorry, you lost ! Maybe next time ...");
    }
    resultPopupElem.modal('show')
}

function updateGameDisplayFromGameData() {
    document.getElementById("gameTitle").innerHTML = gameData.title;
    document.getElementById("gameState").innerHTML = gameData.gameState;
    document.getElementById("player1").innerHTML = gameData.creator;
    document.getElementById("player2").innerHTML = gameData.otherPlayer;

    if (gameData.gameState === "READY" && playerId === gameData.creator) {
        startGameElem.innerHTML = '<button onclick="startGame(\'' + gameData.id + '\')" type="button" class="btn btn-primary btn-sm">Start Game</button>'
    }

    if (gameData.gameState === "STARTED") {
        var assignmentSelf = getAssignment(playerId)
        var assignmentCurrentPlayer = getAssignment(gameData.currentPlayer)
        ongoingGameState.style.display = "inherit";
        playerAssignmentElem.innerHTML = `<b>${assignmentSelf}</b>`;
        currentPlayerElem.innerHTML = `<b>${assignmentCurrentPlayer}</b>`;
    }

    redrawGrid();

    if (isGameOver()) {
        displayGameOverAndRedirect();
    }
}

function getAssignment(playerId) {
    var assignment;
    if (gameData.playerX === playerId) {
        assignment = "X";
    } else if (gameData.playerO === playerId) {
        assignment = "O";
    } else {
        assignment = "unknown";
    }
    return assignment
}

function play(x, y) {
    if (gameData.currentPlayer === playerId) {
        $.post("http://localhost:8080/play/" + gameId + "/" + playerId + "?x=" + x + "&y=" + y)
            .fail(function () {
                alert("error playing game" + gameId);
            });
    } else {
        console.log("not your turn")
    }
}

function ackEndGameThenGamesList() {
    $.post("http://localhost:8080/ackEndGame/" + gameId + "/" + playerId, function () {
        window.location.href = '/';
    })
        .fail(function () {
            alert("error ack end game for game" + gameId);
        });
}

function ackEndGameThenMainMenu() {
    $.post("http://localhost:8080/ackEndGame/" + gameId + "/" + playerId, function () {
        window.location.href = '/mainMenu/mainMenu.html';
    })
        .fail(function () {
            alert("error ack end game for game" + gameId);
        });
}