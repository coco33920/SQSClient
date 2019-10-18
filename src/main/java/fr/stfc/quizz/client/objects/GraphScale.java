package fr.stfc.quizz.client.objects;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.function.Function;

public enum GraphScale {


    HOUR("Heure", 1, new SimpleDateFormat("dd/MM/yyyy 00:00:00"), 24 * 3600 * 1000, 3600 * 1000),
    DAY("Jour", 1, new SimpleDateFormat("dd/MM/yyyy 00:00:00"), 24 * 3600 * 1000 * 7, 3600 * 1000 * 24),
    MONTH("Mois", 1, new SimpleDateFormat("01/MM/yyyy 00:00:00"), 24 * 3600 * 1000L * 28, 3600 * 1000 * 24),
    YEAR("An", 1, new SimpleDateFormat("dd/MM/yyyy 00:00:00"), 24 * 3600 * 1000 * 365L, 3600 * 1000 * 24 * 5);

    private String label;
    private int number;
    private SimpleDateFormat format;
    private long amplitude;
    private long interval;

    private static HashMap<String, HashMap<Integer, String>> h = new HashMap<>();

    static {
        HashMap<Integer, String> days = new HashMap<>();
        days.put(1, "Lundi");
        days.put(2, "Mardi");
        days.put(3, "Mercredi");
        days.put(4, "Jeudi");
        days.put(5, "Vendredi");
        days.put(6, "Samedi");
        days.put(0, "Dimanche");
        h.put("Jour", days);

        HashMap<Integer, String> months = new HashMap<>();
        months.put(0, "Janvier");
        months.put(1, "Février");
        months.put(2, "Mars");
        months.put(3, "Avril");
        months.put(4, "Mai");
        months.put(5, "Juin");
        months.put(6, "Juillet");
        months.put(7, "Aout");
        months.put(8, "Septembre");
        months.put(9, "Octobre");
        months.put(10, "Novembre");
        months.put(11, "Decembre");
        h.put("Mois", months);
    }

    public int getNumber() {
        return number;
    }

    private GraphScale(String label, int number, SimpleDateFormat format, long amplitude, long interval) {
        this.label = label;
        this.number = number;
        this.format = format;
        this.amplitude = amplitude;
        this.interval = interval;
    }

    public long getAmplitude() {
        return amplitude;
    }

    public long getInterval() {
        return interval;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public String constructLabel(int i, long... dates) {
        if (this == HOUR) {
            int s = (i - 1);
            if (s == -1)
                s = 23;
            return s + "h - " + i + "h";
        }
        if (this == DAY) {
            HashMap<Integer, String> d = h.get("Jour");
            return d.get(i);
        }
        if (this == MONTH) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM");
            long actualDate = MONTH.interval * i;
            long startDate = dates[0];
            long totalDate = actualDate + startDate;
            return format.format(totalDate);
        }
        if (this == YEAR) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            long actualDate = MONTH.interval * i * 5;
            long startDate = dates[0];
            long totalDate = actualDate + startDate;
            return format.format(totalDate);
        }
        return "A";
    }

    public String getLabel() {
        return label;
    }
}
