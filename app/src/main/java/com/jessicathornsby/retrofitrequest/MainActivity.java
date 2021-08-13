package com.jessicathornsby.retrofitrequest;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.jessicathornsby.authenticated.pojo.AuthMessage;
import com.jessicathornsby.authenticated.pojo.AuthRequest;
import com.jessicathornsby.authenticated.AuthenticatedDataClient;
import com.jessicathornsby.authenticated.GetAuthenticatedData;
import com.jessicathornsby.authenticated.pojo.SimpleResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private MyAdapter myAdapter;
    private RecyclerView myRecyclerView;
    private ExecutorService executor = Executors.newFixedThreadPool(3); // нужно для аутенификации

    private static final String AUTH_TAG = "AUTHENTICATION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ////AUTHENTICATION ROUTE

        Runnable authService = new Runnable() {
            @Override
            public void run() {
              logAuthenticationData(); // authentication demonstration
            }
        };

        executor.execute(authService);// запускаем для аутентификации

        ////AUTHENTICATION ROUTE

        GetData service = RetrofitClient.getRetrofitInstance().create(GetData.class);
        Call<List<RetroUsers>> call = service.getAllUsers();
        call.enqueue(new Callback<List<RetroUsers>>() {

            @Override
            public void onResponse(Call<List<RetroUsers>> call, Response<List<RetroUsers>> response) {
                loadDataList(response.body());
            }

            @Override
            public void onFailure(Call<List<RetroUsers>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Unable to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadDataList(List<RetroUsers> usersList) {

        myRecyclerView = findViewById(R.id.myRecyclerView);
        myAdapter = new MyAdapter(usersList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        myRecyclerView.setLayoutManager(layoutManager);
        myRecyclerView.setAdapter(myAdapter);
    }


    ////AUTHENTICATION ROUTE

    private void logAuthenticationData() {

       GetAuthenticatedData authService =  AuthenticatedDataClient.getRetrofitInstance().create(GetAuthenticatedData.class);
       Call<AuthMessage> authentionTokenCall = authService.getAuthentication(new AuthRequest("a@b.com", "test"));
       String authentionToken = "";
       try {
           AuthMessage authMessage = authentionTokenCall.execute().body();
           authentionToken = authMessage.getToken();
           Log.d(AUTH_TAG, authMessage.getMessage() + "; token= " + authentionToken);

       Call<SimpleResponse> protectedCall =  authService.getAuthenticatedRoute("Bearer " + authentionToken);
       SimpleResponse protectedMessage = protectedCall.execute().body();

       Log.d(AUTH_TAG,"We touched /protected and the result is: " + protectedMessage.getMessage());

       Call<SimpleResponse> adminCall = authService.checkAdmin("Bearer " + authentionToken);
       SimpleResponse adminMessage = adminCall.execute().body();

       Log.d(AUTH_TAG, "We touched /admin and the result is: " + adminMessage.getMessage());

       } catch (IOException e) {
           e.printStackTrace();
       }

    }

    ////AUTHENTICATION ROUTE


}
