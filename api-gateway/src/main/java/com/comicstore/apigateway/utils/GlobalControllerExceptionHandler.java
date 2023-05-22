package com.comicstore.apigateway.utils;

import com.comicstore.apigateway.utils.exceptions.CardGame.DuplicateCardGameNameException;
import com.comicstore.apigateway.utils.exceptions.CardGame.DuplicateSetNameException;
import com.comicstore.apigateway.utils.exceptions.Clients.DuplicateClientInformationException;
import com.comicstore.apigateway.utils.exceptions.InvalidInputException;
import com.comicstore.apigateway.utils.exceptions.Clients.NoEmailAndPhoneException;
import com.comicstore.apigateway.utils.exceptions.NotFoundException;
import com.comicstore.apigateway.utils.exceptions.StoreInventory.DuplicateStoreLocationException;
import com.comicstore.apigateway.utils.exceptions.Tournament.IllegalEntryCostChange;
import com.comicstore.apigateway.utils.exceptions.Tournament.WinnerNotInPlayerListException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.CONFLICT;

@Slf4j
@RestControllerAdvice
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WinnerNotInPlayerListException.class)
    public HttpErrorInfo handleWinnerNotInPlayerList(WebRequest request, Exception ex) {
        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(IllegalEntryCostChange.class)
    public HttpErrorInfo handleIllegalEntryCostChange(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DuplicateStoreLocationException.class)
    public HttpErrorInfo handleDuplicateStoreLocationException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }


    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public HttpErrorInfo handleNotFoundException(WebRequest request, Exception ex) {

        return createHttpErrorInfo(NOT_FOUND, request, ex);
    }

    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public HttpErrorInfo handleInvalidInputException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(UNPROCESSABLE_ENTITY, request, ex);
    }
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicateClientInformationException.class)
    public HttpErrorInfo handleDuplicateFullNameException(WebRequest request, Exception ex) {
        log.warn("Error in controller advice");

        return createHttpErrorInfo(CONFLICT, request, ex);
    }
    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(NoEmailAndPhoneException.class)
    public HttpErrorInfo handleNoEmailAndPhoneException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(BAD_REQUEST, request, ex);
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicateSetNameException.class)
    public HttpErrorInfo handleDuplicateSetNameException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(CONFLICT, request, ex);
    }
    @ResponseStatus(CONFLICT)
    @ExceptionHandler(DuplicateCardGameNameException.class)
    public HttpErrorInfo handleDuplicateCardGameNameException(WebRequest request, Exception ex) {
        return createHttpErrorInfo(CONFLICT, request, ex);
    }
    private HttpErrorInfo createHttpErrorInfo(HttpStatus httpStatus, WebRequest request, Exception ex) {
        final String path = request.getDescription(false);
        final String message = ex.getMessage();

        log.debug("Returning HTTP status: {} for path: {}, message: {}", httpStatus, path, message);

        return new HttpErrorInfo(httpStatus, path, message);
    }

}