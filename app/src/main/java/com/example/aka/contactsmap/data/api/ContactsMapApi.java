package com.example.aka.contactsmap.data.api;

import com.example.aka.contactsmap.data.api.model.ContactsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by akshayaggarwal99 on 08-04-2016.
 */
public interface ContactsMapApi {

    String BASE_URL = "http://private-b08d8d-nikitest.apiary-mock.com/";

    @GET("/contacts")
    Call<List<ContactsResponse>> loadcontacts();

    class Factory {

        private static ContactsMapApi service;

        public static ContactsMapApi getInstance() {
            if (service == null) {
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
                service = retrofit.create(ContactsMapApi.class);
                return service;
            } else {
                return service;
            }
        }
    }

}
