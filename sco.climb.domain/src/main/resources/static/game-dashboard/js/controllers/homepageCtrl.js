/* global angular */
angular.module('climbGame.controllers.homepage', [])
  .controller('homepageCtrl', ['$scope', '$mdToast', '$state', '$translate', 'leafletData', 'profileService', 'dataService', 'CacheSrv', 'loginService', 'configService', 'mapService', '$mdDialog',
    function ($scope, $mdToast, $state, $translate, leafletData, profileService, dataService, CacheSrv, loginService, configService, mapService, $mdDialog,) {
      $scope.notificationsPresent = false;
      $scope.notifications = [];
      $scope.notificationText = '';
      $scope.challenges = [];
      $scope.globalTeam = '';
      $scope.legs = [];
      $scope.actualScore = 0;
      $scope.scoreToEnd = 0;
      $scope.scoreToNext = 0;
      $scope.nextGoal = 0;
      $scope.endReached = false;
      $scope.isLoading = true;
      $scope.path = [];
      $scope.legs = [];
      $scope.lat = 0;
      $scope.lng = 0;
      $scope.currentLeg = [];
      $scope.checkChallenge = false;
      $scope.gameName = '';
      $scope.status = '';
      $scope.go = function (path, params) {
        $scope.closeSideNavPanel()
        $state.go(path, params)
      }
      angular.extend($scope, {

        defaults: {
          zoomControl: false,
          worldCopyJump: true,
        },
        layers: {
          baselayers: {
            water: {
              name: 'Watercolor',
              url: 'https://stamen-tiles-{s}.a.ssl.fastly.net/watercolor/{z}/{x}/{y}.png',
              type: 'xyz',
              layerOptions: {
                showOnSelector: false,
                noWrap: true
                // attribution: 'Map tiles by Stamen Design, under CC BY 3.0. Data by OpenStreetMap, under CC-BY-SA'
              }
            }
          }
        },
        center: {},
        pathLine: {},
        markers: []
      })
      $scope.getNotifications = function () {
        dataService.getNotifications(CacheSrv.getLastCheck('calendar')).then(
          function (data) {
            if (data && data.length) {
              console.log('[Calendar] New notifications: ' + data.length)
              angular.forEach(data, function (notification) {
                notification.data = $scope.convertFields(notification.data)
                if (!CacheSrv.isGameFinishedNotified(loginService.getOwnerId(),
                  loginService.getGameId(), loginService.getClassRoom())) {
                  if ((notification.key == 'GameFinished') &&
                    (!$scope.isGameFinishedNotificationDisplaied)) {
                    $scope.gameFinishedNotification = notification;
                    $scope.isGameFinishedNotificationDisplaied = true;
                    $mdDialog.show({
                      // targetEvent: $event,
                      scope: $scope, // use parent scope in template
                      preserveScope: true, // do not forget this if use parent scope
                      template: '<md-dialog-game-finisched>' +
                        '<div class="cal-dialog-game-finisched">' +
                        '  <div class="cal-dialog-title">COMPLIMENTI!</div>' +
                        '  <div class="cal-dialog-text">{{"notif_gameFinishedDialog1" | translate}}</div>' +
                        '  <div class="cal-dialog-text">{{"notif_gameFinishedDialog2" | translate:gameFinishedNotification.data}}</div>' +
                        '  <img class="cal-dialog-img" ng-src="{{lastLeg.imageUrl}}">' +
                        '  <div class="cal-dialog-leg">{{"notif_gameFinishedDialogLeg" | translate:gameFinishedNotification.data}}</div>' +
                        '  <div layout="row" layout-align="end">' +
                        '    <div layout="column" layout-align="end">' +
                        '      <md-button ng-click="closeDialog()" class="send-dialog-dismiss">' +
                        '        Chiudi' +
                        '      </md-button>' +
                        '    </div>' +
                        '  </div>' +
                        '</div></md-dialog-game-finisched>',
                      controller: function DialogController($scope, $mdDialog) {
                        $scope.closeDialog = function () {
                          CacheSrv.setGameFinishedNotified(loginService.getOwnerId(),
                            loginService.getGameId(), loginService.getClassRoom(), true);
                          $scope.isGameFinishedNotificationDisplaied = false;
                          $mdDialog.hide();
                        }
                      }
                    })
                  }
                }
              })
              if (data.length == 0) {
                $scope.notifications = data;
              } else {
                $scope.notificationsPresent = true;
                if (data[0].badge != null) {
                  $scope.notificationText = $translate.instant('notif_badge', { badge: escape(data[0].badge) })
                } else {
                  if (data[0].key)
                    $scope.notificationText = $translate.instant('notif_' + data[0].key, data[0].data)
                }
              }
              CacheSrv.updateLastCheck('calendar');
            }
            $scope.isLoading = false;

          },
          function (reason) {
            console.log('[Calendar]' + reason);
            $mdToast.show($mdToast.simple().content('Errore nel caricamento delle notifiche'))
            $scope.isLoading = false;
          }
        )
      }
      $scope.getChallenges = function () {
        var cleanStatesChallenges = function (arrayOfChallenges) {
          $scope.openChallenge = false;
          var challengesNotCompleted = [];
          var d = new Date();
          var now = d.getTime();
          //first get all the not completed
          for (var i = 0; i < arrayOfChallenges.length; i++) {
            if (!arrayOfChallenges[i].completed && !arrayOfChallenges[i].fields.prizeWon) {
              if (arrayOfChallenges[i].start > now) {
                continue;
              }
              if (arrayOfChallenges[i].hasOwnProperty('end')) {
                if (arrayOfChallenges[i].end > now) {
                  challengesNotCompleted.push(arrayOfChallenges[i]);
                }
              } else {
                challengesNotCompleted.push(arrayOfChallenges[i]);
              }
            }
          }
          if (challengesNotCompleted[0]) {
            $scope.lastChallenge.state = [challengesNotCompleted[0]];
            $scope.openChallenge = true;
          }
          for (var j = 1; j < challengesNotCompleted.length; j++) {
            if (challengesNotCompleted[j] && challengesNotCompleted[j].start > $scope.lastChallenge.state[0].start) {
              $scope.lastChallenge.state = [challengesNotCompleted[j]]
            }
          }
        }
        dataService.getChallenges().then(
          function (data) {
            $scope.lastChallenge = { state: [] }
            if (data && data.length) {
              console.log('[Calendar] Challenges: ' + data.length)
              for (var i = 0; i < data.length; i++) {
                if (data[i].state) {
                  angular.forEach(data[i].state, function (state) {
                    state.fields = $scope.convertFields(state.fields)
                    $scope.lastChallenge.state.push(state)
                  })
                }
              }
              cleanStatesChallenges($scope.lastChallenge.state)
            }
          },
          function (reason) {
            console.log('[Calendar]' + reason)
          }
        )
      }
      $scope.init = function () {
        profileService.getProfile().then(function (profile) {
          loginService.setUserToken(profile.token)
          loginService.setAllOwners(profile.ownerIds)

          dataService.getStatus().then(
            function (data) {
              $scope.status = data;
              console.log(data)
              if (data) {
                $scope.globalTeam = data.game.globalTeam;
                $scope.legs = data.legs;
                if (data.game)
                  $scope.gameName = data.game.gameName;
                for (var i = 0; i < data.teams.length; i++) {
                  if (data.teams[i].classRoom == data.game.globalTeam) {
                    $scope.globalStatus = data.teams[i];
                    if (data.teams[i].currentLeg) {
                      $scope.currentLeg = data.teams[i].currentLeg;

                    } else {
                      $scope.currentLeg = data.legs[data.legs.length - 1];

                    }
                    $scope.previousStep = data.teams[i].previousLeg.position + 1;
                    $scope.totalStep = data.legs.length;
                    $scope.lat = data.teams[i].previousLeg.geocoding[1];
                    $scope.lng = data.teams[i].previousLeg.geocoding[0];

                    $scope.center = {
                      'lng': data.teams[i].previousLeg.geocoding[0],
                      'lat': data.teams[i].previousLeg.geocoding[1]
                      // zoom: 10
                    }
                    //% 100 completed and 0 just started
                    $scope.actualScore = Math.round(data.teams[i].score / (data.teams[i].score + data.teams[i].scoreToEnd)) * 100;
                    $scope.scoreToEnd = Math.round(data.teams[i].scoreToEnd / 1000);
                    $scope.scoreToNext = Math.round(data.teams[i].scoreToNext / 1000);
                    $scope.nextGoal = Math.round($scope.actualScore + $scope.scoreToEnd);
                    if ($scope.nextGoal <= $scope.actualScore) {
                      $scope.endReached = true;
                    }
                  }
                }

                for (var i = 0; i < data.legs.length; i++) {

                  var icon = getMarkerIcon($scope.legs[i]);
                  $scope.markers.push(getMarker($scope.legs[i], null, icon, i));

                  $scope.pathLine[i] = {
                    color: '#3f51b5',
                    weight: 5,
                    latlngs: mapService.decode(data.legs[i].polyline)
                  }
                }
                console.log($scope.pathLine)
                addPlayerPosition();
                $scope.markers.push({
                  getMessageScope: function () {
                    return $scope;
                  },
                  lat: $scope.actualMarkerPosition[0],
                  lng: $scope.actualMarkerPosition[1],

                  icon: {
                    iconUrl: '' + configService.IMAGES_PREFIX_URL + 'img/POI_here.png',
                    iconSize: [50, 50],
                    iconAnchor: [25, 50],
                    popupAnchor: [0, -50]
                  }
                });
                leafletData.getMap('maphome').then(function (map) {
                  var markers = []
                  for (var i = 0; i < $scope.markers.length; i++) {
                    markers.push(new L.marker([$scope.markers[i].lat, $scope.markers[i].lng]))
                  };
                  markers.push(new L.marker([$scope.lat, $scope.lng]))
                  var group = new L.FeatureGroup(markers);
                  map.fitBounds(group.getBounds(), { padding: [10, 10] });
                  // map.fitBounds($scope.markers);
                });
              }
            })

          CacheSrv.resetLastCheck('calendar');
          $scope.getNotifications();
          $scope.getChallenges();

        }
        )
        //call the auto refresh for challenge and calendar
        refresh();
      }

      function refresh() {
        $scope.getChallenges();
        $scope.getNotifications();
        setTimeout(refresh, 60000);
      }
      function addPlayerPosition() {
        //var poly = {};
        var poligons = {};
        //var tmpPolys = [];
        $scope.actualMarkerPosition = [];
        if ($scope.status) {
          var polylines = $scope.status.legs;
          var myLeg = null;
          var isCurrLeg = true;
          if ($scope.globalStatus.currentLeg) {
            myLeg = $scope.globalStatus.currentLeg;
          }
          if ($scope.globalStatus.previousLeg) {
            myLeg = $scope.globalStatus.previousLeg;
            isCurrLeg = false;
          }
          var myScores = $scope.globalStatus.score; //2800;
          var totalRange = ($scope.globalStatus.currentLeg) ? ($scope.globalStatus.currentLeg.score - myLeg.score) : ($scope.globalStatus.score + $scope.globalStatus.scoreToEnd); //1000;
          var doneRange = ($scope.globalStatus.currentLeg) ? (myScores - myLeg.score) : myLeg.score; //800;
          //        if (doneRange > totalRange)
          //          (doneRange = totalRange);
          if (totalRange == 0) {
            totalRange = myLeg.score;
            doneRange = myScores;
          }
          if (polylines) {
            for (var i = 0; i < polylines.length; i++) {
              var pointArr = polyline.decode(polylines[i].polyline);
              var middlePoint = Math.floor(pointArr.length / 2);
              if ((polylines[i].position == myLeg.position + 1) || $scope.endReached) {
                // Actual leg. I have to split them in 2 part considering the percentage
                var actual_completing = doneRange / totalRange; //0,8
                var lengthInMeters = mapService.sumAllDistances(pointArr) * 1000;
                if (actual_completing > 0) {
                  if ((actual_completing < 1)) { // case completing between 1% and 99%
                    var proportionalLength = lengthInMeters * actual_completing;
                    var splittedSubPolys = mapService.retrievePercentagePoly(pointArr, actual_completing, proportionalLength);
                    for (var y = 0; y < splittedSubPolys.length; y++) {
                      if (y == 0) { // I initialize the actual position for the specific marker with the last coord of the firt splitted polyline
                        $scope.actualMarkerPosition = splittedSubPolys[y][splittedSubPolys[y].length - 1];
                      }
                    }
                  } else {
                    //end of the leg
                    $scope.actualMarkerPosition = pointArr[pointArr.length - 1];

                  }
                } else {
                  //beginning of the leg
                  $scope.actualMarkerPosition = pointArr[0];
                }
              }
            }
          }
        }
      }

      function getMarkerIcon(leg) {

        if (leg.position == 0) {
          return configService.IMAGES_PREFIX_URL + 'img/POI_start.png'
        }
        if (leg.position == $scope.legs.length - 1) {
          return configService.IMAGES_PREFIX_URL + './img/POI_destination.png'
        }

        console.log($scope.currentLeg.position)
        return configService.getIconImg(leg.icon, leg.position < $scope.currentLeg.position);
      }



      function getMarker(data, url, icon, i) {
        var returnMarker = {};
        returnMarker = {
          getMessageScope: function () {
            return $scope;
          },
          lat: data.geocoding[1],
          lng: data.geocoding[0],

          icon: {
            iconUrl: icon,
            iconSize: [50, 50],
            iconAnchor: [25, 25],
            popupAnchor: [0, -25]
          }
        }
        return returnMarker;
      }

      $scope.showGite = function (ev) {
        $mdDialog.show({
          controller: '',
          templateUrl: 'templates/pop-up-excursion.html',

          // Appending dialog to document.body to cover sidenav in docs app
          // Modal dialogs should fully cover application to prevent interaction outside of dialog

          parent: angular.element(document.body),
          targetEvent: ev,
          clickOutsideToClose: true,
          fullscreen: $scope.customFullscreen // Only for -xs, -sm breakpoints.
        }).then(function (answer) {
          $scope.status = 'You said the information was "' + answer + '".';
        }, function () {
          $scope.status = 'You cancelled the dialog.';
        }
        );
      };
      $scope.init();

    }

  ])
