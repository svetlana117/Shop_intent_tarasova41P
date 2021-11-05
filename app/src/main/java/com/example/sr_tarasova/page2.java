package com.example.sr_tarasova;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class page2 extends AppCompatActivity implements View.OnClickListener{

    Button bOforml, bStore, bDB;
    TextView summ;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
        bOforml = (Button) findViewById(R.id.bOforml);
        bOforml.setOnClickListener(this);

        summ = (TextView) findViewById(R.id.summ);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        bStore = (Button) findViewById(R.id.bStore);
        bStore.setOnClickListener(this);

        bDB = (Button) findViewById(R.id.bDB);
        bDB.setOnClickListener(this);

        UpdateTable();
    }

    public void UpdateTable() {
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int tovarIndex = cursor.getColumnIndex(DBHelper.KEY_TOVAR);
            int pricelIndex = cursor.getColumnIndex(DBHelper.KEY_PRICE);
            TableLayout dbOutPut = findViewById(R.id.dbOutPut);
            dbOutPut.removeAllViews();
            do {
                TableRow dbOutPutRow = new TableRow(this);
                dbOutPutRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.MATCH_PARENT);
                TextView outputID = new TextView(this);
                params.weight = 1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutPutRow.addView(outputID);

                TextView outputTovar = new TextView(this);
                params.weight = 3.0f;
                outputTovar.setLayoutParams(params);
                outputTovar.setText(cursor.getString(tovarIndex));
                dbOutPutRow.addView(outputTovar);

                TextView outputPrice = new TextView(this);
                params.weight = 3.0f;
                outputPrice.setLayoutParams(params);
                outputPrice.setText(cursor.getString(pricelIndex));
                dbOutPutRow.addView(outputPrice);

                Button basketBtn = new Button(this);
                basketBtn.setOnClickListener(this);
                params.weight = 1.0f;
                basketBtn.setLayoutParams(params);
                basketBtn.setText("Корзина");
                basketBtn.setId(cursor.getInt(idIndex));
                dbOutPutRow.addView(basketBtn);


                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOutPutRow.addView(deleteBtn);


                dbOutPut.addView(dbOutPutRow);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {
        dbHelper = new DBHelper(this);
        switch (v.getId()) {

            case R.id.bStore:
                break;

            case R.id.bDB:
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
                break;

            case R.id.bOforml:
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сумма заказа: " + summ.getText(), Toast.LENGTH_SHORT);
                toast.show();
                summ.setText("0");
                break;

            default:
                Button b = (Button) v;
                switch (b.getText().toString()) {
                    case "Удалить":
                        View outputBDRow = (View) v.getParent();
                        ViewGroup outputBD = (ViewGroup) outputBDRow.getParent();
                        outputBD.removeView(outputBDRow);
                        outputBD.invalidate();
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{String.valueOf((v.getId()))});
                        Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                        contentValues = new ContentValues();
                        if (cursorUpdater.moveToFirst()) {
                            int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                            int tovarIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_TOVAR);
                            int pricelIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_PRICE);
                            int realID = 1;
                            do {
                                if (cursorUpdater.getInt(idIndex) > realID) {
                                    contentValues.put(DBHelper.KEY_ID, realID);
                                    contentValues.put(DBHelper.KEY_TOVAR, cursorUpdater.getString(tovarIndex));
                                    contentValues.put(DBHelper.KEY_PRICE, cursorUpdater.getString(pricelIndex));
                                    database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);

                                }
                                realID++;
                            } while (cursorUpdater.moveToNext());
                            if (cursorUpdater.moveToLast() && v.getId() != realID) {
                                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                            }
                            UpdateTable();
                        }
                        break;
                    case "Корзина":
                        String selection = "_id = ?";
                        Cursor cursorSumm = database.query(DBHelper.TABLE_CONTACTS, null, selection, new String[]{String.valueOf(v.getId())}, null, null, null);
                        float ObshSumma = Float.valueOf(summ.getText().toString());
                        float firstSumm = 0;
                        if (cursorSumm.moveToFirst()) {
                            int z = cursorSumm.getColumnIndex(DBHelper.KEY_PRICE);
                            do {
                                firstSumm = cursorSumm.getFloat(z);
                            } while (cursorSumm.moveToNext());
                        }
                        cursorSumm.close();
                        ObshSumma= ObshSumma + firstSumm;
                        summ.setText(String.valueOf(ObshSumma));
                        break;

                }
                dbHelper.close();

        }

    }
}