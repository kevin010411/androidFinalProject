package com.example.crawler.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crawler.R;
import com.example.crawler.cardComponent;

import java.util.ArrayList;
import java.util.Vector;

public class FilterFragmentAdapter extends RecyclerView.Adapter<FilterFragmentAdapter.ViewHolder> {

    private  Vector<cardComponent> allCard;
    public FilterFragmentAdapter(Vector<cardComponent> all)
    {
        allCard=all;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = new cardComponent(parent.getContext());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setCard(allCard.get(position));
    }

    @Override
    public int getItemCount() {
        return allCard.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private cardComponent item;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = (cardComponent) itemView;
        }
    }
}
