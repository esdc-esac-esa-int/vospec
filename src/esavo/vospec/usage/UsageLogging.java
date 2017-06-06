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
package esavo.vospec.usage;

import esavo.vospec.util.EnvironmentDefs;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Statistics logging access class
 * @author jgonzale
 *
 */
public class UsageLogging
{
	private static Log log = LogFactory.getLog(UsageLogging.class);

	private static String wsUrl = "http://esavo.esac.esa.int/VOSpecStats/services/StatsLogging";

	private static boolean accessLogged = false;

        private static String tool = "VOSpec";
	private static String toolVersion = EnvironmentDefs.getVERSION();  // Stands for the version of VOScript
	private static String queryType;    //the "type" of query to be logged: it can contain the following values: positional query, SSAP query or Registry query
	private static int dataVolume = 0;      // It is the data volume retrieved in a 'data retrieval' operation


	/**
	 * Insert an usage log of a performed query
	 * @return
	 */
	public static  String insertQueryLog(int numOps){

		if(!accessLogged){
			insertOperationLog("accessLog", 1);
			accessLogged = true;
		}

		return insertOperationLog("queryLog", numOps);

	}

	/**
	 * Insert and usage log of a performed data spectra download
	 * @return
	 */
	public static String insertDataRetrievalLog(int numOps){

		if(!accessLogged){
			insertOperationLog("accessLog", 1);
			accessLogged = true;
		}

		return insertOperationLog("dataRetrievalLog", numOps);

	}


	/**
	 * RESTFUL: Web service Invocation via POST
	 */
	private static String insertOperationLog(String op, int numOps)
	{
		StringBuffer sbInvocationResponse = new StringBuffer("");
		URL url = null;

		try
		{
			// Web service URL
			if (op.equals("accessLog"))
				url  = new URL(wsUrl+"/insertAccessLog");
			else if (op.equals("dataRetrievalLog"))
				url  = new URL(wsUrl+"/insertDataRetrievalLog");
			else if (op.equals("queryLog"))
				url  = new URL(wsUrl+"/insertQueryLog");

			// Establishing the connection
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();

			// Sending associated data to the request
			connection.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());



			//Common parameters to accessLog, queryLog and dataRetrievalLog
			wr.write(URLEncoder.encode("IPAddress", "UTF-8") + "=" + URLEncoder.encode(obtainIPAddress(), "UTF-8"));
			wr.write("&");
			wr.write(URLEncoder.encode("os", "UTF-8") + "=" + URLEncoder.encode(System.getProperty("os.name"), "UTF-8"));
			wr.write("&");
			wr.write(URLEncoder.encode("tool", "UTF-8") + "=" + URLEncoder.encode(tool, "UTF-8"));
			wr.write("&");
			wr.write(URLEncoder.encode("toolVersion", "UTF-8") + "=" + URLEncoder.encode(toolVersion, "UTF-8"));

			//Specific parameter for queryLog
			if (op.equals("queryLog"))
			{
				wr.write("&");
				wr.write(URLEncoder.encode("queryType", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(queryType), "UTF-8"));
			}

			wr.write("&");
			wr.write(URLEncoder.encode("numOps", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(numOps), "UTF-8"));

			//Specific parameter for dataRetrievalLog
			if (op.equals("dataRetrievalLog"))
			{
				wr.write("&");
				wr.write(URLEncoder.encode("volume", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(dataVolume), "UTF-8"));
			}

			wr.flush();

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK)
			{
				// Reading the content of the response and showing it on the standard output
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = reader.readLine();
				while (line != null)
				{
					sbInvocationResponse.append(line);
					line = reader.readLine();
				}

				reader.close();
				log.debug("Usage logging OK: "+url);
			}
			else {
				log.debug("Usage logging connection failed, response "+connection.getResponseMessage());
			}

			// Closing the connection
			connection.disconnect();
		}
		catch(Exception e)
		{
			log.debug("Logging failed: " + e.getMessage());
		}

		return "Invocation to the Webservice is:" + sbInvocationResponse.toString();
	}

	protected static String obtainIPAddress()
	{
		InetAddress address;
		String IPAddress = new String("");
		try
		{
			address = InetAddress.getLocalHost();
			log.debug("Usage logging: Host name: " + address.getHostName() + "; IP address: " + address.getHostAddress());
			IPAddress = address.getHostAddress();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return IPAddress;
	}


	public static void setToolVersion(String ver) {
		toolVersion = ver;
	}


	public static void setDataVolume(int volume) {
		dataVolume = volume;
	}

	public static void setQueryType(String type) {
		queryType = type;
	}

}
