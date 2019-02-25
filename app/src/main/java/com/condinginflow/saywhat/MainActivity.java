package com.condinginflow.saywhat;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SIZE = 3;
    private sqliteDAO sqliteController;
    private SQLiteDatabase db;
    private ContentValues values;
    private Button[][] Buttons = new Button[SIZE][SIZE];
    private boolean player1Turn = true; //X begins
    private int countRounds = 0;
    private int player1points;
    private int player2points;
    private TextView textPlayer1, textPlayer2;
    private static Context context;
    private List listFromDb = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();
        sqliteController = new sqliteDAO(getAppContext());
        textPlayer1 = findViewById(R.id.text_view_p1);
        textPlayer2 = findViewById(R.id.text_view_p2);
       // setContentView(R.layout.news_arcticle);
        // Gets the data repository in write mode
        db = sqliteController.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        values = new ContentValues();


        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                Buttons[i][j] = findViewById(resID);
                Buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                resetBoard();
            }
        });

        Button buttonRead = findViewById(R.id.button_read_db);
        buttonRead.setOnClickListener((v) -> { listFromDb = readFromDb();
        for (int i = 0; i < listFromDb.size(); i++)
        Log.d( "row:", listFromDb.get(i).toString() );

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentOne f1 = new FragmentOne();
        fragmentTransaction.add(R.id.fragment_container, f1);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        } );
    }

    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) { // Used button clicked
            return;
        } else {
            if (player1Turn) {
                ((Button) v).setText(cellState.X.toString());
            } else if (!player1Turn) {
                ((Button) v).setText(cellState.O.toString());
            }
        }

        countRounds++;
        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else if (!player1Turn) {
                player2Wins();
            }
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(sqliteDAO.FeedEntry.TABLE_NAME, null, values);
            Toast.makeText(this, "New Row Id from SQLITE: " + newRowId, Toast.LENGTH_SHORT).show();
        }  else {
                if (countRounds == SIZE*SIZE)
                    draw();
                else
                    player1Turn = !player1Turn;
            }
    }

    private boolean checkForWin() {
        String[][] field = new String[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                field[i][j] = Buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < SIZE; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < SIZE; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void player1Wins() {
        player1points++;
        values.put(sqliteDAO.FeedEntry.COLUMN_NAME_TITLE, "Player1");
        values.put(sqliteDAO.FeedEntry.COLUMN_NAME_SUBTITLE, new Date().toString());
        Toast.makeText(this, "Player 1 Won!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void player2Wins() {
        player2points++;
        values.put(sqliteDAO.FeedEntry.COLUMN_NAME_TITLE, "Player2");
        values.put(sqliteDAO.FeedEntry.COLUMN_NAME_SUBTITLE, new Date().toString());
        Toast.makeText(this,  "Player 2 Won!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        resetBoard();
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
    }

    private void updatePointsText() {
        textPlayer1.setText("Player 1 " + player1points);
        textPlayer2.setText("Player 2 " + player2points);
    }

    private void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                Buttons[i][j].setText(""); // Reset all buttons to show empty txt
            }
        }

        countRounds = 0;
        player1Turn = true;
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public List readFromDb() {
        SQLiteDatabase db = sqliteController.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                sqliteDAO.FeedEntry.COLUMN_NAME_TITLE,
                sqliteDAO.FeedEntry.COLUMN_NAME_SUBTITLE
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = sqliteDAO.FeedEntry.COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "Player1" };
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                sqliteDAO.FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor cursor = db.rawQuery("SELECT * FROM " + sqliteDAO.FeedEntry.TABLE_NAME+";", null); // Read the player who won by date sort order(?)
        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            String itemId = cursor.getString(
                    cursor.getColumnIndex(sqliteDAO.FeedEntry.COLUMN_NAME_TITLE));
            itemIds.add(itemId);
        }
        cursor.close();
        return itemIds;
    }
}
