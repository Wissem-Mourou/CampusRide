package com.example.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.R;
import com.example.activities.LoginActivity;
import com.example.models.User;
import com.example.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvRating, tvEmail, tvPhone, tvCity;
    private MaterialButton btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvName = view.findViewById(R.id.tv_profile_name);
        tvRating = view.findViewById(R.id.tv_profile_rating);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvPhone = view.findViewById(R.id.tv_profile_phone);
        tvCity = view.findViewById(R.id.tv_profile_city);
        btnLogout = view.findViewById(R.id.btn_logout);

        loadUserProfile();

        btnLogout.setOnClickListener(v -> {
            SessionManager.getInstance(requireContext()).logout();
            Toast.makeText(requireContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadUserProfile() {
        User user = SessionManager.getInstance(requireContext()).getUser();
        if (user != null) {
            tvName.setText(user.getNomComplet());
            double note = user.getNoteMoyenne() > 0 ? user.getNoteMoyenne() : 5.0;
            tvRating.setText(String.format(Locale.getDefault(), "★ %.1f • Étudiant vérifié", note));
            tvEmail.setText(user.getEmailUniversitaire());
            tvPhone.setText(user.getTelephone());
            tvCity.setText(user.getVille());
        }
    }
}
