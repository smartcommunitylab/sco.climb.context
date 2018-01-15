angular.module('consoleControllers.leg')

.controller('SearchOnContentRepositoryDialogCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, addElementsFunction, position) {
    
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
                                //try to find thumbnail from youtube
                                var regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
                                var match = element.link.match(regExp);
                                if (match && match[2].length == 11) {
                                    element.referenceImg = "https://img.youtube.com/vi/" + match[2] + "/0.jpg";
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