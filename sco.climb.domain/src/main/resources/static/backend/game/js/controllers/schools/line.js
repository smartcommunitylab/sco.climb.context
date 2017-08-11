angular.module('consoleControllers.line', [])

// Edit the line for the selected school
.controller('LineCtrl', function ($scope, $stateParams, $rootScope, $window, $timeout, DataService, 
		uploadImageOnImgur, drawMapLine, createDialog) {
    $scope.$parent.selectedTab = 'lines';
    var currentSchool = $scope.$parent.currentSchool;
    $scope.enableOrder = false;
    
    $scope.dateFormat = 'dd/MM/yyyy';
    $scope.startDate = new Date();
    $scope.endDate = new Date();
    $scope.isCalendarOpen = [false, false];
    $scope.minDate = new Date(1970, 1, 1);
    
    InitLinePage();
    
    $scope.isNewLine = function() {
    	return ($stateParams.idLine == null || $stateParams.idLine == '');
    };

    // Create $scope.line variable and init the map
    function InitLinePage() {
      if ($stateParams.idLine)     // se sto modificando una linea già esistente
      {
         	$scope.line = angular.copy($rootScope.schools[currentSchool].lines[$stateParams.idLine]);
          $scope.startDate.setTime($scope.line.from);
          $scope.endDate.setTime($scope.line.to);
          $scope.saveData = DataService.editData;
          DataService.getData('stops',
          		$rootScope.schools[$scope.currentSchool].ownerId, 
          		$rootScope.schools[$scope.currentSchool].instituteId, 
          		$rootScope.schools[$scope.currentSchool].objectId,
          		$scope.line.objectId).then(
              function(response) {
              	$scope.line['stops'] = response.data; 
                console.log('Caricamento delle linee a buon fine.');
                drawMapLine.createMap('map-line', $scope.line.stops);
              }, function() {
                alert('Errore nel caricamento delle linee.');
              }
          );
      }
      else
      {
        $scope.line = {
            "name": '',
            "instituteId": $scope.$parent.selectedInstitute.objectId,
            "ownerId": $scope.$parent.selectedOwner,
            "schoolId": $rootScope.schools[currentSchool].objectId,
            "from": '',             
            "to": '',
            "distance": '',         
            "stops": []
        };
        $scope.saveData = DataService.saveData;
        drawMapLine.createMap('map-line', $scope.line.stops);
      }
    }

    $scope.sortableOptions = {
        handle: ' .handle',
        axis: 'y',
        stop: function(){$scope.updatePath()}
    };

    $scope.$on('stopMarkerPosChanged', function(event, stopNumber, newLat, newLng) {     // listener del broadcast che indica il cambiamento della posizione del marker
        $scope.line.stops[stopNumber].geocoding[1] = newLat;
        $scope.line.stops[stopNumber].geocoding[0] = newLng;
        if(!$scope.$$phase)
            $scope.$apply();        // forzo il controllo per l'aggiornamento dei campi
        drawMapLine.updateMarkers($scope.line.stops);
    });

    // Update the marker position when the user change coordinates
    $scope.updatePath = function () {
        drawMapLine.updateMarkers($scope.line.stops);
    };

    $scope.save = function () {    
        if (checkFields()) {
        	$scope.line.from = $scope.startDate.getTime();
        	$scope.line.to = $scope.endDate.getTime();
          $scope.saveData('route', $scope.line).then(
                function(response) {
                    console.log('Linea salvata.');
                    $scope.line['objectId'] = response.data.objectId;
                    if($scope.line.stops.length > 0) {
                      //create stops
                    	for (i = 0; i < $scope.line.stops.length; i++) { 
                    		$scope.line.stops[i].position = i;
                    	}
                      var routeModel = {
                      		"routeId": '',
                      		"ownerId": '',
                      		"stops": []
                      }
                      routeModel['ownerId'] = $scope.line.ownerId;
                      routeModel['routeId'] = $scope.line.objectId;
                      routeModel['stops'] = $scope.line.stops;
                      $scope.saveData('stops', routeModel).then(
                    		function(response) {
                          // riaggiorno la lista delle linee
                          DataService.getData('route', 
                          	$rootScope.schools[$scope.currentSchool].ownerId, 
                          	$rootScope.schools[$scope.currentSchool].instituteId, 
                          	$rootScope.schools[$scope.currentSchool].objectId).then(       
                              function(response) {
                                  console.log('Caricamento delle linee a buon fine.');
                                  $rootScope.schools[$scope.currentSchool].lines = response.data;
                                  $window.history.back();
                              }, function() {
                                  alert('Errore nel caricamento delle linee.');
                              }
                          );
                    		},
                    		function(error) {
                    			alert('Errore nel salvataggio delle fermate.');
                    		}
                    	);
                    } else {
                    	$rootScope.schools[currentSchool].lines[$stateParams.idLine] = angular.copy($scope.line);
                    	$window.history.back();
                    }
                }, function() {
                    alert('Errore nel salvataggio della linea.');
                }
          );
        }
    };
    
    function checkFields() {
        var allCompiled = true;
        var invalidFields = $('.ng-invalid');
        // Get all inputs
        if (invalidFields.length > 0) {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e di aver inserito almeno due fermate.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
            allCompiled = false;
        }
        return allCompiled;
    }

    $scope.addStop = function()
    {
        var stopsArray = $scope.line.stops;
        var center = drawMapLine.getMapCenter();
        stopsArray.push(
        	{
        		geocoding: [center.lng(), center.lat()]
        	}
        );
        $scope.updatePath();
    };

    $scope.deleteStop = function(index)
    {
        $scope.line.stops.splice(index, 1);
        $scope.updatePath();
    };

    // Exit without saving changes
    $scope.back = function () {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$window.history.back();} }
        });
    };
    
    $scope.assignPassengers = function(index) {
      DataService.getData('children',
      		$rootScope.schools[$scope.currentSchool].ownerId, 
      		$rootScope.schools[$scope.currentSchool].instituteId, 
      		$rootScope.schools[$scope.currentSchool].objectId).then(
          function(response) {
          	$scope.children = response.data; 
            console.log('Caricamento degli scolari a buon fine.');
          	createDialog('templates/modals/assign-passengers.html',
          		{
              	id : 'assign-passengers-dialog',
              	title: 'Assegna i passeggeri alla fermata',
              	controller: 'AssignPassegersModalController'
          		},
          		{
          			stop: $scope.line.stops[index],
          			children: $scope.children
          		}
          	);
          }, function() {
            alert('Errore nel caricamento degli scolari.');
          }
      );
    }; 
})

.controller('AssignPassegersModalController', function($scope, stop, children) {
	$scope.stop = stop;
	$scope.children = children;
});