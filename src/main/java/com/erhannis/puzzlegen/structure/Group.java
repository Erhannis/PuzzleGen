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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Groups contain and are defined by cells.  However, note that unlike the
 * cell-face-vertex relationship, those other three are not aware of Group, nor
 * does Group keep a redundant copy of faces or vertices.  There are, however,
 * convenience methods for retrieving them.  The set of Cells in a Group is
 * expected to change, unlike in the case of cell-face-vertex.
 * 
 * @author erhannis
 */
public class Group {
  /** Definitional */
  public HashSet<Cell> cells = new HashSet<>();
  
  public Set<Face> getFaces() {
    return cells.stream().flatMap(c -> c.faces.stream()).collect(Collectors.toSet());
  }

  public Set<Vertex> getVertices() {
    return cells.stream().flatMap(c -> c.vertices.stream()).collect(Collectors.toSet());
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(cells);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Group)) {
      return false;
    }
    return Objects.equals(this.cells, ((Group)obj).cells);
  }
}
