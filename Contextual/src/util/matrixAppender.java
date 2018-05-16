/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.mindprod.ledatastream.LEDataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

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
    
    public static final double[][] getFromTxT(File[] distbins, String extension, int rows, int columns) throws IOException{
        return MatrixLoader.NormMatrixIO(distbins, extension, rows, columns);
    }
    
    public static final double[][] getFromDistbin(File[] distbins, String extension, int rows, int columns) throws IOException{
        return MatrixLoader.LEMatrixIO(distbins, extension, rows, columns);
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

        // faz o append dos topics na matriz de contexto
        // antes as matrizes de contexto so haviam 20k de itens
//----------------------------------------------------------------------    
        // para cada descritor:

        String descriptorPath = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/bic/"; // recebe um descritor

        String mapPath = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/clef.map"; // o mapa dos distbins de consulta desse descritor(geralmente nao muda)

        File[] files = MatrixLoader.getFilesFromMap(descriptorPath, mapPath); // lista os arquivos

        files = Arrays.copyOfRange(files, 20000, 20180);// filtra o numero de arquivos que eu quero do mapa(so os topics)

        double[][] distbins = getFromDistbin(files, ".distbin", 20180, 180); // upa a matriz de distbins para a memoria

        // para cada contexto:
        
        mapPath = "/media/luciano/530492be-a614-4aca-b8cd-036f158e2080/ic/www.recod.ic.unicamp.br/~rtripodi/ic08tarballs/clef_cs3.map"; // carrega seu mapa
        
        files = MatrixLoader.getFilesFromMap(descriptorPath, mapPath); // lista todos os arquivos do mapa (faz uma concatenacao do diretorio com o nome do arquivo) 
        
        files = Arrays.copyOfRange(files, 20000, 20180);// filtra o numero de arquivos que eu quero do mapa(so os topics)        
        
        double[][] contextual = getFromTxT(files, "", 20180, 180); // carrega a matriz de contexto desse diretorio                        
        
        System.out.println("antes -> "+contextual[20001][0]);
        
        double[][] appended = append(distbins, contextual, 20000, 20180, 0, 180); // faz um append dos topics da matriz original na matriz de contexto
        
        System.out.println("despues-> "+appended[20001][0]);
        
        // TODO: escrever os arquivos em formato distbin em suas respectivas pastas.
        for(int i = 0; i < files.length; i ++){
            MatrixLoader.matrixOutput(files[i], appended,20180,i);                    
        }
    }
}
