
package org.tensorflow.demo;

import android.graphics.Bitmap;
import android.graphics.RectF;
import java.util.List;

/**
 * Generic interface for interacting with different recognition engines.
 */
public interface Classifier {
  List<Recognition> recognizeImage(Bitmap bitmap);

  void enableStatLogging(final boolean debug);

  String getStatString();

  void close();





  /** An immutable result returned by a Classifier describing what was recognized. */
  public class Recognition {
    /**
     * A unique identifier for what has been recognized. Specific to the class, not the instance of
     * the object.
     */
    private  RectF boxes;

    /** Display name for the recognition. */
    private final float score;

    /**
     * A sortable score for how good the recognition is relative to others. Higher should be better.
     */
    private final float category;

    /** Optional location within the source image for the location of the recognized object. */


    public Recognition(
            final RectF boxes, final float score, final float category) {
      this.boxes = boxes;
      this.score = score;
      this.category = category;
    }

    public RectF getBoxes() {
      return this.boxes;
    }

    public float getScore() {
      return this.score;
    }

    public float getCategory() {
      return this.category;
    }



    public void setBoxes(RectF box){this.boxes=box;}




  }
}
