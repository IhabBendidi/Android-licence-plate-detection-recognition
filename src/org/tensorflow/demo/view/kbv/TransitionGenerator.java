package org.tensorflow.demo.view.kbv;

import android.graphics.RectF;

public interface TransitionGenerator {
    Transition generateNextTransition(RectF drawableBounds, RectF viewport);
}
