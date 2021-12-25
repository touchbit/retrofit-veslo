package org.touchbit.retrofit.veslo.client;

import org.touchbit.retrofit.veslo.exception.UtilityClassException;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * The utility class provides a socket context that allows you to trust the host on which SSL errors is present.
 * <p>
 * Created by Oleg Shaburov on 16.05.2018
 * shaburov.o.a@gmail.com
 */
public class TrustSocketHelper {

    public static final String CONTEXT = "TLSv1.2";
    public static final TrustAllCertsManager TRUST_ALL_CERTS_MANAGER = new TrustAllCertsManager();
    public static final TrustAllHostnameVerifier TRUST_ALL_HOSTNAME = new TrustAllHostnameVerifier();
    public static final SSLContext TRUST_ALL_SSL_CONTEXT = getTrustAllSSLContext(CONTEXT, TRUST_ALL_CERTS_MANAGER);
    public static final SSLSocketFactory TRUST_ALL_SSL_SOCKET_FACTORY = TRUST_ALL_SSL_CONTEXT.getSocketFactory();

    public static SSLContext getTrustAllSSLContext(String context, X509TrustManager manager) {
        try {
            SSLContext sslContext = SSLContext.getInstance(context);
            sslContext.init(null, new X509TrustManager[]{manager}, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create " + context + " context", e);
        }
    }

    @SuppressWarnings({"java:S5527"})
    public static class TrustAllHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String var1, SSLSession var2) {
            return true;
        }

    }

    public static class TrustAllCertsManager implements X509TrustManager {

        @Override
        @SuppressWarnings({"java:S4830"})
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // do nothing
        }

        @Override
        @SuppressWarnings({"java:S4830"})
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // do nothing
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private TrustSocketHelper() {
        throw new UtilityClassException();
    }

}
