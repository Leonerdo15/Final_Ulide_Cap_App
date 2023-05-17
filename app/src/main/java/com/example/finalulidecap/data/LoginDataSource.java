package com.example.finalulidecap.data;

import android.util.Log;

import com.example.finalulidecap.data.model.LoggedInUser;
import com.example.finalulidecap.downloaders.JSONObjDownloader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private String id;
    private JSONObject objLogin;
    public static int ID = 1;


    public Result<LoggedInUser> login(String username, String password) {

        try {
            getJson(username, password);
            if (objLogin != null){
                Log.e("id", ""+id);
                Log.e("username", ""+username);
                LoggedInUser user =
                        new LoggedInUser(
                                id,
                                ""+username);
                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Error logging in"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }


    public void getJson(String username, String password){
        JSONObjDownloader task = new JSONObjDownloader();
        String url = "http://ulideparty.ddns.net:8080/api/user/login/email/"+ username + "/pass/" + password;
//        url = "/user/login/email/"+username+"/pass/" + password;
        url = "http://192.168.1.67:8080/api/user/login/email/leonardo@gmail.com/pass/123456";
        url = "http://ulideparty.ddns.net:8080/api/user/login/email/leonardo@gmail.com/pass/123456";
        Log.e("URL", url);
        try {
            objLogin = task.execute(url).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            objLogin = null;
        }

        Log.e("Login", ""+objLogin);

        try {
            id = objLogin.getString("us_id");
            Log.e("id", id);

            ID = Integer.parseInt(id);
            Log.e("testeeeeeeeeeeeeeeeeeeeeee", ""+ID);
            Log.e("ID do loginDataSource", ""+ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}