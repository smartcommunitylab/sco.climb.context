angular.module('consoleControllers.line', [])

// Edit the line for the selected school
.controller('LineCtrl', function ($scope, $stateParams, $rootScope, $window, $timeout, DataService, uploadImageOnImgur, drawMapLine, createDialog) {
    $scope.$parent.selectedTab = 'lines';
    var currentSchool = $scope.$parent.currentSchool;
    InitLinePage();

    // Create $scope.line variable and init the map
    function InitLinePage() {
        if ($stateParams.idLine)     // se sto modificando una linea già esistente
        {
            $scope.line = angular.copy($rootScope.schools[currentSchool].lines[$stateParams.idLine]);
            $scope.saveData = DataService.editData;
        }
        else
        {
            $scope.line = {
                "name": '',
                "instituteId": $scope.$parent.selectedInstitute.objectId,
                "ownerId": $scope.$parent.selectedOwner,
                "schoolId": $rootScope.schools[currentSchool].objectId,
                "from": '',             // TODO: DATA DI INIZIO/FINE LINEA
                "to": '',
                "distance": '',         // TODO
                "stops": []
            };
            $scope.saveData = DataService.saveData;
        }
        drawMapLine.createMap('map-line', $scope.line.stops);
    }

    $scope.sortableOptions = {
        handle: ' .handle',
        axis: 'y',
        stop: function(){$scope.updatePath()}
    };

    $scope.$on('stopMarkerPosChanged', function(event, stopNumber, newLat, newLng) {     // listener del broadcast che indica il cambiamento della posizione del marker
        $scope.line.stops[stopNumber].coordinates.lat = newLat;
        $scope.line.stops[stopNumber].coordinates.lng = newLng;
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
        drawMapLine.updateMarkers($scope.line.stops);
    });

    // Update the marker position when the user change coordinates
    $scope.updatePath = function () {
        drawMapLine.updateMarkers($scope.line.stops);
    };

    $scope.save = function () {     // TODO: da implementare salvataggio fermate
        if (checkFields()) {
            $scope.saveData('route', $scope.line).then(
                function() {
                    console.log('Linea salvata.');
                    DataService.getData($rootScope.schools[$scope.currentSchool].ownerId, $rootScope.schools[$scope.currentSchool].instituteId, 'route', $rootScope.schools[$scope.currentSchool].objectId).then(       // riaggiorno la lista delle linee
                        function(response) {
                            console.log('Caricamento delle linee a buon fine.');
                            $rootScope.schools[$scope.currentSchool].lines = response.data;
                        }, function() {
                            alert('Errore nel caricamento delle linee.');
                        }
                    );
                    $window.history.back();
                }, function() {
                    alert('Errore nel caricamento della linea.');
                }
            );
        }
    };

    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.ng-invalid');
        // Get all inputs
        if (invalidFields.length > 0 || $scope.line.stops.length < 2) {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e di aver inserito almeno due fermate.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
            allCompiled = false;
        }
        return allCompiled;
    }

    $scope.addStop = function()
    {
        var stopsArray = $scope.line.stops;       // per migliorare la leggibilità
        stopsArray.push({
            coordinates: (stopsArray.length > 0) ? angular.copy(stopsArray[stopsArray.length-1].coordinates) : {lat: 45.88326, lng: 11.00145}
        });
        $scope.updatePath();
    };

    $scope.deleteStop = function(index)
    {
        $scope.line.stops.splice(index, 1);
        $scope.updatePath();
    };

    // Exit without saving changes
    $scope.back = function () {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$window.history.back();} }
        });
    };
});