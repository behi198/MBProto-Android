package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * UserBlockedException class
 */
@SuppressWarnings("unused")
public class UserBlockedException extends RuntimeException {


	public UserBlockedException() {
		super();
	}// constructor


	public UserBlockedException(String message) {
		super(message);
	}// constructor


	public UserBlockedException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public UserBlockedException(Throwable cause) {
		super(cause);
	}// constructor


}// UserBlockedException class
