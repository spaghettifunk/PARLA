package davideberdin.goofing.controllers;

import android.os.Parcel;
import android.os.Parcelable;


public class CardTuple implements Parcelable {
    private String date;
    private byte[] image;

    public CardTuple(String first, byte[] second) {
        this.date = first;
        this.image = second;
    }

    public String getDate() {
        return date;
    }
    public byte[] getImage() { return image; }

    //region Parcelable
    public CardTuple(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<CardTuple> CREATOR = new Parcelable.Creator<CardTuple>() {
        public CardTuple createFromParcel(Parcel in) {
            return new CardTuple(in);
        }

        public CardTuple[] newArray(int size) {
            return new CardTuple[size];
        }
    };

    public void readFromParcel(Parcel in) {
        this.date = in.readString();
        this.image = in.createByteArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeByteArray(this.image);
    }
    //endregion
}