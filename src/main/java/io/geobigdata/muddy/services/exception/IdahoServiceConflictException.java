package io.geobigdata.muddy.services.exception;

import java.util.List;

import javax.ws.rs.core.Response;

public class IdahoServiceConflictException extends IdahoServiceException {
	private static final long serialVersionUID = 1736707486401545199L;

	public IdahoServiceConflictException(List<String> messages) {
		super(messages);
	}

	public IdahoServiceConflictException(List<String> messages, Throwable cause) {
		super(messages, cause);
	}

	public IdahoServiceConflictException() {
	}

	public IdahoServiceConflictException(String message) {
		super(message);
	}

	public IdahoServiceConflictException(Throwable cause) {
		super(cause);
	}


	public IdahoServiceConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.CONFLICT;
	}
}
