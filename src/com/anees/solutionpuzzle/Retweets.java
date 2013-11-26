package com.anees.solutionpuzzle;

import android.os.Parcel;
import android.os.Parcelable;

public class Retweets implements Parcelable {
    private String name = "";
    private String url = "";
    private int followers;

    public Retweets(String name, String url, int followers) {
	this.name = name;
	this.url = url;
	this.followers = followers;
    }

    private Retweets(Parcel in) {
	name = in.readString();
	url = in.readString();
	followers = in.readInt();
    }

    public int getFollowers() {
	return followers;
    }

    public String getImageURL() {
	return url;
    }

    public String toString() {
	return "Name: " + this.name + "-- Followers: " + this.followers;
    }

    @Override
    public int describeContents() {
	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
	dest.writeString(name);
	dest.writeString(url);
	dest.writeInt(followers);
    }

    public static final Parcelable.Creator<Retweets> CREATOR = new Parcelable.Creator<Retweets>() {

	@Override
	public Retweets createFromParcel(Parcel source) {
	    return new Retweets(source);
	}

	@Override
	public Retweets[] newArray(int size) {
	    return new Retweets[size];
	}

    };
}
