package com.platfomni.ymkclusterization;

import com.yandex.mapkit.geometry.Point;

import java.util.Collection;

public interface Cluster<T extends ClusterItem> {

    Point getPosition();

    Collection<T> getItems();

    int getSize();

}
