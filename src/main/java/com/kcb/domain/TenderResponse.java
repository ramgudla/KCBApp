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
public class TenderResponse {
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
		public String channelCode;
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
		public String customerName;
		public String customerNumber;
		public String customerId;
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
	}
}