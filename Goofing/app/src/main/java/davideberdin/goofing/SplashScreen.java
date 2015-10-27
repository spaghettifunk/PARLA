package davideberdin.goofing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        final ImageView imageView = (ImageView) findViewById(R.id.splashImageView);
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_popup_enter);
        animation.setDuration(2000);

        imageView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finish();
                Intent newIntent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(newIntent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
}
