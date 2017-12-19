/* global angular */
angular.module('climbGameUser.controllers.users.editRole', [])
.controller('userRoleEditCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    console.log("User email: " + $stateParams.userEmail);
    initParentNavigation();
    
    if (!$stateParams.userEmail) {    
      $state.go($rootScope.backStateToGo);
    }
    $scope.saving = false;
    dataService.getUserByEmail($stateParams.userEmail).then(
      function (data) {
        $scope.user = data;
        console.log(JSON.stringify($scope.user));
        $scope.initialRoles = angular.copy($scope.user.roles);
        //the UI can assign only a single role, so have to filter roles to the first non admin role
        $scope.initialSingleRole = ''
        if ($scope.user.roles.includes('owner')) $scope.initialSingleRole = 'owner';
        else if ($scope.user.roles.includes('parent')) $scope.initialSingleRole = 'parent';
        else if ($scope.user.roles.includes('teacher')) $scope.initialSingleRole = 'teacher';
        else if ($scope.user.roles.includes('volunteer')) $scope.initialSingleRole = 'volunteer';
        $scope.user.roles = angular.copy($scope.initialSingleRole);
        console.log("Initial role: " + $scope.initialRoles);
        var authorizationText = JSON.stringify($scope.user.authorizations);
        var resolveAuthorizationField = function(sourceString, param) {
          var regex = new RegExp('"name":"' + param + '","value":"([^\"]*)"}');
          var result = regex.exec(sourceString);
          if (result && result.length > 0) {
            $scope.user[param] = result[1];
          }
        }
        resolveAuthorizationField(authorizationText, 'instituteId');
        resolveAuthorizationField(authorizationText, 'schoolId');
        resolveAuthorizationField(authorizationText, 'gameId');
        //TODO: initialRole priority
        //TODO: have to initialize user.instituteId, gameId, ... from first authorization
      }
    );

    $rootScope.saveUserRole = function() {
      $scope['role_edit'].$setSubmitted();
      if ($scope['role_edit'].$invalid) {
        $mdToast.show(
          $mdToast.simple()
            .textContent('Compila i campi richiesti!')
            .position("bottom")
            .hideDelay(3000)
        );
        return;
      }   
      //single role only, if multiple role can be assigned we have to edit this method!
      $scope.saving = true;

      var removeRoleSuccess = function(data) {
        dataService.addRole($scope.user.roles, $scope.user).then(
          function (data) {
            $state.go($rootScope.backStateToGo);
          },
          function () {
            $scope.saving = false;
            $mdToast.show(
              $mdToast.simple()
                .textContent('Errore nell\'aggiunta del ruolo!')
                .position("bottom")
                .hideDelay(3000)
            );
          }
        );
      }

      if ($scope.initialSingleRole) {
        dataService.removeRole($scope.initialSingleRole, $scope.user).then(
          removeRoleSuccess,
          function () {
            $scope.saving = false;
            $mdToast.show(
              $mdToast.simple()
                .textContent('Errore nella rimozione del ruolo!')
                .position("bottom")
                .hideDelay(3000)
            );
          }
        );
      } else {
        removeRoleSuccess();
      }
    }

    function initParentNavigation() {
      $rootScope.title = "User role edit";
      $rootScope.backStateToGo = "home.users-lists.list";
    }

  }
])
