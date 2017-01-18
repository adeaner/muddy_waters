package io.geobigdata.muddy.services.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Base class for all ServiceExceptions.  This class is abstract so that
 * derived classes must override
 */
public abstract class IdahoServiceException extends WebApplicationException {
    private static final long serialVersionUID = -1957137270200438785L;

    private List<String> messages = null;
    private String stackTraceString = null;

    /* package */ IdahoServiceException(List<String> messages) {
        super();
        this.messages = messages;
    }


    /* package */ IdahoServiceException(List<String> messages, Throwable cause) {
        super();
        this.messages = messages;

        stackTraceString = ExceptionUtils.getStackTrace(cause);
    }

    public List<String> getMessages() {
        return messages;
    }

    public String getStackTraceString() {
        return stackTraceString;
    }

    public void addErrorMessage(String message) {
        if (messages == null)
            messages = new ArrayList<>();

        this.messages.add(message);
    }

    /* package */ IdahoServiceException() {
        super();
    }


    /* package */ IdahoServiceException(String message) {
        super(message);

        if (messages == null)
            messages = new ArrayList<>();

        this.messages.add(message);
    }

    /* package */ IdahoServiceException(Throwable cause) {
        super(cause);

        stackTraceString = ExceptionUtils.getStackTrace(cause);

        if (messages == null)
            messages = new ArrayList<>();

        this.messages.add(cause.getMessage());
    }

    /* package */ IdahoServiceException(String message, Throwable cause) {
        super(message, cause);

        if (messages == null)
            messages = new ArrayList<>();

        this.messages.add(message);

        stackTraceString = ExceptionUtils.getStackTrace(cause);
    }


    public abstract Response.Status getStatus();

    public String getExceptionType() {
        return this.getClass().getSimpleName();
    }
}
