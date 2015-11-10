package davideberdin.goofing.networking;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

// My imports
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;

public class NetworkingTask extends AsyncTask
{
    private User user;
    private GetCallback userCallback;
    private ProgressDialog progressDialog;

    private int currentNetworkingState;

    public NetworkingTask(User u, GetCallback userCallback, ProgressDialog progressDialog) {
        this.currentNetworkingState = -1;

        this.user = u;
        this.userCallback = userCallback;
        this.progressDialog = progressDialog;
    }

    public NetworkingTask(GetCallback userCallback, ProgressDialog progressDialog) {
        this.currentNetworkingState = -1;

        this.userCallback = userCallback;
        this.progressDialog = progressDialog;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        HashMap<String, String> postParams = new HashMap<>();
        try {

            switch ((int)params[0])
            {
                case Constants.NETWORKING_LOGIN_STATE:

                    this.currentNetworkingState = Constants.NETWORKING_LOGIN_STATE;

                    User loggedUser = (User)params[1];

                    postParams.put("Username", loggedUser.GetUsername());
                    postParams.put("Password", loggedUser.GetPassword());

                    return performPostCall(Constants.SERVER_URL + Constants.LOGIN_URL, postParams);

                case Constants.NETWORKING_REGISTER_STATE:

                    this.currentNetworkingState = Constants.NETWORKING_REGISTER_STATE;

                    User registeredUser = (User)params[1];

                    postParams.put("Username", registeredUser.GetUsername());
                    postParams.put("Password", registeredUser.GetPassword());
                    postParams.put("Gender", registeredUser.GetGender());
                    postParams.put("Nationality", registeredUser.GetNationality());
                    postParams.put("Occupation", registeredUser.GetOccupation());

                    return performPostCall(Constants.SERVER_URL + Constants.REGISTRATION_URL, postParams);

                case Constants.NETWORKING_HANDLE_RECORDED_VOICE:

                    this.currentNetworkingState = Constants.NETWORKING_HANDLE_RECORDED_VOICE;

                    // Treat wav file to send to server
                    User currentUser = (User)params[1];
                    byte[] fileAudio = (byte[])params[2];
                    String currentSentence = (String)params[3];

                    //String decoded = new String(fileAudio, "UTF-8");
                    String audioFileAsString = Base64.encodeToString(fileAudio, Base64.DEFAULT);

                    String u = toJSON(currentUser);
                    postParams.put("User", u);
                    postParams.put("FileAudio", audioFileAsString);
                    postParams.put("Sentence", currentSentence);

                    return performPostCall(Constants.SERVER_URL + Constants.HANDLE_RECORDING_URL, postParams);

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
    protected void onPostExecute(Object result) {
        this.progressDialog.dismiss();

        // Handle every response here
        try {

            assert result != null;
            if (!(result instanceof String)) throw new AssertionError();

            JSONObject jsonObject = new JSONObject((String)result);

            String response = "";
            response = jsonObject.getString("Response");

            if (response.equals(Constants.FAILED_POST) || response.isEmpty()){
                this.userCallback.done(jsonObject);
            }
            else
            {
                assert response.equals(Constants.SUCCESS_POST);

                HashMap<String, Object> responseObject = (HashMap<String, Object>) jsonToMap(jsonObject);

                switch (this.currentNetworkingState)
                {
                    case Constants.NETWORKING_LOGIN_STATE:
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.LOGIN_ACTIVITY);

                        String username = ((String) responseObject.get(Constants.GET_USERNAME_POST));
                        String password = ((String) responseObject.get(Constants.GET_PASSWORD_POST));
                        String gender = ((String) responseObject.get(Constants.GET_GENDER_POST));
                        String nationality = ((String) responseObject.get(Constants.GET_NATIONALITY_POST));
                        String occupation = ((String) responseObject.get(Constants.GET_OCCUPATION_POST));
                        String sentence = (String) responseObject.get(Constants.GET_SENTENCE_POST);
                        String phonetic = (String) responseObject.get(Constants.GET_PHONETIC_POST);

                        User loggedUser = new User(username, password, gender, nationality, occupation, sentence, phonetic);

                        this.userCallback.done(loggedUser);
                        break;

                    case Constants.NETWORKING_REGISTER_STATE:
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.REGISTRATION_ACTIVITY);

                        String sentenceReg = (String) responseObject.get(Constants.GET_SENTENCE_POST);
                        String phoneticReg = (String) responseObject.get(Constants.GET_PHONETIC_POST);

                        this.userCallback.done(sentenceReg, phoneticReg);

                        break;

                    case Constants.NETWORKING_HANDLE_RECORDED_VOICE:
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.PRONUNCIATION_ACTIVITY);

                        // handle response here
                        String pitch_chart = (String) responseObject.get(Constants.GET_PITCH_CHART_POST);
                        byte[] pitch_chart_byte = Base64.decode(pitch_chart, Base64.DEFAULT);

                        String vowel_chart = (String) responseObject.get(Constants.GET_VOWEL_CHART_POST);
                        byte[] vowel_chart_byte = Base64.decode(vowel_chart, Base64.DEFAULT);

                        userCallback.done(pitch_chart_byte, vowel_chart_byte);
                        break;

                    default:
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.GENERAL_ERROR_RESPONSE);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams) {
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

    private String getPostDataString(HashMap<String, String> params) {
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

    public static String toJSON(Object object) throws JSONException, IllegalAccessException {
        String str = "";
        Class c = object.getClass();
        JSONObject jsonObject = new JSONObject();
        for (Field field : c.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            String value = String.valueOf(field.get(object));
            jsonObject.put(name, value);
        }
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
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

