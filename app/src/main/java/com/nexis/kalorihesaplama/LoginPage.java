package com.nexis.kalorihesaplama;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText editTextEmail, editTextPassword;
    Button buttonLogin,buttonRegister;

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gerekli bileşenleri tanımlama
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister=findViewById(R.id.buttonRegister);

        // SQLite veritabanı yardımcısını başlatma
        dbHelper = new SQLiteOpenHelper(this, "UserDB", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                // Kullanıcı tablosunu oluşturma
                db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // Veritabanı sürümü yükseltildiğinde yapılacak işlemler
            }
        };

        // Giriş butonuna tıklama olayını dinleme
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kayıt ol ekranına geçmek için Intent oluştur
                Intent intent = new Intent(MainActivity.this, RegisterPage.class);
                startActivity(intent);
            }
        });


    }

    private void loginUser() {
        // E-posta ve şifreyi al
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        try {
            // Veritabanına bağlan
            database = dbHelper.getReadableDatabase();

            // Kullanıcıyı sorgula
            Cursor cursor = database.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

            // Kullanıcı bulunduysa
            if (cursor.getCount() > 0) {
                // Giriş başarılı mesajını göster
                Toast.makeText(getApplicationContext(), "Giriş başarılı!", Toast.LENGTH_SHORT).show();
            } else {
                // Kullanıcı bulunamadı mesajını göster
                Toast.makeText(getApplicationContext(), "Kullanıcı bulunamadı!", Toast.LENGTH_SHORT).show();
            }

            // Cursor ve veritabanını kapat
            cursor.close();
        }catch (SQLiteException e) {
            // Veritabanı erişiminde hata oluşması durumunda hata mesajı göster
            Toast.makeText(getApplicationContext(), "Veritabanı hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Herhangi bir hata oluşsa bile database bağlantısını kapat
            if (database != null) {
                database.close();
            }
        }
    }
}
