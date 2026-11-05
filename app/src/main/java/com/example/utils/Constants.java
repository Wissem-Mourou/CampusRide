package com.example.utils;

public class Constants {
    // URL de base de l'API PHP (10.0.2.2 pointe vers l'hôte local depuis l'émulateur Android)
    // Remplacer par l'IP de votre machine locale si test sur appareil physique (ex: 192.168.1.50)
    public static final String BASE_URL = "http://10.0.2.2/campusride/api/";

    // Préférences partagées
    public static final String PREF_NAME = "CampusRidePrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NOM = "user_nom";
    public static final String KEY_USER_PRENOM = "user_prenom";
    public static final String KEY_USER_EMAIL = "user_email";
    public static final String KEY_USER_VILLE = "user_ville";
    public static final String KEY_USER_TELEPHONE = "user_telephone";
    public static final String KEY_USER_NOTE = "user_note";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    // Domaines universitaires autorisés pour vérification
    public static final String[] AUTHORIZED_DOMAINS = {
            "univ.tn",
            "rnu.tn",
            "isetn.rnu.tn",
            "enit.utm.tn",
            "insat.u-carthage.tn",
            "essths.u-sousse.tn",
            "fsegt.rnu.tn",
            "isg.rnu.tn",
            "ensi.uma.tn",
            "fst.rnu.tn",
            "edu.tn"
    };

    // Villes et campus populaires pour autocomplétion
    public static final String[] POPULAR_CITIES = {
            "Nabeul",
            "ISET Nabeul",
            "Tunis",
            "Hammamet",
            "Sousse",
            "Monastir",
            "Bizerte",
            "Sfax",
            "Ariana",
            "Kélibia",
            "Grombalia"
    };
}
