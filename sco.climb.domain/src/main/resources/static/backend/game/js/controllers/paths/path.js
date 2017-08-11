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
.controller('PathCtrl', function ($scope, $stateParams, $rootScope, $location, $timeout, DataService, createDialog) {
    $scope.$parent.mainView = 'itinerary';

    if ($stateParams.idPath)    // controlla se si sta modificando un percorso esistente
    {
        $scope.currentPath = Number($stateParams.idPath);
        $scope.saveData = DataService.editData;
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

    /*function makeid() {         // data e ora di creazione + codice random di 4 caratteri -- GENERATI DAL SERVER, possono tornare utili per testing in locale
        var id = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        var date = new Date();
        date = $filter('date')(date,'ddMMyy-hhmmss','+0100');

        for (var i = 0; i < 4; i++)
            id += possible.charAt(Math.floor(Math.random() * possible.length));

        return date + '_' + id;
    }*/

    // Reorder of the legs
    $scope.sortableOptions = {
        handle: ' .handle',
        axis: 'y'
    };

    // Save the changes made to the path
    $scope.save = function () {
        if (checkFields())
        {
            $scope.saveData($scope.$parent.mainView, $rootScope.paths[$scope.currentPath]).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function(response) {
                    console.log('Salvataggio del percorso a buon fine.');
                    $rootScope.paths[$scope.currentPath] = response.data;
                }, function() {
                    alert('Errore nel salvataggio del percorso.');
                }
            );

            $rootScope.paths[$scope.currentPath].legs = $scope.legs;        // lo metto all'interno dell'oggetto per comodità nell'invio
            $scope.saveData('legs', $rootScope.paths[$scope.currentPath]).then(
                function() {
                    console.log('Salvataggio dati a buon fine.');
                    $location.path('paths-list');
                }, function() {
                    alert('Errore nel salvataggio delle tappe.');
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

        if ($scope.legs.length === 0)
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
})

.controller('InfoCtrl', function ($scope, $rootScope) {
    $scope.$parent.selectedTab = 'info';
})

.controller('LegsListCtrl', function ($scope, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'legs';
    $scope.remove = function (idLeg) {
        createDialog('templates/modals/delete-leg.html',{
            id : 'delete-leg-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                $scope.legs.splice(idLeg, 1);
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

.controller('MapCtrl', function ($scope, $rootScope, $timeout, drawMap, createDialog) {
    $scope.$parent.selectedTab = 'map';
    $scope.toggle = true;
    $scope.initMap = function () {
        // Verifica che ci siano abbastanza tappe
        if($scope.$parent.legs.length < 2)
        {
            createDialog('templates/modals/err-nolegs.html',{
                id : 'nolegs-dialog',
                title: 'Non ci sono abbastanza tappe!',
                success: { label: 'Torna indietro', fn: function() {
                    window.history.back();
                } },
                footerTemplate: '<button class="btn btn-danger" ng-click="$modalSuccess()">{{$modalSuccessLabel}}</button>'
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