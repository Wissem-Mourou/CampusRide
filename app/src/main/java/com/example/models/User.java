package com.example.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class User implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("nom")
    private String nom;

    @SerializedName("prenom")
    private String prenom;

    @SerializedName("email_universitaire")
    private String emailUniversitaire;

    @SerializedName("mot_de_passe")
    private String motDePasse;

    @SerializedName("ville")
    private String ville;

    @SerializedName("telephone")
    private String telephone;

    @SerializedName("note_moyenne")
    private double noteMoyenne;

    public User() {
    }

    public User(int id, String nom, String prenom, String emailUniversitaire, String ville, String telephone, double noteMoyenne) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.emailUniversitaire = emailUniversitaire;
        this.ville = ville;
        this.telephone = telephone;
        this.noteMoyenne = noteMoyenne;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNomComplet() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }

    public String getEmailUniversitaire() {
        return emailUniversitaire;
    }

    public void setEmailUniversitaire(String emailUniversitaire) {
        this.emailUniversitaire = emailUniversitaire;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }
}
