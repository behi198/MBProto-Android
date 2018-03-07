package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * ResetPasswordException class
 */
@SuppressWarnings("unused")
public class ResetPasswordException extends RuntimeException {


	public ResetPasswordException() {
		super();
	}// constructor


	public ResetPasswordException(String message) {
		super(message);
	}// constructor


	public ResetPasswordException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public ResetPasswordException(Throwable cause) {
		super(cause);
	}// constructor


}// ResetPasswordException class
