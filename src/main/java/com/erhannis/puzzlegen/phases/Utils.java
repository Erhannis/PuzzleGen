/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.mathnstuff.MeMath;
import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Vertex;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author erhannis
 */
public class Utils {
  //TODO Possibly extract even more generically
  /**
   * Recursively discovers each cell, performing "action" once upon each.
   * @param root
   * @param action 
   */
  public static void forEachCell(Cell root, Consumer<Cell> action) {
    HashSet<Cell> toDo = new HashSet<>();
    HashSet<Cell> done = new HashSet<>();
    toDo.add(root);
    while (!toDo.isEmpty()) {
      Iterator<Cell> i = toDo.iterator();
      Cell c = i.next();
      i.remove();
      
      action.accept(c);
      
      HashSet<Cell> lToDo = new HashSet<Cell>();
      lToDo.addAll(c.getAllNeighbors());
      lToDo.removeAll(done); // Hopefully this is smart about it
      toDo.addAll(lToDo);
      
      done.add(c);
    }
  }
  
  /**
   * Order vertices (counter?)clockwise around their centroid.  Good for filling polygons.
   * 
   * Only works in 2D.
   * 
   * @param vertices
   * @return 
   */
  public static List<Vertex> orderVertices(Collection<Vertex> vertices) {
    ArrayList<Vertex> result = new ArrayList<Vertex>();
    if (vertices.isEmpty()) {
      return result;
    }
    
    double[] centroid = new double[]{0,0};
    
    for (Vertex v : vertices) {
      centroid[0] += v.coords[0];
      centroid[1] += v.coords[1];
    }
    centroid[0] /= vertices.size();
    centroid[1] /= vertices.size();
    
    result.addAll(vertices);
    
    result.sort((a, b) -> {
      double[] ca = MeMath.vectorSubtract(a.coords, centroid);
      double ta = Math.atan2(ca[1], ca[0]);
      double[] cb = MeMath.vectorSubtract(b.coords, centroid);
      double tb = Math.atan2(cb[1], cb[0]);
      return Double.compare(ta, tb);
    });
    return result;
  }
}
