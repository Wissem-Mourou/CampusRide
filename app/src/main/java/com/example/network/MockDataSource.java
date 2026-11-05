package com.example.network;

import com.example.models.Request;
import com.example.models.Trip;
import com.example.models.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Source de données en mémoire pour tester l'application en mode démonstration
 * lorsque le serveur PHP/MySQL local n'est pas encore démarré dans l'environnement d'exécution.
 */
public class MockDataSource {
    private static MockDataSource instance;
    private final List<User> users = new ArrayList<>();
    private final List<Trip> trips = new ArrayList<>();
    private final List<Request> requests = new ArrayList<>();
    private int nextUserId = 100;
    private int nextTripId = 200;
    private int nextRequestId = 300;

    private MockDataSource() {
        initInitialData();
    }

    public static synchronized MockDataSource getInstance() {
        if (instance == null) {
            instance = new MockDataSource();
        }
        return instance;
    }

    private void initInitialData() {
        // Utilisateurs étudiants de test
        User u1 = new User(1, "Ben Ali", "Mohamed", "mohamed.benali@isetn.rnu.tn", "Nabeul", "98123456", 4.9);
        u1.setMotDePasse("password123");
        users.add(u1);

        User u2 = new User(2, "Trabelsi", "Sarra", "sarra.trabelsi@univ.tn", "Tunis", "21456789", 4.8);
        u2.setMotDePasse("password123");
        users.add(u2);

        User u3 = new User(3, "Gharbi", "Yassine", "yassine.gharbi@enit.utm.tn", "Hammamet", "55123987", 4.7);
        u3.setMotDePasse("password123");
        users.add(u3);

        // Trajets universitaires initiaux
        trips.add(new Trip(1, 1, "Mohamed Ben Ali", 4.9, "Nabeul", "ISET Nabeul", "2026-11-06", "07:45", 3, 2, 2.5, "disponible"));
        trips.add(new Trip(2, 2, "Sarra Trabelsi", 4.8, "Tunis", "Nabeul", "2026-11-06", "08:00", 4, 3, 7.0, "disponible"));
        trips.add(new Trip(3, 1, "Mohamed Ben Ali", 4.9, "Nabeul", "Hammamet", "2026-11-06", "17:30", 3, 1, 3.0, "disponible"));
        trips.add(new Trip(4, 3, "Yassine Gharbi", 4.7, "Hammamet", "Tunis", "2026-11-07", "07:15", 4, 4, 6.5, "disponible"));
        trips.add(new Trip(5, 2, "Sarra Trabelsi", 4.8, "Tunis", "ISET Nabeul", "2026-11-07", "08:15", 3, 0, 8.0, "complet"));

        // Demandes de test
        requests.add(new Request(1, 1, 2, "Sarra Trabelsi", "21456789", "en_attente", "2026-11-05 10:00", "Nabeul", "ISET Nabeul", "2026-11-06", "07:45", 2.5));
        requests.add(new Request(2, 2, 3, "Yassine Gharbi", "55123987", "acceptee", "2026-11-05 09:30", "Tunis", "Nabeul", "2026-11-06", "08:00", 7.0));
    }

    public synchronized ApiResponse<User> login(String email, String password) {
        ApiResponse<User> response = new ApiResponse<>();
        for (User u : users) {
            if (u.getEmailUniversitaire().equalsIgnoreCase(email)) {
                if (u.getMotDePasse() == null || u.getMotDePasse().equals(password) || "password123".equals(password)) {
                    response.setSuccess(true);
                    response.setMessage("Connexion réussie");
                    response.setData(u);
                    return response;
                } else {
                    response.setSuccess(false);
                    response.setMessage("Mot de passe incorrect");
                    return response;
                }
            }
        }
        // Pour faciliter les tests d'étudiants avec n'importe quelle adresse valide
        if (email.contains("@")) {
            User newUser = new User(nextUserId++, "Étudiant", "Campus", email, "Nabeul", "98000000", 5.0);
            newUser.setMotDePasse(password);
            users.add(newUser);
            response.setSuccess(true);
            response.setMessage("Bienvenue sur CampusRide !");
            response.setData(newUser);
            return response;
        }

        response.setSuccess(false);
        response.setMessage("Utilisateur introuvable");
        return response;
    }

    public synchronized ApiResponse<User> register(String nom, String prenom, String email, String password, String ville, String tel) {
        ApiResponse<User> response = new ApiResponse<>();
        for (User u : users) {
            if (u.getEmailUniversitaire().equalsIgnoreCase(email)) {
                response.setSuccess(false);
                response.setMessage("Cet email universitaire est déjà utilisé");
                return response;
            }
        }
        User newUser = new User(nextUserId++, nom, prenom, email, ville, tel, 5.0);
        newUser.setMotDePasse(password);
        users.add(newUser);
        response.setSuccess(true);
        response.setMessage("Compte créé avec succès !");
        response.setData(newUser);
        return response;
    }

    public synchronized List<Trip> searchTrips(String depart, String arrivee, String date) {
        List<Trip> result = new ArrayList<>();
        for (Trip t : trips) {
            boolean matchDepart = (depart == null || depart.trim().isEmpty() || t.getVilleDepart().toLowerCase().contains(depart.toLowerCase().trim()));
            boolean matchArrivee = (arrivee == null || arrivee.trim().isEmpty() || t.getVilleArrivee().toLowerCase().contains(arrivee.toLowerCase().trim()));
            boolean matchDate = (date == null || date.trim().isEmpty() || t.getDateDepart().equals(date.trim()));

            if (matchDepart && matchArrivee && matchDate && !"annule".equals(t.getStatut())) {
                result.add(t);
            }
        }
        return result;
    }

    public synchronized Trip addTrip(int idConducteur, String nomConducteur, String depart, String arrivee, String date, String heure, int places, double prix) {
        Trip trip = new Trip(nextTripId++, idConducteur, nomConducteur, 5.0, depart, arrivee, date, heure, places, places, prix, "disponible");
        trips.add(0, trip);
        return trip;
    }

    public synchronized Request addRequest(int idTrajet, int idPassager, String nomPassager, String telPassager) {
        Trip trip = getTrip(idTrajet);
        if (trip == null || trip.getPlacesRestantes() <= 0) return null;
        Request req = new Request(nextRequestId++, idTrajet, idPassager, nomPassager, telPassager, "en_attente",
                "Maintenant", trip.getVilleDepart(), trip.getVilleArrivee(), trip.getDateDepart(), trip.getHeureDepart(), trip.getPrix());
        requests.add(0, req);
        return req;
    }

    public synchronized Trip getTrip(int id) {
        for (Trip t : trips) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public synchronized List<Trip> getTripsByDriver(int idConducteur) {
        List<Trip> res = new ArrayList<>();
        for (Trip t : trips) {
            if (t.getIdConducteur() == idConducteur) res.add(t);
        }
        return res;
    }

    public synchronized List<Request> getRequestsByPassenger(int idPassager) {
        List<Request> res = new ArrayList<>();
        for (Request r : requests) {
            if (r.getIdPassager() == idPassager) res.add(r);
        }
        return res;
    }

    public synchronized List<Request> getRequestsForDriver(int idConducteur) {
        List<Request> res = new ArrayList<>();
        for (Request r : requests) {
            Trip t = getTrip(r.getIdTrajet());
            if (t != null && t.getIdConducteur() == idConducteur) {
                res.add(r);
            }
        }
        return res;
    }

    public synchronized boolean answerRequest(int idDemande, String action) {
        for (Request r : requests) {
            if (r.getId() == idDemande) {
                if ("accepter".equalsIgnoreCase(action)) {
                    r.setStatut("acceptee");
                    Trip t = getTrip(r.getIdTrajet());
                    if (t != null && t.getPlacesRestantes() > 0) {
                        t.setPlacesRestantes(t.getPlacesRestantes() - 1);
                        if (t.getPlacesRestantes() == 0) {
                            t.setStatut("complet");
                        }
                    }
                } else {
                    r.setStatut("refusee");
                }
                return true;
            }
        }
        return false;
    }

    public synchronized boolean cancelTrip(int idTrajet, int idConducteur) {
        for (Trip t : trips) {
            if (t.getId() == idTrajet && t.getIdConducteur() == idConducteur) {
                t.setStatut("annule");
                return true;
            }
        }
        return false;
    }
}
