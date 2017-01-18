package io.geobigdata.muddy.services.exception.bodywriter;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.gson.Gson;
import io.geobigdata.muddy.services.exception.IdahoServiceException;

@Produces( MediaType.APPLICATION_JSON )
public class IdahoServiceMessageBodyWriter implements MessageBodyWriter<IdahoServiceException> {
 
    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return type == IdahoServiceException.class;
    }
 
    @Override
    public long getSize(IdahoServiceException myBean, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        // deprecated by JAX-RS 2.0 and ignored by JAX-RS runtime
        return -1;
    }

    @Override
    public void writeTo(IdahoServiceException exception,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream)
                        throws IOException, WebApplicationException {
        
        Gson gson = new Gson();
        
        String jsonString = gson.toJson(exception);
        
        entityStream.write( jsonString.getBytes( "UTF-8" ) );
    }
}