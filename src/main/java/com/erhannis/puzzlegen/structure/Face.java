/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * 
 * @see Cell
 * @author erhannis
 */
public class Face {
  /** Redundant */
  public HashSet<Cell> cells = new HashSet<>();
  
  /** Definitional */
  public HashSet<Vertex> vertices = new HashSet<>();
  
  public Face(Vertex... vertices) {
    this.vertices.addAll(Arrays.asList(vertices));
    for (Vertex v : vertices) {
      v.faces.add(this);
    }
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(vertices);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Face)) {
      return false;
    }
    return Objects.equals(this.vertices, ((Face)obj).vertices);
  }
}
