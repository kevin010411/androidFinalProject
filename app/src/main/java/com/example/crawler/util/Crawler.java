package com.example.crawler.util;

import android.os.Handler;
import android.util.Log;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class Crawler implements Runnable {
    private Document doc;
    private ArrayList<Document> allDoc;
    private Boolean isDeepSearch;
    private String Url;

    public Crawler(String url){
        Url=url;
        isDeepSearch=false;
    }
    public  Crawler(String url,boolean IsDeepSearch) {
        Url = url;
        isDeepSearch=IsDeepSearch;
        allDoc=new ArrayList<>();
    }

    @Override
    public void run() {
        if(Url==null)
            return;
        if(!isDeepSearch) {
            try {
                Connection con = Jsoup.connect(Url)
                        .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                        .timeout(5000)
                        .ignoreHttpErrors(true);
                Log.i("Test", "Crawling " + Url);
                doc = con.get();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Test Error", "Error occur -> 單一搜尋:" + Url);
            }
        }
        else {
            String nowUrl = Url;
            int page = 2;
            try {
                Document doc;
                do
                {
                    Log.i("Test","now searching "+nowUrl);
                    Connection con = Jsoup.connect(nowUrl)
                            .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                            .timeout(5000)
                            .ignoreHttpErrors(true);
                    doc = con.get();
                    allDoc.add(doc);
                    nowUrl = Url + "&page=" + Integer.toString(page);
                    page++;
                }while(!isSearchDone(doc));
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
                Log.e("Test Error","Error occur -> 持續搜尋:" + nowUrl);
            }
        }
    }
    private boolean isSearchDone(Document doc) throws InterruptedException {
        Elements nowElements = doc.select("tr[bgcolor=#d0d0d0]").select("td");
        for(Element now: nowElements) {
            if(now.text().equals("Expired CFPs")){
                return true;
            }
        }
        return false;
    }
    public Document getDoc() {
        return doc;
    }

    public ArrayList<Document> getDeepDoc(){
        return allDoc;
    }
}

