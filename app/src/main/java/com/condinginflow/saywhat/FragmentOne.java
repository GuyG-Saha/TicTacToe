package com.condinginflow.saywhat;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentOne extends Fragment {
    private static final String TAG = "FRAGMENT_ONE";
    ArrayAdapter<String> myArrayAdapter;
    ArrayList<String> myArrayList;
    String[] records = {"Player1, Player2"};
    private sqliteDAO sqliteController = new sqliteDAO(getActivity());
    private SQLiteDatabase db;

    // Inflate the layout for this fragment
    private TextView text;

    public FragmentOne() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        myArrayList = new ArrayList<>();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ListView myListView = getView().findViewById(R.id.list);
        sqliteDAO sqliteController = new sqliteDAO(getActivity());
        db = sqliteController.getReadableDatabase();
        myArrayList = readFromDb();
        Log.i(TAG, myArrayList.size() + "");
        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, myArrayList);
        myListView.setAdapter(myArrayAdapter);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1, records);

        setListAdapter(adapter);
        //getListView().setOnItemClickListener(this);
    }



    public void setListAdapter(ArrayAdapter<String> listAdapter) {
        this.myArrayAdapter = listAdapter;
    }

    public ArrayAdapter<String> getListAdapter() {
        return myArrayAdapter;
    }

    public ArrayList<String> readFromDb() {
        Cursor cursor = db.rawQuery("SELECT * FROM " + sqliteDAO.FeedEntry.TABLE_NAME+";", null); // Read the player who won by date sort order(?)
        ArrayList<String> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndex(sqliteDAO.FeedEntry.COLUMN_NAME_TITLE));
            String itemIdDate = cursor.getString(
                    cursor.getColumnIndex(sqliteDAO.FeedEntry.COLUMN_NAME_SUBTITLE));

            Log.i(TAG, itemId + " " + parseDate(itemIdDate));
            itemIds.add(itemId + " " + parseDate(itemIdDate));
        }
        cursor.close();
        return itemIds;
    }

    private void truncateDBTable() {
        sqliteController.onUpgrade(db, 0, 0);
    }

    private String parseDate(String rawDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("EEE MMM dd HH:mm:ss zXXX yyyy");
            Date date = parser.parse(rawDate);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(date);
        } catch (ParseException e) {
            Log.e("ParseException", e.getMessage());
            String secondParse = rawDate.substring(0, 19).concat(rawDate.substring(29, 34));
            return secondParse;
        }
    }
}
