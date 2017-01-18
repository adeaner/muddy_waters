package io.geobigdata.muddy.services.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * In JAX-RS, this method is needed to log responses, <b>after</b> they have been committed.  It
 * isn't possible to use the ContainerResponseFilter to do what we want because that filter is called
 * <b>before</b> the response is actually written.  That's great for things like additional headers, but
 * it won't do us any good for logging.
 */
@Logged
@Provider
public class ResponseInterceptor implements WriterInterceptor {
    private static Logger LOG = LoggerFactory.getLogger(ResponseInterceptor.class);

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        OutputStream originalStream = context.getOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        context.setOutputStream(baos);
        try {
            context.proceed();
        } finally {
            LOG.info("response body: " + baos.toString("UTF-8"));
            baos.writeTo(originalStream);
            baos.close();
            context.setOutputStream(originalStream);
        }
    }
}
