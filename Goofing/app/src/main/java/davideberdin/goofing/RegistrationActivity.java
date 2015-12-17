package davideberdin.goofing;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.GetCallback;
import davideberdin.goofing.networking.ServerRequest;
import davideberdin.goofing.utilities.AppWindowManager;
import davideberdin.goofing.utilities.Logger;
import davideberdin.goofing.utilities.UserLocalStore;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button register;

    private EditText nameTextField;
    private EditText passwordTextField;
    private EditText passwordTextField2;
    private Spinner genderSpinner;
    private Spinner nationalitySpinner;
    private Spinner occupationSpinner;

    private UserLocalStore userLocalStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        this.userLocalStore = new UserLocalStore(this);

        this.nameTextField = (EditText) findViewById(R.id.etUsernameReg);
        this.passwordTextField = (EditText) findViewById(R.id.etPasswordReg);
        this.passwordTextField2 = (EditText) findViewById(R.id.etPassword2);
        this.genderSpinner = (Spinner) findViewById(R.id.spGender);
        this.nationalitySpinner = (Spinner) findViewById(R.id.spNationality);
        this.occupationSpinner = (Spinner) findViewById(R.id.spOccupation);

        this.register = (Button) findViewById(R.id.registerButtonReg);
        this.register.setOnClickListener(this);

        // get the nationalities available in Android
        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, countries);
        this.nationalitySpinner.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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

                Logger.WriteOnReport("RegistrationActivity", "Clicked on Register BUTTON");

                String name_text = nameTextField.getText().toString();
                String password_text = passwordTextField.getText().toString();
                String password_text2 = passwordTextField2.getText().toString();
                String gender_text = genderSpinner.getSelectedItem().toString();
                String nationality_text = nationalitySpinner.getSelectedItem().toString();
                String occupation_text = occupationSpinner.getSelectedItem().toString();

                // check main fields
                if (name_text.isEmpty() || password_text.isEmpty() || password_text2.isEmpty()){
                    Toast.makeText(RegistrationActivity.this, R.string.emptyFields, Toast.LENGTH_SHORT).show();
                    return;
                }

                // check password
                if (!password_text.equals(password_text2)){
                    Toast.makeText(RegistrationActivity.this, R.string.passwordsDoNotMatch, Toast.LENGTH_SHORT).show();
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
                                AppWindowManager.showErrorMessage(RegistrationActivity.this, obj.getString("Reason"));
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
