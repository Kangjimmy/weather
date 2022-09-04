package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    @Value("${openweathermap.key}")
    private String apiKey;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    public void createDiary(LocalDate date, String text) {
        //open weather map 에서 데이터 받아오기
        String weatherData = getWeatherString();
        System.out.println(weatherData);

        //받아온 날씨 데이터 json 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        //파싱된 데이터를 우리 db에 저장하기
        Diary nowDiary = new Diary();
        nowDiary.setWeather(parsedWeather.get("main").toString());
        nowDiary.setIcon(parsedWeather.get("icon").toString());
        nowDiary.setTemperature((Double)parsedWeather.get("temp"));
        nowDiary.setText(text);
        nowDiary.setDate(date);

        diaryRepository.save(nowDiary);
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=Seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader br;

            if (connection.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    //형태가 mainData를 받아와서 여기서 temp값을 가져옴
    //형태가 weatherData를 받아와서 main, icon을 가져옴

    private Map<String, Object> parseWeather(String jsonString) {

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        try {
           jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject mainData = (JSONObject) jsonObject.get("main");

        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("temp", mainData.get("temp"));
        resultMap.put("icon", weatherData.get("icon"));
        resultMap.put("main", weatherData.get("main"));

        return resultMap;
    }
//    private Map<String, Object> parseWeather(String jsonString) {
//        JSONParser jsonParser = new JSONParser();
//        JSONObject jsonObject;
//
//        try {
//            jsonObject = (JSONObject) jsonParser.parse(jsonString);
//
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        Map<String, Object> resultMap = new HashMap<>();
//
//        JSONObject mainData = (JSONObject) jsonObject.get("main");
//        resultMap.put("temp", mainData.get("temp"));
//        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
//        JSONObject weatherData = (JSONObject) weatherArray.get(0);
//        resultMap.put("main", weatherData.get("main"));
//        resultMap.put("icon", weatherData.get("icon"));
//
//        return resultMap;
//    }
}
