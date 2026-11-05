package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.models.Trip;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private List<Trip> trips = new ArrayList<>();
    private final OnTripClickListener listener;

    public TripAdapter(OnTripClickListener listener) {
        this.listener = listener;
    }

    public void setTrips(List<Trip> newTrips) {
        this.trips = newTrips != null ? newTrips : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip, listener);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDepart, tvArrivee, tvPrix, tvDateHeure, tvPlaces, tvConducteur;
        private final MaterialButton btnReserver;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepart = itemView.findViewById(R.id.tv_depart);
            tvArrivee = itemView.findViewById(R.id.tv_arrivee);
            tvPrix = itemView.findViewById(R.id.tv_prix);
            tvDateHeure = itemView.findViewById(R.id.tv_date_heure);
            tvPlaces = itemView.findViewById(R.id.tv_places);
            tvConducteur = itemView.findViewById(R.id.tv_conducteur);
            btnReserver = itemView.findViewById(R.id.btn_reserver);
        }

        public void bind(Trip trip, OnTripClickListener listener) {
            tvDepart.setText(trip.getVilleDepart());
            tvArrivee.setText(trip.getVilleArrivee());
            tvPrix.setText(String.format(Locale.getDefault(), "%.2f TND", trip.getPrix()));
            tvDateHeure.setText(trip.getDateDepart() + " à " + trip.getHeureDepart());

            int rest = trip.getPlacesRestantes();
            if (rest > 0) {
                tvPlaces.setText(rest + (rest > 1 ? " places restantes" : " place restante"));
                tvPlaces.setTextColor(itemView.getContext().getResources().getColor(R.color.accent_green));
            } else {
                tvPlaces.setText("Complet");
                tvPlaces.setTextColor(itemView.getContext().getResources().getColor(R.color.error_red));
            }

            String conducteur = trip.getConducteurNom();
            if (conducteur == null || conducteur.isEmpty()) conducteur = "Conducteur étudiant";
            double note = trip.getConducteurNote() > 0 ? trip.getConducteurNote() : 5.0;
            tvConducteur.setText(conducteur + " (" + String.format(Locale.getDefault(), "%.1f", note) + " ★)");

            View.OnClickListener clickListener = v -> {
                if (listener != null) listener.onTripClick(trip);
            };

            itemView.setOnClickListener(clickListener);
            btnReserver.setOnClickListener(clickListener);
        }
    }
}
