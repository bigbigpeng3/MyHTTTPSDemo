package com.me.www.myhtttpsdemo;


import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okio.Buffer;

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

    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            return mOkHttpClient;
        }

        return initClient();
    }

    public OkHttpClient initClient() {

        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        mOkHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
//                .addNetworkInterceptor(new StethoInterceptor())
//                .addInterceptor(new IdentityIntercaptor())
                .build();
//        mOkHttpClient.newBuilder().connectTimeout(15, TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().readTimeout(15,TimeUnit.SECONDS);
//        mOkHttpClient.newBuilder().writeTimeout(15,TimeUnit.SECONDS);
//        mOkHttpClient.networkInterceptors().add(new StethoInterceptor());//这里添加了Stetho调试框架
        //                .addNetworkInterceptor(new StethoInterceptor())

        return mOkHttpClient;
    }

    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null; // By convention, 'null' creates an empty key store.
            keyStore.load(in, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private InputStream trustedCertificatesInputStream() {
        // PEM files for root certificates of Comodo and Entrust. These two CAs are sufficient to view
        // https://publicobject.com (Comodo) and https://squareup.com (Entrust). But they aren't
        // sufficient to connect to most HTTPS sites including https://godaddy.com and https://visa.com.
        // Typically developers will need to get a PEM file from their organization's TLS administrator.
        String comodoRsaCertificationAuthority = ""
                + "-----BEGIN CERTIFICATE-----\n"
                + "MIIDSjCCAjICCQD0bYX23U1k8jANBgkqhkiG9w0BAQUFADBnMQswCQYDVQQGEwJDTjELMAkGA1UE\n"
                + "CAwCQkwxCzAJBgNVBAcMAkJMMREwDwYDVQQKDAhiYWlsaXRvcDEQMA4GA1UECwwHamlzaHVidTEZ\n"
                + "MBcGA1UEAwwQYXBpLmJhaWxpdG9wLmVkdTAeFw0xNjA5MjYwODEyMjBaFw0xNzA5MjYwODEyMjBa\n"
                + "MGcxCzAJBgNVBAYTAkNOMQswCQYDVQQIDAJCTDELMAkGA1UEBwwCQkwxETAPBgNVBAoMCGJhaWxp\n"
                + "dG9wMRAwDgYDVQQLDAdqaXNodWJ1MRkwFwYDVQQDDBBhcGkuYmFpbGl0b3AuZWR1MIIBIjANBgkq\n"
                + "hkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwB6/pQE0cEEK//WZ8aHauW8U5bDvJw4bhu5Mxf4iBh1V\n"
                + "qtHHCkSPttwXr3qgIA29ZQp2r6ZXEGc5VZym1iY5KgBizjyI4SfNRlRQbr729kYxmDcf7dcYFIHN\n"
                + "O1gUMvNgBMyEI0m8CILOv8nDZVTaWq9Hay3thEQ0Spkq4kuetG8K4OLyv2Wmd4VO7RX5zJ7ZHgwY\n"
                + "FJcBee7FPe+oWtvM38JjZSPKU6ns6dTzGQFLTksAOCeO8I9XFH1ZvFtaNaGBsBxIrYkLyNxgTN2U\n"
                + "cs2HxgL67LBPd8EW7loOWMEpSmyCKaRl91bC9H8BhHfj4ZOHbAW+Gq8Tl8brS+IY+tyW0wIDAQAB\n"
                + "MA0GCSqGSIb3DQEBBQUAA4IBAQCUZP5CL99R4xFDPMvM0WM0VOn+VfTGA/Dq/9sp0i+7jd5FjOu9\n"
                + "dNpr2sPkXFqXFo5R6OjDxF+XXEz/sZBZTdIVynus2lzVf2rav1rwxZGzgEYKB9DgCRMzDykjAZ4Z\n"
                + "0j2RPzDLqXSDdrFTDDzJz25iEp7g5ULlPt+M3U4AQ4nfWOtqhvWEpBz0Zcmj5F5dwEoMTHvQAxYw\n"
                + " QnB+YJ2bGwvGnM8jRQeNIp5XpvPpfp7V41fhpm7Z7jm9w9ityqtZwu/UZV01IBT4onR5hgY5d7QG\n"
                + "3rw2crDIizQB5lm3qU1InU8QTIh/SGLdYAgF6R3Cdhzm+5/K7DiTfbSdlzQ0Uvo7\n"
                + "-----END CERTIFICATE-----\n";
//        String entrustRootCertificateAuthority = ""
//                + "-----BEGIN CERTIFICATE-----\n"
//                + "MIIEkTCCA3mgAwIBAgIERWtQVDANBgkqhkiG9w0BAQUFADCBsDELMAkGA1UEBhMC\n"
//                + "VVMxFjAUBgNVBAoTDUVudHJ1c3QsIEluYy4xOTA3BgNVBAsTMHd3dy5lbnRydXN0\n"
//                + "Lm5ldC9DUFMgaXMgaW5jb3Jwb3JhdGVkIGJ5IHJlZmVyZW5jZTEfMB0GA1UECxMW\n"
//                + "KGMpIDIwMDYgRW50cnVzdCwgSW5jLjEtMCsGA1UEAxMkRW50cnVzdCBSb290IENl\n"
//                + "cnRpZmljYXRpb24gQXV0aG9yaXR5MB4XDTA2MTEyNzIwMjM0MloXDTI2MTEyNzIw\n"
//                + "NTM0MlowgbAxCzAJBgNVBAYTAlVTMRYwFAYDVQQKEw1FbnRydXN0LCBJbmMuMTkw\n"
//                + "NwYDVQQLEzB3d3cuZW50cnVzdC5uZXQvQ1BTIGlzIGluY29ycG9yYXRlZCBieSBy\n"
//                + "ZWZlcmVuY2UxHzAdBgNVBAsTFihjKSAyMDA2IEVudHJ1c3QsIEluYy4xLTArBgNV\n"
//                + "BAMTJEVudHJ1c3QgUm9vdCBDZXJ0aWZpY2F0aW9uIEF1dGhvcml0eTCCASIwDQYJ\n"
//                + "KoZIhvcNAQEBBQADggEPADCCAQoCggEBALaVtkNC+sZtKm9I35RMOVcF7sN5EUFo\n"
//                + "Nu3s/poBj6E4KPz3EEZmLk0eGrEaTsbRwJWIsMn/MYszA9u3g3s+IIRe7bJWKKf4\n"
//                + "4LlAcTfFy0cOlypowCKVYhXbR9n10Cv/gkvJrT7eTNuQgFA/CYqEAOwwCj0Yzfv9\n"
//                + "KlmaI5UXLEWeH25DeW0MXJj+SKfFI0dcXv1u5x609mhF0YaDW6KKjbHjKYD+JXGI\n"
//                + "rb68j6xSlkuqUY3kEzEZ6E5Nn9uss2rVvDlUccp6en+Q3X0dgNmBu1kmwhH+5pPi\n"
//                + "94DkZfs0Nw4pgHBNrziGLp5/V6+eF67rHMsoIV+2HNjnogQi+dPa2MsCAwEAAaOB\n"
//                + "sDCBrTAOBgNVHQ8BAf8EBAMCAQYwDwYDVR0TAQH/BAUwAwEB/zArBgNVHRAEJDAi\n"
//                + "gA8yMDA2MTEyNzIwMjM0MlqBDzIwMjYxMTI3MjA1MzQyWjAfBgNVHSMEGDAWgBRo\n"
//                + "kORnpKZTgMeGZqTx90tD+4S9bTAdBgNVHQ4EFgQUaJDkZ6SmU4DHhmak8fdLQ/uE\n"
//                + "vW0wHQYJKoZIhvZ9B0EABBAwDhsIVjcuMTo0LjADAgSQMA0GCSqGSIb3DQEBBQUA\n"
//                + "A4IBAQCT1DCw1wMgKtD5Y+iRDAUgqV8ZyntyTtSx29CW+1RaGSwMCPeyvIWonX9t\n"
//                + "O1KzKtvn1ISMY/YPyyYBkVBs9F8U4pN0wBOeMDpQ47RgxRzwIkSNcUesyBrJ6Zua\n"
//                + "AGAT/3B+XxFNSRuzFVJ7yVTav52Vr2ua2J7p8eRDjeIRRDq/r72DQnNSi6q7pynP\n"
//                + "9WQcCk3RvKqsnyrQ/39/2n3qse0wJcGE2jTSW3iDVuycNsMm4hH2Z0kdkquM++v/\n"
//                + "eu6FSqdQgPCnXEqULl8FmTxSQeDNtGPPAUO6nIPcj2A781q0tHuu2guQOHXvgR1m\n"
//                + "0vdXcDazv/wor3ElhVsT/h5/WrQ8\n"
//                + "-----END CERTIFICATE-----\n";
        return new Buffer()
                .writeUtf8(comodoRsaCertificationAuthority)
//                .writeUtf8(entrustRootCertificateAuthority)
                .inputStream();
    }

}
/**
 * 封装的方法放在下面
 */

//    public void setCertificates(InputStream... certificates) {
//        try {
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
//            keyStore.load(null);
//            int index = 0;
//            for (InputStream certificate : certificates) {
//                String certificateAlias = Integer.toString(index++);
//                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
//
//                try {
//                    if (certificate != null)
//                        certificate.close();
//                } catch (IOException e) {
//                }
//            }
//
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//
//            TrustManagerFactory trustManagerFactory =
//                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//
//            trustManagerFactory.init(keyStore);
//            sslContext.init
//                    (
//                            null,
//                            trustManagerFactory.getTrustManagers(),
//                            new SecureRandom()
//                    );
////            mOkHttpClient.sslSocketFactory();
//            mOkHttpClient.newBuilder().sslSocketFactory(sslContext.getSocketFactory());
//
////            sslSocketFactory(sslContext.getSocketFactory())
////            mOkHttpClient.setSslSocketFactory(sslContext.getSocketFactory());
////            mOkHttpClient.setSslSocketFactory();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void shutDownAllRequest() {
//        getInstance().getOkHttpClient().connectionPool().evictAll();
//        ExecutorService executorService = getInstance().getOkHttpClient().dispatcher().executorService();
//        executorService.shutdown();
//    }