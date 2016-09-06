package com.example.pening.qb;

/**
 * Created by pening on 2016-06-28.
 */
public class Data {
    private String id;
    private String contents;
    private int bitcount;
    private int score;
    private String time;
    private String time_id;
    private String score_id;
    private int use_count;

    public void Data(){
        contents = null;
        bitcount = 0;
        score = 0;
        time = "00:00";
    }
    public void setData(String contents, int bitcount, int score, String time){
        this.contents = contents;
        this.bitcount = bitcount;
        this.score = score;
        this.time = time;
    }
    public void setId(String id){
        this.id = id;
    }
    public void setUsecount(int usecount){
        this.use_count = usecount;
    }
    public void setScore_time(String score_id, String time_id){
        this.score_id = score_id;
        this.time_id = time_id;
    }
    public String getId(){
        return id;
    }
    public String getContents(){
        return contents;
    }
    public int getBitcount(){
        return bitcount;
    }
    public int getScore(){
        return score;
    }
    public String getTime(){
        return time;
    }
    public String getScore_id(){
        return score_id;
    }
    public String getTime_id(){
        return time_id;
    }
    public int getUse_count(){
        return use_count;
    }
}
