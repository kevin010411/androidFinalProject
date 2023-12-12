package com.example.crawler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.crawler.Fragment.ContentFragment;
import com.example.crawler.util.Crawler;
import com.example.crawler.util.util;
import com.google.android.material.navigation.NavigationView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private final String HomeUrl = "http://www.wikicfp.com/cfp/";
    private Crawler crawler;
    private DrawerLayout mainLayout;
    private NavigationView drawerView;
    public LinearLayout cardContainer;
    private Toolbar TopBar;
    public int count=0;
    public boolean[]love;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        Intent initIntent=getIntent();
        count=initIntent.getIntExtra("count",20);
        love=new boolean[count];
        Bundle allData=initIntent.getBundleExtra("allData");
        if(allData!=null){
            Bundle loveData=allData.getBundle("love");
            for(int i=0;i<count;i++)
                love[i]=loveData.getBoolean(String.valueOf(i));
        }

        //fetch web data
        crawler = new Crawler(HomeUrl);
        Vector<cardComponent> allCard = new Vector<cardComponent>();
        if(util.isNetworkAvailable(this)) {
            Thread crawlerThread = new Thread(crawler);
            crawlerThread.start();
            try {
                crawlerThread.join();
            } catch (InterruptedException e) {
                Log.i("Error Test","等不到爬蟲好");
            }
            //show on Home page
            Document doc = crawler.getDoc();
            cardContainer = (LinearLayout) findViewById(R.id.card_container);
            Elements form = doc.select(".contsec").select("td > a");
            for (Element temp : form) {
                count+=1;
                cardComponent tempComp = new cardComponent(MainActivity.this);
                tempComp.setTitleText(temp.text());
                tempComp.setContentURL("http://www.wikicfp.com"+temp.attr("href"));
                allCard.add(tempComp);
                //Log.i("Test",temp.text());
            }
            Elements deadLine = doc.select("form[name='myform']").select("td > table").get(1).select("tr > td");
            //Log.i("Test",Integer.toString(deadLine.size())+" "+Integer.toString(form.size()));
            for (int i = 5, now = 0; i < deadLine.size(); ++i) {
                //Log.i("Test",Integer.toString(i-5));
                //Log.i("Test",Integer.toString(i-5)+" "+deadLine.get(i).toString());
                if ((i - 5) % 6 == 3)
                    allCard.elementAt(now).setWhen(deadLine.get(i).text());
                else if ((i - 5) % 6 == 4)
                    allCard.elementAt(now).setWhere(deadLine.get(i).text());
                else if ((i - 5) % 6 == 5) {
                    allCard.elementAt(now).setDeadLine(deadLine.get(i).text());
                    now++;
                }
            }
            for (cardComponent card : allCard) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        cardContainer.addView(card, new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                140
                        ));
                    }
                });
            }
        }
        //Log.i("Tested", form.toString());

        //add listener to mainLayout
        mainLayout = (DrawerLayout) findViewById(R.id.mainLayout);
        mainLayout.addDrawerListener(new ActionBarDrawerToggle(this, mainLayout,
                R.string.sideMenuOpen, R.string.sideMenuClose) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        });

        //add side menu listener
        drawerView = (NavigationView) findViewById(R.id.SideMenu);
        drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("Test", item.toString());
                switch (item.toString()) {
                    case "首頁":
                        Log.i("Test", "從首頁至首頁無事發生");
                        break;
                    case "分類":
                    case "統計圖表":
                        Intent nextIntent = new Intent(MainActivity.this,categoryActivity.class);
                        int index=0;
                        Bundle allData=new Bundle();
                        Bundle loveData=new Bundle();
                        for(cardComponent temp:allCard) {
                            Log.i("allCard",String.valueOf(index)+String.valueOf(temp.love));
                            loveData.putBoolean(String.valueOf(allCard.get(index++).love), temp.love);
                        }
                        allData.putBundle("love",loveData);
                        nextIntent.putExtra("allData",allData);
                        nextIntent.putExtra("status",item.toString());
                        nextIntent.putExtra("count",count);
                        startActivity(nextIntent);
                        finish();
                        break;
                    case "關於":
                        nextIntent = new Intent(MainActivity.this,aboutpageActivity.class);
                        startActivity(nextIntent);
                    default:
                        Log.e("未知去向", "前往未知去向");
                        break;
                }
                mainLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //show TopBar
        TopBar = (Toolbar) findViewById(R.id.TopBar);
        TopBar.setTitle("首頁");
        TopBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.toString()) {
                    case "callSideMenu":
                        mainLayout.openDrawer(drawerView);
                        break;
                    default:
                        Log.i("Test", item.toString());
                        break;
                }
                return true;
            }
        });
    }


    public void changeFragment(Class fragmentClass,String url) {
        Fragment fragment = null;
        if (fragmentClass.equals(ContentFragment.class)) {
            fragment = (Fragment) ContentFragment.newInstance(url);
        }

        if(fragment!=null) {
            cardContainer.setVisibility(View.INVISIBLE);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.ContentView, fragment)
                    .commit();
        }
    }
}




