package ua.mintmalory.githubfeed.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface GitHubService {

    String ENDPOINT = "https://translate.yandex.net/api/v1.5/tr.json";
	String API_KEY = "trnsl.1.1.20160331T065039Z.998c0e81c56fb0fe.8b1ab0611b1b6c7f28d15d8bd563a86977b7889a";

    @GET("/translate") //?key=trnsl.1.1.20160331T065039Z.998c0e81c56fb0fe.8b1ab0611b1b6c7f28d15d8bd563a86977b7889a&text=text&lang=en-ru
    Call<String> getTranslation(@Query("key") String apiKey, @Query("text") String textFortranslation, @Query("lang") String translationDirection);

}
