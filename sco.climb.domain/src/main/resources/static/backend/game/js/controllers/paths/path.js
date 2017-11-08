angular.module('consoleControllers.paths', ['ngSanitize'])

// Paths controller
.controller('PathsCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'itinerary';
    if($scope.$parent.selectedOwner && $scope.$parent.selectedInstitute && $scope.$parent.selectedSchool && $scope.$parent.selectedGame && $scope.$parent.pathsModified)
        $scope.$parent.loadData($scope.$parent.mainView);

    $scope.delete = function (pathIndex) {
        createDialog('templates/modals/delete-path.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData($scope.$parent.mainView, $rootScope.paths[pathIndex]).then(
                    function() {
                        console.log("Rimozione effettuata con successo.");
                        $scope.$parent.loadData($scope.$parent.mainView);
                    }, function() {
                        alert("Errore nella richiesta.");
                    });
            } }
        });
    };
})



// Edit an existing path
.controller('PathCtrl', function ($scope, $stateParams, $rootScope, $location, $state, $timeout, DataService, createDialog) {
    $scope.$parent.mainView = 'itinerary';
    
    $scope.classes = [];
    if($scope.$parent.selectedGame.classRooms != null) {
    	$scope.$parent.selectedGame.classRooms.forEach(function(entry) {
    		var classEntry = {};
  			classEntry.value = false;
  			classEntry.name = entry;
  			$scope.classes.push(classEntry);
    	});
    }
    $scope.isNewPath = true;
    if ($stateParams.idPath)    // controlla se si sta modificando un percorso esistente
    {
        $scope.isNewPath = false;
        $scope.currentPath = Number($stateParams.idPath);
        $scope.saveData = DataService.editData;
        $scope.classes.forEach(function(entry) {
        	if($rootScope.paths[$scope.currentPath].classRooms != null) {
        		if($rootScope.paths[$scope.currentPath].classRooms.includes(entry.name)) {
        			entry.value = true;
        		}
        	}
        });
        DataService.getData('legs', 
        		$rootScope.paths[$scope.currentPath].ownerId, 
        		$scope.$parent.selectedInstitute.objectId,
        		$scope.$parent.selectedSchool.objectId,
        		null,
        		$rootScope.paths[$scope.currentPath].pedibusGameId, 
        		$rootScope.paths[$scope.currentPath].objectId).then(
                function(response) {
                    console.log('Caricamento delle tappe a buon fine.');
                    $scope.legs = response.data;
                }, function() {
                    alert('Errore nel caricamento delle tappe.');
                }
        );
    }
    else
    {
        $scope.currentPath = $rootScope.paths.push({        // variabile che indica la posizione nell'array del path che sta venendo editato
            name: '',
            description: '',
            pedibusGameId: $scope.$parent.selectedGame.objectId,     // NEW: id del gioco di appartenenza
            ownerId: $scope.$parent.selectedOwner,
            classRooms: []
        })-1;
        $scope.legs = [];
        $scope.saveData = DataService.saveData;
    }

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
        	$rootScope.paths[$scope.currentPath].classRooms = [];
          $scope.classes.forEach(function(entry) {
          	if(entry.value) {
          		$rootScope.paths[$scope.currentPath].classRooms.push(entry.name);
          	}
          });
        	
            $scope.saveData('itinerary', $rootScope.paths[$scope.currentPath]).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function(response) {
                    console.log('Salvataggio del percorso a buon fine.');
                    $rootScope.paths[$scope.currentPath] = response.data;
                  	for (i = 0; i < $scope.legs.length; i++) { 
                  		$scope.legs[i].position = i;
                  	}
                    $rootScope.paths[$scope.currentPath].legs = $scope.legs;        // lo metto all'interno dell'oggetto per comodità nell'invio
                    $scope.saveData('legs', $rootScope.paths[$scope.currentPath]).then(
                        function() {
                            console.log('Salvataggio dati a buon fine.');                            
                            $location.path('paths-list');
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
        var path = $rootScope.paths[$scope.currentPath];

        if (!path.name || !path.description || path.name === "" || path.description === "")
            isValidate = false;

        return isValidate;
    }

    // Back without saving changes
    $scope.back = function() {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$location.path('paths-list');} }
        });
    };

    $scope.legsTabSelected = function() {
        if ($scope.isNewPath) { //New path, need to save before creating legs
            $state.go('root.path.info');
            createDialog('templates/modals/save-path-before-edit-leg.html',{
                id : 'save-path-limit-dialog',
                title: 'Salva le info',
                noCancelBtn: true
            });
        }
    }
})

.controller('InfoCtrl', function ($scope, $rootScope) {
    $scope.$parent.selectedTab = 'info';
})

.controller('LegsListCtrl', function ($scope, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'legs';
    $scope.$parent.legsTabSelected();
    $scope.remove = function (idLeg) {
        createDialog('templates/modals/delete-leg.html',{
            id : 'delete-leg-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                $scope.legs.splice(idLeg, 1);
              	for (i = 0; i < $scope.legs.length; i++) { 
              		$scope.legs[i].position = i;
              	}
                $rootScope.paths[$scope.currentPath].legs = $scope.legs;
                DataService.editData('legs', $rootScope.paths[$scope.currentPath]).then(
                    function() {
                        console.log('Salvataggio dati a buon fine.');
                    }, function() {
                        alert('Errore nel salvataggio delle tappe.');
                    }
                );
            } }
        });
    };
})

.controller('MapCtrl', function ($scope, $rootScope, $timeout, $state, drawMap, createDialog) {
    $scope.$parent.selectedTab = 'map';
    $scope.toggle = true;
    $scope.initMap = function () {
        // Verifica che ci siano abbastanza tappe
        if($scope.$parent.legs.length < 2)
        {
            if ($scope.$parent.isNewPath) { //new path, return to info
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