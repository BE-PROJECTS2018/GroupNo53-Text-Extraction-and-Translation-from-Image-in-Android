package com.example.dinesh.btranslate.People;

import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dinesh.btranslate.People.PDFAdaptor;
import com.example.dinesh.btranslate.R;

import java.util.List;

/**
 * Created by dinesh on 21-03-2018.
 */

public class PDFAdaptor extends RecyclerView.Adapter<PDFAdaptor.MyViewHolder> {

    private List<PDF> PDFList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.pdfs);
        }
    }


    public PDFAdaptor(List<PDF> PDFList) {
        this.PDFList =PDFList ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pdf_view_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PDF pdf = PDFList.get(position);
        holder.name.setText(pdf.getPDFname());
        }

    @Override
    public int getItemCount() {
        return PDFList.size();
    }
}
