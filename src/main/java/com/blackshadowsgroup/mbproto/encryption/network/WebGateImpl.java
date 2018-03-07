package com.blackshadowsgroup.mbproto.encryption.network;

import android.content.Context;
import android.util.Base64;


import com.blackshadowsgroup.mbproto.encryption.binary.BinaryReader;
import com.blackshadowsgroup.mbproto.encryption.binary.BinaryWriter;
import com.blackshadowsgroup.mbproto.encryption.encrypt.aes.AES;
import com.blackshadowsgroup.mbproto.encryption.encrypt.diffiehellman.DH;
import com.blackshadowsgroup.mbproto.encryption.encrypt.interfaces.ConnectionHandler;
import com.blackshadowsgroup.mbproto.encryption.encrypt.rsa.RSA;
import com.blackshadowsgroup.mbproto.encryption.model.Result;
import com.blackshadowsgroup.mbproto.encryption.encrypt.preferences.MySharedPreferences;
import com.blackshadowsgroup.mbproto.encryption.encrypt.sha.SHA2;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.InitializeSessionFailedException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.MethodBlockedException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.RequestFailedException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.ServerFloodException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.SessionIdInvalidException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.UserBlockedException;
import com.blackshadowsgroup.mbproto.encryption.network.exceptions.factory.ExceptionFactory;
import com.blackshadowsgroup.mbproto.encryption.threads.DefaultExecutorSupplier;
import com.blackshadowsgroup.mbproto.encryption.threads.PriorityRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * WebGateImpl class
 * @author Behi198
 */
@SuppressWarnings("unused")
final class WebGateImpl implements WebGate {


    private static Config config;

    private SessionManager sessionManager;
    private MySharedPreferences preferences;

    private BinaryReader reader;
    private BinaryWriter writer;

    private static WebGateImpl instance;
    private int retryCount;


    static WebGateImpl getInstance() {
        return instance;
    }// getInstance method


    private WebGateImpl(Context context) {
        sessionManager = SessionManager.getInstance();
        preferences = MySharedPreferences.getInstance(context, sessionManager.getPreferencesName());
    }// constructor


    @Override
    public boolean isUserRegistered() {
        return sessionManager.isUserRegistered();
    }// isUserRegistered method


    @Override
    public boolean isUserLoggedIn() {
        return sessionManager.getSessionId() != 0;
    }// getSessionId method


    @Override
    public void signOut(final ConnectionHandler handler) {
        writer = new BinaryWriter();
        try {
            writer.writeInt32(Methods.SIGN_OUT);
            sendPacket(writer.toByteArray(), new ConnectionHandler() {

                @Override
                public void onResultReceived(Result result) {
                    BinaryReader binaryReader = new BinaryReader(result.getStream().toByteArray());
                    try {
                        config.url = binaryReader.readString();
                        if (handler != null) {
                            handler.onResultReceived(result);
                        }// if
                    } catch (IOException e) {
                        e.printStackTrace();
                    }// catch
                }// onResultReceived method

                @Override
                public void onRequestFailed(Throwable throwable) {
                    handler.onRequestFailed(throwable);
                }// onRequestFailed method
            }, retryCount);
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRequestFailed(new RequestFailedException());
            }// if
        }// catch
    }// signOut method


    @Override
    public void checkVersion(String osType, String platformVersion, String bundle, String versionCode, final ConnectionHandler connectionHandler) {
        writer = new BinaryWriter();
        try {
            writer.writeInt32(Methods.CHECK_VERSION);
            writer.writeString(osType);
            writer.writeString(platformVersion);
            writer.writeString(bundle);
            writer.writeString(versionCode);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        DefaultExecutorSupplier.getInstance()
                .forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.LOW) {
                    @Override
                    public void run() {
                        long authKeyId = sessionManager.getAuthKeyId();
                        byte[] sharedKey = sessionManager.getSharedKey();
                        sessionManager.setAuthKeyId(2962488597456547L);
                        BinaryWriter writer1 = new BinaryWriter();
                        try {
                            writer1.writeInt64(sessionManager.getAuthKeyId());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        sessionManager.setSharedKey(writer1.toByteArray());
                        long time = System.currentTimeMillis();
                        int length = writer.toByteArray().length;
                        long seqNo = SEQ.getSequenceNumber();
                        sessionManager.setSalt();
                        writer1 = new BinaryWriter();
                        try {
                            writer1.writeInt64(sessionManager.getSalt());
                            writer1.writeInt64(sessionManager.getSessionId());
                            writer1.writeInt64(time);
                            writer1.writeInt32(length);
                            writer1.writeInt64(seqNo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }// catch

                        sessionManager.setMsgKey(SHA2.getInstance().hash(writer1.toByteArray()));
                        writer1 = new BinaryWriter();
                        try {
                            writer1.writeByteArray(sessionManager.getSharedKey());
                            writer1.writeByteArray(sessionManager.getMsgKey());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }// catch

                        byte[] aesKey = SHA2.getInstance().hash256(writer1.toByteArray());
                        writer1 = new BinaryWriter();
                        try {
                            writer1.writeInt64(sessionManager.getSalt());
                            writer1.writeInt64(sessionManager.getSessionId());
                            writer1.writeInt64(time);
                            writer1.writeInt32(length);
                            writer1.writeInt64(seqNo);
                            writer1.writeByteArray(writer.toByteArray());
                            AES aes = new AES(aesKey);
                            byte[] encryptedData = aes.encrypt(writer1.toByteArray());
                            writer = new BinaryWriter();
                            writer.writeByteArray(encryptedData);
                            Result result = connect(config.URL);
                            if (connectionHandler != null) {
                                if (result.getCode() == WebGate.RESULT_OK) {
                                    BinaryReader reader = new BinaryReader(result.getStream().toString().getBytes());
                                    result.setMessage(reader.readString());
                                    connectionHandler.onResultReceived(result);
                                } else if (result.getCode() == WebGate.USER_BLOCKED_OR_AUTH_KEY_ID_INVALID_ERROR_CODE) {
                                    clearSession();
                                    connectionHandler.onRequestFailed(new SessionIdInvalidException());
                                } else {
                                    connectionHandler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
                                }// else
                            }// if
                            saveSession();
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (connectionHandler != null) {
                                connectionHandler.onRequestFailed(new RequestFailedException());
                            }// if
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (connectionHandler != null) {
                                connectionHandler.onRequestFailed(new RequestFailedException());
                            }// if
                        }// catch
                        sessionManager.setAuthKeyId(authKeyId);
                        sessionManager.setSharedKey(sharedKey);
                    }// run method
                });
    }// checkVersion method


    private void sendSms(final String mobile, final ConnectionHandler handler) {
        sessionManager.setMobile(mobile);
        sessionManager.setMsgKey(SHA2.getInstance().hash("0"));
        sessionManager.setAuthKeyId(2);
        DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.HIGH) {
                    @Override
                    public void run() {
                        try {
                            writer = new BinaryWriter();
                            writer.writeInt32(Methods.SEND_SMS);
                            writer.writeString(mobile);
                            Result result = connect(config.URL);
                            if (handler != null) {
                                if (result.getCode() == WebGate.RESULT_OK)
                                    handler.onResultReceived(result);
                                else {
                                    if (result.getCode() == WebGate.FLOOD_CODE) {
                                        handler.onRequestFailed(new ServerFloodException(result.getStream().toString()));
                                    } else {
                                        handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
                                    }// else
                                }// else
                            }// if
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (handler != null)
                                handler.onRequestFailed(new RequestFailedException());
                        }// catch
                    }// run
                });
    }// sendSms method


    @Override
    public void registerUser(final String passwordSt, final String usernameSt, final String userfamilySt, final ConnectionHandler handler) {
        DefaultExecutorSupplier.getInstance().forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.HIGH) {
                    @Override
                    public void run() {
                        BinaryWriter writer1 = new BinaryWriter();
                        byte[] rawBytes;
                        try {
                            writer1.writeInt32(Methods.REGISTER);
                            writer1.writeString(passwordSt);
                            writer1.writeString(usernameSt);
                            writer1.writeString(userfamilySt);
                            rawBytes = writer1.toByteArray();
                            final byte[] finalRawBytes = rawBytes;
                            long time = System.currentTimeMillis();
                            int length = finalRawBytes.length;
                            long seqNo = SEQ.getSequenceNumber();
                            sessionManager.setSalt();
                            writer = new BinaryWriter();
                            try {
                                writer.writeInt64(sessionManager.getSalt());
                                writer.writeInt64(sessionManager.getSessionId());
                                writer.writeInt64(time);
                                writer.writeInt32(length);
                                writer.writeInt64(seqNo);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }// catch
                            if (sessionManager.getSessionId() == 0) {
                                sessionManager.setAuthKeyId(2);
                                BinaryWriter wr = new BinaryWriter();
                                try {
                                    wr.writeInt64(2);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }// catch
                                sessionManager.setSharedKey(wr.toByteArray());
                            }// if
                            sessionManager.setMsgKey(SHA2.getInstance().hash(writer.toByteArray()));
                            writer = new BinaryWriter();
                            try {
                                writer.writeByteArray(sessionManager.getSharedKey());
                                writer.writeByteArray(sessionManager.getMsgKey());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }// catch
                            byte[] aesKey = SHA2.getInstance().hash256(writer.toByteArray());

                            writer = new BinaryWriter();
                            try {
                                writer.writeInt64(sessionManager.getSalt());
                                writer.writeInt64(sessionManager.getSessionId());
                                writer.writeInt64(time);
                                writer.writeInt32(length);
                                writer.writeInt64(seqNo);
                                writer.writeByteArray(finalRawBytes);
                                AES aes = new AES(aesKey);
                                byte[] encryptedData = aes.encrypt(writer.toByteArray());
                                writer = new BinaryWriter();
                                writer.writeByteArray(encryptedData);
                                Result result = connect(config.URL);
                                if (handler != null) {
                                    if (result.getCode() == WebGate.RESULT_OK) {
                                        result.setMessage(handleResponse(result.getStream()));
                                        saveSession();
                                        handler.onResultReceived(result);
                                    } else if (result.getCode() == WebGate.USER_BLOCKED_OR_AUTH_KEY_ID_INVALID_ERROR_CODE) {
                                        clearSession();
                                        handler.onRequestFailed(new InitializeSessionFailedException());
                                    } else {
                                        handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
                                    }// else
                                }// if
                            } catch (IOException e) {
                                writer = new BinaryWriter();
                                getLink(new ConnectionHandler() {
                                    @Override
                                    public void onResultReceived(Result result) {
                                        saveSession();
                                        sendPacket(finalRawBytes, handler, retryCount);
                                    }// onResultReceived method

                                    @Override
                                    public void onRequestFailed(Throwable throwable) {
                                        if (handler != null) {
                                            handler.onRequestFailed(throwable);
                                        }// if
                                    }// onRequestFailed method
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (handler != null)
                                    handler.onRequestFailed(new RequestFailedException());
                            }// catch
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (handler != null)
                                handler.onRequestFailed(new RequestFailedException());
                        }// catch
                    }// run method
                });
    }// registerUser method


    @Override
    public void initSession(String mobile, final ConnectionHandler handler) {
        if (preferences.containsKey("zpu")) {
            retrieveSession();
        }
        if (sessionManager.getSessionId() == 0)
            sendSms(mobile, new ConnectionHandler() {
                @Override
                public void onResultReceived(Result result) {
                    if (result != null) {
                        ByteArrayOutputStream stream = result.getStream();
                        reader = new BinaryReader(stream.toByteArray());
                        try {
                            sessionManager.setHash(reader.readString());
                            RSA.getInstance().setE(new BigInteger(reader.readString()));
                            RSA.getInstance().setN(new BigInteger(reader.readString()));
                            if (handler != null) {
                                handler.onResultReceived(result);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }// catch
                    }// if
                }// onResultReceived method

                @Override
                public void onRequestFailed(Throwable throwable) {
                    if (handler != null)
                        handler.onRequestFailed(throwable);
                }// onRequestFailed method
            });
        else {
            clearSession();
            initSession(mobile, handler);
        }// else
    }// initSession method


    @Override
    public void initDHExchange(final String code, final ConnectionHandler handler) {
        DefaultExecutorSupplier.getInstance()
                .forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.HIGH) {
                    @Override
                    public void run() {
                        try {
                            SessionManager.getInstance().setMsgKey(SHA2.getInstance().hash("1"));
                            Result result;
                            writer = new BinaryWriter();
                            writer.writeInt32(WebGateImpl.Methods.INIT_DH);
                            writer.writeString(sessionManager.getHash());
                            writer.writeString(code);
                            writer.writeString(sessionManager.getMobile());
                            writer.writeString(DH.getInstance().generate());
                            byte[] rawData = writer.toByteArray();
                            byte[] encryptedData = RSA.getInstance().encryptRSA(rawData);
                            writer = new BinaryWriter();
                            writer.writeByteArray(encryptedData);
                            result = connect(config.URL);
                            if (result.getCode() == WebGate.RESULT_OK) {
                                reader = new BinaryReader(result.getStream().toByteArray());
                                DH dh = DH.getInstance();
                                dh.setServerResponse(reader.readString());
                                int size = result.getStream().size();
                                int position = reader.getPosition();
                                byte[] encryptedAES = reader.readByteArray(size - position);
                                dh.handleResponse();
                                AES aes = new AES(sessionManager.getSharedKey());
                                byte[] decryptedAES = aes.decrypt(encryptedAES);
                                reader = new BinaryReader(decryptedAES);
                                sessionManager.setSessionId(reader.readInt64());
                                sessionManager.setAuthKeyId(reader.readInt64());
                                sessionManager.setSalt(reader.readInt64());
                                byte b = reader.readByte();
                                sessionManager.setUserRegistered(b == 1);
                                config.url = reader.readString();
                                if (handler != null) {
                                    Result finalResult = new Result();
                                    finalResult.setCode(200);
                                    finalResult.setMessage("عملیات با موفقیت انجام شد");
                                    handler.onResultReceived(result);
                                }// if
                            } else {
                                if (handler != null)
                                    handler.onRequestFailed(ExceptionFactory.getVerificationException(result.getCode(), result.getStream().toString()));
                            }// else
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (handler != null) {
                                handler.onRequestFailed(new RequestFailedException());
                            }// if
                        }// catch
                    }// run method
                });
    }// initDHExchange method


    @Override
    public void sendRequest(final BaseRequest request) {
        DefaultExecutorSupplier.getInstance()
                .forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.LOW) {
                    @Override
                    public void run() {
                        sendPacket(request.getBody(), request.getHandler(), retryCount);
                    }// run method
                });
    }// sendRequest method


    private void sendPacket(final byte[] rawBytes, final ConnectionHandler handler, int retry) {
        long time = System.currentTimeMillis();
        int length = rawBytes.length;
        long seqNo = SEQ.getSequenceNumber();
        sessionManager.setSalt();
        writer = new BinaryWriter();
        try {
            writer.writeInt64(sessionManager.getSalt());
            writer.writeInt64(sessionManager.getSessionId());
            writer.writeInt64(time);
            writer.writeInt32(length);
            writer.writeInt64(seqNo);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        if (sessionManager.getSessionId() == 0) {
            sessionManager.setAuthKeyId(2);
            BinaryWriter wr = new BinaryWriter();
            try {
                wr.writeInt64(2);
            } catch (IOException e) {
                e.printStackTrace();
            }// catch
            sessionManager.setSharedKey(wr.toByteArray());
        }// if
        sessionManager.setMsgKey(SHA2.getInstance().hash(writer.toByteArray()));
        writer = new BinaryWriter();
        try {
            writer.writeByteArray(sessionManager.getSharedKey());
            writer.writeByteArray(sessionManager.getMsgKey());
        } catch (IOException e) {
            e.printStackTrace();
        }// catch

        byte[] aesKey = SHA2.getInstance().hash256(writer.toByteArray());
        writer = new BinaryWriter();
        try {
            writer.writeInt64(sessionManager.getSalt());
            writer.writeInt64(sessionManager.getSessionId());
            writer.writeInt64(time);
            writer.writeInt32(length);
            writer.writeInt64(seqNo);
            writer.writeByteArray(rawBytes);
            AES aes = new AES(aesKey);
            byte[] encryptedData = aes.encrypt(writer.toByteArray());
            writer = new BinaryWriter();
            writer.writeByteArray(encryptedData);
            Result result = connect(config.url);
            if (handler != null) {
                if (result.getCode() == WebGate.RESULT_OK) {
                    result.setMessage(handleResponse(result.getStream()));
                    handler.onResultReceived(result);
                } else if (result.getCode() == WebGate.USER_BLOCKED_OR_AUTH_KEY_ID_INVALID_ERROR_CODE) {
                    clearSession();
                    handler.onRequestFailed(new SessionIdInvalidException());
                } else {
                    handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
                }// else
            }// if
            saveSession();
        } catch (IOException e) {
            e.printStackTrace();
            if (retry > 0){
                retry --;
                sendPacket(rawBytes, handler, retry);
            }
//            clearSession();
//            writer = new BinaryWriter();
//            getLink(new ConnectionHandler() {
//                @Override
//                public void onResultReceived(Result result) {
//                    saveSession();
//                    sendPacket(rawBytes, handler);
//                }// onResultReceived method
//
//                @Override
//                public void onRequestFailed(Throwable throwable) {
//                    if (handler != null) {
//                        handler.onRequestFailed(throwable);
//                    }// if
//                }// onRequestFailed method
//            });
        } catch (Exception e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRequestFailed(new RequestFailedException());
            }// if
        }// catch
    }// sendPacket method


    @Override
    public void getPolicy(final byte[] rawBytes, final ConnectionHandler handler) {
        final long tempSessionID = sessionManager.getSessionId();
        final long tempAuthKekID = sessionManager.getAuthKeyId();
        final byte[] tempSharedKey = sessionManager.getSharedKey();
        sessionManager.setSessionId(0);
        DefaultExecutorSupplier.getInstance()
                .forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.HIGH) {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis();
                        int length = rawBytes.length;
                        long seqNo = SEQ.getSequenceNumber();
                        sessionManager.setSalt();
                        writer = new BinaryWriter();
                        try {
                            writer.writeInt64(sessionManager.getSalt());
                            writer.writeInt64(sessionManager.getSessionId());
                            writer.writeInt64(time);
                            writer.writeInt32(length);
                            writer.writeInt64(seqNo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (sessionManager.getSessionId() == 0) {
                            sessionManager.setAuthKeyId(2);
                            BinaryWriter wr = new BinaryWriter();
                            try {
                                wr.writeInt64(2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sessionManager.setSharedKey(wr.toByteArray());
                        }
                        sessionManager.setMsgKey(SHA2.getInstance().hash(writer.toByteArray()));
                        writer = new BinaryWriter();
                        try {
                            writer.writeByteArray(sessionManager.getSharedKey());
                            writer.writeByteArray(sessionManager.getMsgKey());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        byte[] aesKey = SHA2.getInstance().hash256(writer.toByteArray());
                        writer = new BinaryWriter();
                        try {
                            writer.writeInt64(sessionManager.getSalt());
                            writer.writeInt64(sessionManager.getSessionId());
                            writer.writeInt64(time);
                            writer.writeInt32(length);
                            writer.writeInt64(seqNo);
                            writer.writeByteArray(rawBytes);
                            AES aes = new AES(aesKey);
                            byte[] encryptedData = aes.encrypt(writer.toByteArray());
                            writer = new BinaryWriter();
                            writer.writeByteArray(encryptedData);
                            Result result = connect(config.url);
                            if (handler != null) {
                                if (result.getCode() == WebGate.RESULT_OK) {
                                    handler.onResultReceived(result);
                                } else {
                                    handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
                                }// else
                            }// if
                            saveSession();
                        } catch (IOException e) {
                            writer = new BinaryWriter();
                            getLink(new ConnectionHandler() {
                                @Override
                                public void onResultReceived(Result result) {
                                    saveSession();
                                    sendPacket(rawBytes, handler, retryCount);
                                }// onResultReceived method

                                @Override
                                public void onRequestFailed(Throwable throwable) {
                                    if (handler != null) {
                                        handler.onRequestFailed(throwable);
                                    }// if
                                }// onRequestFailed method
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (handler != null)
                                handler.onRequestFailed(new RequestFailedException());
                        } finally {
                            sessionManager.setSessionId(tempSessionID);
                            sessionManager.setAuthKeyId(tempAuthKekID);
                            sessionManager.setSharedKey(tempSharedKey);
                            saveSession();
                        }// finally
                    }// run method
                });
    }// getPolicy method


    @Override
    public void getLink(ConnectionHandler handler) {
        byte[] msgKey = sessionManager.getMsgKey();
        sessionManager.setMsgKey(SHA2.getInstance().hash("0"));
        long authkeyid = sessionManager.getAuthKeyId();
        sessionManager.setAuthKeyId(2);
        writer = new BinaryWriter();
        try {
            writer.writeInt32(Methods.GET_URL_METHOD);
            Result result = connect(config.URL);
            BinaryReader binaryReader = new BinaryReader(result.getStream().toByteArray());
            config.url = binaryReader.readString();
            if (handler != null) {
                if (result.getCode() == WebGate.RESULT_OK)
                    handler.onResultReceived(result);
                else
                    handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
            }// if
        } catch (IOException e) {
            e.printStackTrace();
            if (handler != null) {
                handler.onRequestFailed(new RequestFailedException());
            }// if
        } finally {
            sessionManager.setMsgKey(msgKey);
            sessionManager.setAuthKeyId(authkeyid);
        }// finally
    }// getLink method


    @Override
    public void signIn(final String password, final ConnectionHandler handler) {
        DefaultExecutorSupplier.getInstance()
                .forBackgroundTasks()
                .submit(new PriorityRunnable(PriorityRunnable.Priority.HIGH) {
                    @Override
                    public void run() {
                        writer = new BinaryWriter();
                        try {
                            writer.writeInt32(Methods.SIGN_IN);
                            writer.writeString(password);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] rawBytes = writer.toByteArray();
                        long time = System.currentTimeMillis();
                        int length = rawBytes.length;
                        long seqNo = SEQ.getSequenceNumber();
                        sessionManager.setSalt();
                        writer = new BinaryWriter();
                        try {
                            writer.writeInt64(sessionManager.getSalt());
                            writer.writeInt64(sessionManager.getSessionId());
                            writer.writeInt64(time);
                            writer.writeInt32(length);
                            writer.writeInt64(seqNo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (sessionManager.getSessionId() == 0) {
                            sessionManager.setAuthKeyId(2);
                            BinaryWriter wr = new BinaryWriter();
                            try {
                                wr.writeInt64(2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sessionManager.setSharedKey(wr.toByteArray());
                        }
                        sessionManager.setMsgKey(SHA2.getInstance().hash(writer.toByteArray()));
                        writer = new BinaryWriter();
                        try {
                            writer.writeByteArray(sessionManager.getSharedKey());
                            writer.writeByteArray(sessionManager.getMsgKey());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        byte[] aesKey = SHA2.getInstance().hash256(writer.toByteArray());

                        writer = new BinaryWriter();
                        try {
                            writer.writeInt64(sessionManager.getSalt());
                            writer.writeInt64(sessionManager.getSessionId());
                            writer.writeInt64(time);
                            writer.writeInt32(length);
                            writer.writeInt64(seqNo);
                            writer.writeByteArray(rawBytes);
                            AES aes = new AES(aesKey);
                            byte[] encryptedData = aes.encrypt(writer.toByteArray());
                            writer = new BinaryWriter();
                            writer.writeByteArray(encryptedData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Result result;
                        try {
                            result = connect(config.URL);
                            if (result.getCode() == WebGate.RESULT_OK) {
                                String json = handleResponse(result.getStream());
                                saveSession();
                                if (handler != null) {
                                    result.setMessage(json);
                                    handler.onResultReceived(result);
                                }// if
                            } else if (result.getCode() == WebGate.USER_BLOCKED_OR_AUTH_KEY_ID_INVALID_ERROR_CODE) {
                                if (handler != null) {
                                    if (result.getStream().toString().contains("USER_BLOCKED"))
                                        handler.onRequestFailed(new UserBlockedException());
                                    else
                                        handler.onRequestFailed(new SessionIdInvalidException());
                                }// if
                            } else {
                                if (handler != null)
                                    handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
                            }// else
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            if (handler != null)
                                handler.onRequestFailed(new RequestFailedException());
                        }// catch
                    }// run method
                });
    }// signIn method


    @Override
    public void resetPassword(ConnectionHandler handler) {
        writer = new BinaryWriter();
        try {
            writer.writeInt32(Methods.RESET_PASSWORD);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        byte[] rawBytes = writer.toByteArray();
        long time = System.currentTimeMillis();
        int length = rawBytes.length;
        long seqNo = SEQ.getSequenceNumber();
        sessionManager.setSalt();
        writer = new BinaryWriter();
        try {
            writer.writeInt64(sessionManager.getSalt());
            writer.writeInt64(sessionManager.getSessionId());
            writer.writeInt64(time);
            writer.writeInt32(length);
            writer.writeInt64(seqNo);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        if (sessionManager.getSessionId() == 0) {
            sessionManager.setAuthKeyId(2);
            BinaryWriter wr = new BinaryWriter();
            try {
                wr.writeInt64(2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sessionManager.setSharedKey(wr.toByteArray());
        }
        sessionManager.setMsgKey(SHA2.getInstance().hash(writer.toByteArray()));
        writer = new BinaryWriter();
        try {
            writer.writeByteArray(sessionManager.getSharedKey());
            writer.writeByteArray(sessionManager.getMsgKey());
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        byte[] aesKey = SHA2.getInstance().hash256(writer.toByteArray());

        writer = new BinaryWriter();
        try {
            writer.writeInt64(sessionManager.getSalt());
            writer.writeInt64(sessionManager.getSessionId());
            writer.writeInt64(time);
            writer.writeInt32(length);
            writer.writeInt64(seqNo);
            writer.writeByteArray(rawBytes);
            AES aes = new AES(aesKey);
            byte[] encryptedData = aes.encrypt(writer.toByteArray());
            writer = new BinaryWriter();
            writer.writeByteArray(encryptedData);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        saveSession();
        Result result;
        try {
            result = connect(config.URL);
            if (result.getCode() == WebGate.RESULT_OK) {
                String json = handleResponse(result.getStream());
//                saveSession();
                if (handler != null) {
                    result.setMessage(json);
                    handler.onResultReceived(result);
                }// if
            } else if (result.getCode() == WebGate.USER_BLOCKED_OR_AUTH_KEY_ID_INVALID_ERROR_CODE) {
                if (result.getStream().toString().contains("TOO_MANY")) {
                    if (handler != null)
                        handler.onRequestFailed(new MethodBlockedException());
                }// if
            } else {
                if (handler != null)
                    handler.onRequestFailed(ExceptionFactory.getException(result.getCode(), result.getStream().toString()));
            }// else
        } catch (IOException e1) {
            e1.printStackTrace();
            if (handler != null)
                handler.onRequestFailed(new RequestFailedException());
        }// catch
    }// resetPassword method


    private Result connect(String url) throws IOException {

        java.net.URL requestUrl;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        DataOutputStream outputStream = null;
        Result result = new Result();

        try {
            requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(20000);
            outputStream = new DataOutputStream(connection.getOutputStream());
            writer.writeByteArrayBefore(sessionManager.getMsgKey());
            writer.writeInt64Before(sessionManager.getAuthKeyId());
            if (writer != null)
                outputStream.write(writer.toByteArray());
            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            result.setCode(responseCode);
            result.setMessage(responseMessage);
            int statusCode = connection.getResponseCode();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            if (statusCode != 200 /* or statusCode >= 200 && statusCode < 300 */) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = connection.getInputStream();
            }// else

            byte[] data = new byte[1024];
            int length;
            while ((length = inputStream.read(data)) != -1) {
                out.write(data, 0, length);
            }// while
            result.setStream(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }// catch
            }// if

            if (outputStream != null) {
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }// catch
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }// catch
            }// if

            if (connection != null) {
                connection.disconnect();
            }// if
        }// finally
        return result;
    }// connect method


    private String handleResponse(ByteArrayOutputStream stream) {
        writer = new BinaryWriter();
        try {
            writer.writeByteArray(sessionManager.getSharedKey());
            writer.writeInt64(sessionManager.getSessionId());
        } catch (IOException e) {
            e.printStackTrace();
        }// catch

        byte[] aesKey = SHA2.getInstance().hash256(writer.toByteArray());
        com.blackshadowsgroup.mbproto.encryption.encrypt.aes.AES aes = new com.blackshadowsgroup.mbproto.encryption.encrypt.aes.AES(aesKey);
        byte[] decryptedMessage = aes.decrypt(stream.toByteArray());
        reader = new BinaryReader(decryptedMessage);
        String json = null;
        try {
            reader.readInt64();
            sessionManager.setSessionId(reader.readInt64());
            reader.readInt64();
            reader.readInt64();
            String newUrl = reader.readString();
            if (newUrl != null && !newUrl.equals("")) {
                config.url = newUrl;
            }// if
            json = reader.readString();
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        return json != null ? json : "";
    }// handleResponse method


    private void saveSession() {
        preferences.putLong("zpu", sessionManager.getSessionId());
        preferences.putString("zps", Base64.encodeToString(sessionManager.getSharedKey(), Base64.DEFAULT));
        preferences.putLong("asd", sessionManager.getAuthKeyId());
        preferences.putString("usa", config.url);
        preferences.commit();
    }// saveSession method


    @Override
    public boolean clearSession() {
        sessionManager.setSessionId(0);
        sessionManager.setAuthKeyId(2);
        BinaryWriter wri = new BinaryWriter();
        try {
            wri.writeInt64(2);
        } catch (IOException e) {
            e.printStackTrace();
        }// catch
        sessionManager.setSharedKey(wri.toByteArray());
        preferences.putLong("zpu", sessionManager.getSessionId());
        preferences.putString("zps", Base64.encodeToString(sessionManager.getSharedKey(), Base64.DEFAULT));
        preferences.putLong("asd", sessionManager.getAuthKeyId());
        preferences.putString("usa", config.url);
        preferences.commit();
        return true;
    }// clearSession method


    void retrieveSession() {
        if (preferences.containsKey("zpu")) {
            sessionManager.setSessionId(preferences.getLong("zpu", 0));
            sessionManager.setSharedKey(Base64.decode(preferences.getString("zps", ""), Base64.DEFAULT));
            sessionManager.setAuthKeyId(preferences.getLong("asd", 2));
            config.url = preferences.getString("usa", "");
        }// if
    }// retrieveSession method


    /**
     * Methods interface
     */
    private interface Methods {
        int SEND_SMS = 0xc8e8f8;
        int INIT_DH = 0x5f8a99;
        int REGISTER = 0x794bfc;
        int GET_URL_METHOD = 0x944cfb;
        int SIGN_OUT = 0x2da9ea;
        int CHECK_VERSION = 0xf47743;
        int SIGN_IN = 0xb36fd2;
        int RESET_PASSWORD = 0xec6aca;
    }// Methods interface


    /**
     * Config class
     */
    static class Config {

        private static Config instance;

        private String url = "";
        private String URL = "";

        private Config() {
        }// constructor

        public static Config begin() {
            if (instance == null)
                instance = new Config();
            return instance;
        }// begin method

        public Config setUrl(String url) {
            this.URL = url;
            this.url = url;
            return this;
        }// setUrl method

        public Config initiateWith(Context context) {
            WebGateImpl.instance = new WebGateImpl(context);
            return this;
        }// initiateWith method

        public WebGateImpl createGate() {
            WebGateImpl.config = this;
            return WebGateImpl.getInstance();
        }// createGate method

    }// Config class

}// WebGAteImpl class
