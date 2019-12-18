angular.module('consoleControllers.leg')

.controller('SearchOnContentRepositoryDialogCtrl', function ($scope, $state, DataService, schoolId, addElementsFunction, saveFunction, position, getYoutubeImageFromLink, leg) {
    
    $scope.errorMsg;
    $scope.totalCounter = 0;
    $scope.searchtype = [];
    $scope.searchdistance = null;
    $scope.searchlocalschool = false;
    $scope.classes=[];
	$scope.schoolYears=[];
    $scope.subjects=[];
    $scope.selectedSearchtype = "Tutti"; //it may be : all / notAll / ...(value of the first selected one)
    $scope.selectedSubject = "Tutti";
    $scope.selectedSchoolYear = "Tutti";
    // $scope.searchtext='';
    $scope.subjectsListToggle = function(dropdownID, searchTime){
        if(searchTime == "before"){
            $('#'+dropdownID).slideToggle('fast');
        }else if(searchTime == 'after'){
            $('#subject'+dropdownID).slideToggle('fast');
        }
    }
    $scope.schoolYearsListToggle = function(dropdownID, searchTime){
        if(searchTime == "before"){
            $('#'+dropdownID).slideToggle('fast');
        }else if(searchTime == 'after'){
            $('#schoolYear'+dropdownID).slideToggle('fast');
        }
    }
    $scope.checkSearchtype = function(){
        var allSelected = true, notAllSelected = false;
        angular.forEach($scope.searchtype, function(value, key){
            if(value.selected == true && allSelected == true){allSelected = true;}else{allSelected = false;}
            if(value.selected == false && notAllSelected==false){notAllSelected=false;}else{notAllSelected=true;}
        });
        if(allSelected){
            $scope.selectedSearchtype = "Tutti";
        }else if(!notAllSelected){
            $scope.selectedSearchtype = "Non Selezionato";
        }else{
            var selectedItem = 0;
            for(var i=0; i<$scope.searchtype.length; i++){
                if($scope.searchtype[i].selected){
                    selectedItem++;
                    if(selectedItem == 1){$scope.selectedSearchtype = $scope.searchtype[i].searchtype;}
                }
            }
            if(selectedItem > 1){$scope.selectedSearchtype += "...";}
        }
    }
    $scope.checkSubjects = function(){
        var allSelected = true, notAllSelected = false;
        angular.forEach($scope.subjects, function(value, key){
            if(value.selected == true && allSelected == true){allSelected = true;}else{allSelected = false;}
            if(value.selected == false && notAllSelected==false){notAllSelected=false;}else{notAllSelected=true;}
        });
        if(allSelected){
            $scope.selectedSubject = "Tutti";
        }else if(!notAllSelected){
            $scope.selectedSubject = "Non Selezionato";
        }else{
            var selectedItem = 0;
            for(var i=0; i<$scope.subjects.length; i++){
                if($scope.subjects[i].selected){
                    selectedItem++;
                    if(selectedItem == 1){$scope.selectedSubject = $scope.subjects[i].value;}
                }
            }
            if(selectedItem > 1){$scope.selectedSubject += "...";}
        }
    }
    $scope.checkSchoolYears = function(){
        var allSelected = true, notAllSelected = false;
        angular.forEach($scope.schoolYears, function(value, key){
            if(value.selected == true && allSelected == true){allSelected = true;}else{allSelected = false;}
            if(value.selected == false && notAllSelected==false){notAllSelected=false;}else{notAllSelected=true;}
        });
        if(allSelected){
            $scope.selectedSchoolYear = "Tutti";
        }else if(!notAllSelected){
            $scope.selectedSchoolYear = "Non Selezionato";
        }else{
            var selectedItem = 0;
            for(var i=0; i<$scope.schoolYears.length; i++){
                if($scope.schoolYears[i].selected){
                    selectedItem++;
                    if(selectedItem == 1){$scope.selectedSchoolYear = $scope.schoolYears[i].value;}
                }
            }
            if(selectedItem > 1){$scope.selectedSchoolYear += "...";}
        }
    }

    $scope.changeSubjectSelectedMode = (index) =>{
		$scope.contentResults[index].referenceContent.selectAllSubjects = $scope.contentResults[index].referenceContent.selectAllSubjects ? false : true;
		
		if($scope.contentResults[index].referenceContent.selectAllSubjects){
			// have to select all
			$scope.contentResults[index].referenceContent.selectAllSubjectText="Deseleziona tutte le materie";
			angular.forEach($scope.contentResults[index].referenceContent.selectedSubjects, function(value, key){
				value.selected=true;
			});
		}else{
			//have to unselect all
			$scope.contentResults[index].referenceContent.selectAllSubjectText="Seleziona tutte le materie";
			angular.forEach($scope.contentResults[index].referenceContent.selectedSubjects, function(value, key){
				value.selected=false;
			});
		}
    }
    
    $scope.changeSchoolYearSelectedMode = (index) =>{
        $scope.contentResults[index].referenceContent.selectAllSchoolYears = $scope.contentResults[index].referenceContent.selectAllSchoolYears ? false : true;
		
		if($scope.contentResults[index].referenceContent.selectAllSchoolYears){
			// have to select all
			$scope.contentResults[index].referenceContent.selectAllSchoolYearText="Deseleziona tutte le classi";
			angular.forEach($scope.contentResults[index].referenceContent.selectedSchoolYears, function(value, key){
				value.selected=true;
			});
		}else{
			//have to unselect all
			$scope.contentResults[index].referenceContent.selectAllSchoolYearText="Seleziona tutte le classi";
			angular.forEach($scope.contentResults[index].referenceContent.selectedSchoolYears, function(value, key){
				value.selected=false;
			});
		}
    }
    DataService.getMultimediaContentTags(leg.ownerId, leg.pedibusGameId).then(
		function(response) {
            // console.log("tags:",response)
            $scope.searchtype.push(
                {searchtype:'Immagini',value:'image',selected:true},
                {searchtype:'Video',value:'video',selected:true},
                {searchtype:'Collegamento a pagina web',value:'link',selected:true},
                {searchtype:'File',value:'file',selected:true});
			//$scope.classes=response.data.classes;
			angular.forEach(response.data.classes, function(item, key){
				$scope.classes.push({value:item,selected:true});
            });
            
			angular.forEach(response.data.schoolYears, function(item, key){
				$scope.schoolYears.push({value:item,selected:true});
            });

			angular.forEach(response.data.subjects, function(item, key){
				$scope.subjects.push({value:item,selected:true});
            });
		},function(error) {
			console.log('Errore :' , error.data.errorMsg);
		}
	);
    $scope.searchOnContentRepository = function(searchText, searchtype, searchdistance) {
        //if (!$scope.searchtext) return;
        $scope.searchtext = searchText;
        // $scope.searchtype = searchtype;
        $scope.searchdistance = searchdistance
        $scope.resetResults();
        var searchposition = undefined;
        var searchdistance = undefined;
        if($scope.searchdistance) {
            if(position){searchposition = position;}
        	searchdistance = $scope.searchdistance;
        }
        var selectedSearchtype=[];
        $scope.searchtype.forEach(e => {
            if(e.selected){
                selectedSearchtype.push(e.value);
            }
        });
        var selectedSubjects=[];
        $scope.subjects.forEach(e => {
            if(e.selected){
                selectedSubjects.push(e.value);
            }
        });
        var selectedSchoolYears=[];
        $scope.schoolYears.forEach(e=>{
            if(e.selected){
                selectedSchoolYears.push(e.value);
            }
        });
        console.log("$scope.searchtext",$scope.searchtext)
        DataService.searchOnContentRepository($scope.searchtext, searchposition, searchdistance,
        		$scope.searchlocalschool ? schoolId : undefined, 
                selectedSearchtype,
                selectedSubjects, selectedSchoolYears).then(
                function(response) {
                    console.log("response.data;",response.data)
                    response.data.forEach(element => {
                        switch (element.referenceContent.type) {
                            case 'image':
                                element.referenceContent.referenceImg = element.referenceContent.link;
                                break;
                            case 'video':
                                var youtubeThumbnail = getYoutubeImageFromLink(element.referenceContent.link);
                                if (youtubeThumbnail) {
                                    element.referenceContent.referenceImg = youtubeThumbnail;
                                    element.referenceContent.isYoutubeVideo = true;
                                } else {
                                    element.referenceContent.referenceImg = "img/video.png";
                                }                                
                                break;
                            case 'link':
                                element.referenceContent.referenceImg = "img/link.png";
                                break;
                            case 'file':
                              element.referenceContent.referenceImg = "img/file.png";
                              break;
                        }
                    });          
                    $scope.contentResults = response.data;
                    $scope.noResults = response.data.length == 0;
                    //change the formet of array classes, schoolYears, subjects. because of the selection option
                    // also add selectAllSubjects, selectAllSchoolYears and selectAllSubjectText, selectAllSchoolYearText
                    $scope.contentResults.forEach(e=>{
                        e.referenceContent.selectAllSubjects=true;
                        e.referenceContent.selectAllSubjectText = "Deseleziona tutte le materie";
                        e.referenceContent.selectAllSchoolYears=true;
                        e.referenceContent.selectAllSchoolYearText = "Deseleziona tutte le classi";
                    	e.referenceContent.selectedClasses = [];
                        angular.forEach($scope.classes, function(item, key){
                            e.referenceContent.selectedClasses[key]={value:item.value,selected:true};
                        });
                        e.referenceContent.selectedSchoolYears = [];
                        var i=0;
                        angular.forEach($scope.schoolYears, function(item, key){
                            e.referenceContent.selectedSchoolYears[key]={value:item.value,selected:true};
                        });
                        e.referenceContent.selectedSubjects = [];
                        var j=0;
                        angular.forEach($scope.subjects, function(item, key){
                            e.referenceContent.selectedSubjects[key]={value:item.value,selected:true};
                        })
                    }); 
                }, function() {
                }
        );
    }
    $scope.$modalSuccess = function() {
        var countSeletedItem=0;
        $scope.contentResults.forEach(element => {
            if (element.selectedToAdd) {
                var selectedClasses=[];
                countSeletedItem++;
                element.referenceContent.selectedClasses.forEach(e => {
                    if(e.selected){
                        selectedClasses.push(e.value)
                    }
                });

                var selectedSchoolYears=[];
                element.referenceContent.selectedSchoolYears.forEach(e=>{
                    if(e.selected){
                        selectedSchoolYears.push(e.value)
                    }
                });

                var selectedSubjects=[];
                element.referenceContent.selectedSubjects.forEach(e => {
                    if(e.selected){
                        selectedSubjects.push(e.value);
                    }
                });
                addElementsFunction(element.referenceContent.name, element.referenceContent.link, element.referenceContent.type, 
                    selectedClasses, selectedSubjects, selectedSchoolYears, true, true, element.referenceContent.objectId);
                // addElementsFunction(element.info[0].name, element.link, element.type);
                saveFunction();
            }
        });
        if(countSeletedItem > 0){
            $scope.$modalClose();
        }else{
            $scope.errorMsg = "Errore: selezionare prima l'elemento.";
        }
        
    }
    $scope.resetError = function() {
    	$scope.errorMsg = undefined;
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
    $scope.changePage = function(newPageNumber, oldPageNumber) {
    	var el = document.getElementById('contentList');
    	el.scrollTop = 0;
    }
    $scope.getListText = function(list) {
        var text = '';
    	for(var i=0; i<list.length; i++) {
    		text = text + list[i] + ", "
    	}
    	return text.substring(0, text.length - 2)
    }
    
});