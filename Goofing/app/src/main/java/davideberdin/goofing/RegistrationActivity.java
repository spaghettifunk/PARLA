package davideberdin.goofing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import davideberdin.goofing.controllers.User;
import davideberdin.goofing.networking.Networking;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;

public class RegistrationActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // let's go fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        Button register = (Button) findViewById(R.id.registerButtonReg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser(v);
            }
        });
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

    private void createUser(View view)
    {
        try {
            // retrieve values from GUI
            EditText nameTextField = (EditText) findViewById(R.id.etUsernameReg);
            String name_text = nameTextField.getText().toString();

            EditText passwordTextField = (EditText) findViewById(R.id.etPasswordReg);
            String password_text = passwordTextField.getText().toString();

            Spinner genderSpinner = (Spinner) findViewById(R.id.spGender);
            String gender_text = genderSpinner.getSelectedItem().toString();

            Spinner nationalitySpinner = (Spinner) findViewById(R.id.spNationality);
            String nationality_text = nationalitySpinner.getSelectedItem().toString();

            Spinner occupationSpinner = (Spinner) findViewById(R.id.spOccupation);
            String occupation_text = occupationSpinner.getSelectedItem().toString();

            if (name_text.isEmpty() || password_text.isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                builder.setMessage(R.string.emptyFields).setTitle(R.string.errorDialogTitle);

                builder.setPositiveButton(R.string.okText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // create the actual user - singleton instance
                User.createUser(name_text, password_text, gender_text, nationality_text, occupation_text);
                // throw exceptions - should never happen :)
                if (User.getUser() != null) {
                    // register here
                    Networking n = new Networking();
                    n.execute(Constants.NETWORKING_REGISTER_STATE);

                } else {
                    throw new Exception();
                }
            }
        }
        catch(Exception ex)
        {
            Logger.Log(Constants.REGISTRATION_ACTIVITY, ex.getMessage());
            return;
        }
    }
}
