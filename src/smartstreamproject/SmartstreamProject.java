/*
 * SmartstreamProject - Some simple read/write file/database Java code
 * 
 * Copyright (C) 2014  Oscar Harrup
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package smartstreamproject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oscar
 */
public class SmartstreamProject {

    /**
     * @param args the command line arguments
     */    
    public static final Logger LOG 
            = Logger.getLogger(SmartstreamProject.class.getName());
    
    private static String DBname, DBuser, DBpass, peoplefile, ordersfile,
            schema;   
    
    private static OrderReader oreader;
    private static PersonReader preader;
    
    private static DBManager dbmanager;

    private static ResultSet orders, orderer;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        // Arguments:
        // Database location, Person.data location, Order.data location,
        parseArgs(args);
        
        // Creating these objects fills the database
        dbmanager = new DBManager(DBname, DBuser, DBpass);        
        preader = new PersonReader(peoplefile, dbmanager, schema);
        oreader = new OrderReader(ordersfile, dbmanager, schema);               
        
        // Code to perform required readings and printings
        try {
            // Builders to construct the outputs that are going to be
            // printed.
            StringBuilder orderersDetails = new StringBuilder(0)
                    .append("Details of persons with at least one order: ");
            StringBuilder ordersWithNames = new StringBuilder(0)
                    .append("Details of all orders with orderer's name: ");
            
            ArrayList<Integer> peopleWithOrders = new ArrayList<>(0);
            
            
                // Create a string to request the person's data
                StringBuilder sqlBuild = new StringBuilder(0);
                sqlBuild.append("SELECT * FROM ");
                if (schema.length() > 0){
                    sqlBuild.append(schema)
                            .append(".");
                }
                sqlBuild.append('"')
                        .append("ORDER")
                        .append('"');
                String sql = sqlBuild.toString();            
            
            orders = dbmanager.getData("SELECT * FROM APP." 
                    + '"' + "ORDER" + '"');
            // Iterate over all orders
            while (orders.next()){                
                int id = orders.getInt("PERSON_ID");
                ordersWithNames.append('\n')
                        .append("Order ID: ")
                        .append(orders.getInt("ORDER_ID"))
                        .append("; Order Number: ")
                        .append(orders.getInt("ORDER_NO"))
                        .append("; Person ID: ")
                        .append(id);
                peopleWithOrders.add(id);
            }
            
            ArrayList<Integer> doneWith = new ArrayList<>(0);
            int m = 1;            
            for (int id: peopleWithOrders){
                // Since we're doing this backwards, remove the ID, then check
                // to see if it's still there (duplicate)
                
                // Create a string to request the person's data
                sqlBuild = new StringBuilder(0);
                sqlBuild.append("SELECT * FROM ");
                if (schema.length() > 0){
                    sqlBuild.append(schema)
                            .append(".");
                }
                sqlBuild.append("PERSON WHERE PERSON_ID = ")
                        .append(Integer.toString(id));
                sql = sqlBuild.toString();
                orderer = dbmanager.getData(sql);                
                
                    if (orderer.next()) {
                        // Find the newlines in the stringbuilder and add
                        // the name just before that
                        String nameSec = "; Orderer's first name: " 
                                + orderer.getString("FIRST_NAME");
                        
                        // This loop finds \n, and iff the newline corresponds
                        // to a person, inserts the current name. The position
                        // of correspondance increases with the for-loop.
                        // coughhack
                        int i = 0, n = 0;
                        while (true){                            
                            i = ordersWithNames
                                    .indexOf(Character.toString('\n'), i);
                            if ( i == -1){
                                break;
                            }
                            else if (n == m){
                                ordersWithNames.insert(i, nameSec);
                                i = i + nameSec.length() + 1;
                                n++;
                            }
                            else {
                                i++;
                                n++;
                            }
                            
                        }
//                        ordersWithNames.append("; Orderer's first name: ")
//                                .append(orderer.getString("FIRST_NAME"));
                        
                        if (!doneWith.contains(id)) {
                            // If we won't add this person again, add them
                            orderersDetails.append('\n')
                                    .append("ID: ")
                                    .append(orderer.getInt("PERSON_ID"))
                                    .append("; Last name: ")
                                    .append(orderer.getString("LAST_NAME"))
                                    .append("; First name: ")
                                    .append(orderer.getString("FIRST_NAME"))
                                    .append("; Street: ")
                                    .append(orderer.getString("STREET"))
                                    .append("; City: ")
                                    .append(orderer.getString("CITY"));
                            doneWith.add(id);
                        }                    
                    }
                    else {
                        // Code to perform functions, eg print a warning, when
                        // the orderer isn't available, should go here
                        //System.out.println("No orderer found");
                    }
                    m++;
            }
            System.out.println(orderersDetails.toString());
            System.out.println(ordersWithNames.toString());
            
        }
        catch (SQLException sqlx){
            LOG.log(Level.SEVERE, null, sqlx);
            System.out.println(sqlx.getMessage());
            System.exit(1);            
        }

    }
    
    private static void parseArgs(String[] args){
        
        if (args.length == 6){
            // There are 6 string arguments by default. The option to enter 
            // fewer ought to be added, but isn't crucial.
            DBname = args[0];
            DBuser = args[1];
            DBpass = args[2];
            schema = args[3];
            
            peoplefile = args[4];
            ordersfile = args[5];            
            
        }
        else {
            System.out.println("Incorrect number of arguments provided");
            System.exit(1);
        }
    }
        
}
