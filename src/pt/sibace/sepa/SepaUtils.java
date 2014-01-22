package pt.sibace.sepa;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Utility class containing various helper methods to generate common attributes
 * 
 * @author "Joao Osorio <joao.osorio@sibace.pt>"
 *
 */
public class SepaUtils {
	/**
	 * Converts java.util.Date into XMLGregorianCalendar (required by the xml structure), using the ISO DateTime pattern to represent date and time.
	 * 
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar ISODateTime(Date date){
		XMLGregorianCalendar dateTime;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
	    try {
			dateTime=DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
			dateTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
			dateTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);

		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}		
	    return dateTime;		
	}

	/**
	 * Converts java.util.Date into XMLGregorianCalendar (required by the xml structure), using the ISO Date pattern to represent date.
	 * 
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar ISODate(Date date){
		XMLGregorianCalendar dateOnly;
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
	    try {
			dateOnly=DatatypeFactory.newInstance().newXMLGregorianCalendarDate(calendar.get(Calendar.YEAR), 
																				calendar.get(Calendar.MONTH)+1, 
																				calendar.get(Calendar.DAY_OF_MONTH),
																				DatatypeConstants.FIELD_UNDEFINED);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}		
	    return dateOnly;		
	}

	public static String genericId(String ref){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmm");
		return ref+"-"+df.format(new Date());
	}
	
	public static String paymentid(String paymentGroupId, long seq){
		DecimalFormat df = new DecimalFormat("00000");
		return paymentGroupId+"-"+df.format(seq);
	}
	
	
	public static Map<String,String> NibToBicMapping;
	static {
	    Map<String,String> temp = new HashMap<String, String>();
	    
		temp.put("0001","BGALPTPL");
	    temp.put("0007","BESCPTPL");
	    temp.put("0008","BAIPPTPL");
	    temp.put("0010","BBPIPTPL");
	    temp.put("0012","CDACPTPA");
	    temp.put("0014","IVVSPTPL");
	    temp.put("0018","TOTAPTPL");
	    temp.put("0019","BBVAPTPL");
	    temp.put("0022","BRASPTPL");
	    temp.put("0023","BCOMPTPL");
	    temp.put("0025","CXBIPTPL");
	    temp.put("0027","BPIPPTPL");
	    temp.put("0029","GEBAPTPL");
	    temp.put("0032","BARCPTPL");
	    temp.put("0033","BCOMPTPL");
	    temp.put("0034","BNPAPTPL");
	    temp.put("0035","CGDIPTPL");
	    temp.put("0036","MPIOPTPL");
	    temp.put("0038","BNIFPTPL");
	    temp.put("0043","DEUTPTPL");
	    temp.put("0045","CCCMPTPL");
	    temp.put("0046","CRBNPTPL");
	    temp.put("0047","ESSIPTPL");
	    temp.put("0048","BFIAPTPL");
	    temp.put("0049","INIOPTP1");
	    temp.put("0059","CEMAPTP2");
	    temp.put("0061","BDIGPTPL");
	    temp.put("0063","BNFIPTPL");
	    temp.put("0064","BPGPPTPL");
	    temp.put("0065","BESZPTPL");
	    temp.put("0073","IBNBPTP1");
	    temp.put("0076","FBCOPTPP");
	    temp.put("0079","BPNPPTPL");
	    temp.put("0086","EFISPTPL");
	    temp.put("0092","CAVIPTPP");
	    temp.put("0097","CCCHPTP1");
	    temp.put("0098","CERTPTP1");
	    temp.put("0099","CSSOPTPX");
	    temp.put("0160","BESAPTPA");
	    temp.put("0168","CAHMPTPL");
	    temp.put("0169","CITIPTPX");
	    temp.put("0170","CAGLPTPL");
	    temp.put("0183","PRTTPTP1");
	    temp.put("0188","BCCBPTPL");
	    temp.put("0189","BAPAPTPL");
	    temp.put("0235","CAOEPTP1");
	    temp.put("0244","MPCGPTP1");
	    temp.put("0260","SHHBPTP1");
	    temp.put("0484","ONIFPTP1");
	    temp.put("0500","BBRUPTPL");
	    temp.put("0698","UIFCPTP1");
	    temp.put("0781","IGCPPTPL");
	    temp.put("0881","CRBBPTP1");
	    temp.put("0916","CDCTPTP2");
	    temp.put("5180","CDCTPTP2");	    
	    temp.put("5200","CTIUPTP1");
	    temp.put("5340","CTLOPTP1");

	    NibToBicMapping = Collections.unmodifiableMap(temp);
	}
	
	public static String bicFromNib(String nib){
		String bankCode = nib.substring(0,4);
		if (NibToBicMapping.get(bankCode)==null){
			System.out.println("Unknown Bank Code: "+bankCode);
		}
		return NibToBicMapping.get(bankCode);
	}
	
	public static String nibToIban(String nib){
		return "PT50"+nib;
	}
	

}
