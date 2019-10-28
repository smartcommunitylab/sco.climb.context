/* global angular */
angular.module('climbGame.controllers.calendar', [])
  .controller('calendarCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', 'CacheSrv', 'dataService', 'calendarService', 'configService', 'loginService', 'profileService',
    function ($scope, $filter, $window, $interval, $mdDialog, $mdToast, CacheSrv, dataService, calendarService, configService, loginService, profileService) {
      $scope.week = []
      $scope.prev2Week=true;
      $scope.selectedWeather = ''
      $scope.selectedMean = ''
      $scope.selectedMeanColor = 'cal-menu-col'
      $scope.labelWeek = ''
      $scope.sendingData = false
      $scope.cal = {
        meanOpen: false
      }
      $scope.classMap = {}
      $scope.weekData = []
      $scope.daysOfWeek = 5;
      $scope.calHeaderFlex=25;
      $scope.todayData = {
        babies: [],
        means: {}
      }
      $scope.ENABLE_PAST_DAYS_EDIT = configService.ENABLE_PAST_DAYS_EDIT;
      
      $scope.lastLeg = {}
      $scope.isGameFinishedNotificationDisplaied = false;
      $scope.mapModalities=[];
      $scope.flexNum=0;
      var returnModalitiesColor = function(type){
        var color = ''
        switch (type) {
        case 'A piedi':
          color = 'cal-foot-friend-col'
          break
        case "In bici":
          color = 'cal-bici-col'
          break
        case 'Scuolabus o trasporto pubblico':
          color = 'cal-bus-col'
          break
        case 'In auto fino alla piazzola di sosta':
          color = 'cal-car-square-col'
          break
        case 'Car pooling':
          color = 'cal-car-pooling-col'
          break
        case 'In auto fino a scuola':
          color = 'cal-car-school-col'
          break
        case 'Assente':
          color = 'cal-away-col'
          break
        case 'Pedibus':
          color = 'cal-pedibus-col'
          break
        default:
          color = 'cal-away-col'
          break
        }
        return color
      }
      setTodayIndex()
      setClassSize()
      for (var i = 0; i < $scope.daysOfWeek; i++) {
        $scope.week.push(new Date(getMonday(new Date()).getTime() + (i * 24 * 60 * 60 * 1000)))
      }

      setLabelWeek($scope.week)

      calendarService.setTitle().then(
        function () {},
        function () {
          // default value
        }
      )

      profileService.getProfile().then(function(profile) {
	    	loginService.setUserToken(profile.token)
	    	loginService.setAllOwners(profile.ownerIds)
	      calendarService.getClassPlayers().then(
	        function (players) {
	          $scope.classPlayers = players
	          for (var i = 0; i < players.length; i++) {
	            $scope.todayData.babies.push({
	              name: players[i].nickname,
	              surname: '',
	              childId: players[i].objectId,
	              color: ''
	            })
	            $scope.classMap[players[i].objectId] = players[i]
	          }
	
	        },
	        function () {}
	      )
	      dataService.getStatus().then(
	      	function(data) {
            //check the number of modalities and set color
            if(data.game.modalities.length > 0){
              var pedibusModalityValue = false;
              dataService.getModalityMap().then(function(modalityData){
                //$scope.mapModalities = data.game.modalities.map(val => ({ value: val, color: returnModalitiesColor(val) }));
                data.game.modalities.map(function(val){ 
                  modalityData.modalities.find(function(currentValue){
                    if(currentValue.value==val){
                      $scope.mapModalities.push(currentValue);
                      if(currentValue.value=="pedibus"){pedibusModalityValue=true;}
                    }
                  })
                });
                console.log("mapResult::",$scope.mapModalities)
                if(pedibusModalityValue){
                  $scope.flexNum=100/($scope.mapModalities.length);
                }else{$scope.flexNum=100/($scope.mapModalities.length+1);}
                
                console.log("flexNum and walkPlusPedibusModalityValue",$scope.flexNum)
              },function(er){console.log("error",er)});
            }
            //check the Saturday
            if(data.game.daysOfWeek[5]){
              $scope.daysOfWeek = 6;
              $scope.calHeaderFlex=10;
              $scope.week.push(new Date(getMonday(new Date()).getTime() + (5 * 24 * 60 * 60 * 1000)))
            }else{
              console.log("Saturday is::",data.game.daysOfWeek[5])
            }
            calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
	            function (calendar) {
	              createWeekData(calendar)
	              //updateTodayData(calendar)
	            },
	            function () {}
	          )
		      	if(data.legs && data.legs.length) {
		      		var pos = data.legs.length - 1;
		      		$scope.lastLeg = data.legs[pos]
		      	}
		      }, function (err) {
            console.log("error::",err)
          }
	      )
	    }, function (err) {
	      console.log(err)
	      // Toast the Problem
	      $mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
	    });

      $scope.returnColorByType = function (type) {
        var color = ''
        switch (type) {
        case 'zeroImpact_solo':
          color = 'cal-foot-friend-col'
          break
        case 'zeroImpact_wAdult':
          color = 'cal-foot-adult-col'
          break
        case 'bus':
          color = 'cal-bus-col'
          break
        case 'pandr':
          color = 'cal-car-square-col'
          break
        case 'car':
          color = 'cal-car-school-col'
          break
        case 'absent':
          color = 'cal-away-col'
          break
        case 'pedibus':
          color = 'cal-pedibus-col'
          break
        }
        return color
      }

      $scope.selectWather = function (weather) {
        $scope.selectedWeather = weather
      }

      /*
      $scope.openMeans = function () {
        $scope.cal.meanOpen = !$scope.cal.meanOpen
      }
      */

      $scope.selectGeneralMean = function (mean) {
        $scope.selectedMean = mean;
        // $scope.selectedMeanColor = returnModalitiesColor($scope.selectedMean)
        // $scope.selectedMeanColor = "'background-color':"+$scope.mapModalities.find(val=>{return val.value==$scope.selectedMean;}).color+" !important;";
        $scope.selectedMeanColor = $scope.mapModalities.find(val=>{return val.value==$scope.selectedMean;}).color;
      }

      $scope.selectBabyMean = function (index, dayIndex, babyId) {
        if (!$scope.selectedMean) {
          $mdToast.show($mdToast.simple().content('Selezionare un mezzo di trasporto').position('top left'))
          return
        }
        
        // add mean to index and remove the other
        $scope.weekData[dayIndex][babyId].color = $scope.mapModalities.find(val=>{return val.value==$scope.selectedMean;}).color;
        $scope.weekData[dayIndex][babyId].mean = $scope.selectedMean
        $scope.weekData[dayIndex].walk = 0;
        $scope.weekData[dayIndex].pedibus = 0;
        $scope.weekData[dayIndex].bike = 0;
        $scope.weekData[dayIndex].bus = 0;
        $scope.weekData[dayIndex].pandr = 0;
        $scope.weekData[dayIndex].carpooling = 0;
        $scope.weekData[dayIndex].car = 0;
        $scope.weekData[dayIndex].absent = 0;
        for (var z = 0; z < $scope.classPlayers.length; z++) {
        	var player = $scope.classPlayers[z]
        	if($scope.weekData[dayIndex][player.objectId].mean) {
        		var mean = $scope.weekData[dayIndex][player.objectId].mean
        		$scope.weekData[dayIndex][mean]++
        	}
        }
      }

      $scope.today = function (index) {
        return index === $scope.todayIndex
      }

      $scope.sendData = function (dayIndex) {
        if (dataAreComplete(dayIndex)) {
          $mdDialog.show({
            // targetEvent: $event,
            scope: $scope, // use parent scope in template
            preserveScope: true, // do not forget this if use parent scope
            template: '<md-dialog>' +
              '  <div class="cal-dialog-title">{{\'cal_save_popup_title\'|translate}}</div><md-divider></md-divider>' +
              '  <div class="cal-dialog-text">{{\'cal_save_popup_content\'|translate}}</div>' +
              '    <div layout="row"  layout-align="start center" ><div layout"column" flex="50" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
              '      Annulla' +
              '   </div> </md-button>' +
              '<div layout"column" flex="50" ><md-button ng-click="confirmSend()" class="send-dialog-confirm" ng-disabled="sendingData"> ' +
              '    <span ng-show="!sendingData">Invia</span>' +
              '    <md-progress-circular class="send-dialog-progress" style="margin:auto;border-color:white;" md-mode="indeterminate" md-diameter="20" ng-show="sendingData"></md-progress-circular></md-button></div>' +
              '</div></md-dialog>',
            controller: function DialogController($scope, $mdDialog) {
              $scope.closeDialog = function () {
                $mdDialog.hide()
                $scope.sendingData = false
              }

              $scope.confirmSend = function () {
                if (!$scope.sendingData) {
                  $scope.sendingData = true
                  $scope.todayData.meteo = $scope.selectedWeather
                  $scope.todayData.day = $scope.week[dayIndex].setHours(0, 0, 0, 0);
                  var babiesMap = {}
                  for (var i = 0; i < $scope.classPlayers.length; i++) {
                  	var player = $scope.classPlayers[i]
                    if ($scope.weekData[dayIndex][player.objectId].mean) {
                      babiesMap[player.objectId] = $scope.weekData[dayIndex][player.objectId].mean
                    }
                  }

                  $scope.todayData.modeMap = babiesMap

                  calendarService.sendData($scope.todayData).then(function (returnValue) {
                    // change weekdata to closed
                    $scope.weekData[dayIndex].closed = true
                      // check if merged or not
                    if (returnValue) {
                      // popup dati backend cambiati
                      $mdDialog.show({
                        // targetEvent: $event,
                        scope: $scope, // use parent scope in template
                        preserveScope: true, // do not forget this if use parent scope
                        template: '<md-dialog>' +
                          '  <div class="cal-dialog-title"> Dati cambiati </div><md-divider></md-divider>' +
                          '  <div class="cal-dialog-text">I dati presenti sono cambiati. </div>' +
                          '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialogChanged()" class=" send-dialog-delete">' +
                          '      Ho capito' +
                          '   </div> </md-button>' +
                          '</div></md-dialog>',
                        controller: function DialogController($scope, $mdDialog) {
                          // reload and show
                          calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
                            function (calendar) {
                              createWeekData(calendar)
                              //updateTodayData(calendar)
                              $scope.sendingData = false
                            },
                            function () {
                              // manage error
                              $scope.sendingData = false
                            }
                          )

                          $scope.closeDialogChanged = function () {
                            $mdDialog.hide()
                          }
                        }
                      })
                    } else {
                      $scope.isDevEditMode = undefined;
                      // sent data
                      $mdToast.show($mdToast.simple().content('Dati inviati').position('top left'))
                        // reload and show
                      calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
                        function (calendar) {
                          createWeekData(calendar)
                          //updateTodayData(calendar)
                          $scope.sendingData = false
                        },
                        function () {
                          // manage error
                          $scope.sendingData = false
                        }
                      )
                    }
                    for (var i=0; i < $scope.todayData.babies.length; i++) {
                      $scope.todayData.babies[i].color = '';
                      $scope.todayData.babies[i].mean = '';
                    }
                    $scope.todayData.means = [];
                    $scope.closeDialog();
                  }, function () {
                    // TODO get error
                    $scope.sendingData = false
                  })
                }
              }
            }
          })
        } else {
          $mdDialog.show({
            // targetEvent: $event,
            scope: $scope, // use parent scope in template
            preserveScope: true, // do not forget this if use parent scope
            template: '<md-dialog>' +
              '  <div class="cal-dialog-title"> Dati incompleti  </div><md-divider></md-divider>' +
              '  <div class="cal-dialog-text">{{"cal_data_missing"|translate}}</div>' +
              '    <div layout="row"  layout-align="start center" ><div layout"column" flex="100" ><md-button ng-click="closeDialog()" class=" send-dialog-delete">' +
              '      Ho capito' +
              '   </div> </md-button>' +
              '</div></md-dialog>',
            controller: function DialogController($scope, $mdDialog) {
              $scope.closeDialog = function () {
                $mdDialog.hide()
                $scope.sendingData = false
              }
            }
          })
        }
      }

      $scope.switchDevEditMode = function(dayIndex) {
        if (!$scope.ENABLE_PAST_DAYS_EDIT) return;
        if ($scope.isCurrentEditDay(dayIndex)) {
          $scope.sendData(dayIndex);
        } else {
          //reset: todayData is also for past days in ENABLE_PAST_DAYS_EDIT mode
          for (var i=0; i < $scope.todayData.babies.length; i++) {
            $scope.todayData.babies[i].color = '';
            $scope.todayData.babies[i].mean = '';
          }
          $scope.todayData.means = [];

          $scope.isDevEditMode = {};
          $scope.isDevEditMode.dayIndex = dayIndex;
        }
      }

      $scope.prevWeek = function () {
        changeWeek(-1)
        $scope.isDevEditMode = undefined;
      }
      $scope.nextWeek = function () {
        changeWeek(1)
        $scope.isDevEditMode = undefined;
      }

      $scope.scrollUp = function () {
        document.getElementById('table').scrollTop -= 50
      }
      $scope.scrollDown = function () {
        document.getElementById('table').scrollTop += 50
      }

      $scope.isFuture = function (dayIndex) {
        return (new Date().setHours(0, 0, 0, 0) < $scope.week[dayIndex].setHours(0, 0, 0, 0))
      }

      $scope.isPast = function (dayIndex) {
        return (new Date().setHours(0, 0, 0, 0) > $scope.week[dayIndex].setHours(0, 0, 0, 0))
      }

      $scope.isCurrentEditDay = function (dayIndex) {
        return $scope.isDevEditMode && $scope.isDevEditMode.dayIndex == dayIndex;
      }

      function dataAreComplete(dayIndex) {
        // meteo and means must  be chosen
        if (!$scope.selectedWeather) {
          return false
        }
        for (var i = 0; i < $scope.classPlayers.length; i++) {
        	var player = $scope.classPlayers[i]
          if (!$scope.weekData[dayIndex][player.objectId].mean) {
            return false
          }
        }
        // all babies  have a mean
        return true
      }

      function getMonday(d) {
        d = new Date(d)
        d.setHours(0, 0, 0, 0)
        var day = d.getDay()
        var diff = d.getDate() - day + (day === 0 ? -6 : 1) // adjust when day is sunday
        return new Date(d.setDate(diff))
      }

      function checkDayOfTheWeek(dayFromData, indexOfWeek) {
        // compare timestamp dayFromData.day with timestamp of the $scope.week[indexOfWeek]
        // return true if it is the same day and false otherwise
        if (dayFromData.day === $scope.week[indexOfWeek].getTime()) {
          return true
        }
        return false
      }

      function setTodayIndex() {
        /* set the day of week */
        var day = new Date().getDay()
        day = day - (day === 0 ? -6 : 1)
        $scope.todayIndex = day
      }

      function changeWeek(skipWeek) {
        $scope.isLoadingCalendar = true; 
        // take date of week[0] and go 1 week before or after
        var monday = new Date($scope.week[0].getTime())
        monday.setDate(monday.getDate() + 7 * skipWeek)
        /** */
        if(skipWeek == -1){
          $scope.week = []
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            $scope.week.push(new Date(monday.getTime() + (i * 24 * 60 * 60 * 1000)))
          }
          var currentDate = new Date;
          var first = currentDate.getDate() - currentDate.getDay()+1;
          var weekStart = new Date(currentDate.setDate(first));
          var last2week = new Date(weekStart.setDate(weekStart.getDate()-13));
          if(monday  < last2week ){
          	$scope.prev2Week=false;
          }
        } else {
          $scope.prev2Week=true;
          $scope.week = []
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            $scope.week.push(new Date(monday.getTime() + (i * 24 * 60 * 60 * 1000)))
          }
        }
        
        /** */
        // $scope.week = []
        // for (var i = 0; i < 5; i++) {
        //   $scope.week.push(new Date(monday.getTime() + (i * 24 * 60 * 60 * 1000)))
        // }

        calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
          function (calendar) {
            createWeekData(calendar)
          },
          function(error) {
          	$mdToast.show($mdToast.simple().content('Errore nel caricamento dati'))
          }
        )

        // if the new week is the actual week
        var now = new Date()
        now.setHours(0, 0, 0, 0)
        if (now.getTime() >= $scope.week[0].getTime() && now.getTime() <= $scope.week[$scope.week.length - 1].getTime()) {
          setTodayIndex()
        } else {
          $scope.todayIndex = -1
        }

        setLabelWeek($scope.week)
          // $scope.labelWeek = $filter('date')($scope.week[0], 'dd') + " - "
          // $filter('date')($scope.week[$scope.week.length - 1], 'dd MMM yyyy');
      }

      function setLabelWeek(weekArray) {
        $scope.labelWeek = $filter('date')(weekArray[0], 'dd') + ' - ' +
          $filter('date')(weekArray[weekArray.length - 1], 'dd MMM yyyy')
      }

      function updateTodayData(calendar) {
        // reset the number of means
        $scope.todayData.means = {}
          // if there is today data merge it with $scope.todayData
        var today = new Date().setHours(0, 0, 0, 0)
        for (var i = 0; i < calendar.length; i++) {
          if (calendar[i].day === today) {
            // merge it
            for (var k = 0; k < $scope.todayData.babies.length; k++) {
              if (calendar[i].modeMap[$scope.todayData.babies[k].childId]) {
                //$scope.todayData.babies[k].color = $scope.returnModalitiesColor(calendar[i].modeMap[$scope.todayData.babies[k].childId])
                $scope.todayData.babies[k].color = $scope.mapModalities.find(val=>{return val.value==calendar[i].modeMap[$scope.todayData.babies[k].childId];}).color
                $scope.todayData.babies[k].mean = calendar[i].modeMap[$scope.todayData.babies[k].childId]
                if (!$scope.todayData.means[$scope.todayData.babies[k].mean]) {
                  $scope.todayData.means[$scope.todayData.babies[k].mean] = 0
                }
                $scope.todayData.means[$scope.todayData.babies[k].mean]++
              }
            }
            break
          }
        }
      }

      function setClassSize() {
        var w = window
        var d = document
        var e = d.documentElement
        var g = d.getElementsByTagName('body')[0]
          // x = w.innerWidth || e.clientWidth || g.clientWidth,
        var y = w.innerHeight || e.clientHeight || g.clientHeight
        if (document.getElementById('table')) {
          document.getElementById('table').setAttribute('style', 'height:' + (y - 64 - 100 - 130 - 50) + 'px')
        }
      }

      function createWeekData(calendar) {
        $scope.weekData = []
        var k = 0
        for (var i = 0; i < $scope.daysOfWeek; i++) {
          // get i-th day data and put baby with that object id with that setted mean
          $scope.weekData.push({})
            // if calendar[i] esiste vado avanti
          if (calendar[k]) {
            // se giorno della settimana coincide con calendar.day vado avanti altrimenti skip
            if (checkDayOfTheWeek(calendar[k], i)) {
              for (var property in calendar[k].modeMap) {
                $scope.weekData[i][property] = {
                  mean: calendar[k].modeMap[property]
                }
                // $scope.weekData[i][property].color = $scope.returnModalitiesColor(calendar[k].modeMap[property])
                $scope.weekData[i][property].color = $scope.mapModalities.find(val=>{return val.value==calendar[k].modeMap[property];}).color
                if (!$scope.weekData[i][calendar[k].modeMap[property]]) {
                  $scope.weekData[i][calendar[k].modeMap[property]] = 0
                }
                $scope.weekData[i][calendar[k].modeMap[property]] = $scope.weekData[i][calendar[k].modeMap[property]] + 1
              }
              if (calendar[k].meteo) {
                $scope.weekData[i].meteo = calendar[k].meteo
              }
              // if (calendar[i].closed) {
              $scope.weekData[i].closed = calendar[k].closed
              k++
            } else {
              // add entire day of null data
              for (var prop in calendar[k].modeMap) {
                $scope.weekData[i][prop] = {}
              }
            }
          } else {
            // add entire day of null data
          }
          for (var z = 0; z < $scope.classPlayers.length; z++) {
          	var player = $scope.classPlayers[z]
          	if(!$scope.weekData[i][player.objectId]) {
          		$scope.weekData[i][player.objectId] = {}
          	}
          }
        }
        console.log("weekData::",$scope.weekData);
        console.log("$scope.weekData[2].bike::",$scope.weekData[2].bike);
        console.log("todayData::",$scope.todayData);
        $scope.isLoadingCalendar = false; 
      }
      /*
       * Notifications and Challenges stuff
       */
      $scope.lastNotification = null
      $scope.notificationsPoller = null

      /*
      {
        "gameId": "588889c0e4b0464e16ac40a0",
        "playerId": "5^",
        "state": [{
          "name": "test_KmSettimanali_classe_5",
          "modelName": "KmSettimanali",
          "fields": {
            "TargetTeam": "classe",
            "VirtualPrize": "biglietto aereo di test",
            "bonusPointType": "bonus_distance",
            "bonusScore": 3000,
            "counterName": "total_distance",
            "periodName": "weekly",
            "target": 10000
          },
          "start": 1486512000000,
          "completed": true,
          "dateCompleted": 1486569750531
        }]
      }
      */

      var startPoller = function () {
        /* comment this if you don't want always the last notification available */
        CacheSrv.resetLastCheck('calendar')

        var getNotifications = function () {
          dataService.getNotifications(CacheSrv.getLastCheck('calendar')).then(
            function (data) {
              if (data && data.length) {
                console.log('[Calendar] New notifications: ' + data.length)
                angular.forEach(data, function(notification) {
                	notification.data =  $scope.convertFields(notification.data)
	                if(!CacheSrv.isGameFinishedNotified(loginService.getOwnerId(),
	                		loginService.getGameId(), loginService.getClassRoom())) {
	                	if((notification.key == 'GameFinished') && 
	                			(!$scope.isGameFinishedNotificationDisplaied)) {
                  		$scope.gameFinishedNotification = notification;
                  		$scope.isGameFinishedNotificationDisplaied = true;
                      $mdDialog.show({
                        // targetEvent: $event,
                        scope: $scope, // use parent scope in template
                        preserveScope: true, // do not forget this if use parent scope
                        template: '<md-dialog-game-finisched>' +
                        	'<div class="cal-dialog-game-finisched">' +
                          '  <div class="cal-dialog-title">COMPLIMENTI!</div>' +
                          '  <div class="cal-dialog-text">{{"notif_gameFinishedDialog1" | translate}}</div>' +
                          '  <div class="cal-dialog-text">{{"notif_gameFinishedDialog2" | translate:gameFinishedNotification.data}}</div>' +
                          '  <img class="cal-dialog-img" ng-src="{{lastLeg.imageUrl}}">' +
                          '  <div class="cal-dialog-leg">{{"notif_gameFinishedDialogLeg" | translate:gameFinishedNotification.data}}</div>' +
                          '  <div layout="row" layout-align="end">' +
                          '    <div layout="column" layout-align="end">' + 
                          '      <md-button ng-click="closeDialog()" class="send-dialog-dismiss">' +
                          '        Chiudi' +
                          '      </md-button>' +
                          '    </div>' +
                          '  </div>' +
                          '</div></md-dialog-game-finisched>',
                        controller: function DialogController($scope, $mdDialog) {
                          $scope.closeDialog = function () {
                          	CacheSrv.setGameFinishedNotified(loginService.getOwnerId(),
                          			loginService.getGameId(), loginService.getClassRoom(), true);
                          	$scope.isGameFinishedNotificationDisplaied = false;
                            $mdDialog.hide();
                          }
                        }
                      })
                  	} 	
	                }
                })
                $scope.lastNotification = data[0]
                CacheSrv.updateLastCheck('calendar')
              }
            },
            function (reason) {
              console.log('[Calendar]' + reason)
            }
          )
        }
        var cleanStatesChallenges = function (arrayOfChallenges) {
          $scope.openChallenge = false;
          var challengesNotCompleted = [];
          var d = new Date();
          var now = d.getTime();
          //first get all the not completed
          for (var i = 0; i < arrayOfChallenges.length; i++) {
            if (!arrayOfChallenges[i].completed && !arrayOfChallenges[i].fields.prizeWon) {
            	if(arrayOfChallenges[i].start > now) {
            		continue;
            	}
            	if(arrayOfChallenges[i].hasOwnProperty('end')) {
            		if(arrayOfChallenges[i].end > now) {
            			challengesNotCompleted.push(arrayOfChallenges[i]);
            		}
            	} else {
            		challengesNotCompleted.push(arrayOfChallenges[i]);
            	}
            }  
          }
          if (challengesNotCompleted[0]) {
            $scope.lastChallenge.state = [challengesNotCompleted[0]];
            $scope.openChallenge = true;
          }
          for (var j = 1; j < challengesNotCompleted.length; j++) {
            if (challengesNotCompleted[j] && challengesNotCompleted[j].start > $scope.lastChallenge.state[0].start) {
              $scope.lastChallenge.state = [challengesNotCompleted[j]]
            }
          }
        }
        var getChallenges = function () {
          dataService.getChallenges().then(
            function (data) {
            	 $scope.lastChallenge = {state:[]}
              if (data && data.length) {
                console.log('[Calendar] Challenges: ' + data.length)
                for (var i = 0; i < data.length; i++) {
                  if (data[i].state) {
                    angular.forEach(data[i].state, function (state) {
                    	state.fields = $scope.convertFields(state.fields)
                      $scope.lastChallenge.state.push(state)
                    })
                  }
                }
                cleanStatesChallenges($scope.lastChallenge.state)
              }
            },
            function (reason) {
              console.log('[Calendar]' + reason)
            }
          )
        }

        getNotifications()
        getChallenges()
          // poll every 10 seconds
        $scope.poller = $interval(function () {
          getNotifications()
          getChallenges()
        }, (1000 * 10))
      }

      startPoller()

      function onResize() {
        setClassSize()
      }

      $scope.$on('$destroy', function () {
        if ($scope.poller) {
          $interval.cancel($scope.poller)
          console.log('[Calendar] poller cancelled')
        }
        window.angular.element($window).off('resize', onResize)
      })

      var appWindow = angular.element($window)
      appWindow.bind('resize', onResize)
    }
  ])
