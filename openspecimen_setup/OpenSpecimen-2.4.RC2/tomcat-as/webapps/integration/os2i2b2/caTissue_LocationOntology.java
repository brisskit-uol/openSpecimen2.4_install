package os2i2b2;

import java.io.*;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;

import org.w3c.dom.*;

import com.mysql.jdbc.PreparedStatement;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/*
* @author Shajid Issa
* @version 1.0 
* @date 20/01/2016
*/

public class caTissue_LocationOntology {

	private PreparedStatement pstmt1 = null ;
	private ResultSet rs1 = null;

	public static void main(String ar[])
	{
		try
		{
			new caTissue_LocationOntology().createEnumVariables();

			System.out.println("Xml File Created Successfully");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

        public static Connection getSimpleConnectionSQL() {
                //See your driver documentation for the proper format of this string :
                String DB_CONN_STRING = "";
                //Provided by your driver documentation. In this case, a MySql driver is used :
                String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
		String USER_NAME = "";
		String PASSWORD = "";
		String DB_NAME = "";
		String JDBC_URL = "";

		Connection result = null;
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		}
		catch (Exception ex){
			System.out.println("Check classpath. Cannot load db driver: " + DRIVER_CLASS_NAME);
		}

		try {
			Process p=Runtime.getRuntime().exec("$dbname");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			DB_NAME = br.readLine();
						
			p=Runtime.getRuntime().exec("$jdbcurl");
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                	JDBC_URL = br.readLine();               	               	
			
			p=Runtime.getRuntime().exec("$user");
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));              	
                	USER_NAME = br.readLine();
                	
			p=Runtime.getRuntime().exec("$password");
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	                PASSWORD = br.readLine();
	                
	                DB_CONN_STRING = JDBC_URL+DB_NAME;
	                
			result = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,	PASSWORD);
		}
                catch (SQLException e){
                        System.out.println( "Driver loaded, but cannot connect to db: " + DB_CONN_STRING);
                }
                catch (Exception e) {
                        e.printStackTrace();
                }
                return result;
        }
	

	public void createEnumVariables() throws Exception {
		try {
			pstmt1 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select distinct cas.Specimen_Class,replace(cas.SPECIMEN_TYPE,' ',''),replace(cst.NAME,' ','_') " +
							"from catissue_participant cp,catissue_specimen_protocol cs, catissue_coll_prot_reg cc, " +
							"catissue_specimen csn, catissue_specimen_coll_group csg, CATISSUE_ABSTRACT_SPECIMEN cas," +
							"catissue_specimen_position csp, CATISSUE_ABSTRACT_POSITION cap, catissue_storage_container csc," +
							"catissue_container cac,CATISSUE_COLLECTION_PROTOCOL ccp,CATISSUE_SPECIMEN_PROTOCOL cpr, CATISSUE_SITE cst " +
							"where cc.PARTICIPANT_ID=cp.IDENTIFIER and cc.COLLECTION_PROTOCOL_ID=cs.IDENTIFIER and cs.ACTIVITY_STATUS='Active' " +
							"and csn.SPECIMEN_COLLECTION_GROUP_ID=csg.IDENTIFIER and cc.IDENTIFIER=csg.COLLECTION_PROTOCOL_REG_ID and " +
							"csn.ACTIVITY_STATUS='Active' and cp.ACTIVITY_STATUS='Active' and ccp.IDENTIFIER=cc.COLLECTION_PROTOCOL_ID and " +
							"ccp.IDENTIFIER=cpr.IDENTIFIER and csg.COLLECTION_STATUS='Complete' and cas.IDENTIFIER=csn.IDENTIFIER and " +
							"csp.SPECIMEN_ID=csn.IDENTIFIER and csp.IDENTIFIER=cap.IDENTIFIER and csp.CONTAINER_ID=csc.IDENTIFIER " +
					"and csc.IDENTIFIER=cac.IDENTIFIER and csn.AVAILABLE_QUANTITY <> 0.0 and csc.SITE_ID=cst.IDENTIFIER;" ) ;


			String spec_class="", spec_type="", location="";

			HashMap<String, ArrayList<String>> specMap = new HashMap<String, ArrayList<String>>();

			boolean sts = pstmt1.execute();
			if ( sts ) {
				rs1 = pstmt1.getResultSet();

				while (rs1.next() ) {
					spec_class = rs1.getString(1);
					spec_type = rs1.getString(2);
					location = rs1.getString(3);

					ArrayList<String> list = new ArrayList<String>();

					if(specMap.containsKey(spec_class+"_"+spec_type))
						list = specMap.get(spec_class+"_"+spec_type);

					list.add(location);

					specMap.put(spec_class+"_"+spec_type,list );
				}

			}



			if(specMap!=null && specMap.size()!= 0)
			{
				for (String key : specMap.keySet()) {
					createXmlTree(key,specMap);
					System.out.println("Key: " + key + ", Value: " + specMap.get(key));
				}
			}

			closeDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void createXmlTree(String specimen, HashMap<String, ArrayList<String>> specMap)
	{
		String spec_class="", spec_type="", location="";

		ListIterator <String> listIterator = specMap.get(specimen).listIterator();

		spec_class = specimen.substring(0,specimen.indexOf("_"));
		spec_type = specimen.substring(specimen.indexOf("_")+1);

		try
		{
			DocumentBuilderFactory builderFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			//creating a new instance of a DOM to build a DOM tree.
			Document doc = docBuilder.newDocument();
			//This method creates an element node
			Element root = doc.createElement("rev:enumeratedVariable");
			//adding a node after the last child node of the specified node.
			doc.appendChild(root);

			Attr attr0 = doc.createAttribute("xmlns:rev");
			attr0.setValue("http://brisskit.le.ac.uk/xml/onyxmetadata-rev/v1.0/rev");
			root.setAttributeNode(attr0);
			Attr attr1 = doc.createAttribute("name");
			attr1.setValue("location");
			root.setAttributeNode(attr1);
			Attr attr2 = doc.createAttribute("type");
			attr2.setValue("LOCATION");
			root.setAttributeNode(attr2);
			Attr attr3 = doc.createAttribute("code");
			attr3.setValue("CAT:S."+spec_type+".location");
			root.setAttributeNode(attr3);
			Attr attr4 = doc.createAttribute("path");
			attr4.setValue("\\catissue\\"+spec_class+"\\"+spec_type+"\\S\\location");
			root.setAttributeNode(attr4);
			Attr attr5 = doc.createAttribute("hlevel");
			attr5.setValue("4");
			root.setAttributeNode(attr5);

			while(listIterator.hasNext())
			{
				location = listIterator.next();
				Element rev_variable = doc.createElement("rev:variable");
				root.appendChild(rev_variable);
				Attr attr6 = doc.createAttribute("name");
				attr6.setValue(location);
				rev_variable.setAttributeNode(attr6);
				Attr attr7 = doc.createAttribute("description");
				attr7.setValue(location);
				rev_variable.setAttributeNode(attr7);
				Attr attr8 = doc.createAttribute("code");
				attr8.setValue("CAT:S."+spec_type+".location:"+location);
				rev_variable.setAttributeNode(attr8);
			}

			//TransformerFactory instance is used to create Transformer objects.
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
			String xmlString = sw.toString();

			File file = new File("/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/files/location."+spec_class+"."+spec_type+"_"+new SimpleDateFormat( "ddMMyy" ).format(new Date())+".xml");
			BufferedWriter bw = new BufferedWriter
					(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(xmlString);
			bw.flush();
			bw.close();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeDatabase() {
		try {
			rs1.close() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			pstmt1.close () ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			getSimpleConnectionSQL().close () ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
