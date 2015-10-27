package davideberdin.goofing;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

// my imports
import davideberdin.goofing.networking.NetworkingTask;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;

public class LoginActivity extends AppCompatActivity
{
    private EditText etUsername;
    private EditText etPassword;

    private String username;
    private String password;

    private Button loginButton;
    private Button registerButton;

    private ProgressBar mProgress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // let's go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        this.mProgress = (ProgressBar) findViewById(R.id.loginProgressBar);
        //this.mProgress.setVisibility(View.INVISIBLE);

        this.etUsername = (EditText) findViewById(R.id.etUsername);
        this.etPassword = (EditText) findViewById(R.id.etPassword);

        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Logger.Log(Constants.LOGIN_ACTIVITY_NAME, "Clicked Login");
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                if(username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Insert username or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Networking stuff here
                NetworkingTask net = new NetworkingTask(LoginActivity.this);
                net.execute(Constants.NETWORKING_LOGIN_STATE, username, password);
            }
        });

        registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.Log(Constants.LOGIN_ACTIVITY_NAME, "Clicked Register");

                Intent inent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(inent);
            }
        });
    }
}
