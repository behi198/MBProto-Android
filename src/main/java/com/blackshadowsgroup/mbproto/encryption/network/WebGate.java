package com.blackshadowsgroup.mbproto.encryption.network;


import com.blackshadowsgroup.mbproto.encryption.encrypt.interfaces.ConnectionHandler;

/**
 * WebGate interface
 */
@SuppressWarnings("unused")
public interface WebGate {


    int ARGUMENT_ERROR = 400;


    /**
     * Server response code
     */
    int NOT_FOUND_ERROR = 404;


    /**
     * Server response code
     */
    int RESULT_OK = 200;


    /**
     * Server response code
     */
    int FLOOD_CODE = 406;


    /**
     * Server response code
     */
    int USER_BLOCKED_OR_AUTH_KEY_ID_INVALID_ERROR_CODE = 401;


    /**
     * Server response code
     */
    int SERVER_NOT_RESPONSE = 500;


    /**
     * Application error code
     */
    int REQUEST_NOT_SEND = 1000;


    void sendRequest(BaseRequest request);


    boolean isUserRegistered();


    boolean isUserLoggedIn();


    void signOut(ConnectionHandler handler);


    void checkVersion(String osType, String platformVersion, String bundle, String versionCode, ConnectionHandler connectionHandler);


    void registerUser(String passwordSt, String usernameSt, String userfamilySt, ConnectionHandler handler);


    void initSession(String mobile, ConnectionHandler handler);


    void initDHExchange(String code, ConnectionHandler handler);


    void getPolicy(byte[] rawBytes, ConnectionHandler handler);


    void getLink(ConnectionHandler handler);


    void signIn(String password, ConnectionHandler handler);


    void resetPassword(ConnectionHandler handler);

    boolean clearSession();

}// WebGate interface
