package com.example.outofthecave.geburtstagskalender.model;

import android.arch.persistence.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity(primaryKeys = {"name", "day", "month"})
public final class Birthday implements Parcelable {
    @NonNull
    public String name;
    public int day;
    public int month;
    @Nullable
    public Integer year;

    public static final Parcelable.Creator<Birthday> CREATOR = new Parcelable.Creator<Birthday>() {
        @Override
        public Birthday createFromParcel(Parcel in) {
            Birthday birthday = new Birthday();
            birthday.name = in.readString();
            birthday.day = in.readInt();
            birthday.month = in.readInt();
            boolean hasYear = in.readByte() != 0;
            int year = in.readInt();
            if (hasYear) {
                birthday.year = year;
            }
            return birthday;
        }

        @Override
        public Birthday[] newArray(int size) {
            return new Birthday[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(day);
        out.writeInt(month);
        boolean hasYear = year != null;
        // Write a boolean (as a byte) to indicate whether the year is present.
        out.writeByte((byte) (hasYear ? 1 : 0));
        out.writeInt(hasYear ? year : 0);
    }
}