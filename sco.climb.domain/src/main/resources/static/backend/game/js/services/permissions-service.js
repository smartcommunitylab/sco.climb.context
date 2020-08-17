angular.module('PermissionsService', []).factory('PermissionsService', function ($location, $state) {
    var permissionsService = {};

    var showInstitutes, editInstitute, deleteInstitute;
    var showSchools, editSchool, deleteSchool;
    var showClass, editClass, deleteClass;
    var showGames, editGame, deleteGame, functionGame;
    var infoGames;
    var showClasses, editClasses, deleteClasses;
    var paramInfo;
    var calibrationInfo;
    var showPlayer, editPlayer, deletePlayer;
    var pedibusInfo;
    var showPaths, createPaths, editPath, deletePath, clonePath, infoPath;
    var showLegs, editLegs, deleteLegs, reorderLegs;
    var showLegsMultimedia, editLegsMultimedia, deleteLegsMultimedia;
    var userRoles;

    permissionsService.getProfilePermissions = function () {
        return userRoles;
    }
    permissionsService.setProfilePermissions = function (roles) {
        //roles = ['teacher']; //uncomment to debug UI as a teacher!
        userRoles = roles;
        roles.forEach(role => {
            if (role == 'admin') {
                showInstitutes = true;
                editInstitute = true;
                deleteInstitute = true;
                showSchools = true;
                editSchool = true;
                deleteSchool = true;
                showClass = true;
                editClass = true;
                deleteClass = true;
                showGames = true;
                editGame = true;
                deleteGame = true;
                functionGame = true;
                infoGames = true;
                showClasses = true;
                editClasses = true;
                deleteClasses = true;
                paramInfo = true;
                calibrationInfo = true;
                showPlayer = true;
                editPlayer = true;
                deletePlayer = true;
                pedibusInfo = true;
                createPaths=true;
                showPaths = true;
                editPath = true;
                deletePath = true;
                clonePath = true;
                infoPath = true;
                showLegs = true;
                editLegs = true;
                deleteLegs = true;
                reorderLegs = true;
                showLegsMultimedia = true;
                editLegsMultimedia = true;
                deleteLegsMultimedia = true;
            } else if (role == 'owner' || role == 'school-owner') {
                showInstitutes = true;
                editInstitute = true;
                deleteInstitute = true;
                showSchools = true;
                editSchool = true;
                deleteSchool = true;
                showClass = true;
                editClass = true;
                deleteClass = true;
                showGames = true;
                editGame = true;
                deleteGame = true;
                functionGame = true;
                infoGames = true;
                showClasses = true;
                editClasses = true;
                deleteClasses = true;
                paramInfo = true;
                calibrationInfo = true;
                showPlayer = true;
                editPlayer = true;
                deletePlayer = true;
                pedibusInfo = true;
                createPaths = true;
                showPaths = true;
                editPath = true;
                deletePath = true;
                clonePath = true;
                infoPath = true;
                showLegs = true;
                editLegs = true;
                deleteLegs = true;
                reorderLegs = true;
                showLegsMultimedia = true;
                editLegsMultimedia = true;
                deleteLegsMultimedia = true;
            } else if (role == 'teacher') {
                showInstitutes = true;
                editInstitute = false;
                deleteInstitute = false;
                showSchools = true;
                editSchool = false;
                deleteSchool = false;
                showClass = false;
                editClass = false;
                deleteClass = false;
                showGames = true;
                editGame = false;
                deleteGame = false;
                functionGame = false;
                infoGames = true;
                showClasses = false;
                editClasses = false;
                deleteClasses = false;
                paramInfo = true;
                calibrationInfo = false;
                showPlayer = true;
                editPlayer = true;
                deletePlayer = true;
                pedibusInfo = false;
                createPaths = false;
                showPaths = true;
                editPath = true;
                deletePath = false;
                clonePath = false;
                infoPath = true;
                showLegs = true;
                editLegs = true;
                deleteLegs = true;
                reorderLegs = true;
                showLegsMultimedia = true;
                editLegsMultimedia = true;
                deleteLegsMultimedia = true;
            } else if (role == 'childrenedit') {
                showInstitutes = true;
                editInstitute = true;
                showSchools = true;
                editSchool = true;
            } else if (role == 'game-editor') {
                showInstitutes = false;
                editInstitute = false;
                deleteInstitute = false;
                showSchools = false;
                editSchool = false;
                deleteSchool = false;
                showClass = false;
                editClass = false;
                deleteClass = false;
                showGames = true;
                editGame = true;
                deleteGame = true;
                functionGame = false;
                infoGames = true;
                showClasses = false;
                editClasses = false;
                deleteClasses = false;
                paramInfo = true;
                calibrationInfo = true;
                showPlayer = false;
                editPlayer = false;
                deletePlayer = false;
                pedibusInfo = false;
                createPaths = true;
                showPaths = true;
                editPath = true;
                deletePath = true;
                clonePath = true;
                infoPath = true;
                showLegs = true;
                editLegs = true;
                deleteLegs = true;
                reorderLegs = true;
                showLegsMultimedia = true;
                editLegsMultimedia = true;
                deleteLegsMultimedia = true;
            }
        });

        permissionsService.redirectForPermissions();

    }

    permissionsService.redirectForPermissions = function () {
        //redirect to specific page in case permissions are not sufficient for default page
        //usefull when first logging! 
        var stateToRedirect = $state.current.name;
        if (stateToRedirect == 'root.institutes-list' && !showInstitutes) {
            stateToRedirect = 'root.schools-list';
        }
        if (stateToRedirect == 'root.schools-list' && !showSchools) {
            stateToRedirect = 'root.games-list';
        }
        if (stateToRedirect == 'root.games-list' && !showGames) {
            stateToRedirect = 'root.paths-list';
        }

        if (stateToRedirect != $state.current.name) {
            $state.go(stateToRedirect);
        }
    }

    permissionsService.permissionEnabledShowInstitutes = function () {
        return showInstitutes;
    }

    permissionsService.permissionEnabledEditInstitute = function () {
        return editInstitute;
    }
    permissionsService.permissionEnabledDeleteInstitute = function () {
        return deleteInstitute;
    }
    permissionsService.permissionEnabledShowSchools = function () {
        return showSchools;
    }
    permissionsService.permissionEnabledEditSchool = function () {
        return editSchool;
    }
    permissionsService.permissionEnabledDeleteSchool = function () {
        return deleteSchool;
    }
    permissionsService.permissionEnabledShowClass = function () {
        return showClass;
    }
    permissionsService.permissionEnabledEditClass = function () {
        return editClass;
    }
    permissionsService.permissionEnabledDeleteClass = function () {
        return deleteClass;
    }
    permissionsService.permissionEnabledShowGames = function () {
        return showGames;
    }
    permissionsService.permissionEnabledEditGame = function () {
        return editGame;
    }
    permissionsService.permissionEnabledDeleteGame = function () {
        return deleteGame;
    }
    permissionsService.permissionEnabledFunctionGame = function () {
        return functionGame;
    }
    permissionsService.permissionEnabledInfoGame = function () {
        return infoGames;
    }
    permissionsService.permissionEnabledShowClasses = function () {
        return showClasses;
    }
    permissionsService.permissionEnabledEditClasses = function () {
        return editClasses;
    }
    permissionsService.permissionEnabledDeleteClasses = function () {
        return deleteClasses;
    }
    permissionsService.permissionEnabledParamInfo = function () {
        return paramInfo;
    }
    permissionsService.permissionEnabledCalibrationInfo = function () {
        return calibrationInfo;
    }
    permissionsService.permissionEnabledShowPlayer = function () {
        return showPlayer;
    }
    permissionsService.permissionEnabledEditPlayer = function () {
        return editPlayer;
    }
    permissionsService.permissionEnabledDeletePlayer = function () {
        return deletePlayer;
    }
    permissionsService.permissionEnabledPedibusInfo = function () {
        return pedibusInfo;
    }
    permissionsService.permissionEnabledShowPaths= function () {
        return showPaths;
    }
    permissionsService.permissionEnabledEditPaths = function () {
        return editPath;
    }
    permissionsService.permissionEnabledCreatePaths = function () {
        return createPaths;
    }
    permissionsService.permissionEnabledDeletePaths = function () {
        return deletePath;
    }
    permissionsService.permissionEnabledClonePaths = function () {
        return clonePath;
    }
    permissionsService.permissionEnabledInfoPath = function () {
        return infoPath;
    }
    permissionsService.permissionEnabledShowLegs = function () {
        return showLegs;
    }
    permissionsService.permissionEnabledEditLegs = function () {
        return editLegs;
    }
    permissionsService.permissionEnabledDeleteLegs = function () {
        return deleteLegs;
    }
    permissionsService.permissionEnabledReorderLegs = function () {
        return reorderLegs;
    }
    permissionsService.permissionEnabledShowLegsMultimedia = function () {
        return showLegsMultimedia;
    }
    permissionsService.permissionEnabledEditLegsMultimedia = function () {
        return editLegsMultimedia;
    }
    permissionsService.permissionEnabledDeleteLegsMultimedia = function () {
        return deleteLegsMultimedia;
    }




 


    return permissionsService;
});