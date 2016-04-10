package com.example.aka.contactsmap;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.aka.contactsmap.data.api.model.Contact;

import java.util.ArrayList;

/**
 * Created by akshayaggarwal99 on 08-04-2016.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<Contact> contacts;
    private ContactsFragment contactsFragment;
    public RecyclerViewAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
//        this.feedsFragment = feedsFragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView cv;
        TextView personName;
        TextView personEmail;
        TextView personPhone;
        TextView personWork;


        ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            personEmail = (TextView) itemView.findViewById(R.id.person_email);
            personPhone = (TextView) itemView.findViewById(R.id.person_phone);
            personWork = (TextView) itemView.findViewById(R.id.person_work);
        }


    }
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }


    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int i) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.personName.setText(contacts.get(i).getName());
        holder.personEmail.setText("Email: "+contacts.get(i).getEmail());
        holder.personPhone.setText("Mob: "+contacts.get(i).getPhone());
        holder.personWork.setText("Office: "+contacts.get(i).getOfficePhone());


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
