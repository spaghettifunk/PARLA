package davideberdin.goofing.controllers;

public class TrendCard {

    private float[] imageFloatArray;
    private String[] imageTimeArray;
    private String imageTitle;

    public void setImageFloatArray(float[] img) { this.imageFloatArray = img; }
    public float[] getImageFloatArray() { return this.imageFloatArray; }

    public void setImageTimeArray(String[] img) { this.imageTimeArray = img; }
    public String[] getImageTimeArray() { return this.imageTimeArray; }

    public void setImageTitle(String title) { this.imageTitle = title; }
    public String getImageTitle() { return this.imageTitle; }
}
