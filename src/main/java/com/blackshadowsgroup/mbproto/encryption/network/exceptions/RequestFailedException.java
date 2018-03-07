package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * RequestFailedException class
 */
@SuppressWarnings("unused")
public class RequestFailedException extends RuntimeException {


	public RequestFailedException() {
		super();
	}// constructor


	public RequestFailedException(String message) {
		super(message);
	}// constructor


	public RequestFailedException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public RequestFailedException(Throwable cause) {
		super(cause);
	}// constructor


}// RequestFailedException class
