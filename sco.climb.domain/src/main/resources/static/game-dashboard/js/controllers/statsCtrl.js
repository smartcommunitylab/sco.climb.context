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
    mapService.getStatus().then(function (data) {
      console.log("Legs:",data.legs);
      $scope.status = data;
      $scope.legs = data.legs;
      $scope.globalTeam = data.game.globalTeam;
      // get actual situation
      for (var i = 0; i < data.teams.length; i++) {
        if (data.teams[i].classRoom == $scope.globalTeam) {
          $scope.globalScore = data.teams[i].score;
          if (data.teams[i].currentLeg) {
            $scope.currentLeg = data.teams[i].currentLeg;
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
