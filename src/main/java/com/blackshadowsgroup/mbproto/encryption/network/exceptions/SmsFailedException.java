package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * SmsFailedException class
 */
public class SmsFailedException extends RuntimeException {


	public SmsFailedException() {
		super();
	}// constructor


	public SmsFailedException(String message) {
		super(message);
	}// constructor


	public SmsFailedException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public SmsFailedException(Throwable cause) {
		super(cause);
	}// constructor


}// SmsFailedException class
