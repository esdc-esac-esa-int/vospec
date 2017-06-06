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
package esavo.vospec.util;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * 
 *
 * <pre>
 * %full_filespec: Cache.java,2:java:4 %
 * %derived_by: ibarbarisi %
 * %date_created: Wed Feb  8 12:10:01 2006 %
 * 
 * </pre>
 *
 * @author
 * @version %version: 2 %
 */
public class Cache {

    public static String DIRECTORY = System.getProperty("java.io.tmpdir") + File.separator + "VOSpec" + File.separator;
    public static Hashtable cache = new Hashtable();
    public static File cacheFile;
    public static Random random;

    private Cache() {
    }


    /*
     * Returns true if the file is local or has already been dowloaded
     * and is in cache.
     *
     * */

    public static boolean alreadyLoaded(String url) {

        if ((url.toLowerCase().startsWith("local")) || (getFileName(url) == null)) {
            return false;
        } else {
            return true;
        }

    }


     /*
     * Main method, returns a cached File copy of the referenced URL
     *
     * */

    public static File getFile(String url) {

        String fileName = "";

        //if local do not cache it and open directly
        if (url.toLowerCase().startsWith("local")) {

            fileName = url.replace("file:", "");

        //if remote do the caching process
        } else {

            if (getFileName(url) == null) {
                fileName = Cache.DIRECTORY + cacheRemoteFile(url);
            } else {
                fileName = Cache.DIRECTORY + getFileName(url);
            }

        }

        return new File(fileName);
    }


    /* Deletes all files and subdirectories under dir.
     * Returns true if all deletions were successful.
     * If a deletion fails, the method stops attempting to delete and returns false.
     *
     * */

    private static void deleteDir(File dir) {

        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            File child = new File(dir, children[i]);
            child.delete();
        }

        putCacheInMemory();
    }

    public static void deleteCacheInMemory() {
        deleteDir(new File(Cache.DIRECTORY));
        cache = new Hashtable();
    }



    public static void putCacheInMemory() {

        random = new Random();

        cacheFile = new File(DIRECTORY + "cache");

        if (!cacheFile.exists()) {
            //create empty cache file
            createCacheFile();
        } else {
            // read line from cache file, split url and fileName and update cache hashTable
            try {
                String record = "";
                File f = new File(DIRECTORY + "cache");
                FileInputStream fis = new FileInputStream(f);
                BufferedInputStream bis = new BufferedInputStream(fis);
                DataInputStream dis = new DataInputStream(bis);
                while ((record = dis.readLine()) != null) {
                    int pipe = record.indexOf("|");
                    String url = record.substring(0, pipe);
                    String name = record.substring(pipe + 1);
                    putInCache(url, name);
                }
                fis.close();
                bis.close();
            } catch (Exception e) {
                // catch errors from FileInputStream or readLine()
                System.out.println("File is been created and is null");
            }
        }
    }







    
    /*
     * Creates a cached copy of a remote HTTP file
     *
     * */

    private static String cacheRemoteFile(String url) {

        String fileName = new String();

        BufferedInputStream di = null;
        BufferedOutputStream fo = null;
        byte[] b = new byte[1024];
        Date date = new Date();

        long fileId = date.getTime();

        fileName = "spectrum" + fileId;


        fileId = date.getTime();
        fileName = "spectrum" + fileId;
        fileName = getRandomName(fileName, fileId);

        File localFile = new File(DIRECTORY + fileName);

        //checking if more than one thread is executed simultaneously and the file is called at the same way
        while (localFile.exists()) {

            fileName = getRandomName(fileName, fileId);
            localFile = new File(DIRECTORY + "" + fileName);
        }


        try {

            URL urlT = new URL(url);
            URLConnection urlConnection = urlT.openConnection();
            urlConnection.connect();
            di = new BufferedInputStream(urlConnection.getInputStream());
            fo = new BufferedOutputStream(new FileOutputStream(localFile));

            //  copy data
            while (-1 != di.read(b, 0, 1)) {
                fo.write(b, 0, 1);
            }

            di.close();
            fo.close();

        } catch (MalformedURLException e) {
            System.err.println(e.toString());
        } catch (IOException e) {
            System.err.println(e.toString());
        }


        try {
            //adding row to file cache
            BufferedWriter out = new BufferedWriter(new FileWriter(DIRECTORY + "cache", true));
            out.write(url + "|" + fileName + "\n");
            out.close();

        } catch (Exception e) {
            System.out.println(e);
        }

        return fileName;

    }



    private static void putInCache(String url, String fileName) {
        cache.put(url, fileName);
    }

    private static String getFileName(String url) {
        String fileName = (String) cache.get(url);
        return fileName;
    }

    private static void createCacheFile() {
        try {
            File file1 = new File(DIRECTORY);
            file1.mkdirs();
            FileWriter cache = new FileWriter(DIRECTORY + "cache");

        } catch (IOException e) {
            // catch io errors from FileInputStream or readLine()
            System.out.println("IOException error!" + e.getMessage());
        }
    }



    private static synchronized String getRandomName(String fileName, long fileId) {
        int next = getRandomInt();
        fileName = "spectrum" + fileId + "" + next;
        return fileName;
    }

    private static synchronized int getRandomInt() {
        return random.nextInt();
    }

   

}
