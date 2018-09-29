package com.example.android.dalishboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

//Adapter class for populating recyclerView
public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonHolder>{

    //LOG_TAG for logging purposes
    private static final String LOG_TAG = PersonAdapter.class.getSimpleName();

    public class PersonHolder extends RecyclerView.ViewHolder{

        //Initialize views to be populated with Person data
        private ImageView icon;
        private TextView name, message, hometown, termsOn, project;

        //Constructor sets initialized views to corresponding views in layout file
        public PersonHolder(View itemView){
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            hometown = itemView.findViewById(R.id.hometown);
            termsOn = itemView.findViewById(R.id.terms_on);
            project = itemView.findViewById(R.id.project);
        }

        //setDetails extracts data from person object and populates appropriate view with
        //corresponding data
        public void setDetails(Person person){

            //Html.fromHtml allows part of string to be stylized. E.g: "Name" appears in bold
            //while Ricky doesn't
            name.setText(Html.fromHtml(person.getmName()));
            message.setText(Html.fromHtml(person.getmMessage()));
            termsOn.setText(Html.fromHtml(person.getmTermsOn()));
            project.setText(Html.fromHtml(person.getmProject()));

            //address is set after background IntentService completes so it might be null the first
            //time this part is run
            if(person.getmAddress() != null) {
                hometown.setText(Html.fromHtml(person.getmAddress()));
            }

            //Used Glide API to easily fetch image from url and edit them to have rounded corners
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new RoundedCorners(50));
            Glide.with(itemView.getContext()).load(person.getmIconUrl()).apply(requestOptions).into(icon);
        }

    }

    private Context mContext;
    private List<Person> mPersons;

    //PersonAdapter constructor takes in the context it was called from and a list of persons to
    //populate RecyclerView with.
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

    //refreshPersons updates mPersons by replacing its contents with new data. This is called after
    //fetchAddressIntentService background task has finished resolving Person coordinates
    public void refreshPersons(List<Person> people){
        this.mPersons.clear();
        this.mPersons.addAll(people);
        this.notifyDataSetChanged();
    }
}

