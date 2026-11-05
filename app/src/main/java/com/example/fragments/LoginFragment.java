package com.example.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.R;
import com.example.activities.LoginActivity;
import com.example.models.User;
import com.example.network.ApiResponse;
import com.example.network.RetrofitClient;
import com.example.utils.Constants;
import com.example.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvGoRegister;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilEmail = view.findViewById(R.id.til_email);
        tilPassword = view.findViewById(R.id.til_password);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvGoRegister = view.findViewById(R.id.tv_go_register);
        progressBar = view.findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(v -> performLogin());

        tvGoRegister.setOnClickListener(v -> {
            if (getActivity() instanceof LoginActivity) {
                ((LoginActivity) getActivity()).showRegisterFragment();
            }
        });
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        tilEmail.setError(null);
        tilPassword.setError(null);

        // Validation email
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError(getString(R.string.err_empty_fields));
            etEmail.requestFocus();
            return;
        }

        if (!isValidUniversityEmail(email)) {
            tilEmail.setError(getString(R.string.err_invalid_email));
            etEmail.requestFocus();
            return;
        }

        // Validation mot de passe
        if (TextUtils.isEmpty(password)) {
            tilPassword.setError(getString(R.string.err_empty_fields));
            etPassword.requestFocus();
            return;
        }

        setLoading(true);

        // Appel API Retrofit vers le script PHP login.php
        RetrofitClient.getApiService().login(email, password).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                if (!isAdded()) return;
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        SessionManager.getInstance(requireContext()).saveUser(user);
                        Toast.makeText(requireContext(), "Bienvenue " + user.getPrenom() + " !", Toast.LENGTH_SHORT).show();

                        if (getActivity() instanceof LoginActivity) {
                            ((LoginActivity) getActivity()).openMainActivity();
                        }
                    } else {
                        String msg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Échec de connexion";
                        tilPassword.setError(msg);
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Erreur serveur (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<User>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                setLoading(false);
                Toast.makeText(requireContext(), getString(R.string.err_network) + " : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean isValidUniversityEmail(String email) {
        if (!email.contains("@")) return false;
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        // Vérifie les domaines universitaires autorisés (.tn, .rnu.tn, .edu)
        if (domain.endsWith(".tn") || domain.endsWith(".edu")) {
            return true;
        }
        for (String authDomain : Constants.AUTHORIZED_DOMAINS) {
            if (domain.equalsIgnoreCase(authDomain)) return true;
        }
        return false;
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!loading);
            btnLogin.setText(loading ? "Connexion en cours…" : getString(R.string.btn_login));
        }
    }
}
