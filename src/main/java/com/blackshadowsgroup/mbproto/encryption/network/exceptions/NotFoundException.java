package com.blackshadowsgroup.mbproto.encryption.network.exceptions;

/**
 * NotFoundException class
 *
 */
@SuppressWarnings("unused")
public class NotFoundException extends RuntimeException {


	public NotFoundException() {
		super();
	}// constructor


	public NotFoundException(String message) {
		super(message);
	}// constructor


	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}// constructor


	public NotFoundException(Throwable cause) {
		super(cause);
	}// constructor


}// NotFoundException class
