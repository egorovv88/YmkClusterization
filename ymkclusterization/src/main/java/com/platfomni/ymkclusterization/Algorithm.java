package com.platfomni.ymkclusterization;

import java.util.Collection;
import java.util.Set;

public interface Algorithm<T extends ClusterItem> {

    void addItem(T item);

    void addItems(Collection<T> items);

    void clearItems();

    void removeItem(T item);

    Set<? extends Cluster<T>> getClusters(double zoom);

    Collection<T> getItems();
}