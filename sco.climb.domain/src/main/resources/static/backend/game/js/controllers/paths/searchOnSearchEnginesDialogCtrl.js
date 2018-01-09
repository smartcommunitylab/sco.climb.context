angular.module('consoleControllers.leg')

.controller('SearchOnSearchEnginesDialogCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService) {
    

    $scope.searchOnEngine = function() {
        if ($scope.searchtype == 'wikipedia') {
            //https://it.wikipedia.org/w/api.php?action=opensearch&search=Castello%20buonconsiglio&limit=1&namespace=0&format=jsonfm
            DataService.searchOnWikipedia($scope.searchtext).then(
                    function(response) {
                        console.log(response);
                    }, function() {
                    }
            );
        } else if ($scope.searchtype == 'video') {
            //https://www.googleapis.com/youtube/v3/search?part=snippet&q=Castello%20Buonconsiglio&maxResults=10
        } else if ($scope.searchtype == 'image') {
            
        }
    }
});