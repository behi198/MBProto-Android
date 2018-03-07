package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * VerificationCodeInvalidException class
 */
@SuppressWarnings("unused")
public class VerificationCodeInvalidException extends RuntimeException {


	public VerificationCodeInvalidException() {
		super();
	}// constructor


	public VerificationCodeInvalidException(String message) {
		super(message);
	}// constructor


	public VerificationCodeInvalidException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public VerificationCodeInvalidException(Throwable cause) {
		super(cause);
	}// constructor


}// VerificationCodeInvalidException class
