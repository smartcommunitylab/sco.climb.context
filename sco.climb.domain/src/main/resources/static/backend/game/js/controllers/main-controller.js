angular.module('consoleControllers.mainCtrl', [])

.controller('MainCtrl', function ($scope, $rootScope, $location, DataService, $window)
    {
        $scope.selectedOwner = JSON.parse(localStorage.getItem("selectedOwner"));
        $scope.selectedInstitute = JSON.parse(localStorage.getItem("selectedInstitute"));
        $scope.selectedSchool = JSON.parse(localStorage.getItem("selectedSchool"));
        $scope.institutesList = [];
        $scope.selectedGame = ''//JSON.parse(localStorage.getItem("selectedGame")); // Per selezione itinerario

        // Variabili per evitare continue chiamate inutili al server
        $scope.pathsModified = false;
        $scope.gamesModified = false;
        $scope.schoolsModified = false;

        $scope.getLocation = function() {
            return $location.url()
        };

        DataService.getProfile().then(function (p) {
            $scope.profile = p;
            $rootScope.profile = $scope.profile;
            localStorage.setItem("local_profile", JSON.stringify($scope.profile));

            if ($scope.profile.ownerIds.length == 1) {
                $scope.selectedOwner = $scope.profile.ownerIds[0];
                $scope.loadInstitutesList();
            }
        });
        
        $scope.logout = function() {
          $scope.selectedOwner = '';
          $scope.selectedInstitute = '';
          $scope.selectedSchool = '';
          $scope.institutesList = [];
          $scope.selectedGame = ''; 
          //TODO: logout remove localStorage 
          $scope.profile = null;
          $rootScope.profile = null;
          var logoutUrl = DataService.getBaseUrl();
          logoutUrl = logoutUrl + '/logout?target=' + $location.path('/').absUrl();
          $window.location.href = logoutUrl;
        }

        $scope.loadInstitutesList = function() {
            localStorage.setItem("selectedOwner", JSON.stringify($scope.selectedOwner));
            DataService.getInstitutesList($scope.selectedOwner).then(
                function(response) {
                    $scope.institutesList = response.data;
                    if ($scope.institutesList.length == 1) {
                        $scope.selectedInstitute = $scope.institutesList[0];
                        $scope.loadSchoolsList();
                    }
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        };

        $scope.loadSchoolsList = function() {
            if($scope.selectedInstitute === null) {     // serve a evitare che il campo "Gioco" rimanga visibile anche quando l'istituto viene riselezionato
                $scope.selectedSchool = '';             // TODO: da testare con più istituti
                return;
            }
            localStorage.setItem("selectedInstitute", JSON.stringify($scope.selectedInstitute));
            DataService.getData('school', 
            		$scope.selectedOwner, 
            		$scope.selectedInstitute.objectId).then(
                function(response) {
                    if ($rootScope.schools == undefined) { //already parsed from localstorage, not fresh data but needed for refresh in child controller
                        $rootScope.schools = response.data;
                        localStorage.setItem("local_schools", JSON.stringify($scope.schools));
                    }
                    if ($rootScope.schools.length == 1) {
                        $scope.selectedSchool = $scope.schools[0];
                        $scope.loadData('game');
                    }
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        };

        $scope.loadData = function(type) {
            localStorage.setItem("selectedSchool", JSON.stringify($scope.selectedSchool));
            if(type === 'all')              // valutare se effettivamente servirà
            {
                DataService.getData('school',
                		$scope.selectedOwner, 
                		$scope.selectedInstitute.objectId).then(
                    function(response) {
                        $rootScope.schools = response.data;
                        localStorage.setItem("local_schools", JSON.stringify($scope.schools));
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );

                DataService.getData('game', 
                		$scope.selectedOwner, 
                		$scope.selectedInstitute.objectId, 
                		$scope.selectedSchool.objectId).then(
                    function(response) {
                        $rootScope.games = response.data;
                        localStorage.setItem("local_games", JSON.stringify($scope.games));
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );

                DataService.getData('itinerary', 
                		$scope.selectedOwner, 
                		$scope.selectedInstitute.objectId, 
                		$scope.selectedSchool.objectId, 
                		null,
                		$scope.selectedGame.objectId).then(
                    function(response) {
                        $rootScope.paths = response.data;
                        localStorage.setItem("local_paths", JSON.stringify($scope.paths));
                        console.log(response.data);
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );
            }
            else
                DataService.getData(type, 
                		$scope.selectedOwner, 
                		$scope.selectedInstitute.objectId, 
                		$scope.selectedSchool.objectId,
                		null,
                		$scope.selectedGame.objectId).then(
                    function(response) {
                        if(type !== 'itinerary') {
                            eval("$rootScope." + type + "s = response.data");
                            eval("$scope." + type + "sModified = false");
                            localStorage.setItem("local_"+type+"s", JSON.stringify(response.data));

                            if (type == 'game') {
                                if ($rootScope.games.length == 1) {
                                    $scope.selectedGame = $scope.games[0];
                                    $scope.loadData('itinerary');
                                }
                            }

                        }
                        else {
                            $rootScope.paths = response.data;
                            $scope.pathsModified = false;
                        }
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );
        };

        $scope.uploadComplete = function (content) {
            if (content.id) {
                $rootScope.errorTexts = [];
                $rootScope.successText = 'Data successfully uploaded!';
                $scope.profile = content;
                $rootScope.profile = $scope.profile;
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
