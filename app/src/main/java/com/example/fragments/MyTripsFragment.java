package com.example.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.adapters.RequestAdapter;
import com.example.adapters.TripAdapter;
import com.example.models.Request;
import com.example.models.Trip;
import com.example.models.User;
import com.example.network.ApiResponse;
import com.example.network.RetrofitClient;
import com.example.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTripsFragment extends Fragment {

    private MaterialButtonToggleGroup toggleRole;
    private MaterialButton btnToggleConducteur, btnTogglePassager;
    private RecyclerView rvMyTrips;
    private LinearLayout layoutEmpty;
    private TripAdapter tripAdapter;
    private RequestAdapter requestAdapter;
    private boolean isDriverSelected = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_trips, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toggleRole = view.findViewById(R.id.toggle_role);
        btnToggleConducteur = view.findViewById(R.id.btn_toggle_conducteur);
        btnTogglePassager = view.findViewById(R.id.btn_toggle_passager);
        rvMyTrips = view.findViewById(R.id.rv_my_trips);
        layoutEmpty = view.findViewById(R.id.layout_my_trips_empty);

        rvMyTrips.setLayoutManager(new LinearLayoutManager(requireContext()));

        tripAdapter = new TripAdapter(trip -> {
            Toast.makeText(requireContext(), "Trajet: " + trip.getVilleDepart() + " -> " + trip.getVilleArrivee(), Toast.LENGTH_SHORT).show();
        });

        requestAdapter = new RequestAdapter(true, new RequestAdapter.OnRequestActionListener() {
            @Override
            public void onAccept(Request request) {
                answerRequest(request.getId(), "accepter");
            }

            @Override
            public void onReject(Request request) {
                answerRequest(request.getId(), "refuser");
            }
        });

        toggleRole.check(R.id.btn_toggle_conducteur);

        toggleRole.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                isDriverSelected = (checkedId == R.id.btn_toggle_conducteur);
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        User user = SessionManager.getInstance(requireContext()).getUser();
        int userId = user != null ? user.getId() : 1;

        if (isDriverSelected) {
            rvMyTrips.setAdapter(tripAdapter);
            RetrofitClient.getApiService().getMesTrajetsConducteur(userId, "conducteur")
                    .enqueue(new Callback<ApiResponse<List<Trip>>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<List<Trip>>> call, @NonNull Response<ApiResponse<List<Trip>>> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                List<Trip> trips = response.body().getData();
                                tripAdapter.setTrips(trips);
                                rvMyTrips.setVisibility(trips.isEmpty() ? View.GONE : View.VISIBLE);
                                layoutEmpty.setVisibility(trips.isEmpty() ? View.VISIBLE : View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<List<Trip>>> call, @NonNull Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            rvMyTrips.setAdapter(requestAdapter);
            RetrofitClient.getApiService().getMesTrajetsPassager(userId, "passager")
                    .enqueue(new Callback<ApiResponse<List<Request>>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<List<Request>>> call, @NonNull Response<ApiResponse<List<Request>>> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                List<Request> requests = response.body().getData();
                                requestAdapter.setRequests(requests, false);
                                rvMyTrips.setVisibility(requests.isEmpty() ? View.GONE : View.VISIBLE);
                                layoutEmpty.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<List<Request>>> call, @NonNull Throwable t) {
                            if (!isAdded()) return;
                            Toast.makeText(requireContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void answerRequest(int requestId, String action) {
        RetrofitClient.getApiService().repondreDemande(requestId, action)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Demande " + action + "e !", Toast.LENGTH_SHORT).show();
                        loadData();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
