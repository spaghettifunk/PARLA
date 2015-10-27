package davideberdin.goofing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

// my imports
import org.json.JSONException;
import org.json.JSONObject;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.ErrorManager;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText etUsername;
    private EditText etPassword;

    private String username;
    private String password;

    private Button loginButton;
    private Button registerButton;

    private UserLocalStore userLocalStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        this.userLocalStore = new UserLocalStore(this);

        this.etUsername = (EditText) findViewById(R.id.etUsername);
        this.etPassword = (EditText) findViewById(R.id.etPassword);

        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.registerButton = (Button) findViewById(R.id.registerButton);

        this.loginButton.setOnClickListener(this);
        this.registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginButton:

                Logger.Log(Constants.LOGIN_ACTIVITY, "Clicked Login");
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();

                if(username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Insert username or password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Networking stuff here
                User user = new User(username, password);
                ServerRequest request = new ServerRequest(this);
                request.fetchUserDataInBackgroud(user, new GetCallback()
                {
                    @Override
                    public void done(Object... params) {
                        if (params[0] instanceof JSONObject) {
                            JSONObject obj = (JSONObject) params[0];
                            try {
                                ErrorManager.showErrorMessage(LoginActivity.this, obj.getString("Reason"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            User u = (User) params[0];
                            if (u == null) {
                                ErrorManager.showErrorMessage(LoginActivity.this, Constants.TOAST_ERROR_LOGIN_ERROR);
                            } else {
                                userLocalStore.storeUserData(u);
                                userLocalStore.setUserLoggedIn(true);
                                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                            }
                        }
                    }
                });
                break;
            case R.id.registerButton:

                Logger.Log(Constants.LOGIN_ACTIVITY, "Clicked Register");

                Intent inent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(inent);

                break;
        }
    }
}
