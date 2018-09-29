package app;


import java.util.ArrayList;

public class popularItem {
    Integer O_ID;
    String O_ENTRY_D;
    String CName;
    ArrayList<String> orders ;
    public popularItem(int O_ID, String O_ENTRY_D, String CName, ArrayList<String> orders){
        this.O_ID=O_ID;

        this.O_ENTRY_D=O_ENTRY_D;
        this.CName=CName;
        this.orders=orders;
    }
}
