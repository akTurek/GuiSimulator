package org.example.guisimulator;

public class Celica {
    private float preTemp;
    private float nowTemp;
    public Boolean isHeatSource;


    public Celica(float preTemp, float nowTemp, Boolean isHeatSource) {
        this.preTemp = preTemp;
        this.nowTemp = nowTemp;
        this.isHeatSource = isHeatSource;
    }

    public void setHeatSource(Boolean heatSource) {
        isHeatSource = heatSource;
    }

    public Boolean getHeatSource() {
        return isHeatSource;
    }

    public float getNowTemp() {
        return nowTemp;
    }

    public float getPreTemp() {
        return preTemp;
    }

    public void setNowTemp(float nowTemp) {
        this.nowTemp = nowTemp;
    }

    public void setPreTemp(float preTemp) {
        this.preTemp = preTemp;
    }

    public void swTemp() {
        this.preTemp = this.nowTemp;
    }


}





