/* global angular */
angular.module('climbGame.controllers.stats', [])
  .controller('statsCtrl', function ($scope, $filter, $window, dataService, mapService) {
    var KMS_PER_FOOT = 10

    $scope.stats = {
      'gameScore': 0,
      'maxGameScore': 0
    }

    var data2stats = function (data) {
      return {
        'gameScore': Math.round(data.gameScore / 1000, 0),
        'maxGameScore': Math.round(data.maxGameScore / 1000, 0),
        'scoreModeMap': {
          'zeroImpact_wAdult': Math.floor(data['scoreModeMap']['zeroImpact_wAdult'] / (1000 * KMS_PER_FOOT)),
          'bus': Math.floor(data['scoreModeMap']['bus'] / (1000 * KMS_PER_FOOT)),
          'pandr': Math.floor(data['scoreModeMap']['pandr'] / (1000 * KMS_PER_FOOT)),
          'bonus': Math.floor(data['scoreModeMap']['bonus'] / (1000 * KMS_PER_FOOT)),
          'zeroImpact_solo': Math.floor(data['scoreModeMap']['zeroImpact_solo'] / (1000 * KMS_PER_FOOT))
        }
      }
    }

    dataService.getStats().then(
      function (stats) {
        console.log("getStats:",stats);
        $scope.stats = data2stats(stats)
        console.log("$scope.stats:",$scope.stats);
      },
      function (reason) {
        console.log(reason)
      }
    )
    var rad = function(x) {
      return x * Math.PI / 180;
    };
    //find the distance between two position
    var getDistance = function(p1, p2) {
      var R = 6378137; // Earth’s mean radius in meter
      var dLat = rad(p2.lat - p1.lat);
      var dLong = rad(p2.lng - p1.lng);
      var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(rad(p1.lat)) * Math.cos(rad(p2.lat)) *
        Math.sin(dLong / 2) * Math.sin(dLong / 2);
      var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      var d = R * c;
      return d; // returns the distance in meter
    };
    mapService.getStatus().then(function (data) {
      console.log("Data:",data);
      console.log("teams:",data.teams);
      $scope.status = data;
      $scope.legs = data.legs;
      $scope.globalTeam = data.game.globalTeam;
      // get actual situation
      for (var i = 0; i < data.teams.length; i++) {
        if (data.teams[i].classRoom == $scope.globalTeam) {
          $scope.globalScore = data.teams[i].score;
          if (data.teams[i].currentLeg) {
            $scope.currentLeg = data.teams[i].currentLeg;
            //find the next leg
            var currentIndex=data.legs.findIndex(item=>item.objectId==data.teams[i].currentLeg.objectId);
            $scope.nextLag=data.legs[currentIndex+1];
            //find the distance of current to next leg
            var currentPosition={lat:data.teams[i].currentLeg.geocoding[1],lng:data.teams[i].currentLeg.geocoding[0]};
            var nextPosition={lat:data.legs[currentIndex+1].geocoding[1],lng:data.legs[currentIndex+1].geocoding[0]};
            $scope.distanceNextPosition=Math.round(getDistance(currentPosition,nextPosition)/1000)
          } else {
            $scope.currentLeg = data.legs[data.legs.length - 1];
            $scope.endReached = true;
          }
          $scope.lastReachedLeg = data.teams[i].previousLeg;
          $scope.globalStatus = data.teams[i];
          break;
        }
      }
      console.log("currentLeg:",$scope.currentLeg);
    });
    $scope.scroll = function (id, direction) {
      if (direction === 'up') {
        $window.document.getElementById(id).scrollTop -= 50
      } else if (direction === 'down') {
        $window.document.getElementById(id).scrollTop += 50
      }
    }

    $scope.getGameScorePercentage = function () {
      if ($scope.stats) {
        return ($scope.stats.gameScore * 100) / $scope.stats.maxGameScore
      }
    }

    $scope.getCount = function (count) {
      return !count ? 0 : new Array(count)
    }

    $scope.MathFloor = function (n) {
      return Math.floor(n)
    }
  })
