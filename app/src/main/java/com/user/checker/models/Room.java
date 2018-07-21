package com.user.checker.models;


public class Room{
    public int room_id;
    public String s_id;

    public Room(int room_id,String s_id) {
        this.room_id = room_id;
        this.s_id = s_id;
    }

    @Override
    public String toString() {
        return "[" +
                "room_id=" + room_id +
                ", s_id='" + s_id + '\'' +
                ']';
    }
}
