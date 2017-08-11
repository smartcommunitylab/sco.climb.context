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
            if($scope.selectedInstitute === null) {     // serve a evitare che il campo "Gioco" rimanga visibile anche quando l'istituto viene riselezionato
                $scope.selectedSchool = '';             // TODO: da testare con più istituti
                return;
            }
            DataService.getData('school', 
            		$scope.selectedOwner, 
            		$scope.selectedInstitute.objectId).then(
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
                DataService.getData('school',
                		$scope.selectedOwner, 
                		$scope.selectedInstitute.objectId).then(
                    function(response) {
                        $rootScope.schools = response.data;
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
