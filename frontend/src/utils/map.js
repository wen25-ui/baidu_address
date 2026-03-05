// This file contains utility functions for handling map-related operations, such as initializing the Baidu Map and managing routes.

export const initMap = (mapContainerId) => {
    const map = new BMap.Map(mapContainerId); // Create a new map instance
    const point = new BMap.Point(116.404, 39.915); // Set the center point of the map
    map.centerAndZoom(point, 15); // Initialize the map with the center point and zoom level
    map.enableScrollWheelZoom(true); // Enable scroll wheel zoom
    return map;
};

export const addMarker = (map, position, title) => {
    const marker = new BMap.Marker(position); // Create a new marker
    map.addOverlay(marker); // Add the marker to the map
    marker.setTitle(title); // Set the title for the marker
};

export const drawRoute = (map, startPoint, endPoint) => {
    const driving = new BMap.DrivingRoute(map, {
        renderOptions: { map: map, autoViewport: true }
    });
    driving.search(startPoint, endPoint); // Search for the route from start to end point
};