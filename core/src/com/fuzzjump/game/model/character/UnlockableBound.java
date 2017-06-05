package com.fuzzjump.game.model.character;

/**
 * Created by Steveadoo on 12/5/2015.
 */
public class UnlockableBound {

    public double x;
    public double y;
    public double w;
    public double h;

    public UnlockableBound(double x, double y, String w, String h) {
        this.x = x;
        this.y = y;
        try {
            this.w = Float.parseFloat(w);
        } catch(Exception e) {
            this.w = w.equals("asp") ? -1 : 0;
        }
        try {
            this.h = Float.parseFloat(h);
        } catch(Exception e) {
            this.h = h.equals("asp") ? -1 : 0;
        }
    }
}
