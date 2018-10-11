package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
	public static int nxact = 0;
    public static long startTime;
    public static long endTime;
    public static int counts[] = new int[256];
    public static long times[] = new long[256];
    //Task 2 take input
    public static void handleInput(int limit) {
        try{
            char []buf = new char[2];
            startTime = System.currentTimeMillis();
            while(bi.read(buf, 0, 2) > 0 && limit-- != 0){
                System.out.println(buf[0]);
                long start = System.currentTimeMillis();
                fs.get(buf[0]).run();
                times[buf[0]] += System.currentTimeMillis() - start; 
                counts[buf[0]]++;
                nxact++;
            }
            endTime = System.currentTimeMillis();
        }catch(IOException ioe){System.out.println("Unable to handle input: "); ioe.printStackTrace();}
        System.out.println("Ended input");
	}
	
	public static void printStats() {
		double timeSeconds = (endTime - startTime) / 1000.0;
		System.out.println("#!#!STATS: number of transactions : " + nxact + ", total transaction execution time : " + timeSeconds + " seconds, transaction throughput : " + ( (nxact)/timeSeconds) );
		for(char c :fs.keySet()){
			timeSeconds = times[c]/1000.0;
			System.out.printf("Transaction %c was executed %d times, took a total of %f seconds, average time per transaction was %f\n",
				c,
				counts[c],
				timeSeconds,
				timeSeconds > 0.0 ? counts[c]/timeSeconds : 0.0
			);
		}
	}

	private static BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
    public static final Map<Character, Runnable> fs = new HashMap<>();
    static {
        //Transaction (a)
        fs.put('N', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int cid = Integer.parseInt(input[0]);
            int wid = Integer.parseInt(input[1]);
            int did = Integer.parseInt(input[2]);
            int m = Integer.parseInt(input[3]);
            List<Integer> ids = new ArrayList<>(m);
            List<Integer> wids = new ArrayList<>(m);
            List<Integer> quantities = new ArrayList<>(m);
            for(int i = 0; i < m; i++){
                try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
                ids.add(i, Integer.parseInt(input[0]));
                wids.add(i, Integer.parseInt(input[1]));
                quantities.add(i, Integer.parseInt(input[2]));
            }
            try { Transaction.newOrder(wid, did, cid, ids, wids, quantities); }
            catch(Exception e){ System.out.println("Unable to perform newOrder transaction" ); e.printStackTrace();}
        });

        // //Transaction (b)
        fs.put('P', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int cid = Integer.parseInt(input[2]);
            BigDecimal payment = new BigDecimal(input[3]);
            try { Transaction.paymentTransaction(wid, did, cid, payment); }
            catch(Exception e){ System.out.println("Unable to perform payment transaction"); e.printStackTrace();}
        });

        // //Transaction (c)
        fs.put('D', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int carrierid = Integer.parseInt(input[1]);
            try{ Transaction.processDelivery(wid, carrierid); }
            catch(Exception e) { System.out.println("Unable to perform Delivery transaction"); e.printStackTrace(); }
        });

        // //Transaction (d)
        fs.put('O', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int cid = Integer.parseInt(input[2]);
            try{ Transaction.getOrderStatus(wid, did, cid); }
            catch(Exception e) { System.out.println("Unable to perform Order-Status" ); e.printStackTrace(); }
        });

        // //Transaction (e)
        fs.put('S', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            long T = Long.parseLong(input[2]);
            int L = Integer.parseInt(input[3]);
            try{ Transaction.stockLevel(wid, did, T, L); }
            catch(Exception e) { System.out.println("Unable to perform Stock-Level"); e.printStackTrace();}
        });

        // //Transaction (f)
        fs.put('I', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int L = Integer.parseInt(input[2]);
            try{ Transaction.popularItem(wid, did, L); }
            catch(Exception e) { System.out.println("Unable to perform Popular-Item transaction"); e.printStackTrace();}
        });

        // //Transaction (g)
        fs.put('T', () -> {
            Transaction.topBalance();
        });

        // //Transaction (h)
        fs.put('R', () -> {
            String []input;
            try{input = bi.readLine().split(",");} catch(IOException ioe) {ioe.printStackTrace(); return;}
            int wid = Integer.parseInt(input[0]);
            int did = Integer.parseInt(input[1]);
            int cid = Integer.parseInt(input[2]);
            try{ Transaction.relatedCustomer(wid, did, cid); }
            catch(Exception e) { System.out.println("Unable to perform Related-Customer"); e.printStackTrace();}
        });
    }
}