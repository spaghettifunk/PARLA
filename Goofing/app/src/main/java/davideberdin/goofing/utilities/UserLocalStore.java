package davideberdin.goofing.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import davideberdin.goofing.controllers.User;


public class UserLocalStore
{
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("Username", user.GetUsername());
        spEditor.putString("Password", user.GetPassword());
        spEditor.putString("Gender", user.GetGender());
        spEditor.putString("Nationality", user.GetNationality());
        spEditor.putString("Occupation", user.GetOccupation());
        spEditor.putString("Sentence", user.GetCurrentSentence());
        spEditor.putString("Phonetic", user.GetCurrentPhonetic());

        spEditor.commit();
    }

    public User getLoggedUser(){
        String username = userLocalDatabase.getString("Username", "");
        String password = userLocalDatabase.getString("Password", "");
        String gender = userLocalDatabase.getString("Gender", "");
        String nationality = userLocalDatabase.getString("Nationality", "");
        String occupation = userLocalDatabase.getString("Occupation", "");
        String sentence = userLocalDatabase.getString("Sentence", "");
        String phonetic = userLocalDatabase.getString("Phonetic", "");

        User storedUser = new User(username, password, gender, nationality, occupation, sentence, phonetic);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", loggedIn);

        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        if (userLocalDatabase.getBoolean("LoggedIn", false) == true)
            return true;
        else
            return false;
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
