<html>
<head><TITLE>VOSPEC Open Spectrum</TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<meta http-equiv="content-language" content="en">
<meta name="robots" content="all">
<meta name="description" lang="en" content="open Spectrum, VOSpec">
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
	String localFile  =   "";
        String type       =   "";

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
        document.writeln('<param name="java_code" value="esavo.vospec.standalone.VOSpecSpectrum">');
        document.writeln('<param name="java_codebase" value="http://esavo.esac.esa.int:80/vospec/">');
        document.writeln('<param name="CURRENTURL" value="' + parent.document.URL +'">');
        document.writeln('<param name="java_pluginspage" value="http://java.sun.com/products/plugin/1.1.2/plugin-install.html">');
        document.writeln('<param name="java_archive" value="lib/Jama-1.0.1.jar,lib/SEDLib_1.2_run.jar,lib/VOSpec_Subversion.jar,lib/absi-cl-samp-module-0.2.2.jar,lib/cds-savot-3.0.jar,lib/esavo-units-parser-1.2.jar,lib/jh.jar,lib/jsamp-1.0.jar,lib/nom.jar,lib/org.jar,lib/plastic.jar,lib/ptolemy.plot.jar,lib/regionMatcher.jar,lib/soap.jar,lib/swing-layout-1.0.jar,lib/swingx-0.9.5.jar,lib/stil.jar">');
        document.writeln('<param name="java_type" value="application/x-java-applet;version=1.1.2">');
	document.writeln('<param name="SERVERHOST" value="esavo.esac.esa.int">');
        document.writeln('<param name="SERVERPORT" value="@INSTALLATION_PORT@">');
 	document.writeln('<param name="RMIPORT" value="1099">');
 	document.writeln('<param name="SERVERNAME" value="AioSpecServer">');
	document.writeln('<param name="LOCALFILE" value="<%=localFile%>">');


        document.writeln('</object>');

  }else{

      document.writeln('<EMBED type="application/x-java-applet;version=1.1.2"' +
                  'java_CODE = "esavo.vospec.standalone.VOSpecSpectrum" ' +
		  'java_ARCHIVE = "lib/Jama-1.0.1.jar,lib/SEDLib_1.2_run.jar,lib/VOSpec_Subversion.jar,lib/absi-cl-samp-module-0.2.2.jar,lib/cds-savot-3.0.jar,lib/esavo-units-parser-1.2.jar,lib/jh.jar,lib/jsamp-1.0.jar,lib/nom.jar,lib/org.jar,lib/plastic.jar,lib/ptolemy.plot.jar,lib/regionMatcher.jar,lib/soap.jar,lib/swing-layout-1.0.jar,lib/swingx-0.9.5.jar,lib/stil.jar"' +
		  'WIDTH = 500  ' +
		  'HEIGHT = 120  ' +
		  'pluginspage="http://java.sun.com/products/plugin/1.1.2/plugin-install.html" ' +
		  'CODEBASE="http://esavo.esac.esa.int:80/vospec/" ' +
		  'applet_class = "VOSpecSpectrum" frame_title = "VOSpec" '  +
		  'SERVERHOST = "esavo.esac.esa.int" ' + 'SERVERPORT = "80" ' + 'RMIPORT = "1099" ' + 'SERVERNAME = "AioSpecServer" ' + 'LOCALFILE = "<%=localFile%>" ' +
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
