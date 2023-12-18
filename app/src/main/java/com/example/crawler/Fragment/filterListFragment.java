package com.example.crawler.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crawler.R;
import com.example.crawler.cardComponent;
import com.example.crawler.categoryActivity;
import com.example.crawler.util.Crawler;
import com.example.crawler.util.favoriteDB;
import com.example.crawler.util.util;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Vector;

import com.example.crawler.Adapter.FilterFragmentAdapter;

public class filterListFragment extends Fragment {

    public filterListInterface filterListInterface;

    private View RootView;
    private RecyclerView cardContainer;
    private FilterFragmentAdapter cardAdapter;
    private Vector<cardComponent> allCard;
    private Crawler crawler;

    public interface filterListInterface {
        public ArrayList<String> getUrlArray();

        public Vector<cardComponent> getData();

    }

    public static interface getDataFromFragment {
        public ArrayList<cardComponent> getCardInfo();
    }

    public Vector<cardComponent> getData() {
        return allCard;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof filterListInterface)
            filterListInterface = ((filterListInterface) context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.filter_list_fragment, container, false);
        return RootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Vector<cardComponent> tempCard = filterListInterface.getData();
        if (tempCard == null)
            tempCard = new Vector<>();
        allCard = tempCard;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                cardContainer = getView().findViewById(R.id.cardContainer);
                cardContainer.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));
                cardContainer.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
                cardAdapter = new FilterFragmentAdapter(allCard);
                cardContainer.setAdapter(cardAdapter);
            }
        });
        //test.setText("List正常創建");
    }
    public void showList(Vector<cardComponent> showData) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                cardContainer = getView().findViewById(R.id.cardContainer);
                cardContainer.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 1));
                cardContainer.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
                cardAdapter = new FilterFragmentAdapter(showData);
                cardContainer.setAdapter(cardAdapter);

            }
        });
    }
    public void updateList() {
        ArrayList<String> UrlList = filterListInterface.getUrlArray();
        Log.i("Test", "updateList");
        getDataFromUrl(UrlList);
        showList(allCard);
    }

    private Vector<cardComponent> getDataFromUrl(ArrayList<String> UrlList) {

        ArrayList<ArrayList<Document>> allDocument = new ArrayList<>();
        for (String Url : UrlList) {
            crawler = new Crawler(Url, true);
            Thread nowThread = new Thread(crawler);
            nowThread.start();
            try {
                nowThread.join();
                allDocument.add(crawler.getDeepDoc());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        allCard = null;
        //Log.i("Test",Integer.toString(allDocument.size()));
        for (ArrayList<Document> TagSeries : allDocument) {
            Vector<cardComponent> groupCard = new Vector<>();
            for (Document doc : TagSeries) {
                Vector<cardComponent> tempCard = new Vector<>();
                Elements form = doc.select(".contsec").select("table[cellpadding='3']").select("td > a");
                for (Element temp : form) {
                    cardComponent tempComp = new cardComponent(RootView.getContext());
                    tempComp.setTitleText(temp.text());
                    tempComp.setContentURL("http://www.wikicfp.com"+temp.attr("href"));
                    tempCard.add(tempComp);
                    //Log.i("Test",temp.text());
                }
                Elements deadLine = doc.select("form[name='myform']").select("table[cellpadding='3']").select("tr > td");
                //Log.i("Test",Integer.toString(deadLine.size())+" "+Integer.toString(form.size()));
                for (int i = 5, now = 0; i < (deadLine.size()); ++i) {
                    //Log.i("Test",deadLine.get(i).text());
                    if (deadLine.get(i).text().equals("Expired CFPs")) {
                        while (tempCard.size() > now)
                            tempCard.remove(tempCard.size() - 1);
                        break;
                    }
                    if ((i - 5) % 6 == 3)
                        tempCard.elementAt(now).setWhen(deadLine.get(i).text());
                    else if ((i - 5) % 6 == 4)
                        tempCard.elementAt(now).setWhere(deadLine.get(i).text());
                    else if ((i - 5) % 6 == 5) {
                        tempCard.elementAt(now).setDeadLine(deadLine.get(i).text());
                        now++;
                    }
                }
                groupCard.addAll(tempCard);
            }
            if (allCard == null)
                allCard = groupCard;
            else { //remain same title
                Vector<cardComponent> filterCard = new Vector<>();
                for (cardComponent card : groupCard) {
                    //Log.i("Test", "Card title=" + card.title.getText().toString());
                    while (allCard.size() > 0
                            && util.ChangeTimeToSec(allCard.firstElement().deadLine.getText().toString()) < util.ChangeTimeToSec(card.deadLine.getText().toString())) {
                        // Log.i("Test", "Delete - " + allCard.get(now).title.getText().toString() + "left :" + Integer.toString(allCard.size()));
                        allCard.remove(0);
                    }
                    int tempNow = 0;
                    while (allCard.size() > tempNow
                            && util.ChangeTimeToSec(allCard.get(tempNow).deadLine.getText().toString()) == util.ChangeTimeToSec(card.deadLine.getText().toString())
                            && !allCard.get(tempNow).equals(card)) {
                        tempNow++;
                    }
                    if (allCard.size() > tempNow && allCard.get(tempNow).equals(card))
                        filterCard.add(allCard.get(tempNow));
                    if (allCard.isEmpty())
                        break;
                }
                allCard = filterCard;
            }
            //Log.i("Test","Length from "+groupCard.size()+" to Length = " + Integer.toString(allCard.size()));
        }
        if(allCard==null)
            allCard=new Vector<>();
        if(filterListInterface instanceof  categoryActivity) {
            favoriteDB DB = ((categoryActivity) filterListInterface).DataBase;
            for(int i=0;i<allCard.size();++i){
                if(DB.hasInData(allCard.get(i)))
                    allCard.get(i).FavoriteButton.setChipIconResource(R.drawable.favorite);
            }
        }

        return allCard;
    }
}
