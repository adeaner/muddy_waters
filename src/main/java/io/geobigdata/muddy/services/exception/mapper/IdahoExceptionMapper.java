package io.geobigdata.muddy.services.exception.mapper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import io.geobigdata.muddy.services.exception.IdahoServiceException;

/**
 * Maps a IdahoServiceException to a response.  Basically this means copying data from the
 * IdahoServiceException into a IdahoServiceErrorMessage and serializing it with JSON.
 * <p>
 * Note that currently this method will HTML-ize the stack trace if it exists so that it can
 * be displayed in some sort of UI.
 *
 */
@Provider
public class IdahoExceptionMapper implements ExceptionMapper<IdahoServiceException> {

	@Override
	@Produces(MediaType.APPLICATION_JSON)
	public Response toResponse(IdahoServiceException exception) {

		IdahoServiceErrorMessage catalogServiceErrorMessage = new IdahoServiceErrorMessage(exception.getExceptionType());

		if (exception.getStackTraceString() != null)
            catalogServiceErrorMessage.setStackTrace(exception.getStackTraceString().replaceAll("\n", "<br />")
			        .replaceAll("\t", "&nbsp;&nbsp;"));

		if (exception.getMessages() != null) {
			for (String nextMessage : exception.getMessages()) {
                catalogServiceErrorMessage.getMessages().add(nextMessage);
			}
		}
		else {
			if (exception.getMessage() != null)
                catalogServiceErrorMessage.getMessages().add(exception.getMessage());
		}
		
		Gson gson = new Gson();

		return Response.status(exception.getStatus()).entity(gson.toJson(catalogServiceErrorMessage)).build();
	}
}
