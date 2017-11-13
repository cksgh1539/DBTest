package com.example.hp.dbtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_CODE_READ_CONTACTS = 1;

    EditText mId;
    EditText mName;
    EditText mPhone;

    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mId = (EditText)findViewById(R.id._id);
        mName = (EditText)findViewById(R.id.edit_name);
        mPhone = (EditText)findViewById(R.id.edit_phone);

        mDbHelper = new DBHelper(this);

        // 권한 확인
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) { // 권한이 없으므로, 사용자에게 권한 요청 다이얼로그 표시
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        } else // 권한 있음! 해당 데이터나 장치에 접근!
            viewAllToListView();

        Button button = (Button)findViewById(R.id.veiwall);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                viewAllToListView();
            }
        });

        Button button1 = (Button)findViewById(R.id.insert);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecord();
                viewAllToListView();
            }
        });

        Button button2 = (Button)findViewById(R.id.delete);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord();
                viewAllToListView();
            }
        });

        Button button3 = (Button)findViewById(R.id.update);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRecord();
                viewAllToListView();
            }
        });

        Button button4 = (Button)findViewById(R.id.contacts);
        button4.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                getContacts();
            }
        });

    }
    private void getContacts() {
        String [] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        // 연락처 전화번호 타입에 따른 행 선택을 위한 선택 절
        String selectionClause = ContactsContract.CommonDataKinds.Phone.TYPE + " = ? ";

        // 전화번호 타입이 'MOBILE'인 것을 지정
        String[] selectionArgs = {""+ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE};

        Cursor c = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,  // 조회할 데이터 URI
                projection,         // 조회할 컬럼 들
                selectionClause,    // 선택될 행들에 대한선택될 행들에 대한 조건절
                selectionArgs,      // 조건절에 필요한 파라미터
                null);              // 정렬 안

        String[] contactsColumns = { // 쿼리결과인 Cursor 객체로부터 출력할 열들
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        int[] contactsListItems = { // 열의 값을 출력할 뷰 ID (layout/item.xml 내)
                R.id.name,
                R.id.phone
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.item,
                c,
                contactsColumns,
                contactsListItems,
                0);

        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(adapter);

              Cursor cursor = mDbHelper.getAllUsersByMethod();
                boolean A = true;


        while(c.moveToNext()==true) {
            String name = c.getString
                    (c.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.DISPLAY_NAME));
            String number = c.getString
                    (c.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.NUMBER));

                Toast.makeText(this, "비어있음", Toast.LENGTH_SHORT).show();
            while(cursor.moveToNext()==true) {

                if (number.equals(cursor.getString(2)) ) {
                    Toast.makeText(this, number +" 겹친다  "+cursor.getString(2), Toast.LENGTH_SHORT).show();
                    A = false;

                }else if(number != cursor.getString(2)){
                    Toast.makeText(this, number + " 안겹친다  " + cursor.getString(2), Toast.LENGTH_SHORT).show();
                }
            }
            cursor.moveToPosition(-1);
            if(A ==true)
                mDbHelper.insertUserByMethod(name, number);
            A = true;
        }
    }

    private void viewAllToListView() {

        Cursor cursor = mDbHelper.getAllUsersByMethod();

        android.widget.SimpleCursorAdapter adapter = new android.widget.SimpleCursorAdapter(getApplicationContext(),
                R.layout.item, cursor, new String[]{
                UserContract.Users._ID,
                UserContract.Users.KEY_NAME,
                UserContract.Users.KEY_PHONE},
                new int[]{R.id._id, R.id.name, R.id.phone}, 0);



     //   String a = cursor.getString(cursor.getColumnIndex(UserContract.Users.KEY_PHONE));


       // Toast.makeText(this,a, Toast.LENGTH_SHORT).show();

        ListView lv = (ListView)findViewById(R.id.listview);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Adapter adapter = adapterView.getAdapter();

                mId.setText(((Cursor)adapter.getItem(i)).getString(0));
                mName.setText(((Cursor)adapter.getItem(i)).getString(1));
                mPhone.setText(((Cursor)adapter.getItem(i)).getString(2));
            }
        });
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void updateRecord() {
        EditText _id = (EditText)findViewById(R.id._id);
        EditText name = (EditText)findViewById(R.id.edit_name);
        EditText phone = (EditText)findViewById(R.id.edit_phone);

        long nOfRows = mDbHelper.updateUserByMethod(_id.getText().toString(),
                name.getText().toString(),
                phone.getText().toString());
        if (nOfRows >0)
            Toast.makeText(this,"Record Updated", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"No Record Updated", Toast.LENGTH_SHORT).show();
    }

    private void deleteRecord() {
        EditText _id = (EditText)findViewById(R.id._id);

        long nOfRows = mDbHelper.deleteUserByMethod(_id.getText().toString());
        if (nOfRows >0)
            Toast.makeText(this,"Record Deleted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"No Record Deleted", Toast.LENGTH_SHORT).show();
    }

    private void insertRecord() {

        EditText name = (EditText)findViewById(R.id.edit_name);
        EditText phone = (EditText)findViewById(R.id.edit_phone);

        long nOfRows = mDbHelper.insertUserByMethod(name.getText().toString(),phone.getText().toString());

        if (nOfRows >0)
            Toast.makeText(this,nOfRows+" Record Inserted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"No Record Inserted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                Toast.makeText(getApplicationContext(), "READ_CONTACTS 접근 권한이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
