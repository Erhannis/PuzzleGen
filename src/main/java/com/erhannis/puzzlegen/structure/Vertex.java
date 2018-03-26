/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

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
  
  @Override
  public int hashCode() {
    //Note that there are arguments to be made about "cells", etc. but I am
    //  currently choosing to dismiss them.
    return Objects.hash(coords);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Vertex)) {
      return false;
    }
    return Objects.deepEquals(this.coords, ((Vertex)obj).coords);
  }
}
