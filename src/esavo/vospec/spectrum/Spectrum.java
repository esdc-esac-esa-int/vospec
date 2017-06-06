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

import esavo.vospec.main.*;
import esavo.vospec.resourcepanel.Node;
import java.io.*;
import java.util.*;
/**
 *
 *
 * @author ibarbarisi
 * @version %version: 8 %
 */

public class Spectrum implements Serializable, Runnable{
    
    //public static final long serialVersionUID = -6981298742887694876L;
    
    protected String url; //public because FitsSpectrum uses it
    protected Hashtable metadata;
    protected Vector<String> metadata_identifiers;
    protected Node node;
    protected String format;

    protected String waveLengthColumnName;
    protected String fluxColumnName;
    
    protected double redShift;
    
    protected String dimQ      ="";
    protected String dimQWave  = "";
    
    protected String fluxF     = "1.";
    protected String waveF     = "1.";
    
    protected String unitsW;
    protected String unitsF;
    
    protected String ra;
    protected String dec;
    protected String name;
    protected String title;
    
    protected double[] waveValues;
    protected double[] fluxValues;

    // Errors //
    protected double[] fluxErrorUpper;
    protected double[] fluxErrorLower;

    protected double[] waveErrorUpper;
    protected double[] waveErrorLower;

    protected String fluxErrorUpperColumnName;
    protected String fluxErrorLowerColumnName;
    protected String waveErrorUpperColumnName;
    protected String waveErrorLowerColumnName;

    protected boolean waveErrorsPresent = false;
    protected boolean fluxErrorsPresent = false;
    
    /////////
    protected boolean isSelected = true;
    protected int row;
    
    protected boolean toWait;
    protected boolean realData;
    
    protected String param;
    protected String desc;
    protected double[] values;
    protected String typeValue;
    protected boolean toBeNormalize=false;
    protected double norm=1;
    
    protected boolean output = false;
    
    protected double refWave;
    protected boolean refWaveBoolean   =false;
    protected boolean bb = false;

    private ExtendedJTextField colorNode;

    private VOSpecDetached vospec;

    private boolean alreadyDownloaded = false;

    public Spectrum() {
        url = "";
        metadata = new Hashtable();
        toWait = false;
        realData = true;
        redShift = 0.;
        isSelected=false;
        metadata_identifiers=new Vector();
    }
    
    public Spectrum(Spectrum spectrum) {
        this();
        this.setFormat(spectrum.getFormat());
        this.setUrl(spectrum.getUrl());
        this.setWaveLengthColumnName(spectrum.getWaveLengthColumnName());
        
        this.setFluxColumnName(spectrum.getFluxColumnName());
        this.metadata = spectrum.getMetaData();
        this.metadata_identifiers = spectrum.getMetadata_identifiers();
        this.setRow(spectrum.getRow());
        this.setDimeQ(spectrum.getDimeQ());
        this.setDimeQWave(spectrum.getDimeQWave());
        this.setFluxFactor(spectrum.getFluxFactor());
        this.setUnitsF(spectrum.getUnitsF());
        this.setUnitsW(spectrum.getUnitsW());
        this.setWaveFactor(spectrum.getWaveFactor());
        this.setTitle(spectrum.getTitle());
        this.setName(spectrum.getName());
        this.setRa(spectrum.getRa());
        this.setDec(spectrum.getDec());
        this.setParam(spectrum.getParam());
        this.setDesc(spectrum.getDesc());
        this.setValues(spectrum.getValues());
        this.setToBeNormalized(spectrum.getToBeNormalized());
        this.setTypeValue(spectrum.getTypeValue());
        this.setBB(spectrum.getBB());
        this.setNode(spectrum.getNode());
        this.setWaveValues(spectrum.getWaveValues());
        this.setFluxValues(spectrum.getFluxValues());
        
        this.setFluxErrorLowerColumnName(spectrum.getFluxErrorLowerColumnName());
        this.setFluxErrorUpperColumnName(spectrum.getFluxErrorUpperColumnName());
        this.setWaveErrorLowerColumnName(spectrum.getWaveErrorLowerColumnName());
        this.setWaveErrorUpperColumnName(spectrum.getWaveErrorUpperColumnName());

        this.setFluxErrorLower(spectrum.getFluxErrorLower());
        this.setFluxErrorUpper(spectrum.getFluxErrorUpper());
        this.setWaveErrorLower(spectrum.getWaveErrorLower());
        this.setWaveErrorUpper(spectrum.getWaveErrorUpper());

        this.setWaveErrorsPresent(spectrum.isWaveErrorsPresent());
        this.setFluxErrorsPresent(spectrum.isFluxErrorsPresent());

        this.setAioSpecToolDetached(spectrum.getAioSpecToolDetached());
 
    }

    public void setAioSpecToolDetached(VOSpecDetached vospec){
        this.vospec = vospec;
    }

    public VOSpecDetached getAioSpecToolDetached(){
        return vospec;
    }
    

    public void setNode(Node node){
        this.node=node;
    }
    public Node getNode(){
        return this.node;
    }
    
    
    public void setRa(String ra){
        this.ra = ra;
    }
    
    public String getRa(){
        return ra;
    }
    
     public void setBB(boolean bb){
        this.bb = bb;
    }
    
    public boolean getBB(){
        return bb;
    }
    
    
    public void setDec(String dec){
        this.dec = dec;
    }
    
    public String getDec(){
        return dec;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setTitle(String title){
        this.title = title;
    }
    
    public String getTitle(){
        return title;
    }
    
    
    public void setRow(int row){
        this.row = row;
    }
    
    public int getRow(){
        return row;
    }
    
    public double getRedShift() {
        return redShift;
    }
    
    public void setRedShift(double redShift) {
        this.redShift = redShift;
    }
    
    public void setSelected(boolean isSelected){
        this.isSelected=isSelected;
    }
    
    public boolean getSelected() {
        return this.isSelected;
    }
    
    public void setUrl(String url){
        this.url=url;
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public void addMetaData(String ind,Object value) {
        metadata.put(ind,value);
    }
    
    //Used in ArithmeticWindow
    public void setMetaDataComplete(Hashtable metadata) {
        this.metadata = metadata;
    }
    
    public Hashtable getMetaData() {
        return this.metadata;
    }

    /**
     * @return the metadata_identifiers
     */
    public Vector<String> getMetadata_identifiers() {
        return metadata_identifiers;
    }

    public void setMetadata_identifiers(Vector identifiers) {
        this.metadata_identifiers = identifiers;
    }

    /**
     * @param metadata_identifiers the metadata_identifiers to set
     */
    public void addMetadata_identifier(String metadata_identifier) {
        this.metadata_identifiers.add(metadata_identifier);
    }

    
    public Object getMetaDataAtRef(String ind) {
        return metadata.get(ind);
    }
    
    public void setWaveValues(double[] wave) {
        waveValues = wave;
    }
    
    public double[] getWaveValues() {
        return waveValues;
    }
    
    public void setFluxValues(double[] flux) {
        fluxValues = flux;
    }
    
    public double[] getFluxValues() {
        return fluxValues;
    }
    
    public void setWaveLengthColumnName(String waveName) {
        waveLengthColumnName = waveName;
    }
    
    public String getWaveLengthColumnName() {
        return waveLengthColumnName;
    }
    
    public void setFluxColumnName(String fluxName) {
        this.fluxColumnName = fluxName;
    }
    
    public String getFluxColumnName() {
        return fluxColumnName;
    }
    
    
    public boolean getToWait() {
        return toWait;
    }
    
    public void setToWait(boolean toWait) {
        this.toWait = toWait;
    }
    
    public String getString() {
        String string = "";
        for (int i=0;i<metadata.size();i++){
            string = string + metadata.get(i+"") + "\n";
        }
        return string;
    }
    
    public boolean getRealData() {
        return realData;
    }
    public void setRealData(boolean realData) {
        this.realData = realData;
    }
    
/*
 * Units manipulation. We conserve, for the time being, the old strings instead of using
 * the new class unit everywhere
 *
 */
    public void setUnits(Unit unit) {
        this.dimQ       = (String) unit.getFluxVector().elementAt(1);
        this.dimQWave   = (String) unit.getWaveVector().elementAt(1);
        this.fluxF      = (String) unit.getFluxVector().elementAt(0).toString();
        this.waveF      = (String) unit.getWaveVector().elementAt(0).toString();
    }
    
    public Unit getUnits() {
        try{
            //System.out.println("dimQwave "+dimQWave+" waveF "+waveF+" dimQ "+dimQ+" fluxF "+fluxF);
            return new Unit(this.dimQWave,this.waveF,this.dimQ,this.fluxF);
        }catch(Exception e){
            return new Unit(this.waveF,this.dimQWave,this.fluxF,this.dimQ);
        }
    }
    
    
    public void setDimeQWave(String dimQWave){
        this.dimQWave = dimQWave;
    }
    public void setDimeQ(String dimQ){
        this.dimQ = dimQ;
    }
    public void setWaveFactor(String waveF){
        this.waveF = waveF;
    }
    public void setFluxFactor(String fluxF){
        this.fluxF = fluxF;
    }
    
    
    public String getDimeQ(){
        return dimQ;
    }
    
    
    public String getDimeQWave(){
        return dimQWave;
    }
    
    
    public String getFluxFactor(){
        return fluxF;
    }
    
    public String getWaveFactor(){
        return waveF;
    }
    
    public void setUnitsW(String unitsW){
        this.unitsW = unitsW;
    }
    
    public String getUnitsW(){
        return unitsW;
    }
    
    public void setUnitsF(String unitsF){
        this.unitsF = unitsF;
    }
    
    public String getUnitsF(){
        return unitsF;
    }
    
    
    public void setParam(String ra){
        this.param = param;
    }
    
    public String getParam(){
        return param;
    }
    
    
    public void setDesc(String desc){
        this.desc = desc;
    }
    
    public String getDesc(){
        return desc;
    }
    
    public void setTypeValue(String typeValue){
        this.typeValue = typeValue;
    }
    
    public String getTypeValue(){
        return typeValue;
    }
    
    public void setValues(double[] values) {
        this.values = values;
    }
    
    public double[] getValues() {
        return values;
    }
    
    public boolean getToBeNormalized() {
        return toBeNormalize;
    }
    
    public void setToBeNormalized(boolean toBeNormalize) {
        this.toBeNormalize = toBeNormalize;
    }
    
    public double getNorm() {
        return norm;
    }
    
    public void setNorm(double norm) {
        this.norm = norm;
    }
//______________________________________________________________________________________


    private void ready(){
        vospec.displayConvertedSpectra(this);
        //vospec.spectrumDownloaded();
        this.setToWait(false);
    }

    private void populate(){
        
        System.out.println("Downloading " + this.getUrl());
        
        calculateData();

        if (getNode() != null) {
            getNode().setDownloaded(true);
        }
        
    }


    public void run() {

        if (!alreadyDownloaded || this.url.startsWith("file")) {
            populate();
            alreadyDownloaded = true;
        }

        ready();

    }



    /*
     * To be overridden by the specific extending classes for each type of spectrum
     *
     */

    public void calculateData() {
        
    }
    
    public void setOutputSpectrum(boolean output){
        this.output=output;
    }
    
     public boolean getOutputSpectrum(){
        return this.output;
    }
    
    public void setRefWavelength(double refWave){
        
        this.refWave        = refWave;
        this.refWaveBoolean = true;
        System.out.println("spectrum in velocity = " +this.refWaveBoolean);
    }
    public double getRefWavelength(){
        return this.refWave;
    }
    
    public boolean isInVelocity() {
        return this.refWaveBoolean;
    }
    
    public void setInVelocity() {
        this.refWaveBoolean=true;
    }

    /**
     * @return the colorNode
     */
    public ExtendedJTextField getColorNode() {
        return colorNode;
    }

    /**
     * @param colorNode the colorNode to set
     */
    public void setColorNode(ExtendedJTextField colorNode) {
        this.colorNode = colorNode;
    }

    /**
     * @return the fluxErrorUpper
     */
    public double[] getFluxErrorUpper() {
        return fluxErrorUpper;
    }

    /**
     * @param fluxErrorUpper the fluxErrorUpper to set
     */
    public void setFluxErrorUpper(double[] fluxErrorUpper) {
        this.fluxErrorUpper = fluxErrorUpper;
    }

    /**
     * @return the fluxErrorLower
     */
    public double[] getFluxErrorLower() {
        return fluxErrorLower;
    }

    /**
     * @param fluxErrorLower the fluxErrorLower to set
     */
    public void setFluxErrorLower(double[] fluxErrorLower) {
        this.fluxErrorLower = fluxErrorLower;
    }

    

    /**
     * @return the waveErrorUpper
     */
    public double[] getWaveErrorUpper() {
        return waveErrorUpper;
    }

    /**
     * @param waveErrorUpper the waveErrorUpper to set
     */
    public void setWaveErrorUpper(double[] waveErrorUpper) {
        this.waveErrorUpper = waveErrorUpper;
    }

    /**
     * @return the waveErrorLower
     */
    public double[] getWaveErrorLower() {
        return waveErrorLower;
    }

    /**
     * @param waveErrorLower the waveErrorLower to set
     */
    public void setWaveErrorLower(double[] waveErrorLower) {
        this.waveErrorLower = waveErrorLower;
    }

    /**
     * @return the fluxErrorUpperColumnName
     */
    public String getFluxErrorUpperColumnName() {
        return fluxErrorUpperColumnName;
    }

    /**
     * @param fluxErrorUpperColumnName the fluxErrorUpperColumnName to set
     */
    public void setFluxErrorUpperColumnName(String fluxErrorUpperColumnName) {
        this.fluxErrorUpperColumnName = fluxErrorUpperColumnName;
    }

    /**
     * @return the fluxErrorLowerColumnName
     */
    public String getFluxErrorLowerColumnName() {
        return fluxErrorLowerColumnName;
    }

    /**
     * @param fluxErrorLowerColumnName the fluxErrorLowerColumnName to set
     */
    public void setFluxErrorLowerColumnName(String fluxErrorLowerColumnName) {
        this.fluxErrorLowerColumnName = fluxErrorLowerColumnName;
    }

    /**
     * @return the waveErrorUpperColumnName
     */
    public String getWaveErrorUpperColumnName() {
        return waveErrorUpperColumnName;
    }

    /**
     * @param waveErrorUpperColumnName the waveErrorUpperColumnName to set
     */
    public void setWaveErrorUpperColumnName(String waveErrorUpperColumnName) {
        this.waveErrorUpperColumnName = waveErrorUpperColumnName;
    }

    /**
     * @return the waveErrorLowerColumnName
     */
    public String getWaveErrorLowerColumnName() {
        return waveErrorLowerColumnName;
    }

    /**
     * @param waveErrorLowerColumnName the waveErrorLowerColumnName to set
     */
    public void setWaveErrorLowerColumnName(String waveErrorLowerColumnName) {
        this.waveErrorLowerColumnName = waveErrorLowerColumnName;
    }

    /**
     * @return the waveErrorsPresent
     */
    public boolean isWaveErrorsPresent() {
        return waveErrorsPresent;
    }

    /**
     * @param waveErrorsPresent the waveErrorsPresent to set
     */
    public void setWaveErrorsPresent(boolean waveErrorsPresent) {
        this.waveErrorsPresent = waveErrorsPresent;
    }

    /**
     * @return the fluxErrorsPresent
     */
    public boolean isFluxErrorsPresent() {
        return fluxErrorsPresent;
    }

    /**
     * @param fluxErrorsPresent the fluxErrorsPresent to set
     */
    public void setFluxErrorsPresent(boolean fluxErrorsPresent) {
        this.fluxErrorsPresent = fluxErrorsPresent;
    }

}
