/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.ArrayList;

/**
 *
 * @author erhannis
 */
public class Vertex {
  public ArrayList<Cell> cells = new ArrayList<>(); // Redundant
  public ArrayList<Face> faces = new ArrayList<>(); // Redundant
  
  public double[] coords;
  
  public Vertex(double... coords) {
    this.coords = coords.clone();
  }
}
