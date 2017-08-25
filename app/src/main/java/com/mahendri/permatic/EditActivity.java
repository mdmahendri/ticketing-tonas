package com.mahendri.permatic;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditActivity extends BaseActivity {

    private DatabaseReference mDatabase;

    private EditText mNamaField;
    private EditText mSekolahField;
    private EditText mPaketField;
    private FloatingActionButton mDoneButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mNamaField = (EditText) findViewById(R.id.inputNama);
        mSekolahField = (EditText) findViewById(R.id.inputSekolah);
        mPaketField = (EditText) findViewById(R.id.inputPaket);
        mDoneButton = (FloatingActionButton) findViewById(R.id.fab_done_entry);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPendaftar();
            }
        });
    }

    private void registerPendaftar(){
        final String nama = mNamaField.getText().toString().trim();
        final String sekolah = mSekolahField.getText().toString().trim();
        final String paket = mPaketField.getText().toString().trim();
        final int paketNumb;

        if (nama.isEmpty()){
            mNamaField.setError("isi dulu");
            return;
        }

        if (sekolah.isEmpty()){
            mSekolahField.setError("isi dulu");
            return;
        }

        if (paket.isEmpty()){
            mPaketField.setError("isi 1 atau 2");
            return;
        } else {
            paketNumb = Integer.parseInt(paket);
        }

        setEditingEnabled(false);
        Toast.makeText(this, "Proses", Toast.LENGTH_SHORT).show();

        final String userId = getUid();
        mDatabase.child("anggota").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.toString();

                        if (email == null){
                            Toast.makeText(EditActivity.this, "Fetch user gagal", Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewDaftar(userId, nama, sekolah, paketNumb);
                        }

                        setEditingEnabled(true);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setEditingEnabled(true);
                    }
                }
        );
    }

    private void setEditingEnabled(boolean enabled) {
        mNamaField.setEnabled(enabled);
        mSekolahField.setEnabled(enabled);
        mPaketField.setEnabled(enabled);
        if (enabled) {
            mDoneButton.setVisibility(View.VISIBLE);
        } else {
            mDoneButton.setVisibility(View.GONE);
        }
    }

    private void writeNewDaftar(String userId, String nama, String sekolah, int paket){
        String key = mDatabase.child("daftar").push().getKey();
        Daftar daftar = new Daftar(userId, nama, sekolah, paket);
        Map<String, Object> daftarVal = daftar.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/daftar/" + key, daftarVal);
        childUpdates.put("/daftar-anggota/" + userId + "/" + key, daftarVal);

        mDatabase.updateChildren(childUpdates);
    }
}
