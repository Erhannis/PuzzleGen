/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen;

import com.erhannis.puzzlegen.phases.Phase1CellGeneration;
import com.erhannis.puzzlegen.phases.Phase2Grouping;
import com.erhannis.puzzlegen.phases.Phase4GenSvg;
import com.erhannis.puzzlegen.structure.Cell;
import com.erhannis.puzzlegen.structure.Group;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author erhannis
 */
public class Main {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    long t = System.currentTimeMillis();
    //Collection<Cell> cells = Phase1CellGeneration.generateSquareBoard(50, 50);
    Collection<Cell> cells = Phase1CellGeneration.generateTriangleBoard(10, 10, false);
    System.out.println("Phase 1 " + (System.currentTimeMillis() - t));
    
    t = System.currentTimeMillis();
    int bestCount = 0;
    Set<Group> groups = null;
    for (int i = 0; i < 1; i++) {
      System.err.print(i + " ");
      Set<Group> lGroups = Phase2Grouping.groupCellsDefault(cells.stream().findAny().get(), 1);
      System.err.println("");
      HashSet<Cell> lCells = new HashSet<Cell>();
      for (Group g : lGroups) {
        lCells.addAll(g.cells);
      }
      if (lCells.size() > bestCount) {
        groups = lGroups;
        bestCount = lGroups.size();
      }
      if (lCells.size() == cells.size()) {
        System.out.println("Found ideal candidate, breaking early");
        break;
      }
    }
    System.out.println("Phase 2 " + (System.currentTimeMillis() - t));
    
    t = System.currentTimeMillis();
    Phase4GenSvg.writeGridToSvg(cells.stream().findAny().get(), new File("gen/" + System.currentTimeMillis() + "_grid.svg"));
    Phase4GenSvg.writeGroupsToSvg(groups, new File("gen/" + System.currentTimeMillis() + "_groups.svg"));
    System.out.println("Phase 4 " + (System.currentTimeMillis() - t));
  }
}
