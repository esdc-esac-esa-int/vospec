/* 
 * Copyright (C) 2017 ESDC/ESA 2017
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package esavo.vospec.spectrum;

import java.util.Vector;

/**
 *
 * @author jgonzale
 */
public class SpectrumConvertersRunner extends Thread {

    Vector<SpectrumConverter> converters = new Vector();
    public final static int ADD = 0;
    public final static int REMOVE = 1;

    public synchronized void operation(SpectrumConverter converter, int operation) {
        if (operation == ADD) {
            converters.add(converter);
        }
        if (operation == REMOVE) {
            converters.remove(converter);
        }
    }

    public void run() {

        //this.setPriority(Thread.MIN_PRIORITY);

        while (true) {

            while (converters.size() == 0) {
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            while (converters.size() != 0) {
                try {




                    converters.firstElement().run();

   


                    
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                converters.remove(0);
            }

        }
    }
}
