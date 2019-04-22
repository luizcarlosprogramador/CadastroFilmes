package com.luizcarlos.cadastrofilmes;

import android.app.Application;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

//import com.squareup.picasso.Picasso;

//import com.viralandroid.testezup.R;
//import com.viralandroid.testezup.SubjectData;

import java.util.ArrayList;

public class CustomAdapter implements ListAdapter {
    private ArrayList<MovieData> arrayList;
    Context context;
    public CustomAdapter(Context context, ArrayList<MovieData> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {return true; }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) { }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) { }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) { return (long) position; }

    @Override
    public int getCount() { return arrayList.size(); }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            if (arrayList.size() > 0) {
                MovieData movieData = arrayList.get(position);


                if (convertView == null) {


                    LayoutInflater layoutInflater = LayoutInflater.from(context);
                    convertView = layoutInflater.inflate(R.layout.list_row, null);

                    TextView tittle = convertView.findViewById(R.id.title);
                    ImageView imag = convertView.findViewById(R.id.list_image);
                    tittle.setText(movieData.MovieName);

                    imag.setImageBitmap(BitmapFactory.decodeFile(movieData.MovieImage));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(v.getContext(), "teste", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        } finally {

        }



        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }


    @Override
    public boolean isEmpty() {
        return arrayList.isEmpty();
          }
}
