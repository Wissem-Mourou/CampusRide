package com.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.models.User;

public class SessionManager {
    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    private SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveUser(User user) {
        if (user == null) return;
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putInt(Constants.KEY_USER_ID, user.getId());
        editor.putString(Constants.KEY_USER_NOM, user.getNom());
        editor.putString(Constants.KEY_USER_PRENOM, user.getPrenom());
        editor.putString(Constants.KEY_USER_EMAIL, user.getEmailUniversitaire());
        editor.putString(Constants.KEY_USER_VILLE, user.getVille());
        editor.putString(Constants.KEY_USER_TELEPHONE, user.getTelephone());
        editor.putFloat(Constants.KEY_USER_NOTE, (float) user.getNoteMoyenne());
        editor.apply();
    }

    public User getUser() {
        if (!isLoggedIn()) return null;
        User user = new User();
        user.setId(prefs.getInt(Constants.KEY_USER_ID, 0));
        user.setNom(prefs.getString(Constants.KEY_USER_NOM, ""));
        user.setPrenom(prefs.getString(Constants.KEY_USER_PRENOM, ""));
        user.setEmailUniversitaire(prefs.getString(Constants.KEY_USER_EMAIL, ""));
        user.setVille(prefs.getString(Constants.KEY_USER_VILLE, ""));
        user.setTelephone(prefs.getString(Constants.KEY_USER_TELEPHONE, ""));
        user.setNoteMoyenne(prefs.getFloat(Constants.KEY_USER_NOTE, 5.0f));
        return user;
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
