package com.mahendri.permatic;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mahendri on 2/8/2017.
 * to populate view
 */

@SuppressWarnings("WeakerAccess")
public class DaftarViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView sekolahView;
    public TextView paketView;

    public DaftarViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.name_text);
        sekolahView = (TextView) itemView.findViewById(R.id.sekolah_text);
        paketView = (TextView) itemView.findViewById(R.id.paket_text);
    }

    public void setViewHolder(Context context, Daftar daftar){
        nameView.setText(daftar.nama);
        sekolahView.setText(daftar.sekolah);
        int paket = daftar.paket;
        switch (paket){
            case 1:
                paketView.setText(context.getString(R.string.paket1));
                break;
            case 2:
                paketView.setText(context.getString(R.string.paket2));
                break;
            default:
                paketView.setText(String.valueOf(daftar.paket));
        }
    }
}
