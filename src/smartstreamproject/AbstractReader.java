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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Superclass of Readers (PersonReader, OrderReader, possible future Readers)
 *
 * @author Oscar
 */
public abstract class AbstractReader {
    

    
    /**
     * The pattern is a regex that matches the lines that your reader wants to 
     * read from the file
     */
    Pattern pattern;
    Matcher matcher;    
    
    DBManager destination;
    String schema;
    
    ClassLoader cl;
    BufferedReader br;  
    
    String separator;
    String tablename;
            
    /**
     * Sets a string as the regex pattern to match lines against.
     * 
     * @param regex 
     */
    public void setPattern(String regex, String separator){
        // TODO add code to determine the separator from the pattern
        // Doing so would probably be highly inconsistent, but could be
        // done as an overload to this method maybe        
        this.pattern = Pattern.compile(regex);
        this.separator = separator;
    }
    
    /**
     * Sets a DBManager object as the output for this reader
     * 
     * @param dbm
     * @param tablename
     * @param schema 
     */
    public void setDestination(DBManager dbm, String tablename, String schema){
        this.destination = dbm;
        this.tablename = tablename;
        this.schema = schema;
    }
    
    // Unsure if this will work with some set of if-statements to check for a cl
    // and create one if absent. I don't expect it to.
    /**
     * Call after setting a classloader
     * to create a buffered reader of the given file
     * 
     * @param filepath
     * @throws FileNotFoundException 
     */
    public void setSource(String filepath) throws FileNotFoundException{
        this.br = new BufferedReader(new FileReader(
                cl.getResource(filepath).getPath()));
    }
    
    
    /**
     * Reads the file into the database manager. Always set up the DBM, the
     * buffered reader and the pattern before calling this method
     * 
     * @param separator 
     */
    public void read() throws IOException {
        
        // iterate through the file
        while (br.ready()){
            String line = br.readLine();

            // Code to check that the line matches the given pattern
            matcher = pattern.matcher(line);
            if (matcher.find()){
                String[] splitLine = line.split(separator);
                
                
                // Create a string to define the table location within the 
                // database
                StringBuilder tableBuild;
                if (schema.length() > 0){
                    tableBuild = new StringBuilder(0)
                            .append(schema)
                            .append('.')
                            .append('"')
                            .append(tablename)
                            .append('"');
                }
                else {
                    tableBuild = new StringBuilder(0)
                            .append('"')
                            .append(tablename)
                            .append('"');
                }
                String table = tableBuild.toString();
                
                // Finally, update the database with the line
                destination.addLine(splitLine, table);
            }
        }
    }
}
