package com.example.yandexclussterization;

import android.support.v4.util.ObjectsCompat;

import com.platfomni.ymkclusterization.ClusterItem;
import com.yandex.mapkit.geometry.Point;

public class MarkerItem implements ClusterItem {

    private Store store;
    private boolean selected = false;

    public MarkerItem(Store store) {
        this.store = store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public Point getPosition() {
        return new Point(store.getLat(), store.getLng());
    }

    @Override
    public boolean isOutOfCluster() {
        return store.getParam() == -1;
    }

    public Store getStore() {
        return store;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkerItem that = (MarkerItem) o;
        if (store == null || that.store == null) return false;

        return ObjectsCompat.equals(store, that.store);
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(store);
    }

}
