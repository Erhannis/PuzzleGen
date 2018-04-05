/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @see Cell
 * @author erhannis
 */
public class Vertex {
  /** Redundant */
  public HashSet<Cell> cells = new HashSet<>();

  /** Redundant */
  public HashSet<Face> faces = new HashSet<>();
  
  public double[] coords;
  
  public Vertex(double... coords) {
    this.coords = coords.clone();
  }
  
  /**
   * Gets all vertices of connected faces, minus this vertex.
   * @return 
   */
  public Collection<Vertex> getNeighbors() {
    Set<Vertex> result = faces.stream().flatMap(f -> f.vertices.stream()).collect(Collectors.toSet());
    result.remove(this);
    return result;
  }
  
  @Override
  public int hashCode() {
    //Note that there are arguments to be made about "cells", etc. but I am
    //  currently choosing to dismiss them.
    return Arrays.hashCode(coords);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Vertex)) {
      return false;
    }
    return Objects.deepEquals(this.coords, ((Vertex)obj).coords);
  }
  
  @Override
  public String toString() {
    return "(v " + Arrays.toString(coords) + ")"; //To change body of generated methods, choose Tools | Templates.
  }
}
