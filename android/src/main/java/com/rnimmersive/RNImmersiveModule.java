package com.rnimmersive;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.graphics.Color;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;

import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * {@link NativeModule} that allows changing the appearance of the menu bar.
 */
public class RNImmersiveModule extends ReactContextBaseJavaModule {

  private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
  private static final String ERROR_NO_ACTIVITY_MESSAGE = "Tried to set immersive while not attached to an Activity";

  private static final int UI_FLAG_FULL_IMMERSIVE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

  // In Partial Immersive Mode NavigationBar is visible to user.
  // But user can made it transparent, translucent and render UI like in FULL
  // Immersive
  private static final int UI_FLAG_PARTIAL_IMMERSIVE = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

  private static RNImmersiveModule SINGLETON = null;

  private ReactContext _reactContext = null;
  private boolean _isImmersiveOn = false;

  public static RNImmersiveModule getInstance() {
    return SINGLETON;
  }

  public RNImmersiveModule(ReactApplicationContext reactContext) {
    super(reactContext);

    _reactContext = reactContext;
    SINGLETON = this;
  }

  @Override
  public void onCatalystInstanceDestroy() {
    _reactContext = null;
    SINGLETON = null;
  }

  @Override
  public String getName() {
    return "RNImmersive";
  }

  @ReactMethod
  public void setFullImmersiveMode(final boolean isOn, final Promise res) {
    _setFullImmersiveMode(isOn, res);
  }

  @ReactMethod
  public void setPartialImmersiveMode(final boolean isOn, final Promise res) {
    _setPartialImmersiveMode(isOn, res);
  }

  @ReactMethod
  public void getFullImmersiveState(final Promise res) {
    _getFullImmersiveState(res);
  }

  @ReactMethod
  public void getPartialImmersiveState(final Promise res) {
    _getPartialImmersiveState(res);
  }

  @ReactMethod
  public void addFullImmersiveListener() {
    _addFullImmersiveListener();
  }

  @ReactMethod
  public void addPartialImmersiveListener() {
    _addPartialImmersiveListener();
  }

  public void emitImmersiveStateChangeEvent() {
    if (_reactContext != null && _reactContext.hasActiveCatalystInstance()) {
      _reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("@@IMMERSIVE_STATE_CHANGED",
          null);
    }
  }

  private void _setFullImmersiveMode(final boolean isOn, final Promise res) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      res.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
          _isImmersiveOn = isOn;
          activity.getWindow().getDecorView()
              .setSystemUiVisibility(isOn ? UI_FLAG_FULL_IMMERSIVE : View.SYSTEM_UI_FLAG_VISIBLE);
          res.resolve(null);
        }
      });
    }
  }

  private void _setPartialImmersiveMode(final boolean isOn, final Promise res) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      res.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
          _isImmersiveOn = isOn;
          activity.getWindow().setNavigationBarColor(Color.argb(0.3f, 0.0f, 0.0f, 0.0f));
          activity.getWindow().getDecorView()
              .setSystemUiVisibility(isOn ? UI_FLAG_PARTIAL_IMMERSIVE : View.SYSTEM_UI_FLAG_VISIBLE);
          res.resolve(null);
        }
      });
    }
  }

  private void _getFullImmersiveState(final Promise res) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      res.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
          int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
          boolean isImmersiveOn = 0 != (visibility & UI_FLAG_FULL_IMMERSIVE);

          WritableMap map = Arguments.createMap();
          map.putBoolean("isImmersiveOn", isImmersiveOn);

          res.resolve(map);
        }
      });
    }
  }

  private void _getPartialImmersiveState(final Promise res) {
    final Activity activity = getCurrentActivity();
    if (activity == null) {
      res.reject(ERROR_NO_ACTIVITY, ERROR_NO_ACTIVITY_MESSAGE);
      return;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
          int visibility = activity.getWindow().getDecorView().getSystemUiVisibility();
          boolean isImmersiveOn = 0 != (visibility & UI_FLAG_PARTIAL_IMMERSIVE);
          WritableMap map = Arguments.createMap();
          map.putBoolean("isImmersiveOn", isImmersiveOn);

          res.resolve(map);
        }
      });
    }
  }

  private void _addFullImmersiveListener() {
    final Activity activity = getCurrentActivity();
    if (activity == null)
      return;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
          activity.getWindow().getDecorView()
              .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                  boolean isImmersiveOn = 0 != (visibility & UI_FLAG_FULL_IMMERSIVE);

                  if (isImmersiveOn != _isImmersiveOn) {
                    emitImmersiveStateChangeEvent();
                  }
                }
              });
        }
      });
    }
  }

  private void _addPartialImmersiveListener() {
    final Activity activity = getCurrentActivity();
    if (activity == null)
      return;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      UiThreadUtil.runOnUiThread(new Runnable() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void run() {
          activity.getWindow().getDecorView()
              .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                  boolean isImmersiveOn = 0 != (visibility & UI_FLAG_PARTIAL_IMMERSIVE);

                  if (isImmersiveOn != _isImmersiveOn) {
                    emitImmersiveStateChangeEvent();
                  }
                }
              });
        }
      });
    }
  }
}
