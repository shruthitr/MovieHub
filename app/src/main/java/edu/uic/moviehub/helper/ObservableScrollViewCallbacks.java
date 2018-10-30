
package edu.uic.moviehub.helper;



public interface ObservableScrollViewCallbacks {

    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging);

    /**
     * Called when the down motion event occurred.
     */
    public void onDownMotionEvent();

    /**
     * Called when the dragging ended or canceled.
     **/
    public void onUpOrCancelMotionEvent(ScrollState scrollState);
}