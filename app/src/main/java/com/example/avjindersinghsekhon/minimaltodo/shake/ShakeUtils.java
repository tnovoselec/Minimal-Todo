package com.example.avjindersinghsekhon.minimaltodo.shake;

import java.util.List;

/**
 * Handles the shake calculations. Most of this stuff could just be in ShakeSensorEventListener, but it seemed
 * cleaner here.
 */
class ShakeUtils {
  /** Constant for low filter smoothening */
  private static final float ALPHA = 0.15f;
  /** After we detect a shake, we ignore any events for a bit of time. We don't want two shakes too close together. */
  private static final int IGNORE_EVENTS_AFTER_SHAKE = 1000;
  private static final float POSITIVE_COUNTER_THRESHHOLD = 2.0f;
  private static final float NEGATIVE_COUNTER_THRESHHOLD = -2.0f;
  private static final int MINIMUM_EACH_DIRECTION = 2;
  private static final long KEEP_DATA_POINTS_FOR = 1500;


  private ShakeUtils() {
  }


  /**
   * @see http://en.wikipedia.org/wiki/Low-pass_filter#Algorithmic_implementation
   * @see http://developer.android.com/reference/android/hardware/SensorEvent.html#values
   */
  static float[] lowPass( float[] input, float[] output ) {
    if ( output == null ) return input;

    for ( int i=0; i<input.length; i++ ) {
      output[i] = output[i] + ALPHA * (input[i] - output[i]);
    }
    return output;
  }


  static boolean wasRecentlyShaken(long lastShake, long curTime) {
    return lastShake != 0 && (curTime - lastShake) < IGNORE_EVENTS_AFTER_SHAKE;
  }


  static boolean wasStateChange(ShakePoint last, ShakePoint current) {
    boolean lastNotZero = last.x != 0 && last.y != 0 && last.z != 0;
    boolean hasChanged = last.x != current.x || last.y != current.y || last.z != current.z;
    return lastNotZero && hasChanged;
  }


  static boolean pastThreshhold(ShakePoint delta) {
    return Math.abs(delta.x) > POSITIVE_COUNTER_THRESHHOLD
        || Math.abs(delta.y) > POSITIVE_COUNTER_THRESHHOLD
        || Math.abs(delta.z) > POSITIVE_COUNTER_THRESHHOLD;
  }


  /** Modifies shakePoints such that points stored before the window are removed. */
  static void removeOldShakePoints(List<ShakePoint> shakePoints) {
    long curTime = System.currentTimeMillis();
    long cutOffTime = curTime - KEEP_DATA_POINTS_FOR;
    while (shakePoints.size() > 0 && shakePoints.get(0).time < cutOffTime) {
      shakePoints.remove(0);
    }

  }


  static boolean isShakeDetected(List<ShakePoint> shakePoints) {
    // Does not include Z...May not matter.
    int xPos = 0;
    int xNeg = 0;
    int xDir = 0;
    int yPos = 0;
    int yNeg = 0;
    int yDir = 0;

    for (ShakePoint shake : shakePoints) {

      if (shake.x > POSITIVE_COUNTER_THRESHHOLD && xDir < 1) {
        ++xPos;
        xDir = 1;
      }
      if (shake.x < NEGATIVE_COUNTER_THRESHHOLD && xDir > -1) {
        ++xNeg;
        xDir = -1;
      }
      if (shake.y > POSITIVE_COUNTER_THRESHHOLD && yDir < 1) {
        ++yPos;
        yDir = 1;
      }
      if (shake.y < NEGATIVE_COUNTER_THRESHHOLD && yDir > -1) {
        ++yNeg;
        yDir = -1;
      }
    }

    return isShakeDetected(xPos, xNeg, yPos, yNeg);
  }

  private static boolean isShakeDetected(int xPos, int xNeg, int yPos, int yNeg) {
    boolean xMoved = xPos >= MINIMUM_EACH_DIRECTION && xNeg >= MINIMUM_EACH_DIRECTION;
    boolean yMoved = yPos >= MINIMUM_EACH_DIRECTION && yNeg >= MINIMUM_EACH_DIRECTION;
    return xMoved || yMoved;
  }


  public static class ShakePoint { // TODO: Hide once behind ShakeListener
    final float x;
    final float y;
    final float z;
    final long time; // Time in millis

    ShakePoint(float x, float y, float z) {
      this(x, y, z, -1);
    }

    ShakePoint(float x, float y, float z, long time) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.time = time;
    }
  }
}
