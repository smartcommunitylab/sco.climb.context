angular.module('consoleControllers.gameconfig', ['ngSanitize'])

    .controller('GameConfigCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, MainDataService, createDialog) {

        $scope.initController = function () {
            $scope.isNewGameConf = true;
            if ($stateParams.idGameConfig) $scope.isNewGameConf = false;

            MainDataService.getGames($stateParams.idSchool).then(function (response) {
                $scope.games = response.data;
                $scope.games.forEach(game => {
                    if (game.objectId == $stateParams.idGame) {
                        $scope.selectedGame = game;
                        //load general templates
                        DataService.getGameConfData('gameconfigtemplate').then(
                            function (response) {
                                console.log('Caricamento delle config andata a buon fine.');
                                $scope.configs = response.data;
                                // load conf data for game and check if template exists inside.
                                DataService.getGameConfData('gameconfigbyid', { "ownerId": $stateParams.idDomain, "confId": $stateParams.idGame }).then(
                                    function (response) {
                                        console.log('Caricamento della config specifica andata a buon fine.');
                                        var specificConfig = response.data;
                                        for (var i = 0; i < $scope.configs.length; i++) {
                                            if ($scope.configs[i].objectId == specificConfig.confTemplateId) {
                                                var backupObjectId = $scope.configs[i].objectId; //used to preserve objectId when editing an existing config. objectId is used to identify template
                                                angular.merge($scope.configs[i], specificConfig);
                                                $scope.configs[i].objectId = backupObjectId;
                                                $scope.configs[i].saved = true;
                                                break;
                                            }
                                        }
                                        $scope.selectedConfig = $scope.configs[i];
                                        $scope.$parent.selectedConfig = $scope.selectedConfig;
                                    }, function (error) {
                                        if (error.data.errorMsg == 'game conf not found') {
                                            var titleMsg = 'Scegli un template per il gioco';
                                            createDialog(null, {
                                                id: 'back-dialog',
                                                title: 'Attenzione!',
                                                success: {
                                                    label: 'Ok',
                                                },
                                                template: '<p>' + titleMsg + '</p>'
                                            });
                                        }
                                    }
                                );
                            }, function () {
                                alert('Errore nel caricamento della config.');
                            }
                        );
                    }
                });
            });
        }


        $scope.initController();

        // Exit without saving changes
        $scope.back = function () {
            $state.go('root.games-list'); //$window.history.back();
        };

        // select template.
        $scope.setTemplate = function (i) {
            $scope.selectedConfig = $scope.configs[i];
            $scope.selectedConfig.pedibusGameId = $stateParams.idGame;
            $scope.selectedConfig.ownerId = $stateParams.idDomain;

            var titleMsg = 'Sei sicuro di salvare template?';
            // identify and select game object.
            if ($scope.selectedGame.gameId) {
                titleMsg = 'Gioco già istanziato. Sei sicuro di cambiare template?';
            }

            createDialog(null, {
                id: 'back-dialog',
                title: 'Attenzione!',
                success: {
                    label: 'Conferma',
                    fn: function () {
                        DataService.updateTemplateToGame($scope.selectedConfig).then(
                            function () {
                                console.log('Salvataggio template a buon fine.');
                                $scope.initController();
                            }, function (error) {
                                if (error.data.errorMsg) {
                                    alert(error.data.errorMsg);
                                } else {
                                    alert('Errore nel salvataggio delle template.');
                                }
                            }
                        );

                    }
                },
                template: '<p>' + titleMsg + '</p>',
            });
        }


    });