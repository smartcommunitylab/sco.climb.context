angular.module('consoleControllers.leg', [])

// Edit the leg for the selected path
.controller('LegCtrl', function ($scope, $stateParams, $rootScope, $window, $timeout, DataService, uploadImageOnImgur, drawMapLeg, createDialog) {
    $scope.$parent.selectedTab = 'legs';
    var currentPath = $scope.$parent.currentPath;
    InitLegPage();

    // Create $scope.leg variable and init the map
    function InitLegPage() {
        if ($stateParams.idLeg)     // se sto modificando una tappa già esistente
        {
            $scope.leg = angular.copy($scope.$parent.legs[$stateParams.idLeg]);
            $scope.leg.coordinates = {lat: $scope.leg.geocoding[1], lng: $scope.leg.geocoding[0]};      // trasformo le coordinate in un formato gestibile da GMaps
        }
        else
            $scope.leg = {
                "name": '',
                "description": '',
                "imageUrl": '',
                "coordinates": {
                    lat: 45.8832637,
                    lng: 11.0014507
                },
                "score": '',
                "polyline": '',         // NEW: stringa contenente il percorso compreso tra la tappa e la sua precedente (nel 1° LEG sarà vuota, ovviamente)
                "transport": 'foot',     // NEW: mezzo con cui si arriva alla tappa (foot [default], plane, boat)
                "externalUrls": [],        // NEW: array di oggetti contenente gli elementi multimediali
                "position": $scope.$parent.legs.length
            };

        if($scope.leg.position === 0)
            drawMapLeg.createMap('map-leg', null, $scope.leg.coordinates, $scope.leg.transport);
        else
            drawMapLeg.createMap('map-leg', {lat: $scope.$parent.legs[$scope.leg.position-1].geocoding[1], lng: $scope.$parent.legs[$scope.leg.position-1].geocoding[0]}, $scope.leg.coordinates, $scope.leg.transport);
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
        drawMapLeg.setTravelType(newTravelType);
    };

    // Update the marker position when the user change coordinates
    $scope.updateMarkerPosition = function () {
        drawMapLeg.updateMarker($scope.leg.coordinates.lat, $scope.leg.coordinates.lng);
    };

    $scope.calculateNewMarkerPos = function(distance)
    {
        drawMapLeg.calculateMarkerPosFromDistance(Number(distance)*1000);
    };

    $scope.newMedia = {type: 'image'};      // Necessario a evitare che il campo "Tipologia" rimanga vuoto

    // Add new media item to the leg
    $scope.addMedia = function ()
    {
        if(!this.newMedia.link)      // controlla che sia stato inserito un URL
            alert("Non è stato inserito un indirizzo valido.");
        else
        {
            if ($scope.newMedia.type)
                $scope.leg.externalUrls.push($scope.newMedia);
            else
                alert("Errore: il tipo dell'oggetto non è un tipo valido (solo immagine o video).");

            // Reset the newMedia object
            $scope.newMedia = {type: 'image'};
        }
    };

    $scope.save = function () {
        if ($scope.leg.position > 0)
            $scope.leg.polyline = drawMapLeg.getPathPolyline();     // ottiene la polyline dal servizio
        if (checkFields()) {
            $scope.leg.geocoding = [$scope.leg.coordinates.lng, $scope.leg.coordinates.lat];        // converto le coordinate in modo che possano essere "digerite dal server"
            if ($stateParams.idLeg)
                $scope.$parent.legs[$stateParams.idLeg] = $scope.leg;
            else
                $scope.$parent.legs.push($scope.leg);

            // Back to the path
            $window.history.back();
        }
    };

    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.error','.ng-invalid');
        // Get all inputs
        if (invalidFields.length > 0 || $scope.leg.externalUrls.length === 0) {
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
            $scope.newMedia.link = response.data.link;
            // Reset the input field
            $scope.file = null;
        });
    };

    $scope.uploadFeaturedPic = function (file) {
        uploadImageOnImgur(file).success(function (response) {
            // Update the link of the new media with the imgur link
            $scope.leg.imageUrl = response.data.link;
        });
    };

    $scope.delete = function (element) {
        createDialog('templates/modals/delete-media.html',{
            id : 'delete-media-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {$scope.leg.externalUrls.splice($scope.leg.externalUrls.indexOf(element), 1);} }
        });
    };

    $scope.updateElementData = function(index, newTitle, newUrl)
    {
        $scope.leg.externalUrls[index].name = newTitle;
        $scope.leg.externalUrls[index].link = newUrl;
    }
});