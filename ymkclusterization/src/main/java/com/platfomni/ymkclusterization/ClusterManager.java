package com.platfomni.ymkclusterization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateSource;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.mapview.MapView;

import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ClusterManager<T extends ClusterItem> implements CameraListener {

    private static final float ZOOM_DURATION = 0.3f;

    private final MarkerManager mMarkerManager;
    private final MarkerManager.Collection mMarkers;
    private final MarkerManager.Collection mClusterMarkers;
    private final ReadWriteLock mAlgorithmLock;
    private final ReadWriteLock mClusterTaskLock;
    private ClusterManager<T>.ClusterTask mClusterTask;
    private ClusterRenderer<T> mRenderer;

    private MapView mapView;
    private Algorithm<T> mAlgorithm;

    public ClusterManager(Context context, MapView mapView) {
        this.mAlgorithmLock = new ReentrantReadWriteLock();
        this.mClusterTaskLock = new ReentrantReadWriteLock();
        this.mapView = mapView;
        this.mAlgorithm = new PreCachingAlgorithmDecorator(new NonHierarchicalDistanceBasedAlgorithm());
        this.mMarkerManager = new MarkerManager(mapView);
        this.mRenderer = new DefaultClusterRenderer(context, mapView, this);
        this.mMarkers = mMarkerManager.newCollection();
        this.mClusterMarkers = mMarkerManager.newCollection();
        this.mClusterTask = new ClusterManager.ClusterTask();

        mMarkerManager.setClusterListener(clusterClickListener);

        mapView.getMap().addCameraListener(this);
    }

    public void setRenderer(ClusterRenderer<T> renderer) {
        this.mClusterMarkers.clear();
        this.mMarkers.clear();
        this.mRenderer = renderer;
        this.cluster();
    }

    public MarkerManager getMarkerManager() {
        return mMarkerManager;
    }

    public void setOnMarkerClickListener(ClusterManager.OnMarkerClickListener listener) {
        mMarkerManager.setMarkerListener(listener);
    }

    public void setOnClusterClickListener(ClusterManager.OnClusterClickListener listener) {
        mMarkerManager.setClusterListener(listener);
    }

    public MarkerManager.Collection getMarkerCollection() {
        return this.mMarkers;
    }


    public MarkerManager.Collection getClusterMarkerCollection() {
        return this.mClusterMarkers;
    }

    private void onCameraIdle() {
        this.cluster();
    }

    public void clearItems() {
        this.mAlgorithmLock.writeLock().lock();

        try {
            this.mAlgorithm.clearItems();
        } finally {
            this.mAlgorithmLock.writeLock().unlock();
        }

    }

    public void addItems(java.util.Collection<T> items) {
        this.mAlgorithmLock.writeLock().lock();

        try {
            this.mAlgorithm.addItems(items);
        } finally {
            this.mAlgorithmLock.writeLock().unlock();
        }

    }

    public void addItem(T myItem) {
        this.mAlgorithmLock.writeLock().lock();

        try {
            this.mAlgorithm.addItem(myItem);
        } finally {
            this.mAlgorithmLock.writeLock().unlock();
        }

    }

    public void removeItem(T item) {
        this.mAlgorithmLock.writeLock().lock();

        try {
            this.mAlgorithm.removeItem(item);
        } finally {
            this.mAlgorithmLock.writeLock().unlock();
        }

    }

    public void cluster() {
        this.mClusterTaskLock.writeLock().lock();

        try {
            this.mClusterTask.cancel(true);
            this.mClusterTask = new ClusterManager.ClusterTask();
            this.mClusterTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this.mapView.getMap().getCameraPosition().getZoom());
        } finally {
            this.mClusterTaskLock.writeLock().unlock();
        }

    }

    @Override
    public void onCameraPositionChanged(@NonNull Map map, @NonNull CameraPosition cameraPosition, @NonNull CameraUpdateSource cameraUpdateSource, boolean b) {
        if (b) {
            onCameraIdle();
        }
    }

    public interface OnMarkerClickListener<T> {
        void onMarkerClick(T markerItem);
    }

    public interface OnClusterClickListener {
        void onClusterClick(Point point);
    }

    @SuppressLint("StaticFieldLeak")
    private class ClusterTask extends AsyncTask<Float, Void, Set<? extends Cluster<T>>> {
        private ClusterTask() {
        }

        protected Set<? extends Cluster<T>> doInBackground(Float... zoom) {
            ClusterManager.this.mAlgorithmLock.readLock().lock();

            Set<? extends Cluster<T>> var2;
            try {
                var2 = ClusterManager.this.mAlgorithm.getClusters((double) zoom[0]);
            } finally {
                ClusterManager.this.mAlgorithmLock.readLock().unlock();
            }

            return var2;
        }

        protected void onPostExecute(Set<? extends Cluster<T>> clusters) {
            ClusterManager.this.mRenderer.onClustersChanged(clusters);
        }
    }

    private ClusterManager.OnClusterClickListener clusterClickListener = new ClusterManager.OnClusterClickListener() { //тыкаем по кластеру
        @Override
        public void onClusterClick(Point point) {
            mapView.getMap().move(
                    new CameraPosition(point, mapView.getMap().getCameraPosition().getZoom() + 1f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, ZOOM_DURATION),
                    null);
        }
    };

}

