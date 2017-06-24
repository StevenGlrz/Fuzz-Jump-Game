package com.fuzzjump.game.game.screen.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *
 *
 */
public class ParallaxLayer {

   private TextureRegion region;
   private float xRatio, yRatio;

   public ParallaxLayer(Texture texture, float xRatio, float yRatio) {
      super();
      this.region = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
      this.xRatio = xRatio;
      this.yRatio = yRatio;
   }

   public TextureRegion getRegion() {
      return region;
   }

   public float getxRatio() {
      return xRatio;
   }

   public float getyRatio() {
      return yRatio;
   }

   public void render(float xPosition, float yPosition, float width, float height, SpriteBatch batch) {
      batch.draw(region, xPosition, yPosition, width, height);
   }

}