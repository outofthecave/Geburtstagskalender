package com.example.outofthecave.geburtstagskalender.model;

import android.support.annotation.Nullable;

import java.util.Objects;

public final class BirthdayUpdate {
    @Nullable
    public Birthday birthdayToAdd;
    @Nullable
    public Birthday birthdayToReplace;

    @Override
    public boolean equals(@Nullable Object that) {
        if (this == that) {
            return true;
        }

        if (that == null || getClass() != that.getClass()) {
            return false;
        }

        BirthdayUpdate other = (BirthdayUpdate) that;
        return Objects.equals(birthdayToAdd, other.birthdayToAdd)
                && Objects.equals(birthdayToReplace, other.birthdayToReplace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthdayToAdd, birthdayToReplace);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BirthdayUpdate{");
        sb.append("birthdayToAdd=\"");
        sb.append(birthdayToAdd);
        sb.append("\",");
        sb.append("birthdayToReplace=");
        sb.append(birthdayToReplace);
        sb.append("}");
        return sb.toString();
    }
}
