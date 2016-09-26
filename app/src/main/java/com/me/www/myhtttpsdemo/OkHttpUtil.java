package com.me.www.myhtttpsdemo;


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.concurrent.ExecutorService;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.OkHttpClient;

/**
 * Description :
 * Email  : bigbigpeng3@gmail.com
 * Author : peng zhang
 * Date   : 2016-3-11
 */
public class OkHttpUtil {

    private static OkHttpUtil mInstance;

    private OkHttpClient mOkHttpClient;

    private OkHttpUtil() {
        mOkHttpClient = initClient();
    }

    public static OkHttpUtil getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtil.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtil();
                }
            }
        }
        return mInstance;
    }

    public OkHttpClient getOkHttpClient(){
        if (mOkHttpClient == null){
            return mOkHttpClient;
        }

        return initClient();
    }

    public OkHttpClient initClient(){

        mOkHttpClient =  new OkHttpClient.Builder()
//                .addNetworkInterceptor(new StethoInterceptor())
//                .addInterceptor(new IdentityIntercaptor())
                .build();
//        mOkHttpClient.newBuilder().connectTimeout(15, TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().readTimeout(15,TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().writeTimeout(15,TimeUnit.SECONDS);
//        mOkHttpClient.networkInterceptors().add(new StethoInterceptor());//这里添加了Stetho调试框架
        //                .addNetworkInterceptor(new StethoInterceptor())

        return mOkHttpClient ;
    }

    public void setCertificates(InputStream... certificates)
    {
        try
        {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates)
            {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));

                try
                {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e)
                {
                }
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");

            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            trustManagerFactory.init(keyStore);
            sslContext.init
                    (
                            null,
                            trustManagerFactory.getTrustManagers(),
                            new SecureRandom()
                    );
//            mOkHttpClient.sslSocketFactory();
            mOkHttpClient.newBuilder().sslSocketFactory(sslContext.getSocketFactory());

//            sslSocketFactory(sslContext.getSocketFactory())
//            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
//            mOkHttpClient.setSslSocketFactory();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void shutDownAllRequest(){
        getInstance().getOkHttpClient().connectionPool().evictAll();
        ExecutorService executorService = getInstance().getOkHttpClient().dispatcher().executorService();
        executorService.shutdown();
    }

    /**
     * 封装的方法放在下面
     */



}