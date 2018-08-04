package com.example.kitchen.data.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "steps")
public class Step implements Parcelable {
    public static final Creator<Step> CREATOR = new Creator<Step>() {
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String direction;
    public int stepNumber;
    public int recipe_id;

    @Ignore
    public Step(@NonNull String direction) {
        this.direction = direction;
        this.stepNumber = 0;
        this.recipe_id = 0;
    }

    public Step(@NonNull String direction, int stepNumber, int recipe_id) {
        this.direction = direction;
        this.stepNumber = stepNumber;
        this.recipe_id = recipe_id;
    }

    private Step(Parcel in) {
        id = in.readInt();
        direction = in.readString();
        stepNumber = in.readInt();
        recipe_id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(direction);
        dest.writeInt(stepNumber);
        dest.writeInt(recipe_id);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nname: " + direction +
                "\nrecipe_id: " + stepNumber +
                "\nfood_id: " + recipe_id;
    }
}
