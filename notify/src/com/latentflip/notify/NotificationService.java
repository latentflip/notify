package com.latentflip.notify;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.json.JSONObject;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;


public class NotificationService extends AccessibilityService {
  private static final int DISCOVERY_PORT = 2562;

  private static void printException(Exception es) {
    System.out.println("Fail message");
    System.out.println("#############################################");
    System.out.println("Exception message : "+es.getMessage() );
    System.out.println("#############################################");
    System.out.println("Exception message with class name : "+es.toString() );
    System.out.println("#############################################");
    es.printStackTrace();
  }

  public static String encodeIcon(Drawable icon){
    String appIcon64 = new String();
    Drawable ic = icon;

    if(ic !=null){
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 

      BitmapDrawable bitDw = ((BitmapDrawable) ic);
      Bitmap bitmap = bitDw.getBitmap();
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
      byte[] bitmapByte = stream.toByteArray();

      appIcon64 = Base64.encodeToString(bitmapByte,Base64.NO_WRAP);//;|Base64.URL_SAFE);
    }
    return appIcon64;
  }


  public void sendMessageEncrypted(String msg) {

      //String encryptedMsg = AESUtil.encrypt(msg);

      HashMap<String, String> encrypted = AESUtil.encryptWithKey(settings().getString("passcode", ""), msg);
      try {
        JSONObject json = new JSONObject();
        json.put("iv", encrypted.get("iv"));
        json.put("encrypted", encrypted.get("encrypted"));
        sendMessage(json.toString());
      } catch (Exception e) {
          printException(e);
      }
  }

  public void sendMessage(String msg) {
    SendMessageTask smt = new SendMessageTask(getApplicationContext());
    smt.execute(msg);
  }

  private void setEventDetails(AccessibilityEvent event, JSONObject json) {
    PackageManager pm = getApplicationContext().getPackageManager();
    
    String name = (String)event.getPackageName();
    Drawable icon;
    try {
      ApplicationInfo info = pm.getApplicationInfo(name, 0);
      name = (String)pm.getApplicationLabel(info);
      icon = (Drawable)pm.getApplicationIcon(info);
      String icon64 = encodeIcon(icon);
    
      json.put("app", name);
      json.put("icon", icon64);
    } catch (Exception e) {
    }
  }

  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
      if (event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {

          try {
              android.app.Notification notification;
              notification = (android.app.Notification) event.getParcelableData();

              if (notification != null) {

                RemoteViews views = notification.contentView;
                Class secretClass = views.getClass();

                ArrayList message = new ArrayList();
                JSONObject messageJSON = new JSONObject();

                setEventDetails(event, messageJSON);
                //message.add(getEventCreator(event));
                System.out.println(messageJSON);

                Map<Integer, String> text = new HashMap<Integer, String>();

                String from = null;
                String content = null;
                //outerFields is the declared fields of RemoteViews?
                Field outerFields[] = secretClass.getDeclaredFields();
                for (int i = 0; i < outerFields.length; i++) {
                    System.out.println("OF: " + outerFields[i].getName());
                    if (!outerFields[i].getName().equals("mActions")) continue;

                    outerFields[i].setAccessible(true);
                    
                    ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
                            .get(views);

                    for (Object action : actions) {
                        Field innerFields[] = action.getClass().getDeclaredFields();

                        Object value = null;
                        Integer type = null;
                        Integer viewId = null;
                        for (Field field : innerFields) {
                            field.setAccessible(true);
                            if (field.getName().equals("value")) {
                              value = field.get(action);
                            } else if (field.getName().equals("type")) {
                              type = field.getInt(action);
                            }
                        }

                        if (type != null && value != null && type == 10) {
                          if (from == null) {
                            from = value.toString();
                          } else if (content == null) {
                            content = value.toString();
                          }
                        }
                    }

                    messageJSON.put("from", from);
                    messageJSON.put("content", content);
                    sendMessageEncrypted( messageJSON.toString() );
                }
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
          
      }
  }
  @Override
  protected void onServiceConnected() {
      System.out.println("onServiceConnected");
      sendConnectMessage();
      AccessibilityServiceInfo info = new AccessibilityServiceInfo();
      info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
      info.notificationTimeout = 100;
      info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
      setServiceInfo(info);
  }
  private SharedPreferences settings() {
    return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
  }

  protected void sendConnectMessage() {
      JSONObject messageJSON = new JSONObject();

      try {
          messageJSON.put("app", "Notify");
          messageJSON.put("from", "Notify Service");
          messageJSON.put("content", "Notify is connected with code " + settings().getString("passcode", ""));
          sendMessageEncrypted(messageJSON.toString());
      } catch (Exception e) {
      }
  }

  @Override
  public void onInterrupt() {
      System.out.println("onInterrupt");
  }
}
