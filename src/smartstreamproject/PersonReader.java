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

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *  "[0-9]+(,[a-zA-Z]+){4}"
 * @author Oscar
 */
public class PersonReader extends AbstractReader {    
    // Reader class for Person.data
       
    public PersonReader(String filepath, DBManager destination, String schema){
        cl = this.getClass().getClassLoader();
        try {
            this.schema = schema;
            this.setSource(filepath);
            this.setDestination(destination, "PERSON", schema);
            this.setPattern("[0-9]+(,[a-zA-Z]+){4}", ",");
            
            this.read();
        }
        catch (FileNotFoundException fnfex){
            System.out.println("Person file not found.");
            System.out.println(fnfex.getMessage());
            System.exit(1);
        }
        catch (IOException ioex){
            System.out.println("Error reading the person file.");
            System.out.println(ioex.getMessage());
            System.exit(1);
        }
    }
    
}
