
package com.harsh.addressbook;

public class DuplicateContactException extends RuntimeException {

    public DuplicateContactException(String message) {
        super(message);
    }
}
