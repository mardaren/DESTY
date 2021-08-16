package com.example.desty.ui.main;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Connection;

public class ob implements Parcelable {
    private Connection conn;
    public ob(Connection con){
        conn = con;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
