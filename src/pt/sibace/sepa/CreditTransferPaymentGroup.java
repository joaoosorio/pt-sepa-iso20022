package pt.sibace.sepa;

import java.math.BigDecimal;
import java.util.Date;

import iso.std.iso._20022.tech.xsd.pain_001_001_03.*;

/**
 * Helper class to build a Credit Transfer payment group. A payment group holds a collection of payment transactions.
 *  
 * 
 * @author "Joao Osorio <joao.osorio@sibace.pt>"
 *
 */
public class CreditTransferPaymentGroup {

	// Payment information (PmtInf) object for credit transfers
	PaymentInstructionInformation3 paymentGroup;
	
	
	public CreditTransferPaymentGroup(String pmtInfId, Date reqDate, 
									  String debtorName, String debtorIBAN, String debtorBIC)
	{
		paymentGroup = new PaymentInstructionInformation3();

		// Set payment information id
		paymentGroup.setPmtInfId(pmtInfId);
		
		// Set payment method - always TRF
		paymentGroup.setPmtMtd(PaymentMethod3Code.TRF);
		
		// Initialize counters
		paymentGroup.setNbOfTxs("0");
		paymentGroup.setCtrlSum(BigDecimal.ZERO);
		
		// Set requested execution date
		paymentGroup.setReqdExctnDt(SepaUtils.ISODate(reqDate));
	    
	    // Set debtor
	    paymentGroup.setDbtr(createParty(debtorName));
	    
	    // Set debtor account
	    paymentGroup.setDbtrAcct(createAccount(debtorIBAN));
	    
	    // Set debtor agent
	    paymentGroup.setDbtrAgt(createAgent(debtorBIC));	 
	    	    	    
	}
	
	public PaymentInstructionInformation3 getInformation(){
		return paymentGroup;
	}
		
	public void addTransaction(String endToEndIdentification, BigDecimal amount,
							   String creditorName, String creditorIBAN, String creditorBIC)
	{	
		CreditTransferTransactionInformation10 transaction = new CreditTransferTransactionInformation10();

		// Set transaction id
		PaymentIdentification1 pmtId = new PaymentIdentification1();
		pmtId.setEndToEndId(endToEndIdentification);
		transaction.setPmtId(pmtId);
		
		// Set transaction amount
		AmountType3Choice amt = new AmountType3Choice();
		ActiveOrHistoricCurrencyAndAmount instdAmt = new ActiveOrHistoricCurrencyAndAmount();
		instdAmt.setValue(amount);
		instdAmt.setCcy("EUR");
		amt.setInstdAmt(instdAmt);
		transaction.setAmt(amt);
		
		// Set creditor agent
		transaction.setCdtrAgt(createAgent(creditorBIC));
	
		// Set creditor
		transaction.setCdtr(createParty(creditorName));
		
		// Set creditor account
		transaction.setCdtrAcct(createAccount(creditorIBAN));

		// Set proprietary code for transfers
		Purpose2Choice purp = new Purpose2Choice();
		purp.setPrtry("12");
				
		// Add transaction to payment group
		paymentGroup.getCdtTrfTxInf().add(transaction);
		
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
