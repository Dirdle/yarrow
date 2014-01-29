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

    private static ResultSet rs;
    
    public static void main(String[] args) {
        
        parseArgs(args);
        
        // Creating these objects fills the database
        dbmanager = new DBManager(DBname, DBuser, DBpass);        
        preader = new PersonReader(peoplefile, dbmanager, schema);
        oreader = new OrderReader(ordersfile, dbmanager, schema);               
        
        // Code to perform required readings and printings
        // TODO figure out how to make this class work with input files or
        // somesuch, allowing for arbitrary readings
        //
        // wait no that's stupid
        try {
            // Builders to construct the outputs that are going to be
            // printed.
            StringBuilder orderersDetails = new StringBuilder(0)
                    .append("Details of persons with at least one order: ");
            StringBuilder ordersWithNames = new StringBuilder(0)
                    .append("Details of all orders with orderer's name: ");
            
            StringBuilder sqlBuilder = new StringBuilder(0);
            
            
            //Create an SQL query to fetch a joined result set that contains
            // all the information required to build both output strings.            
            // Note that the schema has to be correctly referenced every time
            sqlBuilder.append("SELECT * FROM ");
            sqlBuilder = appendSchema(sqlBuilder);
            sqlBuilder.append('"')
                    .append("ORDER")
                    .append('"')
                    .append(" LEFT JOIN ");
            sqlBuilder = appendSchema(sqlBuilder);
            sqlBuilder.append("PERSON ON ");
            sqlBuilder = appendSchema(sqlBuilder);
            sqlBuilder.append('"')
                    .append("ORDER")
                    .append('"')
                    .append(".PERSON_ID = ");
            sqlBuilder = appendSchema(sqlBuilder);
            sqlBuilder.append("PERSON.PERSON_ID");
            
            
            // create result set from query
            rs = dbmanager.getData(sqlBuilder.toString());
            
            // iterate through the result set
            ArrayList<Integer> usedIDs = new ArrayList<>(0);
            while (rs.next()){
                ordersWithNames.append('\n')
                        .append("Order ID: ")
                        .append(rs.getInt("ORDER_ID"))
                        .append("; Order Number: ")
                        .append(rs.getInt("ORDER_NO"))
                        .append("; Person ID: ")
                        .append(rs.getInt("PERSON_ID"));
                if (rs.getString("FIRST_NAME") != null){
                    // if there's a person for the order, give their name
                    ordersWithNames.append("; First Name: ")
                            .append(rs.getString("FIRST_NAME"));
                }
                
                if (!usedIDs.contains(rs.getInt("PERSON_ID"))
                        // Arbitrarily, decide that having a first and last name 
                        // is enough to print the person's details
                        && rs.getString("FIRST_NAME") != null
                        && rs.getString("LAST_NAME") != null){
                    usedIDs.add(rs.getInt("PERSON_ID"));
                    orderersDetails.append('\n')
                            .append("ID: ")
                            .append(rs.getInt("PERSON_ID"))
                            .append("; Last name: ")
                            .append(rs.getString("LAST_NAME"))
                            .append("; First name: ")
                            .append(rs.getString("FIRST_NAME"))
                            .append("; Street: ")
                            .append(rs.getString("STREET"))
                            .append("; City: ")
                            .append(rs.getString("CITY"));                    
                }     
            }

            System.out.println(orderersDetails.toString());
            System.out.println();
            System.out.println(ordersWithNames.toString());            
        }
        catch (SQLException sqlx){
            LOG.log(Level.SEVERE, null, sqlx);
            System.out.println(sqlx.getMessage());
            System.exit(1);            
        }

    }
    
    // Highly repeated 'add a schema if there is one' code
    private static StringBuilder appendSchema(StringBuilder s){
        if (schema.length() > 0){
            s.append(schema)
                    .append(".");
        }
        return s;
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
