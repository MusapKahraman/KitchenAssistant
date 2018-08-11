/*
 * Reference
 * http://www.vogella.com/tutorials/AndroidParcelable/article.html
 */

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
    @NonNull
    public int id;
    @NonNull public String title;
    public String imagePath;
    public int servings;
    public int prepTime;
    public int cookTime;
    public String language;
    public String cuisine;
    public String course;
    public String writerUid;
    public String writerName;
    public long timeStamp;
    public String publicKey;
    public float rating;


    @Ignore
    public Recipe() {
    }

    @Ignore
    public Recipe(long timeStamp) {
        this.title = "";
        this.imagePath = "";
        this.servings = 1;
        this.language = "";
        this.cuisine = "";
        this.course = "";
        this.writerUid = "";
        this.writerName = "";
        this.timeStamp = timeStamp;
        this.publicKey = "";
    }

    public Recipe(int id, @NonNull String title, String imagePath, int prepTime, int cookTime, String language, String cuisine, String course,
                  String writerUid, String writerName, int servings, long timeStamp, String publicKey, float rating) {
        if (id != 0) {
            this.id = id;
        }
        this.title = title;
        this.imagePath = imagePath;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.language = language;
        this.cuisine = cuisine;
        this.course = course;
        this.writerUid = writerUid;
        this.writerName = writerName;
        this.timeStamp = timeStamp;
        this.publicKey = publicKey;
        this.rating = rating;
    }

    private Recipe(Parcel in) {
        id = in.readInt();
        title = in.readString();
        imagePath = in.readString();
        servings = in.readInt();
        prepTime = in.readInt();
        cookTime = in.readInt();
        language = in.readString();
        cuisine = in.readString();
        course = in.readString();
        writerUid = in.readString();
        writerName = in.readString();
        timeStamp = in.readLong();
        publicKey = in.readString();
        rating = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeInt(servings);
        dest.writeInt(prepTime);
        dest.writeInt(cookTime);
        dest.writeString(language);
        dest.writeString(cuisine);
        dest.writeString(course);
        dest.writeString(writerUid);
        dest.writeString(writerName);
        dest.writeLong(timeStamp);
        dest.writeString(publicKey);
        dest.writeFloat(rating);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nname: " + title +
                "\nimagePath: " + imagePath +
                "\nservings: " + servings +
                "\nrecipeId: " + prepTime +
                "\nfood_id: " + cookTime +
                "\nlanguage: " + language +
                "\ncuisine: " + cuisine +
                "\ncourse: " + course +
                "\nwriterUid: " + writerUid +
                "\nwriterName: " + writerName +
                "\ntimeStamp: " + timeStamp +
                "\npublicKey: " + publicKey +
                "\nrating: " + rating;
    }
}
