package com.user.checker.import_package;

import java.io.Serializable;

/**
 * Created by user on 2017-06-04.
 */

public class Index implements Serializable{

    public int x;

    public int y;

    public Index(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        try{
            return this.x == ((Index)obj).x&&this.y == ((Index)obj).y;
        }catch(Exception e){
            return false;
        }
    }
}
