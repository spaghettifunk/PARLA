package davideberdin.goofing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class FeedbacksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);

        Bundle b = getIntent().getExtras();
        byte[] audioFileAsByte = b.getByteArray("vowelchart");

        ImageView imageView = (ImageView) findViewById(R.id.vowelChartImageView);

        // Build feedbacks image
        Bitmap bmp = BitmapFactory.decodeByteArray(audioFileAsByte, 0, audioFileAsByte.length);
        imageView.setImageBitmap(bmp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
