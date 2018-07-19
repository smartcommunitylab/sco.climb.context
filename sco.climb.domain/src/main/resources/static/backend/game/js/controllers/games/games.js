angular.module('consoleControllers.games', ['ngSanitize'])

    // Games controller
    .controller('GamesListCtrl', function ($scope, $rootScope, DataService, MainDataService, createDialog, PermissionsService) {
        $scope.$parent.mainView = 'game';
        $scope.PermissionsService = PermissionsService;

        $scope.delete = function (game) {
            createDialog('templates/modals/delete-game.html', {
                id: 'delete-dialog',
                title: 'Attenzione!',
                success: {
                    label: 'Conferma', fn: function () {
                        DataService.removeData('game', game).then(
                            function () {
                                console.log("Rimozione gioco effettuata con successo.");
                                $scope.games.splice($scope.games.indexOf(game), 1);
                            }, function () {
                                alert("Errore nella richiesta.");
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
                        DataService.initGameCall(MainDataService.getSelectedDomain(), game.objectId).then(
                            function (response) {
                                alert("Init game riuscito!");
                            }, function () {
                                alert('Errore in init game!');
                            }
                        );
                    }
                }
            });

        }
    })

    .controller('GameCtrl', function ($scope, $stateParams, $state, $rootScope, $timeout, DataService, MainDataService, createDialog, $filter) {
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
        $scope.classes = [];


        $scope.initController = function () {
            if ($scope.currentGame) { //edit game
                $scope.saveData = DataService.editData;

                $scope.startDate.setTime($scope.currentGame.from);
                $scope.endDate.setTime($scope.currentGame.to);
                $scope.collectFromHour.setHours(Number($scope.currentGame.fromHour.slice(0, 2)), Number($scope.currentGame.fromHour.slice(3, 5)));
                $scope.collectToHour.setHours(Number($scope.currentGame.toHour.slice(0, 2)), Number($scope.currentGame.toHour.slice(3, 5)));
            } else {
                $scope.currentGame = {
                    gameName: '',
                    gameDescription: '',
                    from: '',
                    to: '',
                    fromHour: '',
                    toHour: '',
                    school: '',
                    classRooms: [],
                    ownerId: $stateParams.idDomain,
                    schoolId: $stateParams.idSchool,
                    instituteId: $stateParams.idInstitute,
                }
                $scope.saveData = DataService.saveData;
            }
            DataService.getData('classes',
                $stateParams.idDomain,
                $stateParams.idInstitute,
                $stateParams.idSchool).then(
                    function (response) {
                        var classes = response.data;
                        if (classes) {
                            classes.forEach(function (entry) {
                                var classEntry = {
                                    value: false,
                                    name: entry
                                };
                                if ($scope.currentGame.classRooms && $scope.currentGame.classRooms.includes(entry)) {
                                    classEntry.value = true;
                                }
                                $scope.classes.push(classEntry);
                            });
                        }
                        $scope.$broadcast('gameLoaded');
                    }, function () {
                        alert('Errore nel caricamento delle classi.');
                    }
                );

            $scope.currentGame.params = {
                piedi_o_bici_in_autonomia_studenti: 0,
                piedi_o_bici_in_autonomia_distanza: 0,
                piedi_o_bici_con_adulti_studenti: 0,
                piedi_o_bici_con_adulti_distanza: 0,
                scuolabus_o_autobus_studenti: 0,
                scuolabus_o_autobus_distanza: 0,
                parcheggio_attestamento_studenti: 0,
                parcheggio_attestamento_distanza: 0,
                auto_fine_a_scuola_studenti: 0,
                auto_fine_a_scuola_distanza: 0,
                const_daily_nominal_distance: 0,
                const_zi_solo_bonus: 0,
                const_cloudy_bonus: 0,
                const_rain_bonus: 0,
                const_snow_bonus: 0,
                const_ZeroImpactDayClass_bonus: 0,
                const_NoCarDayClass_bonus: 0,
                km_bonus: 0
            }
            // read params of pedibus game configuration
            DataService.getGameConfData('gameconfigbyid', { "ownerId": $stateParams.idDomain, "confId": $stateParams.idGame }).then(
                function (response) {
                    console.log('Caricamento della config specifica andata a buon fine.');
                    var specificConfig = response.data;
                    if (specificConfig.params) {
                        $scope.currentGame.params = specificConfig.params;
                    }
                }, function () {
                    alert('Errore nel caricamento della config specifica.');
                }
            );

        }

        if ($stateParams.idGame) {
            MainDataService.getDomains().then(function (response) {
                MainDataService.getInstitutes($stateParams.idDomain).then(function (response) {
                    MainDataService.getSchools($stateParams.idInstitute).then(function (response) {
                        MainDataService.getGames($stateParams.idSchool).then(function (response) {
                            $scope.games = response.data;
                            $scope.currentGame = angular.copy($scope.games.find(function (e) { return e.objectId == $stateParams.idGame }));
                            $scope.initController();
                        });
                    });
                });
            });
        } else { //new game
            $scope.initController();
        }

        // Save the changes made to the path
        $scope.save = function () {

            if (checkFields()) {
                // Salvataggio date in timestamp Unix (ms)
                $scope.currentGame.from = $scope.startDate.getTime();
                $scope.currentGame.to = $scope.endDate.getTime();

                // Salvataggio orari di inizio e fine raccolta dati
                if (this.enablePiediBusParams) {
                    $scope.currentGame.fromHour = $scope.collectFromHour.toTimeString().slice(0, 5);
                    $scope.currentGame.toHour = $scope.collectToHour.toTimeString().slice(0, 5);
                }

                $scope.currentGame.classRooms = [];
                $scope.classes.forEach(function (entry) {
                    if (entry.value) {
                        $scope.currentGame.classRooms.push(entry.name);
                    }
                });

                $scope.saveData('game', $scope.currentGame).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                    function (response) {
                        console.log('Salvataggio dati a buon fine.');
                        if ($scope.currentGame.objectId) { //edited
                            for (var i = 0; i < $scope.games.length; i++) {
                                if ($scope.games[i].objectId == $scope.currentGame.objectId) $scope.games[i] = $scope.currentGame;
                            }
                        } else {
                            if ($scope.games) $scope.games.push(response.data);
                        }

                        DataService.updateConfParams($scope.currentGame).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                            function (response) {
                                console.log('Salvataggio params a buon fine.');
                                $state.go('root.games-list');
                            });


                    }, function () {
                        alert('Errore nella richiesta.');
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
            var isValidate = true;
            var invalidFields = $('.ng-invalid');

            if (invalidFields.length > 0 || $scope.endDate.toISOString().localeCompare($scope.startDate.toISOString(), { numeric: true }) <= 0
                || $scope.collectToHour.toTimeString().localeCompare($scope.collectFromHour.toTimeString(), { numeric: true }) <= 0)
                isValidate = false;

            return isValidate;
        }

        // Back without saving changes
        $scope.back = function () {
            createDialog('templates/modals/back.html', {
                id: 'back-dialog',
                title: 'Sei sicuro di voler uscire senza salvare?',
                success: { label: 'Conferma', fn: function () { $state.go('root.games-list'); } }
            });
        };
    })


    .controller('GameInfoCtrl', function ($scope) {
        $scope.$parent.selectedTab = 'info';
        $scope.$parent.enablePiediBusParams = false;

        $scope.$on('gameLoaded', function (e) {
            $scope.classToggled();
        });

        $scope.toggleSelectedClasses = function () {
            $scope.$parent.classes.forEach(function (currentClass) {
                currentClass.value = $scope.classesAllSelected;
            });
        }
        $scope.classToggled = function () {
            $scope.classesAllSelected = $scope.$parent.classes.every(function (cl) { return cl.value; })
        }

    })


    .controller('GameParamsCtrl', function ($scope) {
        $scope.$parent.selectedTab = 'params';

        $scope.$on('gameLoaded', function (e) {
            $scope.classToggled();
        });

        $scope.toggleSelectedClasses = function () {
            $scope.$parent.classes.forEach(function (currentClass) {
                currentClass.value = $scope.classesAllSelected;
            });
        }
        $scope.classToggled = function () {
            $scope.classesAllSelected = $scope.$parent.classes.every(function (cl) { return cl.value; })
        }

        $scope.calculateCDND = function () {
            if ($scope.currentGame) {
                $scope.currentGame.params.const_daily_nominal_distance = (
                    (parseInt($scope.currentGame.params.piedi_o_bici_in_autonomia_studenti) * parseInt($scope.currentGame.params.piedi_o_bici_in_autonomia_distanza)) +
                    (parseInt($scope.currentGame.params.piedi_o_bici_con_adulti_studenti) * parseInt($scope.currentGame.params.piedi_o_bici_con_adulti_distanza)) +
                    (parseInt($scope.currentGame.params.scuolabus_o_autobus_studenti) * parseInt($scope.currentGame.params.scuolabus_o_autobus_distanza)) +
                    (parseInt($scope.currentGame.params.parcheggio_attestamento_studenti) * parseInt($scope.currentGame.params.parcheggio_attestamento_distanza)) +
                    (parseInt($scope.currentGame.params.auto_fine_a_scuola_studenti) * parseInt($scope.currentGame.params.auto_fine_a_scuola_distanza))
                ) / 1000;

                return $scope.currentGame.params.const_daily_nominal_distance;
            }
        };

        $scope.calculateKMStimati = function () {
            if ($scope.currentGame) {
                actualDays = 1;
                // calcuate actual days.

                $scope.kmStimati = $scope.currentGame.params.const_daily_nominal_distance * actualDays;

                return $scope.kmStimati;
            }

        }

        // $scope.calculateKMBonus = function () {
        //     if ($scope.currentGame) {
        //         $scope.currentGame.params.km_bonus = (
        //             parseInt($scope.currentGame.params.const_zi_solo_bonus) +
        //             parseInt($scope.currentGame.params.const_cloudy_bonus) +
        //             parseInt($scope.currentGame.params.const_rain_bonus) +
        //             parseInt($scope.currentGame.params.const_snow_bonus) +
        //             parseInt($scope.currentGame.params.const_ZeroImpactDayClass_bonus) +
        //             parseInt($scope.currentGame.params.const_NoCarDayClass_bonus)
        //         ) / 1000;
        //         return $scope.currentGame.params.km_bonus;
        //     } else {
        //         return 0;
        //     }

        // }

        $scope.calculateKMTarget = function () {
            if ($scope.currentGame) {
                return ($scope.kmStimati + parseInt($scope.currentGame.params.km_bonus));
            }
        }

    });