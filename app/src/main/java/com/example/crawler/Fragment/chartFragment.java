package com.example.crawler.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.crawler.R;
import com.example.crawler.cardComponent;
import com.example.crawler.categoryActivity;
import com.example.crawler.util.Crawler;
import com.example.crawler.util.util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class chartFragment extends Fragment {
    private View RootView;
    private BarChart chart;
    private TextView test;
    private Crawler crawler;
    private filterListFragment.filterListInterface filterListInterface;
    private Vector<cardComponent> allCard;
    private HashMap<String,Integer> CountDay;
    private Vector<String> xAxisString;
    public chartFragment(){

    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof filterListFragment.filterListInterface) {
            filterListInterface = ((filterListFragment.filterListInterface) context);
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RootView = inflater.inflate(R.layout.chart_fragment,container,false);
        test = RootView.findViewById(R.id.testText);
        chart = RootView.findViewById(R.id.MainChart);
        //chart init
        chartInit();

        return RootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Vector<cardComponent> tempCard = filterListInterface.getData();
        if(tempCard!=null)
            allCard=tempCard;
        else
            allCard=new Vector<>();
        showChart(allCard);
        test.setText("Chart正常創建");
    }
    public void showChart(Vector<cardComponent> CardContainer)
    {
        List<BarEntry> displayData = new ArrayList<>();
        xAxisString=new Vector<>();
        CountDay = new HashMap<>();
        for(cardComponent card : CardContainer)
        {
            String DayName = card.deadLine.getText().toString();
            int time = util.ChangeTimeToSec(DayName);
            if(CountDay.computeIfPresent(DayName,(key,value)->value+1)==null){
                CountDay.put(DayName,1);
                xAxisString.add(card.deadLine.getText().toString());
            }
        }
        for(int pos = 0;pos<xAxisString.size();++pos)
            displayData.add(new BarEntry(pos,CountDay.get(xAxisString.get(pos))));

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BarDataSet dataSet = new BarDataSet(displayData,"截止日期分布");
                dataSet.setValueTextSize(15f);
                BarData data = new BarData(dataSet);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return Integer.toString(Math.round(value));
                    }
                });
//                chart.getXAxis().setValueFormatter(new ValueFormatter(){
//                    @Override
//                    public String getFormattedValue(float value) {
//                        //Log.i("Test","Value="+Float.toString(value));
//                        if(value==Math.round(value))
//                            return xAxisString.get((int)value);
//                        else return "";
//                    }
//                });
                chart.setData(data);
                chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        //Log.i("Test",xAxisString.get((int)e.getX()));
                        dataSet.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getBarLabel(BarEntry barEntry) {
//                                    if(barEntry.getX()==Math.round(barEntry.getX()) && e.getX()!=barEntry.getX())
//                                        return Integer.toString((int)e.getY());
                                    if(e.getX()==barEntry.getX())
                                        return xAxisString.get((int)barEntry.getX());
                                    else return "";
                            }
                        });
                        chart.invalidate();
                    }

                    @Override
                    public void onNothingSelected() {
                        dataSet.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return Integer.toString(Math.round(value));
                            }
                        });
                        chart.invalidate();
                    }
                });
                chart.invalidate();
            }
        });
    }
    public void updateChart()
    {
        getDataFromUrl(filterListInterface.getUrlArray());
        showChart(allCard);
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
        return allCard;
    }
    private void chartInit()
    {
        chart.getDescription().setText("數量-時間 統計圖");
        chart.getDescription().setTextSize(15f);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setNoDataText("目前沒有資料");
        chart.getAxisLeft().setGranularityEnabled(true);
        chart.getAxisLeft().setGranularity(1);
        chart.getAxisRight().setGranularityEnabled(true);
        chart.getAxisRight().setGranularity(1);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(15f);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1);
    }

    public Vector<cardComponent> getData(){
        return allCard;
    }
}
