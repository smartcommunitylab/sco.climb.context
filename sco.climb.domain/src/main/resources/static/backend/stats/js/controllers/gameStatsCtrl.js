/* global angular */
angular.module('climbGameUser.controllers.game.stat', [])
.controller('gameStatsCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$sce', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $sce, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    console.log("StatsCtrl");
    console.log("Game id to show stats: " + $stateParams.gameId);
    initParentNavigation();
    
    if ($stateParams.gameId) {
      dataService.getGame($stateParams.gameId).then(
        function (data) {
          $scope.game = data;
          $scope.game.gameDescription = $sce.trustAsHtml($scope.game.gameDescription);
          console.log("Game");
          console.log(data)
        }
      );
      dataService.getGameStats($stateParams.gameId).then(
        function (data) {
          $scope.gameStats = data;
          $scope.gameStats.gameScore = Math.floor($scope.gameStats.gameScore / 1000); 
          $scope.gameStats.maxGameScore = Math.floor($scope.gameStats.maxGameScore / 1000); 
          $scope.gameStats.progressPercentage = data.gameScore / data.maxGameScore * 100.0;
          if ($scope.gameStats.progressPercentage > 100) $scope.gameStats.progressPercentage = 100;
          
          $scope.travelTypePiechartData = [
            $scope.gameStats.scoreModeMap.bonus,
            $scope.gameStats.scoreModeMap.bus,
            $scope.gameStats.scoreModeMap.car,
            //$scope.gameStats.scoreModeMap.pandr,
            $scope.gameStats.scoreModeMap.zeroImpact_solo,
            $scope.gameStats.scoreModeMap.zeroImpact_wAdult
          ];
          $scope.travelTypePiechartLabel = [
            "Bonus",
            "Con scuolabus o autobus",
            "In auto fino alla piazzola di sosta",
            //"Pandr",
            "A piedi o in bici da soli o con gli amici",
            "A piedi o in bici accompagnati da un adulto",
          ]

          $scope.numberOfPlaysByType = [
            $scope.gameStats.plays.calendar.number,
            $scope.gameStats.plays.trip.number,
            $scope.gameStats.plays.pedibus.number
          ];
          $scope.numberOfPlaysByTypeLabels = [
            "Calendario",
            "Viaggio",
            "Pedibus"
          ];

          console.log("GameStat");
          console.log(data);
        }
      );
    } else {
      $state.go("home.games-list");
    }

    $scope.options = {
      tooltips: {
        enabled: false
      },
      hover: {mode: null},
      legend: {
        display: true,
        position: 'right',
        labels: {
          generateLabels: function(chart) {
              var data = chart.data;
              if (data.labels.length && data.datasets.length) {
                
                  return data.labels.map(function(label, i) {
                      var meta = chart.getDatasetMeta(0);
                      var ds = data.datasets[0];
                      var arc = meta.data[i];
                      var custom = arc && arc.custom || {};
                      var getValueAtIndexOrDefault = Chart.helpers.getValueAtIndexOrDefault;
                      var arcOpts = chart.options.elements.arc;
                      var fill = custom.backgroundColor ? custom.backgroundColor : getValueAtIndexOrDefault(ds.backgroundColor, i, arcOpts.backgroundColor);
                      var stroke = custom.borderColor ? custom.borderColor : getValueAtIndexOrDefault(ds.borderColor, i, arcOpts.borderColor);
                      var bw = custom.borderWidth ? custom.borderWidth : getValueAtIndexOrDefault(ds.borderWidth, i, arcOpts.borderWidth);

                      // We get the value of the current label
                      var total = chart.config.data.datasets[arc._datasetIndex].data.reduce(function(a, b) { return a + b; }, 0);
                      var value = chart.config.data.datasets[arc._datasetIndex].data[arc._index];

                      return {
                          text: Math.round(value / total * 1000) / 10 + "% " + label + " : " + value / 1000 + " KM",
                          fillStyle: fill,
                          strokeStyle: stroke,
                          lineWidth: bw,
                          hidden: isNaN(ds.data[i]) || meta.data[i].hidden,
                          index: i
                      };
                  });
              } else {
                  return [];
              }
          },
          fontSize: 15
        }
      },
      responsive: true,
      maintainAspectRatio: false,
    };

    function initParentNavigation() {
      $scope.$parent.$parent.title = "game_stat_title";
      $scope.$parent.$parent.backStateToGo = "home.games-list";
      $scope.$parent.$parent.hideBack = false;
    }

    $scope.daysAgo = function(timestamp) {
      var today = new Date();
      return Math.floor(Math.abs((today.getTime() - timestamp)/86400000));
    }
    $scope.pathPlayInWarning = function(timestamp) {
      var today = new Date();
      if ($scope.daysAgo(timestamp) >= 7) {
        return 'high-warning';
      } else if ($scope.daysAgo(timestamp) >= 3) {
        return 'medium-warning';
      } else {
        return 'no-warning';
      }
    }

  }
])
