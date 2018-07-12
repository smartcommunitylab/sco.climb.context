angular.module('consoleControllers.gameconfig', ['ngSanitize'])

.controller('GameConfigCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, createDialog) {
    
    $scope.initController = function () {
        $scope.isNewGameConf = true;
        if ($stateParams.idGameConfig) $scope.isNewGameConf = false;
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
                    }, function () {
                        alert('Errore nel caricamento della config specifica.');
                    }
                );

                // $scope.configs.forEach(config => {
                //     formatIntFields(config);
                // });

            }, function () {
                alert('Errore nel caricamento della config.');
            }
        );

        $scope.saveData = DataService.saveData;

    }


    $scope.initController();
    

    // function formatIntFields(config) {
    //     config.params.groups.forEach(group => {
    //         group.fields.forEach(field => {
    //             if (!field.value) {
    //                 field.value = field.defaultValue;
    //             }
    //             if (field.type == "number") {
    //                 field.value = parseInt(field.value);
    //             }   
    //         });
    //     });
    // }

    // Exit without saving changes
    $scope.back = function () {
        $state.go('root.games-list'); //$window.history.back();
    };

    // select template.
    $scope.setTemplate = function (i) {
        $scope.selectedConfig = $scope.configs[i];
        $scope.selectedConfig.pedibusGameId = $stateParams.idGame;
        $scope.selectedConfig.ownerId = $stateParams.idDomain;

        createDialog('templates/modals/back.html', {
            id: 'back-dialog',
            title: 'Sei sicuro di salvare template?',
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
            }
        });


    };
    
});