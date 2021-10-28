angular.module('climbGame.services.map', [])
  .factory('mapService', function ($q, dataService) {
    var mapService = {}
    mapService.getStatus = function () {
      var deferr = $q.defer();
      dataService.getStatus().then(function (data) {
        //return only the path and the legs
        deferr.resolve(data);
      }, function (err) {
        deferr.reject();
      });
      return deferr.promise;
    }

    mapService.encode = function (coordinate, factor) {
      coordinate = Math.round(coordinate * factor);
      coordinate <<= 1;
      if (coordinate < 0) {
        coordinate = ~coordinate;
      }
      var output = '';
      while (coordinate >= 0x20) {
        output += String.fromCharCode((0x20 | (coordinate & 0x1f)) + 63);
        coordinate >>= 5;
      }
      output += String.fromCharCode(coordinate + 63);
      return output;
    }

    // This is adapted from the implementation in Project-OSRM
    // https://github.com/DennisOSRM/Project-OSRM-Web/blob/master/WebContent/routing/OSRM.RoutingGeometry.js
    mapService.decode = function (str, precision) {
      var index = 0,
        lat = 0,
        lng = 0,
        coordinates = [],
        shift = 0,
        result = 0,
        byte = null,
        latitude_change,
        longitude_change,
        factor = Math.pow(10, precision || 5);

      // Coordinates have variable length when encoded, so just keep
      // track of whether we've hit the end of the string. In each
      // loop iteration, a single coordinate is decoded.
      while (index < str.length) {

        // Reset shift, result, and byte
        byte = null;
        shift = 0;
        result = 0;

        do {
          byte = str.charCodeAt(index++) - 63;
          result |= (byte & 0x1f) << shift;
          shift += 5;
        } while (byte >= 0x20);

        latitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));

        shift = result = 0;

        do {
          byte = str.charCodeAt(index++) - 63;
          result |= (byte & 0x1f) << shift;
          shift += 5;
        } while (byte >= 0x20);

        longitude_change = ((result & 1) ? ~(result >> 1) : (result >> 1));

        lat += latitude_change;
        lng += longitude_change;

        coordinates.push([lat / factor, lng / factor]);
      }

      return coordinates;
    };

    mapService.encode = function (coordinates, precision) {
      if (!coordinates.length) return '';

      var factor = Math.pow(10, precision || 5),
        output = encode(coordinates[0][0], factor) + encode(coordinates[0][1], factor);

      for (var i = 1; i < coordinates.length; i++) {
        var a = coordinates[i],
          b = coordinates[i - 1];
        output += encode(a[0] - b[0], factor);
        output += encode(a[1] - b[1], factor);
      }

      return output;
    };
    mapService.resizeElementHeight = function (element, mapId) {
      var height = 0;
      var body = window.document.body;
      if (window.innerHeight) {
        height = window.innerHeight;
      } else if (body.parentElement.clientHeight) {
        height = body.parentElement.clientHeight;
      } else if (body && body.clientHeight) {
        height = body.clientHeight;
      }
      console.log('height' + height);
      element.style.height = (((height - element.offsetTop) / 2) + "px");
      this.getMap(mapId).then(function (map) {
        map.invalidateSize();
      })
    }
    // Method used to calculate a polyline length (in meters) with the sum of the distances between each point
    mapService.sumAllDistances = function (arrOfPoints) {
      var partialDist = 0;
      for (var i = 1; i < arrOfPoints.length; i++) {
        var lat1 = arrOfPoints[i - 1][0];
        var lon1 = arrOfPoints[i - 1][1];
        var lat2 = arrOfPoints[i][0];
        var lon2 = arrOfPoints[i][1];
        partialDist += mapService.getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2);
      }
      return partialDist;
    };

    // Method used to calculate the distance between two points
    mapService.getDistanceFromLatLonInKm = function (lat1, lon1, lat2, lon2) {
      var R = 6371; // Radius of the earth in km
      var dLat = mapService.deg2rad(lat2 - lat1); // deg2rad below
      var dLon = mapService.deg2rad(lon2 - lon1);
      var a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(mapService.deg2rad(lat1)) * Math.cos(mapService.deg2rad(lat2)) *
        Math.sin(dLon / 2) * Math.sin(dLon / 2);
      var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      var d = R * c; // Distance in km
      return d;
    };

    mapService.deg2rad = function (deg) {
      return deg * (Math.PI / 180)
    };

    // Method used to split a polyline in two polylines considering a percentage value.
    // Now the percentage is related with the array elements number but we can consider the real distance in meters
    mapService.retrievePercentagePoly = function (pointArr, percentage, proportionalLength) {
      var findSplitPoint = false;
      var count = 1;
      do {
        var partialPoly = pointArr.slice(0, count);
        var lengthInMeters = mapService.sumAllDistances(partialPoly) * 1000;
        if (lengthInMeters > proportionalLength) {
          findSplitPoint = true;
        } else {
          count++;
        }
      } while (!findSplitPoint);
      if (count == pointArr.length) {
        count--;
      }
      var previousPoint = pointArr[count - 1];
      var nextPoint = pointArr[count];
      var deltaY = nextPoint[0] - previousPoint[0];
      var deltaX = nextPoint[1] - previousPoint[1];
      var newX = previousPoint[1] + (deltaX * percentage);
      var newY = previousPoint[0] + (deltaY * percentage);
      var newPoint = [newY, newX];

      var partialPoly1 = pointArr.slice(0, count);
      partialPoly1.push(newPoint);
      var partialPoly2 = pointArr.slice(count, pointArr.length);
      partialPoly2.unshift(newPoint);

      var splittedPolys = [];
      splittedPolys.push(partialPoly1);
      splittedPolys.push(partialPoly2);
      return splittedPolys;
    };
    return mapService
  });
