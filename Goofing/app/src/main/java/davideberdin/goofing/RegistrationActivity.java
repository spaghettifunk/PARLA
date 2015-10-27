package davideberdin.goofing;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.ErrorManager;
import davideberdin.goofing.utilities.UserLocalStore;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button register;

    private EditText nameTextField;
    private EditText passwordTextField;
    private Spinner genderSpinner;
    private Spinner nationalitySpinner;
    private Spinner occupationSpinner;

    private String name_text;
    private String password_text;
    private String gender_text;
    private String nationality_text;
    private String occupation_text;

    private UserLocalStore userLocalStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // let's go fullscreen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registration);

        this.userLocalStore = new UserLocalStore(this);

        this.nameTextField = (EditText) findViewById(R.id.etUsernameReg);
        this.passwordTextField = (EditText) findViewById(R.id.etPasswordReg);
        this.genderSpinner = (Spinner) findViewById(R.id.spGender);
        this.nationalitySpinner = (Spinner) findViewById(R.id.spNationality);
        this.occupationSpinner = (Spinner) findViewById(R.id.spOccupation);

        this.register = (Button) findViewById(R.id.registerButtonReg);
        this.register.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.registerButtonReg:

                name_text = nameTextField.getText().toString();
                password_text = passwordTextField.getText().toString();
                gender_text = genderSpinner.getSelectedItem().toString();
                nationality_text = nationalitySpinner.getSelectedItem().toString();
                occupation_text = occupationSpinner.getSelectedItem().toString();

                if (name_text.isEmpty() || password_text.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
                    return;
                }

                final User registeredUser = new User(name_text, password_text, gender_text, nationality_text, occupation_text, "", "");

                // register here
                ServerRequest request = new ServerRequest(this);
                request.storeUserDataInBackground(registeredUser, new GetCallback() {
                    @Override
                    public void done(Object... params)
                    {
                        if(params[0] instanceof JSONObject){
                            JSONObject obj = (JSONObject) params[0];
                            try {
                                ErrorManager.showErrorMessage(RegistrationActivity.this, obj.getString("Reason"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            String sentence = (String) params[0];
                            String phonetic = (String) params[1];

                            registeredUser.SetCurrentSentence(sentence);
                            registeredUser.SetCurrentPhonetic(phonetic);

                            userLocalStore.storeUserData(registeredUser);
                            userLocalStore.setUserLoggedIn(true);
                            startActivity(new Intent(RegistrationActivity.this, MenuActivity.class));
                        }
                    }
                });

                break;
        }
    }
}
