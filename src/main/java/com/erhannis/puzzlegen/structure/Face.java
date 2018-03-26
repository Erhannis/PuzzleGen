/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

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
}
