import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import spark.Request;
import spark.Response;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHandler {

    public static String inputHandler(Request request, Response response) {
        String value = request.queryParams("value");
        String returnValue ;
        URL url= null;
        try {
            url = new URL(value);
            String fileName = url.toString().substring(url.toString().lastIndexOf("/") + 1);
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    int contentLength = httpURLConnection.getContentLength();
                    httpURLConnection.disconnect();
                    TUID tuid = TUID.creatTUID(url);
                    String downloadLink = "http://" + request.host() + "/download/" + tuid.gettUID() + "/" + fileName.substring(1, fileName.length() - 1).replace(" ", "%20");
                    returnValue = "{\n" +
                            "\t\"filename\": \"" + fileName + "\",\n" +
                            "\t\"filesize\": " + contentLength + ",\n" +
                            "\t\"link\": \"" + value + "\",\n" +
                            "\t\"download\": \"" + downloadLink + "\"\n" +
                            "}";
                }else{
                    returnValue = "{\n" +
                            "\t\"error\": HttpURLConnection " +httpURLConnection.getResponseCode()+
                            "}";
                }
            } catch (IOException e) {
                //e.printStackTrace();
                returnValue = "{\n" +
                        "\t\"error\": HttpURLConnection error "+
                        "}";
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
            returnValue = "{\n" +
                    "\t\"error\": Invalid URL "+
                    "}";

        }
        return returnValue;
    }

    public static String downloadHandler(Request request, Response response) {
        String ans = "";
        String id = request.params(":id");
        TUID tuid = TUID.getTUID(id);
        if (tuid != null) {
            URL url = tuid.getUrl();
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                int respCode = +httpURLConnection.getResponseCode();
                if (respCode == HttpURLConnection.HTTP_OK) {
                    String fileName = url.toString().substring(url.toString().lastIndexOf("/") + 1);
                    String contentType = httpURLConnection.getContentType();
                    int contentLength = httpURLConnection.getContentLength();
                    httpURLConnection.disconnect();
                    response.raw().setHeader("Content-Length", Integer.toString(contentLength));
                    response.raw().setHeader("Content-Type", contentType);
                    response.raw().setHeader("Accept-Ranges", "none");
                    response.raw().setHeader("Content-Disposition", "attachment; filename=" + fileName);
                    //TODO need to create download method using Threads for Accept-Ranges;
                    //response.raw().setHeader("Range","bytes=500-999");
                    //response.raw().setHeader("Content-Range","bytes 500-999"+contentLength);
                    try (BufferedInputStream in = new BufferedInputStream(url.openStream())) {
                        byte[] dataBuffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                            response.raw().getOutputStream().write(dataBuffer, 0, bytesRead);
                        }
                        TUID.removeTUID(tuid);
                    } catch (IOException e) {
                        //Once user closes the download or the download had a problem.
                    }
                } else {
                    ans = "Rejected HTTPSession";
                }
            } catch (IOException e) {
                //HTTPSession problem.
            }


        } else {
            ans = "wrong request";
        }
        return ans;
    }
}
