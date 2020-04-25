package com.fss.dev.service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fss.dev.bean.MastercardModelMapper;
import com.fss.dev.bean.DataModel;
import com.fss.dev.bean.OutputModel;
import com.fss.dev.common.util.LoggerUtil;

@Service
public class AppService {

	LoggerUtil logger = new LoggerUtil(AppService.class);
	
	  Document doc;
      DocumentBuilderFactory docBuilderFactory;
      DocumentBuilder docBuilder;
      List <DataModel> li = null; 
      OutputModel test = null;
      
      
      public OutputModel setIPMData(List<MastercardModelMapper> mappingXmlData,String psBitMap,String psTranType, InputStream inStream) throws Exception{
      	test = new OutputModel();
      	li = new ArrayList<>();
      	int bitIndex = psBitMap.indexOf('1');
      	try{    		
      		while(bitIndex >= 0) {  
      		final int i = bitIndex;
      		if(bitIndex>0){
      		List<MastercardModelMapper>  data = mappingXmlData.stream().filter(dataa -> dataa.getID().equals(""+(i+1))).collect(Collectors.toList());    			
      			MastercardModelMapper item = data.get(0);
      			int length = 0;    			    			    			
      			// --------------------SUB ELEMENT--------------------------------
      			if(item.getSubElementFlag().equals("Y") && item.getPDSFlag().equalsIgnoreCase("N")){
      				//process sub element of normal as well as PDS element
      				logger.debug("Normal Sub");
      			 length = getDataLength(item,inStream);
      			 byte parrData[] = new byte[length];
     				 inStream.read(parrData);
     				 String DATA = new String(parrData).replace('\'',' ');
     				 if(!item.getFieldName().equals("") && !item.getFieldName().equals(item.getID())) {
     				 
     					 li.add(new DataModel(item.getFieldName(),DATA));
     				 }
     				 //handle sub element 
     				 List<MastercardModelMapper> children = item.getSubElement();
     				 for(MastercardModelMapper child : children){   		
     					int len = Integer.parseInt(child.getLength());
     					 if(!(child.getFieldName()).equals("")){   						 
     						 String fieldValue = DATA.substring(0, len);
     						li.add(new DataModel(child.getFieldName(),fieldValue));
     						 DATA = DATA.substring(len);
     					 }else{
     						DATA = DATA.substring(len);
     						 continue;
     					 }
     				 }    				
      			}  //-------------------------- Normal DE ----------------------------------------
      			else if(!item.getFieldName().equals("") && !item.getFieldName().equals(item.getID())){
      				length = getDataLength(item, inStream);    				    				
      				 byte parrData[] = new byte[length];
      				 inStream.read(parrData);
      				 if(item.getFieldName().equals("MIM_CARD_NMBR")){
      					 String  msCardNum = new String(parrData).replace('\'',' ');
      					 li.add(new DataModel(item.getFieldName(),getMaskedString(msCardNum, "*", "6", "4")));
      				 }else 
      				 {
      					 li.add(new DataModel(item.getFieldName(),new String(parrData).replace('\'',' ')));
      				 }    			
      			 //------------------------ DE has PDS------------------------------------------- 
      				 if(item.getPDSFlag().equals("Y")){
      					 makePDSElements(new String(parrData).replace('\'',' '),4,mappingXmlData);
      				 }
      			}//------------------------------Normal DE's without fieldName Need to skip -------------
      			else{
      				length = getDataLength(item, inStream); 
      				byte parrData[] = new byte[length];
      				inStream.read(parrData);
      			} 	
      	}	
      		   bitIndex = psBitMap.indexOf('1', bitIndex+1);
      		}
      		test.setMainList(li);
      		return test;
      	}catch(Exception e){
      		logger.error("Error: ",e);
      	throw new Exception(e.getMessage());	
      	}
  	
      }
      
      
	
	public List<MastercardModelMapper> populateData() {
		List<MastercardModelMapper> data = new ArrayList<MastercardModelMapper>();
		try {
			docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docBuilderFactory.newDocumentBuilder();
			File mappingXml = ResourceUtils.getFile("classpath:Mapping_Reader1.xml");
			doc = docBuilder.parse(mappingXml); // xml filePath
			doc.getDocumentElement().normalize();

			NodeList nodeListDE = doc.getElementsByTagName("DE");
			NodeList nodeListPDS = doc.getElementsByTagName("PDS");
			for (int i = 0; i < nodeListDE.getLength(); i++) {
				Node currentNode = nodeListDE.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentNode;
					MastercardModelMapper datum = new MastercardModelMapper("DE", element.getAttribute("Id"),
							element.getAttribute("Type"), element.getAttribute("Length"),
							element.getAttribute("MaxLength"), element.getAttribute("SubElementFlag"),
							element.getAttribute("PDSFlag"), element.getAttribute("FieldName"),
							element.getAttribute("multiple"));
					if (element.getAttribute("SubElementFlag").equals("Y")) {
						List<MastercardModelMapper> childNodeList = new ArrayList<MastercardModelMapper>();
						for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
							if (child instanceof Element) {
								Element ch = (Element) child;
								MastercardModelMapper chDatum = new MastercardModelMapper("SE", ch.getAttribute("Id"),
										ch.getAttribute("Type"), ch.getAttribute("Length"),
										ch.getAttribute("MaxLength"), ch.getAttribute("SubElementFlag"),
										ch.getAttribute("PDSFlag"), ch.getAttribute("FieldName"),
										element.getAttribute("multiple"));
								childNodeList.add(chDatum);
							}
						}
						datum.setSubElement(childNodeList);
						data.add(datum);
					}
					data.add(datum);
				}
			}

			for (int i = 0; i < nodeListPDS.getLength(); i++) {
				Node currentNode = nodeListPDS.item(i);
				if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) currentNode;
					MastercardModelMapper datum = new MastercardModelMapper("PDS", element.getAttribute("Id"),
							element.getAttribute("Type"), element.getAttribute("Length"),
							element.getAttribute("MaxLength"), element.getAttribute("SubElementFlag"),
							element.getAttribute("PDSFlag"), element.getAttribute("FieldName"),
							element.getAttribute("multiple"), element.getAttribute("table"));
					if (element.getAttribute("SubElementFlag").equals("Y")) {
						// NodeList childNodes = element.getChildNodes();
						List<MastercardModelMapper> childNodeList = new ArrayList<MastercardModelMapper>();
						for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
							if (child instanceof Element) {
								Element ch = (Element) child;
								MastercardModelMapper chDatum = new MastercardModelMapper("PDS", ch.getAttribute("Id"),
										ch.getAttribute("Type"), ch.getAttribute("Length"),
										ch.getAttribute("MaxLength"), ch.getAttribute("SubElementFlag"),
										ch.getAttribute("PDSFlag"), ch.getAttribute("FieldName"),
										ch.getAttribute("multiple"));
								childNodeList.add(chDatum);
							}
						}
						datum.setSubPdsElement(childNodeList);
						data.add(datum);
					}
					data.add(datum);
				}
			}

			return data;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	
	}
	
public void makePDSElements(String str, int cnt,List<MastercardModelMapper> mappingXmlData){ 
    	
    	int len=0;
    	String data="";
    	try{
    	MastercardModelMapper item = getData(mappingXmlData, str);    	
    	if(item!=null){
    	str = str.substring(4);
    	if(item.getType().equalsIgnoreCase("F")) 
			len = Integer.parseInt(item.getLength());			
		else{			
			int lenData = Integer.parseInt(item.getLength());
			String extractLen = str.substring(0,lenData);			
			len = Integer.parseInt(extractLen); // total length
			str = str.substring(lenData);
		}
    	data = str.substring(0, len); 
    	if(!item.getFieldName().equals(item.getID()) && !item.getFieldName().equals("") ){
    		li.add(new DataModel(item.getFieldName(),data));
    	}    	
    	//check for subelement of pds 
    	String childFieldVal = "";
    	List<MastercardModelMapper> children = new ArrayList<>();
    	if(item.getSubElementFlag().equalsIgnoreCase("Y") && !item.getMultiple().equalsIgnoreCase("Y")){  		
    		children = item.getSubPdsElement();   		
    		for(MastercardModelMapper child : children){
    			int childNodeLen = Integer.parseInt(child.getLength());
    			if(!data.isEmpty() && childNodeLen<=data.length())
    				childFieldVal = data.substring(0,childNodeLen);
    			else 
    				break;
    			data = data.substring(childNodeLen);
    			if(!child.getFieldName().equalsIgnoreCase("")){	    			    		   
    		    	li.add(new DataModel(child.getFieldName(),childFieldVal));
	    			}
    		}    		    	
    	}else if(item.getSubElementFlag().equalsIgnoreCase("Y") && item.getMultiple().equalsIgnoreCase("Y")){
    		children = item.getSubPdsElement();
    		List<List<DataModel>> mainList = new ArrayList<>();
    		for(int i=0;i<len/Integer.parseInt(item.getLength());i++){
    			List<DataModel> subList = new ArrayList<>();
    			for(MastercardModelMapper child : children){
        			int childNodeLen = Integer.parseInt(child.getLength());
        			childFieldVal = data.substring(0,childNodeLen);
        			data = data.substring(childNodeLen);
        			if(!child.getFieldName().equalsIgnoreCase("")){	    			    		   
        				subList.add(new DataModel(child.getFieldName(),childFieldVal));
    	    			}
    			
    			}
    			subList.add(new DataModel("table",item.getTable()));
    			mainList.add(subList);
    		}
    		test.setSubLists(mainList);
    	}    		
    }else{
    	str = str.substring(4);
    	String lenData = str.substring(0, 3);
    	str = str.substring(3);
    	len = Integer.parseInt(lenData);
    }
    	str = str.substring(len);
    	if(str.length()>0)
    		makePDSElements(str,4,mappingXmlData);	
    	}catch(Exception e){
    		logger.error("Err ",e);
    	}	
    }
	
	private int getDataLength(MastercardModelMapper item,InputStream inStream) throws Exception{
    	int length = 0;
    	byte parrLen[] = null;
    	try{
    	if(item.getType().equals("F"))
				 length = Integer.parseInt(item.getLength());
				else if(item.getType().equals("L")){
					parrLen= new byte[Integer.parseInt(item.getLength())];
				     inStream.read(parrLen);
				     String len = parrLen.toString();
			          if(!len.trim().equals(""))
			          length = Integer.parseInt(new String(parrLen));
				}     	
    	return length;
    	}catch(Exception e){
    		logger.error("Invalid Data: Expected Number: Actual Text or blank :"+new String(parrLen)+" :",e);
    		throw new Exception(e.getMessage());
    	}
    	}
    	
    	public static String getMaskedString(String input, String maskeingWith, String left, String right){
        	String str="";
        	if(input.length()<16){    		
        		input = StringUtils.rightPad(input, 16, "0");
        	}    	
        	String maskedChars = StringUtils.leftPad(str, input.length()-new Integer(left).intValue()-new Integer(right).intValue(), "*");    	
        	str = input.substring(0, new Integer(left).intValue())+maskedChars+input.substring(input.length()-new Integer(right).intValue());
        	return str;
        }
    	
    	public MastercardModelMapper getData(List<MastercardModelMapper> mappingXmlData, String data){
        	Optional<MastercardModelMapper> optObject =  mappingXmlData.stream().filter(dataa ->dataa.getID().equals(data.substring(0, 4))).findFirst();
        	try{
        	MastercardModelMapper object = optObject.get();
        	return object;
        	}catch(Exception e){
        		logger.warn("No matching tag found with ID: "+data.substring(0, 4));
        		return null;
        	}
        }
    	
    	public List<MastercardModelMapper> jcbPopulateData(){    	
            Document doc;
            DocumentBuilderFactory docBuilderFactory;
            DocumentBuilder docBuilder;
            
        	List<MastercardModelMapper> data = new ArrayList<MastercardModelMapper>();
        	try{    	    		
        		docBuilderFactory= DocumentBuilderFactory.newInstance();
                docBuilder= docBuilderFactory.newDocumentBuilder();
                doc=docBuilder.parse (new File("F:/testFile/JCB_Mapping_Reader1.xml")); //xml filePath
        		doc.getDocumentElement().normalize();    		
        		
        		NodeList nodeListDE = doc.getElementsByTagName("DE");
        		NodeList nodeListPDS = doc.getElementsByTagName("PDS");    		
        		for(int i=0;i<nodeListDE.getLength();i++){
        			Node currentNode = nodeListDE.item(i);    			
        			if(currentNode.getNodeType() == Node.ELEMENT_NODE){
        				Element element = (Element) currentNode;
        				MastercardModelMapper datum = new MastercardModelMapper("DE",element.getAttribute("ID"),element.getAttribute("Type"),element.getAttribute("Length"),element.getAttribute("MaxLength"),element.getAttribute("SubElementFlag"),element.getAttribute("PDSFlag"),element.getAttribute("FieldName"),element.getAttribute("multiple"));
        				if(element.getAttribute("SubElementFlag").equals("Y")){
        					List<MastercardModelMapper> childNodeList = new ArrayList<MastercardModelMapper>();
        					for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
        						if (child instanceof Element) {
        		    		    	  Element ch = (Element)child;
        		    		    	  MastercardModelMapper chDatum = new MastercardModelMapper("SE",ch.getAttribute("ID"),ch.getAttribute("Type"),ch.getAttribute("Length"),ch.getAttribute("MaxLength"),ch.getAttribute("SubElementFlag"),ch.getAttribute("PDSFlag"),ch.getAttribute("FieldName"),element.getAttribute("multiple"));
        		    		    	  childNodeList.add(chDatum);
        					}
        				}
        				datum.setSubElement(childNodeList);	
        				data.add(datum);
        			}  
        				data.add(datum);	
        		}
        		}	
        		
        		for(int i=0;i<nodeListPDS.getLength();i++){
        			Node currentNode = nodeListPDS.item(i);    			
        			if(currentNode.getNodeType() == Node.ELEMENT_NODE){
        				Element element = (Element) currentNode;
        				MastercardModelMapper datum = new MastercardModelMapper("PDS",element.getAttribute("ID"),element.getAttribute("Type"),element.getAttribute("Length"),element.getAttribute("MaxLength"),element.getAttribute("SubElementFlag"),element.getAttribute("PDSFlag"),element.getAttribute("FieldName"),element.getAttribute("multiple"),element.getAttribute("table"));
        				if(element.getAttribute("SubElementFlag").equals("Y")){
        					List<MastercardModelMapper> childNodeList = new ArrayList<MastercardModelMapper>();
        					for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
        						if (child instanceof Element) {
        		    		    	  Element ch = (Element)child;
        		    		    	  MastercardModelMapper chDatum = new MastercardModelMapper("PDS",ch.getAttribute("ID"),ch.getAttribute("Type"),ch.getAttribute("Length"),ch.getAttribute("MaxLength"),ch.getAttribute("SubElementFlag"),ch.getAttribute("PDSFlag"),ch.getAttribute("FieldName"),ch.getAttribute("multiple"),ch.getAttribute("table"));
        		    		    	  childNodeList.add(chDatum);
        					}
        				}
        				datum.setSubPdsElement(childNodeList);	
        				data.add(datum);
        			}
        				data.add(datum);	
        		}
        		}
        	
        		return data;
        	}catch(Exception e){    	
        		logger.error("Err ",e);
        		return new ArrayList<>();
        	}    	
        }
}
