package pangian.car.hermeslight2;

public class Bus {
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

    double lat;
    double lon;

    public Bus(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
