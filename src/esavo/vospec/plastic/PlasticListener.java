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
package esavo.vospec.plastic;

import java.net.URI;
import java.util.List;


/***
 * The interface that java-rmi Plastic-compatible applications should support.
 * 
 * @see <a href="http://plastic.sourceforge.net/">http://plastic.sourceforge.net</a>
 * @author Isa Barbarisi
 * @date 28-04-2006
*/
public interface PlasticListener {
    /***
     * The current version of Plastic defined by this interface.
     */
    String CURRENT_VERSION = "0.4";

    /***
     * Request that the application perform an action based on a message.
     * 
     * @param sender the ID of the originating application.
     * @param message the URI representing the action.
     * @param args any arguments to pass.
     * @return any return value of the action.
     * @xmlrpc the URIs are strings (of the appropriate form) and the List is an array
     * @see <a href="http://plastic.sourceforge.net">http://plastic.sourceforge.net</a>
     */
    public Object perform(URI sender, URI message, List args);

}
