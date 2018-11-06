package app;


import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class App {

    public static void main(String args[]) {
        //Disable logging:
        ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("org.mongodb.driver").setLevel(Level.OFF  );
        ReadConcern rc; WriteConcern wc;
        if(args.length <= 0){
            System.out.println("this program take arg [concern]");
            return;
        }
        if(args[0].equals("local")){
            rc = ReadConcern.LOCAL;
            wc = WriteConcern.W1;
        } else if(args[0].equals("majority")) {
            rc = ReadConcern.MAJORITY;
            wc = WriteConcern.MAJORITY;
        } else {
            System.out.println("Concern has to be in either \"local\" or \"majority\"");
            return;
        }
        Connector.connect(rc, wc);
        Client.handleInput(args.length < 2 ? -1 : Integer.parseInt(args[1]));
        Client.printStats();
        Connector.close();
    }

}