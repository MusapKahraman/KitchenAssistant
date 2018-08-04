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
    public int food_id;
    public float amount;

    @Ignore
    public Ingredient() {
        this.recipe_id = 0;
        this.food_id = 0;
        this.amount = 0;
    }

    public Ingredient(int recipe_id, int food_id, float amount) {
        this.recipe_id = recipe_id;
        this.food_id = food_id;
        this.amount = amount;
    }

    private Ingredient(Parcel in) {
        id = in.readInt();
        recipe_id = in.readInt();
        food_id = in.readInt();
        amount = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(recipe_id);
        dest.writeInt(food_id);
        dest.writeFloat(amount);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nrecipe_id: " + recipe_id +
                "\nfood_id: " + food_id +
                "\namount: " + amount;
    }
}
