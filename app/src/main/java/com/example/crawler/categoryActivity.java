package com.example.crawler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crawler.Adapter.CategoryAdapter;
import com.example.crawler.Fragment.ContentFragment;
import com.example.crawler.Fragment.chartFragment;
import com.example.crawler.Fragment.filterListFragment;
import com.example.crawler.util.Crawler;

import com.google.android.material.navigation.NavigationView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class categoryActivity extends AppCompatActivity
        implements filterListFragment.filterListInterface {

    private final String categoryUrl = "http://www.wikicfp.com/cfp/allcat";
    private ArrayList<String> prePickedString;
    private ArrayList<String> pickedString;
    private Vector<cardComponent> allInfo;

    private FragmentTransaction fragmentTransaction;
    public Fragment fragment;
    private DrawerLayout mainLayout;
    private NavigationView drawerView;
    private GridLayout pickedContainer;
    private Toolbar TopBar;
    private ProgressBar progressBar;

    private RecyclerView categoryContainer;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_page);

        allInfo=null;
        //add listener to mainLayout
        mainLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mainLayout.addDrawerListener(new ActionBarDrawerToggle(this, mainLayout ,
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

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //add side menu listener
        drawerView = (NavigationView) findViewById(R.id.side_menu);
        drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Log.i("Test", item.toString());
                switch (item.toString()) {
                    case "首頁":
                        Intent nextIntent = new Intent(categoryActivity.this,MainActivity.class);
                        startActivity(nextIntent);
                        break;
                    case "分類":
                        getChildData();
                        TopBar.setTitle("分類");
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragment = new filterListFragment();
                        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
                        fragmentTransaction.commit();
                        break;
                    case "統計圖表":
                        getChildData();
                        TopBar.setTitle("統計圖表");
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragment = new chartFragment();
                        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
                        fragmentTransaction.commit();
                        break;
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
        TopBar.setTitle("分類");
        TopBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.toString()) {
                    case "callSideMenu":
                        mainLayout.openDrawer(drawerView);
                        break;
                    case "favorite":
                    case "filter":
                        if(Objects.equals(item.toString(), "filter")) {
                            item.setTitle("favorite");
                            item.setIcon(R.drawable.favorite);
                        }
                        else {
                            item.setTitle("filter");
                            item.setIcon(R.drawable.filter_icon);
                        }
                        break;
                    default:
                        Log.i("Test", item.toString());
                        break;
                }
                return true;
            }
        });


        //default Fragment
        TopBar.setTitle(getIntent().getStringExtra("status"));
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(getIntent().getStringExtra("status").equals("統計圖表"))
            fragment = new chartFragment();
        else
            fragment = new filterListFragment();
        fragmentTransaction.replace(R.id.fragmentContainer,fragment);
        fragmentTransaction.commit();

        //fetch category
        Crawler crawler = new Crawler(categoryUrl);
        Thread fetchThread = new Thread(crawler);
        fetchThread.start();
        try {
            fetchThread.join();
        } catch (InterruptedException e) {
            Log.e("Error Test","獲取類別出錯");
        }
        Document doc = crawler.getDoc();
        Elements elements = doc.select(".contsec").select("td > a");
        //Log.i("Test Count",Integer.toString(elements.size()));
        ArrayList<String> allCategory = new ArrayList<>();

        for(Element temp : elements)
        {
            //Log.i("Test",temp.toString());
            allCategory.add(temp.text());
        }

        //put it into RecyclerView
        categoryContainer = findViewById(R.id.categoryContainer);
        categoryContainer.setLayoutManager(new GridLayoutManager(this,1));
        categoryContainer.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        categoryAdapter = new CategoryAdapter(allCategory,this);
        categoryContainer.setAdapter(categoryAdapter);

        //picked category container
        pickedContainer = findViewById(R.id.pickedCategory);
        pickedString = new ArrayList<>();

        //filter by searchView
        SearchView searchView = TopBar.findViewById(R.id.searchInput);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                categoryAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryContainer.setVisibility(View.VISIBLE);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.hide(fragment);
                fragmentTransaction.commit();
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                searchView.setQuery("",false);

                if(!searchView.isIconified())
                    searchView.onActionViewCollapsed();

                progressBar.setVisibility(View.VISIBLE);

                categoryContainer.setVisibility(View.INVISIBLE);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.show(fragment);
                fragmentTransaction.commit();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).start();


                return true;
            }
        });



    }

    private void pickedStringToUrlString() {
        for(int i=0;i<pickedString.size();++i)
        {
            String temp = pickedString.get(i);
            temp = temp.replaceFirst("\\s+","%20");
            pickedString.set(i,"http://www.wikicfp.com/cfp/call?conference="+temp);
            //Log.i("Test",pickedString.get(i));
        }
    }

    @Override
    public ArrayList<String> getUrlArray() {
        return pickedString;
    }

    @Override
    public Vector<cardComponent> getData() {
        return allInfo;
    }

    public void updateList() {
        prePickedString = new ArrayList<>();
        for(String str:pickedString)
            prePickedString.add(str);
        pickedString.clear();
        for(int i=0;i<pickedContainer.getChildCount();++i)
            pickedString.add((String)((Button) pickedContainer.getChildAt(i)).getText());

        pickedStringToUrlString();
        //Log.i("Test",Integer.toString(prePickedString.size())+" to "+Integer.toString(pickedString.size()));
        if(isPickTagSame(pickedString,prePickedString))
            return;
        if(fragment instanceof filterListFragment)
            ((filterListFragment) fragment).updateList();
        else if(fragment instanceof chartFragment)
            ((chartFragment)fragment).updateChart();
    }

    private boolean isPickTagSame(ArrayList<String> a,ArrayList<String> b) {
        if(a.size()!=b.size())
            return false;
        for(String str : a)
        {
            boolean hasSame=false;
            for(String cmp: b) {
                if(cmp.equals(str)) {
                    hasSame=true;
                    break;
                }
            }
            if(!hasSame) return false;
        }
        return true;
    }
    private void getChildData() {
        if(fragment instanceof filterListFragment)
            allInfo=((filterListFragment)fragment).getData();
        else if(fragment instanceof chartFragment)
            allInfo=((chartFragment)fragment).getData();
    }


    public void changeFragment(Class fragmentClass,String url) {
        Log.i("Test","Category clicked");
        Fragment nowFragment = null;
        if (fragmentClass.equals(ContentFragment.class)) {
            nowFragment = (Fragment) ContentFragment.newInstance(url);
        }

        if(fragment!=null) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.hide(fragment);
            fragmentTransaction.add(R.id.fragmentContainer, nowFragment)
                    .commit();
        }
    }
}