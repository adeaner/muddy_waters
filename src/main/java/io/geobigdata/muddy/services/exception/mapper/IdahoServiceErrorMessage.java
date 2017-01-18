

package io.geobigdata.muddy.services.exception.mapper;

import java.util.ArrayList;
import java.util.List;


/**
 * This class exists as the return to the front end.  Messages are serialized
 * from this to an error response.
 *
 */
public class IdahoServiceErrorMessage {

    private List<String> messages = null;
    private String stackTrace = null;
    private String exceptionType = null;

    /* package */ IdahoServiceErrorMessage(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    /**
     * Gets the value of the messages property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the object.
     * This is why there is not a <CODE>set</CODE> method for the messages property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMessages().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    /* package */ List<String> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return this.messages;
    }

    /**
     * Gets the value of the stackTrace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the value of the stackTrace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    /* package */ void setStackTrace(String value) {
        this.stackTrace = value;
    }

    /* package */ void setExceptionType(String exceptionType) {
    	this.exceptionType = exceptionType;
    }
    
    public String getExceptionType() {
    	return exceptionType;
    }
}
