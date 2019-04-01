package com.platfomni.ymkclusterization;

import android.annotation.SuppressLint;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.yandex.mapkit.geometry.Point;


public final class PointBounds {
    
    public final Point southwest;

    public final Point northeast;

    @SuppressLint("RestrictedApi")
    public PointBounds(Point southwest, Point northeast) {
        this.southwest = southwest;
        this.northeast = northeast;
    }
    

    public static PointBounds.Builder builder() {
        return new PointBounds.Builder();
    }

    public final boolean contains(Point var1) {
        double var4 = var1.getLatitude();
        return this.southwest.getLatitude() <= var4 && var4 <= this.northeast.getLatitude() && this.zza(var1.getLongitude());
    }

    public final PointBounds including(Point var1) {
        double var3 = Math.min(this.southwest.getLatitude(), var1.getLatitude());
        double var5 = Math.max(this.northeast.getLatitude(), var1.getLatitude());
        double var7 = this.northeast.getLongitude();
        double var9 = this.southwest.getLongitude();
        double var11 = var1.getLongitude();
        if (!this.zza(var11)) {
            if (zza(var9, var11) < zzb(var7, var11)) {
                var9 = var11;
            } else {
                var7 = var11;
            }
        }

        return new PointBounds(new Point(var3, var9), new Point(var5, var7));
    }

    public final Point getCenter() {
        double var1 = (this.southwest.getLatitude() + this.northeast.getLatitude()) / 2.0D;
        double var3 = this.northeast.getLongitude();
        double var5 = this.southwest.getLongitude();
        double var7;
        if (this.southwest.getLongitude() <= var3) {
            var7 = (var3 + var5) / 2.0D;
        } else {
            var7 = (var3 + 360.0D + var5) / 2.0D;
        }

        return new Point(var1, var7);
    }

    private static double zza(double var0, double var2) {
        return (var0 - var2 + 360.0D) % 360.0D;
    }

    private static double zzb(double var0, double var2) {
        return (var2 - var0 + 360.0D) % 360.0D;
    }

    private final boolean zza(double var1) {
        if (this.southwest.getLongitude() <= this.northeast.getLongitude()) {
            return this.southwest.getLongitude() <= var1 && var1 <= this.northeast.getLongitude();
        } else {
            return this.southwest.getLongitude() <= var1 || var1 <= this.northeast.getLongitude();
        }
    }

    public final boolean equals(Object var1) {
        if (this == var1) {
            return true;
        } else if (!(var1 instanceof PointBounds)) {
            return false;
        } else {
            PointBounds var2 = (PointBounds)var1;
            return this.southwest.equals(var2.southwest) && this.northeast.equals(var2.northeast);
        }
    }

    public static final class Builder {
        private double zzdh = 1.0D / 0.0;
        private double zzdi = -1.0D / 0.0;
        private double zzdj = 0.0D / 0.0;
        private double zzdk = 0.0D / 0.0;

        public Builder() {
        }

        public final PointBounds.Builder include(Point var1) {
            this.zzdh = Math.min(this.zzdh, var1.getLatitude());
            this.zzdi = Math.max(this.zzdi, var1.getLatitude());
            double var2 = var1.getLongitude();
            if (Double.isNaN(this.zzdj)) {
                this.zzdj = var2;
            } else {
                if (this.zzdj <= this.zzdk ? this.zzdj <= var2 && var2 <= this.zzdk : this.zzdj <= var2 || var2 <= this.zzdk) {
                    return this;
                }

                if (PointBounds.zza(this.zzdj, var2) < PointBounds.zzb(this.zzdk, var2)) {
                    this.zzdj = var2;
                    return this;
                }
            }

            this.zzdk = var2;
            return this;
        }

        public final PointBounds build() {
            return new PointBounds(new Point(this.zzdh, this.zzdj), new Point(this.zzdi, this.zzdk));
        }
    }
}