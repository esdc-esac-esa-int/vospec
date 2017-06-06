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

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

/**
 * Minimal window for displaying a JList of PLASTIC applications.
 *
 * @author  Mark Taylor
 * @since   10 Apr 2006
 */
public class PlasticWindow extends JFrame {

    /**
     * Constructor.
     *
     * @param   model  model to display
     */
    public PlasticWindow( ListModel model ) {
        JList list = new JList( model );
        JScrollPane scroller = new JScrollPane( list );
        scroller.setPreferredSize( new Dimension( 200, 150 ) );
        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( scroller, BorderLayout.CENTER );
        JComponent heading = new JLabel( "PLASTIC registered applications" );
        ((JComponent) getContentPane())
                     .setBorder( BorderFactory
                                .createEmptyBorder( 5, 5, 5, 5 ) );
        heading.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        getContentPane().add( heading, BorderLayout.NORTH );
    }
}
