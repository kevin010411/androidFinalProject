package com.example.crawler;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.navigation.NavigationView;

public class aboutpageActivity extends AppCompatActivity {


    private DrawerLayout mainLayout;
    private NavigationView drawerView;
    private Toolbar TopBar;

    public TextView contact;

    public TextView contact2;
    public TextView ref;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutpage);

        contact=(TextView)findViewById(R.id.contact);
        contact2=(TextView)findViewById(R.id.contact2);
        ref=(TextView)findViewById(R.id.ref);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                TextView con=(TextView) view;
                Uri webpage = Uri.parse("mailto:s1101512@mail.yzu.edu.tw");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });
        contact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView con=(TextView) view;
                Uri webpage = Uri.parse("mailto:s1103301@mail.yzu.edu.tw");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView con=(TextView) view;
                Uri webpage = Uri.parse("http://www.wikicfp.com/cfp/");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);
            }
        });
        mainLayout = (DrawerLayout) findViewById(R.id.main_Layout);
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

        drawerView = (NavigationView) findViewById(R.id.SideMenu);
        drawerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.i("Test", item.toString());
                switch (item.toString()) {
                    case "首頁":
                        Intent nextIntent = new Intent(aboutpageActivity.this,MainActivity.class);
                        startActivity(nextIntent);
                        break;
                    case "分類":
                    case "統計圖表":
                        nextIntent = new Intent(aboutpageActivity.this,categoryActivity.class);
                        nextIntent.putExtra("status",item.toString());
                        startActivity(nextIntent);
                        finish();
                        break;
                    case "關於":
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
        TopBar.setTitle("關於");
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


}
