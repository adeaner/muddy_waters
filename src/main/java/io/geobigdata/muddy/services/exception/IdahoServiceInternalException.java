package io.geobigdata.muddy.services.exception;

import java.util.List;

import javax.ws.rs.core.Response;

public class IdahoServiceInternalException extends IdahoServiceException {
	private static final long serialVersionUID = 1736707486401545199L;

	public IdahoServiceInternalException(List<String> messages) {
		super(messages);
	}

	public IdahoServiceInternalException(List<String> messages, Throwable cause) {
		super(messages, cause);
	}

	public IdahoServiceInternalException() {
	}

	public IdahoServiceInternalException(String message) {
		super(message);
	}

	public IdahoServiceInternalException(Throwable cause) {
		super(cause);
	}


	public IdahoServiceInternalException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.INTERNAL_SERVER_ERROR;
	}
}
