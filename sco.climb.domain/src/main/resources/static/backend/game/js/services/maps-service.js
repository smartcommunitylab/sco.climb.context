angular.module('MapsService', [])

    // Google maps shape drawing
    .service('drawMap', function () {
        var map;            // contains the google.maps.Map istance
        var polyPath;       // the line drawed on the map
        var markers = [];   // Array of legs markers
        // Draw the map with his shape
        this.createMap = function (idMap, poisArray) {
            // Google Maps + path edit
            map = new google.maps.Map(document.getElementById(idMap), {
                center: {
                    lat: parseFloat(poisArray[0].geocoding[1]),
                    lng: parseFloat(poisArray[0].geocoding[0])
                },
                zoom: 10
            });


           
            // Inizialize the polyline on the map
            polyPath = new google.maps.Polyline({
                geodesic: true,
                strokeColor: '#2980b9',
                strokeOpacity: 1.0,
                strokeWeight: 4,
                map: map,
                editable: false
            });
            // Genera la polyline del percorso
            generatePath(poisArray);
            // Draw markers on the map
            drawMarkers(poisArray);
            zoomToCoverPath(map, polyPath);
        };
        var zoomToCoverPath = function (map, path) {
            var bounds = new google.maps.LatLngBounds();
            path.getPath().forEach(function (latLng) {
                bounds.extend(latLng);
            });
            map.fitBounds(bounds);
        }

        var generatePath = function (poisArray) {
            var generatedPath = new google.maps.MVCArray();
            for (var i = 1; i < poisArray.length; i++) {
                google.maps.geometry.encoding.decodePath(poisArray[i].polyline).forEach(function (element) {         // aggiunge ogni elemento LatLng della polyline decodificata alla polyline da visualizzare
                    generatedPath.push(element);
                });
            }
            polyPath.setPath(generatedPath);
        };

        this.getPathLength = function () {
            return google.maps.geometry.spherical.computeLength(polyPath.getPath());
        };

        // Create and draw a list of markers on the map
        function drawMarkers(legs) {
            legs.forEach(function (element, i) {
                var contentString = '<div id="content">' +
                    i + '. ' + element.name
                '</div>';
                var infowindow = new google.maps.InfoWindow({
                    content: contentString
                });

                //   var marker = new google.maps.Marker({
                //     position: uluru,
                //     map: map,
                //     title: 'Uluru (Ayers Rock)'
                //   });
                var marker = new google.maps.Marker({
                    position: {
                        lat: parseFloat(element.geocoding[1]),
                        lng: parseFloat(element.geocoding[0])
                    },
                    map: map,
                    animation: google.maps.Animation.DROP,
                    label: (i + 1).toString()
                })
                marker.addListener('click', function () {
                    infowindow.open(map, marker);
                });
                markers.push(marker);
            })
            // for (var i = 0; i < legs.length; i++) {

            // }
        }

        // Shows markers on the map
        this.showMarkers = function () {
            for (var i in markers)
                markers[i].setMap(map);
        };

        // Hides markers on the map
        this.hideMarkers = function () {
            for (var i in markers) {
                markers[i].setMap(null);
            }
        };
    })

    // This service draws the map in the leg page
    .service("drawMapLeg", function ($rootScope) {
        // Proprietà della leg
        var prevPoiCoordinates;
        var travelType;
        var directionChangeListener = null;

        // Oggetti di Google Maps
        var map;
        var prevPoiMarker;      // usato solo nella polyline in linea d'aria
        var thisPoiMarker;
        var polyPath;
        var directionsService = new google.maps.DirectionsService();
        var directionsDisplay;

        this.createMap = function (idMap, idHintField, prevPoiCoords, thisPoiCoordinates, customWaypoints, arriveBy) {       // (prevPoiCoordinates: coordinate del leg precedente (==null se il metodo viene chiamato dal 1° leg))
            // Inizializza variabili del servizio
            prevPoiCoordinates = prevPoiCoords;
            travelType = arriveBy;

            // Inizializza l'oggetto mappa
            map = new google.maps.Map(document.getElementById(idMap), {
                center: {
                    lat: parseFloat(thisPoiCoordinates.lat),
                    lng: parseFloat(thisPoiCoordinates.lng)
                },
                zoom: 13
            });
            google.maps.LatLng.prototype.kmTo = function (a) {
                var e = Math, ra = e.PI / 180;
                var b = this.lat() * ra, c = a.lat() * ra, d = b - c;
                var g = this.lng() * ra - a.lng() * ra;
                var f = 2 * e.asin(e.sqrt(e.pow(e.sin(d / 2), 2) + e.cos(b) * e.cos
                    (c) * e.pow(e.sin(g / 2), 2)));
                return f * 6378.137;
            }
            google.maps.Polyline.prototype.inKm = function (n) {
                var a = this.getPath(n), len = a.getLength(), dist = 0;
                for (var i = 0; i < len - 1; i++) {
                    dist += a.getAt(i).kmTo(a.getAt(i + 1));
                }
                return dist;
            }

            var autocomplete = new google.maps.places.Autocomplete(document.getElementById(idHintField));
            autocomplete.bindTo('bounds', map);

            autocomplete.addListener('place_changed', function () {
                var place = autocomplete.getPlace();
                if (!place.geometry) {
                    // User entered the name of a Place that was not suggested and
                    // pressed the Enter key, or the Place Details request failed.
                    window.alert("Nessun luogo trovato per: '" + place.name + "'");
                    return;
                }

                thisPoiMarker.setPosition(place.geometry.location);
                reloadMarkerPosition();
            });


            // Inizializza l'oggetto polyline per la tratta in linea d'aria, nel caso venga usata
            polyPath = new google.maps.Polyline({
                // geodesic: true,
                editable: true,
                strokeColor: '#2980b9',
                strokeOpacity: 1.0,
                strokeWeight: 4
            });

            // Inizializza il servizio per il rendering del percorso a piedi
            directionsDisplay = new google.maps.DirectionsRenderer({
                draggable: true,
                map: map,
                suppressMarkers: true,
                preserveViewport: true
            });

            //    directionsDisplay.addListener('directions_changed', function() {
            // $rootScope.$broadcast('poiMapTotalKmChanged', computeTotalDistance(directionsDisplay.getDirections()));
            // if (!directionChangeListener)
            {
                directionChangeListener = directionsDisplay.addListener('directions_changed', function () {
                    $rootScope.$broadcast('poiMapTotalKmChanged', computeTotalDistance(directionsDisplay.getDirections()));
                    // $rootScope.$broadcast('poiMapTotalKmFirst', computeTotalDistance(directionsDisplay.getDirections()));
                })
            }
            // });

            // Inizializza gli oggetti marker
            prevPoiMarker = new google.maps.Marker({
                position: prevPoiCoordinates,
                label: 'P',
                draggable: false,
                map: map
            });
            thisPoiMarker = new google.maps.Marker({
                map: map,
                draggable: true,
                position: thisPoiCoordinates
            });

            thisPoiMarker.addListener('dragend', reloadMarkerPosition);
            drawPolyline(customWaypoints, true);
        };

        this.setTravelType = function (newTravelType) {
            travelType = newTravelType;
            if ((travelType === 'foot') || (travelType === 'car')) {
                polyPath.setPath([]);  						// azzera la polyline della tratta in linea d'aria
                directionsDisplay.setMap(map); 		// visualizza l'itinerario calcolato dal DirectionService
            } else {
                directionsDisplay.setMap(null);		// cancella itinerario del DirectionService 
            }
            drawPolyline();
        };

        var drawPolyline = function (customWaypoints, zoomToFit) {
            if (prevPoiCoordinates !== null)     // se si tratta del 1° LEG ovviamente la polyline non viene disegnata
            {
                // calcola l'itinerario seguendo le strade
                if ((travelType === 'foot') || (travelType === 'car')) {
                    var mapTravelMode = (travelType === 'foot') ? 'WALKING' : 'DRIVING';
                    var getFormattedWaypoints = function () {
                        var toRtn = [];
                        if (customWaypoints) {
                            customWaypoints.forEach(function (waypoint) {
                                toRtn.push({
                                    location: new google.maps.LatLng(waypoint.latitude, waypoint.longitude),
                                    stopover: false
                                });
                            });
                        }
                        return toRtn;
                    };
                    var request = {
                        origin: prevPoiCoordinates,
                        destination: thisPoiMarker.getPosition(),
                        travelMode: mapTravelMode,
                        waypoints: getFormattedWaypoints()
                    };
                    directionsService.route(request, function (result, status) {
                        if (status === 'OK') {
                            if (zoomToFit) {
                                directionsDisplay.setOptions({ preserveViewport: false });
                            } else {
                                directionsDisplay.setOptions({ preserveViewport: true });
                            }
                            directionsDisplay.setDirections(result);
                            //  $rootScope.$broadcast('poiMapTotalKmChanged', computeTotalDistance(result));

                        }
                        else
                            window.alert('Errore nella richiesta: ' + status);
                    });
                }
                else {
                    // calcolo della tratta in linea d'aria
                    if (customWaypoints) {
                        var decoded =google.maps.geometry.encoding.decodePath(customWaypoints);
                        polyPath.setPath(decoded);
                        polyPath.setMap(map);
                        //broadcast the new value to the page and update
                         $rootScope.$broadcast('poiMapTotalKmChanged', getDistanceFromLatLonInKm(decoded[0].lat, decoded[0].lng, decoded[decoded.length-1].lat, decoded[decoded.length-1].lng));

                    } else {
                        polyPath.setPath([prevPoiCoordinates, thisPoiMarker.getPosition()]);
                        polyPath.setMap(map);
                        //broadcast the new value to the page and update
                         $rootScope.$broadcast('poiMapTotalKmChanged', getDistanceFromLatLonInKm(prevPoiCoordinates.lat, prevPoiCoordinates.lng, thisPoiMarker.position.lat(), thisPoiMarker.position.lng()));
                    }
                }
            }
        };

        function getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
            var R = 6371; // Radius of the earth in km
            var dLat = deg2rad(lat2 - lat1);  // deg2rad below
            var dLon = deg2rad(lon2 - lon1);
            var a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
                ;
            var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            var d = R * c; // Distance in km
            return Number(d.toFixed(3)); //return 3 float number as a number 
        }

        function deg2rad(deg) {
            return deg * (Math.PI / 180)
        }
        var computeTotalDistance = function (result) {
            var total = 0;
            var myroute = result.routes[0];
            for (var i = 0; i < myroute.legs.length; i++) {
                total += myroute.legs[i].distance.value;
            }
            total = total / 1000;
            return total;
        }
        
        var reloadMarkerPosition = function () {
            // if (!directionChangeListener)
            // {directionChangeListener = directionsDisplay.addListener('directions_changed', function() {
            //     $rootScope.$broadcast('poiMapTotalKmChanged', computeTotalDistance(directionsDisplay.getDirections()));
            // })}
            drawPolyline();
            centerOnLastMarker();
            var length = 0;
            if (polyPath.getPath().length != 0)
                length = polyPath.inKm();
            else if (directionsDisplay.direction)
                length = (directionsDisplay.directions.routes[0].legs[0].distance.value) / 1000;

            //var length = google.maps.geometry.spherical.computeLength(directionsDisplay.getPath());

            $rootScope.$broadcast('poiMarkerPosChanged', thisPoiMarker.position.lat(), thisPoiMarker.position.lng(), true, length);       // avviso che la posizione del marker è cambiata
        }
        var centerOnLastMarker = function () {
            map.panTo(thisPoiMarker.getPosition());
            if (map.getZoom() < 10) map.setZoom(10);
        }

        this.calculateMarkerPosFromDistance = function (distance) {
            var heading = google.maps.geometry.spherical.computeHeading(prevPoiMarker.getPosition(), thisPoiMarker.getPosition());
            thisPoiMarker.setPosition(google.maps.geometry.spherical.computeOffset(prevPoiMarker.getPosition(), distance, heading));
            drawPolyline();
            var length = calculatelength();
            $rootScope.$broadcast('poiMarkerPosChanged', thisPoiMarker.position.lat(), thisPoiMarker.position.lng(), false, length);        // false: non svuotare il campo lunghezza linea
        };

        this.getPathPolyline = function ()       // restituisce la polyline del percorso selezionato
        {
            if ((travelType === 'foot') || (travelType === 'car'))
                return directionsDisplay.getDirections().routes[0].overview_polyline;
            else
                return google.maps.geometry.encoding.encodePath(polyPath.getPath());
        };
        this.getCustomWayPoint = function (polyline) {
            var customWayPoints = [];
            var directions = directionsDisplay.getDirections();
            if (directions && (travelType != 'plane') && (travelType != 'boat')) {
                var tmp = directionsDisplay.getDirections().routes[0].legs[0].via_waypoint;
                for (var i = 0; i < tmp.length; i++) {
                    var obj = {
                        latitude: tmp[i].location.lat(),
                        longitude: tmp[i].location.lng()
                    };
                    customWayPoints[i] = obj;
                }
            } else {
                //if not directions and is boat or plane get customWayPoints from decode
                if  ((travelType === 'plane') || (travelType === 'boat')){
                    var decoded = google.maps.geometry.encoding.decodePath(polyline);
                    if (decoded.length>2){
                        for (var k = 1; k< decoded.length-1; k++) {
                            var obj = {
                                latitude: decoded[k].lat(),
                                longitude: decoded[k].lng()
                            };
                            customWayPoints.push(obj);
                        }
                    }
                }
            }
            return customWayPoints;
        }

        this.updateMarker = function (lat, lng) {           // se le coordinate vengono inserite attraverso i campi di testo
            thisPoiMarker.setPosition(new google.maps.LatLng(lat, lng));
            drawPolyline();
        };
    })

    .service("drawMapLine", function ($rootScope, $q) {
        var map;
        var directionsService = new google.maps.DirectionsService();
        var directionsDisplay;
        var stopsMarkers = [];
        var stopsArray = [];

        var serviceScope = this;        // serve per poter chiamare le funzioni dichiarate con "this.NOME" dentro altre funzioni

        this.createMap = function (idMap, stops) {
            // Inizializza l'oggetto mappa
            map = new google.maps.Map(document.getElementById(idMap), {
                center: {
                    lat: 45.88326,      // posizione di default
                    lng: 11.00145
                },
                zoom: 13
            });
            google.maps.LatLng.prototype.kmTo = function (a) {
                var e = Math, ra = e.PI / 180;
                var b = this.lat() * ra, c = a.lat() * ra, d = b - c;
                var g = this.lng() * ra - a.lng() * ra;
                var f = 2 * e.asin(e.sqrt(e.pow(e.sin(d / 2), 2) + e.cos(b) * e.cos
                    (c) * e.pow(e.sin(g / 2), 2)));
                return f * 6378.137;
            }
            google.maps.Polyline.prototype.inKm = function (n) {
                var a = this.getPath(n), len = a.getLength(), dist = 0;
                for (var i = 0; i < len - 1; i++) {
                    dist += a.getAt(i).kmTo(a.getAt(i + 1));
                }
                return dist;
            }
            // Inizializza il servizio per il rendering del percorso a piedi
            directionsDisplay = new google.maps.DirectionsRenderer({ markerOptions: { visible: false } });
            directionsDisplay.setMap(map);

            serviceScope.updateMarkers(stops);
        };

        this.getMapCenter = function () {
            return map.getCenter();
        };

        this.updateMarkers = function (stops) {
            for (var i = 0; i < stopsMarkers.length; i++)
                stopsMarkers[i].setMap(null);

            stopsMarkers = [];
            stopsArray = stops;
            for (i = 0; i < stopsArray.length; i++) {
                stopsMarkers.push(new google.maps.Marker({
                    position: {
                        lat: parseFloat(stopsArray[i].geocoding[1]),
                        lng: parseFloat(stopsArray[i].geocoding[0])
                    },
                    map: map,
                    draggable: true,
                    label: (i + 1).toString()
                }));

                (function (e) {          // qui sta spiegato il perché di sta roba contorta: https://stackoverflow.com/questions/16724620/mutable-variable-is-accessible-from-closure-how-can-i-fix-this (seconda risposta)
                    stopsMarkers[e].addListener('mouseup', function () {
                        $rootScope.$broadcast('stopMarkerPosChanged', e, stopsMarkers[e].position.lat(), stopsMarkers[e].position.lng());       // avviso che la posizione del marker è cambiata
                    })
                })(i);
            }

            if (stopsMarkers.length > 1)
                serviceScope.drawPath();
            else
                directionsDisplay.setMap(null);
        };

        function calculateWaypoints() {
            var waypoints = [];
            for (var i = 1; i < stopsMarkers.length - 1; i++) {
                waypoints.push({
                    location: stopsMarkers[i].getPosition(),
                    stopover: false
                });
            }
            return waypoints;
        }

        this.drawPath = function () {
            if (directionsDisplay.map === null)
                directionsDisplay.setMap(map);

            var request = {
                origin: stopsMarkers[0].getPosition(),
                destination: stopsMarkers[stopsMarkers.length - 1].getPosition(),
                waypoints: calculateWaypoints(),
                travelMode: 'WALKING'
            };
            directionsService.route(request, function (result, status) {
                if (status === 'OK')
                    directionsDisplay.setDirections(result);
                else
                    window.alert('Errore nella richiesta: ' + status);
            });
        }


        //function that calculate score and direction of a path given indexes of starting and ending points
        this.recalculateLegs = function (legsArray, starting, ending) {
            //for every leg of array
            //recalculate path using points e get the new value and sum it to the previous
            for (var i=starting; i<ending; i++)
            if (i != 0)     // se si tratta del 1° LEG salta il ricalcolo
            {
                // calcola l'itinerario seguendo le strade
                if ((legsArray[i].transport === 'foot') || (legsArray[i].transport === 'car')) {
                    var mapTravelMode = (legsArray[i].transport === 'foot') ? 'WALKING' : 'DRIVING';
                    var getFormattedWaypoints = function () {
                        var toRtn = [];
                        if (legsArray[i].additionalPoints) {
                            legsArray[i].additionalPoints.forEach(function (waypoint) {
                                toRtn.push({
                                    location: new google.maps.LatLng(waypoint.latitude, waypoint.longitude),
                                    stopover: false
                                });
                            });
                        }
                        return toRtn;
                    };
                    var request = {
                        origin: { lat: legsArray[i-1].geocoding[1], lng: legsArray[i-1].geocoding[0] },
                        destination: { lat: legsArray[i].geocoding[1], lng: legsArray[i].geocoding[0] },
                        travelMode: mapTravelMode,
                        waypoints: getFormattedWaypoints()
                    };
                    directionsService.route(request, function (result, status) {
                        if (status === 'OK') {
                            console.log(JSON.stringify(result));
                            // if (zoomToFit) {
                            //     directionsDisplay.setOptions({ preserveViewport: false });
                            // } else {
                            //     directionsDisplay.setOptions({ preserveViewport: true });
                            // }
                            // directionsDisplay.setDirections(result);
                            //  $rootScope.$broadcast('poiMapTotalKmChanged', computeTotalDistance(result));

                        }
                        else
                            window.alert('Errore nella richiesta: ' + status);
                    });
                }
                else {
                    // calcolo della tratta in linea d'aria
                    if (legsArray[i].additionalPoints) {
                        polyPath.setPath(google.maps.geometry.encoding.decodePath(legsArray[i].additionalPoints));
                        //polyPath.setMap(map);
                        //broadcast the new value to the page and update
                        console.log(getDistanceFromLatLonInKm(legsArray[i].additionalPoints[0].latitude, legsArray[i].additionalPoints[0].longitude, legsArray[i].additionalPoints[legsArray[i].additionalPoints.length].latitude, legsArray[i].additionalPoints[legsArray[i].additionalPoints.length].longitude));

                    } else {
                        polyPath.setPath([{ lat: legsArray[i-1].geocoding[1], lng: legsArray[i-1].geocoding[0] }, { lat: legsArray[i].geocoding[1], lng: legsArray[i].geocoding[0] }]);
                        // polyPath.setMap(map);
                        //broadcast the new value to the page and update
                        console.log( getDistanceFromLatLonInKm(legsArray[i-1].geocoding[1], legsArray[i-1].geocoding[0], legsArray[i].geocoding[1], legsArray[i-1].geocoding[0]));
                    }
                }
            }
        }

        this.selectMode = function (sMode) {
            var mode = google.maps.TravelMode.WALKING;
            if (sMode.toLowerCase() == 'foot') {
                mode = google.maps.TravelMode.WALKING
            } else if (sMode.toLowerCase() == 'transit') {
                mode = google.maps.TravelMode.TRANSIT;
            } else if (sMode.toLowerCase() == 'car') {
                mode = google.maps.TravelMode.DRIVING;
            }
            return mode;

        }

        this.route = function (request) {
            var deferred = $q.defer();
            directionsService.route(request, function (response, status) {
                if (status === 'OK')
                    deferred.resolve(response);
                else
                    deferred.reject(status);
            });
            return deferred.promise;
        }

        this.createEncodings = function (coords) {
            var deferred = $q.defer();

            var i = 0;
            var plat = 0;
            var plng = 0;
            var encoded_points = "";

            for (i = 0; i < coords.length; ++i) {
                var lat = coords[i][0];
                var lng = coords[i][1];
                encoded_points += this.encodePoint(plat, plng, lat, lng);
                plat = lat;
                plng = lng;
            }
            // close polyline...why?
            //encoded_points += this.encodePoint(plat, plng, coords[0][0], coords[0][1]);
            deferred.resolve(encoded_points);

            return deferred.promise;
        }

        this.encodePoint = function (plat, plng, lat, lng) {
            var late5 = Math.round(lat * 1e5);
            var plate5 = Math.round(plat * 1e5)

            var lnge5 = Math.round(lng * 1e5);
            var plnge5 = Math.round(plng * 1e5)

            dlng = lnge5 - plnge5;
            dlat = late5 - plate5;

            return this.encodeSignedNumber(dlat) + this.encodeSignedNumber(dlng);
        }

        this.encodeSignedNumber = function (num) {
            var sgn_num = num << 1;

            if (num < 0) {
                sgn_num = ~(sgn_num);
            }

            return (this.encodeNumber(sgn_num));
        }

        this.encodeNumber = function (num) {
            var encodeString = "";

            while (num >= 0x20) {
                encodeString += (String.fromCharCode((0x20 | (num & 0x1f)) + 63));
                num >>= 5;
            }

            encodeString += (String.fromCharCode(num + 63));
            return encodeString;
        }
    });