package com.example.crawler.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crawler.R;
import com.example.crawler.categoryActivity;

import java.util.ArrayList;
import java.util.Locale;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>
implements Filterable{

    private Context Parent;
    private ArrayList<String> arrayList;
    private ArrayList<String> filterList;

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filterList = new ArrayList<>();
            if(charSequence==null||charSequence.length()==0)
                filterList.addAll(arrayList);
            else{
                for(String nowStr:arrayList)
                {
                    //Filter
                    if(nowStr.toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        filterList.add(nowStr);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values=filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyDataSetChanged();
        }
    };
    public CategoryAdapter(ArrayList<String> arr, Context parent){
        arrayList=arr;
        filterList=new ArrayList<>();
        filterList.addAll(arr);
        Parent=parent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_card_component,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(filterList.get(position));
    }


    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView title;
        public ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.Title);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            filterList.remove(title.getText());
            GridLayout TagContainer = view.getRootView().findViewById(R.id.pickedCategory);
            if(TagContainer.getChildCount()==4) {
                new AlertDialog.Builder(TagContainer.getContext()).setTitle("篩選分類過多").setMessage("請刪除一些分類").show();
                return;
            }
            Button nowTag = new Button(TagContainer.getContext());
            nowTag.setTextSize(12);
            nowTag.setWidth(550);
            nowTag.setHeight(180);
            nowTag.setGravity(Gravity.CENTER);
            nowTag.setPadding(20,5,20,5);
            nowTag.setText((String)title.getText());
            nowTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    filterList.add(((Button) view).getText().toString());
                    TagContainer.removeView(view);
                    if(Parent instanceof categoryActivity)
                        ((categoryActivity)Parent).updateList();
                    notifyDataSetChanged();
                }
            });
            TagContainer.addView(nowTag,TagContainer.getChildCount());
            notifyDataSetChanged();
        }
    }
}

