package davideberdin.goofing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// my imports
import org.json.JSONException;
import org.json.JSONObject;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Debug;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText etUsername;
    private EditText etPassword;

    private String username;
    private String password;

    // variables for saving the login
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    private Button loginButton;
    private TextView registerButton;

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
        this.registerButton = (TextView) findViewById(R.id.registerButton);

        saveLoginCheckBox = (CheckBox)findViewById(R.id.rememberMeCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            etUsername.setText(loginPreferences.getString("username", ""));
            etPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

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

                if (Debug.debugging)
                {
                    User u = new User("a" , "a", "m", "italian", "student", Constants.nativeSentences[0], Constants.nativePhonetics[0]);
                    userLocalStore.storeUserData(u);
                    userLocalStore.setUserLoggedIn(true);
                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                }
                // TODO: remove the if-statement when I will be able to connect to server
                else
                {
                    if(username.isEmpty() || password.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Insert username or password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Networking stuff here
                    User user = new User(username, password);
                    ServerRequest request = new ServerRequest(this);
                    request.fetchUserDataInBackgroud(user, new GetCallback() {
                        @Override
                        public void done(Object... params) {
                            if (params[0] instanceof JSONObject) {
                                JSONObject obj = (JSONObject) params[0];
                                try {
                                    AppWindowManager.showErrorMessage(LoginActivity.this, obj.getString("Reason"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                User u = (User) params[0];
                                if (u == null) {
                                    AppWindowManager.showErrorMessage(LoginActivity.this, Constants.TOAST_ERROR_LOGIN_ERROR);
                                } else {

                                    // save data on phone
                                    if (saveLoginCheckBox.isChecked()) {
                                        loginPrefsEditor.putBoolean("saveLogin", true);
                                        loginPrefsEditor.putString("username", username);
                                        loginPrefsEditor.putString("password", password);
                                        loginPrefsEditor.commit();
                                    } else {
                                        loginPrefsEditor.clear();
                                        loginPrefsEditor.commit();
                                    }

                                    userLocalStore.storeUserData(u);
                                    userLocalStore.setUserLoggedIn(true);
                                    startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.registerButton:

                Logger.Log(Constants.LOGIN_ACTIVITY, "Clicked Register");

                Intent inent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(inent);

                break;
        }
    }
}
