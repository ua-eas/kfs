package org.kuali.kfs.sys.businessobject;


public class Metric {

    public String measure;
    public String metric;
    public String value;

    public Metric(String measure, String metric, String value) {
        this.measure = measure;
        this.metric = metric;
        this.value = value;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
