package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * PasswordInvalidException class
 */
@SuppressWarnings("unused")
public class PasswordInvalidException extends RuntimeException {


	public PasswordInvalidException() {
		super();
	}// constructor


	public PasswordInvalidException(String message) {
		super(message);
	}// constructor


	public PasswordInvalidException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public PasswordInvalidException(Throwable cause) {
		super(cause);
	}// constructor


}// PasswordInvalidException class
