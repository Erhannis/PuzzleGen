/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author erhannis
 */
public class Cell {
  public ArrayList<Face> faces = new ArrayList<>(); // Definitional
  public ArrayList<Vertex> vertices = new ArrayList<>(); // Redundant
  
  public Set<Cell> getAdjacentNeighbors() {
    Set<Cell> result = faces.stream().flatMap(f -> f.cells.stream()).collect(Collectors.toSet());
    result.remove(this);
    return result;
  }
  
  public Set<Cell> getAllNeighbors() {
    Set<Cell> result = vertices.stream().flatMap(v -> v.cells.stream()).collect(Collectors.toSet());
    result.remove(this);
    return result;
  }
}
