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

import esavo.vospec.util.EnvironmentDefs;
import esavo.vospec.util.Utils;
import java.applet.AppletContext;
import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * A basic extension of the javax.swing.JApplet class
 */
public class VOSpec extends JApplet {

    VOSpecDetached aiospectooldetached;
    public static final String SPLASH_IMAGE_PATH = "/esavo/vospec/images/VOSpecSplashT.png";
    public static final String APPLICATION_MAIN_CLASS = "esavo.vospec.main.AioSpecToolDetached";

    public void destroy() {
        aiospectooldetached.unregisterInterop();
        aiospectooldetached.setVisible(false);
        aiospectooldetached.dispose();
    }

    public void init() {

        try {

            SplashWindow.splash(VOSpec.class.getResource(SPLASH_IMAGE_PATH));
            SplashWindow.setProgressValue(1);
            SplashWindow.setProgressMessage("Loading VOSpec "+EnvironmentDefs.getVERSION());

            // This line prevents the "Swing: checked access to system event queue" message seen in some browsers.
            getRootPane().putClientProperty("defeatSystemEventQueueCheck", Boolean.TRUE);

            getContentPane().setLayout(null);
            setSize(new Dimension(770, 700));

            Properties props = new Properties();

            props.setProperty("SERVERHOST", getParameter("SERVERHOST"));
            props.setProperty("SERVERPORT", getParameter("SERVERPORT"));
            props.setProperty("RMIPORT", getParameter("RMIPORT"));
            props.setProperty("SERVERNAME", getParameter("SERVERNAME"));
            Utils.rmiPropertiesDefinition(props);
            Utils.setExitMan(false);

            AppletContext thisAppletContext = this.getAppletContext();

            // JFrame1 Create and show the JFrame1 with a title
            String thisTitle = "VOSpec Search Tool\n";
            //System.out.println("Calling AioSpecToolDetached ");

            aiospectooldetached = (new VOSpecDetached(thisTitle, thisAppletContext, props));
            aiospectooldetached.setVisible(true);
            aiospectooldetached.setIsApplet(true);

            SplashWindow.disposeSplash();
            //System.out.println("Frame shown");

        } catch (java.lang.Exception e) {
            JOptionPane.showMessageDialog(this, " Problems opening VOSpec Applet. Please check your java settings or contact ESA VO Team at esavo.helpdesk@sciops.esa.int ");
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {


        try {
            SplashWindow.splash(VOSpec.class.getResource(SPLASH_IMAGE_PATH));
            SplashWindow.setProgressValue(3);
            SplashWindow.setProgressMessage("Loading VOSpec "+EnvironmentDefs.getVERSION());
            String SERVERHOST = "esavo.esac.esa.int";
            String SERVERPORT = "80";
            String RMIPORT = "1099";
            String SERVERNAME = "AioSpecServer";

            Properties props = new Properties();
            props.setProperty("SERVERHOST", SERVERHOST);
            props.setProperty("SERVERPORT", SERVERPORT);
            props.setProperty("RMIPORT", RMIPORT);
            props.setProperty("SERVERNAME", SERVERNAME);

            Utils.rmiPropertiesDefinition(props);
            Utils.setExitMan(true);

            // JFrame1 Create and show the JFrame1 with a title
            String thisTitle = "VOSpec Search Tool\n";
            //System.out.println("Calling AioSpecToolDetached ");

            (new VOSpecDetached(thisTitle, props)).setVisible(true);
            SplashWindow.disposeSplash();
        
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    public String getJavaVersion() {
        return System.getProperty("java.version");
    }
}
