/* global angular */
angular.module('climbGame.controllers.homepage', [])
  .controller('homepageCtrl', ['$scope', '$mdToast', '$state', 'profileService', 'dataService', 'CacheSrv', 'loginService', 'configService', 'mapService', '$mdDialog',
    function ($scope, $mdToast, $state, profileService, dataService, CacheSrv, loginService, configService, mapService, $mdDialog,) {
      $scope.notificationsPresent = false;
      $scope.notifications = [];
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
      $scope.status = '';

      $scope.go = function (path,params) {
        $scope.closeSideNavPanel()
        $state.go(path,params)
      }


      angular.extend($scope, {

        defaults: {
          zoomControl: false,
          worldCopyJump: true,
          attributionControl: true,
          name: 'Watercolor',
          url: 'https://stamen-tiles-{s}.a.ssl.fastly.net/watercolor/{z}/{x}/{y}.png',
          type: 'xyz',

        },
        center: {},
        pathLine: {},
        markers: [],
        layers: {
          baselayers: {
            altro: {
              name: 'Watercolor',
              url: 'https://stamen-tiles-{s}.a.ssl.fastly.net/watercolor/{z}/{x}/{y}.png',
              type: 'xyz',
              layerOptions: {
                attribution: 'Map tiles by Stamen Design, under CC BY 3.0. Data by OpenStreetMap, under CC-BY-SA'
              }
            },
            osm: {
              name: 'OpenStreetMap',
              url: 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',
              type: 'xyz'
            },
            satellite: {
              name: 'Satellitare',
              url: 'https://services.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}?blankTile=false',
              type: 'xyz',
              layerOptions: {
                attribution: 'Copyright ESRI. Sources: Esri, DigitalGlobe, Earthstar Geographics, CNES/Airbus DS, GeoEye, USDA FSA, USGS, Aerogrid, IGN, IGP, and the GIS User Community'
              }
            },
            fisica: {
              name: 'Fisica',
              url: 'http://{s}.tile.stamen.com/terrain/{z}/{x}/{y}.png',
              type: 'xyz',
              layerOptions: {
                attribution: 'Map tiles by Stamen Design, under CC BY 3.0. Data © OpenStreetMap contributors.'
              }
            }
          }
        },


      })


      $scope.init = function () {

        profileService.getProfile().then(function (profile) {
          loginService.setUserToken(profile.token)
          loginService.setAllOwners(profile.ownerIds)

          dataService.getStatus().then(
            function (data) {


              console.log(data)
              $scope.globalTeam = data.game.globalTeam;
              $scope.legs = data.legs;


              for (var i = 0; i < data.teams.length; i++) {
                if (data.teams[i].classRoom == data.game.globalTeam) {

                  if (data.teams[i].currentLeg) {
                    $scope.currentLeg = data.teams[i].currentLeg;

                  } else {
                    $scope.currentLeg = data.legs[data.legs.length - 1];

                  }
                  $scope.lat = data.teams[i].previousLeg.geocoding[1];
                  $scope.lng = data.teams[i].previousLeg.geocoding[0];

                  $scope.center = {
                    'lng': data.teams[i].previousLeg.geocoding[0],
                    'lat': data.teams[i].previousLeg.geocoding[1],
                    zoom: 8
                  }

                  $scope.actualScore = Math.round(data.teams[i].score / 1000);
                  $scope.scoreToEnd = Math.round(data.teams[i].scoreToEnd / 1000);
                  $scope.scoreToNext = Math.round(data.teams[i].scoreToNext / 1000);
                  $scope.nextGoal = $scope.actualScore + $scope.scoreToEnd;
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
              $scope.markers.push({
                getMessageScope: function () {
                  return $scope;
                },
                lng: $scope.lng,
                lat: $scope.lat,

                icon: {
                  iconUrl: '' + configService.IMAGES_PREFIX_URL + 'img/POI_here.png',
                  iconSize: [50, 50],
                  iconAnchor: [25, 50],
                  popupAnchor: [0, -50]
                }
              });

            })

          CacheSrv.resetLastCheck('calendar')

          dataService.getNotifications(CacheSrv.getLastCheck('calendar')).then(
            function (data) {
              console.log(data)
              if (data.length == 0) {
                $scope.notifications = data;
              } else {
                $scope.notificationsPresent = true;
                if (data[0].badge != null) {
                  $scope.notifications = data[0].badge;
                } else { $scope.notifications = data[1].badge; }

              }
              $scope.isLoading = false;
            }, function (error) {
              $mdToast.show($mdToast.simple().content('Errore nel caricamento delle notifiche'))
              $scope.isLoading = false;
            })


          dataService.getChallenges().then(
            function (data) {

              if (data != []) {
                if (data[data.length - 1].playerId != 'Scuola') {
                  if (data.state == null) {
                    $scope.checkChallenge = false;
                  }
                  $scope.challenges = data[data.length - 1];

                } else {
                  $scope.challenges = data[data.length - 2];

                }
              }
              else {
                $scope.challenges = data;
              }
            })
        }

        )
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
