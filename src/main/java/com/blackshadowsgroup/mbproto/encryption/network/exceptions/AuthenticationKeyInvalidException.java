package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * AuthenticationKeyInvalidException class
 */
@SuppressWarnings("unused")
public class AuthenticationKeyInvalidException extends RuntimeException {


	public AuthenticationKeyInvalidException() {
		super();
	}// constructor


	public AuthenticationKeyInvalidException(String message) {
		super(message);
	}// constructor


	public AuthenticationKeyInvalidException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public AuthenticationKeyInvalidException(Throwable cause) {
		super(cause);
	}// constructor


}// AuthenticationKeyInvalidException class
