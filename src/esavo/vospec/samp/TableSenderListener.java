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
import esac.archive.absi.modules.cl.samp.NotificationSenderListener;
import org.astrogrid.samp.Message;

/**
 *
 * @author jgonzale
 */
public class TableSenderListener extends NotificationSenderListener {

    private String tableUrl;
    private String tableId;
    private String tableName;

    public TableSenderListener(String messageType, Interop interopAccessor, String tableUrl, String tableId, String tableName) {
        super(messageType, interopAccessor);
        this.tableUrl = tableUrl;
        this.tableId = tableId;
        this.tableName = tableName;
    }

    protected Message buildMessage() {

        //Build message for specific MType table.load.votable
        Message message = new Message(this.getMType());

        message.addParam("url", tableUrl);
        message.addParam("table-id", tableId);
        message.addParam("name", tableName);

        System.out.println(tableUrl);
        System.out.println("Message " + message.toString());

        return message;


    }


}