/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen;

import com.erhannis.puzzlegen.phases.Phase4GenSvg;
import com.erhannis.puzzlegen.structure.Cell;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

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
    Collection<Cell> cells = CellGenerator.generateSquareBoard(5, 5);
    System.out.println("Phase 1 " + (System.currentTimeMillis() - t));
    t = System.currentTimeMillis();
    Phase4GenSvg.writeGridToSvg(cells.stream().findAny().get(), new File("test.svg"));
    System.out.println("Phase 4 " + (System.currentTimeMillis() - t));
  }
}
