/*
 * Reference
 * https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a#da9f
 * http://www.vogella.com/tutorials/AndroidParcelable/article.html
 */

package com.example.kitchen.data.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "shopping_list")
public class Ware implements Parcelable {
    public static final Creator<Ware> CREATOR = new Creator<Ware>() {
        public Ware createFromParcel(Parcel in) {
            return new Ware(in);
        }

        public Ware[] newArray(int size) {
            return new Ware[size];
        }
    };
    @PrimaryKey(autoGenerate = true) public int id;
    public String name;
    public int amount;
    public String amountType;

    @Ignore
    public Ware(int id, String name, int amount, String amountType) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.amountType = amountType;
    }

    public Ware(String name, int amount, String amountType) {
        this.name = name;
        this.amount = amount;
        this.amountType = amountType;
    }

    private Ware(Parcel in) {
        id = in.readInt();
        name = in.readString();
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
        dest.writeString(name);
        dest.writeInt(amount);
        dest.writeString(amountType);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nname: " + name +
                "\namount: " + amount +
                "\namountType: " + amountType;
    }
}
