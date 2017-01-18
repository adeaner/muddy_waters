package io.geobigdata.muddy.services.exception;

import java.util.List;

import javax.ws.rs.core.Response;

public class IdahoBadRequestException extends IdahoServiceException {
	private static final long serialVersionUID = -2621117784193984815L;

	public IdahoBadRequestException(List<String> messages) {
		super(messages);
	}

	public IdahoBadRequestException(List<String> messages, Throwable cause) {
		super(messages, cause);
	}

	public IdahoBadRequestException() {
	}

	public IdahoBadRequestException(String message) {
		super(message);
	}

	public IdahoBadRequestException(Throwable cause) {
		super(cause);
	}

	public IdahoBadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public Response.Status getStatus() {
		return Response.Status.BAD_REQUEST;
	}

}
