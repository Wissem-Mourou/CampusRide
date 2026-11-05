package com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.R;
import com.example.models.Request;
import com.example.models.Trip;
import com.example.models.User;
import com.example.network.ApiResponse;
import com.example.network.RetrofitClient;
import com.example.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripDetailFragment extends Fragment {

    private static final String ARG_TRIP = "arg_trip";
    private Trip trip;

    private TextView tvItineraire, tvDateHeure, tvPlaces, tvPrix, tvConducteur, tvNote;
    private MaterialButton btnReserverPlace;
    private ImageButton btnBack;

    public static TripDetailFragment newInstance(Trip trip) {
        TripDetailFragment fragment = new TripDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TRIP, trip);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            trip = (Trip) getArguments().getSerializable(ARG_TRIP);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvItineraire = view.findViewById(R.id.tv_detail_itineraire);
        tvDateHeure = view.findViewById(R.id.tv_detail_date_heure);
        tvPlaces = view.findViewById(R.id.tv_detail_places);
        tvPrix = view.findViewById(R.id.tv_detail_prix);
        tvConducteur = view.findViewById(R.id.tv_detail_conducteur);
        tvNote = view.findViewById(R.id.tv_detail_note);
        btnReserverPlace = view.findViewById(R.id.btn_reserver_place);
        btnBack = view.findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (trip != null) {
            tvItineraire.setText(trip.getVilleDepart() + " ➔ " + trip.getVilleArrivee());
            tvDateHeure.setText(trip.getDateDepart() + " • " + trip.getHeureDepart());
            tvPlaces.setText(trip.getPlacesRestantes() + " / " + trip.getPlacesTotales() + " places");
            tvPrix.setText(String.format(Locale.getDefault(), "%.2f TND", trip.getPrix()));

            String nomCond = trip.getConducteurNom() != null ? trip.getConducteurNom() : "Conducteur étudiant";
            tvConducteur.setText(nomCond);
            double note = trip.getConducteurNote() > 0 ? trip.getConducteurNote() : 5.0;
            tvNote.setText("Note moyenne : " + String.format(Locale.getDefault(), "%.1f ★", note));

            User currentUser = SessionManager.getInstance(requireContext()).getUser();

            // Règle métier : Le conducteur ne peut pas réserver son propre trajet
            if (currentUser != null && currentUser.getId() == trip.getIdConducteur()) {
                btnReserverPlace.setEnabled(false);
                btnReserverPlace.setText("Vous êtes le conducteur de ce trajet");
                btnReserverPlace.setBackgroundColor(getResources().getColor(R.color.text_hint));
            } else if (trip.getPlacesRestantes() <= 0) {
                btnReserverPlace.setEnabled(false);
                btnReserverPlace.setText("Ce trajet est complet");
                btnReserverPlace.setBackgroundColor(getResources().getColor(R.color.error_red));
            } else {
                btnReserverPlace.setOnClickListener(v -> demanderPlace(currentUser));
            }
        }
    }

    private void demanderPlace(User user) {
        if (trip == null) return;
        int passagerId = user != null ? user.getId() : 2;

        btnReserverPlace.setEnabled(false);
        btnReserverPlace.setText("Envoi de la demande…");

        RetrofitClient.getApiService().envoyerDemande(trip.getId(), passagerId)
                .enqueue(new Callback<ApiResponse<Request>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Request>> call, @NonNull Response<ApiResponse<Request>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Demande de place envoyée au conducteur !", Toast.LENGTH_LONG).show();
                            btnReserverPlace.setText("Demande envoyée (En attente)");
                        } else {
                            btnReserverPlace.setEnabled(true);
                            btnReserverPlace.setText("Demander une place");
                            String msg = response.body() != null && response.body().getMessage() != null
                                    ? response.body().getMessage()
                                    : "Erreur lors de l'envoi de la demande";
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Request>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btnReserverPlace.setEnabled(true);
                        btnReserverPlace.setText("Demander une place");
                        Toast.makeText(requireContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
