package com.example.android.dalishboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonHolder>{

    private static final String LOG_TAG = PersonAdapter.class.getSimpleName();

    public class PersonHolder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView name, message, hometown, termsOn, project;
        public PersonHolder(View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            hometown = itemView.findViewById(R.id.hometown);
            termsOn = itemView.findViewById(R.id.terms_on);
            project = itemView.findViewById(R.id.project);
        }

        public void setDetails(Person person){
            name.setText(Html.fromHtml(person.getmName()));
            message.setText(Html.fromHtml(person.getmMessage()));
            if(person.getmAddress() != null) {
                hometown.setText(Html.fromHtml(person.getmAddress()));
                Log.e(LOG_TAG, person.getmAddress());
            }
            termsOn.setText(Html.fromHtml(person.getmTermsOn()));
            project.setText(Html.fromHtml(person.getmProject()));
            RequestOptions requestOptions = new RequestOptions();

            requestOptions = requestOptions.transform(new RoundedCorners(50));
            Glide.with(itemView.getContext()).load(person.getmIconUrl()).apply(requestOptions).into(icon);
        }

    }

    private Context mContext;
    private List<Person> mPersons;
    public PersonAdapter(Context context, List<Person> persons){
        mContext = context;
        mPersons = persons;
    }

    @NonNull
    @Override
    public PersonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.person_card, parent, false);
        return new PersonHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonHolder holder, int position) {
        Person person = mPersons.get(position);
        holder.setDetails(person);
    }

    @Override
    public int getItemCount() {
        return mPersons.size();
    }

    public void refreshPersons(List<Person> people){
        this.mPersons.clear();
        this.mPersons.addAll(people);
        this.notifyDataSetChanged();
    }
}

