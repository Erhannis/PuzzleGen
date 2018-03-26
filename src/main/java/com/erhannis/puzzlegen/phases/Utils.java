/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.puzzlegen.phases;

import com.erhannis.puzzlegen.structure.Cell;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 *
 * @author erhannis
 */
public class Utils {
  //TODO Possibly extract even more generically
  /**
   * Recursively discovers each cell, performing "action" once upon each.
   * @param root
   * @param action 
   */
  public static void forEachCell(Cell root, Consumer<Cell> action) {
    HashSet<Cell> toDo = new HashSet<>();
    HashSet<Cell> done = new HashSet<>();
    toDo.add(root);
    while (!toDo.isEmpty()) {
      Iterator<Cell> i = toDo.iterator();
      Cell c = i.next();
      i.remove();
      
      action.accept(c);
      
      HashSet<Cell> lToDo = new HashSet<Cell>();
      lToDo.addAll(c.getAllNeighbors());
      lToDo.removeAll(done); // Hopefully this is smart about it
      toDo.addAll(lToDo);
      
      done.add(c);
    }
  }
}
