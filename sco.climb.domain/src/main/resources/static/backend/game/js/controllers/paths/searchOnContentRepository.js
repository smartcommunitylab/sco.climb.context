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
                    if(selectedItem == 1){$scope.selectedSubject = $scope.subjects[i].subject;}
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
                    if(selectedItem == 1){$scope.selectedSchoolYear = $scope.schoolYears[i].schoolYear;}
                }
            }
            if(selectedItem > 1){$scope.selectedSchoolYear += "...";}
        }
    }
    DataService.getMultimediaContentTags(leg.ownerId, leg.pedibusGameId).then(
		function(response) {
            console.log("tags:",response)
            $scope.searchtype.push(
                {searchtype:'Immagini',value:'image',selected:true},
                {searchtype:'Video',value:'video',selected:true},
                {searchtype:'Collegamento a pagina web',value:'link',selected:true},
                {searchtype:'File',value:'file',selected:true});
			//$scope.classes=response.data.classes;
			angular.forEach(response.data.classes, function(value, key){
				$scope.classes.push({class:value,selected:true});
            });
            
			angular.forEach(response.data.schoolYears, function(value, key){
				$scope.schoolYears.push({schoolYear:value,selected:true});
            });

			angular.forEach(response.data.subjects, function(value, key){
				$scope.subjects.push({subject:value,selected:true});
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
                selectedSubjects.push(e.subject);
            }
        });
        var selectedSchoolYears=[];
        $scope.schoolYears.forEach(e=>{
            if(e.selected){
                selectedSchoolYears.push(e.schoolYear);
            }
        });
        console.log("$scope.searchtext",$scope.searchtext)
        DataService.searchOnContentRepository($scope.searchtext, searchposition, searchdistance,
        		$scope.searchlocalschool ? schoolId : undefined, 
                selectedSearchtype,
                selectedSubjects, selectedSchoolYears).then(
                function(response) {
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
                    $scope.contentResults.forEach(e=>{
                    		e.referenceContent.selectedClasses = [];
                        angular.forEach($scope.classes, function(value, key){
                            e.referenceContent.selectedClasses[key]={class:value.class,selected:true};
                        });
                        e.referenceContent.selectedSchoolYears = []
                        angular.forEach($scope.schoolYears, function(value, key){
                            e.referenceContent.selectedSchoolYears[key]={schoolYear:value.schoolYear,selected:true};
                            if(e.referenceContent.schoolYears.includes(value.schoolYear)) {
                            	e.referenceContent.schoolYears[key] = {schoolYear:value.schoolYear,selected:true};
                            }
                        });
                        e.referenceContent.selectedSubjects = [];
                        angular.forEach($scope.subjects, function(value, key){
                            e.referenceContent.selectedSubjects[key]={subject:value.subject,selected:true};
                            if(e.referenceContent.subjects.includes(value.subject)) {
                            	e.referenceContent.subjects[key] = {subject:value.subject,selected:true};
                            }
                        })
                    });
                    console.log("contentResults:",$scope.contentResults); 
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
                        selectedClasses.push(e.class)
                    }
                });

                var selectedSchoolYears=[];
                element.referenceContent.selectedSchoolYears.forEach(e=>{
                    if(e.selected){
                        selectedSchoolYears.push(e.schoolYear)
                    }
                });

                var selectedSubjects=[];
                element.referenceContent.selectedSubjects.forEach(e => {
                    if(e.selected){
                        selectedSubjects.push(e.subject);
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
    
});