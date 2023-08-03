var institutesModule = angular.module('consoleControllers.institutes', ['ngSanitize'])

.controller('InstitutesListCtrl', function ($scope, $rootScope, DataService, $log,createDialog, PermissionsService) {
    $scope.$parent.mainView = 'institute';
    $scope.PermissionsService = PermissionsService;
    /*ngOidcClient.signinRedirect().then(function (user) {
            $log.log("user:" + JSON.stringify(user));
            if (!!user) {
                $log.log('Logged in so going to home state');
                $state.go('app.home');
            }
        });*/
    $scope.delete = function (institute) {
        createDialog('templates/modals/delete-institute.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData('institute', institute).then(
                    function() {
                        console.log("Rimozione effettuata con successo.");
                        $scope.institutesList.splice($scope.institutesList.indexOf(institute), 1);
                    }, function() {
                        alert("Errore nella richiesta.");
                    });
            } }
        });
    };
})