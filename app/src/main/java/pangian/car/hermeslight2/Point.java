package pangian.car.hermeslight2;

public class Point {

    int id;

    public Point(int id, double lat, double lon, boolean isStation, String stationAddress) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.isStation = isStation;
        this.stationAddress = stationAddress;
    }

    double lat;
    double lon;
    boolean isStation;
    String stationAddress;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isStation() {
        return isStation;
    }

    public void setStation(boolean station) {
        isStation = station;
    }

    public String getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(String stationAddress) {
        this.stationAddress = stationAddress;
    }
}
