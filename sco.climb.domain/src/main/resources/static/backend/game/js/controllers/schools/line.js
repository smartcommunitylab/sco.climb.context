angular.module('consoleControllers.line', [])

// Edit the line for the selected school
.controller('LineCtrl', function ($scope, $stateParams, $rootScope, $window, $timeout, DataService, uploadImageOnImgur, drawMapLine, createDialog) {
    $scope.$parent.selectedTab = 'lines';
    var currentSchool = $scope.$parent.currentSchool;
    InitLinePage();

    // Create $scope.line variable and init the map
    function InitLinePage() {
        if ($stateParams.idLine)     // se sto modificando una linea già esistente
            $scope.line = angular.copy($scope.$parent.schools[currentSchool].lines[$stateParams.idLine]);
        else
            $scope.line = {
                "name": '',
                "description": '',
                "stops": [],
                "polyline": ''         // TODO: valutare utilità
            };

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

    $scope.save = function () {
        /*$scope.line.polyline = drawMapLine.getPathPolyline();     // ottiene la polyline dal servizio (da fare?)*/
        if (checkFields()) {
            if ($stateParams.idLine)
                $scope.$parent.schools[currentSchool].lines[$stateParams.idLine] = $scope.line;
            else
                $scope.$parent.schools[currentSchool].lines.push($scope.line);

            // Back to the school
            $window.history.back();
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

    $scope.moveUp = function(origIndex)
    {
        var swap = $scope.line.stops[origIndex];
        $scope.line.stops[origIndex] = $scope.line.stops[origIndex-1];
        $scope.line.stops[origIndex-1] = swap;
    };

    $scope.moveDown = function(origIndex)
    {
        var swap = $scope.line.stops[origIndex];
        $scope.line.stops[origIndex] = $scope.line.stops[origIndex+1];
        $scope.line.stops[origIndex+1] = swap;
    };

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