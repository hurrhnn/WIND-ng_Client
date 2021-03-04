package utils;

import org.json.JSONObject;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HTTPMethods {
    public static String GET(String URL, @Nullable Map<String, String> headers, boolean isNeedAuth) {

        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(URL).openConnection();
            httpsURLConnection.setRequestMethod("GET");
            HTTPConnector(headers, httpsURLConnection);

            return getResponseString(httpsURLConnection, "GET");
        } catch (IOException e) {
            errHandler(e, "GET");
        }
        return "";
    }

    public static String POST(String URL, @Nullable Map<String, String> headers, JSONObject data) {
        try {
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) new URL(URL).openConnection();
            httpsURLConnection.setRequestMethod("POST");
            httpsURLConnection.setDoOutput(true);
            HTTPConnector(headers, httpsURLConnection);

            if (data.toString().length() != 0) {
                OutputStream httpsOutputStream = httpsURLConnection.getOutputStream();
                PrintWriter writer = new PrintWriter(httpsOutputStream);
                writer.write(data.toString());
                writer.flush();
            }

            return getResponseString(httpsURLConnection, "POST");
        } catch (IOException e) {
            errHandler(e, "POST");
        }
        return "";
    }

    private static void HTTPConnector(@Nullable Map<String, String> headers, HttpURLConnection httpURLConnection) throws IOException {
        if (headers != null)
            headers.forEach(httpURLConnection::setRequestProperty);

        httpURLConnection.connect();
    }

    private static String getResponseString(HttpsURLConnection httpsURLConnection, String method) throws IOException {
        InputStream httpsInputStream = httpsURLConnection.getInputStream();

        byte[] b = new byte[4096];
        StringBuilder responseStringBuilder = new StringBuilder();
        for (int n; (n = httpsInputStream.read(b)) != -1;) {
            responseStringBuilder.append(new String(b, 0, n));
        }

        InputStream httpsErrorStream = httpsURLConnection.getErrorStream();
        if (httpsErrorStream != null) {
            responseStringBuilder = new StringBuilder();
            b = new byte[4096];
            for (int n; (n = httpsErrorStream.read(b)) != -1;) {
                responseStringBuilder.append(new String(b, 0, n));
            }

            if (responseStringBuilder.length() != 0) {
                System.out.println("REQUEST " + method + " ERROR: " + responseStringBuilder.toString());
                return "";
            }

        }
        return responseStringBuilder.toString();
    }

    public static void errHandler(Exception e, String method) {
        PrintStream errPrintStream = null;
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try {
            errPrintStream = new PrintStream(err, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ignored) {
        }
        e.printStackTrace(errPrintStream);
        System.out.println("REQUEST " + method + " ERROR: " + err.toString().split("\n")[0]);
    }
}
