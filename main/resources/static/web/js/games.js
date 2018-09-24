var data;
var gamesData;
var leaderBoardData;
var submitButton;

updateJson();

$(function() {
    $('.submitbutton').click(function () {
        submitButton = $(this).attr('name')
    });

});

$('#login-form').on('submit', function (event) {
    event.preventDefault();

    if (submitButton == "login") {
        $.post("/api/login",
            { name: $("#username").val(),
                pwd: $("#password").val() })
            .done(function() {
                console.log("login ok");
                $('#loginSuccess').show( "slow" ).delay(2000).hide( "slow" );
                // $("#username").val("");
                $("#password").val("");
                updateJson();

            })
            .fail(function() {
                console.log("login failed");
                $('#loginFailed').show( "slow" ).delay(2000).hide( "slow" );
                $("#username").val("");
                $("#password").val("");
                $("#username").focus();
                // $('#loginFailed').hide( "slow" );
            })
            .always(function() {

            });

    } else if (submitButton == "signup") {
        $.post("/api/players",
            { name: $("#username").val(),
                pwd: $("#password").val() })
            .done(function(data) {
                console.log("signup ok");
                console.log(data);
                $('#signupSuccess').show( "slow" ).delay(2000).hide( "slow" );
                $.post("/api/login",
                    { name: $("#username").val(),
                        pwd: $("#password").val() })
                    .done(function() {
                        console.log("login ok");
                        $('#loginSuccess').show( "slow" ).delay(2500).hide( "slow" );
                        $("#username").val("");
                        $("#password").val("");
                        updateJson();

                    })
                    .fail(function() {
                        console.log("login failed");
                        $('#loginFailed').show( "slow" ).delay(2000).hide( "slow" );
                        $("#username").val("");
                        $("#password").val("");
                        $("#username").focus();
                        // $('#loginFailed').hide( "slow" );
                    })
                    .always(function() {

                    });
            })
            .fail(function(data) {
                console.log("signup failed");
                // console.log(data);
                $("#username").val("");
                $("#password").val("");
                $("#username").focus();
                $('#errorSignup').text(data.responseJSON.error);
                $('#errorSignup').show( "slow" ).delay(3000).hide( "slow" );
            })
            .always(function() {

            });


    } else {
        //no button pressed
    }
});

$('#logout-form').on('submit', function (event) {
        event.preventDefault();
        $.post("/api/logout")
            .done(function () {
                console.log("logout ok");
                $('#logoutSuccess').show("slow").delay(2000).hide("slow");
                updateJson();
            })
            .fail(function () {
                console.log("logout fails");
            })
            .always(function () {

            });
    });

$('#createGame').on('submit', function (event) {
    event.preventDefault();
    $.post("/api/games")
        .done(function (data) {
            console.log(data);
            console.log("game created");
            gameViewUrl = "/web/game.html?gp=" + data.gpid;
            $('#gameCreatedSuccess').show("slow").delay(2000).hide("slow");
            setTimeout(
                function()
                {
                    location.href = gameViewUrl;
                }, 3000);
        })
        .fail(function (data) {
            console.log("game creation failed");
            $('#errorSignup').text(data.responseJSON.error);
            $('#errorSignup').show( "slow" ).delay(4000).hide( "slow" );

        })
        .always(function () {

        });
});


function fetchJson(url) {
        return fetch(url, {
            method: 'GET',
            credentials: 'include'
        }).then(function (response) {
            if (response.ok) {
                return response.json();
            }
            throw new Error(response.statusText);
        });
}

function updateJson() {
        fetchJson('/api/games').then(function (json) {
            // do something with the JSON;
            data = json;
            gamesData = data.games;
            //gamesData = json;
            updateView();

        }).catch(function (error) {
            // do something getting JSON fails
        });

        fetchJson('/api/leaderBoard').then(function (json2) {
            // do something with the JSON
            leaderBoardData = json2;
            updateView();

        }).catch(function (error) {
            // do something getting JSON fails
        });
}

function updateView() {
    //addScoresToPlayersArray(getPlayers(gamesData));
    showScoreBoard(leaderBoardData);
    showGamesTable(gamesData);
        if (data.player == "Guest") {
            $('#currentPlayer').text(data.player);
            $('#logout-form').hide("slow");
            $('#login-form').show("slow");

        } else {
            $('#currentPlayer').text(data.player.userName);
            $('#login-form').hide("slow");
            $('#logout-form').show("slow");

        }
}

function showGamesTable(gamesData) {
        // let mytable = $('<table></table>').attr({id: "gamesTable", class: ""});
        let table = "#gamesList tbody";
        let gpid;
        $(table).empty();
        for (let i = 0; i < gamesData.length; i++) {

            let isLoggedPlayer = false;
            let joinButtonHtml = null;

            let DateCreated = new Date(gamesData[i].creationDate);
            DateCreated = DateCreated.getMonth() + 1 + "/" + DateCreated.getDate() + " " + DateCreated.getHours() + ":" + DateCreated.getMinutes();
            let row = $('<tr></tr>').prependTo(table);
            $('<td class="textCenter">' + gamesData[i].id + '</td>').appendTo(row);
            $('<td>' + DateCreated + '</td>').appendTo(row);


            for (let j = 0; j < gamesData[i].gamePlayers.length; j++) {


                if (gamesData[i].gamePlayers.length == 2) {
                    $('<td>' + gamesData[i].gamePlayers[j].player.email + '</td>').appendTo(row);
                }
                if (gamesData[i].gamePlayers.length == 1 && (data.player == "Guest" || data.player.id == gamesData[i].gamePlayers[j].player.id)) {
                    $('<td>' + gamesData[i].gamePlayers[0].player.email + '</td><td class="yellow500">WAITING FOR PLAYER</td>').appendTo(row);
                }
                if (gamesData[i].gamePlayers.length == 1 && data.player.id != null && data.player.id != gamesData[i].gamePlayers[j].player.id) {
                    $('<td>' + gamesData[i].gamePlayers[0].player.email + '</td><td class="yellow500">WAITING FOR PLAYER</td>').appendTo(row);
                    joinButtonHtml = '<td class="textCenter"><button class="joinGameButton btn btn-info" data-gameid=' + '"' + gamesData[i].id + '"' + '>JOIN GAME</button></td>';

                }
                if (gamesData[i].gamePlayers[j].player.id == data.player.id) {
                    gpid = gamesData[i].gamePlayers[j].id;
                    isLoggedPlayer = true;
                }
            }

            if (isLoggedPlayer === true) {
                let gameUrl = "/web/game.html?gp=" + gpid;
                $('<td class="textCenter"><a href=' + '"' + gameUrl + '"' + 'class="btn btn-warning" role="button">ENTER GAME</a></td>').appendTo(row);
            } else if (joinButtonHtml !== null){
                $(joinButtonHtml).appendTo(row);
            } else {
                $('<td class="textCenter">-</td>').appendTo(row);
        }



        }
    $('.joinGameButton').click(function (e) {
        e.preventDefault();

        let joinGameUrl = "/api/game/" + $(this).data('gameid') + "/players";
        $.post(joinGameUrl)
            .done(function (data) {
                console.log(data);
                console.log("game joined");
                gameViewUrl = "/web/game.html?gp=" + data.gpid;
                $('#gameJoinedSuccess').show("slow").delay(2000).hide("slow");
                setTimeout(
                   function()
                  {
                       location.href = gameViewUrl;
                   }, 3000);
            })
            .fail(function (data) {
                console.log("game join failed");
                $('#errorSignup').text(data.responseJSON.error);
                $('#errorSignup').show("slow").delay(4000).hide("slow");

            })
            .always(function () {

            });
    });
}

function getPlayers(gamesData) {

        playersArray = [];
        let playersIds = [];

        for (let i = 0; i < gamesData.length; i++) {

            for (let j = 0; j < gamesData[i].gamePlayers.length; j++) {

                if (!playersIds.includes(gamesData[i].gamePlayers[j].player.id)) {
                    playersIds.push(gamesData[i].gamePlayers[j].player.id);
                    let playerScoreData = {
                        "id": gamesData[i].gamePlayers[j].player.id,
                        "email": gamesData[i].gamePlayers[j].player.email,
                        "scores": [],
                        "total": 0.0
                    };
                    playersArray.push(playerScoreData);
                }
            }
        }
        return playersArray;
}

function addScoresToPlayersArray(playersArray) {

        for (let i = 0; i < gamesData.length; i++) {

            for (let j = 0; j < gamesData[i].scores.length; j++) {

                let scorePlayerId = gamesData[i].scores[j].playerID;

                for (let k = 0; k < playersArray.length; k++) {

                    if (playersArray[k].id == scorePlayerId) {
                        playersArray[k].scores.push(gamesData[i].scores[j].score);
                        playersArray[k].total += gamesData[i].scores[j].score;
                    }
                }
            }
        }
}

function showScoreBoard(leaderBoardData) {

    leaderBoardData.sort(function (a, b) {
            return b.total - a.total;
    });

    var table = "#scoreBoard tbody";
    $(table).empty();

    for (var m = 0; m < leaderBoardData.length; m++) {
            var row = $('<tr></tr>').appendTo(table);
            $('<td>' + leaderBoardData[m].email + '</td>').appendTo(row);
            $("<td class='textCenter'>" + leaderBoardData[m].total.toFixed(1) + '</td>').appendTo(row);
            $("<td class='textCenter'>" + leaderBoardData[m].won + '</td>').appendTo(row);
            $("<td class='textCenter'>" + leaderBoardData[m].lost + '</td>').appendTo(row);
            $("<td class='textCenter'>" + leaderBoardData[m].tied + '</td>').appendTo(row);
        }
    }








