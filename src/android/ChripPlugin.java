package com.cordova.plugin;


import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import io.chirp.connect.ChirpConnect;
import io.chirp.connect.interfaces.ConnectEventListener;
import io.chirp.connect.models.ChirpError;

public class ChripPlugin extends CordovaPlugin implements ConnectEventListener {
    private static final String STOP_CHIRP = "stop";
    private static final String SEND_DATA = "sendData";
    private static final String REGISTER_AS_RECEIVER = "registerAsReceiver";
    private static final String GET_DATA_TO_SEND = "getDataToSend";
    private static final String GET_DATA_RECEIVED = "getDataReceived";

    ChirpConnect chirp;
    CallbackContext context;


    String[] permissions = {Manifest.permission.RECORD_AUDIO};
    CordovaWebView cordovaWebView;
    String dataToSend;
    String dataReceived;
    int actionToDo;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        cordovaWebView = webView;
    }

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        // Verify that the user sent a 'show' action
        context = callbackContext;

        switch (action) {
            case STOP_CHIRP:
                stopChirp(callbackContext);
                break;
            case SEND_DATA:
                try {
                    dataToSend = args.getString(0);
		    //dataReceived = "dataReceived Send";
		    actionToDo = 1;	
                    checkPermission();
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                break;

            case REGISTER_AS_RECEIVER:
		try {
		    actionToDo = 2;
		    checkPermission();
		} catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
		//dataReceived = "dataReceived Register";	
                //registerAsReceiver();

                break;

	    case GET_DATA_TO_SEND:
                try {
                    callbackContext.success(dataToSend);
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                break;	
			
	     case GET_DATA_RECEIVED:
                try {
                    callbackContext.success(dataReceived);
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                break;			
        }


        return true;
    }

    private void stopChirp(CallbackContext callbackContext) {
        chirp.stop();
        try {
            chirp.close();
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

        callbackContext.success();
    }

    private void registerAsReceiver() {
		try {
			if (chirp == null) {
				String CHIRP_APP_KEY= cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier("CHIRP_APP_KEY" , "string", cordova.getActivity().getPackageName()));
				String CHIRP_APP_SECRET= cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "CHIRP_APP_SECRET", "string", cordova.getActivity().getPackageName()));
				String CHIRP_APP_CONFIG= cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "CHIRP_APP_CONFIG", "string", cordova.getActivity().getPackageName()));
				chirp = new ChirpConnect(cordova.getActivity(),CHIRP_APP_KEY,CHIRP_APP_SECRET );
				ChirpError errorConfig = chirp.setConfig(CHIRP_APP_CONFIG);
				
				if (errorConfig.getCode() == 0) {
					Toast.makeText(cordova.getActivity(), "Chirp configuration succeeded!", Toast.LENGTH_SHORT).show();
					/*Log.v("ChirpSDK: ", "Configured ChirpSDK");*/
				} else {
					JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Code", errorConfig.getCode());
                    jsonObject.put("Message", errorConfig.getMessage());
                    context.error(jsonObject);
					Toast.makeText(cordova.getActivity(), "ErrorConfig "+errorConfig.getMessage(), Toast.LENGTH_SHORT).show();
					return;
					/*Log.e("ChirpError: ", error.getMessage());*/
				}
				
				/*
				chirp = new ChirpConnect(cordova.getActivity(),cordova.getActivity().getResources().getString(R.string.CHIRP_APP_KEY), cordova.getActivity().getResources().getString(R.string.CHIRP_APP_SECRET) );


				chirp.setConfig(cordova.getActivity().getResources().getString(R.string.CHIRP_APP_CONFIG));
				*/
				
				ChirpError errorStart = chirp.start(true, true);
				
				if (errorStart.getCode() == 0) {
					Toast.makeText(cordova.getActivity(), "Chirp has been started!", Toast.LENGTH_SHORT).show();
					/*Log.v("ChirpSDK: ", "Configured ChirpSDK");*/
				} else {
					Toast.makeText(cordova.getActivity(), "ErrorStart "+errorStart.getMessage(), Toast.LENGTH_SHORT).show();
					return;
					/*Log.e("ChirpError: ", error.getMessage());*/
				}

				context.success();
			}
			else
			{
				Toast.makeText(cordova.getActivity(), "Chirp is already running and in receiving mode!", Toast.LENGTH_SHORT).show();
				
				context.success();
			}
			
			chirp.setListener(this);
		} catch (Exception ex) {
            context.error(ex.getMessage());
        }
    }

    private void sendData() {
        try {
            if (chirp == null) {
                String CHIRP_APP_KEY= cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier("CHIRP_APP_KEY" , "string", cordova.getActivity().getPackageName()));
                String CHIRP_APP_SECRET= cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "CHIRP_APP_SECRET", "string", cordova.getActivity().getPackageName()));
                String CHIRP_APP_CONFIG= cordova.getActivity().getString(cordova.getActivity().getResources().getIdentifier( "CHIRP_APP_CONFIG", "string", cordova.getActivity().getPackageName()));
                chirp = new ChirpConnect(cordova.getActivity(), CHIRP_APP_KEY, CHIRP_APP_SECRET);
                ChirpError errorConfig = chirp.setConfig(CHIRP_APP_CONFIG);
				
				if (errorConfig.getCode() != 0) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Code", errorConfig.getCode());
                    jsonObject.put("Message", errorConfig.getMessage());
                    context.error(jsonObject);
					Toast.makeText(cordova.getActivity(), "ErrorConfig "+errorConfig.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
				else
				{
					Toast.makeText(cordova.getActivity(), "Chirp configuration succeeded!", Toast.LENGTH_SHORT).show();
				}
                //Toast.makeText(cordova.getActivity(), CHIRP_APP_KEY, Toast.LENGTH_SHORT).show();
             /*   chirp = new ChirpConnect(cordova.getActivity(), cordova.getActivity().getResources().getString(R.string.CHIRP_APP_KEY), cordova.getActivity().getResources().getString(R.string.CHIRP_APP_SECRET));
                ChirpError errorConfig = chirp.setConfig(cordova.getActivity().getResources().getString(R.string.CHIRP_APP_CONFIG));
                */
				ChirpError errorStart = chirp.start(true, true);
				
				if (errorStart.getCode() == 0) {
					Toast.makeText(cordova.getActivity(), "Chirp has been started!", Toast.LENGTH_SHORT).show();
				/*Log.v("ChirpSDK: ", "Configured ChirpSDK");*/
				} else {
					Toast.makeText(cordova.getActivity(), "ErrorStart "+errorStart.getMessage(), Toast.LENGTH_SHORT).show();
					return;
					/*Log.e("ChirpError: ", error.getMessage());*/
				}
            }
			
			chirp.setListener(this);
            //Toast.makeText(cordova.getActivity(),dataToSend, Toast.LENGTH_SHORT).show();
            byte[] payload = dataToSend.getBytes(Charset.forName("UTF-8"));
            ChirpError errorSend = chirp.send(payload);
			
			if (errorSend.getCode() == 0) {
				Toast.makeText(cordova.getActivity(), "Sending "+dataToSend, Toast.LENGTH_SHORT).show();
			/*Log.v("ChirpSDK: ", "Configured ChirpSDK");*/
			} else {
				Toast.makeText(cordova.getActivity(), "ErrorSend "+errorSend.getMessage(), Toast.LENGTH_SHORT).show();
				return;
				/*Log.e("ChirpError: ", error.getMessage());*/
			}
			
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Code", errorSend.getCode());
            jsonObject.put("Message", errorSend.getMessage());

            context.success(jsonObject);
        } catch (Exception ex) {
            context.error(ex.getMessage());
        }

    }

    private void checkPermission() {

        if (hasPermisssion()) {
	    if (actionToDo == 1)
	    {
		sendData();
	    }
	    else
  	    if (actionToDo == 2)
	    {
	        registerAsReceiver(); 
	    }
        } else {
            PermissionHelper.requestPermissions(this, 0, permissions);
        }
    }
	
    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if (context != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    context.sendPluginResult(result);
                    return;
                }

            }
            if (actionToDo == 1)
	    {
		sendData();
	    }
	    else
  	    if (actionToDo == 2)
	    {
	        registerAsReceiver(); 
	    }
        }
    }

    public boolean hasPermisssion() {
        for (String p : permissions) {
            if (!PermissionHelper.hasPermission(this, p)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onReceived(@Nullable byte[] bytes, int i) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String data=new String(bytes);
		dataReceived = data;
                Toast.makeText(cordova.getActivity(),"onReceived "+data, Toast.LENGTH_SHORT).show();
                cordovaWebView.loadUrl("javascript:CallbackReceived('"+data+"','"+i+"');");
            }
        });
    }

    @Override
    public void onReceiving(int i) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(cordova.getActivity(),"onReceiving"+i, Toast.LENGTH_SHORT).show();
                cordovaWebView.loadUrl("javascript:CallbackReceiving('"+i+"','"+i+"');");
            }
        });
    }

    @Override
    public void onSending(@NotNull byte[] bytes, int i) {

        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                
                String data=new String(bytes);
                //Toast.makeText(cordova.getActivity(),"onSending"+data, Toast.LENGTH_SHORT).show();
                cordovaWebView.loadUrl("javascript:CallbackSending('"+data+"','"+i+"');");
            }
        });

    }

    @Override
    public void onSent(@NotNull byte[] bytes, int i) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String data=new String(bytes);
                Toast.makeText(cordova.getActivity(),"onSent "+data, Toast.LENGTH_SHORT).show();
                cordovaWebView.loadUrl("javascript:CallbackSent('"+data+"','"+i+"');");
            }
        });

    }

    @Override
    public void onStateChanged(int i, int i1) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(cordova.getActivity(),"onStateChanged "+i, Toast.LENGTH_SHORT).show();
                cordovaWebView.loadUrl("javascript:CallbackStateChanged('"+i+"','"+i1+"');");
            }
        });

    }

    @Override
    public void onSystemVolumeChanged(float v, float v1) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(cordova.getActivity(),"onSystemVolumeChanged "+v, Toast.LENGTH_SHORT).show();
                cordovaWebView.loadUrl("javascript:CallbackSystemVolumeChanged('"+v+"','"+v1+"');");
            }
        });

        
    }
}
