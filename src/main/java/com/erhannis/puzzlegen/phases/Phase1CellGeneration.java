/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.mathnstuff.FactoryHashMap;
import com.erhannis.mathnstuff.utils.BagMap;
import com.erhannis.mathnstuff.utils.ListMap;
import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Face;
import com.erhannis.puzzlegen.structure.Vertex;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Contains a variety of methods for generating cells.
 * @author erhannis
 */
public class Phase1CellGeneration {
 public static Collection<Cell> generateSquareBoard(int w, int h) {
   ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
     return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
   }));
   BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
     return new Face(input.toArray(new Vertex[0]));
   }));
   BagMap<Vertex, Cell> v2c = new BagMap<>();
   
   int factor = 10;
   for (double y = 0; y < (h * factor); y += factor) {
     for (double x = 0; x < (w * factor); x += factor) {
       Face[] lFaces = new Face[4];
       double o = factor;
       lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x+o, y));
       lFaces[1] = faces.get(vertices.get(x+o, y), vertices.get(x+o, y+o));
       lFaces[2] = faces.get(vertices.get(x+o, y+o), vertices.get(x, y+o));
       lFaces[3] = faces.get(vertices.get(x, y+o), vertices.get(x, y));
       Cell c = new Cell(lFaces);
       v2c.put(c, c.vertices.toArray(new Vertex[0]));
     }
   }
   return v2c.map.values();
 }
}
