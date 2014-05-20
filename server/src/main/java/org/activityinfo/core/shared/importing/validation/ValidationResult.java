package org.activityinfo.core.shared.importing.validation;

import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.Pair;
import org.activityinfo.core.shared.importing.strategy.InstanceScorer;

public class ValidationResult {

    public static enum State {
        OK, MISSING, ERROR, CONFIDENCE
    }

    public static final ValidationResult MISSING = new ValidationResult(State.MISSING) {
    };

    public static final ValidationResult OK = new ValidationResult(State.OK) {
    };

    private final State state;
    private Pair<Cuid, Cuid> rangeWithInstanceId;
    private Cuid instanceId;
    private String typeConversionErrorMessage;
    private String convertedValue;
    private double confidence;

    private ValidationResult(State state) {
        this.state = state;
    }

    public static ValidationResult error(String message) {
        ValidationResult result = new ValidationResult(State.ERROR);
        result.typeConversionErrorMessage = message;
        return result;
    }

    public static ValidationResult converted(String value, double confidence) {
        ValidationResult result = new ValidationResult(State.CONFIDENCE);
        result.convertedValue = value;
        result.confidence = confidence;
        return result;
    }

    public boolean hasTypeConversionError() {
        return typeConversionErrorMessage != null;
    }

    public String getTypeConversionErrorMessage() {
        return typeConversionErrorMessage;
    }

    public String getConvertedValue() {
        return convertedValue;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean wasConverted() {
        return convertedValue != null;
    }

    public State getState() {
        return state;
    }

    public boolean shouldPersist() {
        return state == State.OK || (state == State.CONFIDENCE && confidence >= InstanceScorer.MINIMUM_SCORE);
    }

    public Cuid getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Cuid instanceId) {
        this.instanceId = instanceId;
    }

    public Pair<Cuid, Cuid> getRangeWithInstanceId() {
        return rangeWithInstanceId;
    }

    public void setRangeWithInstanceId(Pair<Cuid, Cuid> rangeWithInstanceId) {
        this.rangeWithInstanceId = rangeWithInstanceId;
    }
}
