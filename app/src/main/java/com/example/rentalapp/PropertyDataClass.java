package com.example.rentalapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class PropertyDataClass implements Parcelable {

    private String documentId;
    private String possession;
    private String postedBy;
    private String apartmentName;
    private String bhkType;
    private double propertySize;
    private int propertyAge;
    private int floor;
    private int totalFloors;
    private int numberOfBathrooms;
    private String waterSupplier;
    private String parking;
    private String security;
    private String tenantPreference;
    private String locality;
    private double latitude;
    private double longitude;
    private double expectedRent;
    private double expectedDeposit;
    private String furnishingType;
    private String photos;

    public PropertyDataClass(String documentId, String possession, String postedBy, String apartmentName, String bhkType, double propertySize, int propertyAge, int floor, int totalFloors, int numberOfBathrooms, String waterSupplier, String parking, String security, String tenantPreference, String locality, double latitude, double longitude, double expectedRent, double expectedDeposit, String furnishingType, String photos) {
        this.documentId = documentId;
        this.possession = possession;
        this.postedBy = postedBy;
        this.apartmentName = apartmentName;
        this.bhkType = bhkType;
        this.propertySize = propertySize;
        this.propertyAge = propertyAge;
        this.floor = floor;
        this.totalFloors = totalFloors;
        this.numberOfBathrooms = numberOfBathrooms;
        this.waterSupplier = waterSupplier;
        this.parking = parking;
        this.security = security;
        this.tenantPreference = tenantPreference;
        this.locality = locality;
        this.latitude = latitude;
        this.longitude = longitude;
        this.expectedRent = expectedRent;
        this.expectedDeposit = expectedDeposit;
        this.furnishingType = furnishingType;
        this.photos = photos;
    }

    public PropertyDataClass() {
    }

    protected PropertyDataClass(Parcel in) {
        documentId = in.readString();
        possession = in.readString();
        postedBy = in.readString();
        apartmentName = in.readString();
        bhkType = in.readString();
        propertySize = in.readDouble();
        propertyAge = in.readInt();
        floor = in.readInt();
        totalFloors = in.readInt();
        numberOfBathrooms = in.readInt();
        waterSupplier = in.readString();
        parking = in.readString();
        security = in.readString();
        tenantPreference = in.readString();
        locality = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        expectedRent = in.readDouble();
        expectedDeposit = in.readDouble();
        furnishingType = in.readString();
        photos = in.readString();
    }

    public static final Creator<PropertyDataClass> CREATOR = new Creator<PropertyDataClass>() {
        @Override
        public PropertyDataClass createFromParcel(Parcel in) {
            return new PropertyDataClass(in);
        }

        @Override
        public PropertyDataClass[] newArray(int size) {
            return new PropertyDataClass[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(possession);
        dest.writeString(postedBy);
        dest.writeString(apartmentName);
        dest.writeString(bhkType);
        dest.writeDouble(propertySize);
        dest.writeInt(propertyAge);
        dest.writeInt(floor);
        dest.writeInt(totalFloors);
        dest.writeInt(numberOfBathrooms);
        dest.writeString(waterSupplier);
        dest.writeString(parking);
        dest.writeString(security);
        dest.writeString(tenantPreference);
        dest.writeString(locality);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(expectedRent);
        dest.writeDouble(expectedDeposit);
        dest.writeString(furnishingType);
        dest.writeString(photos);
    }

    //    Getters

    public String getDocumentId() {
        return documentId;
    }

    public String getPossession() {
        return possession;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public String getApartmentName() {
        return apartmentName;
    }

    public String getBhkType() {
        return bhkType;
    }

    public double getPropertySize() {
        return propertySize;
    }

    public int getPropertyAge() {
        return propertyAge;
    }

    public int getFloor() {
        return floor;
    }

    public int getTotalFloors() {
        return totalFloors;
    }

    public int getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public String getWaterSupplier() {
        return waterSupplier;
    }

    public String getParking() {
        return parking;
    }

    public String getSecurity() {
        return security;
    }

    public String getTenantPreference() {
        return tenantPreference;
    }

    public String getLocality() {
        return locality;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getExpectedRent() {
        return expectedRent;
    }

    public double getExpectedDeposit() {
        return expectedDeposit;
    }

    public String getFurnishingType() {
        return furnishingType;
    }

    public String getPhotos() {
        return photos;
    }

    //    Setters

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setPossession(String possession) {
        this.possession = possession;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public void setBhkType(String bhkType) {
        this.bhkType = bhkType;
    }

    public void setPropertySize(double propertySize) {
        this.propertySize = propertySize;
    }

    public void setPropertyAge(int propertyAge) {
        this.propertyAge = propertyAge;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setTotalFloors(int totalFloors) {
        this.totalFloors = totalFloors;
    }

    public void setNumberOfBathrooms(int numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public void setWaterSupplier(String waterSupplier) {
        this.waterSupplier = waterSupplier;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public void setTenantPreference(String tenantPreference) {
        this.tenantPreference = tenantPreference;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setExpectedRent(double expectedRent) {
        this.expectedRent = expectedRent;
    }

    public void setExpectedDeposit(double expectedDeposit) {
        this.expectedDeposit = expectedDeposit;
    }

    public void setFurnishingType(String furnishingType) {
        this.furnishingType = furnishingType;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }
}
