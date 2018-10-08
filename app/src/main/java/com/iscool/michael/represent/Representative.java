package com.iscool.michael.represent;

import android.media.Image;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

public class Representative implements Parcelable {

    private String name;
    private String party;
    private String title;
    private String email;
    private String website;
    private String endDate;
    private String member_id;
    private String bioguide_id;
    private String district;
    private String state;
    private ArrayList<String> committees;
    private HashMap<String, String> bills;

    public Representative(){
        name = null;
        party = null;
        title = null;
        email = null;
        website = null;
        endDate = null;
        member_id = null;
        bioguide_id = null;
        district = null;
        state = null;
        committees = new ArrayList<>();
        bills = new HashMap<>();
    }

    public Representative(Parcel in){
        name = in.readString();
        party = in.readString();
        title = in.readString();
        email = in.readString();
        website = in.readString();
        endDate = in.readString();
        member_id = in.readString();
        bioguide_id = in.readString();
        district = in.readString();
        state = in.readString();
        committees = in.readArrayList(String.class.getClassLoader());
        bills = in.readHashMap(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(name);
        dest.writeString(party);
        dest.writeString(title);
        dest.writeString(email);
        dest.writeString(website);
        dest.writeString(endDate);
        dest.writeString(member_id);
        dest.writeString(bioguide_id);
        dest.writeString(district);
        dest.writeString(state);
        dest.writeList(committees);
        dest.writeMap(bills);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Representative createFromParcel(Parcel in) {
            return new Representative(in);
        }

        public Representative[] newArray(int size) {
            return new Representative[size];
        }
    };

    public void setName(String new_name) {
        name = new_name;
    }

    public void setParty(String new_party) {
        party = new_party;
    }

    public void setTitle(String new_title) {
        title = new_title;
    }

    public void setEmail(String new_email) {
        email = new_email;
    }

    public void setWebsite(String new_website) {
        website = new_website;
    }

    public void setEndDate(String new_endDate) {
        endDate = new_endDate;
    }

    public void setMember_id(String new_member_id) {
        member_id = new_member_id;
    }

    public void setBioguide_id(String new_bioguide_id) {
        bioguide_id = new_bioguide_id;
    }

    public void setDistrict(String new_district) {
        district = new_district;
    }

    public void setState(String new_state) {
        state = new_state;
    }

    public void addCommittee(String new_committee) {
        committees.add(new_committee);
    }

    public void addBill(String new_bill, String date){
        bills.put(new_bill, date);
    }

    public String getName(){
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getMember_id() {
        return member_id;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }

    public ArrayList<String> getCommittees() {
        return committees;
    }

    public HashMap<String, String> getBills() {
        return bills;
    }
}
