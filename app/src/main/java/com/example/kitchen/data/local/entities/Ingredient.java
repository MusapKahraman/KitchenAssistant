package com.example.kitchen.data.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "ingredients")
public class Ingredient implements Parcelable {
    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int recipe_id;
    public String food;
    public int amount;
    public String amountType;

    public Ingredient(int recipe_id, String food, int amount, String amountType) {
        this.recipe_id = recipe_id;
        this.food = food;
        this.amount = amount;
        this.amountType = amountType;
    }

    @Ignore
    public Ingredient(int id, int recipe_id, String food, int amount, String amountType) {
        this.id = id;
        this.recipe_id = recipe_id;
        this.food = food;
        this.amount = amount;
        this.amountType = amountType;
    }

    private Ingredient(Parcel in) {
        id = in.readInt();
        recipe_id = in.readInt();
        food = in.readString();
        amount = in.readInt();
        amountType = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(recipe_id);
        dest.writeString(food);
        dest.writeInt(amount);
        dest.writeString(amountType);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nrecipe_id: " + recipe_id +
                "\nfood: " + food +
                "\namount: " + amount +
                "\namountType: " + amountType;
    }
}
