package os2i2b2;

import java.io.*;
import java.net.URI;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;
import java.util.Random;

import org.w3c.dom.*;

import com.mysql.jdbc.PreparedStatement;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

/*
* @author Shajid Issa
* @version 1.0 
* @date 20/01/2016
*/

public class caTissueParser {

	private PreparedStatement pstmt1 = null ;
	private ResultSet rs1 = null;

	private PreparedStatement pstmt2 = null ;
	private ResultSet rs2 = null;

	private PreparedStatement pstmt3 = null ;
	private ResultSet rs3 = null;

	private PreparedStatement pstmt4 = null ;
	private ResultSet rs4 = null;

	private PreparedStatement pstmt5 = null ;
	private ResultSet rs5 = null;

	private PreparedStatement pstmt6 = null ;
	private ResultSet rs6 = null;

	private PreparedStatement pstmt7 = null ;
	private ResultSet rs7 = null;

	private PreparedStatement pstmt8 = null ;
	private ResultSet rs8 = null;

	private PreparedStatement pstmt9 = null ;
	private ResultSet rs9 = null;

	private PreparedStatement pstmt10 = null ;
	private ResultSet rs10 = null;

	private PreparedStatement pstmt11 = null ;
	private ResultSet rs11 = null;

	private PreparedStatement pstmt12 = null ;
	private ResultSet rs12 = null;

	private PreparedStatement pstmt13 = null ;
	private ResultSet rs13 = null;

	private PreparedStatement pstmt14 = null ;
	private ResultSet rs14 = null;

	private int obs_count=0,i=0,j=1;

	public static void main(String ar[])
	{
		try
		{
			new caTissueParser().createXmlTree();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

	private Document document()
	{
		DocumentBuilderFactory builderFactory = null;
		Document doc = null;
		try
		{
			builderFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			//creating a new instance of a DOM to build a DOM tree.
			doc = docBuilder.newDocument();
		}
		catch(Exception e)
		{
			System.out.println(e);
		}

		return doc;
	}

	/** Uses DriverManager.  */
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

	public void createXmlTree () throws Exception {
		String molTable="", colTable="", recTable="", frozTable="";

		try {
			Document doc=document();
			pstmt2 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from (select NAME from dyextn_database_properties"+
							" where identifier=(select Identifier from dyextn_table_properties"+
							" where ABSTRACT_ENTITY_ID =(select Identifier from dyextn_abstract_metadata"+
					" where NAME like 'CollectionEventParameters'))) ce;");

			pstmt2.execute();
			rs2 = pstmt2.getResultSet();

			if(rs2.next())
				colTable=rs2.getString(1);

			pstmt3 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from (select NAME from dyextn_database_properties"+
							" where identifier=(select Identifier from dyextn_table_properties"+
							" where ABSTRACT_ENTITY_ID =(select Identifier from dyextn_abstract_metadata"+
					" where NAME like 'ReceivedEventParameters'))) re;");

			pstmt3.execute();
			rs3 = pstmt3.getResultSet();

			if(rs3.next())
				recTable=rs3.getString(1);

			pstmt12 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from (select NAME from dyextn_database_properties"+
							" where identifier=(select Identifier from dyextn_table_properties"+
							" where ABSTRACT_ENTITY_ID =(select Identifier from dyextn_abstract_metadata"+
					" where NAME like 'FrozenEventParameters'))) re;");

			pstmt12.execute();
			rs12 = pstmt12.getResultSet();

			if(rs12.next())
				frozTable=rs12.getString(1);

			pstmt4 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from (select NAME from dyextn_database_properties"+
							" where identifier=(select Identifier from dyextn_table_properties"+
							" where ABSTRACT_ENTITY_ID =(select Identifier from dyextn_abstract_metadata"+
					" where NAME like 'MolecularSpecimenReviewParameters'))) me;");

			pstmt4.execute();
			rs4 = pstmt4.getResultSet();

			if(rs4.next())
				molTable=rs4.getString(1);

			pstmt5 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from "+rs4.getString(1)+";");
			pstmt5.execute();
			rs5 = pstmt5.getResultSet();
			ResultSetMetaData md = rs5.getMetaData();

			pstmt6 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from "+colTable+";");
			pstmt6.execute();
			rs6 = pstmt6.getResultSet();
			ResultSetMetaData cmd = rs6.getMetaData();


			pstmt7 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from "+recTable+";");
			pstmt7.execute();
			rs7 = pstmt7.getResultSet();
			ResultSetMetaData rmd = rs7.getMetaData();

			pstmt13 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select * from "+frozTable+";");
			pstmt13.execute();
			rs13 = pstmt13.getResultSet();
			ResultSetMetaData fmd = rs13.getMetaData();

			pstmt1 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
					"select replace(cp.LAST_NAME,' ',''),"+
							" replace(cas.Specimen_Class,' ',''),replace(cas.SPECIMEN_TYPE,' ',''),replace(cs.TITLE,' ',''),cs.START_DATE,cs.END_DATE, replace(csg.NAME,' ',''),"+
							" csg.ENCOUNTER_TIMESTAMP,csg.AGE_AT_COLLECTION, replace(csn.LABEL,' ',''), csn.AVAILABLE, replace(csn.BARCODE,' ',''),"+
							" cas.INITIAL_QUANTITY, replace(csg.COLLECTION_STATUS,' ',''), csn.CREATED_ON_DATE,csn.AVAILABLE_QUANTITY, "+
							" replace(cp.ACTIVITY_STATUS,' ',''), cp.BIRTH_DATE, replace(cp.ETHNICITY,' ',''), replace(cp.GENDER,' ',''),csn.SPP_APPLICATION_ID,replace(cpr.TITLE,' ',''),replace(cst.NAME,' ','_') from catissue_participant cp,"+
							" catissue_specimen_protocol cs, catissue_coll_prot_reg cc, catissue_specimen csn,CATISSUE_SITE cst,"+
							" catissue_specimen_coll_group csg, CATISSUE_ABSTRACT_SPECIMEN cas,catissue_specimen_position csp, CATISSUE_ABSTRACT_POSITION cap," +
							"catissue_storage_container csc,catissue_container cac,CATISSUE_COLLECTION_PROTOCOL ccp,CATISSUE_SPECIMEN_PROTOCOL cpr"+
							" where cc.PARTICIPANT_ID=cp.IDENTIFIER and cc.COLLECTION_PROTOCOL_ID=cs.IDENTIFIER and cs.ACTIVITY_STATUS='Active'"+
							" and csn.SPECIMEN_COLLECTION_GROUP_ID=csg.IDENTIFIER and cc.IDENTIFIER=csg.COLLECTION_PROTOCOL_REG_ID "+
							" and csn.ACTIVITY_STATUS='Active' and cp.ACTIVITY_STATUS='Active' and ccp.IDENTIFIER=cc.COLLECTION_PROTOCOL_ID and ccp.IDENTIFIER=cpr.IDENTIFIER"+
							" and csg.COLLECTION_STATUS='Complete' and cas.IDENTIFIER=csn.IDENTIFIER and csc.SITE_ID=cst.IDENTIFIER" +
					" and csp.SPECIMEN_ID=csn.IDENTIFIER and csp.IDENTIFIER=cap.IDENTIFIER and csp.CONTAINER_ID=csc.IDENTIFIER and csc.IDENTIFIER=cac.IDENTIFIER and csn.AVAILABLE_QUANTITY <> 0.0 order by cp.LAST_NAME;" ) ;

			getSimpleConnectionSQL().close();

			
			Element root = doc.createElement("pdo:patient_data");
			doc.appendChild(root);

			Attr attr0 = doc.createAttribute("xmlns:pdo");
			attr0.setValue("http://www.i2b2.org/xsd/hive/pdo/1.1/pdo");
			root.setAttributeNode(attr0);

			caTissueParser_EventSet c_event_set = null;
			caTissueParser_PidSet c_pid_set = null;
			caTissueParser_EidSet c_eid_set = null;
			//caTissueParser_PatientSet c_patient_set = null;
			caTissueParser_Observations c_observations = null;

			ArrayList <caTissueParser_EventSet> eventList = new ArrayList <caTissueParser_EventSet>();
			ArrayList <caTissueParser_PidSet> pidList = new ArrayList <caTissueParser_PidSet>();
			ArrayList <caTissueParser_EidSet> eidList = new ArrayList <caTissueParser_EidSet>();
			ArrayList <caTissueParser_PatientSet> patientList = new ArrayList <caTissueParser_PatientSet>();
			ArrayList <caTissueParser_Observations> observationList = new ArrayList <caTissueParser_Observations>();

			ArrayList <String> dateList1 = new ArrayList<String>();
			ArrayList <String> bptList1 = new ArrayList<String>();

			ArrayList <String> bptList = new ArrayList<String>();

			boolean sts = pstmt1.execute();
			if ( sts ) {
				rs1 = pstmt1.getResultSet();
				String todaysDate = dateFormat(new Date());
				while (rs1.next()) {
					ArrayList <caTissueParser_Observations> cp_observationList = new ArrayList <caTissueParser_Observations>();
					ArrayList <caTissueParser_EventSet> cp_eventList = new ArrayList <caTissueParser_EventSet>();
					ArrayList <caTissueParser_PidSet> cp_pidList = new ArrayList <caTissueParser_PidSet>();
					ArrayList <caTissueParser_EidSet> cp_eidList = new ArrayList <caTissueParser_EidSet>();
					ArrayList <caTissueParser_PatientSet> cp_patientList = new ArrayList <caTissueParser_PatientSet>();

					c_event_set = new caTissueParser_EventSet();
					c_pid_set = new caTissueParser_PidSet();
					c_eid_set = new caTissueParser_EidSet();
					//c_patient_set = new caTissueParser_PatientSet();
					c_observations = new caTissueParser_Observations();

					String colDate="", recDate="", frozDate="", colContainer="", colProc="", recQuality="", abAt280="", abAt260="", ratio="", position="", dateItem1="", bptItem1="";

					int date1Inc=0, bpt1Inc=0;

					String spp_app_id=rs1.getString(21), cond="";

					if(spp_app_id==null)
					{
						spp_app_id = "caa.SPP_APP_IDENTIFIER IS NULL";
						cond = "caa.SPECIMEN_ID=csn.IDENTIFIER";
					}
					else
					{
						spp_app_id = "caa.SPP_APP_IDENTIFIER="+spp_app_id;
						cond = "caa.SPP_APP_IDENTIFIER=csn.SPP_APPLICATION_ID";
					}

					pstmt8 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
							"select replace(cep."+cmd.getColumnName(2)+",' ',''),replace(cep."+cmd.getColumnName(3)+",' ',''),cse.TIMESTAMP"+
									" from catissue_action_application caa, catissue_abstract_application cse, catissue_specimen csn,"+colTable+" cep"+
									" where caa.ACTION_APP_RECORD_ENTRY_ID=cep."+cmd.getColumnName(5)+" and "+spp_app_id+" and caa.IDENTIFIER=cse.IDENTIFIER"+
									" and "+cond+" and csn.LABEL='"+rs1.getString(10)+"' and csn.BARCODE='"+rs1.getString(12)+"';");

					pstmt8.execute();
					rs8 = pstmt8.getResultSet();

					getSimpleConnectionSQL().close();

					pstmt9 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
							"select replace(replace(rep."+rmd.getColumnName(2)+",' ',''),',',''),cse.TIMESTAMP"+
									" from catissue_action_application caa, catissue_abstract_application cse, catissue_specimen csn,"+recTable+" rep"+
									" where caa.ACTION_APP_RECORD_ENTRY_ID=rep."+rmd.getColumnName(4)+" and "+spp_app_id+" and caa.IDENTIFIER=cse.IDENTIFIER"+
									" and "+cond+" and csn.LABEL='"+rs1.getString(10)+"' and csn.BARCODE='"+rs1.getString(12)+"';");

					pstmt9.execute();
					rs9 = pstmt9.getResultSet();

					getSimpleConnectionSQL().close();

					pstmt14 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
							"select replace(replace(rep."+fmd.getColumnName(2)+",' ',''),',',''),cse.TIMESTAMP"+
									" from catissue_action_application caa, catissue_abstract_application cse, catissue_specimen csn,"+frozTable+" rep"+
									" where caa.ACTION_APP_RECORD_ENTRY_ID=rep."+fmd.getColumnName(4)+" and "+spp_app_id+" and caa.IDENTIFIER=cse.IDENTIFIER"+
									" and "+cond+" and csn.LABEL='"+rs1.getString(10)+"' and csn.BARCODE='"+rs1.getString(12)+"';");
					pstmt14.execute();
					rs14 = pstmt14.getResultSet();

					getSimpleConnectionSQL().close();

					pstmt10 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
							"select mep."+md.getColumnName(2)+",mep."+md.getColumnName(3)+",mep."+md.getColumnName(8)+
							" from catissue_action_app_rcd_entry car, catissue_action_application caa, catissue_specimen csn,"+molTable+" mep"+
							" where car.IDENTIFIER=caa.ACTION_APP_RECORD_ENTRY_ID and caa.SPECIMEN_ID=csn.IDENTIFIER and caa.ACTION_APP_RECORD_ENTRY_ID=mep."+md.getColumnName(10)+
							" and csn.LABEL='"+rs1.getString(10)+"' and csn.BARCODE='"+rs1.getString(12)+"';");
					pstmt10.execute();
					rs10 = pstmt10.getResultSet();

					getSimpleConnectionSQL().close();



					pstmt11 = (PreparedStatement) getSimpleConnectionSQL().prepareStatement(
							"select cc.name,cap.POSITION_DIMENSION_ONE,cap.POSITION_DIMENSION_TWO "+
									"from catissue_specimen_position csp, catissue_specimen csn,CATISSUE_ABSTRACT_POSITION cap," +
									"catissue_storage_container csc,catissue_container cc " +
									"where csp.SPECIMEN_ID=csn.IDENTIFIER and csp.IDENTIFIER=cap.IDENTIFIER " +
									"and csp.CONTAINER_ID=csc.IDENTIFIER and csc.IDENTIFIER=cc.IDENTIFIER "+
									"and csn.LABEL='"+rs1.getString(10)+"' and csn.BARCODE='"+rs1.getString(12)+"'");
					pstmt11.execute();
					rs11 = pstmt11.getResultSet();

					getSimpleConnectionSQL().close();

					String currentDate = dateFormat(new Date()),hospital="";//sDate = null,age_at_coll="0";

					//eventDate = currentDate;

					if(rs8.next())
					{
						colProc = rs8.getString(1);
						colContainer = rs8.getString(2);
						colDate = dateFormat(new java.sql.Date(rs8.getTimestamp(3).getTime()));
					}

					if(rs9.next())
					{
						recQuality = rs9.getString(1);
						recDate = dateFormat(new java.sql.Date(rs9.getTimestamp(2).getTime()));
					}

					if(rs14.next())
					{
						frozDate = dateFormat(new java.sql.Date(rs14.getTimestamp(2).getTime()));
					}

					if(rs10.next())
					{
						abAt260 = rs10.getString(1);
						ratio = rs10.getString(2);
						abAt280 = rs10.getString(3);
					}

					if(rs11.next())
					{
						hospital=""+rs11.getString(1).charAt(0)+rs11.getString(1).charAt(rs11.getString(1).indexOf(" ")+1)+rs11.getString(1).substring(rs11.getString(1).indexOf("_"));
						position = hospital+"-"+rs11.getString(2)+":"+rs11.getString(3);
					}

					c_event_set.setUpdate_date(currentDate);
					dateList1.add(currentDate);
					c_event_set.setImport_date(currentDate);
					c_event_set.setDownload_date(currentDate);
					c_event_set.setSourcesystem_cd("BRISSKIT");
					c_event_set.setUpload_id("1");
					c_event_set.setEvent_id_body(rs1.getString(1)+"_"+todaysDate);
					c_event_set.setEvent_source("BRISSKIT");
					c_event_set.setPatient_id_body(rs1.getString(1));
					bptList1.add(rs1.getString(1));
					c_event_set.setPatient_source("BRISSKIT");
					c_event_set.setParam1_body("F");
					c_event_set.setColumn1("ACTIVE_STATUS_CD");
					c_event_set.setName1("active status");
					c_event_set.setParam2_body("@");
					c_event_set.setColumn2("INOUT_CD");
					c_event_set.setName2("");
					c_event_set.setParam3_body("@");
					c_event_set.setColumn3("LOCATION_CD");
					c_event_set.setName3("");
					c_event_set.setParam4_body("@");
					c_event_set.setColumn4("LOCATION_PATH");
					c_event_set.setName4("");
					c_event_set.setStart_date(dateFormat(rs1.getDate(5)));

					java.sql.Date eve_enddate=null;
					if(rs1.getDate(6) != null)
						eve_enddate=rs1.getDate(6);

					if(eve_enddate != null)
						c_event_set.setEnd_date(dateFormat(eve_enddate));
					else
						c_event_set.setEnd_date("@");

					ListIterator <String> dateIterator1 = dateList1.listIterator();
					ListIterator <String> bptIterator1 = bptList1.listIterator();


					while(dateIterator1.hasNext())
					{
						dateItem1 = dateIterator1.next();

						if(dateItem1.equals(currentDate))
							date1Inc++;
					}


					while(bptIterator1.hasNext())
					{
						bptItem1 = bptIterator1.next();

						if(bptItem1.equals(rs1.getString(1)))
							bpt1Inc++;
					}


					if(date1Inc==1 && bpt1Inc==1)
						eventList.add(c_event_set);

					c_pid_set.setBpt_number(rs1.getString(1));
					c_pid_set.setSource("BRISSKIT");
					c_pid_set.setStatus(rs1.getString(17));
					c_pid_set.setUpdate_date(currentDate);
					c_pid_set.setDownload_date(currentDate);
					c_pid_set.setImport_date(currentDate);
					c_pid_set.setSourcesystem_cd("BRISSKIT");
					c_pid_set.setUpload_id("1");

					if(date1Inc==1 && bpt1Inc==1)
						pidList.add(c_pid_set);

					c_eid_set.setBpt_number(rs1.getString(1)+"_"+todaysDate);
					c_eid_set.setSource("BRISSKIT");
					c_eid_set.setStatus(rs1.getString(17));
					c_eid_set.setUpdate_date(currentDate);
					c_eid_set.setDownload_date(currentDate);
					c_eid_set.setImport_date(currentDate);
					c_eid_set.setSourcesystem_cd("BRISSKIT");
					c_eid_set.setUpload_id("1");

					if(date1Inc==1 && bpt1Inc==1)
						eidList.add(c_eid_set);

					

					c_observations.setUpdate_date(currentDate);
					c_observations.setDownload_date(currentDate);
					c_observations.setImport_date(currentDate);
					c_observations.setSourcesystem_cd("BRISSKIT");
					c_observations.setUpload_id("1");
					c_observations.setSpec_class(rs1.getString(2));
					c_observations.setEvent_id_body(rs1.getString(1)+"_"+todaysDate);
					c_observations.setEvent_source("BRISSKIT");
					c_observations.setPatient_id_body(rs1.getString(1));
					c_observations.setPatient_source("BRISSKIT");
					c_observations.setConcept_cd_body(rs1.getString(3));
					c_observations.setConcept_cd_name("BRISSKIT");
					c_observations.setObserver_cd_body("@");
					c_observations.setObserver_cd_source("BRISSKIT");
					c_observations.setStart_date(dateFormat(rs1.getDate(5)));
					c_observations.setModifier_cd_body("@");
					c_observations.setModifier_cd_name("missing value");
					c_observations.setValuetype_cd_body("T");
					c_observations.setUnits_cd_body("@");
					c_observations.setQuantity_num("@");

					java.sql.Date enddate=null;

					if(rs1.getDate(6) != null)
						enddate=rs1.getDate(6);

					if(enddate != null)
						c_observations.setEnd_date(dateFormat(enddate));
					else
						c_observations.setEnd_date("@");

					c_observations.setLocation_cd_body("@");
					c_observations.setLocation_cd_name("missing value");
					c_observations.setInitial_quantity(rs1.getString(13));
					c_observations.setAvailable_quantity(rs1.getString(16));
					c_observations.setCollection_container(colContainer);
					c_observations.setCollection_procedure(colProc);
					c_observations.setReceived_quality(recQuality);
					c_observations.setAbsorbance_at260(abAt260);
					c_observations.setAbsorbance_at280(abAt280);
					c_observations.setRatio28S_to18S(ratio);
					c_observations.setBarcode(rs1.getString(12));
					c_observations.setStorage(position);
					c_observations.setColl_date(colDate);
					c_observations.setRec_date(recDate);
					c_observations.setFroz_date(frozDate);
					c_observations.setCollection_protocol(rs1.getString(22));
					c_observations.setSite_location(rs1.getString(23));

					observationList.add(c_observations);


					if(!bptList.contains(rs1.getString(1))){
						bptList.add(rs1.getString(1));
						i++;
					}

					boolean isExtraData=false;

					if ((i%5==0 && i!=0) || (i>=5))
					{
						if(i>5)
							bptList.remove(bptList.size()-1);

						if(!bptList.contains(rs1.getString(1)))
						{
							cp_observationList.add(c_observations);
							cp_eventList.add(eventList.get(eventList.size()-1));
							cp_pidList.add(pidList.get(pidList.size()-1));
							cp_eidList.add(eidList.get(eidList.size()-1));
							//cp_patientList.add(patientList.get(patientList.size()-1));

							eventList.remove(eventList.size()-1);
							pidList.remove(pidList.size()-1);
							eidList.remove(eidList.size()-1);
							//patientList.remove(patientList.size()-1);
							observationList.remove(observationList.size()-1);

							isExtraData=true;
							i=5;
						}
					}

					if(i%5==0 && i!=0 && isExtraData)
					{
						xmlSplitsCreation(doc,root,eventList,pidList,eidList,patientList,observationList);
						eventList.clear();
						pidList.clear();
						eidList.clear();
						patientList.clear();
						observationList.clear();
						bptList.clear();

						if(cp_observationList.size()!=0)
							observationList.add(cp_observationList.get(0));
						if(cp_eventList.size()!=0)
							eventList.add(cp_eventList.get(0));
						if(cp_eidList.size()!=0)
							eidList.add(cp_eidList.get(0));
						if(cp_pidList.size()!=0)
							pidList.add(cp_pidList.get(0));
						if(cp_patientList.size()!=0)
							patientList.add(cp_patientList.get(0));

						doc=document();
						root = doc.createElement("pdo:patient_data");
						doc.appendChild(root);

						attr0 = doc.createAttribute("xmlns:pdo");
						attr0.setValue("http://www.i2b2.org/xsd/hive/pdo/1.1/pdo");
						root.setAttributeNode(attr0);
					}
				}

				if(i!=0)
				{
					xmlSplitsCreation(doc,root,eventList,pidList,eidList,patientList,observationList);
				}

				System.out.println("Xml File(s) Created Successfully");

				closeDatabase();
				System.out.println("Observations count:"+obs_count);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void closeDatabase() {
		try {
			getSimpleConnectionSQL().close();
			rs1.close() ;
			rs2.close() ;
			rs3.close() ;
			rs4.close() ;
			rs5.close() ;
			rs6.close() ;
			rs7.close() ;
			rs8.close() ;
			rs9.close() ;
			rs10.close() ;
			rs11.close() ;
			rs12.close() ;
			rs13.close() ;
			rs14.close() ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			pstmt1.close () ;
			pstmt2.close () ;
			pstmt3.close () ;
			pstmt4.close () ;
			pstmt5.close () ;
			pstmt6.close () ;
			pstmt7.close () ;
			pstmt8.close () ;
			pstmt9.close () ;
			pstmt10.close () ;
			pstmt11.close () ;
			pstmt12.close () ;
			pstmt13.close () ;
			pstmt14.close () ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			getSimpleConnectionSQL().close () ;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void eventSet(Document doc, Element root, ArrayList <caTissueParser_EventSet> eventList)
			throws SQLException
			{
		ListIterator <caTissueParser_EventSet> listIterator = eventList.listIterator();
		caTissueParser_EventSet eventSet = null;

		Element event_set = doc.createElement("pdo:event_set");
		root.appendChild(event_set);

		while(listIterator.hasNext())
		{
			eventSet = listIterator.next();
			Element events = doc.createElement("event");
			event_set.appendChild(events);
			Attr attr0_event = doc.createAttribute("update_date");
			attr0_event.setValue(eventSet.getUpdate_date());
			events.setAttributeNode(attr0_event);
			Attr attr0_1_event = doc.createAttribute("download_date");
			attr0_1_event.setValue(eventSet.getDownload_date());
			events.setAttributeNode(attr0_1_event);
			Attr attr0_2_event = doc.createAttribute("import_date");
			attr0_2_event.setValue(eventSet.getImport_date());
			events.setAttributeNode(attr0_2_event);
			Attr attr0_3_event = doc.createAttribute("sourcesystem_cd");
			attr0_3_event.setValue(eventSet.getSourcesystem_cd());
			events.setAttributeNode(attr0_3_event);
			Attr attr0_4_event = doc.createAttribute("upload_id");
			attr0_4_event.setValue(eventSet.getUpload_id());
			events.setAttributeNode(attr0_4_event);

			Element eve_element2 = doc.createElement("event_id");
			events.appendChild(eve_element2);
			Text eve_text1 = doc.createTextNode(eventSet.getEvent_id_body());
			eve_element2.appendChild(eve_text1);
			Attr eve_attr = doc.createAttribute("source");
			eve_attr.setValue(eventSet.getEvent_source());
			eve_element2.setAttributeNode(eve_attr);

			Element eve_element3 = doc.createElement("patient_id");
			events.appendChild(eve_element3);
			Text eve_text2 = doc.createTextNode(eventSet.getPatient_id_body());
			eve_element3.appendChild(eve_text2);
			Attr eve_attr2 = doc.createAttribute("source");
			eve_attr2.setValue(eventSet.getPatient_source());
			eve_element3.setAttributeNode(eve_attr2);

			Element eve_element4 = doc.createElement("param");
			events.appendChild(eve_element4);
			Text eve_text2_1 = doc.createTextNode(eventSet.getParam1_body());
			eve_element4.appendChild(eve_text2_1);
			Attr eve_attr2_1 = doc.createAttribute("column");
			eve_attr2_1.setValue(eventSet.getColumn1());
			eve_element4.setAttributeNode(eve_attr2_1);
			Attr eve_attr2_2 = doc.createAttribute("name");
			eve_attr2_2.setValue(eventSet.getName1());
			eve_element4.setAttributeNode(eve_attr2_2);

			Element eve_element5 = doc.createElement("param");
			events.appendChild(eve_element5);
			Text eve_text2_3 = doc.createTextNode(eventSet.getParam2_body());
			eve_element5.appendChild(eve_text2_3);
			Attr eve_attr2_3 = doc.createAttribute("column");
			eve_attr2_3.setValue(eventSet.getColumn2());
			eve_element5.setAttributeNode(eve_attr2_3);
			Attr eve_attr2_4 = doc.createAttribute("name");
			eve_attr2_4.setValue(eventSet.getName2());
			eve_element5.setAttributeNode(eve_attr2_4);

			Element eve_element6 = doc.createElement("param");
			events.appendChild(eve_element6);
			Text eve_text2_4 = doc.createTextNode(eventSet.getParam3_body());
			eve_element6.appendChild(eve_text2_4);
			Attr eve_attr2_5 = doc.createAttribute("column");
			eve_attr2_5.setValue(eventSet.getColumn3());
			eve_element6.setAttributeNode(eve_attr2_5);
			Attr eve_attr2_6 = doc.createAttribute("name");
			eve_attr2_6.setValue(eventSet.getName3());
			eve_element6.setAttributeNode(eve_attr2_6);

			Element eve_element7 = doc.createElement("param");
			events.appendChild(eve_element7);
			Text eve_text2_5 = doc.createTextNode(eventSet.getParam4_body());
			eve_element7.appendChild(eve_text2_5);
			Attr eve_attr2_7 = doc.createAttribute("column");
			eve_attr2_7.setValue(eventSet.getColumn4());
			eve_element7.setAttributeNode(eve_attr2_7);
			Attr eve_attr2_8 = doc.createAttribute("name");
			eve_attr2_8.setValue(eventSet.getName4());
			eve_element7.setAttributeNode(eve_attr2_8);

			Element eve_element8 = doc.createElement("start_date");
			events.appendChild(eve_element8);
			Text eve_text4 = doc.createTextNode(eventSet.getStart_date());
			eve_element8.appendChild(eve_text4);

			String eve_enddate="";
			if(eventSet.getEnd_date() != null)
				eve_enddate=eventSet.getEnd_date();

			Element eve_element9 = doc.createElement("end_date");
			events.appendChild(eve_element9);
			Text eve_text8 = doc.createTextNode(eve_enddate);
			eve_element9.appendChild(eve_text8);
		}
			}

	private void pidSet(Document doc, Element root, ArrayList <caTissueParser_PidSet> pidList)
			throws SQLException
			{
		ListIterator <caTissueParser_PidSet> listIterator = pidList.listIterator();
		caTissueParser_PidSet pidSet = null;

		Element pidset = doc.createElement("pdo:pid_set");
		root.appendChild(pidset);

		while(listIterator.hasNext())
		{
			pidSet = listIterator.next();
			Element child1 = doc.createElement("pid");
			pidset.appendChild(child1);
			Element child2 = doc.createElement("patient_id");
			child1.appendChild(child2);
			Text text = doc.createTextNode(pidSet.getBpt_number());
			child2.appendChild(text);
			Attr p_attr_0 = doc.createAttribute("source");
			p_attr_0.setValue(pidSet.getSource());
			child2.setAttributeNode(p_attr_0);
			Attr p_attr_1 = doc.createAttribute("status");
			p_attr_1.setValue(pidSet.getStatus());
			child2.setAttributeNode(p_attr_1);
			Attr p_attr0_event = doc.createAttribute("update_date");
			p_attr0_event.setValue(pidSet.getUpdate_date());
			child2.setAttributeNode(p_attr0_event);
			Attr p_attr0_1_event = doc.createAttribute("download_date");
			p_attr0_1_event.setValue(pidSet.getDownload_date());
			child2.setAttributeNode(p_attr0_1_event);
			Attr p_attr0_2_event = doc.createAttribute("import_date");
			p_attr0_2_event.setValue(pidSet.getImport_date());
			child2.setAttributeNode(p_attr0_2_event);
			Attr p_attr0_3_event = doc.createAttribute("sourcesystem_cd");
			p_attr0_3_event.setValue(pidSet.getSourcesystem_cd());
			child2.setAttributeNode(p_attr0_3_event);
			Attr p_attr0_4_event = doc.createAttribute("upload_id");
			p_attr0_4_event.setValue(pidSet.getUpload_id());
			child2.setAttributeNode(p_attr0_4_event);
		}
			}

	private void eidSet(Document doc, Element root, ArrayList <caTissueParser_EidSet> eidList)
			throws SQLException
			{
		ListIterator <caTissueParser_EidSet> listIterator = eidList.listIterator();
		caTissueParser_EidSet eidSet = null;

		Element peidset = doc.createElement("pdo:eid_set");
		root.appendChild(peidset);

		while(listIterator.hasNext())
		{
			eidSet = listIterator.next();
			Element echild1 = doc.createElement("eid");
			peidset.appendChild(echild1);
			Element echild2 = doc.createElement("event_id");
			echild1.appendChild(echild2);
			Text etext = doc.createTextNode(eidSet.getBpt_number());
			echild2.appendChild(etext);
			Attr e_p_attr_0 = doc.createAttribute("source");
			e_p_attr_0.setValue(eidSet.getSource());
			echild2.setAttributeNode(e_p_attr_0);
			Attr e_p_attr_1 = doc.createAttribute("status");
			e_p_attr_1.setValue(eidSet.getStatus());
			echild2.setAttributeNode(e_p_attr_1);
			Attr e_p_attr0_event = doc.createAttribute("update_date");
			e_p_attr0_event.setValue(eidSet.getUpdate_date());
			echild2.setAttributeNode(e_p_attr0_event);
			Attr e_p_attr0_1_event = doc.createAttribute("download_date");
			e_p_attr0_1_event.setValue(eidSet.getDownload_date());
			echild2.setAttributeNode(e_p_attr0_1_event);
			Attr e_p_attr0_2_event = doc.createAttribute("import_date");
			e_p_attr0_2_event.setValue(eidSet.getImport_date());
			echild2.setAttributeNode(e_p_attr0_2_event);
			Attr e_p_attr0_3_event = doc.createAttribute("sourcesystem_cd");
			e_p_attr0_3_event.setValue(eidSet.getSourcesystem_cd());
			echild2.setAttributeNode(e_p_attr0_3_event);
			Attr e_p_attr0_4_event = doc.createAttribute("upload_id");
			e_p_attr0_4_event.setValue(eidSet.getUpload_id());
			echild2.setAttributeNode(e_p_attr0_4_event);
		}
			}
	

	private void observations(Document doc, Element obs_set, ArrayList <caTissueParser_Observations> observationList, String conCode)
			throws SQLException
			{
		ListIterator <caTissueParser_Observations> listIterator = observationList.listIterator();
		caTissueParser_Observations observationSet = null;

		while(listIterator.hasNext())
		{
			observationSet = listIterator.next();

			if((conCode.equalsIgnoreCase("volume_aliquots") && observationSet.getInitial_quantity().equalsIgnoreCase("")) ||
			//(conCode.equalsIgnoreCase("remaining_aliquots") && observationSet.getAvailable_quantity().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("storage") && observationSet.getStorage().equalsIgnoreCase("-:")) ||
			(conCode.equalsIgnoreCase("location") && observationSet.getSite_location().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("collection_container") && observationSet.getCollection_container().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("collection_procedure") && observationSet.getCollection_procedure().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("received_quality") && observationSet.getReceived_quality().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("At260") && observationSet.getAbsorbance_at260().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("At280") && observationSet.getAbsorbance_at280().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("Ratio") && observationSet.getRatio28S_to18S().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("barcode") && observationSet.getBarcode().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("time_collected") && observationSet.getColl_date().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("time_frozen") && observationSet.getFroz_date().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("protocol") && observationSet.getCollection_protocol().equalsIgnoreCase("")) ||
			(conCode.equalsIgnoreCase("time_received") && observationSet.getRec_date().equalsIgnoreCase("")))
			{
				continue;
			}

			obs_count++;
			Element element = doc.createElement("observation");
			obs_set.appendChild(element);
			Attr attr0 = doc.createAttribute("update_date");
			attr0.setValue(observationSet.getUpdate_date().toString());
			element.setAttributeNode(attr0);
			Attr attr0_1 = doc.createAttribute("download_date");
			attr0_1.setValue(observationSet.getDownload_date().toString());
			element.setAttributeNode(attr0_1);
			Attr attr0_2 = doc.createAttribute("import_date");
			attr0_2.setValue(observationSet.getImport_date().toString());
			element.setAttributeNode(attr0_2);
			Attr attr0_3 = doc.createAttribute("sourcesystem_cd");
			attr0_3.setValue(observationSet.getSourcesystem_cd());
			element.setAttributeNode(attr0_3);
			Attr attr0_4 = doc.createAttribute("upload_id");
			attr0_4.setValue(observationSet.getUpload_id());
			element.setAttributeNode(attr0_4);

			String event_id_body=observationSet.getEvent_id_body(), time_dates="";

			String startDate = observationSet.getStart_date().toString();

			if(conCode.equalsIgnoreCase("time_collected"))
			{
				time_dates=observationSet.getColl_date();

				if(time_dates.equalsIgnoreCase("") || time_dates==null)
					time_dates = "0000-00-00T00:00:00.000+01:00";

				startDate = time_dates;
			}
			else if(conCode.equalsIgnoreCase("time_frozen"))
			{
				time_dates=observationSet.getFroz_date();

				if(time_dates.equalsIgnoreCase("") || time_dates==null)
					time_dates = "0000-00-00T00:00:00.000+01:00";

				startDate = time_dates;
			}
			else if(conCode.equalsIgnoreCase("time_received"))
			{
				time_dates=observationSet.getRec_date();

				if(time_dates.equalsIgnoreCase("") || time_dates==null)
					time_dates = "0000-00-00T00:00:00.000+01:00";

				startDate = time_dates;
			}

			Element element2 = doc.createElement("event_id");
			element.appendChild(element2);
			Text text1 = doc.createTextNode(event_id_body);
			element2.appendChild(text1);
			Attr attr = doc.createAttribute("source");
			attr.setValue(observationSet.getEvent_source());
			element2.setAttributeNode(attr);

			Element element3 = doc.createElement("patient_id");
			element.appendChild(element3);
			Text text2 = doc.createTextNode(observationSet.getPatient_id_body());
			element3.appendChild(text2);
			Attr attr2 = doc.createAttribute("source");
			attr2.setValue(observationSet.getPatient_source());
			element3.setAttributeNode(attr2);

			String conTextcode = conCode, units_cd="",con_cd_body="CAT:S.";

			if (conCode.equalsIgnoreCase("barcode"))
			{
				conTextcode = conCode+":"+observationSet.getBarcode();//observationSet.getUnits_cd_body();
			}
			else if (conCode.equalsIgnoreCase("protocol"))
			{
				units_cd = observationSet.getCollection_protocol();
				conTextcode = conCode+":"+units_cd;
			}
			else if (conCode.equalsIgnoreCase("location"))
			{
				units_cd = observationSet.getSite_location();
				conTextcode = conCode+":"+units_cd;
			}
			else if (conCode.equalsIgnoreCase("storage"))
			{
				units_cd = observationSet.getStorage();
				conTextcode = conCode+":"+units_cd;
			}
			else if (conCode.equalsIgnoreCase("At260") || conCode.equalsIgnoreCase("At280") || conCode.equalsIgnoreCase("Ratio"))
			{
				con_cd_body="CAT:DT.";
			}
			else if (conCode.equalsIgnoreCase("collection_container"))
			{
				units_cd = observationSet.getCollection_container();
				conTextcode = units_cd;
				con_cd_body="CAT:CC.";
			}
			else if (conCode.equalsIgnoreCase("collection_procedure"))
			{
				units_cd = observationSet.getCollection_procedure();
				conTextcode = units_cd;
				con_cd_body="CAT:CP.";
			}
			else if (conCode.equalsIgnoreCase("received_quality"))
			{
				units_cd = observationSet.getReceived_quality();
				conTextcode = units_cd;
				con_cd_body="CAT:RQ.";
			}

			units_cd = observationSet.getUnits_cd_body();

			Element element3_1 = doc.createElement("concept_cd");
			element.appendChild(element3_1);

			if(observationSet.getConcept_cd_body().equalsIgnoreCase("CryopreservedCells"))
				observationSet.setConcept_cd_body("CryoCells");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("BoneMarrowPlasma"))
                                observationSet.setConcept_cd_body("BonMP");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("CerebrospinalFluid"))
                                observationSet.setConcept_cd_body("CerF");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("WholeBoneMarrow"))
                                observationSet.setConcept_cd_body("WhoBM");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("FrozenCellPellet"))
                                observationSet.setConcept_cd_body("FroCP");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("RNA,poly-Aenriched"))
                                observationSet.setConcept_cd_body("RNApA");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("RNA,cytoplasmic"))
                                observationSet.setConcept_cd_body("RNAc");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("FrozenTissueBlock"))
                                observationSet.setConcept_cd_body("FroTB");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("FrozenTissueSlide"))
                                observationSet.setConcept_cd_body("FroTS");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("FixedTissueBlock"))
                                observationSet.setConcept_cd_body("FixTB");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("FixedTissueSlide"))
                                observationSet.setConcept_cd_body("FixTS");
			else if(observationSet.getConcept_cd_body().equalsIgnoreCase("WholeGenomeAmplifiedDNA"))
                                observationSet.setConcept_cd_body("WhoGADNA");


			Text text2_1 = doc.createTextNode(con_cd_body+observationSet.getConcept_cd_body()+"."+conTextcode);
			element3_1.appendChild(text2_1);
			Attr attr2_1 = doc.createAttribute("name");
			attr2_1.setValue(observationSet.getConcept_cd_name());
			element3_1.setAttributeNode(attr2_1);

			Element element4 = doc.createElement("observer_cd");
			element.appendChild(element4);
			Text text3 = doc.createTextNode(observationSet.getObserver_cd_body());
			element4.appendChild(text3);
			Attr attr3 = doc.createAttribute("source");
			attr3.setValue(observationSet.getObserver_cd_source());
			element4.setAttributeNode(attr3);

			Element element5 = doc.createElement("start_date");
			element.appendChild(element5);

			String random=createRandomInteger();

			String timer2 = random.substring(2,4);

			if(new Integer(timer2).intValue()>59)
				timer2 = ""+(new Integer(timer2).intValue()-59);
			else
				timer2 = ""+new Integer(timer2).intValue();

			if(timer2.length()==1)
				timer2="0"+timer2;

			if(!conCode.equalsIgnoreCase("time_collected") && !conCode.equalsIgnoreCase("time_frozen") && !conCode.equalsIgnoreCase("time_received"))
				startDate = startDate.substring(0,startDate.indexOf("T00:")+4)+random.substring(0,2)+":"+timer2+"."+random.charAt(4)+"00+01:00";
			else
				startDate = startDate.substring(0,startDate.indexOf("00.")+3)+random.substring(2,5)+"+01:00";

			Text text4 = doc.createTextNode(startDate);
			element5.appendChild(text4);

			Element element6 = doc.createElement("modifier_cd");
			element.appendChild(element6);
			Text text5 = doc.createTextNode(observationSet.getModifier_cd_body());
			element6.appendChild(text5);
			Attr attr4 = doc.createAttribute("name");
			attr4.setValue(observationSet.getModifier_cd_name());
			element6.setAttributeNode(attr4);

			Element element7 = doc.createElement("valuetype_cd");
			element.appendChild(element7);

			String valuetype_cd="";

			if(conCode.equalsIgnoreCase("volume_aliquots") || conCode.equalsIgnoreCase("At260")
					|| conCode.equalsIgnoreCase("At280") || conCode.equalsIgnoreCase("Ratio"))
				valuetype_cd = "N";
			else
				valuetype_cd = "T";

			Text text6 = doc.createTextNode(valuetype_cd);
			element7.appendChild(text6);

			if(conCode.equalsIgnoreCase("volume_aliquots") || conCode.equalsIgnoreCase("At260")
					|| conCode.equalsIgnoreCase("At280") || conCode.equalsIgnoreCase("Ratio")) {
				String nval_num="",vflag_body="",vflag_name="";

				Element element11 = doc.createElement("tval_char");
				element.appendChild(element11);
				Text text10 = doc.createTextNode("E");
				element11.appendChild(text10);

				if(conCode.equalsIgnoreCase("volume_aliquots"))
				{
					nval_num = observationSet.getInitial_quantity();

					if(observationSet.getSpec_class().equalsIgnoreCase("Tissue"))
						units_cd="count";
					else if(observationSet.getSpec_class().equalsIgnoreCase("Fluid"))
						units_cd="ml";
					else if(observationSet.getSpec_class().equalsIgnoreCase("Cell"))
						units_cd="cellcount";
					else
						units_cd="µg";
				}
				else if(conCode.equalsIgnoreCase("At260"))
				{
					nval_num = observationSet.getAbsorbance_at260();
					units_cd="a";
				}
	            else if(conCode.equalsIgnoreCase("At280"))
	            {
	            	nval_num = observationSet.getAbsorbance_at280();
	            	units_cd="a";
	            }
	            else if(conCode.equalsIgnoreCase("Ratio"))
	            {
	            	nval_num = observationSet.getRatio28S_to18S();
	            	units_cd="dn";
	            }

				if(new Float(nval_num) < 10)
				{
					vflag_body = "L";
					vflag_name = "Low";
				}
				else
				{
					vflag_body = "H";
					vflag_name = "High";
				}


				Element element12 = doc.createElement("nval_num");
				element.appendChild(element12);
				Text text11 = doc.createTextNode(nval_num);
				element12.appendChild(text11);
				Attr attr6 = doc.createAttribute("units");
				attr6.setValue(units_cd);
				element12.setAttributeNode(attr6);

				Element element13 = doc.createElement("valueflag_cd");
				element.appendChild(element13);
				Text text12 = doc.createTextNode(vflag_body);
				element13.appendChild(text12);
				Attr attr7 = doc.createAttribute("name");
				attr7.setValue(vflag_name);
				element13.setAttributeNode(attr7);

				Element element14 = doc.createElement("quantity_num");
				element.appendChild(element14);
				Text text13 = doc.createTextNode(observationSet.getQuantity_num());
				element14.appendChild(text13);
			}

			/*else if(conCode.equalsIgnoreCase("remaining_aliquots"))
				units_cd = observationSet.getAvailable_quantity();*/

			Element element8 = doc.createElement("units_cd");
			element.appendChild(element8);
			Text text7 = doc.createTextNode(units_cd);
			element8.appendChild(text7);

			String enddate="";
			if(observationSet.getEnd_date() != null)
				enddate=observationSet.getEnd_date().toString();

			Element element9 = doc.createElement("end_date");
			element.appendChild(element9);
			Text text8 = doc.createTextNode(enddate);
			element9.appendChild(text8);

			Element element10 = doc.createElement("location_cd");
			element.appendChild(element10);
			Text text9 = doc.createTextNode(observationSet.getLocation_cd_body());
			element10.appendChild(text9);
			Attr attr5 = doc.createAttribute("name");
			attr5.setValue(observationSet.getLocation_cd_name());
			element10.setAttributeNode(attr5);
		}
			}

	private String dateFormat(Date date) {

		SimpleDateFormat format =
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");

		return format.format(date);
	}

	private String createRandomInteger(){
		int aStart=10000;
		long aEnd=59599L;

		Random aRandom = new Random();

	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);
	    return ""+randomNumber;
	  }

	private void xmlSplitsCreation(Document doc,Element root,ArrayList <caTissueParser_EventSet> eventList,
			ArrayList <caTissueParser_PidSet> pidList,ArrayList <caTissueParser_EidSet> eidList,
			ArrayList <caTissueParser_PatientSet> patientList,ArrayList <caTissueParser_Observations> obsList)
					throws SQLException, TransformerConfigurationException, TransformerException, FileNotFoundException, IOException
	{
			eventSet(doc, root, eventList);
			pidSet(doc, root, pidList);
			eidSet(doc, root, eidList);
			//patientSet(doc, root, patientList);
			Element obs_set = doc.createElement("pdo:observation_set");
			root.appendChild(obs_set);

			
			observations(doc, obs_set, obsList,"volume_aliquots");
			//observations(doc, root, observationList,"remaining_aliquots");
			observations(doc, obs_set, obsList,"storage");
			observations(doc, obs_set, obsList,"location");
			observations(doc, obs_set, obsList,"At260");
			observations(doc, obs_set, obsList,"At280");
			observations(doc, obs_set, obsList,"Ratio");
			observations(doc, obs_set, obsList,"barcode");
			observations(doc, obs_set, obsList,"time_collected");
			observations(doc, obs_set, obsList,"time_frozen");
			observations(doc, obs_set, obsList,"time_received");
			observations(doc, obs_set, obsList,"protocol");
			observations(doc, obs_set, obsList,"collection_container");
			observations(doc, obs_set, obsList,"collection_procedure");
			observations(doc, obs_set, obsList,"received_quality");

			//TransformerFactory instance is used to create Transformer objects.
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			doc.setXmlStandalone(true);
			transformer.transform(source, result);
			String xmlString = sw.toString();

			File file = new File("/var/local/brisskit/openspecimen/system/OpenSpecimen-2.4.RC2/tomcat-as/webapps/integration/temp/pdo/caTissueExportToi2b2_"+j+"_"+new SimpleDateFormat( "ddMMyy" ).format(new Date())+".xml");
			BufferedWriter bw = new BufferedWriter
					(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(xmlString);
			bw.flush();
			bw.close();
			j++;
			i=0;

			ClientConfig config = new DefaultClientConfig();
	        Client client = Client.create(config);
	        WebResource service = client.resource(getBaseURI());

	        Form form = new Form();
			form.add("incomingXML", xmlString);
			form.add("activity_id", "X");

	        ClientResponse response = service.path("service").path("pdo").type(MediaType.APPLICATION_XML).post(ClientResponse.class, form);

	 		System.out.println("performCall Output from Server .... \n");
	 		String output = response.getEntity(String.class);
	 		System.out.println(output);
	}

	private static URI getBaseURI() {
	
	Process p=Runtime.getRuntime().exec("$i2b2ws");
	BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	String I2B2_WS = br.readLine();
			
        //return UriBuilder.fromUri("http://i2b2:8080/i2b2WS/rest").build();
        return UriBuilder.fromUri(I2B2_WS).build();
    }

}


