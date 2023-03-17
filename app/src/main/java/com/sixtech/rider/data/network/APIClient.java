 package com.sixtech.rider.data.network;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.sixtech.rider.BuildConfig;
import com.sixtech.rider.MvpApplication;
import com.sixtech.rider.data.SharedHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

public class APIClient {

    private static Retrofit retrofit = null;
//    private static X509TrustManager x509TrustManager;

    public static ApiInterface getAPIClient() {
        if (retrofit == null) {

                retrofit = new Retrofit
                        .Builder()
                        .baseUrl(BuildConfig.BASE_URL)
                        .client(createTrustingOkHttpClient())

                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

        }
        return retrofit.create(ApiInterface.class);
    }
//    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
//            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
//
//        // Loading CAs from an InputStream
//        CertificateFactory cf = null;
//        cf = CertificateFactory.getInstance("X.509");
//
//        Certificate ca;
//        // I'm using Java7. If you used Java6 close it manually with finally.
//        try (InputStream cert = context.getResources().openRawResource(R.raw.__6ixtaxi_com)) {
//            ca = cf.generateCertificate(cert);
//        }
//
//        // Creating a KeyStore containing our trusted CAs
//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("ca", ca);
//
//        // Creating a TrustManager that trusts the CAs in our KeyStore.
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//        // Creating an SSLSocketFactory that uses our TrustManager
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        x509TrustManager= (X509TrustManager) tmf.getTrustManagers()[0];
//        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());
//
//        return sslContext;
//    }

    private static OkHttpClient createTrustingOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        try {
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                @SuppressLint("TrustAllX509TrustManager")
                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    x509TrustManager
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), x509TrustManager)
                    .hostnameVerifier((hostname, session) -> true)
                    .addNetworkInterceptor(new AddHeaderInterceptor())
                    /*.addInterceptor(new DecryptedPayloadInterceptor(new DecryptedPayloadInterceptor.DecryptionStrategy() {
                        @Override
                        public String decrypt(String stream) throws Exception {
                            if (!SharedHelper.apiState.equals("")) {
                                SharedHelper.apiState="";

                                return new String(Base64.decode(stream , Base64.DEFAULT), StandardCharsets.UTF_8);
                            }else {
                                return stream;
                            }
                        }


                    }))*/
                    //.addNetworkInterceptor(new StethoInterceptor())
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(interceptor)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader(
                    "Authorization",
                    SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            Log.d("RRR TOKEN", SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            return chain.proceed(builder.build());
        }
    }
}
class DecryptedPayloadInterceptor implements Interceptor {

    private final DecryptionStrategy mDecryptionStrategy;

    public interface DecryptionStrategy {
        String decrypt(String stream) throws Exception;
    }

    public DecryptedPayloadInterceptor(DecryptionStrategy mDecryptionStrategy) {
        this.mDecryptionStrategy = mDecryptionStrategy;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        Response.Builder newResponse = response.newBuilder();
        String contentType = response.header("Content-Type");
        if (TextUtils.isEmpty(contentType)) contentType = "application/json";
        String decrypted = null;
        if (mDecryptionStrategy != null) {
            try {
                decrypted = mDecryptionStrategy.decrypt(response.body().string());
                Log.e("sdsd",decrypted);

            } catch (Exception e) {

                e.printStackTrace();
                Log.e("sdsd",e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("No decryption strategy!");
        }
        newResponse.body(ResponseBody.create(MediaType.parse(contentType), decrypted));
        return newResponse.build();

    }
}





