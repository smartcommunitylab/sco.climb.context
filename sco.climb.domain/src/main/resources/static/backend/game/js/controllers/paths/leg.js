angular.module('consoleControllers.leg', ['isteven-multi-select'])

// Edit the leg for the selected path
.controller('LegCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, uploadImageOnImgur, drawMapLeg, createDialog) {
    $scope.$parent.selectedTab = 'legs';
    $scope.viewIconsModels = [
        { icon: "<img src=img/POI_foot_full.png />", name: "A piedi", value:"foot", ticked: true},
        { icon: "<img src=img/POI_airplane_full.png />", name: "Aereo", value:"plane"},
        { icon: "<img src=img/POI_boat_full.png />", name: "Nave/Traghetto", value:"boat"},
        { icon: "<img src=img/POI_baloon_full.png />", name: "Mongolfiera", value:"balloon"},
        { icon: "<img src=img/POI_zeppelin_full.png />", name: "Dirigibile", value:"zeppelin"},
        { icon: "<img src=img/POI_train_full.png />", name: "Treno", value:"train"},
        { icon: "<img src=img/POI_sleigh_full.png />", name: "Slitta", value:"sled"}
    ]; 

    $scope.initController = function() {
        if ($stateParams.idLeg) { //edit path

            $scope.leg = angular.copy($scope.legs.find(function (e) { return e.objectId == $stateParams.idLeg }));
            $scope.leg.coordinates = {lat: $scope.leg.geocoding[1], lng: $scope.leg.geocoding[0]};      // trasformo le coordinate in un formato gestibile da GMaps
            $scope.saveData = DataService.editData;
            
            $scope.viewIconsModels.forEach(function(element) {
                element.ticked = (element.value == $scope.leg.icon); 
            }, this);

        } else {
            $scope.leg = {
                name: '',
                description: '',
                imageUrl: '',
                coordinates: {
                    lat: 45.8832637,
                    lng: 11.0014507
                },
                score: '',
                polyline: '',         // NEW: stringa contenente il percorso compreso tra la tappa e la sua precedente (nel 1° LEG sarà vuota, ovviamente)
                transport: 'foot',     // NEW: mezzo con cui si arriva alla tappa (foot [default], plane, boat)
                externalUrls: [],        // NEW: array di oggetti contenente gli elementi multimediali
                position: $scope.legs.length
            };
        }

        if($scope.leg.position === 0) {
            drawMapLeg.createMap('map-leg', 'geocodeHintInput', null, $scope.leg.coordinates, null, $scope.leg.transport);
        } else {
            drawMapLeg.createMap('map-leg', 'geocodeHintInput', {lat: $scope.legs[$scope.leg.position-1].geocoding[1], lng: $scope.legs[$scope.leg.position-1].geocoding[0]}, $scope.leg.coordinates, $scope.leg.additionalPoints, $scope.leg.transport);
        }
    }


    if ($scope.legs) {
        $scope.initController();
    } else {
        $scope.$on('legsLoaded', function(e) {  
            $scope.initController();        
        });
    }

    $scope.$on('poiMarkerPosChanged', function(event, newLat, newLng, wipeAirDistance) {     // listener del broadcast che indica il cambiamento della posizione del marker
        $scope.leg.coordinates.lat = newLat;
        $scope.leg.coordinates.lng = newLng;
        /*if(wipeAirDistance)
            document.getElementById('airDistance').value = '';       // pulisci la textbox per il calcolo della lunghezza della linea*/
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
    });

    $scope.$on('poiMapTotalKmChanged', function(event, newDistance) {     //total km changed listener
        $scope.leg.totalDistance = newDistance;
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
        $scope.leg.icon = undefined;
        for (var i = 0; i < $scope.viewIconsModels.length && !$scope.leg.icon; i++) { //bug in the library, no output-model present, so need to search selected item in the input-model
            if ($scope.viewIconsModels[i].ticked) $scope.leg.icon = $scope.viewIconsModels[i].value;
        }
        if ($scope.leg.position > 0) {
            $scope.leg.polyline = drawMapLeg.getPathPolyline();     // ottiene la polyline dal servizio
            $scope.leg.additionalPoints = drawMapLeg.getCustomWayPoint();
        }
        if (checkFields()) {
            $scope.leg.geocoding = [$scope.leg.coordinates.lng, $scope.leg.coordinates.lat];        // converto le coordinate in modo che possano essere "digerite dal server"
            var legBackup;
            if ($stateParams.idLeg) { //edited, have to update array
                for (var i = 0; i < $scope.legs.length; i++) {
                    if ($scope.legs[i].objectId == $stateParams.idLeg) {
                        legBackup = {
                            value: $scope.legs[i],
                            positon: i
                        }
                        $scope.legs[i] = $scope.leg;
                        break;
                    }
                }
            } else {
                $scope.legs.push($scope.leg);
            }
            $scope.currentPath.legs = $scope.legs;
            $scope.saveData('legs', $scope.currentPath).then(
                function(response) {
                    console.log('Salvataggio dati a buon fine.');
                    $state.go('root.path.legs');
                }, function() {
                    if (legBackup) {
                        $scope.legs[legBackup.position] = legBackup.value;
                    } else {
                        $scope.legs.splice($scope.legs.length-1, 1);
                    }
                    alert('Errore nel salvataggio delle tappe.');
                }
            );
        } else {
          $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco, di avere inserito almeno una foto e un punto di interesse prima di salvare.";
          $timeout(function () {
              $rootScope.modelErrors = '';
          }, 5000);
        }
    };

    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.error','.ng-invalid');
        // Get all inputs
        if (invalidFields.length > 0) {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco.";
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

    $scope.saveOrder = function() {
        if ($scope.enableOrder) {            
            $scope.currentPath.legs = $scope.legs;
            createDialog('templates/modals/leg-multimedia-order-save.html',{
                id : 'multimedia-save-order',
                title: 'Tappa da salvare!',
                success: { label: 'OK', fn: function() {} },
                noCancelBtn: true
            });
            $scope.enableOrder = false;
        } else {
            $scope.enableOrder = true;
        }
    };

    $scope.searchMultimediaOnSearchEngines = function() {

        createDialog('templates/modals/multimedia-on-search-engines.html',
            {
                id : 'search-on-search-engines-dialog',
                title: 'Cerca elemento multimediale',
                controller: 'SearchOnSearchEnginesDialogCtrl',
                scope: $scope
            }
        );
    }

});