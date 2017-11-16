package com.ricardo.proyecto.conandard.utils;

/**
 * Created by karen on 12/11/2017.
 */

public class HeatIndexCalculator {

    public static double calculateHeatIndex ( int currentTemp, double currentHumidity ) {
        //Setting parameters for Function
        int temperature = currentTemp;
        double humidity = currentHumidity;
        final double C1 = -42.379;
        final double C2 = 2.04901523;
        final double C3 = 10.14333127;
        final double C4 = -0.22475541;
        final double C5 = -.00683783;
        final double C6 = -5.481717E-2;
        final double C7 = 1.22874E-3;
        final double C8 = 8.5282E-4;
        final double C9 = -1.99E-6;
        int T = temperature;
        double R = humidity;
        double T2 = temperature * temperature;
        double R2 = humidity * humidity;

        //Function of Calculating Heat Index
        double answer = C1 + (C2 * T) + (C3 * R) + (C4 * T * R) + (C5 * T2) + (C6 * R2) + (C7 * T2 * R) + (C8 * T * R2) + (C9 * T2 * R2);

        return answer;
    }
}
