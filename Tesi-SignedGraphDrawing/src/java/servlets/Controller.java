/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import beans.SpectralAnalysis;
import beans.Node;
import beans.Edge;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import javafx.util.Pair;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Chiara
 */
public class Controller extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
          
            String dtype = (String) request.getParameter("dtype");
            String graph = (String) request.getParameter("graph");
            
           
            /*dispatcher dell'operazione*/
            //RequestDispatcher rd;
            //ServletContext ctx = getServletContext();
            HashMap<Integer, Node> n = new HashMap<>();
            ArrayList<Edge> e = new ArrayList<>();

            String line = "";
            String cvsSplitBy = ",";
            
            String[] edges_text = graph.split("\n");
            int i;
            System.out.println(graph);
            
            for (i=0; i<edges_text.length; i++) {
                if (i != 0) {
                    line = edges_text[i];
                    String[] data = line.split(cvsSplitBy);
                
                    int source_id = Integer.parseInt(data[0]);
                    int target_id = Integer.parseInt(data[1]);
                    int id = Integer.parseInt(data[3]);

                    String sign = data[4];
                    System.out.println("---" + line);
                    System.out.println("--" + id + "," + source_id + "," + target_id + "," + sign);

                    Node source;
                    Node target;

                    if (!n.containsKey(source_id)) {
                        source = new Node(source_id);
                        n.put(source_id, source);
                    } else {
                        source = n.get(source_id);
                    }

                    if (!n.containsKey(target_id)) {
                        target = new Node(target_id);
                        n.put(target_id, target);
                    } else {
                        target = n.get(target_id);
                    }

                    Edge arco = new Edge(id, source, target, sign);
                    e.add(arco);
                }
            }
            
            /*String csvFile = "/WEB-INF/" + graph;

           
            InputStream is = this.getServletContext().getResourceAsStream(csvFile);
            Scanner input = new Scanner(is);
            int i = 0;

            
            while (input.hasNextLine()) {
                if (i != 0) {
                    line = input.nextLine();
                    String[] data = line.split(cvsSplitBy);
                
                    int source_id = Integer.parseInt(data[0]);
                    int target_id = Integer.parseInt(data[1]);
                    int id = Integer.parseInt(data[3]);

                    String sign = data[4];

                    Node source;
                    Node target;

                    if (!n.containsKey(source_id)) {
                        source = new Node(source_id);
                        n.put(source_id, source);
                    } else {
                        source = n.get(source_id);
                    }

                    if (!n.containsKey(target_id)) {
                        target = new Node(target_id);
                        n.put(target_id, target);
                    } else {
                        target = n.get(target_id);
                    }

                    Edge arco = new Edge(id, source, target, sign);
                    e.add(arco);
                }else{
                    line = input.nextLine();
                }
                i++;
            }*/

          
            Node[] nodes = n.values().toArray(new Node[0]);
            Edge[] edges = e.toArray(new Edge[0]);

            SpectralAnalysis sp_analysis = new SpectralAnalysis(nodes, edges);
            sp_analysis.eigenvectorDecomposition(dtype, 1, 1);

            
            Node[] lay_nodes = sp_analysis.getNodes();
            ArrayList<Edge> lay_interEdges = sp_analysis.getInterEdges();
            ArrayList<Edge> lay_intraEdges = sp_analysis.getIntraEdges();

            //print
            out.write("{\"nodes\":[");
            i=0;
            for(Node nl: lay_nodes){
                if(i!=0){
                    out.write(",");
                }
                
                out.write(nl.toString());
                i++;
            }
            
            out.write("],\"intraEdges\":[");
            i=0;
            
            for(Edge el: lay_intraEdges){
                if(i!=0){
                    out.write(",");
                }
                
                if(el.getSource().getCluster()==el.getTarget().getCluster()){
                    out.write(el.toString());
                    i++; 
                }   
            }
            
            out.write("],\"interEdges\":[");
            i=0;
            for(Edge el: lay_interEdges){
                if(i!=0){
                    out.write(",");
                }
                
                if(el.getSource().getCluster()!=el.getTarget().getCluster()){
                    out.write(el.toString());
                    i++; 
                }   
            }
            out.write("]}");
            out.flush();
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
