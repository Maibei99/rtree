package test;

/**
 * Clase para guardar los resultados de las pruebas.
 */
public class Results {
    /**
     * Arreglo de tiempos de ejecución. Cada tiempo corresponde a una consulta.
     */
    double[] times;
    /**
     * Arreglo de accesos a disco. Cada cantidad de accesos corresponde a una consulta.
     */
    int[] accesses;
    /**
     * Cantidad de consultas.
     */

    int n;

    /**
     * Constructor de la clase.
     * @param times
     * @param accesses
     * @param n
     */
    public Results(double[] times, int[] accesses, int n) {
        this.times = times;
        this.accesses = accesses;
        this.n = n;
    }

    /**
     * Método para obtener el tiempo promedio de las consultas.
     * @return Tiempo promedio.
     */
    public double getAverageTime() {
        double sum = 0;
        for (double time : times) {
            sum += time;
        }
        return sum / n;
    }

    /**
     * Método para obtener la cantidad de accesos promedio a disco.
     * @return Cantidad de accesos promedio.
     */
    public double getAverageAccesses() {
        double sum = 0;
        for (int access : accesses) {
            sum += access;
        }
        return sum / n;
    }

    /**
     * Método para calcular la desviación estándar de los tiempos de ejecución.
     * @return Desviación estándar de los tiempos de ejecución.
     */
    public double calculateStandardDeviationTime() {

        // get the sum of array
        double sum = 0.0;
        for (double i : times) {
            sum += i;
        }

        // get the mean of array
        int length = times.length;
        double mean = sum / length;

        // calculate the standard deviation
        double standardDeviation = 0.0;
        for (double num : times) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    /**
     * Método para calcular la desviación estándar de los accesos a disco.
     * @return Desviación estándar de los accesos a disco.
     */
    public double calculateStandardDeviationAccesses() {

        // get the sum of array
        double sum = 0.0;
        for (double i : accesses) {
            sum += i;
        }

        // get the mean of array
        int length = accesses.length;
        double mean = sum / length;

        // calculate the standard deviation
        double standardDeviation = 0.0;
        for (double num : accesses) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    /**
     * Método para obtener los resultados en forma de String.
     * @return Resultados en forma de String.
     */
    @Override public String toString() {
        return "Average time: " + getAverageTime() + "\n" +
                "Standard deviation time: " + calculateStandardDeviationTime() + "\n" +
                "Average accesses: " + getAverageAccesses() + "\n" +
                "Standard deviation accesses: " + calculateStandardDeviationAccesses() + "\n";
    }

}
