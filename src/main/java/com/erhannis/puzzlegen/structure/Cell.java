/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import com.erhannis.mathnstuff.MeMath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cells contain and are defined by faces.<br/>
 * Faces contain and are defined by vertices.<br/>
 * Vertices contain and are defined by coordinates.<br/>
 * <br/>
 * For convenience, each of the three reference the other two.<br/>
 * <br/>
 * Create vertices, then faces, then cells.<br/>
 * 
 * @author erhannis
 */
public class Cell {
  /** Definitional */
  public HashSet<Face> faces = new HashSet<>();
  
  /** Redundant */
  public HashSet<Vertex> vertices = new HashSet<>();
  
  /**
   * Creates a cell composed of these faces.
   * Updates the faces and vertices with a reference to itself.
   * 
   * @param faces 
   */
  public Cell(Face... faces) {
    this.faces.addAll(Arrays.asList(faces));
    this.vertices.addAll(this.faces.stream().flatMap(f -> f.vertices.stream()).collect(Collectors.toList()));
    for (Face f : this.faces) {
      f.cells.add(this);
    }
    for (Vertex v : this.vertices) {
      v.cells.add(this);
    }
  }

  public Set<Cell> getAdjacentNeighborsAndSelf() {
    Set<Cell> result = faces.stream().flatMap(f -> f.cells.stream()).collect(Collectors.toSet());
    return result;
  }
  
  public Set<Cell> getAdjacentNeighbors() {
    Set<Cell> result = getAdjacentNeighborsAndSelf();
    result.remove(this);
    return result;
  }
  
  public Set<Cell> getAllNeighbors() {
    Set<Cell> result = vertices.stream().flatMap(v -> v.cells.stream()).collect(Collectors.toSet());
    result.remove(this);
    return result;
  }
  
  public static Set<Face> getSharedFaces(Cell a, Cell b) {
    Set<Face> faces = new HashSet<>(a.faces);
    faces.retainAll(b.faces);
    return faces;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(faces);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cell)) {
      return false;
    }
    return Objects.equals(this.faces, ((Cell)obj).faces);
  }

  @Override
  public String toString() {
    //TODO Could export
    Function<double[], Double> norm = a -> {
      double sum = 0;
      for (double d : a) {
        sum += d * d;
      }
      return sum;
    };
    Vertex m = null;
    for (Vertex v : this.vertices) {
      if (m == null || norm.apply(v.coords) < norm.apply(m.coords)) {
        m = v;
      }
    }
    return "(c [" + m.coords[0] + ", " + m.coords[1] + "])"; //To change body of generated methods, choose Tools | Templates.
  }
}
