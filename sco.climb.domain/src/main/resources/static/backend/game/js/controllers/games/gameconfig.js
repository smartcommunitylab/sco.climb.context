angular.module('consoleControllers.gameconfig', ['ngSanitize'])

.controller('GameConfigCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, createDialog) {
    
    $scope.initController = function() {
        $scope.isNewGameConf = true;
        if ($stateParams.idGameConfig) $scope.isNewGameConf = false;
        DataService.getData('gameconfigdetail', 
            $stateParams.idDomain, 
            $stateParams.idInstitute, 
            $stateParams.idSchool,
            null,
            $stateParams.idGame).then(
                function(response) {
                    console.log('Caricamento delle config andata a buon fine.');
                    $scope.configs = response.data;
                    $scope.configs.forEach(config => {
                        config.params.groups.forEach(group => {
                            group.fields.forEach(field => {
                                if (!field.value) {
                                    if (field.type == "number") {
                                        field.value = parseInt(field.defaultValue);
                                    } else {
                                        field.value = field.defaultValue;
                                    }                                    
                                }
                            });
                        });
                    });
                    $scope.selectedConfig = response.data[0];
                    console.log($scope.configs);
                }, function() {
                    alert('Errore nel caricamento delle tappe.');
                }
            );
        
        $scope.saveData = DataService.editData;
    }


    $scope.initController();
    

    $scope.save = function () {        
        if (checkFields()) {
            console.log($scope.selectedConfig);
            $scope.saveData('creategame', $scope.selectedConfig).then(
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
});