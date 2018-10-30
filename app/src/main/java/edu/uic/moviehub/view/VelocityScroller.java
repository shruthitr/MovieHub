

package edu.uic.moviehub.view;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;


public class VelocityScroller {
    private int mMode;

    private final SplineOverScroller mScrollerX;
    private final SplineOverScroller mScrollerY;

    private Interpolator mInterpolator;

    private final boolean mFlywheel;

    private static final int DEFAULT_DURATION = 250;
    private static final int SCROLL_MODE = 0;
    private static final int FLING_MODE = 1;

    private static float sViscousFluidScale;
    private static float sViscousFluidNormalize;

    private static float viscousFluid(float x) {
        x *= sViscousFluidScale;
        if (x < 1.0f) {
            x -= (1.0f - (float) Math.exp(-x));
        } else {
            float start = 0.36787944117f;   // 1/e == exp(-1)
            x = 1.0f - (float) Math.exp(1.0f - x);
            x = start + x * (1.0f - start);
        }
        x *= sViscousFluidNormalize;
        return x;
    }


    public VelocityScroller(Context context) {
        this(context, null);
    }


    public VelocityScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, true);
    }

    public VelocityScroller(Context context, Interpolator interpolator, boolean flywheel) {
        mInterpolator = interpolator;
        mFlywheel = flywheel;
        mScrollerX = new SplineOverScroller(context);
        mScrollerY = new SplineOverScroller(context);
    }


    public VelocityScroller(Context context, Interpolator interpolator,
                            float bounceCoefficientX, float bounceCoefficientY) {
        this(context, interpolator, true);
    }


    public VelocityScroller(Context context, Interpolator interpolator,
                            float bounceCoefficientX, float bounceCoefficientY, boolean flywheel) {
        this(context, interpolator, flywheel);
    }

    void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public final void setFriction(float friction) {
        mScrollerX.setFriction(friction);
        mScrollerY.setFriction(friction);
    }


    public final boolean isFinished() {
        return mScrollerX.mFinished && mScrollerY.mFinished;
    }


    public final void forceFinished(boolean finished) {
        mScrollerX.mFinished = mScrollerY.mFinished = finished;
    }


    public final int getCurrX() {
        return mScrollerX.mCurrentPosition;
    }


    public final int getCurrY() {
        return mScrollerY.mCurrentPosition;
    }


    public float getCurrVelocity() {
        float squaredNorm = mScrollerX.mCurrVelocity * mScrollerX.mCurrVelocity;
        squaredNorm += mScrollerY.mCurrVelocity * mScrollerY.mCurrVelocity;
        return (float) Math.sqrt(squaredNorm);
    }


    public final int getStartX() {
        return mScrollerX.mStart;
    }


    public final int getStartY() {
        return mScrollerY.mStart;
    }


    public final int getFinalX() {
        return mScrollerX.mFinal;
    }


    public final int getFinalY() {
        return mScrollerY.mFinal;
    }


    @Deprecated
    public final int getDuration() {
        return Math.max(mScrollerX.mDuration, mScrollerY.mDuration);
    }


    @Deprecated
    public void extendDuration(int extend) {
        mScrollerX.extendDuration(extend);
        mScrollerY.extendDuration(extend);
    }


    @Deprecated
    public void setFinalX(int newX) {
        mScrollerX.setFinalPosition(newX);
    }


    @Deprecated
    public void setFinalY(int newY) {
        mScrollerY.setFinalPosition(newY);
    }


    public boolean computeScrollOffset() {
        if (isFinished()) {
            return false;
        }

        switch (mMode) {
            case SCROLL_MODE:
                long time = AnimationUtils.currentAnimationTimeMillis();

                final long elapsedTime = time - mScrollerX.mStartTime;

                final int duration = mScrollerX.mDuration;
                if (elapsedTime < duration) {
                    float q = (float) (elapsedTime) / duration;

                    if (mInterpolator == null) {
                        q = viscousFluid(q);
                    } else {
                        q = mInterpolator.getInterpolation(q);
                    }

                    mScrollerX.updateScroll(q);
                    mScrollerY.updateScroll(q);
                } else {
                    abortAnimation();
                }
                break;

            case FLING_MODE:
                if (!mScrollerX.mFinished) {
                    if (!mScrollerX.update()) {
                        if (!mScrollerX.continueWhenFinished()) {
                            mScrollerX.finish();
                        }
                    }
                }

                if (!mScrollerY.mFinished) {
                    if (!mScrollerY.update()) {
                        if (!mScrollerY.continueWhenFinished()) {
                            mScrollerY.finish();
                        }
                    }
                }

                break;
        }

        return true;
    }


    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, DEFAULT_DURATION);
    }


    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        mMode = SCROLL_MODE;
        mScrollerX.startScroll(startX, dx, duration);
        mScrollerY.startScroll(startY, dy, duration);
    }



    public boolean springBack(int startX, int startY, int minX, int maxX, int minY, int maxY) {
        mMode = FLING_MODE;

        // Make sure both methods are called.
        final boolean spingbackX = mScrollerX.springback(startX, minX, maxX);
        final boolean spingbackY = mScrollerY.springback(startY, minY, maxY);
        return spingbackX || spingbackY;
    }

    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY) {
        fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
    }


    public void fling(int startX, int startY, int velocityX, int velocityY,
                      int minX, int maxX, int minY, int maxY, int overX, int overY) {
        // Continue a scroll or fling in progress
        if (mFlywheel && !isFinished()) {
            float oldVelocityX = mScrollerX.mCurrVelocity;
            float oldVelocityY = mScrollerY.mCurrVelocity;
            if (Math.signum(velocityX) == Math.signum(oldVelocityX) &&
                    Math.signum(velocityY) == Math.signum(oldVelocityY)) {
                velocityX += oldVelocityX;
                velocityY += oldVelocityY;
            }
        }

        mMode = FLING_MODE;
        mScrollerX.fling(startX, velocityX, minX, maxX, overX);
        mScrollerY.fling(startY, velocityY, minY, maxY, overY);
    }


    public void notifyHorizontalEdgeReached(int startX, int finalX, int overX) {
        mScrollerX.notifyEdgeReached(startX, finalX, overX);
    }


    public void notifyVerticalEdgeReached(int startY, int finalY, int overY) {
        mScrollerY.notifyEdgeReached(startY, finalY, overY);
    }

    public void notifyFinalXExtended(int finalX) {
        mScrollerX.notifyFinalPositionExtended(finalX);
    }

    public void notifyFinalYExtended(int finalY) {
        mScrollerY.notifyFinalPositionExtended(finalY);
    }


    public boolean isOverScrolled() {
        return ((!mScrollerX.mFinished &&
                mScrollerX.mState != SplineOverScroller.SPLINE) ||
                (!mScrollerY.mFinished &&
                        mScrollerY.mState != SplineOverScroller.SPLINE));
    }


    public void abortAnimation() {
        mScrollerX.finish();
        mScrollerY.finish();
    }


    public int timePassed() {
        final long time = AnimationUtils.currentAnimationTimeMillis();
        final long startTime = Math.min(mScrollerX.mStartTime, mScrollerY.mStartTime);
        return (int) (time - startTime);
    }

    public boolean isScrollingInDirection(float xvel, float yvel) {
        final int dx = mScrollerX.mFinal - mScrollerX.mStart;
        final int dy = mScrollerY.mFinal - mScrollerY.mStart;
        return !isFinished() && Math.signum(xvel) == Math.signum(dx) &&
                Math.signum(yvel) == Math.signum(dy);
    }

    static class SplineOverScroller {
        // Initial position
        private int mStart;

        // Current position
        private int mCurrentPosition;

        // Final position
        private int mFinal;

        // Initial velocity
        private int mVelocity;

        // Current velocity
        private float mCurrVelocity;

        // Constant current deceleration
        private float mDeceleration;

        // Animation starting time, in system milliseconds
        private long mStartTime;

        // Animation duration, in milliseconds
        private int mDuration;

        // Duration to complete spline component of animation
        private int mSplineDuration;

        // Distance to travel along spline animation
        private int mSplineDistance;

        // Whether the animation is currently in progress
        private boolean mFinished;

        // The allowed overshot distance before boundary is reached.
        private int mOver;

        // Fling friction
        private float mFlingFriction = ViewConfiguration.getScrollFriction();

        // Current state of the animation.
        private int mState = SPLINE;

        // Constant gravity value, used in the deceleration phase.
        private static final float GRAVITY = 2000.0f;

        // A context-specific coefficient adjusted to physical values.
        private float mPhysicalCoeff;

        private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
        private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
        private static final float START_TENSION = 0.5f;
        private static final float END_TENSION = 1.0f;
        private static final float P1 = START_TENSION * INFLEXION;
        private static final float P2 = 1.0f - END_TENSION * (1.0f - INFLEXION);

        private static final int NB_SAMPLES = 100;
        private static final float[] SPLINE_POSITION = new float[NB_SAMPLES + 1];
        private static final float[] SPLINE_TIME = new float[NB_SAMPLES + 1];

        private static final int SPLINE = 0;
        private static final int CUBIC = 1;
        private static final int BALLISTIC = 2;

        static {
            float x_min = 0.0f;
            float y_min = 0.0f;
            for (int i = 0; i < NB_SAMPLES; i++) {
                final float alpha = (float) i / NB_SAMPLES;

                float x_max = 1.0f;
                float x, tx, coef;
                while (true) {
                    x = x_min + (x_max - x_min) / 2.0f;
                    coef = 3.0f * x * (1.0f - x);
                    tx = coef * ((1.0f - x) * P1 + x * P2) + x * x * x;
                    if (Math.abs(tx - alpha) < 1E-5) break;
                    if (tx > alpha) x_max = x;
                    else x_min = x;
                }
                SPLINE_POSITION[i] = coef * ((1.0f - x) * START_TENSION + x) + x * x * x;

                float y_max = 1.0f;
                float y, dy;
                while (true) {
                    y = y_min + (y_max - y_min) / 2.0f;
                    coef = 3.0f * y * (1.0f - y);
                    dy = coef * ((1.0f - y) * START_TENSION + y) + y * y * y;
                    if (Math.abs(dy - alpha) < 1E-5) break;
                    if (dy > alpha) y_max = y;
                    else y_min = y;
                }
                SPLINE_TIME[i] = coef * ((1.0f - y) * P1 + y * P2) + y * y * y;
            }
            SPLINE_POSITION[NB_SAMPLES] = SPLINE_TIME[NB_SAMPLES] = 1.0f;

            // This controls the viscous fluid effect (how much of it)
            sViscousFluidScale = 8.0f;
            // must be set to 1.0 (used in viscousFluid())
            sViscousFluidNormalize = 1.0f;
            sViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
        }

        void setFriction(float friction) {
            mFlingFriction = friction;
        }

        SplineOverScroller(Context context) {
            mFinished = true;
            final float ppi = context.getResources().getDisplayMetrics().density * 160.0f;
            mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                    * 39.37f // inch/meter
                    * ppi
                    * 0.84f; // look and feel tuning
        }

        void updateScroll(float q) {
            mCurrentPosition = mStart + Math.round(q * (mFinal - mStart));
        }

        /*
         * Get a signed deceleration that will reduce the velocity.
         */
        static private float getDeceleration(int velocity) {
            return velocity > 0 ? -GRAVITY : GRAVITY;
        }


        private void adjustDuration(int start, int oldFinal, int newFinal) {
            final int oldDistance = oldFinal - start;
            final int newDistance = newFinal - start;
            final float x = Math.abs((float) newDistance / oldDistance);
            final int index = (int) (NB_SAMPLES * x);
            if (index < NB_SAMPLES) {
                final float x_inf = (float) index / NB_SAMPLES;
                final float x_sup = (float) (index + 1) / NB_SAMPLES;
                final float t_inf = SPLINE_TIME[index];
                final float t_sup = SPLINE_TIME[index + 1];
                final float timeCoef = t_inf + (x - x_inf) / (x_sup - x_inf) * (t_sup - t_inf);
                mDuration *= timeCoef;
            }
        }

        void startScroll(int start, int distance, int duration) {
            mFinished = false;

            mStart = start;
            mFinal = start + distance;

            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mDuration = duration;

            // Unused
            mDeceleration = 0.0f;
            mVelocity = 0;
        }

        void finish() {
            mCurrentPosition = mFinal;

            mFinished = true;
        }

        void setFinalPosition(int position) {
            mFinal = position;
            mFinished = false;
        }

        void extendDuration(int extend) {
            final long time = AnimationUtils.currentAnimationTimeMillis();
            final int elapsedTime = (int) (time - mStartTime);
            mDuration = elapsedTime + extend;
            mFinished = false;
        }

        boolean springback(int start, int min, int max) {
            mFinished = true;

            mStart = mFinal = start;
            mVelocity = 0;

            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mDuration = 0;

            if (start < min) {
                startSpringback(start, min, 0);
            } else if (start > max) {
                startSpringback(start, max, 0);
            }

            return !mFinished;
        }

        private void startSpringback(int start, int end, int velocity) {
            // mStartTime has been set
            mFinished = false;
            mState = CUBIC;
            mStart = start;
            mFinal = end;
            final int delta = start - end;
            mDeceleration = getDeceleration(delta);
            // TODO take velocity into account
            mVelocity = -delta; // only sign is used
            mOver = Math.abs(delta);
            mDuration = (int) (1000.0 * Math.sqrt(-2.0 * delta / mDeceleration));
        }

        void fling(int start, int velocity, int min, int max, int over) {
            mOver = over;
            mFinished = false;
            mCurrVelocity = mVelocity = velocity;
            mDuration = mSplineDuration = 0;
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mCurrentPosition = mStart = start;

            if (start > max || start < min) {
                startAfterEdge(start, min, max, velocity);
                return;
            }

            mState = SPLINE;
            double totalDistance = 0.0;

            if (velocity != 0) {
                mDuration = mSplineDuration = getSplineFlingDuration(velocity);
                totalDistance = getSplineFlingDistance(velocity);
            }

            mSplineDistance = (int) (totalDistance * Math.signum(velocity));
            mFinal = start + mSplineDistance;

            // Clamp to a valid final position
            if (mFinal < min) {
                adjustDuration(mStart, mFinal, min);
                mFinal = min;
            }

            if (mFinal > max) {
                adjustDuration(mStart, mFinal, max);
                mFinal = max;
            }
        }

        private double getSplineDeceleration(int velocity) {
            return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
        }

        private double getSplineFlingDistance(int velocity) {
            final double l = getSplineDeceleration(velocity);
            final double decelMinusOne = DECELERATION_RATE - 1.0;
            return mFlingFriction * mPhysicalCoeff * Math.exp(DECELERATION_RATE / decelMinusOne * l);
        }

        /* Returns the duration, expressed in milliseconds */
        private int getSplineFlingDuration(int velocity) {
            final double l = getSplineDeceleration(velocity);
            final double decelMinusOne = DECELERATION_RATE - 1.0;
            return (int) (1000.0 * Math.exp(l / decelMinusOne));
        }

        private void fitOnBounceCurve(int start, int end, int velocity) {
            // Simulate a bounce that started from edge
            final float durationToApex = -velocity / mDeceleration;
            final float distanceToApex = velocity * velocity / 2.0f / Math.abs(mDeceleration);
            final float distanceToEdge = Math.abs(end - start);
            final float totalDuration = (float) Math.sqrt(
                    2.0 * (distanceToApex + distanceToEdge) / Math.abs(mDeceleration));
            mStartTime -= (int) (1000.0f * (totalDuration - durationToApex));
            mStart = end;
            mVelocity = (int) (-mDeceleration * totalDuration);
        }

        private void startBounceAfterEdge(int start, int end, int velocity) {
            mDeceleration = getDeceleration(velocity == 0 ? start - end : velocity);
            fitOnBounceCurve(start, end, velocity);
            onEdgeReached();
        }

        private void startAfterEdge(int start, int min, int max, int velocity) {
            if (start > min && start < max) {
                Log.e("VelocityScroller", "startAfterEdge called from a valid position");
                mFinished = true;
                return;
            }
            final boolean positive = start > max;
            final int edge = positive ? max : min;
            final int overDistance = start - edge;
            boolean keepIncreasing = overDistance * velocity >= 0;
            if (keepIncreasing) {
                // Will result in a bounce or a to_boundary depending on velocity.
                startBounceAfterEdge(start, edge, velocity);
            } else {
                final double totalDistance = getSplineFlingDistance(velocity);
                if (totalDistance > Math.abs(overDistance)) {
                    fling(start, velocity, positive ? min : start, positive ? start : max, mOver);
                } else {
                    startSpringback(start, edge, velocity);
                }
            }
        }

        void notifyEdgeReached(int start, int end, int over) {
            // mState is used to detect successive notifications
            if (mState == SPLINE) {
                mOver = over;
                mStartTime = AnimationUtils.currentAnimationTimeMillis();

                startAfterEdge(start, end, end, (int) mCurrVelocity);
            }
        }


        public void notifyFinalPositionExtended(int position) {
            mOver = 0;
            mFinished = false;
            mDuration = mDuration - (int) (mStartTime - AnimationUtils.currentAnimationTimeMillis());

            if (mDuration < 50) {
                mDuration = 50;
            }

            mSplineDuration = mDuration;

            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            mStart = mCurrentPosition;
            mFinal = position;

            mState = SPLINE;

            mSplineDistance = mFinal - mStart;
        }

        private void onEdgeReached() {
            // mStart, mVelocity and mStartTime were adjusted to their values when edge was reached.
            float distance = mVelocity * mVelocity / (2.0f * Math.abs(mDeceleration));
            final float sign = Math.signum(mVelocity);

            if (distance > mOver) {
                // Default deceleration is not sufficient to slow us down before boundary
                mDeceleration = -sign * mVelocity * mVelocity / (2.0f * mOver);
                distance = mOver;
            }

            mOver = (int) distance;
            mState = BALLISTIC;
            mFinal = mStart + (int) (mVelocity > 0 ? distance : -distance);
            mDuration = -(int) (1000.0f * mVelocity / mDeceleration);
        }

        boolean continueWhenFinished() {
            switch (mState) {
                case SPLINE:
                    // Duration from start to null velocity
                    if (mDuration < mSplineDuration) {
                        // If the animation was clamped, we reached the edge
                        mStart = mFinal;
                        // TODO Better compute speed when edge was reached
                        mVelocity = (int) mCurrVelocity;
                        mDeceleration = getDeceleration(mVelocity);
                        mStartTime += mDuration;
                        onEdgeReached();
                    } else {

                        return false;
                    }
                    break;
                case BALLISTIC:
                    mStartTime += mDuration;
                    startSpringback(mFinal, mStart, 0);
                    break;
                case CUBIC:
                    return false;
            }

            update();
            return true;
        }


        boolean update() {
            final long time = AnimationUtils.currentAnimationTimeMillis();
            final long currentTime = time - mStartTime;

            if (currentTime > mDuration) {
                return false;
            }

            double distance = 0.0;
            switch (mState) {
                case SPLINE: {
                    final float t = (float) currentTime / mSplineDuration;
                    final int index = (int) (NB_SAMPLES * t);
                    float distanceCoef = 1.f;
                    float velocityCoef = 0.f;
                    if (index < NB_SAMPLES) {
                        final float t_inf = (float) index / NB_SAMPLES;
                        final float t_sup = (float) (index + 1) / NB_SAMPLES;
                        final float d_inf = SPLINE_POSITION[index];
                        final float d_sup = SPLINE_POSITION[index + 1];
                        velocityCoef = (d_sup - d_inf) / (t_sup - t_inf);
                        distanceCoef = d_inf + (t - t_inf) * velocityCoef;
                    }

                    distance = distanceCoef * mSplineDistance;
                    mCurrVelocity = velocityCoef * mSplineDistance / mSplineDuration * 1000.0f;
                    break;
                }

                case BALLISTIC: {
                    final float t = currentTime / 1000.0f;
                    mCurrVelocity = mVelocity + mDeceleration * t;
                    distance = mVelocity * t + mDeceleration * t * t / 2.0f;
                    break;
                }

                case CUBIC: {
                    final float t = (float) (currentTime) / mDuration;
                    final float t2 = t * t;
                    final float sign = Math.signum(mVelocity);
                    distance = sign * mOver * (3.0f * t2 - 2.0f * t * t2);
                    mCurrVelocity = sign * mOver * 6.0f * (-t + t2);
                    break;
                }
            }

            mCurrentPosition = mStart + (int) Math.round(distance);

            return true;
        }
    }
}