package com.comicstore.storeservice.Utils.Exceptions;

public class DuplicateStoreLocationException extends RuntimeException{
    public DuplicateStoreLocationException() {}

    public DuplicateStoreLocationException(String message) { super(message); }

    public DuplicateStoreLocationException(Throwable cause) { super(cause); }

    public DuplicateStoreLocationException(String message, Throwable cause) { super(message, cause); }
}
