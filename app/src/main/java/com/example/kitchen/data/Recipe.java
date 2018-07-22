package com.example.kitchen.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "recipes")
public class Recipe implements Parcelable {

    public static final Parcelable.Creator<Recipe> CREATOR
            = new Parcelable.Creator<Recipe>() {
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "_id")
    public int id;
    public String title;
    public String photoUrl;
    public int prepTime;
    public int cookTime;
    public float rating;
    public String languageCode;
    public String cuisineCode;
    public String courseCode;
    public String writer;
    public int servings;

    public Recipe(@NonNull int id, String title, String photoUrl, int prepTime, int cookTime,
                  float rating, String languageCode, String cuisineCode, String courseCode,
                  String writer, int servings) {
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.rating = rating;
        this.languageCode = languageCode;
        this.cuisineCode = cuisineCode;
        this.courseCode = courseCode;
        this.writer = writer;
        this.servings = servings;
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        photoUrl = in.readString();
        prepTime = in.readInt();
        cookTime = in.readInt();
        rating = in.readFloat();
        languageCode = in.readString();
        cuisineCode = in.readString();
        courseCode = in.readString();
        writer = in.readString();
        servings = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(photoUrl);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeFloat(rating);
        dest.writeString(languageCode);
        dest.writeString(cuisineCode);
        dest.writeString(courseCode);
        dest.writeString(writer);
        dest.writeInt(servings);
    }
}
