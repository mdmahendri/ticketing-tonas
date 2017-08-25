package com.mahendri.permatic;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView mRecycler;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Daftar, DaftarViewHolder> mAdapter;

    private EditText mNamaField;
    private EditText mSekolahField;
    private EditText mPaketField;
    private Daftar mDaftar;
    private String mKeyDaftar;
    private boolean mCheckNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecycler = (RecyclerView) findViewById(R.id.list_daftar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FloatingActionButton newEntryFAB = (FloatingActionButton) findViewById(R.id.fab_new_entry);

        newEntryFAB.setOnClickListener(this);
        mRecycler.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        LinearLayoutManager mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);
        DividerItemDecoration divider = new DividerItemDecoration(this, mManager.getOrientation());
        mRecycler.addItemDecoration(divider);

        Query query = mDatabase.child("daftar-anggota").child(getUid());
        mAdapter = new FirebaseRecyclerAdapter<Daftar, DaftarViewHolder>(Daftar.class,
                R.layout.item_daftar, DaftarViewHolder.class, query) {
            @Override
            protected void populateViewHolder(DaftarViewHolder viewHolder,
                                              Daftar model, int position) {
                viewHolder.setViewHolder(getApplicationContext(), model);
            }
        };
        mRecycler.setAdapter(mAdapter);
        mRecycler.addOnItemTouchListener(new RecyclerClickListener(this,
                new RecyclerClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mCheckNew = false;
                        DatabaseReference reference = mAdapter.getRef(position);
                        mKeyDaftar = reference.getKey();
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mDaftar = dataSnapshot.getValue(Daftar.class);
                                showForm();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.v("MainData", databaseError.getMessage());
                            }
                        });
                    }
                }));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mAdapter != null){
            mAdapter.cleanup();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_keluar){
            FirebaseAuth.getInstance().signOut();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_new_entry:
                mCheckNew = true;
                showForm();
                break;
        }
    }

    private void showForm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogButtonTheme);
        View view = View.inflate(this, R.layout.dialog_add, null);
        mNamaField = (EditText) view.findViewById(R.id.namaInput);
        mSekolahField = (EditText) view.findViewById(R.id.sekolahInput);
        mPaketField = (EditText) view.findViewById(R.id.paketInput);
        if (!mCheckNew){
            mNamaField.setText(mDaftar.nama);
            mSekolahField.setText(mDaftar.sekolah);
            mPaketField.setText(String.valueOf(mDaftar.paket));
        }
        builder.setView(view)
                .setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        registerPendaftar();
                    }
                })
                .setNegativeButton("Batal", null);
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void registerPendaftar(){
        final String nama = mNamaField.getText().toString().trim();
        final String sekolah = mSekolahField.getText().toString().trim();
        final String paket = mPaketField.getText().toString().trim();
        final int paketNumb;

        if (nama.isEmpty()){
            mNamaField.setError("isi nama");
            return;
        }

        if (sekolah.isEmpty()){
            mSekolahField.setError("isi sekolah");
            return;
        }

        if (paket.isEmpty()){
            mPaketField.setError("isi dulu");
            return;
        } else {
            paketNumb = Integer.parseInt(paket);
        }

        setEditingEnabled(false);
        showProgressDialog("Proses. . .");

        final String userId = getUid();
        mDatabase.child("anggota").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String email = dataSnapshot.toString();

                        if (email == null){
                            Toast.makeText(MainActivity.this, "Fetch user gagal",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeDaftar(userId, nama, sekolah, paketNumb);
                        }

                        setEditingEnabled(true);
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setEditingEnabled(true);
                        hideProgressDialog();
                    }
                }
        );
    }

    private void setEditingEnabled(boolean enabled) {
        mNamaField.setEnabled(enabled);
        mSekolahField.setEnabled(enabled);
        mPaketField.setEnabled(enabled);
    }

    private void writeDaftar(String userId, String nama, String sekolah, int paket){
        if (mCheckNew){
            mKeyDaftar = mDatabase.child("daftar").push().getKey();
        }
        Daftar daftar = new Daftar(userId, nama, sekolah, paket);
        Map<String, Object> daftarVal = daftar.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/daftar/" + mKeyDaftar, daftarVal);
        childUpdates.put("/daftar-anggota/" + userId + "/" + mKeyDaftar, daftarVal);

        mDatabase.updateChildren(childUpdates);
    }
}
