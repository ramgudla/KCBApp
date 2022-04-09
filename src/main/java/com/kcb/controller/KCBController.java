package com.kcb.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kcb.dao.TransactionsDAO;
import com.kcb.domain.KCBRequest;
import com.kcb.domain.KCBResponse;
import com.kcb.exception.BillRefNumberNotFoundException;

@RestController
public class KCBController {

	private static Logger log = LoggerFactory.getLogger(KCBController.class);

	@Autowired
	TransactionsDAO transactionDao;

	@PostMapping(path = "/query")
	public KCBResponse query(@RequestBody KCBRequest request) throws BillRefNumberNotFoundException {
		log.info("Received request: {}", transactionDao.objToJson(request));
		
		try {
			KCBResponse resp = transactionDao.query(request);
			log.info("Finished execution : query method");
			return resp;
		} catch (BillRefNumberNotFoundException e) {
			throw e;
		}
	}

	@PostMapping(path = "/notify")
	public KCBResponse notify(@RequestBody KCBRequest request) throws Exception {
		log.info("Notification initiated...");
		log.info("Received request: {}", transactionDao.objToJson(request));
		
		KCBResponse resp = transactionDao.notify(request);

		log.info("Finished execution : notify method");
		return resp;
	}

}
