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
package esavo.vospec.samp;

import esac.archive.absi.modules.cl.samp.Interop;
import esac.archive.absi.modules.cl.samp.InteropMenuBlock;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import org.astrogrid.samp.Client;

public class MessageSenderMenu extends InteropMenuBlock implements MenuListener {

    private static final long serialVersionUID = 1L;
    public static final String ALL_ACTION_COMMAND = "sendToAll";
    protected Interop interop;
    protected String text;
    protected String allText;
    protected String mType;
    protected JMenu recipientsMenu;
    protected List menuItems;
    protected Vector<ActionListener> listeners = new Vector();

    public MessageSenderMenu(Interop interopAccessor, String menuText,
            String toAllText, String messageType) {
        super();
        interop = interopAccessor;
        text = menuText;
        allText = toAllText;
        mType = messageType;
        menuItems = new LinkedList();
        createMenuItems();
        reconfigureMenu();
        listeners = new Vector();
    }

    protected void createMenuItems() {
        recipientsMenu = new JMenu(text);
        recipientsMenu.addMenuListener(this);
        menuItems.add(recipientsMenu);
    }

    public List getMenuItems() {
        return menuItems;
    }

    public void reconfigureMenu() {

        for (int i = 0; i < listeners.size(); i++) {
            if(interop!=null){
                if ((listeners.get(i) != null) && (interop.isConnected())) {
                    recipientsMenu.setEnabled(true);
                } else {
                    recipientsMenu.setEnabled(false);
                }
            }
        }
    }

    public void menuSelected(MenuEvent e) {

        if (interop != null) {
            if ((listeners.size() > 0) && (interop.isConnected())) {
                List clients = interop.getClientsSubscribed(mType);
                Iterator it = clients.iterator();
                while (it.hasNext()) {
                    Client client = (Client) it.next();
                    JMenuItem menuItem;
                    if (clientMoreThanOnce(client.getMetadata().getName(), clients)) {
                        menuItem = new JMenuItem(client.getMetadata().getName() +
                                " (" + client.getId() + ")");
                    } else {
                        menuItem = new JMenuItem(client.getMetadata().getName());
                    }
                    menuItem.setActionCommand(client.getId());
                    for (int i = 0; i < listeners.size(); i++) {
                        menuItem.addActionListener(listeners.get(i));
                    }
                    recipientsMenu.add(menuItem);
                }
                if (recipientsMenu.getMenuComponentCount() > 1) {
                    recipientsMenu.addSeparator();
                    JMenuItem toAllMenuItem = new JMenuItem(allText);
                    toAllMenuItem.setActionCommand(ALL_ACTION_COMMAND);
                    for (int i = 0; i < listeners.size(); i++) {
                        toAllMenuItem.addActionListener(listeners.get(i));
                    }
                    recipientsMenu.add(toAllMenuItem);
                }
            }
        }
    }

    public void menuDeselected(MenuEvent e) {
        recipientsMenu.removeAll();
    }

    public void menuCanceled(MenuEvent e) {
    }

    @Override
    public void addActionListener(ActionListener l) {
        listeners.add(l);
    }

    public void removeActionListeners() {
        listeners = new Vector();
    }

    protected boolean clientMoreThanOnce(String name, List clients) {
        int counter = 0;
        Iterator it = clients.iterator();
        while (it.hasNext()) {
            Client client = (Client) it.next();
            if (client.getMetadata().getName().equals(name)) {
                counter++;
            }
        }
        if (counter > 1) {
            return true;
        } else {
            return false;
        }
    }
}
