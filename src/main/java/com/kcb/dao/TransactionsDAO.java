package com.kcb.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcb.domain.KCBRequest;
import com.kcb.domain.KCBResponse;
import com.kcb.domain.KCBResponse.Header;
import com.kcb.domain.KCBResponse.ResponsePayload;
import com.kcb.domain.KCBResponse.TransactionInfo;
import com.kcb.exception.DuplicateRecordException;

@Repository
public class TransactionsDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String JSON_CONVERSION_FAILED = "JSON_RESPONSE_CONVERSION_FAILED";

	private static Logger log = LoggerFactory.getLogger(TransactionsDAO.class);

	public KCBResponse query(KCBRequest request) {
		
		log.info("Query request received:\n {}", objToJson(request));
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getQueryData().getBusinessKey();
		
		try {
			Header header = Header.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Processed Successfully.")
					.build();
			String sqlQuery = "select * from KFS_CUST_TRX_V where 1=1 and trx_number = ?";
			KCBResponse.KCBResponseBuilder responseBuilder = jdbcTemplate.queryForObject(sqlQuery,
					new Object[] { bill_ref }, this::mapRowToQueryResponse);
			responseBuilder.header(header);
			log.info("Query request for BillRefNumber: {} is successful.", bill_ref);
			return responseBuilder.build();
		} catch (EmptyResultDataAccessException dae) {
			log.error("Exception occured while querying. The error is: " + dae.getMessage(), dae);
			
			String errorMessage = String.format("Payment validation Failed. Bill reference: %s does not exist", bill_ref);
			
			Header header = Header.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();
			
			TransactionInfo transactionInfo = TransactionInfo.builder()
					.transactionId("")
					.customerName("")
					.utilityName("")
					.subCompartment("")
					.amount("0")
					.build();

			ResponsePayload payload = ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();
			
			return KCBResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		} catch (Exception ex) {
			log.error("Exception occured while querying. The error is: " + ex.getMessage(), ex);
			throw ex;
		}
	}
	
	public KCBResponse notify(KCBRequest request) throws ParseException, DuplicateRecordException {
		
		log.info("Notify request received:\n {}", objToJson(request));
		
		String sqlQuery = "INSERT INTO KFS_KCB_NOTIFY_DATA_STG (Bill_Reference, Mobile_number, transaction_Amt, transaction_Date, transactionID, customer_name, currency, narration, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getNotificationData().getBusinessKey();
		String mobile_number = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getDebitMSISDN();
		float tranx_amt = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getTransactionAmt();
		Date tranx_date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
				.parse(request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionDate());
		String tranx_id = request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionID();
		String customer_name = request.getRequestPayload().getAdditionalData().getNotificationData().getFirstName();
		String currency = request.getRequestPayload().getAdditionalData().getNotificationData().getCurrency();
		String narration = request.getRequestPayload().getAdditionalData().getNotificationData().getNarration();
		float balance = request.getRequestPayload().getAdditionalData().getNotificationData().getBalance();
		
		try {
			jdbcTemplate.update(sqlQuery, new Object[] { bill_ref, mobile_number, tranx_amt, tranx_date, tranx_id,
					customer_name, currency, narration, balance });
			
			Header header = Header.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Notification received.")
					.build();

			TransactionInfo transactionInfo = TransactionInfo.builder()
					.transactionId(tranx_id)
					.build();

			ResponsePayload payload = ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();
			
			KCBResponse resp = KCBResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
			
			log.info("Notify request for Bill_Reference: {} & transactionID: {} is successful.", bill_ref, tranx_id);

			return resp;
			
		} catch (DuplicateKeyException dae) {
			log.error("Exception occured while inserting the data. The error is: " + dae.getMessage(), dae);
			String errorMessage = String.format(
					"Duplicate record is found with Bill_Reference: %s and transactionID: %s in KFS ERP system. Pass correct Performa invoice number and transactionID",
					bill_ref, tranx_id);
			throw new DuplicateRecordException(errorMessage);
		} catch (DataAccessException dae) {
			log.error("Exception occured while updating the transaction response. The error is: " + dae.getMessage(), dae);
			throw dae;
		}
	}
	
	private KCBResponse.KCBResponseBuilder mapRowToQueryResponse(ResultSet resultSet, int rowNum) throws SQLException {
		// This method is an implementation of the functional interface RowMapper.
		// It is used to map each row of a ResultSet to an object.
		TransactionInfo transactionInfo = TransactionInfo.builder()
				.transactionId(resultSet.getString("trx_number"))
				.customerName(resultSet.getString("customer_name"))
				.utilityName("KFS_INV")
				.subCompartment(resultSet.getString("SUB_COMPARTMENT"))
				.amount(resultSet.getString("AMOUNT_DUE_REMAINING"))
				.build();

		ResponsePayload payload = ResponsePayload.builder()
				.transactionInfo(transactionInfo)
				.build();
		
		return KCBResponse.builder()
				.responsePayload(payload);
	}

	public String objToJson(Object obj) {
		try {
			return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.debug("failed conversion: object to Json", e);
			return JSON_CONVERSION_FAILED;
		}
	}

}
