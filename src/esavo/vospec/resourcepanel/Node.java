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
package esavo.vospec.resourcepanel;

import esavo.vospec.dataingestion.TsaServerParam;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;

public class Node extends DefaultMutableTreeNode {



    //This is the same than the previous CheckNode adding metadata field
    //(previously located only in Spectrum)
    private Hashtable metadata = new Hashtable();
    private Vector<String> metadata_identifiers = new Vector();
    public final static int SINGLE_SELECTION = 0;
    public final static int DIG_IN_SELECTION = 4;
    private Object relatedObject;
    public boolean containsSpectrum = false;
    protected int selectionMode;
    public boolean isSelected = false;
    public boolean isWaiting = false;
    public boolean isReady = true;
    public boolean isDownloaded = false;
    public boolean isFailed = false;
    public boolean highLight = false;
    public boolean isTsa = false;
    public String value = "";
    public String name = "";
    public boolean isValueSelected = false;
    public String toolTipText = "Right-click for options";
    public TsaServerParam tsaServerParam = null;

    public Node(String name, Hashtable metadata, Vector<String> metadata_identifiers) {
        super(name, true);
        setSelectionMode(SINGLE_SELECTION);
        this.name = name;
        this.metadata = metadata;
        this.metadata_identifiers = metadata_identifiers;
        this.isSelected = false;
    }

    public Node() {
        this(null);
    }

    public Node(Object userObject) {
        this(userObject, true, false);
    }

    public Node(Object userObject, boolean allowsChildren, boolean isSelected) {
        super(userObject, allowsChildren);
        this.isSelected = isSelected;
        setSelectionMode(DIG_IN_SELECTION);
        if (userObject.getClass() == String.class) {
            this.name = (String) userObject;
        }
    }

    public void setTsaServerParam(TsaServerParam tsaServerParam) {
        this.tsaServerParam = tsaServerParam;
    }

    public TsaServerParam getTsaServerParam() {
        return tsaServerParam;
    }

    public void setParamTsa(boolean isTsa) {
        this.isTsa = isTsa;
    }

    public boolean isParamTsa() {
        return isTsa;
    }

    public void setSelectionMode(int mode) {
        selectionMode = mode;
    }

    public int getSelectionMode() {
        return selectionMode;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setValueSelected(String value) {
        this.value = value;
    }

    public String getValueSelected() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
        this.setUserObject(name);
    }

    public String getName() {
        return name;
    }

    public void isValueSelected(boolean isValueSelected) {
        this.isValueSelected = isValueSelected;
    }

    public boolean getIsValueSelected() {
        return isValueSelected;
    }

    public void setWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

    public boolean getWaiting() {
        return isWaiting;
    }

    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    public boolean getReady() {
        return isReady;
    }

    public void setDownloaded(boolean isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public boolean getDownloaded() {
        return isDownloaded;
    }

    public void setHighLight(boolean highLight) {
        this.highLight = highLight;
    }

    public boolean getHighLight() {
        return highLight;
    }

    public void setFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }

    public boolean getFailed() {
        return isFailed;
    }

    public void setToolTipTextParam(String toolTipText) {
        String toolTipTextFormatted = "";
        if (toolTipText != null) {
            toolTipTextFormatted = formatToolTipeText(toolTipText);
        }
        this.toolTipText = toolTipTextFormatted;
    }

    public String formatToolTipeText(String toolTip) {
        String[] tokens = toolTip.split(" ");
        int ct = 0;
        int pack = 8;
        boolean stringFinished = false;
        String desc = "";
        while (!stringFinished) {
            desc = desc + "<TR><TD>";
            for (int i = ct; i < pack + 1; i++) {
                if (i == tokens.length) {
                    stringFinished = true;
                    desc = "<HTML><TABLE>" + desc + "</TD></TR></TABLE></HTML>";
                    return desc;
                } else {
                    desc = desc + " " + tokens[i];
                }

            }
            desc = desc + "</TD></TR>";
            ct = pack + 1;
            pack = pack + 8;
        }
        return null;
    }

    public String getToolTipTextParam() {
        return toolTipText;
    }

    public String getToolTipText() {
        /*if(!this.containsSpectrum) return "";
        

        Hashtable   ht      = this.spectrum.getMetaData();
        Enumeration keys    = ht.keys();
        
        String thisKey          = "";
        String thisKeyValue     = "";
        String htmlToolTipText  = "<HTML>" +
        "<CENTER><I>Double Click to Open a Window with this Info</I></CENTER>";
        
        htmlToolTipText         =  htmlToolTipText + "<TABLE>";
        
        while(keys.hasMoreElements()) {
        
        htmlToolTipText =  htmlToolTipText + "<TR>";
        thisKey         = (String) keys.nextElement();
        thisKeyValue    = (String) String.valueOf(ht.get(thisKey));

        htmlToolTipText = htmlToolTipText + "<TD><B>" + thisKey + "</B></TD><TD>" + thisKeyValue + "</TD>";
        htmlToolTipText = htmlToolTipText + "</TR>";
        }
        htmlToolTipText =  htmlToolTipText + "</TABLE></HTML>";
        
        return htmlToolTipText;*/
        //return "";

        return toolTipText;

    }

    /**
     * @return the metadata
     */
    public Hashtable getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(Hashtable metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the metadata_identifiers
     */
    public Vector<String> getMetadata_identifiers() {
        return metadata_identifiers;
    }

    /**
     * @param metadata_identifiers the metadata_identifiers to set
     */
    public void setMetadata_identifiers(Vector<String> metadata_identifiers) {
        this.metadata_identifiers = metadata_identifiers;
    }

    /**
     * @return the relatedObject
     */
    public Object getRelatedObject() {
        return relatedObject;
    }

    /**
     * @param relatedObject the relatedObject to set
     */
    public void setRelatedObject(Object relatedObject) {
        this.relatedObject = relatedObject;
    }
}
