/* global angular */
angular.module('climbGameUser.controllers.users.edit', [])
.controller('userEditCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    console.log("User email: " + $stateParams.userEmail);
    initParentNavigation();
    
    if ($stateParams.userEmail) {      
      dataService.getUserByEmail($stateParams.userEmail).then(
        function (data) {
          $scope.user = data;
        }
      );
    } else {
      $scope.user = {};
    }
    $scope.saving = false;

    $rootScope.saveUser = function() {
      $scope['user_edit'].$setSubmitted();
      if ($scope['user_edit'].$invalid) {
        $mdToast.show(
          $mdToast.simple()
            .textContent('Compila i campi richiesti!')
            .position("bottom")
            .hideDelay(3000)
        );
        return;
      }   
      $scope.saving = true;
      dataService.saveUser($scope.user).then(
        function (data) {
          $scope.saving = false;
          $state.go('home.user-edit', {'userEmail':$scope.user.email, 'newCreated': true});
        },
        function (reason) {
          $scope.saving = false;
          $mdToast.show(
            $mdToast.simple()
              .textContent('Errore nel salvataggio!')
              .position("bottom")
              .hideDelay(3000)
          );
        }
      );
    }

    function initParentNavigation() {
      if ($stateParams.userId) {
        $rootScope.title = "User edit";
      } else {
        $rootScope.title = "User creation";
      }
      $rootScope.backStateToGo = "home.users-lists.list";
    }

  }
])
