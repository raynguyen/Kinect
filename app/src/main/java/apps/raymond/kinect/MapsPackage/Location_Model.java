package apps.raymond.kinect.MapsPackage;

import android.location.Address;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class Location_Model implements Parcelable {
    private String lookup;
    private String address;
    private double lat;
    private double lng;

    public static final Parcelable.Creator<Location_Model> CREATOR
            = new Parcelable.Creator<Location_Model>(){
        @Override
        public Location_Model createFromParcel(Parcel source) {
            return new Location_Model(source);
        }

        @Override
        public Location_Model[] newArray(int size) {
            return new Location_Model[size];
        }
    };

    public Location_Model(){}

    public Location_Model(@Nullable String lookup, Address address){
        this.lookup = lookup;
        this.address = address.getAddressLine(0);
        this.lat = address.getLatitude();
        this.lng = address.getLongitude();
    }

    public Location_Model(Parcel in){
        lookup = in.readString();
        address = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lookup);
        dest.writeString(address);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    public String getLookup() {
        return lookup;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    //This also appears to create a Map field in Firestore with the keys being latitude and longitude.
    public LatLng getLatLng(){
        return new LatLng(this.lat,this.lng);
    }
}
