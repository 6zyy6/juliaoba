package com.yupi.yupao.utils;

/**
 * 地理位置计算工具类
 */
public class GeoUtils {

    private static final double EARTH_RADIUS = 6371.0; // 地球半径，单位千米

    /**
     * 计算两点之间的距离（使用 Haversine 公式）
     *
     * @param lat1 第一个点的纬度
     * @param lon1 第一个点的经度
     * @param lat2 第二个点的纬度
     * @param lon2 第二个点的经度
     * @return 两点之间的距离，单位千米
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
                   
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS * c;
    }
    
    /**
     * 根据距离计算纬度范围
     *
     * @param lat 中心点纬度
     * @param distance 距离（千米）
     * @return 纬度范围 [最小纬度, 最大纬度]
     */
    public static double[] getLatitudeRange(double lat, double distance) {
        // 1度纬度约等于111千米
        double latDelta = distance / 111.0;
        return new double[] {lat - latDelta, lat + latDelta};
    }
    
    /**
     * 根据距离计算经度范围
     *
     * @param lat 中心点纬度
     * @param lon 中心点经度
     * @param distance 距离（千米）
     * @return 经度范围 [最小经度, 最大经度]
     */
    public static double[] getLongitudeRange(double lat, double lon, double distance) {
        // 计算当前纬度下的经度每度代表的距离
        double lonDistance = Math.cos(Math.toRadians(lat)) * 111.0;
        double lonDelta = distance / lonDistance;
        return new double[] {lon - lonDelta, lon + lonDelta};
    }
} 