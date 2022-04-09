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
public class KCBRequest {
	public RequestHeader header;
	public RequestPayload requestPayload;

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RequestHeader {
		public String messageID;
		public String originatorConversationID;
		public String channelCode;
		public String timeStamp;
	}

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class PrimaryData {
		public String businessKey;
		public String businessKeyType;
	}

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class NotificationData {
		public String businessKey;
		public String businessKeyType;
		public String debitMSISDN;
		public float transactionAmt;
		public String transactionDate;
		public String transactionID;
		public String firstName;
		public String middleName;
		public String lastName;
		public String currency;
		public String narration;
		public String transactionType;
		public float balance;
	}

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class QueryData {
	    public String businessKey;
	    public String businessKeyType;
	}

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class AdditionalData {
		public NotificationData notificationData;
		public QueryData queryData;
	}

	@Data
	@Builder
	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RequestPayload {
		public PrimaryData primaryData;
		public AdditionalData additionalData;
	}
}