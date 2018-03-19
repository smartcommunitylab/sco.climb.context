angular.module('consoleControllers.leg')

.controller('CreateNewMultimediaElementDialogCtrl', function ($scope, uploadImageOnImgur, addElementsFunction, saveFunction) {
    
    $scope.newMedia = {type: 'image'};

    $scope.$modalSuccess = function() {
        if(!$scope.newMedia.link) {     // controlla che sia stato inserito un URL
            alert("Non è stato inserito un indirizzo valido.");
        }
        else {
            if ($scope.newMedia.type) {
                addElementsFunction($scope.newMedia.name, $scope.newMedia.link, $scope.newMedia.type);
                $scope.$modalClose();
                saveFunction();
            }
            else {
                alert("Errore: il tipo dell'oggetto non è un tipo valido.");
            }
        }
    }
    
    $scope.uploadPic = function (file) {
        uploadImageOnImgur(file).success(function (response) {
            $scope.newMedia.link = response.data.link;
            $scope.file = null;
        });
    };
});