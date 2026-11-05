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
import com.example.activities.MainActivity;
import com.example.adapters.TripAdapter;
import com.example.models.Trip;
import com.example.network.ApiResponse;
import com.example.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private TextInputEditText etDepart, etArrivee, etDate;
    private MaterialButton btnSearch;
    private RecyclerView rvTrips;
    private LinearLayout layoutEmpty;
    private TripAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDepart = view.findViewById(R.id.et_depart);
        etArrivee = view.findViewById(R.id.et_arrivee);
        etDate = view.findViewById(R.id.et_date);
        btnSearch = view.findViewById(R.id.btn_search);
        rvTrips = view.findViewById(R.id.rv_trips);
        layoutEmpty = view.findViewById(R.id.layout_empty);

        rvTrips.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new TripAdapter(trip -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showTripDetail(trip);
            }
        });
        rvTrips.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> performSearch());

        // Recherche initiale
        performSearch();
    }

    private void performSearch() {
        String depart = etDepart.getText() != null ? etDepart.getText().toString().trim() : "";
        String arrivee = etArrivee.getText() != null ? etArrivee.getText().toString().trim() : "";
        String date = etDate.getText() != null ? etDate.getText().toString().trim() : "";

        RetrofitClient.getApiService().rechercherTrajets(depart, arrivee, date)
                .enqueue(new Callback<ApiResponse<List<Trip>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<Trip>>> call, @NonNull Response<ApiResponse<List<Trip>>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            List<Trip> trips = response.body().getData();
                            adapter.setTrips(trips);

                            boolean empty = trips.isEmpty();
                            rvTrips.setVisibility(empty ? View.GONE : View.VISIBLE);
                            layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                        } else {
                            adapter.setTrips(null);
                            rvTrips.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<Trip>>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
