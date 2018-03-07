package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * ServerResponseException class
 */
@SuppressWarnings("unused")
public class ServerResponseException extends RuntimeException {


	public ServerResponseException() {
		super();
	}// constructor


	public ServerResponseException(String message) {
		super(message);
	}// constructor


	public ServerResponseException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public ServerResponseException(Throwable cause) {
		super(cause);
	}// constructor


}// ServerResponseException class
