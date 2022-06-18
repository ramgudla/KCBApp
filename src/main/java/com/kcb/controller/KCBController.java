package com.kcb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kcb.dao.TransactionsDAO;
import com.kcb.domain.KCBRequest;
import com.kcb.domain.NonPlantationResponse;
import com.kcb.domain.PlantationResponse;
import com.kcb.domain.StudentResponse;
import com.kcb.domain.TenderResponse;
import com.kcb.exception.BillRefNumberNotFoundException;

@RestController
public class KCBController {

	private static Logger log = LoggerFactory.getLogger(KCBController.class);

	@Autowired
	TransactionsDAO transactionDao;
	
	// Query API

	@PostMapping(path = "/query/plantation")
	public PlantationResponse queryPlantation(@RequestBody KCBRequest request) throws BillRefNumberNotFoundException {
		log.info("Received request: queryPlantation method");
		PlantationResponse resp = transactionDao.plantationQuery(request);
		log.info("Finished execution : queryPlantation method");
		return resp;
	}
	
	@PostMapping(path = "/query/nonplantation")
	public NonPlantationResponse queryNonplantation(@RequestBody KCBRequest request) throws BillRefNumberNotFoundException {
		log.info("Received request:  queryNonplantation method");
		NonPlantationResponse resp = transactionDao.nonPlantationQuery(request);
		log.info("Finished execution : queryNonplantation method");
		return resp;
	}
	
	@PostMapping(path = "/query/tenderdeposit")
	public TenderResponse queryTender(@RequestBody KCBRequest request) throws BillRefNumberNotFoundException {
		log.info("Received request: queryTender method");
		TenderResponse resp = transactionDao.tenderQuery(request);
		log.info("Finished execution : queryTender method");
		return resp;
	}
	
	@PostMapping(path = "/query/student")
	public StudentResponse queryStudent(@RequestBody KCBRequest request) throws BillRefNumberNotFoundException {
		log.info("Received request: queryStudent method");
		StudentResponse resp = transactionDao.studentQuery(request);
		log.info("Finished execution : queryStudent method");
		return resp;
	}
	
	// Notify API

	@PostMapping(path = "/notify/plantation")
	public PlantationResponse plantationNotify(@RequestBody KCBRequest request) throws Exception {
		log.info("Received request: plantationNotify method");
		PlantationResponse resp = transactionDao.plantationNotify(request);
		log.info("Finished execution : notify method");
		return resp;
	}
	
	@PostMapping(path = "/notify/nonplantation")
	public NonPlantationResponse nonPlantationNotify(@RequestBody KCBRequest request) throws Exception {
		log.info("Received request: nonPlantationNotify method");
		NonPlantationResponse resp = transactionDao.nonPlantationNotify(request);
		log.info("Finished execution : notify method");
		return resp;
	}
	
	@PostMapping(path = "/notify/tenderdeposit")
	public TenderResponse tenderNotify(@RequestBody KCBRequest request) throws Exception {
		log.info("Received request: tenderNotify method");
		TenderResponse resp = transactionDao.tenderNotify(request);
		log.info("Finished execution : notify method");
		return resp;
	}
	
	@PostMapping(path = "/notify/student")
	public StudentResponse studentNotify(@RequestBody KCBRequest request) throws Exception {
		log.info("Received request: studentNotify method");
		StudentResponse resp = transactionDao.studentNotify(request);
		log.info("Finished execution : notify method");
		return resp;
	}
	
}
