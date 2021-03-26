package com.jfloydconsult.matatu.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }


    public void setUserId(String userId) {
        prefs.edit().putString("userId", userId).apply();
    }
    public String getUserId() {
        return prefs.getString("userId","");
    }

    public void setUser(String user) {
        prefs.edit().putString("user", user).apply();
    }
    public String getUser() {
        return prefs.getString("user","");
    }

    public void setRandomKey(String randomKey) {
        prefs.edit().putString("randomKey", randomKey).apply();
    }
    public String getRandomKey() {
        return prefs.getString("randomKey","");
    }

    public void setToken(String token) {
        prefs.edit().putString("token", token).apply();
    }

    public String getToken() {
        return prefs.getString("token","");
    }

    public void setExpiryDate(String authorizationToken) {
        prefs.edit().putString("expiryDate", authorizationToken).apply();
    }

    public String getExpiryDate() {
        return prefs.getString("expiryDate","");
    }


    public void clearSession(){
        prefs.edit().clear().apply();
    }

    public void clearUserCredential(){
        prefs.edit().remove("user").apply();
        prefs.edit().remove("userId").apply();
    }
}