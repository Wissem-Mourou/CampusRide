package com.example.network;

import com.example.models.Request;
import com.example.models.Trip;
import com.example.models.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("register.php")
    Call<ApiResponse<User>> register(
            @Field("nom") String nom,
            @Field("prenom") String prenom,
            @Field("email_universitaire") String email,
            @Field("mot_de_passe") String motDePasse,
            @Field("ville") String ville,
            @Field("telephone") String telephone
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<ApiResponse<User>> login(
            @Field("email_universitaire") String email,
            @Field("mot_de_passe") String motDePasse
    );

    @GET("trajets_rechercher.php")
    Call<ApiResponse<List<Trip>>> rechercherTrajets(
            @Query("ville_depart") String villeDepart,
            @Query("ville_arrivee") String villeArrivee,
            @Query("date_depart") String dateDepart
    );

    @FormUrlEncoded
    @POST("trajet_publier.php")
    Call<ApiResponse<Trip>> publierTrajet(
            @Field("id_conducteur") int idConducteur,
            @Field("ville_depart") String villeDepart,
            @Field("ville_arrivee") String villeArrivee,
            @Field("date_depart") String dateDepart,
            @Field("heure_depart") String heureDepart,
            @Field("places_totales") int placesTotales,
            @Field("prix") double prix
    );

    @GET("trajet.php")
    Call<ApiResponse<Trip>> getTrajetDetails(
            @Query("id") int idTrajet
    );

    @FormUrlEncoded
    @POST("demande_envoyer.php")
    Call<ApiResponse<Request>> envoyerDemande(
            @Field("id_trajet") int idTrajet,
            @Field("id_passager") int idPassager
    );

    @FormUrlEncoded
    @POST("demande_repondre.php")
    Call<ApiResponse<Void>> repondreDemande(
            @Field("id_demande") int idDemande,
            @Field("action") String action // "accepter" ou "refuser"
    );

    @GET("mes_trajets.php")
    Call<ApiResponse<List<Trip>>> getMesTrajetsConducteur(
            @Query("id_utilisateur") int idUtilisateur,
            @Query("role") String role // "conducteur"
    );

    @GET("mes_trajets.php")
    Call<ApiResponse<List<Request>>> getMesTrajetsPassager(
            @Query("id_utilisateur") int idUtilisateur,
            @Query("role") String role // "passager"
    );

    @GET("demandes_en_attente.php")
    Call<ApiResponse<List<Request>>> getDemandesEnAttente(
            @Query("id_conducteur") int idConducteur
    );

    @FormUrlEncoded
    @POST("trajet_annuler.php")
    Call<ApiResponse<Void>> annulerTrajet(
            @Field("id_trajet") int idTrajet,
            @Field("id_conducteur") int idConducteur
    );
}
