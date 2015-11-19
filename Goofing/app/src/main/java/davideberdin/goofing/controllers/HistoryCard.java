package davideberdin.goofing.controllers;

import java.util.Date;

public class HistoryCard {
    private String cardDate;
    private byte[] imageByteArray;

    public void setCardDate(String date) { this.cardDate = date; }
    public void setImageByteArray(byte[] img) { this.imageByteArray = img; }
    public String getCardDate() { return this.cardDate; }
    public byte[] getImageByteArray() { return this.imageByteArray; }
}
