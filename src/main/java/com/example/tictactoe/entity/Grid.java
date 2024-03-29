package com.example.tictactoe.entity;

import java.util.Arrays;

import static com.example.tictactoe.entity.MARK.NONE;

public class Grid implements Cloneable {
    private MARK[] marks;

    private Grid() {
        marks = new MARK[]{
                NONE, NONE, NONE,
                NONE, NONE, NONE,
                NONE, NONE, NONE
        };
    }

    public Grid(MARK[] marks) {
        this.marks = marks;
    }

    public static Grid newEmptyGrid() {
        return new Grid();
    }

    public static Grid newGrid(MARK[] tiles) {
        return new Grid(tiles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grid grid = (Grid) o;
        return Arrays.equals(marks, grid.marks);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(marks);
    }

    public void mark(int xCoord, int yCoord, MARK tile) {
        int coord = xCoord * 3 + yCoord;
        if(!NONE.equals(marks[coord])){
            throw new WrongPlacementException(String.format("(%d,%d) id not allowed. There is already a tile there: %s",xCoord,yCoord,tile));
        }
        marks[coord] = tile;
    }

    public MARK markAt(int xCoord, int yCoord) {
        int coord = xCoord * 3 + yCoord;
        return marks[coord];
    }

    public MARK[] getMarks() {
        return marks;
    }
}
