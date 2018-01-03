package org.gal.messaging.engine.core.api;

public class EngineException extends RuntimeException {

	private static final long serialVersionUID = -7350944672207285916L;
	
	private final String code;
	
	private final String inResponseTo;

	public EngineException(String message, String code, String inResponseTo, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.inResponseTo = inResponseTo;
	}

	public EngineException(String message, String code, String inResponseTo) {
		super(message);
		this.code = code;
		this.inResponseTo = inResponseTo;
	}

	public String getCode() {
		return code;
	}

	public String getInResponseTo() {
		return inResponseTo;
	}
	
}
