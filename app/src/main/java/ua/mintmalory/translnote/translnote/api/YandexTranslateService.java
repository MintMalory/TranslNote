package ua.mintmalory.translnote.translnote.api;

import java.util.List;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ua.mintmalory.translnote.translnote.model.Translation;
import ua.mintmalory.translnote.translnote.model.DetectedLanguage;

public interface YandexTranslateService {

    String ENDPOINT = "https://translate.yandex.net/api/v1.5/tr.json/";
	String API_KEY = "trnsl.1.1.20160331T065039Z.998c0e81c56fb0fe.8b1ab0611b1b6c7f28d15d8bd563a86977b7889a";

    @GET("translate") 
    Call<Translation> getTranslation(@Query("key") String apiKey, @Query("text") String titleForTranslation, @Query("text") String textForTranslation, @Query("lang") String translationDirection);
	
	@GET("detect")
	Call<DetectedLanguage> detectLanguageOfText(@Query("key") String apiKey, @Query("text") String textForDetection);

}
