package com.example.codex01.Domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable {
    private int Id;
    private String Name;
    private String ImagePath;
    private int Number;

    public Category() {
    }

    public Category(int id, String name, String imagePath, int number) {
        this.Id = id;
        this.Name = name;
        this.ImagePath = imagePath;
        this.Number = number;
    }

    protected Category(Parcel in) {
        Id = in.readInt();
        Name = in.readString();
        ImagePath = in.readString();
        Number = in.readInt();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        this.ImagePath = imagePath;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        this.Number = number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Name);
        dest.writeString(ImagePath);
        dest.writeInt(Number);
    }
}