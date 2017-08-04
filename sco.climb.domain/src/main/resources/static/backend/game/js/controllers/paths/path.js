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
        DataService.getData($rootScope.paths[$scope.currentPath].ownerId, $scope.$parent.selectedInstitute, 'legs',
            $scope.$parent.selectedSchool, $rootScope.paths[$scope.currentPath].pedibusGameId, $rootScope.paths[$scope.currentPath].objectId).then(
                function(response) {
                    console.log('Caricamento delle tappe a buon fine.');
                    $rootScope.legs = response.data;
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

        if (!path.name || !path.description)
            isValidate = false;
        if (!path.name || !path.description || path.name === "" || path.description === "")
                isValidate = false;

        /*if ($scope.legs.length === 0)
            isValidate = false;*/           // TODO: da decommentare una volta implementato salvataggio/caricamento legs

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

.controller('LegsListCtrl', function ($scope, $rootScope, createDialog) {
    $scope.$parent.selectedTab = 'legs';
    $scope.remove = function (idLeg) {
        createDialog('templates/modals/delete-leg.html',{
            id : 'delete-leg-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {$rootScope.paths[$scope.currentPath].legs.splice(idLeg, 1);} }
        });
    };
})
/*
.controller('MultimediaCtrl', function ($scope, $uibModal, uploadImageOnImgur) {
    $scope.$parent.selectedTab = 'multimedia';
    $scope.newMedia = {};
    // Add media to the current path
    $scope.addMedia = function () {
        switch ($scope.data.mediaType) {
        case 'image':
            $rootScope.paths[$scope.currentPath].images.push($scope.newMedia);
            break;
        case 'video':
            $rootScope.paths[$scope.currentPath].videos.push($scope.newMedia);
            break;
        case 'audio':
            $rootScope.paths[$scope.currentPath].audios.push($scope.newMedia);
            break;
        default:
            alert("Errore: il tipo dell'oggetto non è un tipo valido (solo immagine, audio o video).");
        }
        // Reset the newMedia object
        $scope.newMedia = {};
    };

    $scope.delete = function (idx, array) {
        var modalInstance = $uibModal.open({
            templateUrl: 'templates/back.html',
            controller: 'ModalCtrl',
            size: 'lg',
            resolve: {
                titleText: function () {
                    return 'Attenzione!';
                },
                bodyText: function () {
                    return 'Sei sicuro di voler cancellare questo oggetto?';
                }
            }
        });

        modalInstance.result.then(function () {
            array.splice(idx, 1);
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

    // Switch views
    $scope.copyOfImages = {};
    $scope.copyOfVideos = {};
    $scope.copyOfAudios = {};

    // orArray = original $scope array;
    // cpArray = copy of original $scope array;

    $scope.push = function (index, orArray, cpArray) {
        cpArray[index] = angular.copy(orArray[index]);
    }
    $scope.pop = function (orArray, cpArray, index, save) {
        if (save)
            orArray[index] = cpArray[index];

        cpArray[index] = null;
    }
}) NON PIU' NECESSARIO (multimedia rimossi) */

.controller('MapCtrl', function ($scope, $rootScope, $timeout, drawMap, createDialog) {
    $scope.$parent.selectedTab = 'map';
    $scope.toggle = true;
    $scope.initMap = function () {
        // Verifica che ci siano abbastanza tappe
        if($rootScope.paths[$scope.currentPath].legs.length < 2)
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
            drawMap.createMap('map', $rootScope.paths[$scope.currentPath].legs);
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