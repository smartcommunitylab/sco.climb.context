angular.module('consoleControllers.leg')

.controller('SearchOnContentRepositoryDialogCtrl', function ($scope, $state, DataService, addElementsFunction, position, getYoutubeImageFromLink) {
    
    $scope.totalCounter = 0;
    $scope.searchtype = 'all';

    $scope.searchOnContentRepository = function() {
        if (!$scope.searchtext) return;
        $scope.resetResults();
        DataService.searchOnContentRepository($scope.searchtext, $scope.searchbyposition ? position: undefined, $scope.searchlocalschool ? $state.idSchool : '', $scope.searchtype != 'all' ? $scope.searchtype : '').then(
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
                        }
                    });          
                    $scope.contentResults = response.data;
                    $scope.noResults = response.data.length == 0
                    console.log(response); 
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

});