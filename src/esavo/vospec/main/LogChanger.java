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
package esavo.vospec.main;

public class LogChanger implements Runnable {

    private static VOSpecDetached AIOSPECTOOLDETACHED;
    private int counter;

    private boolean restart=false;

    public LogChanger(VOSpecDetached aioSpecToolDetached) {
        this.AIOSPECTOOLDETACHED = aioSpecToolDetached;
        this.counter = 0;
        restart=false;
    }

    public void restart() {
        restart=true;
    }

    public void run() {

        while (true) {

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (counter < AIOSPECTOOLDETACHED.logVector.size()) {

                setLogText((String) AIOSPECTOOLDETACHED.logVector.elementAt(counter));
                counter++;
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setLogText("");

                if(restart){
                    counter=0;
                    restart=false;
                }
            }


            if (restart) {
                counter = 0;
                restart = false;
            }

        }


    }

    private void setLogText(String logMessage) {
        AIOSPECTOOLDETACHED.setLogText(logMessage);
    }
}
