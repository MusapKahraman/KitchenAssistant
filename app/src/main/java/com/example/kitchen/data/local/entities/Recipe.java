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
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String title;
    public String photoUrl;
    public int servings;
    public int prepTime;
    public int cookTime;
    public String language;
    public String cuisine;
    public String course;
    public String writer;
    public long timeStamp;
    public String publicKey;


    @Ignore
    public Recipe() {
    }

    @Ignore
    public Recipe(@NonNull String title, long timeStamp) {
        this.title = title;
        this.photoUrl = "";
        this.servings = 1;
        this.prepTime = 0;
        this.cookTime = 0;
        this.language = "";
        this.cuisine = "";
        this.course = "";
        this.writer = "";
        this.timeStamp = timeStamp;
        this.publicKey = "";
    }

    public Recipe(@NonNull String title, String photoUrl, int prepTime, int cookTime, String language, String cuisine, String course,
                  String writer, int servings, long timeStamp, String publicKey) {
        this.title = title;
        this.photoUrl = photoUrl;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.language = language;
        this.cuisine = cuisine;
        this.course = course;
        this.writer = writer;
        this.timeStamp = timeStamp;
        this.publicKey = publicKey;
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        photoUrl = in.readString();
        servings = in.readInt();
        prepTime = in.readInt();
        cookTime = in.readInt();
        language = in.readString();
        cuisine = in.readString();
        course = in.readString();
        writer = in.readString();
        timeStamp = in.readLong();
        publicKey = in.readString();
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
        dest.writeInt(servings);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeString(language);
        dest.writeString(cuisine);
        dest.writeString(course);
        dest.writeString(writer);
        dest.writeLong(timeStamp);
        dest.writeString(publicKey);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nname: " + title +
                "\nphotoUrl: " + photoUrl +
                "\nservings: " + servings +
                "\nrecipe_id: " + prepTime +
                "\nfood_id: " + cookTime +
                "\nlanguage: " + language +
                "\ncuisine: " + cuisine +
                "\ncourse: " + course +
                "\nwriter: " + writer +
                "\ntimeStamp: " + timeStamp +
                "\npublicKey: " + publicKey;
    }
}
