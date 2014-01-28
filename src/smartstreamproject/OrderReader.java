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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Oscar
 */
public class OrderReader {
    
    private ClassLoader cl;
    private BufferedReader br;
    
    private Pattern pattern = Pattern.compile("[0-9]+(\\|[0-9]+){2}");
    private Matcher matcher;
    
    public OrderReader(String filepath, DBManager destination, String schema){

        cl = this.getClass().getClassLoader();
        
        try {
            String line;
            
            // Create a reader for the file at the given path
            
            br = new BufferedReader(new FileReader(
                cl.getResource(filepath).getPath()));
            
            // Hopefully this should keep reading to the end of the file
            while (br.ready()){
                line = br.readLine();
                
                // Code to check that the line matches the given pattern
                matcher = pattern.matcher(line);
                if (matcher.find()){
                    String[] splitLine = line.split("\\|");
                    
                    // Code to construct a label for the table
                    String table;
                    if (schema.length() > 0){
                        table = new StringBuilder(0)
                                .append(schema)
                                .append(".")
                                .append('"')
                                .append("ORDER")
                                .append('"')
                                .toString();
                    }
                    else {
                        table = new StringBuilder(0)
                                .append('"')
                                .append("ORDER")
                                .append('"')
                                .toString();
                    }
                    
                    destination.addLine(splitLine, table);                      
                    
                    // Code to update the database with the line
                }
            }
        }
        catch (IOException iox){
            System.out.println(iox.getMessage());
            System.out.println("Failed to find a file at the given location.");
            System.exit(1);
        }
    }
    
}