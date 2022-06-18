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
import com.kcb.domain.NonPlantationResponse;
import com.kcb.domain.PlantationResponse;
import com.kcb.domain.PlantationResponse.ResponseHeader;
import com.kcb.domain.PlantationResponse.ResponsePayload;
import com.kcb.domain.PlantationResponse.TransactionInfo;
import com.kcb.domain.StudentResponse;
import com.kcb.domain.TenderResponse;
import com.kcb.exception.DuplicateRecordException;

@Repository
public class TransactionsDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	private static final String JSON_CONVERSION_FAILED = "JSON_RESPONSE_CONVERSION_FAILED";

	private static Logger log = LoggerFactory.getLogger(TransactionsDAO.class);

	public PlantationResponse plantationQuery(KCBRequest request) {
		
		log.info("Plantation Query request received:\n {}", objToJson(request));
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getQueryData().getBusinessKey();
		
		try {
			ResponseHeader header = ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Processed Successfully.")
					.build();
			String sqlQuery = "select * from KFS_CUST_TRX_V where 1=1 and trx_number = ?";
			PlantationResponse.PlantationResponseBuilder responseBuilder = jdbcTemplate.queryForObject(sqlQuery,
					new Object[] { bill_ref }, this::mapRowToPlantationResponse);
			responseBuilder.header(header);
			log.info("Plantation Query request for BillRefNumber: {} is successful.", bill_ref);
			return responseBuilder.build();
		} catch (EmptyResultDataAccessException dae) {
			log.error("Exception occured while querying. The error is: " + dae.getMessage(), dae);
			
			String errorMessage = String.format("Payment validation Failed. Bill reference: %s does not exist", bill_ref);
			
			ResponseHeader header = ResponseHeader.builder()
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
			
			return PlantationResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		} catch (Exception ex) {
			log.error("Exception occured while querying. The error is: " + ex.getMessage(), ex);
			String errorMessage = String.format("Payment validation Failed for Bill reference: %s. The error is: %s", bill_ref, ex.getMessage());
			
			ResponseHeader header = ResponseHeader.builder()
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
			
			return PlantationResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		}
	}
	
	public NonPlantationResponse nonPlantationQuery(KCBRequest request) {

		log.info("Non Plantation Query request received:\n {}", objToJson(request));

		String bill_ref = request.getRequestPayload().getAdditionalData().getQueryData().getBusinessKey();

		try {
			NonPlantationResponse.ResponseHeader header = NonPlantationResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID()).statusCode("0")
					.statusMessage("Processed Successfully.")
					.build();
			String sqlQuery = "select * from KFS_NONPLANTATION_TRX_V where 1=1 and trx_number = ?";
			NonPlantationResponse.NonPlantationResponseBuilder responseBuilder = jdbcTemplate.queryForObject(sqlQuery,
					new Object[] { bill_ref }, this::mapRowToNonPlantationResponse);
			responseBuilder.header(header);
			log.info("Non Plantation Query request for BillRefNumber: {} is successful.", bill_ref);
			return responseBuilder.build();
		} catch (EmptyResultDataAccessException dae) {
			log.error("Exception occured while querying. The error is: " + dae.getMessage(), dae);

			String errorMessage = String.format("Payment validation Failed. Bill reference: %s does not exist",
					bill_ref);

			NonPlantationResponse.ResponseHeader header = NonPlantationResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();

			NonPlantationResponse.TransactionInfo transactionInfo = NonPlantationResponse.TransactionInfo.builder()
					.transactionId("")
					.customerName("")
					.utilityName("")
					.amount("0")
					.build();

			NonPlantationResponse.ResponsePayload payload = NonPlantationResponse.ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();

			return NonPlantationResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		} catch (Exception ex) {
			log.error("Exception occured while querying. The error is: " + ex.getMessage(), ex);
			String errorMessage = String.format("Payment validation Failed for Bill reference: %s. The error is: %s",
					bill_ref, ex.getMessage());

			NonPlantationResponse.ResponseHeader header = NonPlantationResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();

			NonPlantationResponse.TransactionInfo transactionInfo = NonPlantationResponse.TransactionInfo.builder()
					.transactionId("")
					.customerName("")
					.utilityName("")
					.amount("0")
					.build();

			NonPlantationResponse.ResponsePayload payload = NonPlantationResponse.ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();

			return NonPlantationResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		}
	}
	
	public TenderResponse tenderQuery(KCBRequest request) {
		
		log.info("Tender Query request received:\n {}", objToJson(request));
		
		String customer_number = request.getRequestPayload().getAdditionalData().getQueryData().getBusinessKey();
		
		try {
			TenderResponse.ResponseHeader header = TenderResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Processed Successfully.")
					.build();
			String sqlQuery = "select * from KFS_TENDER_DEPOSIT_CUSTOMER_V where 1=1 and customer_number = ?";
			TenderResponse.TenderResponseBuilder responseBuilder = jdbcTemplate.queryForObject(sqlQuery,
					new Object[] { customer_number }, this::mapRowToTenderResponse);
			responseBuilder.header(header);
			log.info("Tender request for Customer Number: {} is successful.", customer_number);
			return responseBuilder.build();
		} catch (EmptyResultDataAccessException dae) {
			log.error("Exception occured while querying. The error is: " + dae.getMessage(), dae);
			
			String errorMessage = String.format("Payment validation Failed. Customer Number: %s does not exist", customer_number);
			
			TenderResponse.ResponseHeader header = TenderResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();
			
			TenderResponse.ResponsePayload payload = TenderResponse.ResponsePayload.builder()
					.customerName("")
					.customerNumber("")
					.customerId("")
					.build();
			
			return TenderResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		} catch (Exception ex) {
			log.error("Exception occured while querying. The error is: " + ex.getMessage(), ex);
			String errorMessage = String.format("Payment validation Failed for Customer Number: %s. The error is: %s", customer_number, ex.getMessage());
			
			TenderResponse.ResponseHeader header = TenderResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();
			
			TenderResponse.ResponsePayload payload = TenderResponse.ResponsePayload.builder()
					.customerName("")
					.customerNumber("")
					.customerId("")
					.build();
			
			return TenderResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		}
	}
	
	public StudentResponse studentQuery(KCBRequest request) {
		
		log.info("Student Query request received:\n {}", objToJson(request));
		
		String admission_number = request.getRequestPayload().getAdditionalData().getQueryData().getBusinessKey();
		
		try {
			StudentResponse.ResponseHeader header = StudentResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Processed Successfully.")
					.build();
			String sqlQuery = "select * from KFS_STUDENT_CUSTOMER_V where 1=1 and admission_number = ?";
			StudentResponse.StudentResponseBuilder responseBuilder = jdbcTemplate.queryForObject(sqlQuery,
					new Object[] { admission_number }, this::mapRowToStudentResponse);
			responseBuilder.header(header);
			log.info("Query request for Admission Number: {} is successful.", admission_number);
			return responseBuilder.build();
		} catch (EmptyResultDataAccessException dae) {
			log.error("Exception occured while querying. The error is: " + dae.getMessage(), dae);
			
			String errorMessage = String.format("Payment validation Failed. Admission Number: %s does not exist", admission_number);
			
			StudentResponse.ResponseHeader header = StudentResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();
			
			StudentResponse.ResponsePayload payload = StudentResponse.ResponsePayload.builder()
					.studentName("")
					.admissionNumber("")
					.ErpStudentNumber("")
					.customerId("")
					.build();
			
			return StudentResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		} catch (Exception ex) {
			log.error("Exception occured while querying. The error is: " + ex.getMessage(), ex);
			String errorMessage = String.format("Payment validation Failed for the Admission Number: %s. The error is: %s ", admission_number, ex.getMessage());
			StudentResponse.ResponseHeader header = StudentResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("1")
					.statusMessage(errorMessage)
					.build();
			
			StudentResponse.ResponsePayload payload = StudentResponse.ResponsePayload.builder()
					.studentName("")
					.admissionNumber("")
					.ErpStudentNumber("")
					.customerId("")
					.build();
			
			return StudentResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
		}
	}
	
	public PlantationResponse plantationNotify(KCBRequest request) throws ParseException, DuplicateRecordException {
		
		log.info("Plantation Notify request received:\n {}", objToJson(request));
		
		String sqlQuery = "INSERT INTO KFS_KCB_NOTIFY_DATA_STG (Bill_Reference, Mobile_number, transaction_Amt, transaction_Date, transactionID, customer_name, currency, narration, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getNotificationData().getBusinessKey();
		String mobile_number = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getDebitMSISDN();
		float tranx_amt = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getTransactionAmt();
		Date tranx_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
				.parse(request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionDate());
		String tranx_id = request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionID();
		String customer_name = request.getRequestPayload().getAdditionalData().getNotificationData().getFirstName();
		String currency = request.getRequestPayload().getAdditionalData().getNotificationData().getCurrency();
		String narration = request.getRequestPayload().getAdditionalData().getNotificationData().getNarration();
		float balance = request.getRequestPayload().getAdditionalData().getNotificationData().getBalance();
		
		try {
			jdbcTemplate.update(sqlQuery, new Object[] { bill_ref, mobile_number, tranx_amt, tranx_date, tranx_id,
					customer_name, currency, narration, balance });
			
			ResponseHeader header = ResponseHeader.builder()
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
			
			PlantationResponse resp = PlantationResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
			
			log.info("Plantation Notify request for Bill_Reference: {} & transactionID: {} is successful.", bill_ref, tranx_id);

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
	
	public NonPlantationResponse nonPlantationNotify(KCBRequest request) throws ParseException, DuplicateRecordException {
		
		log.info("NonPlantation Notify request received:\n {}", objToJson(request));
		
		String sqlQuery = "INSERT INTO KFS_KCB_NOTIFY_NONPLANT_STG (Bill_Reference, Mobile_number, transaction_Amt, transaction_Date, transactionID, customer_name, currency, narration, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getNotificationData().getBusinessKey();
		String mobile_number = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getDebitMSISDN();
		float tranx_amt = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getTransactionAmt();
		Date tranx_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
				.parse(request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionDate());
		String tranx_id = request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionID();
		String customer_name = request.getRequestPayload().getAdditionalData().getNotificationData().getFirstName();
		String currency = request.getRequestPayload().getAdditionalData().getNotificationData().getCurrency();
		String narration = request.getRequestPayload().getAdditionalData().getNotificationData().getNarration();
		float balance = request.getRequestPayload().getAdditionalData().getNotificationData().getBalance();
		
		try {
			jdbcTemplate.update(sqlQuery, new Object[] { bill_ref, mobile_number, tranx_amt, tranx_date, tranx_id,
					customer_name, currency, narration, balance });
			
			NonPlantationResponse.ResponseHeader header = NonPlantationResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Notification received.")
					.build();

			NonPlantationResponse.TransactionInfo transactionInfo = NonPlantationResponse.TransactionInfo.builder()
					.transactionId(tranx_id)
					.build();

			NonPlantationResponse.ResponsePayload payload = NonPlantationResponse.ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();
			
			NonPlantationResponse resp = NonPlantationResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
			
			log.info("NonPlantation Notify request for Bill_Reference: {} & transactionID: {} is successful.", bill_ref, tranx_id);

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
	
	public TenderResponse tenderNotify(KCBRequest request) throws ParseException, DuplicateRecordException {
		
		log.info("Tender Notify request received:\n {}", objToJson(request));
		
		String sqlQuery = "INSERT INTO KFS_KCB_NOTIFY_TENDER_DEP_STG (Bill_Reference, Mobile_number, transaction_Amt, transaction_Date, transactionID, customer_name, currency, narration, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getNotificationData().getBusinessKey();
		String mobile_number = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getDebitMSISDN();
		float tranx_amt = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getTransactionAmt();
		Date tranx_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
				.parse(request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionDate());
		String tranx_id = request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionID();
		String customer_name = request.getRequestPayload().getAdditionalData().getNotificationData().getFirstName();
		String currency = request.getRequestPayload().getAdditionalData().getNotificationData().getCurrency();
		String narration = request.getRequestPayload().getAdditionalData().getNotificationData().getNarration();
		float balance = request.getRequestPayload().getAdditionalData().getNotificationData().getBalance();
		
		try {
			jdbcTemplate.update(sqlQuery, new Object[] { bill_ref, mobile_number, tranx_amt, tranx_date, tranx_id,
					customer_name, currency, narration, balance });
			
			TenderResponse.ResponseHeader header = TenderResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Notification received.")
					.build();

			TenderResponse.TransactionInfo transactionInfo = TenderResponse.TransactionInfo.builder()
					.transactionId(tranx_id)
					.build();

			TenderResponse.ResponsePayload payload = TenderResponse.ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();
			
			TenderResponse resp = TenderResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
			
			log.info("Tender Notify request for Bill_Reference: {} & transactionID: {} is successful.", bill_ref, tranx_id);

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
	
	public StudentResponse studentNotify(KCBRequest request) throws ParseException, DuplicateRecordException {
		
		log.info("Student Notify request received:\n {}", objToJson(request));
		
		String sqlQuery = "INSERT INTO KFS_KCB_NOTIFY_STUDENT_FEE_STG (Bill_Reference, Mobile_number, transaction_Amt, transaction_Date, transactionID, customer_name, currency, narration, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String bill_ref = request.getRequestPayload().getAdditionalData().getNotificationData().getBusinessKey();
		String mobile_number = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getDebitMSISDN();
		float tranx_amt = request.getRequestPayload().getAdditionalData().getNotificationData()
				.getTransactionAmt();
		Date tranx_date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
				.parse(request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionDate());
		String tranx_id = request.getRequestPayload().getAdditionalData().getNotificationData().getTransactionID();
		String customer_name = request.getRequestPayload().getAdditionalData().getNotificationData().getFirstName();
		String currency = request.getRequestPayload().getAdditionalData().getNotificationData().getCurrency();
		String narration = request.getRequestPayload().getAdditionalData().getNotificationData().getNarration();
		float balance = request.getRequestPayload().getAdditionalData().getNotificationData().getBalance();
		
		try {
			jdbcTemplate.update(sqlQuery, new Object[] { bill_ref, mobile_number, tranx_amt, tranx_date, tranx_id,
					customer_name, currency, narration, balance });
			
			StudentResponse.ResponseHeader header = StudentResponse.ResponseHeader.builder()
					.messageID(request.getHeader().getMessageID())
					.originatorConversationID(request.getHeader().getOriginatorConversationID())
					.statusCode("0")
					.statusMessage("Notification received.")
					.build();

			StudentResponse.TransactionInfo transactionInfo = StudentResponse.TransactionInfo.builder()
					.transactionId(tranx_id)
					.build();

			StudentResponse.ResponsePayload payload = StudentResponse.ResponsePayload.builder()
					.transactionInfo(transactionInfo)
					.build();
			
			StudentResponse resp = StudentResponse.builder()
					.header(header)
					.responsePayload(payload)
					.build();
			
			log.info("Student Notify request for Bill_Reference: {} & transactionID: {} is successful.", bill_ref, tranx_id);

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
	
	private PlantationResponse.PlantationResponseBuilder mapRowToPlantationResponse(ResultSet resultSet, int rowNum) throws SQLException {
		// This method is an implementation of the functional interface RowMapper.
		// It is used to map each row of a ResultSet to an object.
		TransactionInfo transactionInfo = TransactionInfo.builder()
				.transactionId(resultSet.getString("trx_number"))
				.customerName(resultSet.getString("customer_name"))
				.utilityName("Plantation")
				.subCompartment(resultSet.getString("SUB_COMPARTMENT"))
				.amount(resultSet.getString("AMOUNT_DUE_REMAINING"))
				.build();

		ResponsePayload payload = ResponsePayload.builder()
				.transactionInfo(transactionInfo)
				.build();
		
		return PlantationResponse.builder()
				.responsePayload(payload);
	}
	
	private NonPlantationResponse.NonPlantationResponseBuilder mapRowToNonPlantationResponse(ResultSet resultSet, int rowNum) throws SQLException {
		// This method is an implementation of the functional interface RowMapper.
		// It is used to map each row of a ResultSet to an object.
		NonPlantationResponse.TransactionInfo transactionInfo = NonPlantationResponse.TransactionInfo.builder()
				.transactionId(resultSet.getString("trx_number"))
				.customerName(resultSet.getString("customer_name"))
				.utilityName("Non Plantation")
				.amount(resultSet.getString("AMOUNT_DUE_REMAINING"))
				.build();

		NonPlantationResponse.ResponsePayload payload = NonPlantationResponse.ResponsePayload.builder()
				.transactionInfo(transactionInfo)
				.build();
		
		return NonPlantationResponse.builder()
				.responsePayload(payload);
	}
	
	private TenderResponse.TenderResponseBuilder mapRowToTenderResponse(ResultSet resultSet, int rowNum) throws SQLException {
		// This method is an implementation of the functional interface RowMapper.
		// It is used to map each row of a ResultSet to an object.
		TenderResponse.ResponsePayload payload = TenderResponse.ResponsePayload.builder()
				.customerName(resultSet.getString("customer_name"))
				.customerNumber(resultSet.getString("customer_number"))
				.customerId(resultSet.getString("customer_id"))
				.build();
		
		return TenderResponse.builder()
				.responsePayload(payload);
	}
	
	private StudentResponse.StudentResponseBuilder mapRowToStudentResponse(ResultSet resultSet, int rowNum) throws SQLException {
		// This method is an implementation of the functional interface RowMapper.
		// It is used to map each row of a ResultSet to an object.
		StudentResponse.ResponsePayload payload = StudentResponse.ResponsePayload.builder()
				.studentName(resultSet.getString("customer_name"))
				.admissionNumber(resultSet.getString("admission_number"))
				.ErpStudentNumber(resultSet.getString("customer_number"))
				.customerId(resultSet.getString("customer_id"))
				.build();
		
		return StudentResponse.builder()
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
