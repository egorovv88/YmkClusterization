package com.platfomni.ymkclusterization;

import com.yandex.mapkit.geometry.Point;

public interface ClusterItem {

    Point getPosition();

    /**
     * false - marker will be never not placed in any cluster
     * true - marker will be placed in any cluster if satisfies the conditions
     */
    boolean isOutOfCluster();

}
