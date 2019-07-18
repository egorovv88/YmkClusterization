package com.platfomni.ymkclusterization;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.VisibleRegion;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DefaultClusterRenderer<T extends ClusterItem> implements ClusterRenderer<T> {

    private final MapView mMap;
    private final ClusterManager<T> mClusterManager;
    private Context context;

    public DefaultClusterRenderer(Context context, MapView map, ClusterManager<T> clusterManager) {
        this.mMap = map;
        this.context = context;
        this.mClusterManager = clusterManager;
    }

    @Override
    public void onClustersChanged(Set<? extends Cluster<T>> clusters) {
        mMap.getMap().getMapObjects().clear();
        mClusterManager.getClusterMarkerCollection().clear();

        for (Cluster<T> t : clusters) {
            if (itemInRegion(t)) {
                if (t.getSize() <= 1) {
                    drawMarker(t.getItems().iterator().next());
                } else {
                    List<T> itemsOutOfCluster = new ArrayList<>();
                    for (T item : t.getItems()) {
                        if (item.isOutOfCluster()) {
                            itemsOutOfCluster.add(item);
                        }
                    }

                    if (t.getSize() - itemsOutOfCluster.size() <= 1) {
                        drawMarker(t.getItems().iterator().next());
                    } else {
                        drawCluster(t, itemsOutOfCluster.size());
                    }

                    if (itemsOutOfCluster.size() > 0) {
                        for (T item : itemsOutOfCluster) {
                            drawMarker(item);
                        }
                    }
                }
            }
        }
    }

    private boolean itemInRegion(Cluster<T> cluster) {
        VisibleRegion visibleRegion = mMap.getMap().getVisibleRegion();
        return (cluster.getPosition().getLatitude() > visibleRegion.getBottomLeft().getLatitude() &&
                cluster.getPosition().getLatitude() < visibleRegion.getTopRight().getLatitude() &&
                cluster.getPosition().getLongitude() < visibleRegion.getTopRight().getLongitude() &&
                cluster.getPosition().getLongitude() > visibleRegion.getBottomLeft().getLongitude());
    }

    public void drawMarker(T markerItem) {
    }

    /**
     * use getItemClusterResource() for overrive layout res cluster
     * */
    @Deprecated
    public void drawCluster(Cluster<T> t) {
        PlacemarkMapObject clusterObject = mClusterManager.getClusterMarkerCollection().addMarker(calculateCenterCluster(t));
        clusterObject.setIcon(ImageProvider.fromBitmap(clusterMarker(context, t.getSize())));
    }

    private void drawCluster(Cluster<T> t, int outOfClusterItems) {
        PlacemarkMapObject clusterObject = mClusterManager.getClusterMarkerCollection().addMarker(calculateCenterCluster(t));
        clusterObject.setIcon(ImageProvider.fromBitmap(clusterMarker(context, t.getSize() - outOfClusterItems)));
    }

    private Point calculateCenterCluster(Cluster<T> t) {
        double lat = 0;
        double lon = 0;
        for (T item : t.getItems()) {
            lat += item.getPosition().getLatitude();
            lon += item.getPosition().getLongitude();
        }
        return new Point(lat / t.getItems().size(), lon / t.getItems().size());
    }

    public ClusterManager<T> getClusterManager() {
        return mClusterManager;
    }

    /**
     * return layout file
     * <p>
     * file must contains TextView with id = "@+id/count"
     */
    public Integer getItemClusterResource() {
        return R.layout.item_cluster;
    }

    private Bitmap clusterMarker(Context context, int count) {
        @SuppressLint("InflateParams") View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getItemClusterResource(), null);

        TextView txt = marker.findViewById(R.id.count);
        txt.setText(String.valueOf(count));

        marker.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(marker.getMeasuredWidth(), marker.getMeasuredHeight());
        marker.layout(0, 0, marker.getMeasuredWidth(), marker.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

}
