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

      $scope.loading = true;
      var roleToShow = $stateParams.role;
      if (roleToShow == 'all' || roleToShow == 'norole') {
        roleToShow = '';
      }
      dataService.getUsersByRole(roleToShow).then(
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
          $scope.loading = false;
        },
        function (reason) {
          console.log('[UsersList]' + reason)
          $scope.loading = false;
        }
      );

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

    }
  ])
