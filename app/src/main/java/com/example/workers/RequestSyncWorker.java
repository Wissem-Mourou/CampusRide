package com.example.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.models.Request;
import com.example.models.User;
import com.example.network.ApiResponse;
import com.example.network.RetrofitClient;
import com.example.utils.SessionManager;
import java.util.List;
import retrofit2.Response;

public class RequestSyncWorker extends Worker {

    public RequestSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        User user = SessionManager.getInstance(context).getUser();
        if (user == null) {
            return Result.success();
        }

        try {
            Response<ApiResponse<List<Request>>> response = RetrofitClient.getApiService()
                    .getDemandesEnAttente(user.getId())
                    .execute();

            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                List<Request> pending = response.body().getData();
                if (!pending.isEmpty()) {
                    NotificationHelper.showNotification(
                            context,
                            1001,
                            "Nouvelle demande de covoiturage !",
                            "Vous avez " + pending.size() + " demande(s) en attente pour vos trajets CampusRide."
                    );
                }
            }
        } catch (Exception ignored) {
        }

        return Result.success();
    }
}
