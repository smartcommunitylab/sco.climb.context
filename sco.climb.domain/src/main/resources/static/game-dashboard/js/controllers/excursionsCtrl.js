/* global angular */
angular.module('climbGame.controllers.excursions', [])
  .controller('excursionsCtrl',
    function ($scope, $window, $mdDialog, $mdToast, dataService,$stateParams) {
      $scope.showHints = false
      $scope.datepickerisOpen = false
      $scope.excursions = null
      $scope.sendingData = false
      
      /* excursion example
      {
        children: 12,
        classRoom: '1^',
        creationDate: 1486042491236,
        day: 1485385200000,
        distance: 750,
        gameId: '588889c0e4b0464e16ac40a0',
        lastUpdate: 1486042491236,
        meteo: 'cloudy',
        name: 'test1',
        objectId: 'c219c822-35af-4e34-ad81-b39591dd36a2',
        ownerId: 'VELA'
      }
      */
      
      $scope.isExcursion = function (ex) {
      	if(ex && ex.hasOwnProperty('goodAction') && ex.goodAction) {
      		return false;
      	}
      	return true;
      }
      
      $scope.refreshExcursions = function () {
      	dataService.getGameById().then(
            function (game) {
              $scope.game = game;
              dataService.getExcursions(game.from, game.to).then(
                function (excursions) {
                  $scope.excursions = [];
                   //filter by selection
                  if ($stateParams.type =='buonazione'){
                      //  filter buone azioni
                    excursions.forEach(function(element){
                      if (!$scope.isExcursion(element))
                        $scope.excursions.push(element)
                    })
                  } else {
                    // filter excursions
                    excursions.forEach(function(element){
                    if ($scope.isExcursion(element))
                    $scope.excursions.push(element)
                    })
                  }
                },
                function (reason) {
                  console.log("[refreshExcursions]" + JSON.stringify(reason));
                }
              );
            },
            function (reason) {
            	console.log('[refreshExcursions]' + JSON.stringify(reason));
            }
      	);
      }

      $scope.refreshExcursions()
      
      $scope.changeExcursion = function() {
      	$scope.newExcursion.children = null;
      	$scope.newExcursion.distance = null;
      }

      $scope.scroll = function (direction) {
        if (direction === 'up') {
          $window.document.getElementById('excursions-list').scrollTop -= 50
        } else if (direction === 'down') {
          $window.document.getElementById('excursions-list').scrollTop += 50
        }
      }

      /* Form */
      var emptyExcursion = {
        name: null,
        date: null,
        children: null,
        distance: null,
        meteo: 'sunny',
        goodAction: $stateParams.type==='buonazione'?true:false
      }

      $scope.newExcursion = angular.copy(emptyExcursion)

      $scope.now = new Date()

      $scope.createExcursion = function () {

        if($scope.newExcursion.goodAction) {
        	$scope.newExcursion.children = 1;
        	$scope.newExcursion.meteo = 'sunny';
        }
        
        var params = {
          name: $scope.newExcursion.name,
          date: $scope.newExcursion.date.getTime(),
          children: $scope.newExcursion.children,
          distance: $scope.newExcursion.distance * 1000,
          meteo: $scope.newExcursion.meteo,
          goodAction: $scope.newExcursion.goodAction
        }        

        if (!params.name || !params.date || !params.children || !params.distance || !params.meteo) {
          return
        }
        $mdDialog.show({
          // targetEvent: $event,
          scope: $scope, // use parent scope in template
          preserveScope: true, // do not forget this if use parent scope
          template: '<md-dialog>' +
            '  <div class="cal-dialog-title">Invio dati</div><md-divider></md-divider>' +
            '  <div class="cal-dialog-text">' +
            '			Si stanno per aggiungere <span class="big_txt">{{newExcursion.distance * newExcursion.children}}</span> Km ' + 
            '     per questa {{(isExcursion(newExcursion)) ? \'gita\' : \'buona azione\'}}. Confermate l\'operazione?</div>' +
            '    <div layout="row"  layout-align="start center" ><div layout"column" flex="50" ><md-button ng-click="closeDialog()" class="send-dialog-delete">' +
            '      Annulla' +
            '   </div> </md-button>' +
            '<div layout"column" flex="50" ><md-button ng-click = "confirmSend()" class = "send-dialog-confirm" > ' +
            '      Invia' +
            '    </md-button></div>' +
            '</div></md-dialog>',
            
          controller: function DialogController($scope, $mdDialog) {
            $scope.closeDialog = function () {
              $mdDialog.hide()
            }

            $scope.confirmSend = function () {
              if (!$scope.sendingData) {
                $scope.sendingData = true;
                dataService.postExcursion(params).then(
                  function (data) {
                    // reset form
                    $scope.resetForm()
                      // refresh
                    $scope.refreshExcursions()
                      //close dialog
                    $mdDialog.hide();
                    $scope.sendingData = false;
                  },
                  function (reason) {
                    // console.log(reason)
                  	$mdDialog.hide();
                  	$mdToast.show($mdToast.simple().content('errore nel salvataggio dati'));
                    $scope.sendingData = false;
                  }
                )
              }
            }
          }
        })

      }

      $scope.resetForm = function () {
        $scope.newExcursion = angular.copy(emptyExcursion);
        $scope.excursionForm.$setPristine();
        $scope.excursionForm.$setUntouched();
      }
    })
