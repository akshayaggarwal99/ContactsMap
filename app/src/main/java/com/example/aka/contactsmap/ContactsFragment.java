package com.example.aka.contactsmap;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.aka.contactsmap.data.api.ContactsMapApi;
import com.example.aka.contactsmap.data.api.model.Contact;
import com.example.aka.contactsmap.data.api.model.ContactsResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by akshayaggarwal99 on 07-04-2016.
 */
// In this case, the fragment displays simple text based on the page
public class ContactsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private int mPage;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<ContactsResponse> feedsData = new ArrayList<>();
    private ArrayList<Contact> contacts = new ArrayList<>();
    private ArrayList<Contact> contacts_ = new ArrayList<>();
    Button button;
    Context cntx;
    FloatingActionButton fab;
    View view;

    public static ContactsFragment newInstance() {
        Bundle args = new Bundle();
        ContactsFragment fragment = new ContactsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recyclerViewAdapter = new RecyclerViewAdapter(contacts);
        view = inflater.inflate(R.layout.fragment_contacts, container, false);
        cntx = getActivity().getApplicationContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_feeds);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
        contacts_ = sendRequest();
        Log.i("contacts_debug2", contacts_.size() + "");
//
//        button = (Button) view.findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendRequest();
//            }
//        });

        return view;

    }

    private ArrayList<Contact> sendRequest() {
//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
//                .create();
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://private-b08d8d-nikitest.apiary-mock.com/")
//                .addConverterFactory(GsonConverterFactory.create(new Gson()))
//                .build();
//        ContactsMapApi api = retrofit.create(ContactsMapApi.class);
//        Call<ContactsResponse> call = api.loadcontacts();
//        call.enqueue(new Callback<ContactsResponse>() {
//            @Override
//            public void onResponse(Call<ContactsResponse> call, Response<ContactsResponse> response) {
//
//                ContactsResponse contactsResponse = response.body();
////                feedsData = new ArrayList<>(Arrays.asList(contactsResponse.getResults()));
//                Log.i("response", response.toString());
//
//            }
//
//            @Override
//            public void onFailure(Call<ContactsResponse> call, Throwable t) {
//                Log.d("Error", t.getMessage());
//
//            }
//        });
        ContactsMapApi.Factory.getInstance().loadcontacts().enqueue(new Callback<List<ContactsResponse>>() {
            @Override
            public void onResponse(Call<List<ContactsResponse>> call, Response<List<ContactsResponse>> response) {
                List<ContactsResponse> contactsResponseArrayList = response.body();
                ContactsResponse contactsResponse = contactsResponseArrayList.get(0);
                contacts = new ArrayList<>(contactsResponse.getContacts());
                contacts_ = contacts;

                Log.i("response", contactsResponse.getContacts().get(0).getEmail());
                recyclerViewAdapter = new RecyclerViewAdapter(contacts);
                recyclerView.setAdapter(recyclerViewAdapter);
                Log.i("contacts_debug1", contacts_.size() + "");
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int j = 0; j < contacts.size(); j++) {
                            WritePhoneContact(contacts.get(j).getName(), contacts.get(j).getPhone(), contacts.get(j).getEmail(),contacts.get(j).getOfficePhone(), cntx);
                        }
                        Snackbar.make(view, "Contacts has been added", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                });


            }

            @Override
            public void onFailure(Call<List<ContactsResponse>> call, Throwable t) {
                Log.e("error", t.toString());
            }
        });
        return contacts;
    }

    public void WritePhoneContact(String displayName, String number, String email,String officenumber, Context cntx /*App or Activity Ctx*/) {
        Context contetx = cntx; //Application's context or Activity's context
        String strDisplayName = displayName; // Name of the Person to add
        String strNumber = number; //number of the person to add with the Contact
        String strEmail = email;
        String strOfficeNumber=officenumber;

        ArrayList<ContentProviderOperation> cntProOper = new ArrayList<ContentProviderOperation>();
        int contactIndex = cntProOper.size();//ContactSize

        //Newly Inserted contact
        // A raw contact will be inserted ContactsContract.RawContacts table in contacts database.
        cntProOper.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)//Step1
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null).build());

        //Display name will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step2
                .withValueBackReference(Data.RAW_CONTACT_ID, contactIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, strDisplayName) // Name of the contact
                .build());
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step2
                .withValueBackReference(Data.RAW_CONTACT_ID, contactIndex)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)

                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, strEmail)

                .build());

        //Mobile number will be inserted in ContactsContract.Data table
        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)//Step 3
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, strNumber) // Number to be added
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE).build()); //Type like HOME, MOBILE etc

        cntProOper.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, contactIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strOfficeNumber).
                        withValue(Phone.TYPE, Phone.TYPE_WORK)
                .build());
        try {
            // We will do batch operation to insert all above data
            //Contains the output of the app of a ContentProviderOperation.
            //It is sure to have exactly one of uri or count set
            ContentProviderResult[] contentProresult = null;
            contentProresult = contetx.getContentResolver().applyBatch(ContactsContract.AUTHORITY, cntProOper); //apply above data insertion into contacts list
        } catch (RemoteException exp) {
            //logs;
        } catch (OperationApplicationException exp) {
            //logs
        }
    }
}