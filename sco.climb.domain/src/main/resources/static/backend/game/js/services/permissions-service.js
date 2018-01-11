angular.module('PermissionsService', []).factory('PermissionsService', function () {
    var permissionsService = {};

    var showSchools, editSchool;
    var showGames, editGame;
    var showPaths, editPath;
    var showLegs, editLegs, editLegsMultimedia;

    permissionsService.setProfilePermissions = function(roles) {
        //roles = ['teacher']; //uncomment to debug UI as a teacher!
        roles.forEach(role => {
            if (role == 'owner' || role == 'admin') {
                showSchools = true;
                editSchool = true;
                showGames = true;
                editGame = true;
                showPaths = true;
                editPath = true;
                showLegs = true;
                editLegs = true;
                editLegsMultimedia = true;
            } else if (role = 'teacher') {
                showPaths = true;
                showLegs = true;
                editLegsMultimedia = true;
            }
        });        
    }

    permissionsService.permissionEnabledShowSchools = function() {
        return showSchools;
    }
    permissionsService.permissionEnabledEditSchool = function() {
        return editSchool;
    }
    permissionsService.permissionEnabledShowGames = function() {
        return showGames;
    }
    permissionsService.permissionEnabledEditGame = function() {
        return editGame;
    }
    permissionsService.permissionEnabledShowPaths = function() {
        return showPaths;
    }
    permissionsService.permissionEnabledEditPath = function() {
        return editPath;
    }
    permissionsService.permissionEnabledShowLegs = function() {
        return showLegs;
    }
    permissionsService.permissionEnabledEditLegs = function() {
        return editLegs;
    }
    permissionsService.permissionEnabledEditLegsMultimedia = function() {
        return editLegsMultimedia;
    }

    return permissionsService;
});