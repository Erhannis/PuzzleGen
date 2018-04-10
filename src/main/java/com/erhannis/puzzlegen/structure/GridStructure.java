/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.structure;

import java.util.HashMap;

/**
 * Represents a grid.
 * 
 * @author erhannis
 */
public abstract class GridStructure {
  public static class SquareGridStructure extends GridStructure {
    private static final int N = 0;
    private static final int E = 1;
    private static final int S = 2;
    private static final int W = 3;
    
    private static final int dirCount = 4;
    
    private final HashMap<Integer, double[]> directions = new HashMap<Integer, double[]>(){{
        put(N, new double[]{0, 1});
        put(E, new double[]{1, 0});
        put(S, new double[]{0, -1});
        put(W, new double[]{-1, 0});
    }};
    
    @Override
    public Vertex getAVertex() {
      return new Vertex(0, 0);
    }

    @Override
    public int getDirectionCount() {
      return dirCount;
    }

    @Override
    public double[] getVector(int direction) {
      return directions.get(direction);
    }

    @Override
    public boolean isVertexOnGrid(Vertex v) {
      return ((((int)v.coords[0]) == v.coords[0]) && (((int)v.coords[1]) == v.coords[1]));
    }  
}
  
  public abstract Vertex getAVertex();
  
  public abstract int getDirectionCount();
  
  public abstract double[] getVector(int direction);
  
  public abstract boolean isVertexOnGrid(Vertex v);
}
