package com.example.a612;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences myListPref;
    private List<Map<String, String>> simpleAdapterContent = new ArrayList<>();
    private List<Integer> deletePosition = new ArrayList<>();
    public static final String DELETE_LIST = "deleteList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        final ListView list = findViewById(R.id.list);
        final SwipeRefreshLayout swipeLayout = findViewById(R.id.swipeRefresh);
        setSupportActionBar(toolbar);

        myListPref = getSharedPreferences("List", MODE_PRIVATE);
        prepareContent();

        final SimpleAdapter listContentAdapter = createAdapter(simpleAdapterContent);

        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                simpleAdapterContent.remove(position);
                deletePosition.add(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareContent();
                listContentAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList(DELETE_LIST, (ArrayList<Integer>) deletePosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<Integer> list = savedInstanceState.getIntegerArrayList(DELETE_LIST);
        for (int i = 0; i < list.size(); i++) {
            int position = list.get(i);
            simpleAdapterContent.remove(position);
        }
    }

    @NonNull
    private SimpleAdapter createAdapter(List<Map<String, String>> data) {
        return new SimpleAdapter(this, data, R.layout.list,
                new String[]{"text_1", "text_2"}, new int[]{R.id.text_1, R.id.text_2});

    }

    @NonNull
    private void prepareContent() {
        if (!myListPref.contains("myList")) {

            String[] arrayContent = getString(R.string.large_text).split("\n\n");

            String content = getString(R.string.large_text);
            SharedPreferences.Editor myEditor = myListPref.edit();
            myEditor.putString("myList", content);
            myEditor.apply();

            for (String array : arrayContent) {
                Map<String, String> map = new HashMap<>();
                map.put("text_1", array);
                map.put("text_2", Integer.toString(array.length()));
                simpleAdapterContent.add(map);
            }
        } else {
            simpleAdapterContent.clear();
            String content = myListPref.getString("myList", "");
            String[] arrayContent = content.split("\n\n");

            for (String array : arrayContent) {
                Map<String, String> map = new HashMap<>();
                map.put("text_1", array);
                map.put("text_2", Integer.toString(array.length()));
                simpleAdapterContent.add(map);
            }
        }
    }
}