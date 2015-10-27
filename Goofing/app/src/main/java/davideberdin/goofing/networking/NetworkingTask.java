package davideberdin.goofing.networking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

// My imports
import davideberdin.goofing.MenuActivity;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;

public class NetworkingTask extends AsyncTask
{
    private Activity currentActivity = null;
    private int currentNetworkingState;

    public NetworkingTask(Activity activity) {
        this.currentActivity = activity;
        this.currentNetworkingState = -1;
    }

    @Override
    protected Object doInBackground(Object[] params)
    {
        HashMap<String, String> postParams = new HashMap<>();
        try {

            switch ((int)params[0]) {
                case Constants.NETWORKING_LOGIN_STATE:
                    this.currentNetworkingState = Constants.NETWORKING_LOGIN_STATE;

                    postParams.put("Username", (String)params[1]);
                    postParams.put("Password", (String)params[2]);

                    return performPostCall(Constants.SERVER_URL + Constants.LOGIN_URL, postParams);
                case Constants.NETWORKING_REGISTER_STATE:

                    assert User.getUser() != null;
                    this.currentNetworkingState = Constants.NETWORKING_REGISTER_STATE;

                    // we should have a User created at this point
                    postParams.put("Username", User.getUser().GetUsername());
                    postParams.put("Password", User.getUser().GetPassword());
                    postParams.put("Gender", User.getUser().GetGender());
                    postParams.put("Nationality", User.getUser().GetNationality());
                    postParams.put("Occupation", User.getUser().GetOccupation());

                    return performPostCall(Constants.SERVER_URL + Constants.REGISTRATION_URL, postParams);
                case Constants.NETWORKING_GET_SENTENCE:
                    this.currentNetworkingState = Constants.NETWORKING_GET_SENTENCE;

                    // no parameters
                    return performPostCall(Constants.SERVER_URL + Constants.GET_SENTENCE_URL, postParams);

                default:
                    Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.GENERAL_ERROR_REQUEST);
                    break;
            }
        } catch (Exception ex) {
            Logger.Log(Constants.CONNECTION_ACTIVITY, "Error in NETWORKING!");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object result)
    {
        // Handle every response here
        try {

            assert result != null;
            if (!(result instanceof String)) throw new AssertionError();

            JSONObject jsonObject = new JSONObject((String)result);

            String response = "";
            response = jsonObject.getString("Response");

            if (response.equals(Constants.FAILED_POST) || response.isEmpty()) {
                Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.TOAST_ERROR_LOGIN_ERROR);
                Toast.makeText(currentActivity, Constants.TOAST_ERROR_LOGIN_ERROR, Toast.LENGTH_SHORT).show();
                return;
            }

            assert response.equals(Constants.SUCCESS_POST);

            User u = User.getUser();
            HashMap<String, Object> responseObject = (HashMap<String, Object>) jsonToMap(jsonObject);

            switch (this.currentNetworkingState) {
                    case Constants.NETWORKING_LOGIN_STATE:
                    case Constants.NETWORKING_REGISTER_STATE:
                        u.SetUsername((String) responseObject.get(Constants.GET_USERNAME_POST));
                        u.SetGender((String) responseObject.get(Constants.GET_GENDER_POST));
                        u.SetNationality((String) responseObject.get(Constants.GET_NATIONALITY_POST));
                        u.SetOccupation((String) responseObject.get(Constants.GET_OCCUPATION_POST));

                        currentActivity.startActivity(new Intent(currentActivity, MenuActivity.class));
                        break;
                    case Constants.NETWORKING_GET_SENTENCE:
                        // assume that the user has been either loaded or previously created
                        String sentence = (String)responseObject.get(Constants.GET_SENTENCE_POST);
                        u.SetCurrentSentence(sentence);

                        String phonetic = (String)responseObject.get(Constants.GET_PHONETIC_POST);
                        u.SetCurrentPhonetic(phonetic);

                        break;
                    default:
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.GENERAL_ERROR_RESPONSE);
                        break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams)
    {
        URL url;
        String response = "";

        try
        {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="FAILED";
            }
        } catch (Exception e) {
            Logger.Log(Constants.CONNECTION_ACTIVITY, e.getMessage());
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params)
    {
        JSONObject obj = new JSONObject();
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            try {
                obj.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                Logger.Log(Constants.CONNECTION_ACTIVITY, e.getMessage());
            }
        }
        return obj.toString();
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}

