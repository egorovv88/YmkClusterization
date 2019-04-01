package com.platfomni.ymkclusterization;

import com.yandex.mapkit.geometry.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StaticCluster<T extends ClusterItem> implements Cluster<T> {

    private Point mCenter;
    private final List<T> mItems = new ArrayList();

    public StaticCluster(Point center) {
        this.mCenter = center;
    }

    public boolean add(T t) {
        return this.mItems.add(t);
    }

    public Point getPosition() {
        return this.mCenter;
    }

    public boolean remove(T t) {
        return this.mItems.remove(t);
    }

    public Collection<T> getItems() {
        return this.mItems;
    }

    public int getSize() {
        return this.mItems.size();
    }

    public String toString() {
        return "StaticCluster{mCenter=" + this.mCenter + ", mItems.size=" + this.mItems.size() + '}';
    }

    public int hashCode() {
        return this.mCenter.hashCode() + this.mItems.hashCode();
    }

    public boolean equals(Object other) {
        if (!(other instanceof StaticCluster)) {
            return false;
        } else {
            return ((StaticCluster) other).mCenter.equals(this.mCenter) && ((StaticCluster) other).mItems.equals(this.mItems);
        }
    }
}