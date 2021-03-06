package com.outofthecave.geburtstagskalender.model;

import androidx.room.Entity;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

@Entity(primaryKeys = {"name", "day", "month"})
public final class Birthday implements Parcelable {
    @NonNull
    public String name = "";
    public int day = 0;
    public int month = 0;
    @Nullable
    public Integer year;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        Birthday birthday = (Birthday) that;
        return Objects.equals(name, birthday.name)
                && day == birthday.day
                && month == birthday.month
                && Objects.equals(year, birthday.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, day, month, year);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Birthday{");
        sb.append("name=\"");
        sb.append(name);
        sb.append("\",");
        sb.append("day=");
        sb.append(day);
        sb.append(",");
        sb.append("month=");
        sb.append(month);
        if (year != null) {
            sb.append(",");
            sb.append("year=");
            sb.append(year);
        }
        sb.append("}");
        return sb.toString();
    }

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
