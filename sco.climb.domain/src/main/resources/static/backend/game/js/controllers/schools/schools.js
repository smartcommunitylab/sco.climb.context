angular.module('consoleControllers.schools', ['ngSanitize'])

// Schools controller
.controller('SchoolsListCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'school';
    if($scope.$parent.selectedOwner && $scope.$parent.selectedInstitute && $scope.$parent.schoolsModified)
        $scope.$parent.loadData($scope.$parent.mainView);

    $scope.delete = function (schoolIndex) {
        createDialog('templates/modals/delete-school.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData($scope.$parent.mainView, $rootScope.schools[schoolIndex]).then(
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

.controller('SchoolCtrl', function ($scope, $stateParams, $rootScope, $location, $timeout, DataService, createDialog, $filter) {
    $scope.$parent.mainView = 'school';

    if ($stateParams.idSchool)        // controlla se si sta modificando una scuola esistente
    {
        $scope.currentSchool = Number($stateParams.idSchool);
        $scope.saveData = DataService.editData;
    }
    else
    {
        $scope.currentSchool = $rootScope.schools.push({        // variabile che indica la posizione nell'array della scuola che sta venendo editata
            name: '',
            address: '',
            instituteId: $scope.$parent.selectedInstitute.objectId,
            ownerId: $scope.$parent.selectedOwner,
            classes: [],                        // TODO: da implementare lato API
            lines: []                           // solo per uso locale
        })-1;
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

    $scope.newClass = '';
    $scope.addNewClass = function()
    {
        $rootScope.schools[$scope.currentSchool].classes.push($scope.newClass);
        $scope.newClass = '';
    };
    $scope.deleteClass = function(index)
    {
        $rootScope.schools[$scope.currentSchool].classes.splice(index,1);
    };
    $scope.sortClasses = function()
    {
        $rootScope.schools[$scope.currentSchool].classes.sort();
    };

    // Save the changes made to the path
    $scope.save = function () {
        if (checkFields())
        {
            $scope.saveData($scope.$parent.mainView, $rootScope.schools[$scope.currentSchool]).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function() {
                    console.log('Salvataggio dati a buon fine.');
                    $location.path('schools-list');
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
            // TODO: da implementare anche salvataggio linee
        }
        else
        {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e di aver aggiunto almeno una classe e una linea pedibus.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
        }
    };

    // Controlla se ci sono campi vuoti e se non ci sono classi
    function checkFields() {
        var isValidate = true;
        var invalidFields = $('.ng-invalid');

        if (invalidFields.length > 0 ||$rootScope.schools[$scope.currentSchool].classes.length === 0)
            isValidate = false;

        return isValidate;
    }

    // Back without saving changes
    $scope.back = function() {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$location.path('schools-list');} }
        });
    };
})


.controller('SchoolInfoCtrl', function ($scope) {
    $scope.$parent.selectedTab = 'info';
})

.controller('LinesListCtrl', function ($scope, $rootScope, createDialog) {
    $scope.$parent.selectedTab = 'lines-list';
    $scope.remove = function (idLine) {
        createDialog('templates/modals/delete-line.html',{
            id : 'delete-line-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {$rootScope.schools[$scope.currentSchool].lines.splice(idLine, 1);} }
        });
    };
});