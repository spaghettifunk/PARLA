package davideberdin.goofing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import davideberdin.goofing.utilities.GoofingExceptions;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void createUser(View view)
    {
        try {
            // retrieve values from GUI
            EditText nameTextField = (EditText) findViewById(R.id.nameText);
            String name_text = nameTextField.getText().toString();

            Spinner areaSpinner = (Spinner) findViewById(R.id.regional_area_spinner);
            String regional_text = areaSpinner.getSelectedItem().toString();

            Spinner nationalitySpinner = (Spinner) findViewById(R.id.nationality_spinner);
            String nationality_text = nationalitySpinner.getSelectedItem().toString();

            Spinner occupationSpinner = (Spinner) findViewById(R.id.occupation_spinner);
            String occupation_text = occupationSpinner.getSelectedItem().toString();

            if (name_text.compareTo("Name") == 0 || name_text.isEmpty()){
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.different_username).setTitle(R.string.error_dialog_title);

                builder.setPositiveButton(R.string.ok_text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {

                // create the actual user - singleton instance
                User.createUser(name_text, regional_text, nationality_text, occupation_text);
                // throw exceptions - should never happen :)
                if (User.getUser() != null) {
                    // start a new activity here
                    Intent selectionPageIntent = new Intent(this, SelectionActivity.class);
                    startActivity(selectionPageIntent);
                } else {
                    throw new GoofingExceptions("UserTag", "Failed to create User");
                }
            }
        }
        catch(GoofingExceptions ex)
        {
            ex.getMessage();
            return;
        }
    }
}
