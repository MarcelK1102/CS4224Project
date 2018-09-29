package app;

import javafx.util.Pair;

import java.util.ArrayList;

public class popularItem {
    Integer O_ID;
    String O_ENTRY_D;
    String CName;
    ArrayList<Pair<String,Integer>> orders ;
    public popularItem(int O_ID, String O_ENTRY_D, String CName, ArrayList<Pair<String,Integer>> orders){
        this.O_ID=O_ID;
        this.O_ENTRY_D=O_ENTRY_D;
        this.CName=CName;
        this.orders=orders;
    }
}
