angular.module('consoleControllers.mainCtrl', [])

.controller('MainCtrl', ['$scope', '$rootScope', '$location', 'DataService', '$window',
    function ($scope, $rootScope, $location, DataService)
    {
        $scope.selectedOwner = '';
        $scope.selectedInstitute = '';
        $scope.selectedSchool = '';
        $scope.institutesList = [];
        $scope.selectedGame = '';               // Per selezione itinerario

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
        });

        $scope.loadInstitutesList = function() {
            DataService.getInstitutesList($scope.selectedOwner).then(
                function(response) {
                    $scope.institutesList = response.data;
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        };

        $scope.loadSchoolsList = function() {
            DataService.getData($scope.selectedOwner, $scope.selectedInstitute, 'school').then(
                function(response) {
                  $rootScope.schools = response.data;
                }, function() {
                  alert('Errore nella richiesta.');
                }
            );
        };

        $scope.loadData = function(type) {
            if(type === 'all')              // valutare se effettivamente servirà
            {
                DataService.getData($scope.selectedOwner, $scope.selectedInstitute, 'school').then(
                    function(response) {
                        $rootScope.schools = response.data;
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );

                DataService.getData($scope.selectedOwner, $scope.selectedInstitute, 'game', $scope.selectedSchool).then(
                    function(response) {
                        $rootScope.games = response.data;
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );

                DataService.getData($scope.selectedOwner, $scope.selectedInstitute, 'itinerary', $scope.selectedSchool, $scope.selectedGame.objectId).then(
                    function(response) {
                        $rootScope.paths = response.data;
                        console.log(response.data);
                    }, function() {
                        alert('Errore nella richiesta.');
                    }
                );
            }
            else
                DataService.getData($scope.selectedOwner, $scope.selectedInstitute, type, $scope.selectedSchool, $scope.selectedGame.objectId).then(
                    function(response) {
                        if(type !== 'itinerary') {
                            eval("$rootScope." + type + "s = response.data");
                            eval("$scope." + type + "sModified = false");
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
]);
