angular.module('consoleControllers.games', ['ngSanitize'])

// Games controller
.controller('GamesListCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'game';
    if($scope.$parent.selectedOwner && $scope.$parent.selectedInstitute && $scope.$parent.selectedSchool && $scope.$parent.gamesModified)
        $scope.$parent.loadData($scope.$parent.mainView);

    $scope.delete = function (gameIndex) {
        createDialog('templates/modals/delete-game.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData($scope.$parent.mainView, $rootScope.games[gameIndex]).then(
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

.controller('GameCtrl', function ($scope, $stateParams, $rootScope, $location, $timeout, DataService, createDialog, $filter) {
    $scope.$parent.mainView = 'game';

    // Variabili per date-picker
    $scope.dateFormat = 'dd/MM/yyyy';
    $scope.startDate = new Date();
    $scope.endDate = new Date();
    $scope.isCalendarOpen = [false, false];
    $scope.minDate = new Date(1970, 1, 1);

    // Variabili per time-picker
    $scope.collectFromHour = new Date();
    $scope.collectFromHour.setSeconds(0);           // in questo modo evito che nell'input field compaiano anche i secondi e i millisecondi
    $scope.collectFromHour.setMilliseconds(0);
    $scope.collectToHour = new Date();
    $scope.collectToHour.setSeconds(0);
    $scope.collectToHour.setMilliseconds(0);

    // Variabili per selezione scuola, classi e linee corrispondenti
    $scope.selectedSchool = {};
    $scope.selectedSchoolString = '';
    $scope.classes = [];
    
    

    // Funzioni per caricamento dati di scuole e linee
    $scope.findSchoolById = function(schoolId)
    {
        var foundSchool = $rootScope.schools.find(function(element) {
            return element.localId === schoolId;
        });

        if(foundSchool === undefined)
            return '';
        else
            return foundSchool;
    };

    $scope.initializeClasses = function() {
    	DataService.getData('classes',
    			$scope.$parent.selectedOwner, 
    			$scope.$parent.selectedInstitute.objectId, 
    			$scope.$parent.selectedSchool.objectId).then(
          function(response) {
          	var classes = response.data;
          	if(classes != null) {
          		classes.forEach(function(entry) {
          			var classEntry = {};
          			classEntry.value = false;
          			classEntry.name = entry;
          			if($rootScope.games[$scope.currentGame].classRooms != null) {
          				if($rootScope.games[$scope.currentGame].classRooms.includes(entry)) {
          					classEntry.value = true;
          				}
          			}
          			$scope.classes.push(classEntry);
          		});
          	}
          }, function() {
            alert('Errore nel caricamento delle classi.');
          }
      );
    };

    if ($stateParams.idGame)        // controlla se si sta modificando un percorso esistente
    {
        $scope.currentGame = Number($stateParams.idGame);

        // Scrivo nei campi di date e ore il loro contenuto effettivo
        $scope.startDate.setTime($rootScope.games[$scope.currentGame].from);
        $scope.endDate.setTime($rootScope.games[$scope.currentGame].to);
        $scope.collectFromHour.setHours(Number($rootScope.games[$scope.currentGame].fromHour.slice(0,2)), Number($rootScope.games[$scope.currentGame].fromHour.slice(3,5)));
        $scope.collectToHour.setHours(Number($rootScope.games[$scope.currentGame].toHour.slice(0,2)), Number($rootScope.games[$scope.currentGame].toHour.slice(3,5)));

        // Caricamento del campo della scuola
        $scope.selectedSchool = $scope.findSchoolById($rootScope.games[$scope.currentGame].school);
        if($scope.selectedSchool != '') {
            $scope.selectedSchoolString = JSON.stringify($scope.selectedSchool);
        }
        $scope.initializeClasses();             
        $scope.saveData = DataService.editData;
    }
    else
    {
        $scope.currentGame = $rootScope.games.push({ // variabile che indica la posizione nell'array del path che sta venendo editato           
            gameName: '',
            gameDescription: '',
            from: '',                                           
            to: '',
            fromHour: '',
            toHour: '',
            school: '',                                         
            classRooms: [],                                     
            ownerId: $scope.$parent.selectedOwner,
            schoolId: $scope.$parent.selectedSchool.objectId,
            instituteId: $scope.$parent.selectedInstitute.objectId
        })-1;
        $scope.saveData = DataService.saveData;
    }

    $scope.parseChoice = function() {     // parsing della stringa dell'oggetto school selezionato dal dropdown
        $scope.selectedSchool = JSON.parse($scope.selectedSchoolString);
    };

    // Save the changes made to the path
    $scope.save = function () {
        if (checkFields())
        {
            // Salvataggio date in timestamp Unix (ms)
            $rootScope.games[$scope.currentGame].from = $scope.startDate.getTime();
            $rootScope.games[$scope.currentGame].to = $scope.endDate.getTime();

            // Salvataggio orari di inizio e fine raccolta dati
            $rootScope.games[$scope.currentGame].fromHour = $scope.collectFromHour.toTimeString().slice(0,5);
            $rootScope.games[$scope.currentGame].toHour = $scope.collectToHour.toTimeString().slice(0,5);
            
          	$rootScope.games[$scope.currentGame].classRooms = [];
            $scope.classes.forEach(function(entry) {
            	if(entry.value) {
            		$rootScope.games[$scope.currentGame].classRooms.push(entry.name);
            	}
            });

            $scope.saveData('game', $rootScope.games[$scope.currentGame]).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function() {
                    console.log('Salvataggio dati a buon fine.');
                    $location.path('games-list');
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        }
        else
        {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e che le date siano valide.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
        }
    };

    // Controlla se ci sono campi vuoti e se le ore e le date di fine sono precedenti o uguali a quelle di inizio comparando le stringhe
    function checkFields() {
        var isValidate = true;
        var invalidFields = $('.ng-invalid');

        if (invalidFields.length > 0 || $scope.endDate.toISOString().localeCompare($scope.startDate.toISOString(), {numeric: true}) <= 0
            || $scope.collectToHour.toTimeString().localeCompare($scope.collectFromHour.toTimeString(), {numeric: true}) <= 0)
                isValidate = false;

        return isValidate;
    }

    // Back without saving changes
    $scope.back = function() {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$location.path('games-list');} }
        });
    };
})


.controller('GameInfoCtrl', function ($scope) {
    $scope.$parent.selectedTab = 'info';
});