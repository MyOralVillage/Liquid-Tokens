package org.myoralvillage.android.data.transaction;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MOVTransaction implements Comparable<MOVTransaction>{

    private String from;
    private String from_name;
    private String from_num;
    private String to_num;
    private String to;
    private String to_name;
    private int amount;
    private String currency;
    private long time;

    public void setFrom(String from) {
        this.from = from;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public String getFrom_name() {return from_name;
    }

    public String getTo() {
        return to;
    }

    public String getTo_name() {
        return to_name;
    }

    public int getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public long getTime() {
        return time;
    }
    public MOVTransaction(){
        this.from_name = null;
        this.from = ""+-1;
        this.to_name = null;
        this.to = ""+-1;
        this.amount = -1;
        this.currency = null;
        this.time = -1;
    }
    /* From https://stackoverflow.com/questions/6782185/convert-timestamp-long-to-normal-date-format
        Given a long representing the seconds since the Epoch (00:00:00 Thursday, 1 January 1970)
        Format it into a yyyy/MM/dd HH:mm:ss format for easy of printing to screen.
     */
    private String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return format.format(date);
    }
    @Override
    public String toString(){
        return getFrom()+" sent "+printAmount()+""+getCurrency()+"" +
                " to "+getTo()+" on "+convertTime(getTime())+".";

    }

    /*
        printAmount turns the int Amount representing that many cents into printable dollar form.

     */
    private String printAmount() {
        int am = getAmount();
        int amHigh = am/100;
        int amLow = am%100;
        String strAmLow;
        //for the case when amount ends in 0 add a 0 at the end
        // unless amount <100 which in 0.100
        if (amLow % 10 == 0 && am > 99){
            strAmLow = ""+amLow+"0";
        }
        else
            strAmLow =  ""+amLow;
        return ""+amHigh+"."+strAmLow;
    }

    @Override
    public int compareTo(MOVTransaction o) {
        if(o.getTime() == getTime()) return 0;

        return getTime() < o.getTime() ? 1 : -1;
    }

}
