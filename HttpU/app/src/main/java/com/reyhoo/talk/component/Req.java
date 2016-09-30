package com.reyhoo.talk.component;

import com.google.gson.Gson;
import com.reyhoo.talk.util.GenerateUtil;

/**
 * Created by Administrator on 2016/7/13.
 */
public class Req<T> {
    public Req(Integer id){
        this.id = id;
    }
    public Req(){
        id = GenerateUtil.getID();
    }
    public Integer id;
    public String type;
    public T content;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Req<?> req = (Req<?>) o;

        return id.equals(req.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
