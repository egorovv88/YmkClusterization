package com.example.yandexclussterization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.yandexclussterization.glide.GlideApp;
import com.example.yandexclussterization.glide.GlideRequests;
import com.platfomni.ymkclusterization.Cluster;
import com.platfomni.ymkclusterization.ClusterManager;
import com.platfomni.ymkclusterization.DefaultClusterRenderer;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;

public class ClusterIconRenderer extends DefaultClusterRenderer<MarkerItem> {

    private final GlideRequests glideRequests;
    private int pinSize;

    ClusterIconRenderer(Context context, MapView map, ClusterManager<MarkerItem> clusterManager) {
        super(context, map, clusterManager);
        glideRequests = GlideApp.with(context);
        pinSize = context.getResources().getDimensionPixelSize(R.dimen.pin_size);
    }

    private int getIconForMarker(MarkerItem markerItem) {
        if (markerItem.getStore().getParam() == -1) {
            return markerItem.isSelected() ? R.drawable.ic_marker_selected : R.drawable.ic_non_cluster_marker;
        }

        switch (markerItem.getStore().getParam() % 3) {
            case 0:
                return markerItem.isSelected() ? R.drawable.ic_marker_selected : R.drawable.ic_marker;
            case 1:
                return markerItem.isSelected() ? R.drawable.ic_marker_selected : R.drawable.ic_marker_one;
            case 2:
                return markerItem.isSelected() ? R.drawable.ic_marker_selected : R.drawable.ic_marker_two;
            default:
                return markerItem.isSelected() ? R.drawable.ic_marker_selected : R.drawable.ic_marker;
        }
    }

    @Override
    public void drawMarker(final MarkerItem markerItem) {

        Store store = markerItem.getStore();

        if (store != null) {
            glideRequests
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .placeholder(R.drawable.ic_marker)
                    .load(getIconForMarker(markerItem))
                    .into(new SimpleTarget<Bitmap>(pinSize, pinSize) {
                        @Override
                        public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        }

                        @Override
                        public void onResourceReady(@NotNull final Bitmap bitmap, final Transition<? super Bitmap> transition) {
                            try {
                                PlacemarkMapObject clusterObject = getClusterManager().getClusterMarkerCollection().addMarker(markerItem.getPosition());
                                clusterObject.setIcon(ImageProvider.fromBitmap(bitmap));
                                clusterObject.setUserData(markerItem);
                            } catch (IllegalArgumentException e) {
                                //marker is not available anymore, just skip
                                Log.d("DEBUG_TAG", "skip");
                            }
                        }
                    });
        }
    }

    @Override
    public Integer getItemClusterResource() {
        return R.layout.item_cluster;
    }

    private PlacemarkMapObject getMarkerItem(MarkerItem markerItem) {
        for (PlacemarkMapObject placemarkMapObject : getClusterManager().getClusterMarkerCollection().getMarkers()) {

            if (markerItem.equals(placemarkMapObject.getUserData())) {
                return placemarkMapObject;
            }

        }
        return null;
    }

    void updateClusterItem(final MarkerItem markerItem) {
        final PlacemarkMapObject marker = getMarkerItem(markerItem);

        if (marker != null) {
            glideRequests
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .placeholder(R.drawable.ic_marker)
                    .load(getIconForMarker(markerItem))
                    .into(new SimpleTarget<Bitmap>(pinSize, pinSize) {
                        @Override
                        public void onLoadFailed(@Nullable final Drawable errorDrawable) {
                        }

                        @Override
                        public void onResourceReady(@NotNull final Bitmap bitmap, final Transition<? super Bitmap> transition) {
                            try {
                                marker.setIcon(ImageProvider.fromBitmap(bitmap));
                            } catch (IllegalArgumentException e) {
                                //marker is not available anymore, just skip
                                Log.d("DEBUG_TAG", "skip");
                            }
                        }
                    });
        }
    }

}
