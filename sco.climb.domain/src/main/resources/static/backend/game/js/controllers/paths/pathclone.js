angular.module('consoleControllers.pathclone', ['ngSanitize'])

    .controller('PathCloneCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, MainDataService, createDialog) {

        $scope.initController = function () {
        	DataService.getGameById($stateParams.idDomain, $stateParams.idGame).then(function (response)  {
        		console.log('Caricamento gioco andata a buon fine.');
        		$scope.currentGame = response.data;
        		$scope.classes = $scope.currentGame.classRooms ? $scope.currentGame.classRooms.length : 0; 
        	});
            DataService.getData('gamereports').then(function (response) {
		            console.log('Caricamento report dei giochi andata a buon fine.');
		            $scope.allGames = response.data;
            });
        }
        
        $scope.clone = function (game) {
          createDialog('templates/modals/clone-game.html', {
              id: 'clone-game-dialog',
              title: 'Clona percorso',
              classes: $scope.classes,
              success: {
                  label: 'Conferma', fn: function () {
                      DataService.cloneGame($stateParams.idDomain, $stateParams.idInstitute,
                      		$stateParams.idSchool, $stateParams.idGame, game.itineraryId).then(
                          function (response) {
                              alert("clone gioco effettuato con successo.");
                              $scope.$parent.paths.push(response.data);
                              $scope.back();
                          }, function (error) {
                              alert("Errore nella richiesta:" + error.data.errorMsg);
                          });
                  }
              }
          });
        };
        
        $scope.initController();
        
        // Exit without saving changes
        $scope.back = function () {
            $state.go('root.paths-list'); //$window.history.back();
        };

    });