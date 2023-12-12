package com.example.crawler.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.crawler.MainActivity;
import com.example.crawler.R;
import com.example.crawler.categoryActivity;
import com.example.crawler.util.Crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentFragment extends Fragment {

    private TextView Title;
    private String URL;
    private Crawler crawler;
    public ContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url 要爬的網站.
     * @return A new instance of fragment ContentFragment.
     */

    // Factory method
    public static ContentFragment newInstance(String url) {
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putString("URL",url);
        fragment.setArguments(args);
        return fragment;
    }
    private Context parentContext;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null) {
            URL = getArguments().getString("URL");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContentFragment parentFragment = this;
        crawler = new Crawler(URL);
        view.findViewById(R.id.BackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getContext() instanceof MainActivity){
                    getActivity().getSupportFragmentManager().beginTransaction()
                       .remove(parentFragment).commit();
                    MainActivity activity = (MainActivity) view.getContext();
                    activity.cardContainer.setVisibility(View.VISIBLE);
                }
                else if(view.getContext() instanceof categoryActivity){
                    categoryActivity activity = (categoryActivity) view.getContext();
                    FragmentTransaction transaction =  getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.remove(parentFragment);
                    transaction.show(activity.fragment);
                    transaction.commit();
                }
            }
        });
        TextView Content = view.findViewById(R.id.Content);
        Thread crawlerThread = new Thread(crawler);
        crawlerThread.start();
        try {
            crawlerThread.join();
        } catch (InterruptedException e) {
            Log.i("Test","內容搜尋有誤");
            throw new RuntimeException(e);
        }
        Document doc = crawler.getDoc();
        Element titleEle = doc.select("h2>span[typeof=v:Event]>span[property=v:description]").first();
        Title = view.findViewById(R.id.Title);
        Title.setText(titleEle.text());
        Elements content = doc.select("td>div.cfp");
        String contentStr="";
        for(Element element:content){
            //Log.i("Test",element.toString());
            contentStr+=element.text();
            contentStr+="\n";
        }
        Log.i("Test",contentStr);
        Content.setText(contentStr);
    }

}