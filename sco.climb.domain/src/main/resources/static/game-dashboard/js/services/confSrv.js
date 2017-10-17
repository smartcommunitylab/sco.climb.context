/* global angular */
angular.module('climbGame.services.conf', [])
  .factory('configService', function () {
    var configService = {}
    var DEVELOPMENT = true
    //var URL = 'https://' + (DEVELOPMENT ? 'climbdev' : 'climb') + '.smartcommunitylab.it/v2'
    var URL = 'http://192.168.42.60:6020/domain'
    var FOOT_CONSTANT = 'piedi'
    var BOAT_CONSTANT = 'nave'
    var PLANE_CONSTANT = 'aereo'
    var httpTimeout = 10000
    var DEFAULT_CENTER_MAP = [37.973378, 23.730957]
    var DEFAULT_ZOOM_MAP = 4
    var DEFAULT_ZOOM_POI = 9
      // var APP_BUILD = ''

    configService.getURL = function () {
      return URL
    }

    configService.httpTimout = function () {
      return httpTimeout
    }

    configService.getFootConstant = function () {
      return FOOT_CONSTANT
    }

    configService.getBoatConstant = function () {
      return BOAT_CONSTANT
    }

    configService.getPlaneConstant = function () {
      return PLANE_CONSTANT
    }
    configService.getDefaultMapCenterConstant = function () {
      return DEFAULT_CENTER_MAP
    }
    configService.getDefaultZoomMapConstant = function () {
      return DEFAULT_ZOOM_MAP
    }
    configService.getDefaultZoomPoiConstant = function () {
      return DEFAULT_ZOOM_POI
    }

    return configService
  })
