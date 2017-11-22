angular.module('consoleControllers.schools', ['ngSanitize'])

// Schools controller
.controller('SchoolsListCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'school';

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

.controller('SchoolCtrl', function ($scope, $stateParams, $rootScope, $location, $timeout, DataService, MainDataService, createDialog) {
    $scope.$parent.mainView = 'school'; 

    $scope.initController = function() {
        if ($scope.currentSchool) { //edit school
            $scope.saveData = DataService.editData;
            DataService.getData('route', 
                    $stateParams.idDomain, 
                    $stateParams.idInstitute, 
                    $stateParams.idSchool).then(
                function(response) {
                    console.log('Caricamento delle linee a buon fine.');
                    $scope.currentSchool.lines = response.data;
                }, function() {
                    alert('Errore nel caricamento delle linee.');
                }
            );
            $scope.$broadcast('schoolLoaded');
        } else {
            $scope.currentSchool = {
                name: '',
                address: '',
                instituteId: $stateParams.idInstitute,
                ownerId: $stateParams.idDomain,
            }
            $scope.saveData = DataService.saveData;
        }
    }

    if ($stateParams.idSchool) {
        MainDataService.getDomains().then(function (response) {
            MainDataService.getInstitutes($stateParams.idDomain).then(function (response) {
                MainDataService.getSchools($stateParams.idInstitute).then(function (response) {
                    var schools = response.data;
                    for (var i = 0; i < schools.length && !$scope.currentSchool; i++) {
                        if (schools[i].objectId == $stateParams.idSchool) $scope.currentSchool = schools[i];
                    }
                    $scope.initController();
                });
            });
        });
    } else { //new school
        $scope.initController();
    }

    $scope.newClass = '';
    $scope.addNewClass = function()
    {
        $scope.currentSchool.classes.push($scope.newClass);
        $scope.newClass = '';
    };
    $scope.deleteClass = function(index)
    {
        $scope.currentSchool.classes.splice(index,1);
    };
    $scope.sortClasses = function()
    {
        $scope.currentSchool.classes.sort();
    };
    
    $scope.isNewSchool = function() {
    	return ($stateParams.idSchool == null || $stateParams.idSchool == '');
    };
    
    $scope.getSchoolName = function() {
        if (!$scope.currentSchool) return ''; 
    	if(!$scope.isNewSchool()) {
    		return $scope.currentSchool.name;
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
    			"ownerId": $scope.currentSchool.ownerId,
    			"instituteId": $scope.$parent.selectedInstitute.objectId,
    			"schoolId": $scope.currentSchool.objectId,
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
            $scope.saveData('school', $scope.currentSchool).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function() {
                    console.log('Salvataggio dati a buon fine.');
                    $location.path('schools-list');
                    $scope.$parent.schools.push($scope.currentSchool);
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

        if (invalidFields.length > 0 /*|| $scope.currentSchool.classes.length === 0*/)      // da valutare implementazione classi
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
    

    $scope.initController = function() {
        DataService.getData('children',
                $stateParams.idDomain, 
                $stateParams.idInstitute, 
                $stateParams.idSchool).then(
        function(response) {
            $scope.currentSchool.children = response.data;
            $scope.currentSchool.children.sort(compareChild);
            console.log('Caricamento degli scolari a buon fine.');
        }, function() {
            alert('Errore nel caricamento degli scolari.');
        }
        );
    }


    if ($scope.currentSchool) {
        $scope.initController();
    } else {
        $scope.$on('schoolLoaded', function(e) {  
            $scope.initController();        
        });
    }

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
    
    $scope.remove = function (idChild) {
        createDialog('templates/modals/delete-child.html',{
            id : 'delete-child-dialog',
            title: 'Attenzione!',
            success: { 
            	label: 'Conferma', 
            	fn: function() {
                DataService.removeData('child', $rootScope.schools[$scope.currentSchool].children[idChild]).then(
                    function() {
                        console.log('Cancellazione dello scolaro a buon fine.');
                        $rootScope.schools[$scope.currentSchool].children.splice(idChild, 1);
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
})

.controller('VolunteerListCtrl', function ($scope, $stateParams, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'volunteer-list';

    $scope.initController = function() {
        DataService.getData('volunteers',
                $stateParams.idDomain, 
                $stateParams.idInstitute, 
                $stateParams.idSchool).then(
            function(response) {
                $scope.currentSchool.volunteers = response.data;
                $scope.currentSchool.volunteers.sort(compareVolunteer);
                console.log('Caricamento dei volontari a buon fine.');
            }, function() {
                alert('Errore nel caricamento dei volontari.');
            }
        );
    }    

    if ($scope.currentSchool) {
        $scope.initController();
    } else {
        $scope.$on('schoolLoaded', function(e) {  
            $scope.initController();        
        });
    }

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
    
    $scope.remove = function (idVolunteer) {
        createDialog('templates/modals/delete-volunteer.html',{
            id : 'delete-volunteer-dialog',
            title: 'Attenzione!',
            success: { 
            	label: 'Conferma', 
            	fn: function() {
                DataService.removeData('volunteer', $scope.volunteers[idVolunteer]).then(
                    function() {
                        console.log('Cancellazione del volontario a buon fine.');
                        $scope.volunteers.splice(idVolunteer, 1);
                    }, function() {
                        alert('Errore nella cancellazione del volontario.');
                    }
                );
            	} 
            }
        });
    };
    
  	function compareVolunteer(a,b) {
  		var aName = a.name;
  		var bName = b.name;
  	  if (aName < bName)
  	    return -1;
  	  if (aName > bName)
  	    return 1;
  	  return 0;
  	}

})

.controller('VolunteerCtrl', function ($scope, $stateParams, $rootScope, $timeout, $window, createDialog, DataService) {
	$scope.$parent.selectedTab = 'volunteer-list';
	$scope.selectedVolunteer = {};
	initData();
	
	function initData() {
		if ($stateParams.idVolunteer) {
			$scope.selectedVolunteer = $rootScope.schools[$scope.currentSchool].volunteers[$stateParams.idVolunteer];
			$scope.saveData = DataService.editData;
		} else {
			$scope.selectedVolunteer = {
          "name": '',
          "address": '',
          "phone": '',
          "wsnId": '',
          "cf": '',
          "ownerId": $rootScope.schools[$scope.currentSchool].ownerId,
          "instituteId": $rootScope.schools[$scope.currentSchool].instituteId,
          "schoolId": $rootScope.schools[$scope.currentSchool].objectId
      };
      $scope.saveData = DataService.saveData;
		}
	}
	
  $scope.isNewVolunteer = function() {
  	return ($stateParams.idVolunteer == null || $stateParams.idVolunteer == '');
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
  		$scope.saveData('volunteer', $scope.selectedVolunteer).then(
				function(response) {
					console.log('Volontario salvato.');
					$window.history.back();
				},
				function() {
          alert('Errore nel salvataggio del volontario.');
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


