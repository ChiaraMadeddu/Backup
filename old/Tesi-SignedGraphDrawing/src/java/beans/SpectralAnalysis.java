/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;
import java.util.ArrayList;
import org.apache.commons.math3.linear.EigenDecomposition;

import java.util.HashMap;
import javafx.util.Pair;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math3.linear.RealVector;
/**
 *
 * @author Chiara
 */
public class SpectralAnalysis {
    private Node[] nodes;
    private Edge[] edges;
    
    private double[][] W;
    private double[][] L;
    
    public SpectralAnalysis(Node[] nodes, Edge[] edges){
        this.nodes = nodes;
        this.edges = edges;
        System.out.println(nodes.length);
        buildAdjacencyMatrix();
        buildLaplacianMatrix();
    }
    
    
    
    
    private void buildAdjacencyMatrix(){
        W = new double[nodes.length][nodes.length];

        for(Edge e: edges){
            int s = e.getSource().getId();
            int t = e.getTarget().getId();
            if(e.getSign().equals("+")){
                W[s][t] = 1f;
                W[t][s] = 1f;
            }else if(e.getSign().equals("-")){
                W[s][t] = -1f;
                W[t][s] = -1f;
            }

        }
    }
    
    
    
    private void buildLaplacianMatrix(){
        //D signed degree matrix
        double[][] D = new double[W.length][W.length];

        for(int r=0; r<W.length; r++){
            D[r][r] = 0;
            for(int c=0; c<W.length; c++){
                D[r][r] = D[r][r] + Math.abs(W[r][c]);         
            }
        }
        
        //L signed Laplacian matrix
        L = new double[W.length][W.length];
        for(int r=0; r<W.length; r++){
            for(int c=0; c<W.length; c++){
                L[r][c] = D[r][c] - W[r][c];         
            }
        }            
    }
    
    
    private double[] getEigenVector(String type){
        double[][] matrix;
        if(type.equals("adjacency")){
            matrix = W;
        }else{
            matrix = L;
        }
        
        RealMatrix r_matrix = MatrixUtils.createRealMatrix(matrix);
        EigenDecomposition ed = new EigenDecomposition(r_matrix,MathUtils.SAFE_MIN);
        RealMatrix eigenvalues = ed.getD();


        ArrayList<Double> evalues = new ArrayList<Double>();

        for(int i=0; i<eigenvalues.getRowDimension(); i++){
            double[] row = eigenvalues.getRow(i);
            evalues.add(row[i]);
        }


        int index = getMinOrMaxValueIndex(evalues,type.equals("adjacency"));
        

     
        RealVector eigenvector = ed.getEigenvector(index);
        double[] m_eigenvector = new double[eigenvector.getDimension()];
        double interval = eigenvector.getMaxValue() - eigenvector.getMinValue();
        double input_start = eigenvector.getMinValue();

        if(interval == 0){
            interval = 1;
        }

        for(int i=0; i<m_eigenvector.length; i++){
            m_eigenvector[i] = (((eigenvector.getEntry(i)-input_start)/interval)*2)-1; //mapping tra [-1,1]
        }
        
        return m_eigenvector;
    }

    
    private int getMinOrMaxValueIndex(ArrayList<Double> values, boolean getMax){
        int index = 0;
        double value = values.get(0);

        for(int i=1; i<values.size(); i++){
            if(getMax){
                if(values.get(i) > value){
                    index = i;
                    value = values.get(i);
                }
            }else{
                if(values.get(i) < value){
                    index = i;
                    value = values.get(i);
                }
            }

        }
        return index;
    }
    
    public Node[] getNodes(){
        return nodes;
    }
    
    public Edge[] getEdges(){
        return edges;
    }
    
    
    public ArrayList<Edge> getIntraEdges(){
        ArrayList<Edge> intra = new ArrayList<>();
        for(Edge e:edges){
            if(e.getSource().getCluster()==e.getTarget().getCluster()){
                intra.add(e);
            }  
        }
        return intra;
    }
    
    
    public ArrayList<Edge> getInterEdges(){
        ArrayList<Edge> intra = new ArrayList<>();
        for(Edge e:edges){
            if(e.getSource().getCluster()!=e.getTarget().getCluster()){
                intra.add(e);
            }  
        }
        return intra;
    }
    
    
    public void eigenvectorDecomposition(String type, float xScaleFactor, float yScaleFactor){
        
        HashMap<Float,Integer> distinct_ev = new HashMap<Float,Integer>();
        ArrayList<Float> clusters = new ArrayList<Float>();

        double[] m_eigenvector = getEigenVector(type);
        for (int i=0; i<m_eigenvector.length; i++) {
            float me = (float)m_eigenvector[i];
            if(distinct_ev.containsKey(me)){
                int temp_count = distinct_ev.get(me)+1;
                distinct_ev.put(me,temp_count);
                clusters.add(me);
            }else{
                distinct_ev.put(me,1);
            }
        }



        for (Node n : nodes) {
            int index = n.getId();

            float me = (float)m_eigenvector[index];
            int h = distinct_ev.get(me);
            int cluster = clusters.indexOf(me);
            float x = me*(float)xScaleFactor;
            float y = (float)h*(float)yScaleFactor;
            distinct_ev.put((float)me,h-1);

            n.setX(x);
            n.setY(y);
            n.setOriginalX(me);
            n.setOriginalY(h);
            n.setCluster(cluster);
        }
        
    }
}
