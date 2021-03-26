package com.jfloydconsult.matatu.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAHENTOCA:APA91bFjIHxP4zHmYXTwPOb0ABWifSAYkPM_1isynJeclwNu5QF8UMsKOngTuvdBlllGdn695rUX9LG2D9rCTR8tt5WPO7Q0V3FCinz_WLVvDi9rq-J5b5Z3FhEe00zEpEU_4GHZz88u"
    })
    @POST("form/send")
    Call<String> sendMessage(@Body String body);
}
