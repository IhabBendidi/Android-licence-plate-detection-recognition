package org.tensorflow.demo.view.pzv;

import android.content.res.TypedArray;
import android.view.View;

public interface IPullToZoom<T extends View> {
    /**
     * Get the Wrapped Zoom View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    View getZoomView();

    View getHeaderView();

    /**
     * Get the Wrapped root View.
     *
     * @return The View which is currently wrapped
     */
    T getPullRootView();

    /**
     * Whether Pull-to-Refresh is enabled
     *
     * @return enabled
     */
    boolean isPullToZoomEnabled();

    /**
     * Returns whether the Widget is currently in the Zooming state
     *
     * @return true if the Widget is currently zooming
     */
    boolean isZooming();

    /**
     * Returns whether the Widget is currently in the Zooming anim type
     *
     * @return true if the anim is parallax
     */
    boolean isParallax();

    boolean isHideHeader();

    void handleStyledAttributes(TypedArray a);
}
