package davideberdin.goofing.controllers;

import android.os.Parcel;
import android.os.Parcelable;

public class StressTuple implements Parcelable {
    private String phoneme;
    private String isStress;

    public StressTuple(String phoneme, String isStress) {
        this.phoneme = phoneme;
        this.isStress = isStress;
    }

    public String getPhoneme() {
        return phoneme;
    }

    public String getIsStress() {
        return isStress;
    }

    //region Parcelable
    public StressTuple(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<StressTuple> CREATOR = new Parcelable.Creator<StressTuple>() {
        public StressTuple createFromParcel(Parcel in) {
            return new StressTuple(in);
        }

        public StressTuple[] newArray(int size) {
            return new StressTuple[size];
        }
    };

    public void readFromParcel(Parcel in) {
        this.phoneme = in.readString();
        this.isStress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phoneme);
        dest.writeString(this.isStress);
    }
    //endregion
}
