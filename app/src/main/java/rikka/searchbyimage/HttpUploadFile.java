package rikka.searchbyimage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Rikka on 2015/12/12.
 */
public class HttpUploadFile {

    String boundary = "----WebKitFormBoundaryAAGZldGncBiDdsTP";

    public String Upload(String uri, String fileFromName, InputStream inputStream) {


        byte[] postHeaderBytes = getHeadBytes(fileFromName);
        byte[] boundaryBytes = getBoundaryBytes();

        HttpURLConnection connection = null;
        BufferedInputStream fileStream = null;
        String responseUri = null;

        try {
            /*Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1",
                    27000));*/
            connection = (HttpURLConnection) new URL(uri).openConnection();


            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("content-type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setUseCaches(false);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.152 Safari/535.19");
            connection.setConnectTimeout(60 * 1000);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(postHeaderBytes);

            byte[] buffer = new byte[4096];


            fileStream = new BufferedInputStream(inputStream);
            while ((fileStream.read(buffer)) != -1) {
                os.write(buffer);
            }

            os.write(boundaryBytes);
            os.flush();
            os.close();

            connection.connect();
            connection.getInputStream();

            responseUri = connection.getURL().toString();

        } catch (IOException e) {
            e.printStackTrace();

            responseUri = "";
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connection.disconnect();
            }
        }
        return responseUri;
    }

    private byte[] getHeadBytes(String fileFromName) {
        // 前面
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"encoded_image\"; filename=\"");
        sb.append(fileFromName);
        sb.append("\"");
        sb.append("\r\n");
        sb.append("Content-Type: application/octet-stream");
        sb.append("\r\n\r\n");

        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(sb.toString());
        byte[] b = new byte[byteBuffer.remaining()];
        byteBuffer.get(b);
        return b;
    }

    private byte[] getBoundaryBytes() {
        // 后面
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("--" + boundary + "--");
        sb.append("\r\n");

        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(sb.toString());
        byte[] b = new byte[byteBuffer.remaining()];
        byteBuffer.get(b);
        return b;
    }
}