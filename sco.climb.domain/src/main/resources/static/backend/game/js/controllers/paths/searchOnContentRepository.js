angular.module('consoleControllers.leg')

.controller('SearchOnContentRepositoryDialogCtrl', function ($scope, $state, DataService, schoolId, addElementsFunction, saveFunction, position, getYoutubeImageFromLink, leg) {
    
    $scope.totalCounter = 0;
    $scope.searchtype = 'all';
    $scope.searchdistance = null;
    $scope.searchlocalschool = false;
    $scope.classes=[];
	$scope.schoolYears=[];
    $scope.subjects=[];

    $scope.subjectsListToggle = function(dropdownID){
        // $('.wrapper .list').slideToggle('fast');
        $('#'+dropdownID).slideToggle('fast');
    }
    $scope.schoolYearsListToggle = function(dropdownID){
        // $('.wrapper .list').slideToggle('fast');
        $('#'+dropdownID).slideToggle('fast');
	}
    DataService.getMultimediaContentTags(leg.ownerId, leg.pedibusGameId).then(
		function(response) {
			console.log("tags:",response)
			//$scope.classes=response.data.classes;
			angular.forEach(response.data.classes, function(value, key){
				$scope.classes.push({class:value,selected:true});
            });
            
			// angular.forEach(response.data.schoolYears, function(value, key){
			// 	$scope.schoolYears.push({schoolYear:value,selected:true});
            // });
            $scope.schoolYears.push({schoolYear:'123',selected:true});
            $scope.schoolYears.push({schoolYear:'456',selected:true});

			// angular.forEach(response.data.subjects, function(value, key){
			// 	$scope.subjects.push({subject:value,selected:true});
            // });
            $scope.subjects.push({subject:'Geo',selected:true});
            $scope.subjects.push({subject:'Ing',selected:true});
		},function(error) {
			console.log('Errore :' , error.data.errorMsg);
		}
	);
    $scope.searchOnContentRepository = function() {
        //if (!$scope.searchtext) return;
        $scope.resetResults();
        var searchposition = undefined;
        var searchdistance = undefined;
        if($scope.searchdistance) {
        	searchposition = position;
        	searchdistance = $scope.searchdistance;
        }
        DataService.searchOnContentRepository($scope.searchtext, searchposition, searchdistance,
        		$scope.searchlocalschool ? schoolId : undefined, 
        		$scope.searchtype != 'all' ? $scope.searchtype : '').then(
                function(response) {
                    response.data.forEach(element => {
                        switch (element.type) {
                            case 'image':
                                element.referenceImg = element.link;
                                break;
                            case 'video':
                                var youtubeThumbnail = getYoutubeImageFromLink(element.link);
                                if (youtubeThumbnail) {
                                    element.referenceImg = youtubeThumbnail;
                                    element.isYoutubeVideo = true;
                                } else {
                                    element.referenceImg = "img/video.png";
                                }                                
                                break;
                            case 'link':
                                element.referenceImg = "img/link.png";
                                break;
                            case 'file':
                              element.referenceImg = "img/file.png";
                              break;
                        }
                    });          
                    $scope.contentResults = response.data;
                    $scope.noResults = response.data.length == 0
                    console.log("search result:",response); 
                }, function() {
                }
        );
    }
    $scope.$modalSuccess = function() {
        $scope.contentResults.forEach(element => {
            if (element.selectedToAdd) {
                addElementsFunction(element.info[0].name, element.link, element.type);
            }
        });
        $scope.$modalClose();
        saveFunction();
    }
    $scope.resetResults = function() {
        $scope.contentResults = undefined;
        $scope.noResults = false;
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
    $scope.changePage = function(newPageNumber, oldPageNumber) {
    	var el = document.getElementById('contentList');
    	el.scrollTop = 0;
    }

});