package pt.sibace.sepa;

import iso.std.iso._20022.tech.xsd.pain_001_001_03.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * Helper class to build and save a SEPA Credit Transfer document, using the pain.001.001.03 xml file format, 
 * with conditionals specified for operations in Portugal.
 * 
 * Iso classes were created using the xjc compiler and the pain.001.001.03.xsd schema available at the 
 * ISO 20022 web site (www.iso20022.org)
 *  
 * @author "Joao Osorio <joao.osorio@sibace.pt>"
 */
public class CreditTransfer {
	
	private Document document;
	
	/**
	 * Initialize a SEPA Credit Transfer Document
	 * Sets GroupHeader with supplied arguments.
	 * 
	 * @param msgId
	 * @param companyName
	 */
	public CreditTransfer(String msgId, String companyName){
		// Initialize document
		document = new Document();
		
		// Initialize message root
		document.setCstmrCdtTrfInitn(new CustomerCreditTransferInitiationV03());
		
		// Create group header
		GroupHeader32 groupHeader = new GroupHeader32();
		
		// Set message id
		groupHeader.setMsgId(msgId);
		
		// Set creation date
		groupHeader.setCreDtTm(SepaUtils.ISODateTime(new Date()));
	    
	    // Set number of transactions
		groupHeader.setNbOfTxs("0");
		
		// Set control Sum
		groupHeader.setCtrlSum(BigDecimal.ZERO);
		
		// Set party identification - based on name only
		PartyIdentification32 party = new PartyIdentification32();
		party.setNm(companyName);
		groupHeader.setInitgPty(party);
		
		// Add group header to document
		document.getCstmrCdtTrfInitn().setGrpHdr(groupHeader);		
	}
	
	/**
	 * Add payment group to document, and update header values.
	 * 
	 * @param paymentGroup
	 */
	public void addPaymentGroup(CreditTransferPaymentGroup paymentGroup){
		// Add group do document
		document.getCstmrCdtTrfInitn().getPmtInf().add(paymentGroup.getInformation());

		// Update number of transactions in document's group header
		int gpCount = Integer.parseInt(document.getCstmrCdtTrfInitn().getGrpHdr().getNbOfTxs());
		int pmtCount = Integer.parseInt(paymentGroup.getInformation().getNbOfTxs());
		document.getCstmrCdtTrfInitn().getGrpHdr().setNbOfTxs(Integer.toString(gpCount+pmtCount));
		
		// Update control sum in document's group header
		BigDecimal newSum = document.getCstmrCdtTrfInitn().getGrpHdr().getCtrlSum()
									.add(paymentGroup.getInformation().getCtrlSum());
		document.getCstmrCdtTrfInitn().getGrpHdr().setCtrlSum(newSum);
	}
	
	/**
	 * Writes SEPA Credit Transfer document to file
	 * 
	 * @param fileName System dependent file name
	 * @throws JAXBException
	 * @throws IOException 
	 * 
	 */
	public void write(String fileName) throws JAXBException, IOException{
		FileWriter file = new FileWriter(fileName);
		JAXBContext jc = JAXBContext.newInstance(Document.class);
		Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.marshal(new ObjectFactory().createDocument(document), new BufferedWriter(file));
        file.close();
	}
	
}
