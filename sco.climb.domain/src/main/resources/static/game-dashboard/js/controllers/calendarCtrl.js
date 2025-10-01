/* global angular */
angular.module('climbGame.controllers.calendar', [])
  .controller('calendarCtrl', ['$scope', '$filter', '$window', '$interval', '$mdDialog', '$mdToast', '$timeout', 'CacheSrv', 'dataService', 'calendarService', 'configService', 'loginService', 'profileService',
    function ($scope, $filter, $window, $interval, $mdDialog, $mdToast,$timeout, CacheSrv, dataService, calendarService, configService, loginService, profileService) {
      $scope.isLoadingCalendar = true;
      $scope.week = []
      $scope.prev2Week = true;
      $scope.selectedWeather = ''
      $scope.selectedMean = ''
      $scope.selectedMeanColor = 'cal-menu-col'
      $scope.labelWeek = ''
      $scope.sendingData = false
      $scope.roundTrip = false;
      $scope.groupMode = false;
      $scope.currentEditDayIndex = null;
      $scope.cal = {
        meanOpen: false
      }
      $scope.classMap = {}
      $scope.weekData = []
      $scope.weekDataReturn = []
      $scope.groupWeekData = [];
      for (var i = 0; i < $scope.daysOfWeek; i++) {
        $scope.groupWeekData[i] = {};
      }
      $scope.daysOfWeek = 5;
      $scope.calHeaderFlex = 25;
      $scope.todayData = {
        babies: [],
        means: {}
      }
      $scope.ENABLE_PAST_DAYS_EDIT = configService.ENABLE_PAST_DAYS_EDIT;

      $scope.lastLeg = {}
      $scope.isGameFinishedNotificationDisplaied = false;
      $scope.mapModalities = [];
      $scope.flexNum = 0;
      $scope.activeGroupInput = null;
      $scope.getLabelStyle = function (modality) {
        let baseKey = $scope.activeGroupInput?.key?.split('_')[0];
        if (baseKey !== modality.value) {
          return {};
        }

        // Calcola un colore di sfondo trasparente per evidenziare
        function hexToRgba(hex, opacity) {
          let r = parseInt(hex.substring(1, 3), 16);
          let g = parseInt(hex.substring(3, 5), 16);
          let b = parseInt(hex.substring(5, 7), 16);
          return `rgba(${r}, ${g}, ${b}, ${opacity})`;
        }

        return {
          'background-color': hexToRgba(modality.color, 0.2),
          'border-radius': '10px'
        };
      };
      $scope.$watch('groupWeekData', function(newVal) {
        if (!newVal || !$scope.activeGroupInput) return;
      
        const input = $scope.activeGroupInput;
      
        if ($scope.roundTrip) {
          if (input.keyOut) {
            let valOut = newVal[input.dayIndex][input.keyOut];
            if (valOut === null || valOut === undefined || valOut === '') {
              newVal[input.dayIndex][input.keyOut] = 0;
              valOut = 0;
            }
            input.out = valOut;
          }
          if (input.keyReturn) {
            let valRet = newVal[input.dayIndex][input.keyReturn];
            if (valRet === null || valRet === undefined || valRet === '') {
              newVal[input.dayIndex][input.keyReturn] = 0;
              valRet = 0;
            }
            input.return = valRet;
          }
        } else {
          if (input.key) {
            let val = newVal[input.dayIndex][input.key];
            if (val === null || val === undefined || val === '') {
              newVal[input.dayIndex][input.key] = 0;
              val = 0;
            }
            input.value = val;
          }
        }
      }, true);
      
      $scope.shouldShowSaveButton = function(dayIndex) {
        return $scope.isCurrentEditDay(dayIndex) && $scope.isDevEditMode;
      };
      $scope.selectFloatingInput = function(dayIndex, modalityKey) {
        if (!$scope.canEdit(dayIndex)) return;
      
        $scope.currentEditDayIndex = dayIndex;
        $scope.activeGroupInput = null;
      
        $timeout(function() {
          const color = getModalityColor(modalityKey);
          const icon = getModalityIcon(modalityKey);
          const baseKey = modalityKey.replace(/_(out|return)$/, '');
      
          if ($scope.roundTrip) {
            $scope.activeGroupInput = {
              dayIndex,
              baseKey,
              keyOut: baseKey + '_out',
              keyReturn: baseKey + '_return',
              out: $scope.groupWeekData[dayIndex][baseKey + '_out'],
              return: $scope.groupWeekData[dayIndex][baseKey + '_return'],
              color,
              icon
            };
          } else {
            $scope.activeGroupInput = {
              dayIndex,
              key: modalityKey,
              value: $scope.groupWeekData[dayIndex][modalityKey],
              color,
              icon
            };
          }
        }, 0);
      };
      
      $scope.isSelectedGroupInput = function(dayIndex, key) {
        if (!$scope.activeGroupInput) return false;
      
        // Caso roundTrip: controlla entrambe le chiavi
        if ($scope.roundTrip) {
          return $scope.activeGroupInput.dayIndex === dayIndex &&
                 (key === $scope.activeGroupInput.keyOut || key === $scope.activeGroupInput.keyReturn);
        }
      
        // Caso normale (solo andata)
        return $scope.activeGroupInput.dayIndex === dayIndex &&
               key === $scope.activeGroupInput.key;
      };
      $scope.incrementFloating = function(which) {
        if ($scope.roundTrip && which) {
          if (which === 'out') {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyOut]++;
            $scope.activeGroupInput.out++;
          } else if (which === 'return') {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyReturn]++;
            $scope.activeGroupInput.return++;
          }
        } else {
          const input = $scope.activeGroupInput;
          $scope.groupWeekData[input.dayIndex][input.key]++;
          input.value++;
        }
      };
      
      $scope.decrementFloating = function(which) {
        if ($scope.roundTrip && which) {
          if (which === 'out' &&
              $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyOut] > 0) {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyOut]--;
            $scope.activeGroupInput.out--;
          } else if (which === 'return' &&
                     $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyReturn] > 0) {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyReturn]--;
            $scope.activeGroupInput.return--;
          }
        } else {
          const input = $scope.activeGroupInput;
          if ($scope.groupWeekData[input.dayIndex][input.key] > 0) {
            $scope.groupWeekData[input.dayIndex][input.key]--;
            input.value--;
          }
        }
      };
      $scope.incrementFloating = function(which) {
        if ($scope.roundTrip && which) {
          if (which === 'out') {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyOut]++;
            $scope.activeGroupInput.out++;
          } else if (which === 'return') {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyReturn]++;
            $scope.activeGroupInput.return++;
          }
        } else {
          const input = $scope.activeGroupInput;
          $scope.groupWeekData[input.dayIndex][input.key]++;
          input.value++;
        }
      };
      
      $scope.decrementFloating = function(which) {
        if ($scope.roundTrip && which) {
          if (which === 'out' &&
              $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyOut] > 0) {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyOut]--;
            $scope.activeGroupInput.out--;
          } else if (which === 'return' &&
                     $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyReturn] > 0) {
            $scope.groupWeekData[$scope.activeGroupInput.dayIndex][$scope.activeGroupInput.keyReturn]--;
            $scope.activeGroupInput.return--;
          }
        } else {
          const input = $scope.activeGroupInput;
          if ($scope.groupWeekData[input.dayIndex][input.key] > 0) {
            $scope.groupWeekData[input.dayIndex][input.key]--;
            input.value--;
          }
        }
      };
            

      $scope.hideFloatingInput = function () {
        $scope.activeGroupInput = null;
      };
      function getModalityColor(key) {
        const baseKey = key.replace('_out', '').replace('_return', '');
        const modality = $scope.mapModalities.find(m => m.value === baseKey);
        return modality ? modality.color : 'gray';
      }

      function getModalityIcon(key) {
        const baseKey = key.replace('_out', '').replace('_return', '');
        return $scope.getIconForType(baseKey);
      }
      var returnModalitiesColor = function (type) {
        var color = ''
        switch (type) {
          case 'A piedi':
            color = 'cal-foot-friend-col'
            break
          case "Bici / Monopattino / SkateboardIn bici":
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
      setTodayIndex();
      $scope.isDevEditMode = {};
        $scope.isDevEditMode.dayIndex = $scope.todayIndex;
      setClassSize()
      for (var i = 0; i < $scope.daysOfWeek; i++) {
        $scope.week.push(new Date(getMonday(new Date()).getTime() + (i * 24 * 60 * 60 * 1000)))
      }

      setLabelWeek($scope.week)

      calendarService.setTitle().then(
        function () { },
        function () {
          // default value
        }
      )

      profileService.getProfile().then(function (profile) {

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
          function () { }
        )
        dataService.getStatus().then(
          function (data) {
            //roundtrip
            if (data.game.roundTrip) {
              $scope.roundTrip = true;
              setClassSize();
            }
            if (data.game.groupDataEntry) {
              $scope.groupMode = true;
            }
            //check the number of modalities and set color
            if (data.game.modalities.length > 0) {
              var pedibusModalityValue = false;
              dataService.getModalityMap().then(function (modalityData) {
                //$scope.mapModalities = data.game.modalities.map(val => ({ value: val, color: returnModalitiesColor(val) }));
                data.game.modalities.map(function (val) {
                  modalityData.modalities.find(function (currentValue) {
                    if (currentValue.value == val) {
                      $scope.mapModalities.push(currentValue);
                      if (currentValue.value == "pedibus") { pedibusModalityValue = true; }
                    }
                  })
                });
                console.log("mapResult::", $scope.mapModalities)
                if (pedibusModalityValue) {
                  $scope.flexNum = 100 / ($scope.mapModalities.length);
                } else { $scope.flexNum = 100 / ($scope.mapModalities.length + 1); }

                console.log("flexNum and walkPlusPedibusModalityValue", $scope.flexNum)

              }, function (er) {
                console.log("error", er)
              });
            }
            //check the Saturday
            if (data.game.daysOfWeek[5]) {
              $scope.daysOfWeek = 6;
              $scope.calHeaderFlex = 10;
              $scope.week.push(new Date(getMonday(new Date()).getTime() + (5 * 24 * 60 * 60 * 1000)))
            } else {
              console.log("Saturday is::", data.game.daysOfWeek[5])
            }
            calendarService.getCalendar($scope.week[0].getTime(), $scope.week[$scope.week.length - 1].getTime()).then(
              function (calendar) {
                createWeekData(calendar)
                //updateTodayData(calendar)
              },
              function () { }
            )
            if (data.legs && data.legs.length) {
              var pos = data.legs.length - 1;
              $scope.lastLeg = data.legs[pos]
            }
            $scope.isLoadingCalendar = false;

          }, function (err) {
            console.log("error::", err)
            $scope.isLoadingCalendar = false;

          }
        )
      }, function (err) {
        console.log(err)
        // Toast the Problem
        $mdToast.show($mdToast.simple().content($filter('translate')('toast_uname_not_valid')))
      });
      $scope.canEdit = function(dayIndex) {
        if ($scope.currentEditDayIndex !== null && $scope.currentEditDayIndex !== dayIndex) {
            return false; // ✅ blocca tutto tranne il giorno in edit
        }
        return $scope.today(dayIndex) || $scope.isCurrentEditDay(dayIndex);
    };
      $scope.showInputField = function(dayIndex, modalityKey) {
        const value = $scope.groupWeekData[dayIndex]?.[modalityKey] || 0;
        return $scope.canEdit(dayIndex) || value > 0;
      };
      
      $scope.hexToRgba = function (hex, alpha) {
        var r = 0, g = 0, b = 0;

        // Se formato corto tipo #f00
        if (hex.length == 4) {
          r = "0x" + hex[1] + hex[1];
          g = "0x" + hex[2] + hex[2];
          b = "0x" + hex[3] + hex[3];
        }
        // Se formato lungo tipo #ff0000
        else if (hex.length == 7) {
          r = "0x" + hex[1] + hex[2];
          g = "0x" + hex[3] + hex[4];
          b = "0x" + hex[5] + hex[6];
        }
        return "rgba(" + +r + "," + +g + "," + +b + "," + alpha + ")";
      };
      $scope.today = function (dayIndex) {
        if (!$scope.week || !$scope.week[dayIndex]) {
          return false;
        }
        const todayDate = new Date();
        const dayDate = new Date($scope.week[dayIndex]);

        return todayDate.getDate() === dayDate.getDate() &&
          todayDate.getMonth() === dayDate.getMonth() &&
          todayDate.getFullYear() === dayDate.getFullYear();
      };
      $scope.escape = function (str) {
        return str.replace(/"/g, '\\"');
      }
      $scope.fixInput = function (dayIndex, modality) {
        if ($scope.groupWeekData[dayIndex][modality] < 0 || isNaN($scope.groupWeekData[dayIndex][modality])) {
          $scope.groupWeekData[dayIndex][modality] = 0;
        }
      }
      $scope.getIconForType = function (type) {
        switch (type) {
          case 'walk':
            return 'directions_walk';
          case 'bike':
            return 'directions_bike';
          case 'bus':
            return 'directions_bus';
          case 'pandr':
            return 'local_parking';
          case 'carpooling':
            return 'groups'; 
          case 'car':
            return 'directions_car';
          case 'absent':
            return 'person_off';
          case 'pedibus':
            return 'directions_walk'; 
          default:
            return 'block';
        }
      };
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
        $scope.selectedMeanColor = $scope.mapModalities.find(val => { return val.value == $scope.selectedMean; }).color;
      }

      $scope.selectBabyMean = function (index, dayIndex, babyId, andata) {
        var weekData = (andata ? $scope.weekData : $scope.weekDataReturn)
        if (!$scope.selectedMean) {
          $mdToast.show($mdToast.simple().content('Selezionare un mezzo di trasporto').position('top left'))
          return
        }
        // if (andata)
        // add mean to index and remove the other
        weekData[dayIndex][babyId].color = $scope.mapModalities.find(val => { return val.value == $scope.selectedMean; }).color;
        weekData[dayIndex][babyId].mean = $scope.selectedMean
        weekData[dayIndex].walk = 0;
        weekData[dayIndex].pedibus = 0;
        weekData[dayIndex].bike = 0;
        weekData[dayIndex].bus = 0;
        weekData[dayIndex].pandr = 0;
        weekData[dayIndex].carpooling = 0;
        weekData[dayIndex].car = 0;
        weekData[dayIndex].absent = 0;
        for (var z = 0; z < $scope.classPlayers.length; z++) {
          var player = $scope.classPlayers[z]
          if (weekData[dayIndex][player.objectId].mean) {
            var mean = weekData[dayIndex][player.objectId].mean
            weekData[dayIndex][mean]++
          }
        }
      }

      $scope.today = function (index) {
        return index === $scope.todayIndex
      }
      // Inizializza il dato se serve
      $scope.ensureGroupWeekData = function (dayIndex, modality) {
        if (!$scope.groupWeekData[dayIndex]) {
          $scope.groupWeekData[dayIndex] = {};
        }
        if ($scope.groupWeekData[dayIndex][modality] === undefined) {
          $scope.groupWeekData[dayIndex][modality] = 0;
        }
      };

      // Aumenta il numero
      $scope.increaseCount = function (dayIndex, modality) {
        $scope.ensureGroupWeekData(dayIndex, modality);
        $scope.groupWeekData[dayIndex][modality]++;
      };

      // Diminuisce il numero
      $scope.decreaseCount = function (dayIndex, modality) {
        $scope.ensureGroupWeekData(dayIndex, modality);
        if ($scope.groupWeekData[dayIndex][modality] > 0) {
          $scope.groupWeekData[dayIndex][modality]--;
        }
      };
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
                if ($scope.sendingData) return;
              
                $scope.sendingData = true;
                $scope.todayData.meteo = $scope.selectedWeather;
                $scope.todayData.day = $scope.week[dayIndex].setHours(0, 0, 0, 0);
              
                var babiesMap = {};
                var babiesMapReturn = {};
              
                if ($scope.groupMode) {
                  var unassignedBabies = $scope.classPlayers.map(player => player.objectId);
              
                  if ($scope.roundTrip) {
                    // Se roundtrip è attivo, considera separatamente out e return
                    var outModalities = Object.keys($scope.groupWeekData[dayIndex]).filter(k => k.endsWith('_out'));
                    var returnModalities = Object.keys($scope.groupWeekData[dayIndex]).filter(k => k.endsWith('_return'));
              
                    // Assegna out
                    outModalities.forEach(function (modalityKey) {
                      var count = $scope.groupWeekData[dayIndex][modalityKey];
                      var baseModality = modalityKey.replace('_out', '');
                      for (var i = 0; i < count && unassignedBabies.length > 0; i++) {
                        var babyId = unassignedBabies.shift();
                        babiesMap[babyId] = baseModality;
                      }
                    });
              
                    // Ricrea la lista per il ritorno
                    unassignedBabies = $scope.classPlayers.map(player => player.objectId);
              
                    // Assegna return
                    returnModalities.forEach(function (modalityKey) {
                      var count = $scope.groupWeekData[dayIndex][modalityKey];
                      var baseModality = modalityKey.replace('_return', '');
                      for (var i = 0; i < count && unassignedBabies.length > 0; i++) {
                        var babyId = unassignedBabies.shift();
                        babiesMapReturn[babyId] = baseModality;
                      }
                    });
              
                  } else {
                    // Modalità groupMode senza roundTrip
                    Object.keys($scope.groupWeekData[dayIndex]).forEach(function (modality) {
                      var count = $scope.groupWeekData[dayIndex][modality];
                      for (var i = 0; i < count && unassignedBabies.length > 0; i++) {
                        var babyId = unassignedBabies.shift();
                        babiesMap[babyId] = modality;
                      }
                    });
                  }
                } else {
                  // Modalità normale
                  for (var i = 0; i < $scope.classPlayers.length; i++) {
                    var player = $scope.classPlayers[i];
                    if ($scope.weekData[dayIndex][player.objectId].mean) {
                      babiesMap[player.objectId] = $scope.weekData[dayIndex][player.objectId].mean;
                    }
                  }
              
                  if ($scope.roundTrip) {
                    for (var i = 0; i < $scope.classPlayers.length; i++) {
                      var player = $scope.classPlayers[i];
                      if ($scope.weekDataReturn[dayIndex][player.objectId].mean) {
                        babiesMapReturn[player.objectId] = $scope.weekDataReturn[dayIndex][player.objectId].mean;
                      }
                    }
                  }
                }
              
                // Salva le due mappe
                $scope.todayData.modeMap = babiesMap;
                if ($scope.roundTrip) {
                  $scope.todayData.modeMapReturnTrip = babiesMapReturn;
                }
              
              
                calendarService.sendData($scope.todayData).then(function (returnValue) {
                  $scope.isDevEditMode = null;
                  $scope.currentEditDayIndex = null;
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
                    $mdToast.show($mdToast.simple().content('Dati inviati').position('top right'))
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
                  for (var i = 0; i < $scope.todayData.babies.length; i++) {
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

          })
        } else {
          $mdDialog.show({
            // targetEvent: $event,
            scope: $scope, // use parent scope in template
            preserveScope: true, // do not forget this if use parent scope
            template: '<md-dialog>' +
              '  <div class="cal-dialog-title"> {{"cal_data_missing_title"|translate}} </div><md-divider></md-divider>' +
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
        $scope.activeGroupInput = null;
        if (!$scope.ENABLE_PAST_DAYS_EDIT && $scope.isPast(dayIndex)) return;
      
        if ($scope.isCurrentEditDay(dayIndex)) {
          // chiudo l'edit e invio i dati
          $scope.sendData(dayIndex); // reset avviene in confirmSend()
        } else {
          // prima resetto i dati del vecchio giorno non salvato
          var oldDayIndex = $scope.currentEditDayIndex;
          if (oldDayIndex !== null && oldDayIndex !== undefined) {
            if ($scope.groupMode) {
              // GroupMode → azzero a 0
              const dayData = $scope.groupWeekData[oldDayIndex] || {};
              $scope.mapModalities.forEach(function(m) {
                if ($scope.roundTrip) {
                  dayData[m.value + '_out'] = 0;
                  dayData[m.value + '_return'] = 0;
                } else {
                  dayData[m.value] = 0;
                }
              });
              $scope.groupWeekData[oldDayIndex] = dayData;
            } else {
              // Modalità normale → metto i bambini a null
              if ($scope.weekData && $scope.weekData[oldDayIndex]) {
                $scope.mapModalities.forEach(function(m) {
                  $scope.weekData[oldDayIndex][m.value] = null;
                });
      
                if ($scope.classPlayers) {
                  for (var i = 0; i < $scope.classPlayers.length; i++) {
                    var pid = $scope.classPlayers[i].objectId;
                    if (!$scope.weekData[oldDayIndex][pid]) $scope.weekData[oldDayIndex][pid] = {};
                    $scope.weekData[oldDayIndex][pid].mean = null;
                    $scope.weekData[oldDayIndex][pid].color = null;
                  }
                }
              }
      
              if ($scope.todayData && $scope.todayData.babies) {
                $scope.todayData.babies.forEach(function(b){
                  b.color = null;
                  b.mean = null;
                });
              }
              if ($scope.todayData) {
                $scope.todayData.means = [];
              }
            }
          }
      
          // poi entro in edit sul nuovo giorno
          $scope.isDevEditMode = {};
          $scope.isDevEditMode.dayIndex = dayIndex;
          $scope.currentEditDayIndex = dayIndex;
        }
      };
      
      $scope.isSent = function(dayIndex) {
        return $scope.weekData[dayIndex]?.closed;
      };
      
      $scope.isPending = function(dayIndex, key) {
        const count = $scope.groupWeekData[dayIndex]?.[key] || 0;
        return !$scope.weekData[dayIndex]?.closed &&
               !$scope.isCurrentEditDay(dayIndex) &&
               count > 0;
      };
      $scope.getGroupInputStyle = function(dayIndex, key) {
        const color = getModalityColor(key);
    
        // 🔹 Se ho una cella attiva (roundTrip o no), evidenzio sempre
        if ($scope.activeGroupInput) {
            let isSelected = false;
    
            if ($scope.roundTrip) {
                // Con roundTrip confronto con entrambe le chiavi
                isSelected =
                    ($scope.activeGroupInput.dayIndex === dayIndex) &&
                    (key === $scope.activeGroupInput.keyOut || key === $scope.activeGroupInput.keyReturn);
            } else {
                // Senza roundTrip confronto col singolo key
                isSelected =
                    $scope.activeGroupInput.dayIndex === dayIndex &&
                    key === $scope.activeGroupInput.key;
            }
    
            if (isSelected) {
                return {
                    backgroundColor: $scope.hexToRgba(color, 0.2),
                    borderColor: color
                };
            }
        }
    
        // 🔹 Regole originali
        if ($scope.isSent(dayIndex)) {
            return {
                backgroundColor: $scope.hexToRgba(color, 0.2),
                borderColor: color
            };
        }
        if ($scope.isCurrentEditDay(dayIndex)) {
            return {
                borderColor: color
            };
        }
        if ($scope.isPending(dayIndex, key)) {
            return {
                borderColor: color
            };
        }
    
        return {};
    };
    

    $scope.hasUnsavedData = function(dayIndex) {
      if (!$scope.isDevEditMode) return false;
      if ($scope.isCurrentEditDay(dayIndex)) {
        return true;
      }
      return false;
    };
    
    // Popup di conferma per dati non salvati
    function confirmUnsavedData(nextAction) {
      $mdDialog.show({
        template: '<md-dialog>' +
          '  <div class="cal-dialog-title">Attenzione!</div><md-divider></md-divider>' +
          '  <div class="cal-dialog-text">I dati inseriti e non salvati verranno persi. Procedere?</div>' +
          '  <div layout="row" layout-align="end center" style="margin-top:10px">' +
          '    <md-button ng-click="cancel()" class="md-primary">Annulla</md-button>' +
          '    <md-button ng-click="confirm()" class="md-primary md-raised">Procedi</md-button>' +
          '  </div>' +
          '</md-dialog>',
        controller: function DialogController($scope, $mdDialog) {
          $scope.cancel = function() { $mdDialog.hide(false); }
          $scope.confirm = function() { $mdDialog.hide(true); }
        }
      }).then(function(proceed){
        if (proceed) {
          // Reset dello stato dei dati non salvati
          $scope.isDevEditMode = undefined;
          $scope.activeGroupInput = null; 
          // $scope.currentEditDayIndex = null;
    
          nextAction();
        }      });
    }
    
    // Wrapper per cambio giorno
    $scope.switchDevEditModeSafe = function(dayIndex) {
      if ($scope.hasUnsavedData($scope.currentEditDayIndex)) {
        confirmUnsavedData(function() {
          $scope.switchDevEditMode(dayIndex);
        });
      } else {
        $scope.switchDevEditMode(dayIndex);
      }
    }
    
    // Wrapper per cambio settimana
    function changeWeekSafe(skipWeek) {
      if ($scope.hasUnsavedData($scope.currentEditDayIndex)) {
        confirmUnsavedData(function() {
          changeWeek(skipWeek);
        });
      } else {
        changeWeek(skipWeek);
      }
    }
    
    // Sostituisci i vecchi prevWeek/nextWeek con wrapper
    $scope.prevWeek = function () { changeWeekSafe(-1); }
    $scope.nextWeek = function () { changeWeekSafe(1); }

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
        if (!$scope.groupMode) {
          for (var i = 0; i < $scope.classPlayers.length; i++) {
            var player = $scope.classPlayers[i]
            if (!$scope.weekData[dayIndex][player.objectId].mean) {
              return false
            }
          }

        }
        else {
          let sum = 0;
          let sum_return = 0;
          let sum_out = 0;

          $scope.mapModalities.forEach(function (modality) {
            if ($scope.roundTrip) {
              sum_return += ($scope.groupWeekData[dayIndex][modality.value + '_return'] || 0);
              sum_out += ($scope.groupWeekData[dayIndex][modality.value + '_out'] || 0);
            } else {
              sum += ($scope.groupWeekData[dayIndex][modality.value] || 0);
            }
          });

          if (!$scope.roundTrip && $scope.classPlayers.length !== sum) {
            return false;
          }
          if ($scope.roundTrip && ($scope.classPlayers.length !== sum_out || $scope.classPlayers.length !== sum_return)) {
            return false;
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
      $scope.getEditButtonLabel = function(dayIndex) {
        if (!$scope.isCurrentEditDay(dayIndex)) return null;
        return $scope.isDevEditMode
          ? 'cal_edit_save_dev_data_button'
          : 'cal_edit_dev_data_button';
      };
      function setTodayIndex() {
        /* set the day of week */
        var day = new Date().getDay()
        day = day - (day === 0 ? -6 : 1)
        $scope.todayIndex = day
      }

      function changeWeek(skipWeek) {
        $scope.isDevEditMode = undefined;
        $scope.isLoadingCalendar = true;
        // take date of week[0] and go 1 week before or after
        var monday = new Date($scope.week[0].getTime())
        monday.setDate(monday.getDate() + 7 * skipWeek)
        /** */
        if (skipWeek == -1) {
          $scope.week = []
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            $scope.week.push(new Date(monday.getTime() + (i * 24 * 60 * 60 * 1000)))
          }
          $scope.todayIndex = $scope.week.findIndex(function(day) {
            const today = new Date();
            return day.getDate() === today.getDate() &&
                   day.getMonth() === today.getMonth() &&
                   day.getFullYear() === today.getFullYear();
          });
          
          // 🔧 Se esiste un giorno di oggi, lo seleziono per l’editing
          if ($scope.todayIndex !== -1) {
            $scope.currentEditDayIndex = $scope.todayIndex;
            $scope.isDevEditMode = true;
          }

          var currentDate = new Date;
          var first = currentDate.getDate() - currentDate.getDay() + 1;
          var weekStart = new Date(currentDate.setDate(first));
          var last2week = new Date(weekStart.setDate(weekStart.getDate() - 13));
          if (monday < last2week) {
            $scope.prev2Week = false;
          }
        } else {
          $scope.prev2Week = true;
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
          function (error) {
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


      function setClassSize() {
        var w = window
        var d = document
        var e = d.documentElement
        var g = d.getElementsByTagName('body')[0]
        // x = w.innerWidth || e.clientWidth || g.clientWidth,
        var y = w.innerHeight || e.clientHeight || g.clientHeight
        if (document.getElementById('table')) {
          document.getElementById('table').setAttribute('style', 'height:' + (y - 64 - 100 - 130 - 50 - ($scope.roundTrip ? 50 : 0)) + 'px')
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
                const baseModality = calendar[k].modeMap[property].replace(/_(out|return)$/, '');

                $scope.weekData[i][property].color = $scope.mapModalities.find(val => val.value === baseModality)?.color || '#cccccc';
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
            if (!$scope.weekData[i][player.objectId]) {
              $scope.weekData[i][player.objectId] = {}
            }
          }
        }

        if ($scope.roundTrip) {
          $scope.weekDataReturn = []
          var k = 0
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            // get i-th day data and put baby with that object id with that setted mean
            $scope.weekDataReturn.push({})
            // if calendar[i] esiste vado avanti
            if (calendar[k]) {
              // se giorno della settimana coincide con calendar.day vado avanti altrimenti skip
              if (checkDayOfTheWeek(calendar[k], i)) {
                for (var property in calendar[k].modeMapReturnTrip) {
                  $scope.weekDataReturn[i][property] = {
                    mean: calendar[k].modeMapReturnTrip[property]
                  }
                  // $scope.weekDataReturn[i][property].color = $scope.returnModalitiesColor(calendar[k].modeMapReturnTrip[property])
                  $scope.weekDataReturn[i][property].color = $scope.mapModalities.find(val => { return val.value == calendar[k].modeMapReturnTrip[property]; }).color
                  if (!$scope.weekDataReturn[i][calendar[k].modeMapReturnTrip[property]]) {
                    $scope.weekDataReturn[i][calendar[k].modeMapReturnTrip[property]] = 0
                  }
                  $scope.weekDataReturn[i][calendar[k].modeMapReturnTrip[property]] = $scope.weekDataReturn[i][calendar[k].modeMapReturnTrip[property]] + 1
                }
                if (calendar[k].meteo) {
                  $scope.weekDataReturn[i].meteo = calendar[k].meteo
                }
                // if (calendar[i].closed) {
                $scope.weekDataReturn[i].closed = calendar[k].closed
                k++
              } else {
                // add entire day of null data
                for (var prop in calendar[k].modeMapReturnTrip) {
                  $scope.weekDataReturn[i][prop] = {}
                }
              }
            } else {
              // add entire day of null data
            }
            for (var z = 0; z < $scope.classPlayers.length; z++) {
              var player = $scope.classPlayers[z]
              if (!$scope.weekDataReturn[i][player.objectId]) {
                $scope.weekDataReturn[i][player.objectId] = {}
              }
            }
          }
        }
        if ($scope.groupMode) {
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            $scope.groupWeekData[i] = {}; // reset

            if ($scope.roundTrip) {
              // ROUND TRIP: conta andata e ritorno separatamente

              // Andata (_out)
              $scope.classPlayers.forEach(function (player) {
                var babyData = $scope.weekData[i][player.objectId];
                if (babyData && babyData.mean) {
                  var key = babyData.mean + '_out';
                  if (!$scope.groupWeekData[i][key]) {
                    $scope.groupWeekData[i][key] = 0;
                  }
                  $scope.groupWeekData[i][key]++;
                }
              });

              // Ritorno (_return)
              $scope.classPlayers.forEach(function (player) {
                var babyDataReturn = $scope.weekDataReturn[i][player.objectId];
                if (babyDataReturn && babyDataReturn.mean) {
                  var key = babyDataReturn.mean + '_return';
                  if (!$scope.groupWeekData[i][key]) {
                    $scope.groupWeekData[i][key] = 0;
                  }
                  $scope.groupWeekData[i][key]++;
                }
              });

            } else {
              // SOLO ANDATA
              $scope.classPlayers.forEach(function (player) {
                var babyData = $scope.weekData[i][player.objectId];
                if (babyData && babyData.mean) {
                  var key = babyData.mean;
                  if (!$scope.groupWeekData[i][key]) {
                    $scope.groupWeekData[i][key] = 0;
                  }
                  $scope.groupWeekData[i][key]++;
                }
              });
            }
          }
        }
        if ($scope.roundTrip) {
          // Andata e ritorno: inizializza _out e _return
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            $scope.mapModalities.forEach(function (modality) {
              $scope.ensureGroupWeekData(i, modality.value + '_out');
              $scope.ensureGroupWeekData(i, modality.value + '_return');
            });
          }
        } else {
          // Solo andata: inizializza normale
          for (var i = 0; i < $scope.daysOfWeek; i++) {
            $scope.mapModalities.forEach(function (modality) {
              $scope.ensureGroupWeekData(i, modality.value);
            });
          }
        }
        $scope.isLoadingCalendar = false;
      }
      /*
       * Notifications and Challenges stuff
       */
      $scope.lastNotification = null
      $scope.notificationsPoller = null



      function onResize() {
        setClassSize()
        checkButtonVisibility()
      }
      function checkButtonVisibility() {
        const containers = document.querySelectorAll('.button-container');
        containers.forEach(container => {
          const totalWidth = container.scrollWidth;
          const visibleWidth = container.clientWidth;

          if (totalWidth > visibleWidth) {
            container.classList.add('hide-small-buttons');
          } else {
            container.classList.remove('hide-small-buttons');
          }
        });
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
    // Blocco modifica se non è permesso

  ])
