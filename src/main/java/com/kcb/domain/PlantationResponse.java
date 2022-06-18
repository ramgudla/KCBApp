package com.kcb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantationResponse {
	public ResponseHeader header;
	public ResponsePayload responsePayload;
	
	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResponseHeader {
		public String messageID;
		public String originatorConversationID;
		public String statusCode;
		public String statusMessage;
	}

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ResponsePayload {
		public TransactionInfo transactionInfo;
	}
	
	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class TransactionInfo {
		public String transactionId;
		public String utilityName;
	    public String customerName;
	    public String subCompartment;
	    public String amount;
	}
	
}




