<html>
<head><TITLE>VOSPEC Open Applet</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<meta http-equiv="content-language" content="en">
<meta name="robots" content="all">
<meta name="description" lang="en" content="open vospec from seraching parameter form">
<meta name="keywords" content="vospec,esac,esa,european space agency,ssap,dal,spectra,spectrum,dimensional equation,ivoa,work with spectra,display spectra,overlapping,graph,space,pedro osuna,jesus salgado,isa barbarisi,christophe arviset,villafranca,castillo,madrid,spain">

</head>
<body>

<center>
<table cellspacing=0 cellpadding=0 cols=3 width="100%" nosavewidth="100%" >
 <tr nosave>
  <th rowspan="2" width="100%" nosave center>
   <!--<img SRC="/vospec/images/banner.jpg" nosave  align=center> -->
  </th>
 </tr>

</table>
</center>

<br>
<center>

<%
	String name         =   "";
        String server       =   "";
	String pos          =   "";
	String size         =   "";
        String localFile    =   "";
        String band         =   "";
        String time         =   "";


        String serverPartial;
        String namePartial;
	int l = 1;
        int ct = 0;

	while((serverPartial = request.getParameter("server" + l)) != null ) {

                        if(!serverPartial.equals("")) {
			ct++;
			if(l != 1) server 	= server 	+ "--";
			if(l != 1) name 	= name 		+ "--";

			server = server + serverPartial;

                        namePartial = request.getParameter("name" + l);

                        if(namePartial != null) {

                                if(! namePartial.equals("")) {
					name = name + namePartial;
				} else {
					name = name + "Server" + l;
				}
			} else {
				name = name + "Server" + l;
			}

		}

		l++;

	}

        pos = request.getParameter("POS");
	if(pos != null) {
		pos = pos.replaceAll("\\(","");
		pos = pos.replaceAll("\\)","");
		pos = pos.replaceAll("\\;","");
		pos = pos.replaceAll("\\<","");
		pos = pos.replaceAll("\\*","");
		pos = pos.replaceAll("\\?","");
		pos = pos.replaceAll("\\>","");
	}
	
	size = request.getParameter("SIZE");
	if(size != null) {
		size = size.replaceAll("\\(","");
		size = size.replaceAll("\\)","");
		size = size.replaceAll("\\;","");
		size = size.replaceAll("\\<","");
		size = size.replaceAll("\\*","");
		size = size.replaceAll("\\?","");
		size = size.replaceAll("\\>","");
	}
	
        band = request.getParameter("BAND");
	if(band != null) {
		band = band.replaceAll("\\(","");
		band = band.replaceAll("\\)","");
		band = band.replaceAll("\\;","");
		band = band.replaceAll("\\<","");
		band = band.replaceAll("\\*","");
		band = band.replaceAll("\\?","");
		band = band.replaceAll("\\>","");
	}
	
        time = request.getParameter("TIME");
	if(time != null) {
		time = time.replaceAll("\\(","");
		time = time.replaceAll("\\)","");
		time = time.replaceAll("\\;","");
		time = time.replaceAll("\\<","");
		time = time.replaceAll("\\*","");
		time = time.replaceAll("\\?","");
		time = time.replaceAll("\\>","");
	}

        localFile = request.getParameter("LOCALFILE");
	if(localFile != null) {
		localFile = localFile.replaceAll("\\(","");
		localFile = localFile.replaceAll("\\)","");
		localFile = localFile.replaceAll("\\;","");
		localFile = localFile.replaceAll("\\<","");
		localFile = localFile.replaceAll("\\*","");
		localFile = localFile.replaceAll("\\?","");
		localFile = localFile.replaceAll("\\>","");
	}
	
%>


<script LANGUAGE="JavaScript" TYPE="text/javascript">
<!--
var agt = navigator.userAgent;
var _ie = (agt.indexOf("MSIE") > 0);
var _ns = (navigator.appName.indexOf("Netscape") >= 0 );


     

  if (_ie == true) {

        document.writeln('<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" width="400" height="100" align="baseline">');
        document.writeln('<param name="java_code" value="esavo.vospec.standalone.VOSpecApplet">');
        document.writeln('<param name="java_codebase" value="http://esavo.esac.esa.int:80/vospec/">');
        document.writeln('<param name="CURRENTURL" value="' + parent.document.URL +'">');
        document.writeln('<param name="java_pluginspage" value="http://java.sun.com/products/plugin/1.1.2/plugin-install.html">');
        document.writeln('<param name="java_archive" value="lib/Jama-1.0.1.jar,lib/SEDLib_1.2_run.jar,lib/VOSpec_Subversion.jar,lib/absi-cl-samp-module-0.2.2.jar,lib/cds-savot-3.0.jar,lib/esavo-units-parser-1.2.jar,lib/jh.jar,lib/jsamp-1.0.jar,lib/nom.jar,lib/org.jar,lib/plastic.jar,lib/ptolemy.plot.jar,lib/regionMatcher.jar,lib/soap.jar,lib/swing-layout-1.0.jar,lib/swingx-0.9.5.jar,lib/stil.jar">');
        document.writeln('<param name="java_type" value="application/x-java-applet;version=1.1.2">');
	document.writeln('<param name="SERVERHOST" value="esavo.esac.esa.int">');
        document.writeln('<param name="SERVERPORT" value="80">');
 	document.writeln('<param name="RMIPORT" value="1099">');
 	document.writeln('<param name="SERVERNAME" value="AioSpecServer">');
	document.writeln('<param name="POS" value="<%=pos%>">');
	document.writeln('<param name="SIZE" value="<%=size%>">');
        document.writeln('<param name="BAND" value="<%=band%>">');
        document.writeln('<param name="TIME" value="<%=time%>">');
	document.writeln('<param name="SSASERVERNAME" value="<%=name%>">');
	document.writeln('<param name="SSASERVERURL" value="<%=server%>">');
	document.writeln('<param name="CT" value="<%=ct%>">');
	document.writeln('<param name="LOCALFILE" value="<%=localFile%>">');

        document.writeln('</object>');

  }else{

      document.writeln('<EMBED type="application/x-java-applet;version=1.1.2"' +
                  'java_CODE = "esavo.vospec.standalone.VOSpecApplet" ' +
		  'java_ARCHIVE = "lib/Jama-1.0.1.jar,lib/SEDLib_1.2_run.jar,lib/VOSpec_Subversion.jar,lib/absi-cl-samp-module-0.2.2.jar,lib/cds-savot-3.0.jar,lib/esavo-units-parser-1.2.jar,lib/jh.jar,lib/jsamp-1.0.jar,lib/nom.jar,lib/org.jar,lib/plastic.jar,lib/ptolemy.plot.jar,lib/regionMatcher.jar,lib/soap.jar,lib/swing-layout-1.0.jar,lib/swingx-0.9.5.jar,lib/stil.jar"' +
		  'WIDTH = 500  ' +
		  'HEIGHT = 120  ' +
		  'pluginspage="http://java.sun.com/products/plugin/1.1.2/plugin-install.html" ' +
		  'CODEBASE="http://esavo.esac.esa.int:80/vospec/" ' +
		  'applet_class = "VOSpecApplet" frame_title = "VOSpec" '  +
		  'SERVERHOST = "esavo.esac.esa.int" ' + 'SERVERPORT = "80" '+ 'RMIPORT = "1099" ' + 'SERVERNAME = "AioSpecServer" ' + 'POS = "<%=pos%>" ' + 'SIZE = "<%=size%>" ' + 'SSASERVERNAME = "<%=name%>" ' + 'SSASERVERURL = "<%=server%>" ' + 'CT = "<%=ct%>" ' + 'LOCALFILE = "<%=localFile%>" ' +
		  '</EMBED>');
  }
// -->
</script>
<noscript>
  <FONT COLOR="Red">
  <HR size="1" noshade>
  <STRONG>
  You don't seem to have JavaScript enabled or your browser doesn't support it, to run VOSPEC you need to update your components<br>
  </strong>
  <HR size="1" noshade>
  </STRONG>
  </FONT>
</noscript>

</center>
</body>
</html>
