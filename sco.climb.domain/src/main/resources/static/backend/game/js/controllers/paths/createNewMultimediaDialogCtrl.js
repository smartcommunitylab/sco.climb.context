angular.module('consoleControllers.leg')

.controller('CreateNewMultimediaElementDialogCtrl', function ($scope, addElementsFunction, 
		saveFunction, dataService, leg) {
		$scope.errorMsg;
    $scope.newMedia = {type: 'image'};
    $scope.loading = false;
    
    $scope.resetError = function() {
    	$scope.errorMsg = undefined;
    }
    
    $scope.$modalSuccess = function() {
        if(!$scope.newMedia.link) {     // controlla che sia stato inserito un URL
            $scope.errorMsg = "Non è stato inserito un indirizzo valido.";
        }
        else {
            if ($scope.newMedia.type) {
				console.log("come $modalSuccess")
                addElementsFunction($scope.newMedia.name, $scope.newMedia.link, $scope.newMedia.type, $scope.newMedia.tags);
                $scope.$modalClose();
                saveFunction();
            }
            else {
            	$scope.errorMsg = "Errore: il tipo dell'oggetto non è un tipo valido.";
            }
        }
    }
    
    $scope.uploadFile = function () {
    	$scope.loading = true;
    	var fileInput = document.getElementById('upload-content-file');
    	if(fileInput.files.length == 0) {
    		$scope.errorMsg = "Scegliere un file da caricare";
    		return;
    	}
    	var file = fileInput.files[0];
    	var formData = new FormData();
    	formData.append('file', file);
    	var element = {
    			"ownerId": leg.ownerId,
    			"pedibusGameId": leg.pedibusGameId,
    			"itineraryId": leg.itineraryId,
    			"legId": leg.objectId,
          "formdata": formData,
    	};
    	dataService.uploadFileContent(element).then(
    			function (response) {
            $scope.newMedia.link = response.data.link;
            $scope.file = null;
            $scope.loading = false;
    			},
    			function (error) {
    				$scope.errorMsg = "Errore di comunicazione con il sistema";
    				$scope.loading = false;
    			});
    };
});