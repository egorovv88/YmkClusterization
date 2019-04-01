package com.platfomni.ymkclusterization;

import com.yandex.mapkit.map.PlacemarkMapObject;

import java.util.Set;

public interface ClusterRenderer<T extends ClusterItem> {

    void onClustersChanged(Set<? extends Cluster<T>> var1);

}
