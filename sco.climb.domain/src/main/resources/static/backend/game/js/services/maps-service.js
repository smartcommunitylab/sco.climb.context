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
                lat: parseFloat(poisArray[0].coordinates.lat),
                lng: parseFloat(poisArray[0].coordinates.lng)
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
    };

    var generatePath = function (poisArray){
        var generatedPath = new google.maps.MVCArray();
        for (var i = 1; i < poisArray.length; i++)
        {
            google.maps.geometry.encoding.decodePath(poisArray[i].polyline).forEach(function(element) {         // aggiunge ogni elemento LatLng della polyline decodificata alla polyline da visualizzare
                generatedPath.push(element);
            });
        }
        polyPath.setPath(generatedPath);
    };

    this.getPathLength = function() {
        return google.maps.geometry.spherical.computeLength(polyPath.getPath());
    };

    // Create and draw a list of markers on the map
    function drawMarkers(legs) {
        for (var i = 0; i < legs.length; i++) {
            markers.push(new google.maps.Marker({
                position: {
                    lat: parseFloat(legs[i].coordinates.lat),
                    lng: parseFloat(legs[i].coordinates.lng)
                },
                map: map
            }));
        }
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
.service("drawMapPoi", function ($rootScope) {
    // Proprietà della leg
    var prevPoiCoordinates;
    var travelType;

    // Oggetti di Google Maps
    var map;
    var prevPoiMarker;      // usato solo nella polyline in linea d'aria
    var thisPoiMarker;
    var polyPath;
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay;

    this.createMap = function (idMap, prevPoiCoords, thisPoiCoordinates, arriveBy) {       // (prevPoiCoordinates: coordinate del leg precedente (==null se il metodo viene chiamato dal 1° leg))
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

        // Inizializza l'oggetto polyline per la tratta in linea d'aria, nel caso venga usata
        polyPath = new google.maps.Polyline({
            geodesic: true,
            strokeColor: '#2980b9',
            strokeOpacity: 1.0,
            strokeWeight: 4,
            map: map,
            editable: false
        });

        // Inizializza il servizio per il rendering del percorso a piedi
        directionsDisplay = new google.maps.DirectionsRenderer();
        directionsDisplay.setMap(map);

        // Inizializza gli oggetti marker
        prevPoiMarker = new google.maps.Marker({
            position: prevPoiCoordinates,
            label: 'P',
            draggable: false,
            map: null
        });
        thisPoiMarker = new google.maps.Marker({
            map: map,
            draggable: true,
            position: thisPoiCoordinates
        });

        drawPolyline();
        thisPoiMarker.addListener('mouseup', function() {       // listener che ridisegna il percorso/polyline ogniqualvolta si sposta il marker
            drawPolyline();
            $rootScope.$broadcast('poiMarkerPosChanged', thisPoiMarker.position.lat(), thisPoiMarker.position.lng(), true);       // avviso che la posizione del marker è cambiata
        });
    };

    this.setTravelType = function(newTravelType)
    {
        travelType = newTravelType;
        if(travelType !== 'foot')
        {
            directionsDisplay.setMap(null);         // nasconde l'itinerario calcolato dal DirectionService e
            prevPoiMarker.setMap(map);              // mostra il marker del LEG precedente (feature meramente estetica)
        }
        else if(directionsDisplay.map === null)
        {
            directionsDisplay.setMap(map);          // mostra nuovamente l'itinerario calcolato dal DirectionService,
            polyPath.setPath([]);                   // azzera la polyline della tratta in linea d'aria
            prevPoiMarker.setMap(null);             // nasconde il marker del LEG precedente
        }
        drawPolyline();
    };

    var drawPolyline = function()
    {
        if(prevPoiCoordinates !== null)     // se si tratta del 1° LEG ovviamente la polyline non viene disegnata
        {
            if(travelType === 'foot')       // calcola l'itinerario seguendo le strade
            {
                var request = {
                    origin: prevPoiCoordinates,
                    destination: thisPoiMarker.getPosition(),
                    travelMode: 'WALKING'
                };
                directionsService.route(request, function(result, status) {
                    if (status === 'OK')
                        directionsDisplay.setDirections(result);
                    else
                        window.alert('Errore nella richiesta: ' + status);
                });
            }
            else        // calcolo della tratta in linea d'aria
                polyPath.setPath([prevPoiCoordinates, thisPoiMarker.getPosition()]);
        }
    };

    this.calculateMarkerPosFromDistance = function(distance)
    {
        var heading = google.maps.geometry.spherical.computeHeading(prevPoiMarker.getPosition(), thisPoiMarker.getPosition());
        thisPoiMarker.setPosition(google.maps.geometry.spherical.computeOffset(prevPoiMarker.getPosition(), distance, heading));
        drawPolyline();
        $rootScope.$broadcast('poiMarkerPosChanged', thisPoiMarker.position.lat(), thisPoiMarker.position.lng(), false);        // false: non svuotare il campo lunghezza linea
    };

    this.getPathPolyline = function()       // restituisce la polyline del percorso selezionato
    {
        if(travelType === 'foot')
            return directionsDisplay.getDirections().routes[0].overview_polyline;
        else
            return google.maps.geometry.encoding.encodePath(polyPath.getPath());
    };

    this.updateMarker = function (lat, lng) {           // se le coordinate vengono inserite attraverso i campi di testo
        thisPoiMarker.setPosition(new google.maps.LatLng(lat, lng));
        drawPolyline();
    };
})

.service("drawMapLine", function ($rootScope) {
    var map;
    var directionsService = new google.maps.DirectionsService();
    var directionsDisplay;
    var stopsMarkers = [];
    var stopsArray = [];

    var serviceScope = this;        // serve per poter chiamare le funzioni dichiarate con "this.NOME" dentro altre funzioni

    this.createMap = function (idMap, stops)
    {
        // Inizializza l'oggetto mappa
        map = new google.maps.Map(document.getElementById(idMap), {
            center: {
                lat: 45.88326,      // posizione di default
                lng: 11.00145
            },
            zoom: 13
        });

        // Inizializza il servizio per il rendering del percorso a piedi
        directionsDisplay = new google.maps.DirectionsRenderer({markerOptions: {visible: false}});
        directionsDisplay.setMap(map);

        serviceScope.updateMarkers(stops);
    };

    this.updateMarkers = function(stops)
    {
        for(var i = 0; i < stopsMarkers.length; i++)
            stopsMarkers[i].setMap(null);

        stopsMarkers = [];
        stopsArray = stops;
        for (i = 0; i < stopsArray.length; i++)
        {
            stopsMarkers.push(new google.maps.Marker({
                position: {
                    lat: parseFloat(stopsArray[i].coordinates.lat),
                    lng: parseFloat(stopsArray[i].coordinates.lng)
                },
                map: map,
                draggable: true,
                label: (i+1).toString()
            }));

            (function(e) {          // qui sta spiegato il perché di sta roba contorta: https://stackoverflow.com/questions/16724620/mutable-variable-is-accessible-from-closure-how-can-i-fix-this (seconda risposta)
                stopsMarkers[e].addListener('mouseup', function() {
                    $rootScope.$broadcast('stopMarkerPosChanged', e, stopsMarkers[e].position.lat(), stopsMarkers[e].position.lng());       // avviso che la posizione del marker è cambiata
                })
            })(i);
        }

        if(stopsMarkers.length > 1)
            serviceScope.drawPath();
        else
            directionsDisplay.setMap(null);
    };

    function calculateWaypoints()
    {
        var waypoints = [];
        for (var i = 1; i < stopsMarkers.length-1; i++)
        {
            waypoints.push({
                location: stopsMarkers[i].getPosition(),
                stopover: false
            });
        }
        return waypoints;
    }

    this.drawPath = function()
    {
        if(directionsDisplay.map === null)
            directionsDisplay.setMap(map);

        var request = {
            origin: stopsMarkers[0].getPosition(),
            destination: stopsMarkers[stopsMarkers.length-1].getPosition(),
            waypoints: calculateWaypoints(),
            travelMode: 'WALKING'
        };
        directionsService.route(request, function(result, status) {
            if (status === 'OK')
                directionsDisplay.setDirections(result);
            else
                window.alert('Errore nella richiesta: ' + status);
        });
    }
});