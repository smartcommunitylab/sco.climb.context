angular.module('consoleControllers.leg')

    .controller('SearchOnSearchEnginesDialogCtrl', function ($scope, $stateParams, $state, $rootScope, $window, $timeout, DataService, leg, addElementsFunction, saveFunction) {

        $scope.errorMsg;
        $scope.searchtype = 'image';
        $scope.lastSearchtype = '';
        $scope.totalCounter = 0;
        $scope.classes = [];
        $scope.schoolYears = [];
        $scope.subjects = [];
        $scope.selected;
        $scope.imageResultSelected = [];
        $scope.ytResultSelected = [];
        $scope.wikiResultSelected = [];
        $scope.loading = false;
        $scope.materialeListToggle = function (dropdownID) {
            // $('.wrapper .list').slideToggle('fast');
            $('#materiale' + dropdownID).slideToggle('fast');
        }
        $scope.classListToggle = function (dropdownID) {
            // $('.wrapper .list').slideToggle('fast');
            $('#classe' + dropdownID).slideToggle('fast');
        }
        $scope.changeSubjectSelectedMode = (index) => {
            var tempResult;
            if ($scope.searchtype == 'image') {
                tempResult = $scope.imageResults[index];
            } else if ($scope.searchtype == 'video') {
                tempResult = $scope.ytResults[index];
            } else if ($scope.searchtype == 'wikipedia') {
                tempResult = $scope.wikiResults[index];
            }
            tempResult.selectAllSubjects = tempResult.selectAllSubjects ? false : true;

            if (tempResult.selectAllSubjects) {
                // have to select all
                tempResult.selectAllSubjectText = "Deseleziona tutte le materie";
                angular.forEach(tempResult.subjects, function (value, key) {
                    value.selected = true;
                });
            } else {
                //have to unselect all
                tempResult.selectAllSubjectText = "Seleziona tutte le materie";
                angular.forEach(tempResult.subjects, function (value, key) {
                    value.selected = false;
                });
            }
        }
        $scope.changeSchoolYearSelectedMode = (index) => {
            var tempResult;
            if ($scope.searchtype == 'image') {
                tempResult = $scope.imageResults[index];
            } else if ($scope.searchtype == 'video') {
                tempResult = $scope.ytResults[index];
            } else if ($scope.searchtype == 'wikipedia') {
                tempResult = $scope.wikiResults[index];
            }
            tempResult.selectAllSchoolYears = tempResult.selectAllSchoolYears ? false : true;

            if (tempResult.selectAllSchoolYears) {
                // have to select all
                tempResult.selectAllSchoolYearText = "Deseleziona tutte le classi";
                angular.forEach(tempResult.schoolYears, function (value, key) {
                    value.selected = true;
                });
            } else {
                //have to unselect all
                tempResult.selectAllSchoolYearText = "Seleziona tutte le classi";
                angular.forEach(tempResult.schoolYears, function (value, key) {
                    value.selected = false;
                });
            }
        }
        DataService.getMultimediaContentTags(leg.ownerId, leg.pedibusGameId).then(
            function (response) {
                console.log("tags:", response)
                //$scope.classes=response.data.classes;
                angular.forEach(response.data.classes, function (value, key) {
                    $scope.classes.push({ class: value, selected: true });
                });

                // $scope.schoolYears=response.data.schoolYears;
                angular.forEach(response.data.schoolYears, function (value, key) {
                    $scope.schoolYears.push({ schoolYear: value, selected: true });
                });

                // $scope.subjects=response.data.subjects;
                angular.forEach(response.data.subjects, function (value, key) {
                    $scope.subjects.push({ subject: value, selected: true });
                });
            }, function (error) {
                console.log('Errore :', error.data.errorMsg);
            }
        );
        $scope.searchOnEngine = function (searchtext, searchtype) {
            $scope.searchtext = searchtext;
            $scope.searchtype = searchtype;
            if (!$scope.searchtext) {
                $scope.errorMsg = "si prega di fornire prima il 'testo di ricerca'";
                setTimeout(function () {
                    $scope.errorMsg = '';
                }, 2000);
                return;
            }
            $scope.totalCounter = 0;
            $scope.resetResults();
            $scope.loading = true;
            if ($scope.searchtype == 'wikipedia' && !$scope.wikiResults) {
                $scope.changePage(undefined);
            } else if ($scope.searchtype == 'video' && !$scope.ytResults) {
                $scope.changePage(undefined);
            } else if ($scope.searchtype == 'image' && !$scope.imageResults) {
                $scope.changePage(undefined);
            }
        }

        $scope.changePage = function (pageToken) {
            if ($scope.searchtype == 'wikipedia') {
                //in this case pageToken is an offset of search results
                DataService.searchOnWikipedia($scope.searchtext, pageToken).then(
                    function (response) {
                        $scope.resetResults();
                        if (response.data.query) {
                            $scope.wikiResults = response.data.query.pages;
                            $scope.loading = false;
                            for (var page in $scope.wikiResults) {
                                $scope.wikiResults[page].link = 'https://it.wikipedia.org/wiki/' + $scope.wikiResults[page].title;
                            }
                            $scope.prevPageToken = response.data['query-continue'].search.gsroffset - 20;
                            if ($scope.prevPageToken < 0) $scope.prevPageToken = -1;
                            $scope.nextPageToken = response.data['query-continue'].search.gsroffset;
                            console.log("wikiResults:", $scope.wikiResults);
                            var el = document.getElementById('wikiContentList');
                            el.scrollTop = 0;
                            //check selected before
                            angular.forEach($scope.wikiResults, function (e_wr, k_wr) {
                                //if it's have same link then store from wikiResultSelected
                                if ($scope.lastSearchtype == $scope.searchtype) {
                                    angular.forEach($scope.wikiResultSelected, function (e_wrs, k_yrs) {
                                        if (e_wr.link == e_wrs.link) {
                                            $scope.wikiResults[k_wr] = e_wrs;
                                        }
                                    });
                                }
                                //store tags
                                if (!e_wr.classes) { e_wr.classes = angular.copy($scope.classes); }
                                if (!e_wr.schoolYears) { e_wr.schoolYears = angular.copy($scope.schoolYears); }
                                if (!e_wr.subjects) { e_wr.subjects = angular.copy($scope.subjects); }
                                //select or unselect all
                                e_wr.selectAllSubjects = true;
                                e_wr.selectAllSubjectText = "Deseleziona tutte le materie";
                                e_wr.selectAllSchoolYears = true;
                                e_wr.selectAllSchoolYearText = "Deseleziona tutte le classi";
                            });
                            $scope.lastSearchtype = $scope.searchtype;
                        } else {
                            $scope.loading = false;
                            $scope.errorMsg = "scusa, nessun dato trovato";
                            setTimeout(function () {
                                $scope.errorMsg = '';
                            }, 2000);
                        }
                    }, function (err) {
                        $scope.loading = false;
                        $scope.errorMsg = "Errore di comunicazione";
                        setTimeout(function () {
                            $scope.errorMsg = '';
                        }, 2000);
                    }
                );
            } else if ($scope.searchtype == 'video') {
                DataService.searchOnYoutube($scope.searchtext, pageToken).then(
                    function (response) {
                        $scope.resetResults();
                        if (response.data.items) {
                            $scope.ytResults = response.data.items;
                            $scope.loading = false;
                            $scope.prevPageToken = response.data.prevPageToken;
                            if (!$scope.prevPageToken) $scope.prevPageToken = -1; //used to generalize pagination controls
                            $scope.nextPageToken = response.data.nextPageToken;
                            console.log(response);
                            var el = document.getElementById('videoContentList');
                            el.scrollTop = 0;
                            //check selected before
                            angular.forEach($scope.ytResults, function (e_yr, k_yr) {
                                //if it's have same link then store from ytResultSelected
                                if ($scope.lastSearchtype == $scope.searchtype) {
                                    angular.forEach($scope.ytResultSelected, function (e_yrs, k_yrs) {
                                        if (e_yr.snippet.thumbnails.default.url == e_yrs.snippet.thumbnails.default.url) {
                                            $scope.ytResults.splice(k_yr, 1, e_yrs);
                                        }
                                    });
                                }
                                //store tags
                                if (!e_yr.classes) { e_yr.classes = angular.copy($scope.classes); }
                                if (!e_yr.schoolYears) { e_yr.schoolYears = angular.copy($scope.schoolYears); }
                                if (!e_yr.subjects) { e_yr.subjects = angular.copy($scope.subjects); }
                                //select or unselect all
                                e_yr.selectAllSubjects = true;
                                e_yr.selectAllSubjectText = "Deseleziona tutte le materie";
                                e_yr.selectAllSchoolYears = true;
                                e_yr.selectAllSchoolYearText = "Deseleziona tutte le classi";
                            });
                            $scope.lastSearchtype = $scope.searchtype;
                        } else {
                            $scope.loading = false;
                            $scope.errorMsg = "scusa, nessun dato trovato";
                            setTimeout(function () {
                                $scope.errorMsg = '';
                            }, 2000);
                        }
                    }, function (err) {
                        $scope.loading = false;
                        $scope.errorMsg = "Errore di comunicazione";
                        if (err.status == 403)
                            $scope.errorMsg = "Errore, limite di ricerche superato ";
                        setTimeout(function () {
                            $scope.errorMsg = '';
                        }, 2000);
                    }
                );
            } else if ($scope.searchtype == 'image') {
                //in this case pageToken is an offset of search results
                DataService.searchOnImages($scope.searchtext, pageToken).then(
                    function (response) {
                        $scope.resetResults();
                        if (response.data.items) {
                            $scope.imageResults = response.data.items;
                            $scope.loading = false;
                            if (response.data.queries.previousPage) {
                                $scope.prevPageToken = response.data.queries.previousPage[0].startIndex;
                            } else {
                                $scope.prevPageToken = -1; //used to generalize pagination controls
                            }
                            if (response.data.queries.nextPage) {
                                $scope.nextPageToken = response.data.queries.nextPage[0].startIndex;
                            }
                            console.log("imageResults:", response);
                            var el = document.getElementById('imageContentList');
                            el.scrollTop = 0;
                            //check selected before
                            angular.forEach($scope.imageResults, function (e_ir, k_ir) {
                                //if it's have same link then store from imageResultSelected
                                if ($scope.searchtype == $scope.lastSearchtype) {
                                    angular.forEach($scope.imageResultSelected, function (e_irs, k_irs) {
                                        if (e_ir.link == e_irs.link) { $scope.imageResults.splice(k_ir, 1, e_irs); }
                                    });
                                }
                                //store tags
                                if (!e_ir.classes) { e_ir.classes = angular.copy($scope.classes); }
                                if (!e_ir.schoolYears) { e_ir.schoolYears = angular.copy($scope.schoolYears); }
                                if (!e_ir.subjects) { e_ir.subjects = angular.copy($scope.subjects); }
                                //select or unselect all
                                e_ir.selectAllSubjects = true;
                                e_ir.selectAllSubjectText = "Deseleziona tutte le materie";
                                e_ir.selectAllSchoolYears = true;
                                e_ir.selectAllSchoolYearText = "Deseleziona tutte le classi";
                            });

                            $scope.lastSearchtype = $scope.searchtype;
                        } else {
                            $scope.loading = false;
                            $scope.errorMsg = "scusa, nessun dato trovato";
                            setTimeout(function () {
                                $scope.errorMsg = '';
                            }, 2000);
                        }
                    }, function () {
                        $scope.loading = false;
                        $scope.errorMsg = "Errore di comunicazione";
                        setTimeout(function () {
                            $scope.errorMsg = '';
                        }, 2000);
                    }
                );
            }
        }

        $scope.$modalSuccess = function () {
            var countSeletedItem = 0;
            if ($scope.searchtype == 'wikipedia') {
                for (key in $scope.wikiResultSelected) {
                    var element = $scope.wikiResultSelected[key];
                    if (element.selectedToAdd) {
                        countSeletedItem++;
                        var selectedClasses = [];
                        element.classes.forEach(e => {
                            if (e.selected) {
                                selectedClasses.push(e.class);
                            }
                        });

                        var selectedSubjects = [];
                        element.subjects.forEach(e => {
                            if (e.selected) {
                                selectedSubjects.push(e.subject);
                            }
                        });

                        var selectedSchoolYears = [];
                        element.schoolYears.forEach(e => {
                            if (e.selected) {
                                selectedSchoolYears.push(e.schoolYear);
                            }
                        });
                        addElementsFunction(element.title, element.link, 'link', selectedClasses, selectedSubjects, selectedSchoolYears);
                        saveFunction();
                    }
                }
            } else if ($scope.searchtype == 'video') {
                $scope.ytResultSelected.forEach(element => {
                    if (element.selectedToAdd) {
                        countSeletedItem++;
                        var selectedClasses = [];
                        element.classes.forEach(e => {
                            if (e.selected) {
                                selectedClasses.push(e.class);
                            }
                        });

                        var selectedSubjects = [];
                        element.subjects.forEach(e => {
                            if (e.selected) {
                                selectedSubjects.push(e.subject);
                            }
                        });

                        var selectedSchoolYears = [];
                        element.schoolYears.forEach(e => {
                            if (e.selected) {
                                selectedSchoolYears.push(e.schoolYear);
                            }
                        });

                        addElementsFunction(element.snippet.title, 'https://www.youtube.com/watch?v=' + element.id.videoId, 'video', selectedClasses, selectedSubjects, selectedSchoolYears);
                        saveFunction();
                    }
                });
            } else if ($scope.searchtype == 'image') {
                $scope.imageResultSelected.forEach(element => {
                    if (element.selectedToAdd) {
                        countSeletedItem++;
                        var selectedClasses = [];
                        element.classes.forEach(e => {
                            if (e.selected) {
                                selectedClasses.push(e.class);
                            }
                        });

                        var selectedSubjects = [];
                        element.subjects.forEach(e => {
                            if (e.selected) {
                                selectedSubjects.push(e.subject);
                            }
                        });

                        var selectedSchoolYears = [];
                        element.schoolYears.forEach(e => {
                            if (e.selected) {
                                selectedSchoolYears.push(e.schoolYear);
                            }
                        });

                        addElementsFunction(element.title, element.link, 'image', selectedClasses, selectedSubjects, selectedSchoolYears);
                        saveFunction();
                    }
                });
            }
            if (countSeletedItem > 0) {
                $scope.$modalClose();
            } else {
                $scope.errorMsg = "Errore: seleziona prima la riga.";
            }
        }
        $scope.resetError = function () {
            $scope.errorMsg = undefined;
        }
        $scope.resetResults = function () {
            $scope.wikiResults = undefined;
            $scope.ytResults = undefined;
            $scope.imageResults = undefined;
            //$scope.totalCounter = 0;
            $scope.$modalSuccessLabel = "Aggiungi " + $scope.totalCounter + " elementi";
        }
        $scope.updateTotalCounter = function (state, updateIndex) {
            //selected
            if (state) {
                $scope.totalCounter++;
                if ($scope.searchtype == 'image') { $scope.imageResultSelected.push($scope.imageResults[updateIndex]); }
                else if ($scope.searchtype == 'video') { $scope.ytResultSelected.push($scope.ytResults[updateIndex]); }
                else if ($scope.searchtype == 'wikipedia') {
                    $scope.wikiResultSelected.push($scope.wikiResults[updateIndex]);
                }
            }//unselected 
            else {
                $scope.totalCounter--;
                if ($scope.searchtype == 'image') {
                    angular.forEach($scope.imageResultSelected, function (e, key) {
                        if (e.link == $scope.imageResults[updateIndex].link) {
                            $scope.imageResultSelected.splice(key, 1);
                        }
                    });
                } else if ($scope.searchtype == 'video') {
                    angular.forEach($scope.ytResultSelected, function (e, key) {
                        if (e.snippet.thumbnails.default.url == $scope.ytResults[updateIndex].snippet.thumbnails.default.url) {
                            $scope.ytResultSelected.splice(key, 1);
                        }
                    });
                } else if ($scope.searchtype == 'wikipedia') {
                    angular.forEach($scope.wikiResultSelected, function (e, key) {
                        if (e.link == $scope.wikiResults[updateIndex].link) {
                            $scope.wikiResultSelected.splice(key, 1);
                        }
                    });
                }
            }
            $scope.$modalSuccessLabel = "Aggiungi " + $scope.totalCounter + " elementi";
        }
        $scope.checkImage = function (image) {
            image.selectedToAdd = !image.selectedToAdd;
            $scope.updateTotalCounter(image.selectedToAdd);
        }
    });
