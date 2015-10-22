package davideberdin.goofing.networking;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

// My imports
import davideberdin.goofing.controllers.User;
import davideberdin.goofing.utilities.Constants;
import davideberdin.goofing.utilities.Logger;

public class Networking extends AsyncTask
{
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            HashMap<String, String> postParams = new HashMap<>();

            switch ((int)params[0]) {
                case Constants.NETWORKING_LOGIN_STATE:
                    postParams.put("Username", (String)params[1]);
                    postParams.put("Password", (String)params[2]);

                    break;
                case Constants.NETWORKING_REGISTER_STATE:

                    assert User.getUser() != null;

                    // we should have a User created at this point
                    postParams.put("Username", User.getUser().GetUsername());
                    postParams.put("Password", User.getUser().GetPassword());
                    postParams.put("Gender", User.getUser().GetGender());
                    postParams.put("Nationality", User.getUser().GetNationality());
                    postParams.put("Occupation", User.getUser().GetOccupation());

                    break;
                default:
                    Logger.Log(Constants.LOGIN_ACTIVITY_NAME, "Unknown behaviour during getJSON!");
                    break;
            }

            // perform action
            assert postParams.size() > 0;
            String result = performPostCall(Constants.SERVER_URL, postParams);
            // Handle result here
            // ....

        } catch (Exception ex) {
            Logger.Log(Constants.CONNECTION_ACTIVITY, "Error in NETWROKING!");
        }
        return null;
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
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="FAILED";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

