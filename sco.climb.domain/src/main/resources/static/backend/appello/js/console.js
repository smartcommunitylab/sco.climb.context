angular.module('console', ['DataService']).controller('userCtrl', function($scope, DataService) {
	DataService.getProfile().then(function(p) {
  	$scope.initData(p);
  });

	$scope.selectedTab = "";
	$scope.language = "it";

	$scope.initData = function(profile) {
		$scope.profile = profile;
	}
});
