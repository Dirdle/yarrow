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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

/**
 *
 * @author Oscar
 */
public class DBManager {
    
    private Connection connection; 
    private Statement statement;
    
    private String schema;
    
    public DBManager(String location, String username, String password){
        try {
            // Create a connection with the database given information
            connection = DriverManager.getConnection
                    (location, username, password);
            
            //If autocommit is on, I have to go and clear the database every
            // time I want to try and test a fix to a bug
            connection.setAutoCommit(false);
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);            
            
        }
        catch (SQLException sqlx) {
            SmartstreamProject.LOG.log(Level.SEVERE, null, sqlx);
            System.out.println(sqlx.getMessage());
            System.exit(1);
        }        
    }
    
    public void addLine(String[] line, String table){
        // This method adds a line to a given table
        
        StringBuilder sb = new StringBuilder(0);
        sb.append("INSERT INTO ");
        sb.append(table);
        sb.append(" VALUES (");
        for (String s : line){
            // Given that the strings are either a) actually strings or 
            // b) actually positive integers, the simplest way of choosing which
            // is:
            if (s.matches("[0-9]+")){
                // If the string is a number, append it as-is
                sb.append(s);
            }
            else {
                // Else, add quotations to it
                sb.append("'");                        
                sb.append(s);
                sb.append("'");
            }
            if (!s.equals(line[line.length - 1])){
                // Include a separator unless this is the last substring
                sb.append(",");
            }
            else {
                sb.append(")");
            }
        }
        
        String query = sb.toString();
        try {
            statement.executeUpdate(query);
        } 
        catch (SQLException sqlx) {
            SmartstreamProject.LOG.log(Level.SEVERE, null, sqlx);
            System.out.println(sqlx.getMessage());
            System.exit(1);
        }
    }
    
    public ResultSet getData(String query){
        // Doesn't actually get the data, just passes a pointer to a 
        // portal to the database (the resultset object)
        try {
            ResultSet rs = statement.executeQuery(query);
            return rs;
        }
        catch (SQLException sqlx){
            SmartstreamProject.LOG.log(Level.SEVERE, null, sqlx);
            System.out.println(sqlx.getMessage());
            System.exit(1);
            return null;
        }
    }
}
