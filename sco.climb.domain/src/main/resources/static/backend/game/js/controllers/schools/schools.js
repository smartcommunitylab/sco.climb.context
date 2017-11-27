angular.module('consoleControllers.schools', ['ngSanitize'])

// Schools controller
.controller('SchoolsListCtrl', function ($scope, $rootScope, DataService, createDialog) {
    $scope.$parent.mainView = 'school';

    $scope.delete = function (school) {
        createDialog('templates/modals/delete-school.html',{
            id : 'delete-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData('school', school).then(
                    function() {
                        console.log("Rimozione effettuata con successo.");
                        $scope.schools.splice($scope.schools.indexOf(school), 1);
                    }, function() {
                        alert("Errore nella richiesta.");
                    });
            } }
        });
    };
})

.controller('SchoolCtrl', function ($scope, $stateParams, $state, $rootScope, $location, $timeout, DataService, MainDataService, createDialog) {
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
                    $scope.$broadcast('schoolLoaded');
                }, function() {
                    alert('Errore nel caricamento delle linee.');
                }
            );
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
                    $scope.schools = response.data;
                    for (var i = 0; i < $scope.schools.length && !$scope.currentSchool; i++) {
                        if ($scope.schools[i].objectId == $stateParams.idSchool) $scope.currentSchool = angular.copy($scope.schools[i]);
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
              $state.go('root.schools-list');
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
                function(response) {
                    console.log('Salvataggio dati a buon fine.');
                    if ($scope.currentSchool.objectId) { //edited
                        for (var i = 0; i < $scope.schools.length; i++) {
                            if ($scope.schools[i].objectId == $scope.currentSchool.objectId) $scope.schools[i] = $scope.currentSchool;
                        }
                    } else {
                        $scope.schools.push(response.data);
                    }
                    $state.go('root.schools-list');
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
            success: { label: 'Conferma', fn: function() {$state.go('root.schools-list');} }
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

    $scope.remove = function (line) {
        createDialog('templates/modals/delete-line.html',{
            id : 'delete-line-dialog',
            title: 'Attenzione!',
            success: { label: 'Conferma', fn: function() {
                DataService.removeData('route', line).then(
                    function() {
                        console.log('Cancellazione della tappa a buon fine.');
                        DataService.getData('route',
                                $scope.currentSchool.ownerId, 
                                $scope.currentSchool.instituteId, 
                                $scope.currentSchool.objectId).then(
                            function(response) {
                                console.log('Caricamento delle linee a buon fine.');
                                $scope.currentSchool.lines = response.data;
                            }, function() {
                                alert('Errore nel caricamento delle linee.');
                            }
                        );
                    }, function() {
                        alert('Errore nella cancellazione della tappa.');
                    }
                );
                
            } }
        });
    };
    $scope.saveOrder = function() {
        if (!$scope.enableOrder) {
            $scope.enableOrder = true;
        } else {
            //TODO: save lines... no API to batch save order?
            DataService.saveData('route', $scope.currentSchool.lines).then(     // reference ad una funzione che cambia se sto creando o modificando un elemento
                function() {
                    console.log('Salvataggio dati a buon fine.');
                    $scope.enableOrder = false;
                }, function() {
                    alert('Errore nella richiesta.');
                }
            );
        }
    }
})

.controller('ChildrenCtrl', function ($scope, $stateParams, $rootScope, createDialog, DataService) {
    $scope.$parent.selectedTab = 'children-list';

    $scope.initController = function() {
        DataService.getData('children',
                $stateParams.idDomain, 
                $stateParams.idInstitute, 
                $stateParams.idSchool).then(
        function(response) {
            response.data.sort(compareChild);
            $scope.currentSchool.children = response.data;
            console.log('Caricamento degli scolari a buon fine.');
        }, function() {
            alert('Errore nel caricamento degli scolari.');
        }
        );
    }


    if (!$stateParams.idSchool) //new school
    {
        createDialog('templates/modals/newschool-err.html',{
            id : 'newschoolerr-dialog',
            title: 'Attenzione!',
            success: { label: 'Torna indietro', fn: function() {
                window.history.back();
            } },
            footerTemplate: '<button class="btn btn-danger" ng-click="$modalSuccess()">{{$modalSuccessLabel}}</button>'
        });
    } else if ($scope.currentSchool) {
        $scope.initController();
    } else {
        $scope.$on('schoolLoaded', function(e) {  
            $scope.initController();        
        });
    }

    
    $scope.remove = function (child) {
        createDialog('templates/modals/delete-child.html',{
            id : 'delete-child-dialog',
            title: 'Attenzione!',
            success: { 
            	label: 'Conferma', 
            	fn: function() {
                DataService.removeData('child', child).then(
                    function() {
                        console.log('Cancellazione dello scolaro a buon fine.');
                        $scope.currentSchool.children.splice($scope.currentSchool.children.indexOf(child), 1);
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

.controller('ChildCtrl', function ($scope, $stateParams, $state, $rootScope, $timeout, $window, createDialog, DataService) {
	$scope.$parent.selectedTab = 'children-list';
    
    $scope.initController = function() {
        DataService.getData('children',
                $stateParams.idDomain, 
                $stateParams.idInstitute, 
                $stateParams.idSchool).then(
        function(response) {
            response.data.sort(compareChild);
            $scope.currentSchool.children = response.data;
            console.log('Caricamento degli scolari a buon fine.');
            if ($stateParams.idChild) {
                for (var i = 0; i < $scope.currentSchool.children.length && !$scope.selectedChild; i++) {
                    if ($scope.currentSchool.children[i].objectId == $stateParams.idChild) {
                        $scope.selectedChild = angular.copy($scope.currentSchool.children[i]);
                    }
                }	
                $scope.saveData = DataService.editData;
            } else {
                $scope.selectedChild = {
                    "name": '',
                    "surname": '',
                    "parentName": '',
                    "phone": '',
                    "classRoom": '',
                    "wsnId": '',
                    "ownerId": $scope.currentSchool.ownerId,
                    "instituteId": $scope.currentSchool.instituteId,
                    "schoolId": $scope.currentSchool.objectId
                };
                $scope.saveData = DataService.saveData;
            }
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
	
  $scope.isNewChild = function() {
  	return ($stateParams.idChild == null || $stateParams.idChild == '');
  }
  
	$scope.getChildName = function(child) {
        if (child) return child.name + " " + child.surname;
        return '';
	}
	
  // Exit without saving changes
  $scope.back = function () {
      createDialog('templates/modals/back.html',{
          id : 'back-dialog',
          title: 'Sei sicuro di voler uscire senza salvare?',
          success: { label: 'Conferma', fn: function() {$state.go('root.school.children-list');} }
      });
  }
  
  $scope.save = function () {  
  	if (checkFields()) {
  		$scope.saveData('child', $scope.selectedChild).then(
				function(response) {
                    console.log('Scolaro salvato.');
                    $state.go('root.school.children-list');
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

.controller('VolunteerListCtrl', function ($scope, $stateParams, $rootScope, createDialog, DataService, $state) {
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

    if (!$stateParams.idSchool)        //new school
    {
        createDialog('templates/modals/newschool-err.html',{
            id : 'newschoolerr-dialog',
            title: 'Attenzione!',
            success: { label: 'Torna indietro', fn: function() {
                window.history.back();
            } },
            footerTemplate: '<button class="btn btn-danger" ng-click="$modalSuccess()">{{$modalSuccessLabel}}</button>'
        });
    } else if ($scope.currentSchool) {
        $scope.initController();
    } else {
        $scope.$on('schoolLoaded', function(e) {  
            $scope.initController();        
        });
    }

    
    $scope.remove = function (volunteer) {
        createDialog('templates/modals/delete-volunteer.html',{
            id : 'delete-volunteer-dialog',
            title: 'Attenzione!',
            success: { 
            	label: 'Conferma', 
            	fn: function() {
                DataService.removeData('volunteer', volunteer).then(
                    function() {
                        console.log('Cancellazione del volontario a buon fine.');
                        $scope.currentSchool.volunteers.splice($scope.currentSchool.volunteers.indexOf(volunteer), 1);
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

.controller('VolunteerCtrl', function ($scope, $stateParams, $state, $rootScope, $timeout, $window, createDialog, DataService) {
	$scope.$parent.selectedTab = 'volunteer-list';
    
    $scope.initController = function() {
        DataService.getData('volunteers',
                $stateParams.idDomain, 
                $stateParams.idInstitute, 
                $stateParams.idSchool).then(
        function(response) {
            response.data.sort(compareVolunteer);
            $scope.currentSchool.volunteers = response.data;
            console.log('Caricamento dei volontari a buon fine.');
            if ($stateParams.idVolunteer) {
                for (var i = 0; i < $scope.currentSchool.volunteers.length && !$scope.selectedVolunteer; i++) {
                    if ($scope.currentSchool.volunteers[i].objectId == $stateParams.idVolunteer) {
                        $scope.selectedVolunteer = angular.copy($scope.currentSchool.volunteers[i]);
                    }
                }	
                $scope.saveData = DataService.editData;
            } else {
                $scope.selectedVolunteer = {
                    "name": '',
                    "address": '',
                    "phone": '',
                    "wsnId": '',
                    "cf": '',
                    "ownerId": $scope.currentSchool.ownerId,
                    "instituteId": $scope.currentSchool.instituteId,
                    "schoolId": $scope.currentSchool.objectId
                };
                $scope.saveData = DataService.saveData;
            }
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
    
  $scope.isNewVolunteer = function() {
  	return ($stateParams.idVolunteer == null || $stateParams.idVolunteer == '');
  }
  
  // Exit without saving changes
  $scope.back = function () {
      createDialog('templates/modals/back.html',{
          id : 'back-dialog',
          title: 'Sei sicuro di voler uscire senza salvare?',
          success: { label: 'Conferma', fn: function() {$state.go('root.school.volunteer-list');} }
      });
  }
  
  $scope.save = function () {  
  	if (checkFields()) {
  		$scope.saveData('volunteer', $scope.selectedVolunteer).then(
				function(response) {
					console.log('Volontario salvato.');
                    $state.go('root.school.volunteer-list');
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

    function compareVolunteer(a,b) {
        var aName = a.name;
        var bName = b.name;
        if (aName < bName)
            return -1;
        if (aName > bName)
            return 1;
        return 0;
    }
});


