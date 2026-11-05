package com.example.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.R;
import com.example.models.Request;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    public interface OnRequestActionListener {
        void onAccept(Request request);
        void onReject(Request request);
    }

    private List<Request> requests = new ArrayList<>();
    private boolean isDriverView = false;
    private final OnRequestActionListener listener;

    public RequestAdapter(boolean isDriverView, OnRequestActionListener listener) {
        this.isDriverView = isDriverView;
        this.listener = listener;
    }

    public void setRequests(List<Request> newRequests, boolean isDriverView) {
        this.requests = newRequests != null ? newRequests : new ArrayList<>();
        this.isDriverView = isDriverView;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requests.get(position);
        holder.bind(request, isDriverView, listener);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTrajet, tvStatut, tvInfo, tvDate;
        private final LinearLayout layoutDriverActions;
        private final MaterialButton btnRefuser, btnAccepter;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrajet = itemView.findViewById(R.id.tv_req_trajet);
            tvStatut = itemView.findViewById(R.id.tv_req_statut);
            tvInfo = itemView.findViewById(R.id.tv_req_info);
            tvDate = itemView.findViewById(R.id.tv_req_date);
            layoutDriverActions = itemView.findViewById(R.id.layout_driver_actions);
            btnRefuser = itemView.findViewById(R.id.btn_refuser);
            btnAccepter = itemView.findViewById(R.id.btn_accepter);
        }

        public void bind(Request request, boolean isDriver, OnRequestActionListener listener) {
            tvTrajet.setText(request.getVilleDepart() + " ➔ " + request.getVilleArrivee());

            String statut = request.getStatut();
            if ("acceptee".equalsIgnoreCase(statut)) {
                tvStatut.setText("Acceptée ✓");
                tvStatut.setTextColor(itemView.getContext().getResources().getColor(R.color.accent_green));
            } else if ("refusee".equalsIgnoreCase(statut)) {
                tvStatut.setText("Refusée ✕");
                tvStatut.setTextColor(itemView.getContext().getResources().getColor(R.color.error_red));
            } else {
                tvStatut.setText("En attente ⏳");
                tvStatut.setTextColor(itemView.getContext().getResources().getColor(R.color.warning_orange));
            }

            if (isDriver) {
                String passager = request.getPassagerNom() != null ? request.getPassagerNom() : "Passager";
                String tel = request.getPassagerTelephone() != null ? " • Tél: " + request.getPassagerTelephone() : "";
                tvInfo.setText("Passager : " + passager + tel);
            } else {
                tvInfo.setText("Ma réservation de covoiturage");
            }

            tvDate.setText("Départ: " + request.getDateDepart() + " à " + request.getHeureDepart() + " • " +
                    String.format(Locale.getDefault(), "%.2f TND", request.getPrix()));

            // Afficher les boutons accepter/refuser pour le conducteur si statut en_attente
            if (isDriver && "en_attente".equalsIgnoreCase(statut)) {
                layoutDriverActions.setVisibility(View.VISIBLE);
                btnAccepter.setOnClickListener(v -> {
                    if (listener != null) listener.onAccept(request);
                });
                btnRefuser.setOnClickListener(v -> {
                    if (listener != null) listener.onReject(request);
                });
            } else {
                layoutDriverActions.setVisibility(View.GONE);
            }
        }
    }
}
