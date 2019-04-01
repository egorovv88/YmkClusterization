package com.platfomni.ymkclusterization;

import android.support.v4.util.LruCache;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PreCachingAlgorithmDecorator<T extends ClusterItem> implements Algorithm<T> {

    private final Algorithm<T> mAlgorithm;
    private final LruCache<Integer, Set<? extends Cluster<T>>> mCache = new LruCache(5);
    private final ReadWriteLock mCacheLock = new ReentrantReadWriteLock();

    public PreCachingAlgorithmDecorator(Algorithm<T> algorithm) {
        this.mAlgorithm = algorithm;
    }

    public void addItem(T item) {
        this.mAlgorithm.addItem(item);
        this.clearCache();
    }

    public void addItems(Collection<T> items) {
        this.mAlgorithm.addItems(items);
        this.clearCache();
    }

    public void clearItems() {
        this.mAlgorithm.clearItems();
        this.clearCache();
    }

    public void removeItem(T item) {
        this.mAlgorithm.removeItem(item);
        this.clearCache();
    }

    private void clearCache() {
        this.mCache.evictAll();
    }

    public Set<? extends Cluster<T>> getClusters(double zoom) {
        int discreteZoom = (int)zoom;
        Set<? extends Cluster<T>> results = this.getClustersInternal(discreteZoom);
        if (this.mCache.get(discreteZoom + 1) == null) {
            (new Thread(new PreCachingAlgorithmDecorator.PrecacheRunnable(discreteZoom + 1))).start();
        }

        if (this.mCache.get(discreteZoom - 1) == null) {
            (new Thread(new PreCachingAlgorithmDecorator.PrecacheRunnable(discreteZoom - 1))).start();
        }

        return results;
    }

    public Collection<T> getItems() {
        return this.mAlgorithm.getItems();
    }

    private Set<? extends Cluster<T>> getClustersInternal(int discreteZoom) {
        this.mCacheLock.readLock().lock();
        Set<? extends Cluster<T>> results = (Set)this.mCache.get(discreteZoom);
        this.mCacheLock.readLock().unlock();
        if (results == null) {
            this.mCacheLock.writeLock().lock();
            results = (Set)this.mCache.get(discreteZoom);
            if (results == null) {
                results = this.mAlgorithm.getClusters((double)discreteZoom);
                this.mCache.put(discreteZoom, results);
            }

            this.mCacheLock.writeLock().unlock();
        }

        return results;
    }

    private class PrecacheRunnable implements Runnable {
        private final int mZoom;

        public PrecacheRunnable(int zoom) {
            this.mZoom = zoom;
        }

        public void run() {
            try {
                Thread.sleep((long)(Math.random() * 500.0D + 500.0D));
            } catch (InterruptedException var2) {
                ;
            }

            PreCachingAlgorithmDecorator.this.getClustersInternal(this.mZoom);
        }
    }

}
