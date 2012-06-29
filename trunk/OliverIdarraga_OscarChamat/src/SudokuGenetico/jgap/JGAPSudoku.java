/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SudokuGenetico.jgap;

import java.awt.TextArea;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JSpinner;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.DefaultConfiguration;
import rutas.GetRoutes;

/**
 * Sudoku realizado con algorimtos geneticos con el paquete jgap.
 * @author civilian
 * @version 1.5
 */
public class JGAPSudoku {

    private static int GENERACIONES=600;
    private static int POBLACION=100;
    private int nn;
    private int n;
    private Scanner sc;
    private Coordenadas c;
    TextArea taJpag;

    static void dbg(Object... o) {
        System.out.println(Arrays.deepToString(o));
    }

    /**
     * @author civilian
     * @since 1.0
     */
    public static void main(String[] args) throws FileNotFoundException {
        try {
//            dbg("hola");
            JGAPSudoku sud = new JGAPSudoku();
            sud.solveSudoku();
        } catch (Exception ex) {
            Logger.getLogger(JGAPSudoku.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public JGAPSudoku() throws FileNotFoundException, IOException {
        sc = new Scanner(new File(GetRoutes.escogerRutaArchivo()));
        n = sc.nextInt();
//        dbg(n);
        nn = n * n;
        c = new Coordenadas(n);
    }
//    static void writeMatrix(int[][] solution) {
//        for (int i = 0; i < 9; ++i) {
//            if (i % 3 == 0) {
//                System.out.println(" -----------------------");
//            }
//            for (int j = 0; j < 9; ++j) {
//                if (j % 3 == 0) {
//                    System.out.print("| ");
//                }
//                System.out.print(solution[i][j] == 0 ? " " : Integer.toString(solution[i][j]));
//
//                System.out.print(' ');
//            }
//            System.out.println("|");
//        }
//        System.out.println(" -----------------------");
//    }

    public JGAPSudoku(TextArea taJpag, String archivo, Integer poblacion, Integer generaciones) throws FileNotFoundException {
        this.taJpag = taJpag;
        sc = new Scanner(new File(archivo));
        n = sc.nextInt();
//        dbg(n);
        nn = n * n;
        c = new Coordenadas(n);
        POBLACION=poblacion;
        GENERACIONES=generaciones;
    }

    public void solveSudoku() throws Exception {

        println("Tamaño lado del cuadrado del sudoku = " + n);

        Configuration.reset();
        // Configuracion default
        Configuration conf = new DefaultConfiguration();

        // Se cambia el evaluador de aptitud
        FitnessFunction myFunc = new FuncionAptitudSudoku(n);
        conf.setFitnessFunction(myFunc);

        // Ahora se crea genes de prueba para configurar el problema
        Gene[] sampleGenes = new Gene[nn * nn];
        for (int k = 0; k < sampleGenes.length; k++) {
            sampleGenes[k] = new GenSudoku(conf, 1, nn);
        }

        int val;
        for (int i = 0; i < nn; i++) {
            for (int j = 0; j < nn; j++) {
                val = sc.nextInt();
                if (val != 0) {
//                    sampleGenes[c.campo(i, j)]=new GenSudoku(conf, 1, nn,true,val);
                    ((GenSudoku) sampleGenes[c.campo(i, j)]).setValorInicial(val);
                }
            }
        }
//        dbg(sampleGenes);
//        dbg(sampleGenes.length);
        Chromosome sampleChromosome = new Chromosome(conf, sampleGenes);
        conf.setSampleChromosome(sampleChromosome);

        //Cuantos cromosomas en la poblacion
        conf.setPopulationSize(POBLACION);
        Genotype population;

        population = Genotype.randomInitialGenotype(conf);

        println("Evolucionando ");
//        dbg("populacion",population);
        for (int i = 0; i < GENERACIONES; i++) {
            print(String.format("Generacion %d: %s\n", i, population.getFittestChromosome().getFitnessValue() + " "));
        }
        println("");


        IChromosome bestSolutionSoFar = population.getFittestChromosome();
        println("La mejor solucion tiene una aptitud de: "
                + bestSolutionSoFar.getFitnessValue());
        println("Aqui esta el sudoku completo: ");
        Gene[] genes = bestSolutionSoFar.getGenes();
        for (int k = 0; k < genes.length; k++) {
            String repr = genes[k].getPersistentRepresentation();
            print(repr.substring(0, repr.indexOf(":")) + "\t");
            if ((k + 1) % nn == 0) {
                println("");
            }
        }

//        dbg(bestSolutionSoFar);
    }

    private void println(String string) {
        print(string+"\n");
    }

    private void print(String string) {
        System.out.print(string);
        if(taJpag!=null)
            taJpag.append(string);
    }

//    private void printf(String string, int i, String string0) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}