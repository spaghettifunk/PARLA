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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

// My imports
import davideberdin.goofing.controllers.CardTuple;
import davideberdin.goofing.controllers.TrendTuple;
import davideberdin.goofing.controllers.Tuple;
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;

public class NetworkingTask extends AsyncTask {
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
            switch ((int) params[0]) {
                case Constants.NETWORKING_LOGIN_STATE:
                    //region LOGIN
                    this.currentNetworkingState = Constants.NETWORKING_LOGIN_STATE;

                    User loggedUser = (User) params[1];

                    postParams.put("Username", loggedUser.GetUsername());
                    postParams.put("Password", loggedUser.GetPassword());

                    return performPostCall(Constants.SERVER_URL + Constants.LOGIN_URL, postParams);
                //endregion
                case Constants.NETWORKING_REGISTER_STATE:
                    //region REGISTER
                    this.currentNetworkingState = Constants.NETWORKING_REGISTER_STATE;

                    User registeredUser = (User) params[1];

                    postParams.put("Username", registeredUser.GetUsername());
                    postParams.put("Password", registeredUser.GetPassword());
                    postParams.put("Gender", registeredUser.GetGender());
                    postParams.put("Nationality", registeredUser.GetNationality());
                    postParams.put("Occupation", registeredUser.GetOccupation());

                    return performPostCall(Constants.SERVER_URL + Constants.REGISTRATION_URL, postParams);
                //endregion
                case Constants.NETWORKING_HANDLE_RECORDED_VOICE:
                    //region RECORD
                    this.currentNetworkingState = Constants.NETWORKING_HANDLE_RECORDED_VOICE;

                    // Treat wav file to send to server
                    User currentUser = (User) params[1];
                    byte[] fileAudio = (byte[]) params[2];
                    String predictedPhonemes = (String) params[3];
                    String currentSentence = (String) params[4];

                    //String decoded = new String(fileAudio, "UTF-8");
                    String audioFileAsString = Base64.encodeToString(fileAudio, Base64.DEFAULT);

                    String u = toJSON(currentUser);
                    postParams.put("User", u);
                    postParams.put("FileAudio", audioFileAsString);
                    postParams.put("PredictedPhonemes", predictedPhonemes);
                    postParams.put("Sentence", currentSentence);

                    return performPostCall(Constants.SERVER_URL + Constants.HANDLE_RECORDING_URL, postParams);
                //endregion
                case Constants.NETWORKING_FETCH_HISTORY:
                    //region HISTORY
                    this.currentNetworkingState = Constants.NETWORKING_FETCH_HISTORY;

                    // handle request here
                    String username = (String) params[1];
                    String sentence = (String) params[2];
                    String vowels = (String) params[3];

                    postParams.put("Username", username);
                    postParams.put("Sentence", sentence);
                    postParams.put("Vowels", vowels);

                    return performPostCall(Constants.SERVER_URL + Constants.HANDLE_FETCH_HISTORY_URL, postParams);
                //endregion
                case Constants.NETWORKING_FETCH_PHONEMES:
                    //region PHONEMES SERVICE
                    this.currentNetworkingState = Constants.NETWORKING_FETCH_PHONEMES;

                    // Treat wav file to send to server
                    User user = (User) params[1];
                    byte[] audioFile = (byte[]) params[2];

                    //String decoded = new String(fileAudio, "UTF-8");
                    String decodedAudio = Base64.encodeToString(audioFile, Base64.DEFAULT);

                    postParams.put("User", user.GetUsername());
                    postParams.put("FileAudio", decodedAudio);

                    return performPostCall(Constants.PHONEME_SERVICE_URL, postParams, true);

                //endregion
                case Constants.NETWORKING_SEND_REPORT:
                    //region REPORT
                    this.currentNetworkingState = Constants.NETWORKING_SEND_REPORT;
                    String currentUsername = (String) params[1];
                    String report = Logger.GetReport();

                    postParams.put("Username", currentUsername);
                    postParams.put("Report", report);

                    return performPostCall(Constants.SERVER_URL + Constants.REPORT_URL, postParams);

                    //endregion
                default:
                    Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.GENERAL_ERROR_REQUEST);
                    return null;
            }
        } catch (Exception ex) {
            Logger.Log(Constants.CONNECTION_ACTIVITY, "Error in NETWORKING!");
            return null;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        // Handle every response here
        try {
            assert result != null;
            if (!(result instanceof String)) throw new AssertionError();

            JSONObject jsonObject = new JSONObject((String) result);
            String response = jsonObject.getString("Response");

            if (response.equals(Constants.FAILED_POST) || response.isEmpty()) {
                this.userCallback.done(jsonObject);
            } else {

                HashMap<String, Object> responseObject = (HashMap<String, Object>) jsonToMap(jsonObject);

                switch (this.currentNetworkingState) {
                    case Constants.NETWORKING_LOGIN_STATE:
                        //region LOGIN
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.LOGIN_ACTIVITY);
                        this.progressDialog.dismiss();

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
                    //endregion
                    case Constants.NETWORKING_REGISTER_STATE:
                        //region REGISTER
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.REGISTRATION_ACTIVITY);
                        this.progressDialog.dismiss();

                        String sentenceReg = (String) responseObject.get(Constants.GET_SENTENCE_POST);
                        String phoneticReg = (String) responseObject.get(Constants.GET_PHONETIC_POST);

                        this.userCallback.done(sentenceReg, phoneticReg);
                        break;
                    //endregion
                    case Constants.NETWORKING_HANDLE_RECORDED_VOICE:
                        //region RECORD
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.PRONUNCIATION_ACTIVITY);
                        this.progressDialog.dismiss();

                        // handle response here
                        ArrayList<String> phonemes = (ArrayList<String>) responseObject.get(Constants.GET_PHONEMES_POST);
                        ArrayList<ArrayList<String>> vowelStressObject = (ArrayList<ArrayList<String>>) responseObject.get(Constants.GET_VOWEL_STRESS_POST);

                        ArrayList<Tuple> vowelStress = new ArrayList<Tuple>();
                        for (ArrayList<String> vs : vowelStressObject) {
                            vowelStress.add(new Tuple(vs.get(0), vs.get(1)));
                        }

                        String resultWER = (String) responseObject.get(Constants.GET_WER_POST);

                        ArrayList<String> YValuesNative = (ArrayList<String>) responseObject.get("YValuesNative");
                        ArrayList<String> YValuesUser = (ArrayList<String>) responseObject.get("YValuesUser");

                        String vowelChart = (String) responseObject.get(Constants.GET_VOWEL_CHART_POST);
                        byte[] vowelChartByte = Base64.decode(vowelChart, Base64.DEFAULT);

                        userCallback.done(phonemes, vowelStress, resultWER, YValuesNative, YValuesUser, vowelChartByte);
                        break;
                    //endregion
                    case Constants.NETWORKING_FETCH_HISTORY:
                        //region HISTORY
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.HISTORY_ACTIVITY);
                        this.progressDialog.dismiss();

                        //region History
                        ArrayList<CardTuple> history = new ArrayList<CardTuple>();
                        ArrayList<String> vowelsId = (ArrayList<String>) responseObject.get(Constants.GET_VOWEL_HISTORY_ID);
                        ArrayList<String> vowelsDates = (ArrayList<String>) responseObject.get(Constants.GET_VOWEL_HISTORY_DATE);
                        ArrayList<String> vowelsChartsHistory = (ArrayList<String>) responseObject.get(Constants.GET_VOWEL_HISTORY);

                        int index = 0;
                        for (String chart : vowelsChartsHistory) {
                            String tempId = vowelsId.get(index);
                            String tempDate = vowelsDates.get(index);
                            byte[] tempChart = Base64.decode(chart, Base64.DEFAULT);

                            history.add(new CardTuple(tempId, tempDate, tempChart));
                            index++;
                        }
                        //endregion

                        //region Trend
                        ArrayList<TrendTuple> trend = new ArrayList<>();
                        ArrayList<String> trendChartsTitle = (ArrayList<String>) responseObject.get(Constants.GET_VOWEL_TREND_TITLES);
                        ArrayList<String> trendCharts = (ArrayList<String>) responseObject.get(Constants.GET_VOWEL_TREND);
                        ArrayList<String> trendChartsDates = (ArrayList<String>) responseObject.get(Constants.GET_VOWEL_TREND_DATES);

                        for (int i = 0; i < trendCharts.size(); i++) {

                            // distance from centroid values
                            String y_values = trendCharts.get(i);
                            String y_array = y_values.replace("[", "");
                            y_array = y_array.replace("]", "");

                            String[] y_split = y_array.split(",");
                            float[] tempChart = new float[y_split.length];
                            int index_y = 0;
                            for (String s : y_split) {
                                String val = s.replaceAll("\"", "");
                                tempChart[index_y++] = Float.parseFloat(val);
                            }

                            // time values
                            String x_values = trendChartsDates.get(i);
                            String x_array = x_values.replace("[", "");
                            x_array = x_array.replace("]", "");
                            x_array = x_array.replace("\"", "");
                            String[] x_split = x_array.split(",");

                            // get title
                            String title = trendChartsTitle.get(i);

                            trend.add(new TrendTuple(tempChart, x_split, title));
                        }
                        //endregion

                        userCallback.done(history, trend);
                        break;
                    //endregion
                    case Constants.NETWORKING_FETCH_PHONEMES:
                        //region PHONEMES
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.PHONE_SEARCH);
                        String phonemesResult = (String) responseObject.get(Constants.GET_RESULT_PHONEMES_SERVICE);

                        userCallback.done(phonemesResult);
                        break;
                        //endregion
                    case Constants.NETWORKING_SEND_REPORT:
                        //region REPORT
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.REPORT_ACTIVITY);

                        this.progressDialog.dismiss();
                        userCallback.done(true);
                        break;
                        //endregion
                    default:
                        Logger.Log(Constants.CONNECTION_ACTIVITY, Constants.GENERAL_ERROR_RESPONSE);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            userCallback.done(Constants.FAILED_POST);
        }
    }

    public String performPostCall(String requestURL, HashMap<String, String> postDataParams, boolean... params) {
        URL url;
        String response = "";

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(30000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod("POST");

            // add this when calling java service
            if (params.length > 0) {
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setUseCaches(false);
                conn.setRequestProperty("Connection", "Keep-Alive");
            }

            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            int resultConnection = conn.getResponseCode();
            if (resultConnection == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                return "{ \"Response\" : \"FAILED\", \"Reason\" : Error: " + resultConnection + "\" }";
            }
        } catch (Exception e) {
            Logger.Log(Constants.CONNECTION_ACTIVITY, e.getMessage());
            return "{ \"Response\" : \"FAILED\", \"Reason\" : " + e.getMessage() + "\" }";
        }

        return response;
    }

    //region JSON Methods
    private String getPostDataString(HashMap<String, String> params) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, String> entry : params.entrySet()) {
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

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
    //endregion
}