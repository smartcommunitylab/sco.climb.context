angular.module('consoleControllers.leg')

.controller('SearchOnSearchEnginesDialogCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, leg, addElementsFunction, saveFunction) {
    

    $scope.searchtype = 'image';
    $scope.totalCounter = 0;
    $scope.classes=[];
	$scope.schoolYears=[];
    $scope.subjects=[];
    $scope.selected;
    $scope.materialeListToggle = function(dropdownID){
        // $('.wrapper .list').slideToggle('fast');
        $('#materiale'+dropdownID).slideToggle('fast');
    }
    $scope.classListToggle = function(dropdownID){
        // $('.wrapper .list').slideToggle('fast');
        $('#classe'+dropdownID).slideToggle('fast');
	}
	DataService.getMultimediaContentTags(leg.ownerId, leg.pedibusGameId).then(
		function(response) {
			console.log("tags:",response)
			//$scope.classes=response.data.classes;
			angular.forEach(response.data.classes, function(value, key){
				$scope.classes.push({class:value,selected:false});
            });
            
            // $scope.schoolYears=response.data.schoolYears;
			angular.forEach(response.data.schoolYears, function(value, key){
				$scope.schoolYears.push({schoolYear:value,selected:false});
            });

			// $scope.subjects=response.data.subjects;
			angular.forEach(response.data.subjects, function(value, key){
				$scope.subjects.push({subject:value,selected:false});
            });
		},function(error) {
			console.log('Errore :' , error.data.errorMsg);
		}
	);
    $scope.searchOnEngine = function(searchtext,searchtype) {
        $scope.searchtext=searchtext;
        $scope.searchtype=searchtype;
        console.log("searchtext::",$scope.searchtext)
        if (!$scope.searchtext) return;
        $scope.resetResults();
        if ($scope.searchtype == 'wikipedia' && !$scope.wikiResults) {            
            $scope.changePage(undefined);
        } else if ($scope.searchtype == 'video' && !$scope.ytResults) {
            $scope.changePage(undefined);
        } else if ($scope.searchtype == 'image' && !$scope.imageResults) {            
            $scope.changePage(undefined);
        }
    }

    $scope.changePage = function(pageToken) {
        if ($scope.searchtype == 'wikipedia') {
            //in this case pageToken is an offset of search results
            DataService.searchOnWikipedia($scope.searchtext, pageToken).then(
                function(response) {
                		$scope.resetResults();
                    $scope.wikiResults = response.data.query.pages;
                    for(var page in $scope.wikiResults){
                        $scope.wikiResults[page].link = 'https://it.wikipedia.org/wiki/' + $scope.wikiResults[page].title; 
                    }
                    $scope.prevPageToken = response.data['query-continue'].search.gsroffset - 20;
                    if ($scope.prevPageToken < 0) $scope.prevPageToken = -1;
                    $scope.nextPageToken = response.data['query-continue'].search.gsroffset;
                    console.log(response);
                		var el = document.getElementById('wikiContentList');
                		el.scrollTop = 0;
                }, function() {
                }
            );
        } else if ($scope.searchtype == 'video') {
            DataService.searchOnYoutube($scope.searchtext, pageToken).then(
                function(response) {
                    $scope.resetResults();
                    $scope.ytResults = response.data.items;
                    $scope.prevPageToken = response.data.prevPageToken;
                    if (!$scope.prevPageToken) $scope.prevPageToken = -1; //used to generalize pagination controls
                    $scope.nextPageToken = response.data.nextPageToken;
                    console.log(response);
              			var el = document.getElementById('videoContentList');
              			el.scrollTop = 0;
                }, function() {
                }
            );
        } else if ($scope.searchtype == 'image') {
            //in this case pageToken is an offset of search results
            DataService.searchOnImages($scope.searchtext, pageToken).then(
                function(response) {
                    $scope.resetResults();
                    $scope.imageResults = response.data.items;
                    if (response.data.queries.previousPage) {
                        $scope.prevPageToken = response.data.queries.previousPage[0].startIndex;
                    } else {
                        $scope.prevPageToken = -1; //used to generalize pagination controls
                    }
                    if (response.data.queries.nextPage) {
                        $scope.nextPageToken = response.data.queries.nextPage[0].startIndex;
                    }
                    console.log(response);
                    var el = document.getElementById('imageContentList');
                    el.scrollTop = 0;
                    if($scope.classes){
                        $scope.imageResults.forEach(e=>{
                            e.classes=angular.copy($scope.classes);
                        })
                    }
                    if($scope.schoolYears){
                        $scope.imageResults.forEach(e=>{
                            e.schoolYears=angular.copy($scope.schoolYears);
                        })
                    }
                    if($scope.subjects){
                        $scope.imageResults.forEach(e=>{
                            e.subjects=angular.copy($scope.subjects);
                        })
                    }
                }, function() {
                }
            );
        }
    }

    $scope.$modalSuccess = function() {
        if ($scope.searchtype == 'wikipedia') {
            for(key in $scope.wikiResults){
                var element = $scope.wikiResults[key];
                if (element.selectedToAdd) {
                    addElementsFunction(element.title, element.link, 'link');
                }
            }
        } else if ($scope.searchtype == 'video') {
            $scope.ytResults.forEach(element => {
                if (element.selectedToAdd) {
                    addElementsFunction(element.snippet.title, 'https://www.youtube.com/watch?v=' + element.id.videoId, 'video');
                }
            });
        } else if ($scope.searchtype == 'image') {            
            $scope.imageResults.forEach(element => {
                if (element.selectedToAdd) {
                    var selectedClasses=[];
                    element.classes.forEach(e => {
                        if(e.selected){
                            selectedClasses.push(e.class)
                        }
                    });

                    var selectedSchoolYears=[];
                    element.schoolYears.forEach(e=>{
                        if(e.selected){
                            selectedSchoolYears.push(e.schoolYear)
                        }
                    });

                    var selectedSubjects=[];
                    element.subjects.forEach(e => {
                        if(e.selected){
                            selectedSubjects.push(e.subject);
                        }
                    });
                    addElementsFunction(element.title, element.link, 'image', selectedClasses, selectedSubjects, selectedSchoolYears);
                    saveFunction();
                }
            });
        }
        $scope.$modalClose();
    }
    $scope.resetResults = function() {
        $scope.wikiResults = undefined;
        $scope.ytResults = undefined;
        $scope.imageResults = undefined;
        $scope.totalCounter = 0;
        $scope.$modalSuccessLabel = "Aggiungi " + $scope.totalCounter + " elementi";
    }
    $scope.updateTotalCounter = function(state) {
        if (state) {
            $scope.totalCounter++;
        } else {
            $scope.totalCounter--;
        }
        $scope.$modalSuccessLabel = "Aggiungi " + $scope.totalCounter + " elementi";
    }
    $scope.checkImage = function(image) {
        image.selectedToAdd = !image.selectedToAdd;
        $scope.updateTotalCounter(image.selectedToAdd);
    }
});
