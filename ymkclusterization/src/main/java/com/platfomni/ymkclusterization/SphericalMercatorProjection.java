package com.platfomni.ymkclusterization;

public class SphericalMercatorProjection {
    final double mWorldWidth;

    public SphericalMercatorProjection(double worldWidth) {
        this.mWorldWidth = worldWidth;
    }

    public Point toPoint(com.yandex.mapkit.geometry.Point latLng) {
        double x = latLng.getLongitude() / 360.0D + 0.5D;
        double siny = Math.sin(Math.toRadians(latLng.getLatitude()));
        double y = 0.5D * Math.log((1.0D + siny) / (1.0D - siny)) / -6.283185307179586D + 0.5D;
        return new Point(x * this.mWorldWidth, y * this.mWorldWidth);
    }

    public Point toLatLng(Point point) {
        double x = point.x / this.mWorldWidth - 0.5D;
        double lng = x * 360.0D;
        double y = 0.5D - point.y / this.mWorldWidth;
        double lat = 90.0D - Math.toDegrees(Math.atan(Math.exp(-y * 2.0D * 3.141592653589793D)) * 2.0D);
        return new Point(lat, lng);
    }
}
