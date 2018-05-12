/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.IOException;

/**
 * This will be used to append matrixes.
 *
 * @author Luciano Araujo Dourado Filho
 */
public class matrixAppender {

    public static final double[][] getDoubleMatrixFromDirectory(String path, String fileFilter, int rows, int columns) throws IOException {
        return MatrixLoader.NormMatrixIO(path, fileFilter, rows, columns);
    }

    public static final double[][] getDistbinMatrixFromDirectory(String path, String fileFilter, int rows, int columns) throws IOException {
        return MatrixLoader.LEMatrixIO(path, fileFilter, rows, columns);
    }

    /**
     * Appends matrix1 to matrix2, from a given range.
     *
     * @param matrix1
     * @param matrix2
     * @param fromRow
     * @param toRow
     * @param fromColumn
     * @param toColumn
     * @return
     */
    public static final double[][] append(double[][] matrix1, double[][] matrix2, int fromRow, int toRow, int fromColumn, int toColumn) {

        for (int i = fromRow; i < toRow; i++) {
            for (int j = fromColumn; j < toColumn; j++) {
                matrix2[i][j] = matrix1[i][j];
            }
        }

        return matrix2;
    }

    public static void main(String[] args) throws IOException {

        // isso aqui faz o append dos topics na matriz de contexto
        // antes as matrizes de contexto so haviam 20k de itens
        
        /**
         *  TODO:
         *  Criar metodo para escrever os distbins com seus respectivos nomes
         *  Criar metodo para percorrer todos as pastas de todos os descritores criando seus distbins
         */ 
        
        double[][] normalMatrix = getDoubleMatrixFromDirectory("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics_cs2", "jpg.cs_2", 20180, 180);

        double[][] distbinMatrix = getDistbinMatrixFromDirectory("/home/luciano/Desktop/ic/Rodrigo/descriptors/acc/ic08topics", "jpg.ppm.distbin", 20180, 180);
        
        double[][] appended = append(distbinMatrix, normalMatrix, 20000, 20180, 0, 180);

        System.out.println("##################################################################################");
        System.out.println("##################################################################################");
        System.out.println("##################################################################################");

        for (int i = 20000; i < 20180; i++) {
            for (int j = 0; j < 180; j++) {
                System.out.printf("[" + "%f" + "] ", appended[i][j]);
            }
            System.out.println("");
        }

    }
}
