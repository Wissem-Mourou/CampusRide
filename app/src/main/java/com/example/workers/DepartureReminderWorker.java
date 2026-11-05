package com.example.workers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DepartureReminderWorker extends Worker {

    public DepartureReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String trajet = getInputData().getString("trajet_titre");
        String heure = getInputData().getString("heure_depart");

        if (trajet == null) trajet = "Votre trajet universitaire";
        if (heure == null) heure = "bientôt";

        NotificationHelper.showNotification(
                getApplicationContext(),
                2001,
                "Rappel de départ CampusRide",
                "Votre trajet " + trajet + " part dans 1 heure (à " + heure + "). Soyez à l'heure !"
        );

        return Result.success();
    }
}
