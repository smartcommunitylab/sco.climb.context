angular.module('consoleControllers.leg')

.controller('CreateNewMultimediaElementDialogCtrl', function ($scope, addElementsFunction, saveFunction, dataService, leg) {
	$scope.errorMsg;
    $scope.newMedia = {type: ''};
	$scope.loading = false;
	$scope.publicLink = true;
	$scope.sharable =  true;
	$scope.tagListToggle = function(){
        $('.wrapper .list').slideToggle('fast');
	}
	$scope.tags= [];
	$scope.classes=[];
	$scope.schoolYears=[];
	$scope.subjects=[];
	dataService.getMultimediaContentTags(leg.ownerId, leg.pedibusGameId).then(
		function(response) {
			console.log("tags::",response)
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
	$scope.getSelectedtags = function(tag){
        return tag.selected;
	};
	$scope.changePublicLink = function(publicLink){
		$scope.publicLink = publicLink;
		console.log("publicLink",$scope.publicLink)
	}
	$scope.changeSharable = function(sharable){
		$scope.sharable = sharable;
		console.log("sharable",$scope.sharable)
	}
    $scope.resetError = function() {
    	$scope.errorMsg = undefined;
    }
    
    $scope.$modalSuccess = function() {
		console.log("class after success::",$scope.classes)
        if(!$scope.newMedia.link) {     // controlla che sia stato inserito un URL
            $scope.errorMsg = "Non è stato inserito un indirizzo valido.";
        }
        else {
            if ($scope.newMedia.type) {
				// $scope.newMedia.tags=$scope.getSelectedtags($scope.tags);
				$scope.newMedia.tags=[];
				$scope.tags.forEach(element => {
					if(element.selected){
						$scope.newMedia.tags.push(element.name)
					}
				});
				var classes=[],subjects=[],schoolYears=[];
				$scope.classes.forEach(element => {
					if(element.selected){
						classes.push(element.class)
					}
				});
				$scope.subjects.forEach(element => {
					if(element.selected){
						subjects.push(element.subject)
					}
				});
				$scope.schoolYears.forEach(element => {
					if(element.selected){
						schoolYears.push(element.schoolYear)
					}
				});
				addElementsFunction($scope.newMedia.name, $scope.newMedia.link, $scope.newMedia.type, 
					classes, subjects, schoolYears, $scope.publicLink, $scope.sharable);
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
			}
		);
    };
});