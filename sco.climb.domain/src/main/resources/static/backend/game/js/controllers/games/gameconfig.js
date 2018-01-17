angular.module('consoleControllers.gameconfig', ['ngSanitize'])

.controller('GameConfigCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, createDialog) {
    
    $scope.initController = function() {
        $scope.isNewGameConf = true;
        if ($stateParams.idGameConfig) $scope.isNewGameConf = false;
        //load general templates
        DataService.getGameConfData('gameconfigtemplate').then(
                function(response) {
                    console.log('Caricamento delle config andata a buon fine.');
                    $scope.configs = response.data;
                    $scope.configs.forEach(config => {
                        formatIntFields(config);
                    });
                    if (!$scope.isNewGameConf) {
                        //load specific template for this         
                        DataService.getGameConfData('gameconfigbyid', {"ownerId": $stateParams.idDomain, confId: $stateParams.idGameConfig}).then(
                            function(response) {
                                console.log('Caricamento della config specifica andata a buon fine.');
                                var specificConfig = response.data;
                                for (var i = 0; i < $scope.configs.length; i++) {
                                    if ($scope.configs[i].objectId == specificConfig.confTemplateId) {
                                        var backupObjectId = $scope.configs[i].objectId; //used to preserve objectId when editing an existing config. objectId is used to identify template
                                        angular.merge($scope.configs[i], specificConfig);
                                        $scope.configs[i].objectId = backupObjectId;
                                        break;
                                    }
                                }
                                $scope.selectedConfig = $scope.configs[i];
                                formatIntFields($scope.selectedConfig);
                            }, function() {
                                alert('Errore nel caricamento della config specifica.');
                            }
                        );
                    } else {
                        $scope.selectedConfig = response.data[0];
                    }
                }, function() {
                    alert('Errore nel caricamento della config.');
                }
            );

        $scope.saveData = DataService.saveData;        
        
    }


    $scope.initController();
    

    $scope.save = function () {        
        if (checkFields()) {
            $scope.selectedConfig.pedibusGameId = $stateParams.idGame;
            $scope.selectedConfig.ownerId = $stateParams.idDomain;
            $scope.selectedConfig.confTemplateId = $scope.selectedConfig.objectId;
            if ($scope.isNewGameConf) {
                $scope.selectedConfig.objectId = null;
            } else {
                $scope.selectedConfig.objectId = $stateParams.idGameConfig;
            }
            $scope.saveData('gameconfigdetail', $scope.selectedConfig).then(
                function(response) {
                    console.log('Salvataggio dati a buon fine.');
                    if ($scope.reloadGamesConfig) {
                        $scope.reloadGamesConfig($stateParams.idSchool, true);
                    }
                    $state.go('root.games-list');
                }, function() {
                    alert('Errore nel salvataggio della configurazione.');
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

    function formatIntFields(config) {
        config.params.groups.forEach(group => {
            group.fields.forEach(field => {
                if (!field.value) {
                    field.value = field.defaultValue;
                }
                if (field.type == "number") {
                    field.value = parseInt(field.value);
                }   
            });
        });
    }

    // Exit without saving changes
    $scope.back = function () {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$window.history.back();} }
        });
    };
});