/* global angular */
angular.module('climbGameUser.controllers.users.editRole', [])
.controller('userRoleEditCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
    console.log("User email: " + $stateParams.userEmail);
    initParentNavigation();
    
    if (!$stateParams.userEmail) {    
      $state.go($rootScope.backStateToGo);
    }
    $scope.authTextMap = {};
    $scope.saving = false;
    $scope.actualRole;
    
    $scope.isDomainOwner = false;
    dataService.getProfile().then(
    		function (data) {
    			$scope.isDomainOwner = dataService.isDomainOwner(data);
    		}
    );

    dataService.getUserByEmail($stateParams.userEmail).then(
      function (data) {
        $scope.user = data;
        $scope.loadInstitutesList(true);
        $scope.authTextMap[$scope.user.objectId] = {};
        if(!angular.equals($scope.user.roles, {})) {
          var properties = Object.getOwnPropertyNames($scope.user.roles);
          properties.forEach(function(key) {
            $scope.authTextMap[$scope.user.objectId][key] = {
        	  "data": "",
        	  "loaded": false
            };
            getAuthText($scope.user, key);
          });
    	}
      }
    );

    $rootScope.saveUserRole = function() {
      $scope['role_edit'].$setSubmitted();
      if ($scope['role_edit'].$invalid) {
        $mdToast.show(
          $mdToast.simple()
            .textContent($filter('translate')('validation_error_msg'))
            .position("bottom")
            .hideDelay(3000)
        );
        return;
      }   
      //single role only, if multiple role can be assigned we have to edit this method!
      $scope.saving = true;
      dataService.addRole($scope.actualRole, $scope.user).then(
          function (data) {
            $mdToast.show(
              $mdToast.simple()
                .textContent($filter('translate')('role_add_ok_msg'))
                .position("bottom")
                .hideDelay(3000)
            );
            $state.reload();
          },
          function () {
            $scope.saving = false;
            $mdToast.show(
              $mdToast.simple()
                .textContent($filter('translate')('role_add_error_msg'))
                .position("bottom")
                .hideDelay(3000)
            );
          }
        );      
    }
    
    $scope.showAllOption = function() {
    	return $scope.isDomainOwner && ($scope.actualRole== 'editor');
    }

    $scope.loadInstitutesList = function(recoverOtherStates) {
      dataService.getInstitutesList().then(
        function (data) {
          $scope.institutesList = data;
          if (recoverOtherStates) $scope.loadSchoolsList(true);
        }
      );
    }
    $scope.loadSchoolsList = function(recoverOtherStates) {
      if (!recoverOtherStates) {
        $scope.user.schoolId = undefined;
        $scope.user.gameId = undefined;
        $scope.schoolsList = undefined;
        $scope.gamesList = undefined;
      }
      if($scope.user.instituteId == "*") {
      	$scope.user.schoolId = "*";
      } else {
        dataService.getSchoolsList($scope.user.instituteId).then(
            function (data) {
              $scope.schoolsList = data;
              if (recoverOtherStates) $scope.loadGamesList(true);
            }
          );      	      	
      }
    }
    $scope.loadGamesList = function(recoverOtherStates) {
      if (!recoverOtherStates) {
        $scope.user.gameId = undefined;
        $scope.gamesList = undefined;
      }
      if($scope.user.schoolId != "*") {
        dataService.getGamesList($scope.user.instituteId, $scope.user.schoolId).then(
            function (data) {
              $scope.gamesList = data;
            }
          );      	
      }
    }

    function initParentNavigation() {
      $rootScope.title = "title_user_role_edit";
      $rootScope.backStateToGo = "home.users-lists.list";
    }

    function getAuthText(user, authKey) {
      dataService.getAuthText(user, authKey).then(
        function (data) {
          $scope.authTextMap[user.objectId][authKey]['data'] = data;
          $scope.authTextMap[user.objectId][authKey]['loaded'] = true;
        },
        function (reason) {
          alert(reason);
        }
      );
    }
  }
])
