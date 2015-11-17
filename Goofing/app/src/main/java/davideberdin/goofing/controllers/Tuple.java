package davideberdin.goofing.controllers;

import android.os.Parcel;
import android.os.Parcelable;

public class Tuple implements Parcelable {
    private String first;
    private String second;

    public Tuple(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    //region Parcelable
    public Tuple(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Tuple> CREATOR = new Parcelable.Creator<Tuple>() {
        public Tuple createFromParcel(Parcel in) {
            return new Tuple(in);
        }

        public Tuple[] newArray(int size) {
            return new Tuple[size];
        }
    };

    public void readFromParcel(Parcel in) {
        this.first = in.readString();
        this.second = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.first);
        dest.writeString(this.second);
    }
    //endregion
}
