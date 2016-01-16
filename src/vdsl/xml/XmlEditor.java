package vdsl.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import vdsl.fileIO.FileDealer;
import vdsl.interpreter.core.Core;


/**
 * @author Vikas
 * @version 2.0
 * 
 * Added compatiblity with String streams, removed dependency on file
 */

public class XmlEditor {

	XMLEventReader xmlEventReader = null;
	XMLEventWriter xmlEventWriter = null;
	XMLEventFactory eventFactory = XMLEventFactory.newInstance();
	XMLEvent curEvent = null;
	List<XMLEvent> xmlEventList = new ArrayList<XMLEvent>();

	public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
		XmlEditor xe = new XmlEditor();

//		+"\n	<BookingAction>Generate Posting String</BookingAction>"
		String sampleXml = "<?xml version=\"1.0\"?>"
				+"\n<MeridianMessage MessageFormat=\"NonStandard XML\">"
				+"\n	<amount1></amount1>"
				+"\n	<stoned>"
				+"\n		<amount>200</amount>"
				+"\n	</stoned>"
				+"\n	<amount>"
				+"\n		<amount cur=\"EUR\">300</amount>"
				+"\n	</amount>"
				+"\n	<AccountDetails>"
				+"\n		<AccountInfo>"
				+"\n			<accountId type=\"Internal\">acc0</accountId>"
				+"\n		</AccountInfo>"
				+"\n		<AccountInfo>"
				+"\n			<accountId type=\"External\" branch=\"999\">acc1</accountId>"
				+"\n		</AccountInfo>"
				+"\n		<AccountInfo>"
				+"\n			<accountId type=\"Internal\">acc2</accountId>"
				+"\n		</AccountInfo>"
				+"\n	</AccountDetails>"
				+"\n	<BookingIndicator>Booked</BookingIndicator>"
				+"\n</MeridianMessage>";
		
		String ptXml = new FileDealer().readFromFile("sampleData/amendPartyRq.xml");

		
		System.out.println(ptXml);
//		System.out.println(xe.setTagValue("rqParam", "UNIQUE_ID=134;PT_PFN_Party#PARTYID=asfkdbka;PT_PFN_Party#KYCSTATUS=001;PT_PFN_Party#WATCHLISTSTATUS=Y;", ptXml));
		
//		System.out.println("abc".equals("abc"));
//		System.out.println(xe.getTagValue("BookingAction", new File("sampleData/sample.xml")));	
//		System.out.println(xe.getTagValue("MeridianMessage@MessageFormat", new File("sampleData/sample.xml")));	
//		System.out.println(sampleXml);
//		System.out.println(xe.getTagValue("amount|amount", sampleXml));
		System.out.println(xe.setTagValue("AccountInfo|accountId","", sampleXml));
		
//		System.out.println(("Booked").equals(xe.getTagValue("BookingIndicator", sampleXml)));	
//		System.out.println(("").equals(xe.getTagValue("amount", sampleXml)));
		
//		System.out.println(("300").equals(xe.getTagValue("amount|amount", sampleXml)));	

//		System.out.println(("NonStandard XML").equals(xe.getTagValue("MeridianMessage@MessageFormat", sampleXml)));
//		System.out.println(("EUR").equals(xe.getTagValue("amount|amount@cur", sampleXml)));	

//		System.out.println(("acc0").equals(xe.getTagValue("AccountDetails|accountId", sampleXml)));
		
//		System.out.println(("acc0").equals(xe.getTagValue("AccountDetails|accountId[0]", sampleXml)));	
//		System.out.println(("Internal").equals(xe.getTagValue("AccountInfo|accountId[0]@type", sampleXml)));	
		
//		System.out.println(("acc1").equals(xe.getTagValue("AccountInfo|accountId[1]", sampleXml)));	
//		System.out.println(("External").equals(xe.getTagValue("AccountInfo|accountId[1]@type", sampleXml)));	
//		System.out.println(("999").equals(xe.getTagValue("AccountInfo|accountId[1]@branch", sampleXml)));	
		
//		System.out.println(("acc2").equals(xe.getTagValue("accountId[2]", sampleXml)));	
//		System.out.println(("Internal").equals(xe.getTagValue("AccountInfo|accountId[2]@type", sampleXml)));	
//		
	}
	
	public String getTagValue(String tagName,String xmlStr) {
		return setTagValue(tagName, null, xmlStr);
	}
	
	public String getTagValue(String tagName,File xmlFile) {
		return setTagValue(tagName, null, xmlFile);
	}

	public String setTagValue(String tagName,String value,File xmlFile) {
		InputStream is = null;
		try {
			is = new FileInputStream(xmlFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return setTagValue(tagName,value , is);
	}

	public String setTagValue(String tagName,String value,String xmlStr) {
		InputStream is = new ByteArrayInputStream(xmlStr.getBytes(StandardCharsets.UTF_8));
		return setTagValue(tagName,value , is);
	}

	public String setTagValue(String tagName,String value,InputStream inptStrm) {
		String returnStr = null;
		boolean tagFound = true;
		int count = 0;
		OutputStream optStrm = new ByteArrayOutputStream();
		try{
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			xmlEventReader = inputFactory.createXMLEventReader(inptStrm);
			xmlEventWriter = outputFactory.createXMLEventWriter(optStrm);
			
			String delims = "[|]+";
			String[] tags = tagName.split(delims);
			if(tags.length >= 1){
				if(Core.isInteger(Core.simpleSubString(tags[tags.length - 1], "[", "]", false))){
					count = Integer.parseInt(Core.simpleSubString(tags[tags.length - 1], "[", "]", false));					
				}
				tags[tags.length - 1] = Core.simpleSubString(tags[tags.length - 1], "[", false);
			}
			
			tagFound = gotoTag(tags, count);
			
			if(tagFound){
				if(tags[tags.length-1].contains("@"))
					returnStr =  setAttribute(tags[tags.length-1].substring(0,tags[tags.length-1].indexOf("@")),tags[tags.length-1].substring(tags[tags.length-1].indexOf("@") + 1), value);
				else
					returnStr =  setTag(tags[tags.length-1], value);	
			}
			
		}catch(XMLStreamException e){
			e.printStackTrace();
		}finally{
			try{
				//it goes till the tag in setTag/Attribute and then add the remaining from reader
				xmlEventWriter.add(xmlEventReader);
				xmlEventReader.close();
				xmlEventWriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(returnStr != null){
			return returnStr;
		}else if(value == null){
			return null;
		}
		else{
			return optStrm.toString();
		}
	}

	private boolean gotoTag(String[] tags, int count) throws XMLStreamException{
		XMLEvent xmlEvent = null;
		TagContext tagData = new TagContext();
		int i = 0;
		//if no tag name to be found return nothing found
		if(tags.length == 0){
			return false;
		}
		
		tagData.lastTag = tags[0];
		tagData.nextTag = tags[0];
		

		while(!tagData.documentEnd){

			xmlEvent = findATag(tagData);
						
			if(tagData.moveNext){
				if(i < tags.length - 1){
					i += 1 ;
					tagData.nextTag = tags[i];
					tagData.lastTag = tags[i-1];				
				}
				else{
					if(count == 0){
						break;
					}
					xmlEventList.add(xmlEvent);
					-- count;					
				}
			}else{
				if(i == 0 || i == 1){
					i = 0;
					tagData.nextTag = tags[0];
					tagData.lastTag = tags[0];				
				}
				else{
					i -= 1;
					tagData.nextTag = tags[i];
					tagData.lastTag = tags[i-1];				
				}
			}
		}
		if(!tagData.documentEnd){
			curEvent = xmlEvent;
		}
		return !tagData.documentEnd;
	}
	
	private XMLEvent findATag(TagContext tagData) throws XMLStreamException {
		XMLEvent xmlEvent = null;
		while (xmlEventReader.hasNext()) {
			xmlEvent = xmlEventReader.nextEvent();
			xmlEventWriter.add(xmlEvent);
			
			
			if (xmlEvent.isStartElement()) {
				StartElement startElement = xmlEvent.asStartElement();
				if (startElement.getName().getLocalPart().equals(tagData.nextTag) && xmlEventReader.hasNext()) {
					tagData.moveNext = true;
					break;	
				}
			}

			if (xmlEvent.isEndElement()) {
				EndElement endElement = xmlEvent.asEndElement();
				if (endElement.getName().getLocalPart().equals(tagData.lastTag) && xmlEventReader.hasNext()) {
					tagData.moveNext = false;
					break;						
				}
			}
			
			if(xmlEvent.isEndDocument()){
				tagData.documentEnd = true;
			}
		}
		return xmlEvent;
	}

	private String setTag(String tagName, String value) throws XMLStreamException {
		XMLEvent xmlEvent = null;
		while (xmlEventReader.hasNext()) {
			xmlEvent = curEvent;

			if (xmlEvent.isStartElement()) {
				StartElement startElement = xmlEvent.asStartElement();

				if (startElement.getName().getLocalPart().equals(tagName) && xmlEventReader.hasNext()) {
					xmlEvent = xmlEventReader.nextEvent();
					if (xmlEvent.isCharacters()) {
						if (value == null) {
							xmlEventWriter.add(xmlEvent);
							return xmlEvent.asCharacters().getData().trim();
						} else {
							xmlEventWriter.add(eventFactory.createCharacters(value));
							return null;
						}
					}

					if (xmlEvent.isEndElement()) {
						if (value == null) {
							xmlEventWriter.add(xmlEvent);
							return "";
						} else {
							xmlEventWriter.add(eventFactory.createCharacters(value));
							xmlEventWriter.add(xmlEvent);
							return null;
						}
					}
				
				}
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private String setAttribute(String tagName,String attributeName,String value) throws XMLStreamException{
		while(xmlEventReader.hasNext()){
			XMLEvent xmlEvent = xmlEventReader.nextEvent();
			
			if(xmlEvent.isStartElement()){
				StartElement startElement = xmlEvent.asStartElement();
				if(startElement.getName().getLocalPart().equals(tagName) && xmlEventReader.hasNext()){
					if(value == null){
						xmlEventWriter.add(xmlEvent);
						return startElement.getAttributeByName(new QName(attributeName)).getValue().trim();
					}  
					
					Attribute newAttribute = eventFactory.createAttribute(new QName(attributeName), value);

					Iterator<Attribute> attrIt = startElement.getAttributes();
					Iterator<Namespace> nmIt = startElement.getNamespaces();
					xmlEvent = eventFactory.createStartElement(startElement.getName(), null, nmIt);
					xmlEventWriter.add(xmlEvent);
					
					while (attrIt.hasNext()) {
						Attribute attr =  attrIt.next();
						if(attr.getName().getLocalPart().equalsIgnoreCase(attributeName)){
							xmlEventWriter.add(newAttribute);
						}
						else{
							xmlEventWriter.add(attr); 
						}
					}
				}
				else{
					xmlEventWriter.add(xmlEvent);
				}
			}
			else{
				xmlEventWriter.add(xmlEvent);
			}
		}
		return null;
	}
	
	
	private class TagContext {
		boolean documentEnd = false;
		boolean moveNext = true;
		String nextTag;
		String lastTag;
	}
}
