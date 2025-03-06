package cu.redcuba.object;

import java.util.Date;

public class EvaluationValueInterval {

    private Date dayBegin;
    private Date dayEnd;
    private float evaluation;
    private int rounds;
    private int days;

    public EvaluationValueInterval(Date dayBegin, Date dayEnd, float evaluation, int rounds, int days) {
        this.dayBegin = dayBegin;
        this.dayEnd = dayEnd;
        this.evaluation = evaluation;
        this.rounds = rounds;
        this.days = days;
    }

    public Date getDayBegin() {
        return dayBegin;
    }

    public void setDayBegin(Date dayBegin) {
        this.dayBegin = dayBegin;
    }

    public Date getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(Date dayEnd) {
        this.dayEnd = dayEnd;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

}
