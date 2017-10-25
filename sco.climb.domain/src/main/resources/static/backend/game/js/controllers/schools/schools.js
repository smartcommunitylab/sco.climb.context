angular.module('consoleControllers.schools', ['ngSanitize'])

// Schools controller
.controller('SchoolsListCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'school';
    if($scope.$parent.selectedOwner && $scope.$parent.selectedInstitute && $scope.$parent.schoolsModified)
        $scope.$parent.loadData($scope.$parent.mainView);

    $scope.delete = function (schoolIndex) {
        createDialog('templates/modals/delete-school.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData($scope.$parent.mainView, $rootScope.schools[schoolIndex]).then(
                    function() {
                        console.log("Rimozione effettuata con successo.");
                        $scope.$parent.loadData($scope.$parent.mainView);
                    }, function() {
                        alert("Errore nella richiesta.");
                    });
            } }
        });
    };
})

.controller('SchoolCtrl', function ($scope, $stateParams, $rootScope, $location, $timeout, DataService, createDialog) {
    $scope.$parent.mainView = 'school';

    if ($stateParams.idSchool)        // controlla se si sta modificando una scuola esistente
    {
        $scope.currentSchool = Number($stateParams.idSchool);
        $scope.saveData = DataService.editData;
        DataService.getData('route', 
        		$rootScope.schools[$scope.currentSchool].ownerId, 
        		$scope.$parent.selectedInstitute.objectId, 
        		$rootScope.schools[$scope.currentSchool].objectId).then(
            function(response) {
                console.log('Caricamento delle linee a buon fine.');
                $rootScope.schools[$scope.currentSchool].lines = response.data;
            }, function() {
                alert('Errore nel caricamento delle linee.');
            }
        );
    }
    else
    {
        $scope.currentSchool = $rootScope.schools.push({        // variabile che indica la posizione nell'array della scuola che sta venendo editata
            name: '',
            address: '',
            instituteId: $scope.$parent.selectedInstitute.objectId,
            ownerId: $scope.$parent.selectedOwner,
        })-1;
        $scope.saveData = DataService.saveData;
    }

    /*function makeid() {         // data e ora di creazione + codice random di 4 caratteri -- GENERATI DAL SERVER, possono tornare utili per testing in locale
        var id = "";
        var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        var date = new Date();
        date = $filter('date')(date,'ddMMyy-hhmmss','+0100');

        for (var i = 0; i < 4; i++)
            id += possible.charAt(Math.floor(Math.random() * possible.length));

        return date + '_' + id;
    }*/

    $scope.newClass = '';
    $scope.addNewClass = function()
    {
        $rootScope.schools[$scope.currentSchool].classes.push($scope.newClass);
        $scope.newClass = '';
    };
    $scope.deleteClass = function(index)
    {
        $rootScope.schools[$scope.currentSchool].classes.splice(index,1);
    };
    $scope.sortClasses = function()
    {
        $rootScope.schools[$scope.currentSchool].classes.sort();
    };
    
    $scope.isNewSchool = function() {
    	return ($stateParams.idSchool == null || $stateParams.idSchool == '');
    };
    
    $scope.getSchoolName = function() {
    	if(!$scope.isNewSchool()) {
    		return $rootScope.schools[$scope.currentSchool].name;
    	} else {
    		return "";
    	} 
    };
    
    $scope.uploadFile = function() {
    	var fileInput = document.getElementById('upload-file');
    	if(fileInput.files.length == 0) {
    		alert('Scegliere un file da caricare');
    		return;
    	}
    	var file = fileInput.files[0];
    	var formData = new FormData();
    	formData.append('file', file);
    	var element = {
    			"ownerId": $rootScope.schools[$scope.currentSchool].ownerId,
    			"instituteId": $scope.$parent.selectedInstitute.objectId,
    			"schoolId": $rootScope.schools[$scope.currentSchool].objectId,
    			"formdata": formData 	
    	};
    	DataService.uploadFile(element).then(
          function(response) {
              console.log('Caricamento dati a buon fine.');
              $location.path('schools-list');
          }, function() {
              alert('Errore nel caricamento delle linee.');
          }
      );
    }

    // Save the changes made to the path
    $scope.save = function () {
        if (checkFields())
        {
            $scope.saveData($scope.$parent.mainView, $rootScope.schools[$scope.currentSchool]).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function() {
                    console.log('Salvataggio dati a buon fine.');
                    $location.path('schools-list');
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        }
        else
        {
            $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco e di aver aggiunto almeno una classe e una linea pedibus.";
            $timeout(function () {
                $rootScope.modelErrors = '';
            }, 5000);
        }
    };

    // Controlla se ci sono campi vuoti e se non ci sono classi
    function checkFields() {
        var isValidate = true;
        var invalidFields = $('.ng-invalid');

        if (invalidFields.length > 0 /*|| $rootScope.schools[$scope.currentSchool].classes.length === 0*/)      // da valutare implementazione classi
            isValidate = false;

        return isValidate;
    }

    // Back without saving changes
    $scope.back = function() {
        createDialog('templates/modals/back.html',{
            id : 'back-dialog',
            title: 'Sei sicuro di voler uscire senza salvare?',
            success: { label: 'Conferma', fn: function() {$location.path('schools-list');} }
        });
    };
})

.controller('SchoolInfoCtrl', function ($scope) {
    $scope.$parent.selectedTab = 'info';
})

.controller('LinesListCtrl', function ($scope, $stateParams, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'lines-list';
    $scope.enableOrder = false;
    if (!$stateParams.idSchool)        // controlla se si sta modificando una scuola esistente
    {
        createDialog('templates/modals/newschool-err.html',{
            id : 'newschoolerr-dialog',
            title: 'Attenzione!',
            success: { label: 'Torna indietro', fn: function() {
                window.history.back();
            } },
            footerTemplate: '<button class="btn btn-danger" ng-click="$modalSuccess()">{{$modalSuccessLabel}}</button>'
        });
    }

    $scope.remove = function (idLine) {
        createDialog('templates/modals/delete-line.html',{
            id : 'delete-line-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData('route', $rootScope.schools[$scope.currentSchool].lines[idLine]).then(
                    function() {
                        console.log('Cancellazione della tappa a buon fine.');
                    }, function() {
                        alert('Errore nella cancellazione della tappa.');
                    }
                );
                DataService.getData('route',
                		$rootScope.schools[$scope.currentSchool].ownerId, 
                		$scope.$parent.selectedInstitute.objectId, 
                		$rootScope.schools[$scope.currentSchool].objectId).then(
                    function(response) {
                        console.log('Caricamento delle linee a buon fine.');
                        $rootScope.schools[$scope.currentSchool].lines = response.data;
                    }, function() {
                        alert('Errore nel caricamento delle linee.');
                    }
                );
            } }
        });
    };
})

.controller('ChildrenCtrl', function ($scope, $stateParams, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'children-list';
    $scope.children = [];
    if (!$stateParams.idSchool)        // controlla se si sta modificando una scuola esistente
    {
        createDialog('templates/modals/newschool-err.html',{
            id : 'newschoolerr-dialog',
            title: 'Attenzione!',
            success: { label: 'Torna indietro', fn: function() {
                window.history.back();
            } },
            footerTemplate: '<button class="btn btn-danger" ng-click="$modalSuccess()">{{$modalSuccessLabel}}</button>'
        });
    }
    
    $scope.init = function() {
      DataService.getData('children',
      		$rootScope.schools[$scope.currentSchool].ownerId, 
      		$rootScope.schools[$scope.currentSchool].instituteId, 
      		$rootScope.schools[$scope.currentSchool].objectId).then(
          function(response) {
          	$rootScope.schools[$scope.currentSchool].children = response.data;
          	$rootScope.schools[$scope.currentSchool].children.sort(compareChild);
          	$scope.children = $rootScope.schools[$scope.currentSchool].children; 
            console.log('Caricamento degli scolari a buon fine.');
          }, function() {
            alert('Errore nel caricamento degli scolari.');
          }
      );    	
    };

    $scope.remove = function (idChild) {
        createDialog('templates/modals/delete-child.html',{
            id : 'delete-child-dialog',
            title: 'Attenzione!',
            success: { 
            	label: 'Conferma', 
            	fn: function() {
                DataService.removeData('child', $scope.children[idChild]).then(
                    function() {
                        console.log('Cancellazione dello scolaro a buon fine.');
                        $scope.children.splice(idChild, 1);
                    }, function() {
                        alert('Errore nella cancellazione dello scolaro.');
                    }
                );
            	} 
            }
        });
    };
    
  	$scope.getChildName = function(child) {
  		return child.name + " " + child.surname;
  	}

  	function compareChild(a,b) {
  		var aName = a.name + " " + a.surname;
  		var bName = b.name + " " + b.surname;
  	  if (aName < bName)
  	    return -1;
  	  if (aName > bName)
  	    return 1;
  	  return 0;
  	}
  	
  	$scope.init();
})

.controller('ChildCtrl', function ($scope, $stateParams, $rootScope, $timeout, $window, createDialog, DataService) {
	$scope.$parent.selectedTab = 'children-list';
	$scope.selectedChild = {};
	initData();
	
	function initData() {
		if ($stateParams.idChild) {
			$scope.selectedChild = $rootScope.schools[$scope.currentSchool].children[$stateParams.idChild];
			$scope.saveData = DataService.editData;
		} else {
			$scope.selectedChild = {
          "name": '',
          "surname": '',
          "parentName": '',
          "phone": '',
          "classRoom": '',
          "wsnId": '',
          "ownerId": $rootScope.schools[$scope.currentSchool].ownerId,
          "instituteId": $rootScope.schools[$scope.currentSchool].instituteId,
          "schoolId": $rootScope.schools[$scope.currentSchool].objectId
      };
      $scope.saveData = DataService.saveData;
		}
	}
	
  $scope.isNewChild = function() {
  	return ($stateParams.idChild == null || $stateParams.idChild == '');
  }
  
	$scope.getChildName = function(child) {
		return child.name + " " + child.surname;
	}
	
  // Exit without saving changes
  $scope.back = function () {
      createDialog('templates/modals/back.html',{
          id : 'back-dialog',
          title: 'Sei sicuro di voler uscire senza salvare?',
          success: { label: 'Conferma', fn: function() {$window.history.back();} }
      });
  }
  
  $scope.save = function () {  
  	if (checkFields()) {
  		$scope.saveData('child', $scope.selectedChild).then(
				function(response) {
					console.log('Scolaro salvato.');
					$window.history.back();
				},
				function() {
          alert('Errore nel salvataggio dello scolaro.');
				}
  		);
  	}
  }
  
  function checkFields() {
    var allCompiled = true;
    var invalidFields = $('.ng-invalid');
    // Get all inputs
    if (invalidFields.length > 0) {
        $rootScope.modelErrors = "Errore! Controlla di aver compilato tutti i campi indicati con l'asterisco.";
        $timeout(function () {
            $rootScope.modelErrors = '';
        }, 5000);
        allCompiled = false;
    }
    return allCompiled;
  }
});


