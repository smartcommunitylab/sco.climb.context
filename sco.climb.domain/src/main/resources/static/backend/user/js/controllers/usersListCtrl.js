/* global angular */
angular.module('climbGameUser.controllers.users.lists.list', [])
.controller('usersListCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
      console.log("Selected user list: " + $stateParams.role);
      if (!$stateParams.role) {
        $state.go("home.users-lists.list", {'role':'all'});
        $scope.$parent.currentUsersListTab = "owner-list-tab";
      }
      $scope.$parent.currentUsersListTab = $stateParams.role + "-list-tab";

      $scope.authTextMap = {};
      $scope.showAuthsMap = {};
      $scope.loading = true;
      $scope.roleToShow = $stateParams.role;
      if ($scope.roleToShow == 'all' || $scope.roleToShow == 'norole') {
      	$scope.roleToShow = null;
      }
      dataService.getUsersByRole($scope.roleToShow).then(
        function (data) {
          if ($stateParams.role == 'norole') {
            $scope.users = [];
            data.forEach(element => {
              if (element.roles.length == 0) $scope.users.push(element);
            });
          } else {
            $scope.users = data;
          }
          console.log('[UsersList] Users number: ' + $scope.users.length)
          $scope.users.forEach(function(user) {
          	$scope.showAuthsMap[user.objectId] = false;
          	$scope.authTextMap[user.objectId] = {};
          	if(!angular.equals(user.authorizations, {})) {
          		var properties = Object.getOwnPropertyNames(user.authorizations);
        			properties.forEach(function(key) {
        				$scope.authTextMap[user.objectId][key] = '';
              });
          	}
          });
          $scope.loading = false;
        },
        function (reason) {
          console.log('[UsersList]' + reason)
          $scope.loading = false;
        }
      );
      
      //$scope.fromMap = function(user) {
      	//var out = {};
      	//user.authorizations.forEach((v, k) => out[k] = v);
        //return out;
      	//var result = [];
      	//angular.forEach(user.authorizations, function(auth) {
      	//	result.push(el);
        //});
      //};
      
      $scope.openUser = function(event, user) {
        $mdDialog.show({
          controller: ShowUserControllerDialog,
          templateUrl: 'templates/user_details_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user
          }
        });

        function ShowUserControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
        }

      };
      
      $scope.deleteAuth = function(event, user, authKey) {
        $mdDialog.show({
          controller: DeleteAuthControllerDialog,
          templateUrl: 'templates/auth_delete_confirm_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user,
            authKey: authKey,
            authText: $scope.authTextMap[user.objectId][authKey]
          }
        })
        .then(function(answer) {
          if (answer == 'confirm') {
            $scope.loading = true;
            dataService.removeAuth(authKey, user).then(
              function (data) {
              	delete user.authorizations[authKey];
                $scope.loading = false;
              },
              function (reason) {
                $mdToast.show(
                  $mdToast.simple()
                    .textContent($translate.instant('user_delete_error_msg'))
                    .position("bottom")
                    .hideDelay(3000)
                );
                $scope.loading = false;
              }
            );
          }
        });

        function DeleteAuthControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.authKey = locals.authKey;
          $scope.authText = locals.authText;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
          $scope.delete = function() {
            $mdDialog.hide('confirm');
          }
        }      	
      };
      
      $scope.deleteRole = function(event, user, role) {
        $mdDialog.show({
          controller: DeleteRoleControllerDialog,
          templateUrl: 'templates/role_delete_confirm_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user,
            currentRole: role
          }
        })
        .then(function(answer) {
          if (answer == 'confirm') {
            $scope.loading = true;
            dataService.removeRole(role, user).then(
              function (data) {
              	$scope.users.splice($scope.users.indexOf(user), 1);
                $scope.loading = false;
              },
              function (reason) {
                $mdToast.show(
                  $mdToast.simple()
                    .textContent($translate.instant('user_delete_error_msg'))
                    .position("bottom")
                    .hideDelay(3000)
                );
                $scope.loading = false;
              }
            );
          }
        });

        function DeleteRoleControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.currentRole = locals.currentRole;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
          $scope.delete = function() {
            $mdDialog.hide('confirm');
          }
        }      	
      };

      $scope.deleteUser = function(event, user) {
        $mdDialog.show({
          controller: DeleteUserControllerDialog,
          templateUrl: 'templates/user_delete_confirm_dialog.users_list.html',
          parent: angular.element(document.body),
          targetEvent: event,
          clickOutsideToClose:true,
          locals: {
            currentUser: user
          }
        })
        .then(function(answer) {
          if (answer == 'confirm') {
            $scope.loading = true;
            dataService.removeUser(user).then(
              function (data) {
                $scope.users.splice($scope.users.indexOf(user), 1);
                $scope.loading = false;
              },
              function (reason) {
                $mdToast.show(
                  $mdToast.simple()
                    .textContent($translate.instant('user_delete_error_msg'))
                    .position("bottom")
                    .hideDelay(3000)
                );
                $scope.loading = false;
              }
            );
          }
        });

        function DeleteUserControllerDialog($scope, $mdDialog, locals) {
          $scope.currentUser = locals.currentUser;
          $scope.cancel = function() {
            $mdDialog.cancel();
          };
          $scope.delete = function() {
            $mdDialog.hide('confirm');
          }
        }
     }
    
    $scope.hasRoles = function(user) {
    	if(user.roles) {
    		if(user.roles.length > 0) {
    			return true;
    		}
    	}
    	return false;
    }
    
    $scope.getShowAuths = function(user) {
    	if($scope.showAuthsMap.hasOwnProperty(user.objectId)) {
    		return $scope.showAuthsMap[user.objectId];
    	} else {
    		return false;
    	}
    }
      
    $scope.toggleShowAuths = function(user) {
    	if(!$scope.showAuthsMap.hasOwnProperty(user.objectId)) {
    		$scope.showAuthsMap[user.objectId] = false;
    	}
    	$scope.showAuthsMap[user.objectId] = !$scope.showAuthsMap[user.objectId];
    	var showAuths = $scope.showAuthsMap[user.objectId];
    	if(showAuths && !$scope.authTextMap[user.objectId].hasOwnProperty('retrieved')) {
    		if(!angular.equals(user.authorizations, {})) {
    			var properties = Object.getOwnPropertyNames(user.authorizations);
    			properties.forEach(function(key) {
    				getAuthText(user, key);
          });
    		}
    	}
    	
      function getAuthText(user, authKey) {
        dataService.getAuthText(authKey).then(
          function (data) {
            $scope.authTextMap[user.objectId][authKey] = data;
            $scope.authTextMap[user.objectId]['retrieved'] = true;
          },
          function (reason) {
          	alert(reason);
          }
        );
      } 
    }  

  }
])
.filter('fromMap', function() {
  return function(input) {
    var out = {};
    if(!angular.equals(input, {})) {
      var properties = Object.getOwnPropertyNames(input);
      properties.forEach(function(key) {
      	out[key] = input[key];
      });
    }
    return out;
  };
})
