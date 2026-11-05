package com.example.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Request implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("id_trajet")
    private int idTrajet;

    @SerializedName("id_passager")
    private int idPassager;

    @SerializedName("passager_nom")
    private String passagerNom;

    @SerializedName("passager_email")
    private String passagerEmail;

    @SerializedName("passager_telephone")
    private String passagerTelephone;

    @SerializedName("passager_note")
    private double passagerNote;

    @SerializedName("statut")
    private String statut; // "en_attente", "acceptee", "refusee", "annulee"

    @SerializedName("date_demande")
    private String dateDemande;

    @SerializedName("ville_depart")
    private String villeDepart;

    @SerializedName("ville_arrivee")
    private String villeArrivee;

    @SerializedName("date_depart")
    private String dateDepart;

    @SerializedName("heure_depart")
    private String heureDepart;

    @SerializedName("prix")
    private double prix;

    public Request() {
    }

    public Request(int id, int idTrajet, int idPassager, String passagerNom, String passagerTelephone,
                   String statut, String dateDemande, String villeDepart, String villeArrivee,
                   String dateDepart, String heureDepart, double prix) {
        this.id = id;
        this.idTrajet = idTrajet;
        this.idPassager = idPassager;
        this.passagerNom = passagerNom;
        this.passagerTelephone = passagerTelephone;
        this.statut = statut;
        this.dateDemande = dateDemande;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateDepart = dateDepart;
        this.heureDepart = heureDepart;
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTrajet() {
        return idTrajet;
    }

    public void setIdTrajet(int idTrajet) {
        this.idTrajet = idTrajet;
    }

    public int getIdPassager() {
        return idPassager;
    }

    public void setIdPassager(int idPassager) {
        this.idPassager = idPassager;
    }

    public String getPassagerNom() {
        return passagerNom;
    }

    public void setPassagerNom(String passagerNom) {
        this.passagerNom = passagerNom;
    }

    public String getPassagerEmail() {
        return passagerEmail;
    }

    public void setPassagerEmail(String passagerEmail) {
        this.passagerEmail = passagerEmail;
    }

    public String getPassagerTelephone() {
        return passagerTelephone;
    }

    public void setPassagerTelephone(String passagerTelephone) {
        this.passagerTelephone = passagerTelephone;
    }

    public double getPassagerNote() {
        return passagerNote;
    }

    public void setPassagerNote(double passagerNote) {
        this.passagerNote = passagerNote;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(String dateDemande) {
        this.dateDemande = dateDemande;
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

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }
}
