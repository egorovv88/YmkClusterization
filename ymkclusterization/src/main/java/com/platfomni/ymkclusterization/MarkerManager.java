package com.platfomni.ymkclusterization;

import android.support.annotation.NonNull;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MarkerManager {

    private final MapView mapView;
    private final Map<String, MarkerManager.Collection> mNamedCollections = new HashMap<>();
    private final Map<PlacemarkMapObject, MarkerManager.Collection> mAllMarkers = new HashMap<>();

    private ClusterManager.OnMarkerClickListener markerClickListener;
    private ClusterManager.OnClusterClickListener clusterClickListener;

    MarkerManager(MapView mapView) {
        this.mapView = mapView;
    }

    MarkerManager.Collection newCollection() {
        return new MarkerManager.Collection();
    }

    public MarkerManager.Collection newCollection(String id) {
        if (this.mNamedCollections.get(id) != null) {
            throw new IllegalArgumentException("collection id is not unique: " + id);
        } else {
            MarkerManager.Collection collection = new MarkerManager.Collection();
            this.mNamedCollections.put(id, collection);
            return collection;
        }
    }

    public MarkerManager.Collection getCollection(String id) {
        return this.mNamedCollections.get(id);
    }

    public boolean remove(PlacemarkMapObject marker) {
        MarkerManager.Collection collection = this.mAllMarkers.get(marker);
        return collection != null && collection.remove(marker);
    }

    public void setMarkerListener(ClusterManager.OnMarkerClickListener listener) {
        this.markerClickListener = listener;
    }

    public void setClusterListener(ClusterManager.OnClusterClickListener listener) {
        this.clusterClickListener = listener;
    }

    public class Collection {
        private final Set<PlacemarkMapObject> mMarkers = new HashSet<>();

        Collection() {
        }

        MapObjectTapListener listener = new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                if (mapObject.getUserData() == null) {
                    clusterClickListener.onClusterClick(point);
                } else {
                    markerClickListener.onMarkerClick(mapObject.getUserData());
                }
                return true;
            }
        };

        public PlacemarkMapObject addMarker(Point opts) {
            PlacemarkMapObject marker = MarkerManager.this.mapView.getMap().getMapObjects().addPlacemark(opts);
            marker.addTapListener(listener);
            this.mMarkers.add(marker);
            MarkerManager.this.mAllMarkers.put(marker, this);
            return marker;
        }

        boolean remove(PlacemarkMapObject marker) {
            if (this.mMarkers.remove(marker)) {
                MarkerManager.this.mAllMarkers.remove(marker);
                return true;
            } else {
                return false;
            }
        }

        void clear() {
            this.mMarkers.clear();
        }

        public java.util.Collection<PlacemarkMapObject> getMarkers() {
            return Collections.unmodifiableCollection(this.mMarkers);
        }

    }
}
