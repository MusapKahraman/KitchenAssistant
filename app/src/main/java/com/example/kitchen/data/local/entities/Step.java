/*
 * Reference
 * https://android.jlelse.eu/android-architecture-components-room-relationships-bf473510c14a#da9f
 * http://www.vogella.com/tutorials/AndroidParcelable/article.html
 */

package com.example.kitchen.data.local.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@SuppressWarnings({"NullableProblems", "WeakerAccess", "CanBeFinal"})
@Entity(tableName = "steps",
        foreignKeys = @ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = CASCADE))
public class Step implements Parcelable {
    public static final Creator<Step> CREATOR = new Creator<Step>() {
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
    @PrimaryKey(autoGenerate = true) public int id;
    @NonNull public String instruction;
    public int stepNumber;
    public int recipeId;

    @Ignore
    public Step(int id, @NonNull String instruction, int stepNumber, int recipeId) {
        this.id = id;
        this.instruction = instruction;
        this.stepNumber = stepNumber;
        this.recipeId = recipeId;
    }

    public Step(@NonNull String instruction, int stepNumber, int recipeId) {
        this.instruction = instruction;
        this.stepNumber = stepNumber;
        this.recipeId = recipeId;
    }

    private Step(Parcel in) {
        id = in.readInt();
        instruction = in.readString();
        stepNumber = in.readInt();
        recipeId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(instruction);
        dest.writeInt(stepNumber);
        dest.writeInt(recipeId);
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nid: " + id +
                "\nname: " + instruction +
                "\nrecipeId: " + stepNumber +
                "\nfood_id: " + recipeId;
    }
}
