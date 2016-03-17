var searchTableApp = angular.module('report-appello', ['DataService', 'ngclipboard']);

var searchTableCtrl = searchTableApp.controller('userCtrl', function($scope, $http, $window, DataService) {
	$scope.selectedMenu = "report";
	$scope.selectedTab = "menu-report-appello";
	$scope.language = "it";
	$scope.defaultLang = "it";
	$scope.itemToDelete = "";

	$scope.edit = false;
	$scope.create = false;
	$scope.view = false;
	$scope.search = "";
	$scope.incomplete = true;

	$scope.error = false;
	$scope.errorMsg = "";

	$scope.ok = false;
	$scope.okMsg = "";

	$scope.data = null;
	$scope.status = 200;
	
	$scope.fEventType = -1;
	$scope.fCopyText = "";
	$scope.fDateFrom = "2016-03-14";
	$scope.fDateTo = "2016-03-14";
	$scope.fHourFrom = "07:30:00";
	$scope.fHourTo = "08:30:00";
	$scope.routeList = null;
	$scope.schoolList = null;
	$scope.selectedSchool = null;
	$scope.selectedRoute = null;
	
	DataService.getProfile().then(
		function(p) {
			$scope.initData(p);
		},
		function(e) {
			console.log(e);
			$scope.error = true;
			$scope.errorMsg = e.errorMsg;
		}
	);
	
	$scope.initData = function(profile) {
		$scope.profile = profile;
		
		var urlContext = "report/context/url";
		$http.get(urlContext).then(
			function (response) {
				$scope.contextApiUrl = response.data.url;
				var urlSchoolList = $scope.contextApiUrl + "school/" + $scope.profile.ownerId;
				$http.get(urlSchoolList, {headers: {'X-ACCESS-TOKEN': $scope.profile.token}}).then(
				function (response) {
					$scope.schoolList = response.data;
				},
				function(response) {
					console.log(response.data);
					$scope.error = true;
					$scope.errorMsg = response.data.errorMsg;
				});
			},
			function(response) {
				console.log(response.data);
				$scope.error = true;
				$scope.errorMsg = response.data;
			}
		);
	};
	
	$scope.changeSchool = function() {
		var urlRouteList = $scope.contextApiUrl + "route/" + $scope.profile.ownerId 
		+ "/school/" + $scope.selectedSchool.objectId;
		$http.get(urlRouteList, {headers: {'X-ACCESS-TOKEN': $scope.profile.token}}).then(
		function (response) {
			$scope.routeList = response.data;
		},
		function(response) {
			console.log(response.data);
			$scope.error = true;
			$scope.errorMsg = response.data.errorMsg;
		});
	}
	
	$scope.setNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome;
			map[key] = name;
		}
		return map;
	};

	$scope.setLocalNameMap = function(array) {
		var map = {};
		for (var d = 0, len = array.length; d < len; d += 1) {
			var key = array[d].objectId;
			var name = array[d].nome[$scope.language];
			map[key] = name;
		}
		return map;
	};

	$scope.resetError = function() {
		$scope.error = false;
		$scope.errorMsg = "";
	};

	$scope.resetOk = function() {
		$scope.ok = false;
		$scope.okMsg = "";
	};

	$scope.getModalHeaderClass = function() {
		if($scope.view) {
			return "view";
		}
		if($scope.edit) {
			return "edit";
		}
		if($scope.create) {
			return "create";
		}
	};

	$scope.setItemToDelete = function(id) {
		$scope.itemToDelete = id;
	};

	$scope.changeLanguage = function(language) {
		$scope.language = language;
	};

	$scope.resetUI = function() {
		$scope.search = "";
		$scope.incomplete = false;
		$('html,body').animate({scrollTop:0},0);
	};

	$scope.resetForm = function() {
		$sopne.fEventType = -1;
		$scope.fCopyText = "";
		$scope.fDateFrom = "";
		$scope.fDateTo = "";
		$scope.fHourFrom = "";
		$scope.fHourTo = "";
		$scope.selectedSchool = null;
		$scope.selectedRoute = null;
	};

	$scope.downloadReport = function() {
		if($scope.incomplete) {
			return "";
		}
		var dateFrom = $scope.fDateFrom;
		if($scope.fHourFrom) {
			dateFrom = dateFrom + "T" + $scope.fHourFrom; 
		}
		var dateTo = $scope.fDateTo;
		if($scope.fHourTo) {
			dateTo = dateTo + "T" + $scope.fHourTo;
		}
		
		var urlDownload = "report/attendance/" + $scope.profile.ownerId
		+ "?schoolId=" + $scope.selectedSchool.objectId
		+ "&routeId=" + $scope.selectedRoute.objectId
		+ "&dateFrom=" + dateFrom + "&dateTo=" + dateTo;
		
		return urlDownload;
	};
	
	$scope.getRouteName = function(item) {
		var dateFrom = moment(item.from);
		var dateTo = moment(item.to);
		return item.name + " [" + dateFrom.format('DD/MM/YYYY') + " - " + dateTo.format('DD/MM/YYYY') + "]";
	};
	
	$scope.getEventTimestamp = function(item) {
		var day = moment(item.timestamp);
		var result = day.format('DD/MM/YYYY, hh:mm:ss'); 
		return result;
	};
	
	$scope.copyItem = function(item) {
		return JSON.stringify(item);
	};

	$scope.$watch('selectedSchool',function() {$scope.test();}, true);
	$scope.$watch('selectedRoute',function() {$scope.test();}, true);
	$scope.$watch('fDateFrom',function() {$scope.test();}, true);
	$scope.$watch('fDateTo',function() {$scope.test();}, true);

	$scope.test = function() {
		if(($scope.selectedSchool == null) ||
		($scope.selectedRoute == null) ||		
		(!$scope.fDateFrom) || 
		(!$scope.fDateTo)) {
			$scope.incomplete = true;
		} else {
			$scope.incomplete = false;
		}
	};

	$scope.findByObjectId = function(array, id) {
    for (var d = 0, len = array.length; d < len; d += 1) {
      if (array[d].objectId === id) {
          return array[d];
      }
    }
    return null;
	};

	$scope.findIndex = function(array, id) {
		for (var d = 0, len = array.length; d < len; d += 1) {
			if (array[d].objectId === id) {
				return d;
			}
		}
		return -1;
	};

});

searchTableApp.directive('datepicker', function() {
  return {
    restrict: 'A',
    require : 'ngModel',
    link : function (scope, element, attrs, ngModelCtrl) {
    	$(function(){
    		element.datepicker("option", $.datepicker.regional['it']);
    		element.datepicker({
    			showOn: attrs['showon'],
          buttonImage: "lib/jqueryui/images/ic_calendar.png",
          buttonImageOnly: false,
          buttonText: "Calendario",
          dateFormat: attrs['dateformat'],
          minDate: "-1Y",
          maxDate: "+2Y",
          onSelect:function (date) {
          	scope.$apply(function () {
          		ngModelCtrl.$setViewValue(date);
            });
          }
        });
      });
    }
  }
});
