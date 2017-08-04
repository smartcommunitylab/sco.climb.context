angular.module('consoleControllers.leg', [])

// Edit the leg for the selected path
.controller('LegCtrl', function ($scope, $stateParams, $rootScope, $window, $timeout, DataService, uploadImageOnImgur, drawMapPoi, createDialog) {
    $scope.$parent.selectedTab = 'legs';
    var currentPath = $scope.$parent.currentPath;
    InitLegPage();

    // Create $scope.leg variable and init the map
    function InitLegPage() {
        if ($stateParams.idLeg)     // se sto modificando un LEG già esistente
            $scope.leg = angular.copy($scope.$parent.paths[currentPath].legs[$stateParams.idLeg]);
        else
            $scope.leg = {
                "title": '',
                "description": '',
                "featuredImage": '',
                "coordinates": {
                    lat: 45.8832637,
                    lng: 11.0014507
                },
                "score": '',
                "polyline": '',         // NEW: stringa contenente il percorso compreso tra la tappa e la sua precedente (nel 1° LEG sarà vuota, ovviamente)
                "arriveBy": 'foot',     // NEW: mezzo con cui si arriva alla tappa (foot [default], plane, boat)
                "multimedia": [],       // NEW: array di oggetti contenente gli elementi multimediali
                "localId": $scope.$parent.paths[currentPath].legs.length
            };

        if($scope.leg.localId == 0)
            drawMapPoi.createMap('map-leg', null, $scope.leg.coordinates, $scope.leg.arriveBy);
        else
            drawMapPoi.createMap('map-leg', $scope.$parent.paths[currentPath].legs[$scope.leg.localId-1].coordinates, $scope.leg.coordinates, $scope.leg.arriveBy);
    }

    $scope.$on('poiMarkerPosChanged', function(event, newLat, newLng, wipeAirDistance) {     // listener del broadcast che indica il cambiamento della posizione del marker
        $scope.leg.coordinates.lat = newLat;
        $scope.leg.coordinates.lng = newLng;
        if(wipeAirDistance)
            document.getElementById('airDistance').value = '';       // pulisci la textbox per il calcolo della lunghezza della linea
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
    });

    $scope.updateTravelType = function (newTravelType) {
        drawMapPoi.setTravelType(newTravelType);
    };

    // Update the marker position when the user change coordinates
    $scope.updateMarkerPosition = function () {
        drawMapPoi.updateMarker($scope.leg.coordinates.lat, $scope.leg.coordinates.lng);
    };

    $scope.calculateNewMarkerPos = function(distance)
    {
        drawMapPoi.calculateMarkerPosFromDistance(Number(distance)*1000);
    };

    $scope.newMedia = {type: 'image'};      // Necessario a evitare che il campo "Tipologia" rimanga vuoto

    // Add new media item to the leg
    $scope.addMedia = function ()
    {
        if(!this.newMedia.url)      // controlla che sia stato inserito un URL
            alert("Non è stato inserito un indirizzo valido.");
        else
        {
            if ($scope.newMedia.type)
                $scope.leg.multimedia.push($scope.newMedia);
            else
                alert("Errore: il tipo dell'oggetto non è un tipo valido (solo immagine o video).");

            // Reset the newMedia object
            $scope.newMedia = {type: 'image'};
        }
    };

    $scope.save = function () {
        if ($scope.leg.localId > 0)
            $scope.leg.polyline = drawMapPoi.getPathPolyline();     // ottiene la polyline dal servizio
        if (checkFields()) {
            if ($stateParams.idLeg)
                $scope.$parent.paths[currentPath].legs[$stateParams.idLeg] = $scope.leg;
            else
                $scope.$parent.paths[currentPath].legs.push($scope.leg);

            // Back to the path
            $window.history.back();
        }
    };

    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.error','.ng-invalid');
        // Get all inputs
        if (invalidFields.length > 0 || $scope.leg.multimedia.length === 0) {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e di avere inserito almeno un elemento multimediale prima di salvare.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
            allCompiled = false;
        }
        return allCompiled;
    }

    // Exit without saving changes
    $scope.back = function () {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$window.history.back();} }
        });
    };

    // Upload image on imgur
    $scope.uploadPic = function (file) {
        uploadImageOnImgur(file).success(function (response) {
            // Update the link of the new media with the imgur link
            $scope.newMedia.url = response.data.link;
            // Reset the input field
            $scope.file = null;
        });
    };

    $scope.uploadFeaturedPic = function (file) {
        uploadImageOnImgur(file).success(function (response) {
            // Update the link of the new media with the imgur link
            $scope.leg.featuredImage = response.data.link;
        });
    };

    $scope.delete = function (element) {
        createDialog('templates/modals/delete-media.html',{
            id : 'delete-media-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {$scope.leg.multimedia.splice($scope.leg.multimedia.indexOf(element), 1);} }
        });
    };

    $scope.updateElementData = function(index, newTitle, newUrl)
    {
        $scope.leg.multimedia[index].title = newTitle;
        $scope.leg.multimedia[index].url = newUrl;
    }
});