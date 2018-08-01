package com.example.kitchen.data.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "recipes")
public class Recipe implements Parcelable {
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    @PrimaryKey
    @NonNull
    public String title;
    public String photoUrl;
    public int prepTime;
    public int cookTime;
    public float rating;
    public String language;
    public String cuisine;
    public String course;
    public String writer;
    public int servings;
    public long timeStamp;

    @Ignore
    public Recipe(@NonNull String title, long timeStamp) {
        this.title = title;
        this.photoUrl = "";
        this.prepTime = 0;
        this.cookTime = 0;
        this.rating = 0;
        this.language = "";
        this.cuisine = "";
        this.course = "";
        this.writer = "";
        this.servings = 1;
        this.timeStamp = timeStamp;
    }

    public Recipe(@NonNull String title, String photoUrl, int prepTime, int cookTime,
                  float rating, String language, String cuisine, String course,
                  String writer, int servings, long timeStamp) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.rating = rating;
        this.language = language;
        this.cuisine = cuisine;
        this.course = course;
        this.writer = writer;
        this.servings = servings;
        this.timeStamp = timeStamp;
    }

    private Recipe(Parcel in) {
        title = in.readString();
        photoUrl = in.readString();
        prepTime = in.readInt();
        cookTime = in.readInt();
        rating = in.readFloat();
        language = in.readString();
        cuisine = in.readString();
        course = in.readString();
        writer = in.readString();
        servings = in.readInt();
        timeStamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(photoUrl);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeFloat(rating);
        dest.writeString(language);
        dest.writeString(cuisine);
        dest.writeString(course);
        dest.writeString(writer);
        dest.writeInt(servings);
        dest.writeLong(timeStamp);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\ntitle: " + title +
                "\nphotoUrl: " + photoUrl +
                "\nprepTime: " + prepTime +
                "\ncookTime: " + cookTime +
                "\nrating: " + rating +
                "\nlanguage: " + language +
                "\ncuisine: " + cuisine +
                "\ncourse: " + course +
                "\nwriter: " + writer +
                "\nservings: " + servings +
                "\ntimeStamp: " + timeStamp;
    }
}
