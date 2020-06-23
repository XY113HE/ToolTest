package com.freeme.tooltest;

 

import android.os.Handler;

import android.os.Message;

import android.os.SystemClock;

public abstract class CustomCountDownTimer {

 

    /**

     * Millis since epoch when alarm should stop.

     */

    private final long mMillisInFuture;

 

    /**

     * The interval in millis that the user receives callbacks

     */

    private final long mCountdownInterval;

 

    private long mMillisFinished = 0;

 

    private long mElapsedRealtime;

 

    /**

     * boolean representing if the timer was cancelled

     */

    private boolean mCancelled = false;

 

    /**

     * @param millisInFuture    The number of millis in the future from the call

     *                          to {@link #start()} until the countdown is done and {@link #onFinish(long millisUntilFinished)}

     *                          is called.

     * @param countDownInterval The interval along the way to receive

     *                          {@link #onTick(long)} callbacks.

     */

    public CustomCountDownTimer(long millisInFuture, long countDownInterval) {

        mMillisInFuture = millisInFuture;

        mCountdownInterval = countDownInterval;

    }

 

    /**

     * Cancel the countdown.

     */

    public synchronized final void cancel() {

        mCancelled = true;

        mHandler.removeMessages(MSG);

    }

 

    /**

     * Stop the countdown.

     */

    public synchronized final CustomCountDownTimer stop() {

        mHandler.removeMessages(MSG);

 

        long elapsedRealtime = mElapsedRealtime;

        mElapsedRealtime = SystemClock.elapsedRealtime();

        final long millis = mElapsedRealtime - elapsedRealtime;

        mMillisFinished += millis;

 

        onTick(mMillisFinished);

        return this;

    }

 

    /**

     * Start the countdown.

     */

    public synchronized final CustomCountDownTimer start() {

 

        mCancelled = false;

        if (mMillisInFuture <= mMillisFinished) {

            onFinish(mMillisFinished);

            return this;

        }

        mElapsedRealtime = SystemClock.elapsedRealtime();

        mHandler.sendMessage(mHandler.obtainMessage(MSG));

        return this;

    }

 

    /**

     * 获得当前的时间long.

     */

    public synchronized final long getNowTime() {

        return mMillisFinished + SystemClock.elapsedRealtime() - mElapsedRealtime;

    }

 

    /**

     * Callback fired on regular interval.

     *

     * @param millisUntilFinished The amount of time until finished.

     */

    public abstract void onTick(long millisUntilFinished);

 

    /**

     * Callback fired when the time is up.

     */

    public abstract void onFinish(long millisUntilFinished);

 

 

    private static final int MSG = 1;

 

 

    // handles counting down

    private Handler mHandler = new Handler() {

 

        @Override

        public void handleMessage(Message msg) {

 

            synchronized (CustomCountDownTimer.this) {

                if (mCancelled) {

                    return;

                }

                long elapsedRealtime = mElapsedRealtime;

                mElapsedRealtime = SystemClock.elapsedRealtime();

                final long millis = mElapsedRealtime - elapsedRealtime;

                mMillisFinished += millis;

 

                if (mMillisInFuture <= mMillisFinished) {

                    onFinish(mMillisFinished);

                } else {

                    onTick(mMillisFinished);

 

                    long delay = mMillisInFuture - mMillisFinished;

                    if (delay > mCountdownInterval) {

 

                        // take into account user's onTick taking time to execute

                        delay = mElapsedRealtime + mCountdownInterval - SystemClock.elapsedRealtime() - mMillisFinished % mCountdownInterval;

 

                    }

 

                    // special case: user's onTick took more than interval to

                    // complete, skip to next interval

                    while (delay < 0)

                        delay += mCountdownInterval;

 

                    sendMessageDelayed(obtainMessage(MSG), delay);

                }

            }

        }

    };

}
