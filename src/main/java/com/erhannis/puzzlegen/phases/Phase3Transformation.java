/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Face;
import com.erhannis.puzzlegen.structure.Vertex;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author erhannis
 */
public class Phase3Transformation {
  //TODO This is arguably a bad idea, since vertices aren't guaranteed to be literally equal between faces etc.
  //  Also, you're changing the definition of the vertex inside the hash maps...bad all around.
  public static void rightTriangleToEquilateralBADIDEA(Cell root) {
    HashSet<Vertex> vertices = new HashSet<>();
    Utils.forEachCell(root, c -> {
      vertices.addAll(c.vertices);
    });
    
    double a = (1 + Math.sqrt(3)) / (2 * Math.sqrt(3));
    double c = (-1 + Math.sqrt(3)) / (2 * Math.sqrt(3));
    double b = c;
    double d = a;
    
    ArrayList<Vertex> vertList = new ArrayList<>(vertices);
    for (Vertex v : vertList) {
      double[] newCoords;
      newCoords = new double[]{v.coords[0], v.coords[1]};
      newCoords = new double[]{newCoords[1], -newCoords[0]};
      newCoords = new double[]{(a*newCoords[0]) + (b*newCoords[1]), (c*newCoords[0]) + (d*newCoords[1])};
      v.coords[0] = newCoords[0];
      v.coords[1] = newCoords[1];
    }
  }
}
