package pt.sibace.sepa;

import java.math.BigDecimal;
import java.util.Date;

import iso.std.iso._20022.tech.xsd.pain_008_001_02.*;

/**
 * Helper class to build a Direct Debit payment group. A payment group holds a collection of payment transactions.
 *  
 * 
 * @author Joao Osorio (joao.osorio@sibace.pt)
 *
 */
public class DirectDebitPaymentGroup {
	
	PaymentInstructionInformation4 paymentGroup;
	
	public DirectDebitPaymentGroup(String pmtInfId, Date reqDate, 
									String creditorName, String creditorId, 
									String creditorIBAN, String creditorBIC,
									String groupType)
	{
		paymentGroup = new PaymentInstructionInformation4();

		// Set payment information id
		paymentGroup.setPmtInfId(pmtInfId);
		
		// Set payment method - always DD
		paymentGroup.setPmtMtd(PaymentMethod2Code.DD);
		
		// Initialize counters
		paymentGroup.setNbOfTxs("0");
		paymentGroup.setCtrlSum(BigDecimal.ZERO);
		
		// Set requested collection date
		paymentGroup.setReqdColltnDt(SepaUtils.ISODate(reqDate));
	    
	    // Set creditor
		paymentGroup.setCdtr(createParty(creditorName));
	    
	    // Set creditor account
	    paymentGroup.setCdtrAcct(createAccount(creditorIBAN));
	    
	    // Set creditor agent
	    paymentGroup.setCdtrAgt(createAgent(creditorBIC));	 
	    	    	    
	    // Set creditor scheme id
	    paymentGroup.setCdtrSchmeId(createPartyId(creditorId));
	    
	    // Set group type
	    //TODO enforce the 4 types: FRST, OOFF, RCUR and FINAL
	    PaymentTypeInformation20 pmtTpInf = new PaymentTypeInformation20();
	    pmtTpInf.setSeqTp(SequenceType1Code.fromValue(groupType));
	    paymentGroup.setPmtTpInf(pmtTpInf);
	}
	
	public PaymentInstructionInformation4 getInformation(){
		return paymentGroup;
	}
		
	public void addTransactionWithoutAmendment(String endToEndIdentification, BigDecimal amount,
			   				   	       		   String mandateId, Date mandateDate,
			   				   	       		   String debtorName, String debtorIBAN, String debtorBIC){
		addTransaction(endToEndIdentification, amount, mandateId, mandateDate, debtorName, debtorIBAN, debtorBIC, false, false, "");		
	}
	
	public void addTransactionWithAmendedDebtorBank(String endToEndIdentification, BigDecimal amount,
			   										String mandateId, Date mandateDate,
			   										String debtorName, String debtorIBAN, String debtorBIC){
		addTransaction(endToEndIdentification, amount, mandateId, mandateDate, debtorName, debtorIBAN, debtorBIC, true, false, "");		
		
	}

	public void addTransactionWithAmendedDebtorAccount(String endToEndIdentification, BigDecimal amount,
			   										   String mandateId, Date mandateDate,
			   										   String debtorName, String debtorIBAN, String debtorBIC,
			   										   String originalIBAN){
		addTransaction(endToEndIdentification, amount, mandateId, mandateDate, debtorName, debtorIBAN, debtorBIC, false, true, originalIBAN);		
		
	}

	public void addTransaction(String endToEndIdentification, BigDecimal amount,
							   String mandateId, Date mandateDate,
							   String debtorName, String debtorIBAN, String debtorBIC,
							   boolean debtorBankChanged, boolean debtorAccountChanged, String originalIBAN)
	{	
		DirectDebitTransactionInformation9 transaction = new DirectDebitTransactionInformation9();

		// Set transaction id
		PaymentIdentification1 pmtId = new PaymentIdentification1();
		pmtId.setEndToEndId(endToEndIdentification);
		transaction.setPmtId(pmtId);
		
		// Set transaction amount
		ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
		instdAmt.setValue(amount);
		instdAmt.setCcy("EUR");
		transaction.setInstdAmt(instdAmt);
		
		// Set direct debit transaction info (mandate related)
		DirectDebitTransaction6 drctDbtTx = new DirectDebitTransaction6();
		MandateRelatedInformation6 mndRltdInf = new MandateRelatedInformation6();
		mndRltdInf.setMndtId(mandateId);
		mndRltdInf.setDtOfSgntr(SepaUtils.ISODate(mandateDate));
		drctDbtTx.setMndtRltdInf(mndRltdInf);		
		if (debtorBankChanged) {
			//TODO check if paymentGroup is of type FRST. If it isn't, throw exception "new bank amendments require sequence type of FRST"
			mndRltdInf.setAmdmntInd(true);
			AmendmentInformationDetails6 amdmntInfDtls = new AmendmentInformationDetails6();
			BranchAndFinancialInstitutionIdentification4 orgnldbtragt = new BranchAndFinancialInstitutionIdentification4();
			FinancialInstitutionIdentification7 finInstnId = new FinancialInstitutionIdentification7();
			GenericFinancialIdentification1 othr = new GenericFinancialIdentification1();
			othr.setId("SMNDA");
			finInstnId.setOthr(othr);
			orgnldbtragt.setFinInstnId(finInstnId);
			amdmntInfDtls.setOrgnlDbtrAgt(orgnldbtragt);
			mndRltdInf.setAmdmntInfDtls(amdmntInfDtls);
		} else if (debtorAccountChanged){
			mndRltdInf.setAmdmntInd(true);
			AmendmentInformationDetails6 amdmntInfDtls = new AmendmentInformationDetails6();
			CashAccount16 orgnlDbtAcct = new CashAccount16();
			AccountIdentification4Choice id = new AccountIdentification4Choice();
			id.setIBAN(originalIBAN);
			orgnlDbtAcct.setId(id);
			amdmntInfDtls.setOrgnlDbtrAcct(orgnlDbtAcct);			
			mndRltdInf.setAmdmntInfDtls(amdmntInfDtls);
		}
		transaction.setDrctDbtTx(drctDbtTx);		
		
		// Set debtor
		transaction.setDbtr(createParty(debtorName));
		
		// Set creditor account
		transaction.setDbtrAcct(createAccount(debtorIBAN));

		// Set debtor agent
		transaction.setDbtrAgt(createAgent(debtorBIC));

		// Add transaction to payment group
		paymentGroup.getDrctDbtTxInf().add(transaction);
		
		// Update counters
		int numTxs=Integer.parseInt(paymentGroup.getNbOfTxs()) + 1;
		paymentGroup.setNbOfTxs(Integer.toString(numTxs));
		paymentGroup.setCtrlSum(paymentGroup.getCtrlSum().add(amount));
	}
		
	private PartyIdentification32 createParty(String name){
		PartyIdentification32 party = new PartyIdentification32();
		party.setNm(name);
		return party;
	}

	private PartyIdentification32 createPartyId( String id){
		PartyIdentification32 party = new PartyIdentification32();
		Party6Choice partyId = new Party6Choice();
		PersonIdentification5 prvtId = new PersonIdentification5();
		GenericPersonIdentification1 othrId = new GenericPersonIdentification1();
		othrId.setId(id);
		prvtId.getOthr().add(othrId);
		partyId.setPrvtId(prvtId);
		party.setId(partyId);
		return party;
	}
	
	private CashAccount16 createAccount(String iban){
		CashAccount16 account = new CashAccount16();
		AccountIdentification4Choice accountId = new AccountIdentification4Choice();
		accountId.setIBAN(iban);
		account.setId(accountId);
		return account;		
	}

	private BranchAndFinancialInstitutionIdentification4 createAgent(String bic){
		BranchAndFinancialInstitutionIdentification4 agent = new BranchAndFinancialInstitutionIdentification4();
		FinancialInstitutionIdentification7 finId = new FinancialInstitutionIdentification7();
		finId.setBIC(bic);
		agent.setFinInstnId(finId);
		return agent;
	}
}
