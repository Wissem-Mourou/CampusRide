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

public class RegisterFragment extends Fragment {

    private TextInputLayout tilNom, tilPrenom, tilEmail, tilTelephone, tilVille, tilPassword, tilConfirmPassword;
    private TextInputEditText etNom, etPrenom, etEmail, etTelephone, etVille, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvGoLogin;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tilNom = view.findViewById(R.id.til_nom);
        tilPrenom = view.findViewById(R.id.til_prenom);
        tilEmail = view.findViewById(R.id.til_register_email);
        tilTelephone = view.findViewById(R.id.til_telephone);
        tilVille = view.findViewById(R.id.til_ville);
        tilPassword = view.findViewById(R.id.til_register_password);
        tilConfirmPassword = view.findViewById(R.id.til_confirm_password);

        etNom = view.findViewById(R.id.et_nom);
        etPrenom = view.findViewById(R.id.et_prenom);
        etEmail = view.findViewById(R.id.et_register_email);
        etTelephone = view.findViewById(R.id.et_telephone);
        etVille = view.findViewById(R.id.et_ville);
        etPassword = view.findViewById(R.id.et_register_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);

        btnRegister = view.findViewById(R.id.btn_register);
        tvGoLogin = view.findViewById(R.id.tv_go_login);
        progressBar = view.findViewById(R.id.progress_register);

        btnRegister.setOnClickListener(v -> performRegister());

        tvGoLogin.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void performRegister() {
        clearErrors();

        String nom = getText(etNom);
        String prenom = getText(etPrenom);
        String email = getText(etEmail);
        String telephone = getText(etTelephone);
        String ville = getText(etVille);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        // Validation
        if (TextUtils.isEmpty(prenom)) {
            tilPrenom.setError(getString(R.string.err_empty_fields));
            etPrenom.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nom)) {
            tilNom.setError(getString(R.string.err_empty_fields));
            etNom.requestFocus();
            return;
        }

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

        if (TextUtils.isEmpty(telephone) || telephone.length() < 8) {
            tilTelephone.setError(getString(R.string.err_invalid_phone));
            etTelephone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ville)) {
            tilVille.setError(getString(R.string.err_empty_fields));
            etVille.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            tilPassword.setError(getString(R.string.err_password_short));
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.err_passwords_not_match));
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);

        // Appel API Retrofit vers register.php
        RetrofitClient.getApiService().register(nom, prenom, email, password, ville, telephone)
                .enqueue(new Callback<ApiResponse<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<User>> call, @NonNull Response<ApiResponse<User>> response) {
                        if (!isAdded()) return;
                        setLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<User> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                User newUser = apiResponse.getData();
                                SessionManager.getInstance(requireContext()).saveUser(newUser);
                                Toast.makeText(requireContext(), "Compte créé avec succès ! Bienvenue " + newUser.getPrenom(), Toast.LENGTH_LONG).show();

                                if (getActivity() instanceof LoginActivity) {
                                    ((LoginActivity) getActivity()).openMainActivity();
                                }
                            } else {
                                String msg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Échec de l'inscription";
                                tilEmail.setError(msg);
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

    private void clearErrors() {
        tilNom.setError(null);
        tilPrenom.setError(null);
        tilEmail.setError(null);
        tilTelephone.setError(null);
        tilVille.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText != null && editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private boolean isValidUniversityEmail(String email) {
        if (!email.contains("@")) return false;
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

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
        if (btnRegister != null) {
            btnRegister.setEnabled(!loading);
            btnRegister.setText(loading ? "Création en cours…" : getString(R.string.btn_register));
        }
    }
}
