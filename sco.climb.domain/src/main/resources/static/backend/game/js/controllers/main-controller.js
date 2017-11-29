angular.module('consoleControllers.mainCtrl', [])

.controller('MainCtrl', function ($scope, $rootScope, $location, DataService, $window, MainDataService)
    {
        $scope.institutesList = [];
        $scope.selectedGame = ''//JSON.parse(localStorage.getItem("selectedGame")); // Per selezione itinerario

        // Variabili per evitare continue chiamate inutili al server
        $scope.pathsModified = false;
        $scope.gamesModified = false;
        $scope.schoolsModified = false;

        $scope.getLocation = function() {
            return $location.url()
        };
        
        $scope.logout = function() {
          $scope.selectedOwner = '';
          $scope.selectedInstitute = '';
          $scope.selectedSchool = '';
          $scope.institutesList = [];
          $scope.selectedGame = '';
          $scope.profile = null;
          localStorage.clear();
          sessionStorage.clear();
          var logoutUrl = DataService.getBaseUrl();
          logoutUrl = logoutUrl + '/logout?target=' + $location.path('/').absUrl();
          $window.location.href = logoutUrl;
        }

        MainDataService.getDomains().then(function (p) {
            $scope.profile = p;

            if ($scope.profile.ownerIds.length == 1) {
                MainDataService.setSelectedDomain($scope.profile.ownerIds[0]);
                $scope.selectedOwner = $scope.profile.ownerIds[0];
                console.log($scope.selectedOwner.objectId);
                $scope.loadInstitutesList($scope.profile.ownerIds[0]);
            }
        });

        $scope.loadInstitutesList = function(owner) {
            if (!owner) return;
            MainDataService.getInstitutes(owner).then(function (response) {
                $scope.institutesList = response.data;
                if ($scope.institutesList.length == 1) {
                    $scope.selectedInstitute = $scope.institutesList[0];
                    $scope.loadSchoolsList($scope.institutesList[0]);
                }
            });
        };

        $scope.loadSchoolsList = function(institute) {
            if (!institute) return;          
            MainDataService.getSchools(institute.objectId).then(function (response) {
                $scope.schools = response.data;
                if ($scope.schools.length == 1) {
                    $scope.selectedSchool = $scope.schools[0];
                    $scope.loadGames($scope.schools[0]);
                }
            });  
        };
        $scope.loadGames = function(school) {
            if (!school) return;          
            MainDataService.getGames(school.objectId).then(function (response) {
                $scope.games = response.data;
                if ($scope.games.length == 1) {
                    $scope.selectedGame = $scope.games[0];
                    $scope.loadItineraries($scope.games[0]);
                }
            });  
        };
        $scope.loadItineraries = function(game) {
            if (!game) return;          
            MainDataService.getItineraries(game.objectId).then(function (response) {
                $scope.paths = response.data;
            });  
        };

        $scope.uploadComplete = function (content) {
            if (content.id) {
                $rootScope.errorTexts = [];
                $rootScope.successText = 'Data successfully uploaded!';
                $scope.profile = content;
            }
            else {
                var txt = [];
                if (content.errorMessage) {
                    txt.push(content.errorMessage);
                } else {
                    txt.push("General server error");
                }
                $rootScope.successText = '';
                $rootScope.errorTexts = txt;
            }
        };

        $scope.exportPaths = function () {
            window.open('console/exportexcel', '_blank');
        };
    }
);
