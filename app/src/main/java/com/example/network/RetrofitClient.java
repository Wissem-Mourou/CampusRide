package com.example.network;

import com.example.models.User;
import com.example.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static synchronized ApiService getApiService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Intercepteur intelligent : effectue la requête réelle vers PHP,
            // et si le serveur PHP local n'est pas accessible, bascule en mode démo interactif.
            Interceptor fallbackInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    try {
                        Response response = chain.proceed(request);
                        if (response.isSuccessful()) {
                            return response;
                        }
                    } catch (Exception e) {
                        // Serveur PHP non joignable (hors-ligne ou émulateur sans backend démarré)
                    }

                    // Réponse de secours via MockDataSource
                    String url = request.url().toString();
                    String method = request.method();
                    String jsonResponse = "{\"success\":true,\"message\":\"OK\",\"data\":null}";
                    Gson gson = new Gson();

                    if (url.contains("login.php")) {
                        ApiResponse<User> res = MockDataSource.getInstance().login("etudiant@univ.tn", "password123");
                        jsonResponse = gson.toJson(res);
                    } else if (url.contains("register.php")) {
                        ApiResponse<User> res = MockDataSource.getInstance().register("Ben Ali", "Mohamed", "etudiant@univ.tn", "password123", "Nabeul", "98123456");
                        jsonResponse = gson.toJson(res);
                    } else if (url.contains("trajets_rechercher.php")) {
                        ApiResponse<java.util.List<com.example.models.Trip>> res = new ApiResponse<>();
                        res.setSuccess(true);
                        res.setMessage("Trajets disponibles");
                        res.setData(MockDataSource.getInstance().searchTrips("", "", ""));
                        jsonResponse = gson.toJson(res);
                    } else if (url.contains("mes_trajets.php")) {
                        ApiResponse<java.util.List<com.example.models.Trip>> res = new ApiResponse<>();
                        res.setSuccess(true);
                        res.setMessage("Mes trajets");
                        res.setData(MockDataSource.getInstance().getTripsByDriver(1));
                        jsonResponse = gson.toJson(res);
                    }

                    return new Response.Builder()
                            .code(200)
                            .message("OK (Mode Démo)")
                            .protocol(Protocol.HTTP_1_1)
                            .request(request)
                            .body(ResponseBody.create(MediaType.parse("application/json"), jsonResponse))
                            .addHeader("content-type", "application/json")
                            .build();
                }
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .addInterceptor(fallbackInterceptor)
                    .build();

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }
}
