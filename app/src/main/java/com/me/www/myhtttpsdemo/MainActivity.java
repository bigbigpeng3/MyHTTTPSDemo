package com.me.www.myhtttpsdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.tv_content);

        initData();

    }

    private void initData() {
        System.out.println("initData...............");
        OkHttpClient client = OkHttpUtil.getInstance().getOkHttpClient();

        final Request request = new Request.Builder()
                .url("https://api.bailitop.edu/test.php")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("onFailure...............");
                System.out.println("IOException..............." + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                System.out.println("onResponse...............");
                if (response != null) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                textView.setText(response.body().string());
                                System.out.println(response.body().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });

    }
}
