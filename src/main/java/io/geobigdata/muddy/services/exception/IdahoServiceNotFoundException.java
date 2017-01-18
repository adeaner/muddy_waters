package io.geobigdata.muddy.services.exception;

import java.util.List;

import javax.ws.rs.core.Response;

public class IdahoServiceNotFoundException extends IdahoServiceException {
	private static final long serialVersionUID = 1736707486401545199L;

	public IdahoServiceNotFoundException(List<String> messages) {
		super(messages);
	}

	public IdahoServiceNotFoundException(List<String> messages, Throwable cause) {
		super(messages, cause);
	}

	public IdahoServiceNotFoundException() {
	}

	public IdahoServiceNotFoundException(String message) {
		super(message);
	}

	public IdahoServiceNotFoundException(Throwable cause) {
		super(cause);
	}


	public IdahoServiceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.NOT_FOUND;
	}
}
