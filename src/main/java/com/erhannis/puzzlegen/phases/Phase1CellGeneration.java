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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Contains a variety of methods for generating cells.
 *
 * @author erhannis
 */
public class Phase1CellGeneration {
  public static final int FACTOR = 10;

  public static Collection<Cell> generateSquareBoard(int w, int h) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));
    BagMap<Vertex, Cell> v2c = new BagMap<>();

    for (double y = 0; y < (h * FACTOR); y += FACTOR) {
      for (double x = 0; x < (w * FACTOR); x += FACTOR) {
        Face[] lFaces = new Face[4];
        double o = FACTOR;
        lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + o, y));
        lFaces[1] = faces.get(vertices.get(x + o, y), vertices.get(x + o, y + o));
        lFaces[2] = faces.get(vertices.get(x + o, y + o), vertices.get(x, y + o));
        lFaces[3] = faces.get(vertices.get(x, y + o), vertices.get(x, y));
        Cell c = new Cell(lFaces);
        v2c.put(c, c.vertices.toArray(new Vertex[0]));
      }
    }
    return v2c.map.values();
  }

  public static Collection<Cell> generateTriangleBoard(int w, int h, boolean square) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));
    HashSet<Cell> cells = new HashSet<>();

    //double phi = Math.sin(Math.PI / 3); // About 0.866
    double phi = 0.85546875; // Near-ish base 2 representation
    for (int row = 0; row < h; row++) {
      double y = row * FACTOR * phi;
      int colOffset = 0;
      if (!square) {
        w = (row * 2) + 1;
        colOffset = -row;
      }
      for (int col = colOffset; col < w + colOffset; col++) {
        double x = col * (FACTOR / 2);

        Face[] lFaces = new Face[3];
        double u = FACTOR / 2;
        double u2 = FACTOR;
        double v = phi * FACTOR;

        if (row % 2 == 0) {
          // Leftmost trigangle faces up, from y+1 to y
          if (col % 2 == 0) {
            // Same as leftmost: facing up
            lFaces[0] = faces.get(vertices.get(x, y + v), vertices.get(x + u, y));
            lFaces[1] = faces.get(vertices.get(x + u, y), vertices.get(x + u2, y + v));
            lFaces[2] = faces.get(vertices.get(x + u2, y + v), vertices.get(x, y + v));
          } else {
            // Opposite from leftmost: facing down
            lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + u, y + v));
            lFaces[1] = faces.get(vertices.get(x + u, y + v), vertices.get(x + u2, y));
            lFaces[2] = faces.get(vertices.get(x + u2, y), vertices.get(x, y));
          }
        } else {
          // Leftmost triangle faces down, from y to y+1
          if (col % 2 == 0) {
            // Same as leftmost: facing down
            lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + u, y + v));
            lFaces[1] = faces.get(vertices.get(x + u, y + v), vertices.get(x + u2, y));
            lFaces[2] = faces.get(vertices.get(x + u2, y), vertices.get(x, y));
          } else {
            // Opposite from leftmost: facing up
            lFaces[0] = faces.get(vertices.get(x, y + v), vertices.get(x + u, y));
            lFaces[1] = faces.get(vertices.get(x + u, y), vertices.get(x + u2, y + v));
            lFaces[2] = faces.get(vertices.get(x + u2, y + v), vertices.get(x, y + v));
          }
        }

        Cell c = new Cell(lFaces);
        cells.add(c);
      }
    }
    return cells;
  }

  public static Collection<Cell> generateHexBoard(int w, int h, boolean square) {
    ListMap<Double, Vertex> vertices = new ListMap<>(new FactoryHashMap<List<Double>, Vertex>((input) -> {
      return new Vertex(ArrayUtils.toPrimitive(input.toArray(new Double[0])));
    }));
    BagMap<Vertex, Face> faces = new BagMap<>(new FactoryHashMap<Set<Vertex>, Face>((input) -> {
      return new Face(input.toArray(new Vertex[0]));
    }));
    HashSet<Cell> cells = new HashSet<>();

    //double px = Math.cos(Math.PI / 3); // 1/2
    //double py = Math.sin(Math.PI / 3);
    double phi = 0.85546875; // Near-ish base 2 representation
    for (int row = 0; row < h; row++) {
      double y0 = row * FACTOR * 2 * phi;
      int colOffset = 0;
      if (!square) {
//       w = (row * 2) + 1;
//       colOffset = -row;
        //TODO Deal with this
        throw new RuntimeException("Non-square hex board not yet implemented");
      }
      for (int col = colOffset; col < w + colOffset; col++) {
        double x = (col * 1.5) * FACTOR;

        Face[] lFaces = new Face[6];
        double u = FACTOR / 2;
        double u2 = FACTOR;
        double u3 = (3 * FACTOR) / 2;
        double v = phi * FACTOR;
        double v2 = (phi * 2) * FACTOR;

        double y;
        if (col % 2 == 0) {
          // Up
          y = y0;
        } else {
          // Down
          y = y0 + v;
        }
        lFaces[0] = faces.get(vertices.get(x, y), vertices.get(x + u2, y));
        lFaces[1] = faces.get(vertices.get(x + u2, y), vertices.get(x + u3, y + v));
        lFaces[2] = faces.get(vertices.get(x + u3, y + v), vertices.get(x + u2, y + v2));
        lFaces[3] = faces.get(vertices.get(x + u2, y + v2), vertices.get(x, y + v2));
        lFaces[4] = faces.get(vertices.get(x, y + v2), vertices.get(x - u, y + v));
        lFaces[5] = faces.get(vertices.get(x - u, y + v), vertices.get(x, y));

        Cell c = new Cell(lFaces);
        cells.add(c);
      }
    }
    return cells;
  }
}
