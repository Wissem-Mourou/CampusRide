package com.example.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.R;
import com.example.activities.MainActivity;
import com.example.models.Trip;
import com.example.models.User;
import com.example.network.ApiResponse;
import com.example.network.RetrofitClient;
import com.example.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishFragment extends Fragment {

    private TextInputEditText etDepart, etArrivee, etDate, etHeure, etPlaces, etPrix;
    private MaterialButton btnSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_publish, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDepart = view.findViewById(R.id.et_pub_depart);
        etArrivee = view.findViewById(R.id.et_pub_arrivee);
        etDate = view.findViewById(R.id.et_pub_date);
        etHeure = view.findViewById(R.id.et_pub_heure);
        etPlaces = view.findViewById(R.id.et_pub_places);
        etPrix = view.findViewById(R.id.et_pub_prix);
        btnSubmit = view.findViewById(R.id.btn_submit_publish);

        btnSubmit.setOnClickListener(v -> submitPublish());
    }

    private void submitPublish() {
        String depart = getText(etDepart);
        String arrivee = getText(etArrivee);
        String date = getText(etDate);
        String heure = getText(etHeure);
        String placesStr = getText(etPlaces);
        String prixStr = getText(etPrix);

        if (TextUtils.isEmpty(depart) || TextUtils.isEmpty(arrivee) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(heure) || TextUtils.isEmpty(placesStr) || TextUtils.isEmpty(prixStr)) {
            Toast.makeText(requireContext(), "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        int places;
        double prix;
        try {
            places = Integer.parseInt(placesStr);
            prix = Double.parseDouble(prixStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Places ou prix invalide", Toast.LENGTH_SHORT).show();
            return;
        }

        if (places <= 0) {
            Toast.makeText(requireContext(), "Le nombre de places doit être supérieur à 0", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = SessionManager.getInstance(requireContext()).getUser();
        int userId = user != null ? user.getId() : 1;

        btnSubmit.setEnabled(false);
        RetrofitClient.getApiService().publierTrajet(userId, depart, arrivee, date, heure, places, prix)
                .enqueue(new Callback<ApiResponse<Trip>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Trip>> call, @NonNull Response<ApiResponse<Trip>> response) {
                        if (!isAdded()) return;
                        btnSubmit.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Trajet publié avec succès !", Toast.LENGTH_LONG).show();
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).selectTab(R.id.nav_search);
                            }
                        } else {
                            Toast.makeText(requireContext(), "Erreur lors de la publication", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Trip>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btnSubmit.setEnabled(true);
                        Toast.makeText(requireContext(), "Erreur réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getText(TextInputEditText et) {
        return et != null && et.getText() != null ? et.getText().toString().trim() : "";
    }
}
