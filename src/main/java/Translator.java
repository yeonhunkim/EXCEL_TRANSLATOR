import com.google.cloud.translate.Translation;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.HashMap;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;

public class Translator {
    private static String clientId =  "Oy_jT3nPCovd59npamqt";
    private static String clientSecret = "wcnNvd7cmp";
    private static String apiURL = "https://openapi.naver.com/v1/papago/n2mt";

    private static Translate translate = TranslateOptions.getDefaultInstance().getService();

    public static String readStringFromCsv(String filePath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line;
        StringBuilder resultString = new StringBuilder();
        while (true)   //returns a Boolean value
        {
            try {
                line = br.readLine();
                if (line == null) break;
                resultString.append(line + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return resultString.toString();
    }

    public static String sendStringToPapago(String input) {
        String text = "";
        try {
            text = URLEncoder.encode(input, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("인코딩 실패", e);
        }

        Translation translation = translate.translate(
                input,
                Translate.TranslateOption.sourceLanguage("ko"),
                Translate.TranslateOption.targetLanguage("en"));
        return translation.getTranslatedText();

        /*
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = post(apiURL, requestHeaders, text);

        JSONObject jsonObject = new JSONObject(responseBody);
        String resultString = "";
        try {
            resultString = jsonObject.getJSONObject("message")
                    .getJSONObject("result")
                    .getString("translatedText");
            //return responseBody;
        } catch (Exception e) {
            return "";
        }

        return resultString;
        */
    }

    private static String post(String apiUrl, Map<String, String> requestHeaders, String text){
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=ko&target=en&text=" + text; //�������: �ѱ��� (ko) -> �������: ���� (en)
        try {

            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            //postParams = new String(postParams.getBytes(), "UTF-8");    
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // ���� ����
                return readBody(con.getInputStream());
            } else {  // ���� ����
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) throws UnsupportedEncodingException{
        InputStreamReader streamReader = new InputStreamReader(body, "EUC-KR");

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();
            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }
            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
