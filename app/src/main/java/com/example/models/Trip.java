package com.example.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Trip implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("id_conducteur")
    private int idConducteur;

    @SerializedName("conducteur_nom")
    private String conducteurNom;

    @SerializedName("conducteur_telephone")
    private String conducteurTelephone;

    @SerializedName("conducteur_note")
    private double conducteurNote;

    @SerializedName("ville_depart")
    private String villeDepart;

    @SerializedName("ville_arrivee")
    private String villeArrivee;

    @SerializedName("date_depart")
    private String dateDepart;

    @SerializedName("heure_depart")
    private String heureDepart;

    @SerializedName("places_totales")
    private int placesTotales;

    @SerializedName("places_restantes")
    private int placesRestantes;

    @SerializedName("prix")
    private double prix;

    @SerializedName("statut")
    private String statut; // "disponible", "complet", "annule"

    public Trip() {
    }

    public Trip(int id, int idConducteur, String conducteurNom, double conducteurNote,
                String villeDepart, String villeArrivee, String dateDepart,
                String heureDepart, int placesTotales, int placesRestantes, double prix, String statut) {
        this.id = id;
        this.idConducteur = idConducteur;
        this.conducteurNom = conducteurNom;
        this.conducteurNote = conducteurNote;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.placesTotales = placesTotales;
        this.placesRestantes = placesRestantes;
        this.prix = prix;
        this.statut = statut;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdConducteur() {
        return idConducteur;
    }

    public void setIdConducteur(int idConducteur) {
        this.idConducteur = idConducteur;
    }

    public String getConducteurNom() {
        return conducteurNom;
    }

    public void setConducteurNom(String conducteurNom) {
        this.conducteurNom = conducteurNom;
    }

    public String getConducteurTelephone() {
        return conducteurTelephone;
    }

    public void setConducteurTelephone(String conducteurTelephone) {
        this.conducteurTelephone = conducteurTelephone;
    }

    public double getConducteurNote() {
        return conducteurNote;
    }

    public void setConducteurNote(double conducteurNote) {
        this.conducteurNote = conducteurNote;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public String getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(String dateDepart) {
        this.dateDepart = dateDepart;
    }

    public String getHeureDepart() {
        return heureDepart;
    }

    public void setHeureDepart(String heureDepart) {
        this.heureDepart = heureDepart;
    }

    public int getPlacesTotales() {
        return placesTotales;
    }

    public void setPlacesTotales(int placesTotales) {
        this.placesTotales = placesTotales;
    }

    public int getPlacesRestantes() {
        return placesRestantes;
    }

    public void setPlacesRestantes(int placesRestantes) {
        this.placesRestantes = placesRestantes;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}
