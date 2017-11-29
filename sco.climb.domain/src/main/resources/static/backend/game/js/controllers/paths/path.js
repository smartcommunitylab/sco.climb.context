angular.module('consoleControllers.paths', ['ngSanitize'])

// Paths controller
.controller('PathsCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'itinerary';

    $scope.delete = function (path) {
        createDialog('templates/modals/delete-path.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData('itinerary', path).then(
                    function() {
                        console.log("Rimozione effettuata con successo.");
                        $scope.paths.splice($scope.paths.indexOf(path), 1);
                    }, function() {
                        alert("Errore nella richiesta.");
                    });
            } }
        });
    };
})



// Edit an existing path
.controller('PathCtrl', function ($scope, $stateParams, $state, $rootScope, $location, $timeout, DataService, MainDataService, createDialog) {
    $scope.$parent.mainView = 'itinerary';
    
    $scope.classes = [];

    $scope.initController = function() {

        if($scope.currentGame.classRooms != null) {
            $scope.currentGame.classRooms.forEach(function(entry) {
                var classEntry = {
                    value: false,
                    name: entry
                };
                $scope.classes.push(classEntry);
            });
            $scope.$broadcast('pathLoaded');
        }

        if ($scope.currentPath) { //edit path
            $scope.saveData = DataService.editData;
            $scope.classes.forEach(function(entry) {
                if($scope.currentPath.classRooms != null) {
                    if($scope.currentPath.classRooms.includes(entry.name)) {
                        entry.value = true;
                    }
                }
            });
            DataService.getData('legs', 
                    $stateParams.idDomain, 
                    $stateParams.idInstitute, 
                    $stateParams.idSchool,
                    null,
                    $stateParams.idGame,
                    $scope.currentPath.objectId).then(
                    function(response) {
                        console.log('Caricamento delle tappe a buon fine.');
                        $scope.legs = response.data;
                        $scope.$broadcast('legsLoaded');
                    }, function() {
                        alert('Errore nel caricamento delle tappe.');
                    }
            );
        } else {
            $scope.currentPath = {
                name: '',
                description: '',
                pedibusGameId: $stateParams.idGame,               
                ownerId: $stateParams.idDomain,
                classRooms: []
            }
            $scope.legs = [];
            $scope.saveData = DataService.saveData;
            $scope.$broadcast('legsLoaded');
        }
    }

    MainDataService.getDomains().then(function (response) {
        MainDataService.getInstitutes($stateParams.idDomain).then(function (response) {
            MainDataService.getSchools($stateParams.idInstitute).then(function (response) {
                MainDataService.getGames($stateParams.idSchool).then(function (response) {
                    $scope.currentGame = response.data.find(function (e) {return e.objectId == $stateParams.idGame});
                    MainDataService.getItineraries($stateParams.idGame).then(function (response) {
                        $scope.paths = response.data;
                        if ($stateParams.idPath) {
                            $scope.currentPath = angular.copy($scope.paths.find(function (e) {return e.objectId == $stateParams.idPath}));  
                        }
                        $scope.initController();
                    });
                });
            });
        });
    });

    // Reorder of the legs
    $scope.sortableOptions = {
        handle: ' .handle',
        axis: 'y',
        stop: function(e, ui) {
        	for (i = 0; i < $scope.legs.length; i++) { 
        		$scope.legs[i].position = i;
        	}
        }
    };

    // Save the changes made to the path
    $scope.save = function () {
        if (checkFields()) {
        	$scope.currentPath.classRooms = [];
            $scope.classes.forEach(function(entry) {
                if(entry.value) {
                    $scope.currentPath.classRooms.push(entry.name);
                }
            });
        	
            $scope.saveData('itinerary', $scope.currentPath).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function(response) {
                    console.log('Salvataggio del percorso a buon fine.');
                    if ($scope.currentPath.objectId) { //edited
                        for (var i = 0; i < $scope.paths.length; i++) {
                            if ($scope.paths[i].objectId == $scope.currentPath.objectId) $scope.paths[i] = $scope.currentPath;
                        }
                    } else {
                        $scope.paths.push(response.data);
                    }     
                  	for (i = 0; i < $scope.legs.length; i++) { 
                  		$scope.legs[i].position = i;
                  	}
                    $scope.currentPath.legs = $scope.legs;        // lo metto all'interno dell'oggetto per comodità nell'invio
                    $scope.saveData('legs', $scope.currentPath).then(
                        function(response) {
                            console.log('Salvataggio dati a buon fine.');
                            $state.go('root.paths-list');
                        }, function() {
                            alert('Errore nel salvataggio delle tappe.');
                        }
                    );
                }, function() {
                    alert('Errore nel salvataggio del percorso.');
                }
            );

        }
        else
        {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco, di avere inserito almeno una foto e un punto di interesse prima di salvare.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
        }
    };

    // Validation
    function checkFields() {
        var isValidate = true;
        var path = $scope.currentPath;

        if (!path.name || !path.description || path.name === "" || path.description === "")
            isValidate = false;

        return isValidate;
    }

    // Back without saving changes
    $scope.back = function() {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$state.go('root.paths-list');} }
        });
    };
})

.controller('InfoCtrl', function ($scope, $rootScope) {
    $scope.$parent.selectedTab = 'info';

    $scope.$on('pathLoaded', function(e) {  
        $scope.classToggled();            
    });

    $scope.toggleSelectedClasses = function() {
        $scope.classes.forEach(function(currentClass) {
            currentClass.value = $scope.classesAllSelected;
        });
    }
    $scope.classToggled = function(){
        $scope.classesAllSelected = $scope.classes.every(function(cl){ return cl.value; })
    }

    $scope.classToggled();
})

.controller('LegsListCtrl', function ($scope, $state, $stateParams, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'legs';

    if (!$stateParams.idPath) { //New path, need to save before creating legs
        $state.go('root.path.info');
        createDialog('templates/modals/save-path-before-edit-leg.html',{
            id : 'save-path-limit-dialog',
            title: 'Salva le info',
            noCancelBtn: true
        });
    }

    $scope.remove = function (leg) {
        createDialog('templates/modals/delete-leg.html',{
            id : 'delete-leg-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                $scope.legs.splice($scope.legs.indexOf(leg), 1);
              	for (i = 0; i < $scope.legs.length; i++) { 
              		$scope.legs[i].position = i;
              	}
                $scope.currentPath.legs = $scope.legs;
                DataService.editData('legs', $scope.currentPath).then(
                    function() {
                        console.log('Salvataggio dati a buon fine.');
                    }, function() {
                        alert('Errore nel salvataggio delle tappe.');
                    }
                );
            } }
        });
    };
    $scope.saveOrder = function() {
        if ($scope.enableOrder) {            
            $scope.currentPath.legs = $scope.legs;
            DataService.editData('legs', $scope.currentPath).then(
                function() {
                    console.log('Salvataggio ordine tappe a buon fine.');    
                    $scope.enableOrder = false;
                    createDialog('templates/modals/leg-order-saved.html',{
                        id : 'leg-order-saved',
                        title: 'Modifica percorsi sulla mappa',
                        noCancelBtn: true
                    });
                }, function() {
                    alert('Errore nel salvataggio dell\'ordine tappe.');
                }
            );
        } else {
            $scope.enableOrder = true;
        }
    };
})

.controller('MapCtrl', function ($scope, $state, $stateParams, $rootScope, $timeout, drawMap, createDialog) {
    $scope.$parent.selectedTab = 'map';
    $scope.toggle = true;
    $scope.initMap = function () {
        // Verifica che ci siano abbastanza tappe
        if(!$scope.legs || $scope.legs.length < 2)
        {
            if (!$stateParams.idPath) { //new path, return to info
                $state.go('root.path.info');
            } else {
                $state.go('root.path.legs');
            }

            
            createDialog('templates/modals/err-nolegs.html',{
                id : 'nolegs-dialog',
                title: 'Non ci sono abbastanza tappe!',
                success: { label: 'OK', fn: null },
                noCancelBtn: true
            });
        }
        else
            drawMap.createMap('map', $scope.legs);
    };

    $scope.computeLength = function() {
        var distanceInMeters = drawMap.getPathLength();
        return (distanceInMeters/1000).toFixed(0);
    };

    $scope.toggleMarkers = function () {
        $scope.toggle = !$scope.toggle;
        if ($scope.toggle)
            drawMap.showMarkers();
        else
            drawMap.hideMarkers();
    };
});