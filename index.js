import { NativeModules, DeviceEventEmitter, Platform } from 'react-native';

const { RNImmersive } = NativeModules;

const unSupportedError = __DEV__
  ? () => {
    throw new Error('[react-native-immersive] should not be called on iOS');
  }
  : () => {};

let isListenerEnabled = false;

const Immersive = Platform.OS === 'android'
  ? {
    on: () => RNImmersive.setFullImmersiveMode(true),
    off: () => RNImmersive.setFullImmersiveMode(false),
    setFullImmersiveMode: isOn => RNImmersive.setFullImmersiveMode(isOn),
    setPartialImmersiveMode: isOn => RNImmersive.setPartialImmersiveMode(isOn),
    getFullImmersiveState: () => RNImmersive.getFullImmersiveState(), // do not always match actual display state
    getPartialImmersiveState: () => RNImmersive.getPartialImmersiveState(), // do not always match actual display state
    addFullImmersiveListener: (listener) => {
      DeviceEventEmitter.addListener('@@IMMERSIVE_STATE_CHANGED', listener);
      if (isListenerEnabled) return;
      isListenerEnabled = true;
      RNImmersive.addFullImmersiveListener();
    },
    addPartialImmersiveListener: (listener) => {
      DeviceEventEmitter.addListener('@@IMMERSIVE_STATE_CHANGED', listener);
      if (isListenerEnabled) return;
      isListenerEnabled = true;
      RNImmersive.addPartialImmersiveListener();
    },
    removeImmersiveListener: listener => DeviceEventEmitter.removeListener('@@IMMERSIVE_STATE_CHANGED', listener),
  }
  : {
    on: unSupportedError,
    off: unSupportedError,
    setFullImmersiveMode: unSupportedError,
    setPartialImmersiveMode: unSupportedError,
    getFullImmersiveState: unSupportedError,
    getPartialImmersiveState: unSupportedError,
    addFullImmersiveListener: unSupportedError,
    addPartialImmersiveListener: unSupportedError,
    removeImmersiveListener: unSupportedError,
  };

export { Immersive };
export default Immersive;
