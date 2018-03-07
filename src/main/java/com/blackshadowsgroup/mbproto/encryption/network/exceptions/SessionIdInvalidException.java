package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * SessionIdInvalidException class
 */
@SuppressWarnings("unused")
public class SessionIdInvalidException extends RuntimeException {


	public SessionIdInvalidException() {
		super();
	}// constructor


	public SessionIdInvalidException(String message) {
		super(message);
	}// constructor


	public SessionIdInvalidException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public SessionIdInvalidException(Throwable cause) {
		super(cause);
	}// constructor


}// SessionIdInvalidException class
