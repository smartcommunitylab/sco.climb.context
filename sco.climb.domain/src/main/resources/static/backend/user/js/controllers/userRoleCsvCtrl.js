/* global angular */
angular.module('climbGameUser.controllers.users.csvRole', [])
.controller('userRoleCsvCtrl', ['$scope', '$rootScope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$state', '$stateParams', 'dataService', 'configService',
  function ($scope, $rootScope, $filter, $window, $interval, $mdDialog, $mdToast, $state, $stateParams, dataService, configService) {
		$scope.user = {};
		$scope.saving = false;
		
    $scope.loadInstitutesList = function() {
      dataService.getInstitutesList().then(
        function (data) {
          $scope.institutesList = data;
        }
      );
    }
    
    $scope.loadSchoolsList = function() {
      dataService.getSchoolsList($scope.user.instituteId).then(
        function (data) {
          $scope.schoolsList = data;
        }
      );
    }
    
    $scope.loadGamesList = function() {
      dataService.getGamesList($scope.user.instituteId, $scope.user.schoolId).then(
        function (data) {
          $scope.gamesList = data;
        }
      );
    }
    
    $scope.uploadVolunteerFile = function() {
	  	var fileInput = document.getElementById('upload-file');
	  	if(fileInput.files.length == 0) {
        $mdToast.show(
            $mdToast.simple()
              .textContent($filter('translate')('role_csv_file_missing'))
              .position("bottom")
              .hideDelay(3000)
          );
        return;
	  	}
	  	var file = fileInput.files[0];
	  	var formData = new FormData();
	  	formData.append('file', file);
	  	var element = {
	  			"instituteId": $scope.user.instituteId,
	  			"schoolId": $scope.user.schoolId,
	        "formdata": formData
	  	};
	  	$scope.saving = true;
	  	dataService.uploadVolunteerFile(element).then(
          function (response) {
          	$scope.saving = false;
            $mdToast.show(
              $mdToast.simple()
                .textContent($filter('translate')('role_csv_upload_ok'))
                .position("bottom")
                .hideDelay(3000)
            );
            $state.reload();
          },
          function () {
            $scope.saving = false;
            $mdToast.show(
              $mdToast.simple()
                .textContent($filter('translate')('role_csv_upload_error'))
                .position("bottom")
                .hideDelay(3000)
            );
          }	  			
	    );
	  }

    function initParentNavigation() {
      $rootScope.title = "title_user_role_csv";
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
    
    initParentNavigation();
    $scope.loadInstitutesList();
  }
])
