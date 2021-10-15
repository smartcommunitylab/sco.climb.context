angular.module('consoleControllers.games', ['ngSanitize', 'toaster', 'ngAnimate'])

    // Games controller
    .controller('GamesListCtrl', function ($scope, $rootScope, toaster, DataService, createDialog, PermissionsService) {
        $scope.$parent.mainView = 'game';
        $scope.PermissionsService = PermissionsService;

        $scope.delete = function (game) {
            createDialog('templates/modals/delete-game.html', {
                id: 'delete-dialog',
                title: 'Attenzione!',
                success: {
                    label: 'Conferma', fn: function () {
                        DataService.removeData('game', game).then(
                            function (result) {
                                console.log("Rimozione gioco effettuata con successo.");
                                $scope.games.splice($scope.games.indexOf(game), 1);
                                toaster.pop('toaster', "Cancellato", "Rimozione gioco effettuata con successo");
                            }, function (error) {
                                alert("Errore nella richiesta:" + error.data.errorMsg);
                            });
                    }
                }
            });
        };

        $scope.reset = function (game) {
            createDialog('templates/modals/reset-game.html', {
                id: 'delete-dialog',
                title: 'Attenzione!',
                success: {
                    label: 'Conferma', fn: function () {
                        DataService.resetGame(game.ownerId, game.objectId).then(
                            function (response) {
                                var gameUpdated = response.data;
                                for (var i = 0; i < $scope.games.length; i++) {
                                    if ($scope.games[i].objectId == gameUpdated.objectId) {
                                        $scope.games[i] = gameUpdated;
                                    }
                                }
                                alert("reset gioco effettuato con successo.");
                            }, function (error) {
                                alert("Errore nella richiesta:" + error.data.errorMsg);
                            });
                    }
                }
            });
        };

        $scope.initGameOnServer = function (game) {
            createDialog('templates/modals/init-game-confirmation.html', {
                id: 'init-game-confirmation-dialog',
                title: 'Inizializzare gioco?',
                success: {
                    label: 'Conferma', fn: function () {
                        DataService.initGameCall(game.ownerId, game.objectId).then(
                            function (response) {
                                var gameUpdated = response.data;
                                for (var i = 0; i < $scope.games.length; i++) {
                                    if ($scope.games[i].objectId == gameUpdated.objectId) {
                                        $scope.games[i] = gameUpdated;
                                    }
                                }
                                alert("Init game riuscito!");
                            }, function (error) {
                                alert("Errore nella richiesta:" + error.data.errorMsg);
                            }
                        );
                    }
                }
            });

        }
    })

    .controller('GameCtrl', function ($scope, $stateParams, $state, $rootScope, $timeout, DataService, createDialog, $filter) {
        $scope.$parent.mainView = 'game';

        // Variabili per date-picker
        $scope.dateFormat = 'dd/MM/yyyy';
        $scope.startDate = new Date();
        $scope.endDate = new Date();
        $scope.isCalendarOpen = [false, false];
        $scope.minDate = new Date(1970, 1, 1);
        $scope.players = [];
        // Variabili per time-picker
        $scope.collectFromHour = new Date();
        $scope.collectFromHour.setSeconds(0);           // in questo modo evito che nell'input field compaiano anche i secondi e i millisecondi
        $scope.collectFromHour.setMilliseconds(0);
        $scope.collectToHour = new Date();
        $scope.collectToHour.setSeconds(0);
        $scope.collectToHour.setMilliseconds(0);
        // $scope.paramsChanged = false;
        // $scope.calibrationChanged = false;
        // Variabili per selezione scuola, classi e linee corrispondenti
        $scope.classes = [];
        $scope.modalities = [];

        $scope.manageResponse = function (response) {
            console.log('Salvataggio dati a buon fine.');
            createDialog('templates/modals/save.html', {
                id: 'save-dialog',
                title: 'Modificato!',
                success: { label: 'Ok', fn: null }
            });
            $rootScope.modified = false;
            //show toast salvataggio
            if ($scope.currentGame.objectId) { //edited
                if ($scope.games) {
                    for (var i = 0; i < $scope.games.length; i++) {
                        if ($scope.games[i].objectId == $scope.currentGame.objectId) {
                            $scope.games[i] = $scope.currentGame;
                        }
                    }
                }
            } else {
                $scope.currentGame.objectId = response.data.objectId;
                if ($scope.games) $scope.games.push(response.data);
                $scope.saveData = DataService.editData;

            }
            //$state.go('root.games-list');
        }
        $scope.initController = function () {
            $scope.weekDays = ["Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato", "Domenica"]
            if ($scope.currentGame) { //edit game
                $scope.editGame = true;
                if ($scope.currentGame.daysOfWeek.length == 0) {
                    $scope.currentGame.daysOfWeek = [1, 1, 1, 1, 1, 0, 0]
                }
                $scope.saveData = DataService.editData;
                // if ($scope.currentGame.usingPedibusData) {
                $scope.startDate.setTime($scope.currentGame.from);
                $scope.endDate.setTime($scope.currentGame.to);
                $scope.collectFromHour.setHours(Number($scope.currentGame.fromHour.slice(0, 2)), Number($scope.currentGame.fromHour.slice(3, 5)));
                $scope.collectToHour.setHours(Number($scope.currentGame.toHour.slice(0, 2)), Number($scope.currentGame.toHour.slice(3, 5)));
                // }
                if ($scope.currentGame.interval == 0) {
                    $scope.currentGame.interval = 5;
                }
            } else {
                $scope.editGame = false;
                $scope.currentGame = {
                    gameName: '',
                    gameDescription: '',
                    from: '',
                    to: '',
                    fromHour: '',
                    toHour: '',
                    school: '',
                    classRooms: [],
                    globalTeam: 'Scuola',
                    shortName: getShortName(),
                    ownerId: $stateParams.idDomain,
                    schoolId: $stateParams.idSchool,
                    instituteId: $stateParams.idInstitute,
                    usingPedibusData: false,
                    interval: 5
                }
                $scope.currentGame.daysOfWeek = [1, 1, 1, 1, 1, 0, 0]
                $scope.saveData = DataService.saveData;
            }
            $scope.classes = $scope.currentGame.classRooms.filter(function (el) {
                return el != null;
            });

            $scope.$broadcast('gameLoaded');
            var classes = []
            $scope.currentGame.classRooms.forEach(function (cl) {
                if (cl)
                    classes.push(cl);
            })
            DataService.getStudentsByGame($scope.currentGame, classes).then(
                function (response) {
                    $scope.players = response.data;
                }
            )
            DataService.getModalityMap().then(
                function (response) {
                    if (response.data) {
                        response.data.modalities.forEach(function (entry) {
                            var modalityEntry = {
                                selected: false,
                                value: entry.value,
                                active: entry.active,
                                label: entry.label,
                                category: entry.category,
                                color: entry.color
                            }
                            if ($scope.currentGame.modalities && $scope.currentGame.modalities.includes(entry.value) || entry.value == 'absent') {
                                modalityEntry.selected = true;
                            }
                            $scope.modalities.push(modalityEntry);
                        });
                    }
                },
                function (error) {
                    alert('Errore nel caricamento delle modalità:' + error.data.errorMsg);
                }
            );
        }
        $scope.toggleSelectionWeekDay = function toggleSelectionWeekDay(index) {
            if ($scope.currentGame.daysOfWeek[index]) {
                $scope.currentGame.daysOfWeek[index] = 0;
            } else {
                $scope.currentGame.daysOfWeek[index] = 1;
            }
        }
        if ($stateParams.idGame) {
            if (!$scope.currentGame) {
                DataService.getGameById(
                    $stateParams.idDomain,
                    $stateParams.idGame).then(
                        function (response) {
                            $scope.currentGame = response.data;
                            $scope.initController();
                        }, function (error) {
                            alert('Errore nel caricamento delle classi:' + error.data.errorMsg);
                        }
                    );
            } else {
                $scope.initController();
            }
        } else { //new game
            $scope.initController();
        }
        // get map for class
        //Map<String, Map<String, Integer>> 
        $scope.getParamsGame = function(game){
            return game.mobilityParams;
        }
        // Save the changes made to the path
        $scope.save = function () {

            if (checkFields()) {
                // Salvataggio date in timestamp Unix (ms)
                $scope.startDate.setHours(0, 0, 0, 0);
                $scope.endDate.setHours(23, 59, 59, 999);
                $scope.currentGame.from = $scope.startDate.getTime();
                $scope.currentGame.to = $scope.endDate.getTime();

                // Salvataggio orari di inizio e fine raccolta dati
                if ($scope.currentGame.usingPedibusData) {
                    $scope.currentGame.fromHour = $scope.collectFromHour.toTimeString().slice(0, 5);
                    $scope.currentGame.toHour = $scope.collectToHour.toTimeString().slice(0, 5);
                }

                $scope.currentGame.classRooms = [];
                $scope.classes.forEach(function (entry) {
                    // if (entry.value) {
                    $scope.currentGame.classRooms.push(entry);
                    // }
                });

                $scope.currentGame.modalities = [];
                $scope.modalities.forEach(function (entry) {
                    if (entry.selected) {
                        $scope.currentGame.modalities.push(entry.value);
                    }
                });

                if ($scope.selectedTab == 'params') {
                    DataService.updateParams($scope.currentGame.ownerId ,$scope.currentGame.objectId, $scope.getParamsGame($scope.currentGame)).then(
                        function (response) {
                            $scope.manageResponse(response);
                        })
                } else if ($scope.selectedTab == 'calibration') {
                    DataService.updateCalibrazioni($scope.currentGame).then(
                        function (response) {
                            $scope.manageResponse(response);
                        })
                } else if ($scope.selectedTab == 'gamers') {
                    DataService.updatePlayers($scope.currentGame, $scope.players).then(
                        function (response) {
                            $scope.manageResponse(response);
                        })
                } else
                    $scope.saveData('game', $scope.currentGame).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                        function (response) {
                            $scope.manageResponse(response);

                        }, function (error) {
                            alert("Errore nella richiesta:" + error.data.errorMsg);
                        }
                    );
            }
            else {
                $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e che le date siano valide.";
                $timeout(function () {
                    $rootScope.modelErrors = '';
                }, 5000);
            }
        };

        // Controlla se ci sono campi vuoti e se le ore e le date di fine sono precedenti o uguali a quelle di inizio comparando le stringhe
        function checkFields() {
            var regExp = new RegExp(/\W/);
            if (regExp.test($scope.currentGame.globalTeam)) {
                alert("nel nome del team sono consentiti solo i seguenti caratteri: A-Z, a-z, 0-9, _");
                return false;
            }

            var isValidate = true;
            var invalidFields = $('.ng-invalid');


            for (var p in $scope.currentGame.params) {
                if ($scope.currentGame.params[p] < 0) {
                    isValidate = false;
                }
            }

            if ($scope.currentGame.usingPedibusData) {
                if ($scope.collectToHour.toTimeString().localeCompare($scope.collectFromHour.toTimeString(), { numeric: true }) <= 0) {
                    isValidate = false;
                }
                if (($scope.currentGame.interval <= 0) || ($scope.currentGame.interval > 30)) {
                    isValidate = false;
                }
            }

            if (invalidFields.length > 0 || $scope.endDate.toISOString().localeCompare($scope.startDate.toISOString(), { numeric: true }) <= 0)
                isValidate = false;
            if (!$scope.classes || $scope.classes.length == 0)
                isValidate = false;
            return isValidate;
        }

        function getShortName() {
            var d = new Date()
            var year = d.getFullYear();
            if ($scope.$parent.selectedSchool) {
                var name = $scope.$parent.selectedSchool.name.replace(/[^a-zA-Z0-9]/g, "_");
                return (name + '_' + year + '_' + (Math.floor(Math.random() * 90000) + 10000));
            }
            return ('Scuola ' + year);
        }

        // Back without saving changes
        $scope.back = function () {
            createDialog('templates/modals/back.html', {
                id: 'back-dialog',
                title: 'Sei sicuro di voler uscire senza salvare?',
                success: {
                    label: 'Conferma', fn: function () {
                        $rootScope.modified = false;
                        $state.go('root.games-list');
                    }
                }
            });
        };
    })


    .controller('GameInfoCtrl', function ($scope,createDialog, MainDataService,DataService) {
        $scope.$parent.selectedTab = 'info';
        $scope.new = {
            classe: ""
        }
        $scope.$on('gameLoaded', function (e) {
            // Variabili per date-picker(fix for refresh issue on parametri page)
            $scope.$parent.dateFormat = 'dd/MM/yyyy';
            $scope.$parent.startDate = new Date();
            $scope.$parent.endDate = new Date();
            $scope.$parent.isCalendarOpen = [false, false];
            $scope.$parent.minDate = new Date(1970, 1, 1);
            if ($scope.currentGame.from) {
                $scope.$parent.startDate.setTime($scope.currentGame.from);
            }
            if ($scope.currentGame.to) {
                $scope.$parent.endDate.setTime($scope.currentGame.to);
            }
            $scope.classToggled();
            $scope.$parent.collectFromHour = new Date();
            $scope.$parent.collectFromHour.setSeconds(0);
            $scope.$parent.collectFromHour.setMilliseconds(0);
            if ($scope.currentGame.fromHour) {
                $scope.$parent.collectFromHour.setHours(Number($scope.currentGame.fromHour.slice(0, 2)), Number($scope.currentGame.fromHour.slice(3, 5)));
            }
            $scope.$parent.collectToHour = new Date();
            $scope.$parent.collectToHour.setSeconds(0);
            $scope.$parent.collectToHour.setMilliseconds(0);
            if ($scope.currentGame.toHour) {
                $scope.$parent.collectToHour.setHours(Number($scope.currentGame.toHour.slice(0, 2)), Number($scope.currentGame.toHour.slice(3, 5)));
            }
        });

        $scope.toggleSelectedClasses = function () {
            $scope.$parent.classes.forEach(function (currentClass) {
                currentClass.value = $scope.classesAllSelected;
            });
            $scope.classToggled();
        }
        $scope.addClasse = function (nomeClasse) {
            //check if there is a path for this game
            if ($scope.currentGame && $scope.currentGame.objectId){
            MainDataService.getItineraries($scope.currentGame.objectId).then(function (response) {
                var paths = response.data;
                if (paths && paths.length >0) {
                    createDialog('templates/modals/add-class-to-game.html',
                    {
                        id: 'add-class-to-game-dialog',
                        title: 'Attenzione!',
                        success: {
                            label: "Ok",
                            fn: null
        
                            }
                        }
                    )
                }
            });

            }
            if (nomeClasse) {
                //feedback and clean
                $scope.$parent.classes.push(nomeClasse);
                $scope.new.classe = "";
                $scope.switchAddClasse();
            }
            else {
                //errore
            }
        }
        $scope.switchAddClasse = function () {
            $scope.addClass = !$scope.addClass;
        }
        var deleteClass = function(name) {
            if (name) {
                $scope.selectedClasses = [];
                /*            $scope.classesAllSelected = $scope.$parent.classes.every(function (cl) {
                                return cl.name;
                            })*/

                $scope.$parent.classes.forEach(function (cl) {
                    if (cl != name) {
                        $scope.selectedClasses.push(cl);
                    }
                })
                $scope.$parent.classes = $scope.selectedClasses;
            }
        }
        $scope.classToggled = function (name) {
            //se modifico => modale
            if (name){
            if ($scope.editGame) {
                createDialog('templates/modals/delete-class-warning.html',
                    {
                        id: 'delete-class-warning-dialog',
                        title: 'Attenzione!',
                        success: {
                            label: "Conferma",
                            fn: function () {
                                deleteClass(name);

                            },
                            cancel: {
                                label: "Chiudi",
                                fn: null
                            }
                        }
                    })
            }
            else {
                deleteClass(name);

            }
        }
/*            $scope.calculateStudenti($scope.selectedClasses);
*/        }



        // refresh of checkbox while switching between views
        if ($scope.currentGame) {
            $scope.classToggled();
        }
    })

//params tab ha all the parameters: number of students using different means
    .controller('GameParamsCtrl', function ($scope, DataService, createDialog) {
        $scope.$parent.selectedTab = 'params';
        $scope.nrOfStudenti = {};

        $scope.initParamController = function () {
            if ((!$scope.currentGame.mobilityParams) || (Object.keys($scope.currentGame.mobilityParams).length === 0)) {
                //for every class
                $scope.currentGame.mobilityParams = {};
                if ($scope.currentGame.classRooms.length>0)
                    {
                    $scope.classesArePresent=true;
                    $scope.currentGame.classRooms.forEach(classRoom => {
                    $scope.currentGame.mobilityParams[classRoom] = {
                        walk_studenti: 0,
                        bike_studenti: 0,
                        bus_studenti: 0,
                        pedibus_studenti: 0,
                        pandr_studenti: 0,
                        carpooling_studenti: 0,
                        car_studenti: 0
                    }
                })}
                else {
                    $scope.classesArePresent=false;
                }
                
            } else {
                // typecast params.
                if ($scope.currentGame.classRooms.length>0)

                {$scope.classesArePresent=true;
                    $scope.currentGame.classRooms.forEach(classRoom => {
                    for (var p in $scope.currentGame.mobilityParams[classRoom]) {
                        $scope.currentGame.mobilityParams[classRoom][p] = parseFloat($scope.currentGame.mobilityParams[classRoom][p]);
                    }
                })}
                else {
                    $scope.classesArePresent=false;
                }

            }
            
        }
        $scope.calculateStudenti = function (classes) {
            //call api and calculate nr.of studenti.
            var promises=[]
            classes.forEach(function(classRoom){
                promises.push(DataService.getStudentsByClasses($scope.currentGame.ownerId, $scope.currentGame.objectId, [classRoom]).then(function (data) {
                    $scope.nrOfStudenti[classRoom] = data.data;
                }))
            })
            
            Promise.all(promises).then((values) => {
                console.log(values);
              });

        }
        $scope.$on('gameLoaded', function (e) {
            // Variabili per date-picker(fix for refresh issue on parametri page)
            $scope.$parent.dateFormat = 'dd/MM/yyyy';
            $scope.$parent.startDate = new Date();
            $scope.$parent.endDate = new Date();
            $scope.$parent.isCalendarOpen = [false, false];
            $scope.$parent.minDate = new Date(1970, 1, 1);
            if ($scope.currentGame.from) {
                $scope.$parent.startDate.setTime($scope.currentGame.from);
            }
            if ($scope.currentGame.to) {
                $scope.$parent.endDate.setTime($scope.currentGame.to);
            }
            $scope.calculateStudenti($scope.currentGame.classRooms);
            $scope.initParamController();
        });

        $scope.calculateBonusAutonomia = function() {
               	$scope.currentGame.params.const_pandr_distance = $scope.currentGame.params.parcheggio_attestamento_distanza;
               	$scope.currentGame.params.const_bus_distance = $scope.currentGame.params.scuolabus_o_autobus_distanza;
               	$scope.currentGame.params.const_zeroimpact_distance = $scope.currentGame.params.piedi_o_bici_con_adulti_distanza;
               	$scope.currentGame.params.const_zi_solo_bonus = $scope.currentGame.params.piedi_o_bici_in_autonomia_distanza - $scope.currentGame.params.piedi_o_bici_con_adulti_distanza;
               	return $scope.currentGame.params.const_zi_solo_bonus;
               }

        

        $scope.calculateSS = function (classRoom) {
            if ($scope.currentGame && $scope.currentGame.mobilityParams && $scope.currentGame.classRooms) {
                var ss=0
            // $scope.currentGame.classRooms.forEach(classRoom => {
                    ss+=($scope.currentGame.mobilityParams[classRoom].walk_studenti?$scope.currentGame.mobilityParams[classRoom].walk_studenti:0) +
                    ($scope.currentGame.mobilityParams[classRoom].bike_studenti?$scope.currentGame.mobilityParams[classRoom].bike_studenti:0) +
                    ($scope.currentGame.mobilityParams[classRoom].bus_studenti?$scope.currentGame.mobilityParams[classRoom].bus_studenti:0) +
                    ($scope.currentGame.mobilityParams[classRoom].pedibus_studenti?$scope.currentGame.mobilityParams[classRoom].pedibus_studenti:0) +
                    ($scope.currentGame.mobilityParams[classRoom].pandr_studenti?$scope.currentGame.mobilityParams[classRoom].pandr_studenti:0) +
                    ($scope.currentGame.mobilityParams[classRoom].carpooling_studenti?$scope.currentGame.mobilityParams[classRoom].carpooling_studenti:0) +
                    ($scope.currentGame.mobilityParams[classRoom].car_studenti?$scope.currentGame.mobilityParams[classRoom].car_studenti:0)
                // })
                return ss;
            }
        }


        $scope.isModalityPresent = function (mode) {
            if ($scope.currentGame && $scope.currentGame.modalities)
                return $scope.currentGame.modalities.includes(mode);
            return false
        }

        if ($scope.currentGame) {
            $scope.initParamController();
            $scope.calculateStudenti($scope.currentGame.classRooms);
        }



    })
    .controller('GameGamersCtrl', function ($scope, $stateParams, $rootScope, createDialog, DataService) {
        $scope.$parent.selectedTab = 'gamers';
        //get players
        $scope.isEditEnabledArray = [];
        $scope.classes = $scope.$parent.classes;
        $scope.players = $scope.$parent.players;
        $scope.gamers = {
            currentGame: null
        }
        $scope.selectedPlayer = {
        		nickname: null,
        		classRoom: null
        }
        $scope.initController = function () {
            DataService.getStudentsByGame(
                $scope.$parent.currentGame, $scope.$parent.classes).then(
                    function (response) {
                        $scope.$parent.players = response.data;
                        $scope.players = $scope.$parent.players;
                        for (var i = 0; i < $scope.players.length; i++) {
                            $scope.isEditEnabledArray.push(false);
                        }
                        console.log('Caricamento dei giocatori andato a buon fine.');
                    }, function () {
                        $rootScope.networkProblemDetected('Errore nel caricamento dei giocatori!');
                        console.log("Errore nel caricamento dei giocatori.");
                    }
                );
        }

        $scope.addNewPlayer = function () {
            if ($scope.selectedPlayer) {
                let obj = Object.assign({}, $scope.selectedPlayer);
                $scope.$parent.players.push(obj);
                $scope.selectedPlayer.nickname = null;
                $scope.changed();
            }
            else {
                //errore
                alert("nome player vuoto");
            }
        }
        
        $scope.changeCurrentClass = function () {
        	$scope.selectedPlayer.classRoom = $scope.gamers.currentClass
        }
        
        $scope.isNewPlayerCompleted = function () {
        	var result = false;
        	if ($scope.selectedPlayer) {
        		if($scope.selectedPlayer.nickname && $scope.selectedPlayer.classRoom) {
        			result = true;
        		}
        	}
        	return result;
        }
        
        $scope.switchAddPlayer = function () {
            $scope.isAddPlayer = !$scope.isAddPlayer;
        }
        $scope.isEditEnabled = function (player) {
        	var index = getPlayerIndex(player);
          return $scope.isEditEnabledArray[index];
        }
        $scope.enableEdit = function (player) {
        	var index = getPlayerIndex(player);
          $scope.isEditEnabledArray[index] = !$scope.isEditEnabledArray[index];
        }
        $scope.saveEdit = function (player) {
        	var index = getPlayerIndex(player);
          $scope.isEditEnabledArray[index] = !$scope.isEditEnabledArray[index];
        }
        $scope.remove = function (player) {
        		var index = getPlayerIndex(player);
        		if(index >= 0) {
              createDialog('templates/modals/delete-player.html', {
                id: 'delete-dialog',
                title: 'Attenzione!',
                success: {
                	label: 'Conferma', fn: function () {
                		$scope.$parent.players.splice(index, 1);
                		$scope.players = $scope.$parent.players;
                		$scope.changed();
                  }
                }
              });
        		}
        }

        // if ($scope.currentSchool) {
        if (!$scope.$parent.currentGame)
            $scope.$on('gameLoaded', function (e) {
                $scope.initController();
            });
        else {
            $scope.initController();
        }
        //$scope.initController();
        // } else {
        //     $scope.$on('schoolLoaded', function(e) {  
        //     $scope.initController();        
        // });
        // }
        $scope.selectClass = function (classe) {
            console.log(classe);
        }
        
        $scope.readTextFile = function (file) {
        	var fileInput = document.getElementById('upload-file');
        	var reader = new FileReader();
        	reader.onload = function() {
        		var text = reader.result;
        		var list = text.split(/\r\n|\r|\n/);
        		for (var i = 0; i < list.length; i++) {
        			var nick = list[i].trim();
        			if(nick) {
                var player = {
                		nickname: list[i],
                		classRoom: $scope.gamers.currentClass
                }
                $scope.$parent.players.push(player);
                $scope.changed();        				
        			}
        		}
        		$scope.players = $scope.$parent.players;
        		$scope.$apply();
        	};
        	reader.readAsText(fileInput.files[0]);
        }
        
        function getPlayerIndex(player) {
        	for (var i = 0; i < $scope.players.length; i++) {
        		if(($scope.players[i].nickname === player.nickname) && ($scope.players[i].classRoom === player.classRoom)) {
        			return i;
        		}
        	}
        	return -1;
        }
    })

    //calibration has all params for calibrate: distance, bonus and so on
    .controller('GameCalibrationCtrl', function ($scope) {
        $scope.$parent.selectedTab = 'calibration';

        $scope.initParamController = function () {
            if (($scope.currentGame && !$scope.currentGame.params) || (Object.keys($scope.currentGame.params).length === 0)) {
                $scope.currentGame.params = {
                    const_walk_distance: 0,
                    const_bike_distance: 0,
                    const_bus_distance: 0,
                    const_pedibus_distance: 0,
                    const_pandr_distance: 0,
                    const_carpooling_distance: 0,
                    // car_studenti: 0,
                    const_car_distance: 0,
                    const_daily_nominal_distance: 0,
                    const_zeroimpact_distance: 0,
                    giorni_chiusi: 0,
                    const_cloudy_bonus: 0,
                    const_rain_bonus: 0,
                    const_snow_bonus: 0,
                    const_ZeroImpactDayClass_bonus: 0,
                    const_NoCarDayClass_bonus: 0,
                    km_bonus: 0
                }
            } else {
                // typecast params.
                for (var p in $scope.currentGame.params) {
                    $scope.currentGame.params[p] = parseFloat($scope.currentGame.params[p]);
                }
                if ($scope.isModalityPresent('car'))
                {
                    $scope.currentGame.params['const_car_distance']=0;
                } 
                else {
                    $scope.currentGame.params['const_car_distance']=undefined;
                }
            }
            $scope.calculateCDND();

        }
        $scope.calculateKMStimati = function () {
            if ($scope.currentGame && $scope.currentGame.params) {
                // calcuate actual days.
                actualDays = $scope.getNumWorkDays($scope.currentGame.from, $scope.currentGame.to, $scope.currentGame.daysOfWeek);
                actualDays = actualDays - ($scope.currentGame.params.giorni_chiusi?$scope.currentGame.params.giorni_chiusi:0);
                $scope.kmStimati = ($scope.currentGame.params.const_daily_nominal_distance / 1000) * actualDays;
                $scope.calculateKMTarget();
                return;
            }
            $scope.$scope.kmStimati=0;
            return;

        }
        $scope.calculateCDND = function () {
            if ($scope.currentGame && $scope.currentGame.params) {
                $scope.currentGame.params.const_daily_nominal_distance = 0;
                $scope.currentGame.classRooms.forEach(classRoom => {
                    if ($scope.currentGame.mobilityParams && $scope.currentGame.mobilityParams[classRoom])
                    $scope.currentGame.params.const_daily_nominal_distance+=(
                    (($scope.currentGame.mobilityParams[classRoom].walk_studenti&&$scope.currentGame.params.const_walk_distance)?($scope.currentGame.mobilityParams[classRoom].walk_studenti * $scope.currentGame.params.const_walk_distance):0) +
                    (($scope.currentGame.mobilityParams[classRoom].bike_studenti&&$scope.currentGame.params.const_bike_distance)?($scope.currentGame.mobilityParams[classRoom].bike_studenti * $scope.currentGame.params.const_bike_distance):0) +
                    (($scope.currentGame.mobilityParams[classRoom].pedibus_studenti&&$scope.currentGame.params.const_pedibus_distance)?($scope.currentGame.mobilityParams[classRoom].pedibus_studenti * $scope.currentGame.params.const_pedibus_distance):0) +
                    (($scope.currentGame.mobilityParams[classRoom].bus_studenti&&$scope.currentGame.params.const_bus_distance)?($scope.currentGame.mobilityParams[classRoom].bus_studenti * $scope.currentGame.params.const_bus_distance):0) +
                    (($scope.currentGame.mobilityParams[classRoom].pandr_studenti&&$scope.currentGame.params.const_pandr_distance)?($scope.currentGame.mobilityParams[classRoom].pandr_studenti * $scope.currentGame.params.const_pandr_distance):0) +
                    (($scope.currentGame.mobilityParams[classRoom].carpooling_studenti&&$scope.currentGame.params.const_carpooling_distance)?($scope.currentGame.mobilityParams[classRoom].carpooling_studenti * $scope.currentGame.params.const_carpooling_distance):0) +
                    (($scope.currentGame.mobilityParams[classRoom].car_studenti&&$scope.currentGame.params.const_car_distance)?($scope.currentGame.mobilityParams[classRoom].car_studenti * $scope.currentGame.params.const_car_distance):0))
                
            })
            if($scope.currentGame.roundTrip) {
                $scope.currentGame.params.const_daily_nominal_distance = $scope.currentGame.params.const_daily_nominal_distance * 2;
            }
        $scope.cdnd=$scope.currentGame.params.const_daily_nominal_distance;
            $scope.calculateKMStimati();
            $scope.calculateKMTarget();
                return;
            }
            $scope.cdnd=0;
            $scope.calculateKMStimati();
            $scope.calculateKMTarget();
            return;
        };
        
        $scope.calculateKMTarget = function () {
            if ($scope.currentGame && $scope.currentGame.params) {
                var km = $scope.kmStimati;
                if ($scope.currentGame.params.km_bonus) 
                km+=Number($scope.currentGame.params.km_bonus);
                $scope.kmTarget=km;
                return;  
            }
            $scope.kmTarget=0;
             return;
        }

        $scope.isModalityPresent = function (mode) {
            if ($scope.currentGame && $scope.currentGame.modalities)
                return $scope.currentGame.modalities.includes(mode);
            return false
        }
        
        $scope.getNumWorkDays = function (startTS, endTS, daysOfWeek) {
            var numWorkDays = 0;
            var currentDate = new Date(startTS);
            var endDate = new Date(endTS)
            while (currentDate <= endDate) {
              // check week days
            	if(currentDate.getDay() == 1 && daysOfWeek[0]) {
            		numWorkDays++;
            	}
            	if(currentDate.getDay() == 2 && daysOfWeek[1]) {
            		numWorkDays++;
            	}
            	if(currentDate.getDay() == 3 && daysOfWeek[2]) {
            		numWorkDays++;
            	}
            	if(currentDate.getDay() == 4 && daysOfWeek[3]) {
            		numWorkDays++;
            	}
            	if(currentDate.getDay() == 5 && daysOfWeek[4]) {
            		numWorkDays++;
            	}
            	if(currentDate.getDay() == 6 && daysOfWeek[5]) {
            		numWorkDays++;
            	}
            	if(currentDate.getDay() == 0 && daysOfWeek[6]) {
            		numWorkDays++;
            	}
            	currentDate = new Date(currentDate.setTime(currentDate.getTime() + 1 * 86400000));
            }
            return numWorkDays;
        }
        
        $scope.$on('gameLoaded', function (e) {
            $scope.initParamController();
        });
        if ($scope.currentGame) {
            $scope.initParamController();
        }
    })
    .controller('GamePedibusCtrl', function ($scope) {
        $scope.$parent.selectedTab = 'pedibus';


    })
    .controller('PlayerCtrl', function ($scope, $stateParams, $state, $rootScope, $timeout, $window, createDialog, DataService) {
        $scope.$parent.selectedTab = 'players-list';



        $scope.isNewPlayer = function () {
            return ($stateParams.idPlayer == null || $stateParams.idPlayer == '');
        }

        // Exit without saving changes
        $scope.back = function () {
            createDialog('templates/modals/back.html', {
                id: 'back-dialog',
                title: 'Sei sicuro di voler uscire senza salvare?',
                success: { label: 'Conferma', fn: function () { $state.go('root.game.gamers'); } }
            });
        }

        $scope.save = function () {
            if (checkFields()) {
                DataService.createPlayer($scope.currentGame.ownerId, $scope.currentGame.objectId, $scope.selectedPlayer).then(
                    function (response) {
                        console.log('Giocatore salvato.');
                        $rootScope.modified = false;
                        $state.go('root.game.gamers');
                        //update gamers
                    },
                    function (err) {
                        $rootScope.networkProblemDetected('Errore nel salvataggio del giocatore! ' + err.data.errorMsg);
                        console.log("Errore nel salvataggio del giocatore.");
                    }
                );
            }
        }

        function checkFields() {
            var allCompiled = true;
            var invalidFields = $('.ng-invalid');
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

    });
