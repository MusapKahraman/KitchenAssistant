/*
 * Reference
 * https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a#da9f
 * http://www.vogella.com/tutorials/AndroidParcelable/article.html
 */

package com.example.kitchen.data.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "ingredients",
        indices = {@Index("recipeId")},
        foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = CASCADE))
public class Ingredient implements Parcelable {
    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    @PrimaryKey(autoGenerate = true) public int id;
    public int recipeId;
    public String food;
    public int amount;
    public String amountType;
    public String publicKey;

    @Ignore
    public Ingredient(int id, int recipeId, String food, int amount, String amountType, String publicKey) {
        this.id = id;
        this.recipeId = recipeId;
        this.food = food;
        this.amount = amount;
        this.amountType = amountType;
        this.publicKey = publicKey;
    }

    public Ingredient(int recipeId, String food, int amount, String amountType, String publicKey) {
        this.recipeId = recipeId;
        this.food = food;
        this.amount = amount;
        this.amountType = amountType;
        this.publicKey = publicKey;
    }

    private Ingredient(Parcel in) {
        id = in.readInt();
        recipeId = in.readInt();
        food = in.readString();
        amount = in.readInt();
        amountType = in.readString();
        publicKey = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(recipeId);
        dest.writeString(food);
        dest.writeInt(amount);
        dest.writeString(amountType);
        dest.writeString(publicKey);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nrecipeId: " + recipeId +
                "\nname: " + food +
                "\namount: " + amount +
                "\namountType: " + amountType +
                "\npublicKey: " + publicKey;
    }
}
