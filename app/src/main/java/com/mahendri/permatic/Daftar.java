package com.mahendri.permatic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mahendri on 2/3/2017.
 * Model for daftar Tonas
 */

@SuppressWarnings("WeakerAccess")
public class Daftar {

    public String uid;
    public String nama;
    public String sekolah;
    public int paket;

    @SuppressWarnings("unused")
    public Daftar() {
        //Default const
    }

    public Daftar(String uid, String nama, String sekolah, int paket){
        this.uid = uid;
        this.nama = nama;
        this.sekolah = sekolah;
        this.paket = paket;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("nama", nama);
        result.put("sekolah", sekolah);
        result.put("paket", paket);

        return result;
    }
}
