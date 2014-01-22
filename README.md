# pt-sepa-iso20022
---
API to easily implement SEPA (ISO-20022) in Java applications (tuned for Portugal).

---

## Credit Transfer Usage Example
---

Create a new Credit Transfer message:

		 CreditTransfer ct = new CreditTransfer("message id", "company name");

Create a new Payment Group:

		CreditTransferPaymentGroup pg = new CreditTransferPaymentGroup("payment group id",  (java.util.Date)executionDate, 
																		"debtor Name", "debtor Iban", "debtor Bic");

Add a transaction to the payment group:

		pg.addTransaction("end to end id", (BigDecimal)amount,
							"creditor name", "creditor iban", "creditor bic");

Attach the payment group to the credit transfer message:

		ct.addPaymentGroup(pg);

Write the credit transder message in a XML ISO-20022 compliant file:

        ct.write("/full/path/filename.sepa.xml");

---

## Direct Debit Usage Example
---
Create a new Direct Debit message:

        DirectDebit dd = new DirectDebit("message id", "company name", "company id");        

Create a new Payment Group with FRST transactions:

        DirectDebitPaymentGroup pg = new DirectDebitPaymentGroup("payment group id", (java.util.Date)executionDate, 
                                                                 "creditor Name", "creditor id",
                                                                 "creditor Iban", "creditor Bic",
                                                                 "FRST");

Add a transaction without adc amendment to the payment group:

        pg.gaddTransactionWithoutAmendment("end to end id", (BigDecimal)value,
                                            "mandate id", (java.util.Date)mandateDate,
                                            "debtor name", "debtor IBAN", "debtor BIC");

Add a transaction with adc amendment (new iban) to the payment group:

        pg.gaddTransactionWithAmendedDebtorAccount("end to end id", (BigDecimal)value,
                                                    "mandate id", (java.util.Date)mandateDate,
                                                    "debtor name", "debtor IBAN", "debtor BIC", "original Iban");

Attach the payment group to the direct debit message:

        dd.addPaymentGroup(pg);

Create a new Payment Group with RCUR transactions:

        pg = new DirectDebitPaymentGroup("payment group id", (java.util.Date)executionDate, 
                                            "creditor Name", "creditor id",
                                            "creditor Iban", "creditor Bic",
                                            "RCUR");

Add a transaction with adc amendment (new bank) to the payment group:

        pg.gaddTransactionWithAmendedDebtorBank("end to end id", (BigDecimal)value,
                                                "mandate id", (java.util.Date)mandateDate,
                                                "debtor name", "debtor IBAN", "debtor BIC");

Attach the payment group to the direct debit message:

        dd.addPaymentGroup(pg);

Write the DirectDebit in a XML ISO-20022 compliant file:
        
        dd.write("/full/path/filename.sepa.xml");


---

## SEPA Utils
---

Get BIC from Nib (PT):
 
 		String bic = SepaUtils.bicFromNib("nib");
 		
Get IBAN from Nib (PT): 		

 		String iban = SepaUtils.nibToIban("nib");
 