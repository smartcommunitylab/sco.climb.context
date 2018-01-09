angular.module('consoleControllers.leg')

.controller('SearchOnSearchEnginesDialogCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, addElementsFunction) {
    

    $scope.searchtype = 'wikipedia';
    $scope.totalCounter = 0;

    $scope.searchOnEngine = function() {
        if (!$scope.searchtext) return;
        if ($scope.searchtype == 'wikipedia' && !$scope.wikiResults) {
            //https://it.wikipedia.org/w/api.php?action=opensearch&search=Castello%20buonconsiglio&limit=1&namespace=0&format=jsonfm
            DataService.searchOnWikipedia($scope.searchtext).then(
                    function(response) {
                        $scope.wikiResults = response.data.query.pages;
                        for(var page in $scope.wikiResults){
                            $scope.wikiResults[page].link = 'https://en.wikipedia.org/wiki/' + $scope.wikiResults[page].title; 
                        }
                        console.log(response);
                    }, function() {
                    }
            );
        } else if ($scope.searchtype == 'video' && !$scope.ytResults) {
            DataService.searchOnYoutube($scope.searchtext).then(
                function(response) {
                    $scope.ytResults = response.data.items;
                    console.log(response);
                }, function() {
                }
            );
        } else if ($scope.searchtype == 'image' && !$scope.imageResults) {
            
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
});