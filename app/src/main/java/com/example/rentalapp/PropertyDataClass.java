package com.example.rentalapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class PropertyDataClass implements Parcelable {

    private String documentId;
    private String apartmentName;
    private String bhkType;
    private String propertySize;
    private String propertyAge;
    private String floor;
    private String totalFloors;
    private String locality;
    private String expectedRent;
    private String expectedDeposit;
    private String furnishingType;
    private String photos;

    public PropertyDataClass() {
    }

    public PropertyDataClass(String documentId, String apartmentName, String bhkType, String propertySize, String propertyAge, String floor, String totalFloors, String locality, String expectedRent, String expectedDeposit, String furnishingType, String photos) {
        this.documentId = documentId;
        this.apartmentName = apartmentName;
        this.bhkType = bhkType;
        this.propertySize = propertySize;
        this.propertyAge = propertyAge;
        this.floor = floor;
        this.totalFloors = totalFloors;
        this.locality = locality;
        this.expectedRent = expectedRent;
        this.expectedDeposit = expectedDeposit;
        this.furnishingType = furnishingType;
        this.photos = photos;
    }

    protected PropertyDataClass(Parcel in) {
        documentId = in.readString();
        apartmentName = in.readString();
        bhkType = in.readString();
        propertySize = in.readString();
        propertyAge = in.readString();
        floor = in.readString();
        totalFloors = in.readString();
        locality = in.readString();
        expectedRent = in.readString();
        expectedDeposit = in.readString();
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

    //    Getters
    public String getApartmentName() {
        return apartmentName;
    }

    public String getBhkType() {
        return bhkType;
    }

    public String getPropertySize() {
        return propertySize;
    }

    public String getPropertyAge() {
        return propertyAge;
    }

    public String getFloor() {
        return floor;
    }

    public String getTotalFloors() {
        return totalFloors;
    }

    public String getLocality() {
        return locality;
    }

    public String getExpectedRent() {
        return expectedRent;
    }

    public String getExpectedDeposit() {
        return expectedDeposit;
    }

    public String getFurnishingType() {
        return furnishingType;
    }

    public String getPhotos() {
        return photos;
    }

    public String getDocumentId() {
        return documentId;
    }

    //    Setters
    public void setApartmentName(String apartmentName) {
        this.apartmentName = apartmentName;
    }

    public void setBhkType(String bhkType) {
        this.bhkType = bhkType;
    }

    public void setPropertySize(String propertySize) {
        this.propertySize = propertySize;
    }

    public void setPropertyAge(String propertyAge) {
        this.propertyAge = propertyAge;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setTotalFloors(String totalFloors) {
        this.totalFloors = totalFloors;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setExpectedRent(String expectedRent) {
        this.expectedRent = expectedRent;
    }

    public void setExpectedDeposit(String expectedDeposit) {
        this.expectedDeposit = expectedDeposit;
    }

    public void setFurnishingType(String furnishingType) {
        this.furnishingType = furnishingType;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(documentId);
        dest.writeString(apartmentName);
        dest.writeString(bhkType);
        dest.writeString(propertySize);
        dest.writeString(propertyAge);
        dest.writeString(floor);
        dest.writeString(totalFloors);
        dest.writeString(locality);
        dest.writeString(expectedRent);
        dest.writeString(expectedDeposit);
        dest.writeString(furnishingType);
        dest.writeString(photos);
    }
}
