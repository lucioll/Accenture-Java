

$(function () {
  loadData();
});

function getParameterByName(name) {
  var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
  return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
}

function loadData() {
  $.get('/api/game_view/' + getParameterByName('gp'))
    .done(function (data) {
      var playerInfo;
      if (data.gamePlayers[0].id == getParameterByName('gp'))
        playerInfo = [data.gamePlayers[0].player, data.gamePlayers[1].player];
      else
        playerInfo = [data.gamePlayers[1].player, data.gamePlayers[0].player];

      $('#playerInfo').text(playerInfo[0].userName + '(you) vs ' + playerInfo[1].userName);

      data.ships.forEach(function (shipPiece) {
        shipPiece.locations.forEach(function (shipLocation) {
            var tuple = isHit(shipLocation,data.salvoes,playerInfo[0].id);
          if(tuple[0]) {
              console.log("HIIIIIIIT");
              $('#B_' + shipLocation).addClass('ship-piece-hited').text(tuple[1]);
          }
          else
            $('#B_' + shipLocation).addClass('ship-piece');
        });
      });
      data.salvoes.forEach(function (salvo) {
        if (playerInfo[0].id === salvo.player.id) {
          salvo.locations.forEach(function (location) {
            $('#S_' + location).addClass('salvo');
          });
        } else {
          salvo.locations.forEach(function (location) {
            $('#_' + location).addClass('salvo');
          });
        }
      });
    })
    .fail(function (jqXHR, textStatus) {
      alert('Failed: ' + textStatus);
    });
}

function isHit(shipLocation,salvoes,playerId) {
    var is_hit = false;
    var turn = -1;
    salvoes.forEach(function (salvo) {
    if(salvo.player.id  != playerId)
      salvo.locations.forEach(function (location) {
        if(shipLocation === location)
            is_hit = true;
            turn = salvo.turn;
      });
  });
    return [is_hit, turn];
}


