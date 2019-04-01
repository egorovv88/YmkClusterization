package com.platfomni.ymkclusterization;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PointQuadTree<T extends PointQuadTree.Item> {
    private final Bounds mBounds;
    private final int mDepth;
    private static final int MAX_ELEMENTS = 50;
    private List<T> mItems;
    private static final int MAX_DEPTH = 40;
    private List<PointQuadTree<T>> mChildren;

    public PointQuadTree(double minX, double maxX, double minY, double maxY) {
        this(new Bounds(minX, maxX, minY, maxY));
    }

    public PointQuadTree(Bounds bounds) {
        this(bounds, 0);
    }

    private PointQuadTree(double minX, double maxX, double minY, double maxY, int depth) {
        this(new Bounds(minX, maxX, minY, maxY), depth);
    }

    private PointQuadTree(Bounds bounds, int depth) {
        this.mChildren = null;
        this.mBounds = bounds;
        this.mDepth = depth;
    }

    public void add(T item) {
        Point point = item.getPoint();
        if (this.mBounds.contains(point.x, point.y)) {
            this.insert(point.x, point.y, item);
        }

    }

    private void insert(double x, double y, T item) {
        if (this.mChildren != null) {
            if (y < this.mBounds.midY) {
                if (x < this.mBounds.midX) {
                    ((PointQuadTree)this.mChildren.get(0)).insert(x, y, item);
                } else {
                    ((PointQuadTree)this.mChildren.get(1)).insert(x, y, item);
                }
            } else if (x < this.mBounds.midX) {
                ((PointQuadTree)this.mChildren.get(2)).insert(x, y, item);
            } else {
                ((PointQuadTree)this.mChildren.get(3)).insert(x, y, item);
            }

        } else {
            if (this.mItems == null) {
                this.mItems = new ArrayList();
            }

            this.mItems.add(item);
            if (this.mItems.size() > 50 && this.mDepth < 40) {
                this.split();
            }

        }
    }

    private void split() {
        this.mChildren = new ArrayList(4);
        this.mChildren.add(new PointQuadTree(this.mBounds.minX, this.mBounds.midX, this.mBounds.minY, this.mBounds.midY, this.mDepth + 1));
        this.mChildren.add(new PointQuadTree(this.mBounds.midX, this.mBounds.maxX, this.mBounds.minY, this.mBounds.midY, this.mDepth + 1));
        this.mChildren.add(new PointQuadTree(this.mBounds.minX, this.mBounds.midX, this.mBounds.midY, this.mBounds.maxY, this.mDepth + 1));
        this.mChildren.add(new PointQuadTree(this.mBounds.midX, this.mBounds.maxX, this.mBounds.midY, this.mBounds.maxY, this.mDepth + 1));
        List<T> items = this.mItems;
        this.mItems = null;
        Iterator var2 = items.iterator();

        while(var2.hasNext()) {
            T item = (T)var2.next();
            this.insert(item.getPoint().x, item.getPoint().y, item);
        }

    }

    public boolean remove(T item) {
        Point point = item.getPoint();
        return this.mBounds.contains(point.x, point.y) ? this.remove(point.x, point.y, item) : false;
    }

    private boolean remove(double x, double y, T item) {
        if (this.mChildren != null) {
            if (y < this.mBounds.midY) {
                return x < this.mBounds.midX ? ((PointQuadTree)this.mChildren.get(0)).remove(x, y, item) : ((PointQuadTree)this.mChildren.get(1)).remove(x, y, item);
            } else {
                return x < this.mBounds.midX ? ((PointQuadTree)this.mChildren.get(2)).remove(x, y, item) : ((PointQuadTree)this.mChildren.get(3)).remove(x, y, item);
            }
        } else {
            return this.mItems.remove(item);
        }
    }

    public void clear() {
        this.mChildren = null;
        if (this.mItems != null) {
            this.mItems.clear();
        }

    }

    public Collection<T> search(Bounds searchBounds) {
        List<T> results = new ArrayList();
        this.search(searchBounds, results);
        return results;
    }

    private void search(Bounds searchBounds, Collection<T> results) {
        if (this.mBounds.intersects(searchBounds)) {
            Iterator var3;
            if (this.mChildren != null) {
                var3 = this.mChildren.iterator();

                while(var3.hasNext()) {
                    PointQuadTree<T> quad = (PointQuadTree)var3.next();
                    quad.search(searchBounds, results);
                }
            } else if (this.mItems != null) {
                if (searchBounds.contains(this.mBounds)) {
                    results.addAll(this.mItems);
                } else {
                    var3 = this.mItems.iterator();

                    while(var3.hasNext()) {
                        T item = (T)var3.next();
                        if (searchBounds.contains(item.getPoint())) {
                            results.add(item);
                        }
                    }
                }
            }

        }
    }

    public interface Item {
        Point getPoint();
    }
}