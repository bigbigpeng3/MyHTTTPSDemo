package com.me.www.myhtttpsdemo;

import org.junit.Test;
import org.junit.runner.notification.RunListener;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


    @Test
    public void testOkhttpHTTPS(){

        OkHttpClient client = OkHttpUtil.getInstance().getOkHttpClient();
        String response = null;
        try {
            response = run(client,"https://api.bailitop.edu/test.php");

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response);
    }

    String run(OkHttpClient client, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}