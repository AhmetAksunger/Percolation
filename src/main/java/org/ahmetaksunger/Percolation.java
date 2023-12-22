package org.ahmetaksunger;

import java.awt.*;

public class Percolation {

    private boolean[][] grid;
    private int gridSize;
    private int gridSquared;
    private int virtualTop;
    private int virtualBottom;
    private WeightedQuickUnionFind wquFind;

    /**
     * Another Union to Track which path goes all the way down to bottom.
     * This union only has a virtual top. And the opened sites are colored blue in the visualization.
     */
    private WeightedQuickUnionFind path;

    /**
     * Initializes the properties
     * @param n Grid size n*n
     */
    public Percolation(int n) {
        this.gridSize = n;
        this.gridSquared = n * n;
        this.grid = new boolean[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = false;
            }
        }

        int wquSize = gridSquared + 2;
        this.wquFind = new WeightedQuickUnionFind(wquSize);
        this.path = new WeightedQuickUnionFind(wquSize - 1);
        this.virtualTop = 0;
        this.virtualBottom = wquSize - 1;
    }

    /**
     * Opens a site in the grid & unions them if necessary.
     * @param row row of the site to be opened
     * @param column column of the site to be opened
     */
    public void open(int row, int column) {

        if (isOpen(row, column))
            return;

        grid[row][column] = true;

        // If the specified site is at the top, connect it to the virtual top.
        if (row == 0) {
            wquFind.union(virtualTop, convertTo1DIndex(row, column));

            path.union(virtualTop, convertTo1DIndex(row, column));
        }

        // If the specified site is at the bottom, connect it to the virtual bottom.
        if (row == gridSize - 1) {
            wquFind.union(virtualBottom, convertTo1DIndex(row, column));
        }

        // Check the left site
        if (column - 1 >= 0 && isOpen(row, column - 1)) {
            wquFind.union(convertTo1DIndex(row, column), convertTo1DIndex(row, column - 1));

            path.union(convertTo1DIndex(row, column), convertTo1DIndex(row, column - 1));
        }

        // Check the right site
        if (column + 1 < gridSize && isOpen(row, column + 1)) {
            wquFind.union(convertTo1DIndex(row, column), convertTo1DIndex(row, column + 1));

            path.union(convertTo1DIndex(row, column), convertTo1DIndex(row, column + 1));
        }

        // Check the above site
        if (row - 1 >= 0 && isOpen(row - 1, column)) {
            wquFind.union(convertTo1DIndex(row, column), convertTo1DIndex(row - 1, column));

            path.union(convertTo1DIndex(row, column), convertTo1DIndex(row - 1, column));
        }

        // Check the below site
        if (row + 1 < gridSize && isOpen(row + 1, column)) {
            wquFind.union(convertTo1DIndex(row, column), convertTo1DIndex(row + 1, column));

            path.union(convertTo1DIndex(row, column), convertTo1DIndex(row + 1, column));
        }
    }

    public void openAllSites(double p) {

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                double randomValue = StdRandom.uniform();
                if (randomValue < p) {
                    open(row, col);
                }
            }
        }

    }

    /**
     * Checks if the system percolates
     * @return true if percolates, false otherwise
     */
    public boolean percolationCheck() {
        return wquFind.find(virtualTop) == wquFind.find(virtualBottom);
    }

    /**
     * Displays the grid using {@link StdDraw}
     */
    public void displayGrid() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setXscale(0, gridSize);
        StdDraw.setYscale(0, gridSize);
        StdDraw.filledSquare(gridSize / 2.0, gridSize / 2.0, gridSize / 2.0);

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                if (isOpen(row, col)) {
                    StdDraw.setPenColor(StdDraw.WHITE);
                    if (isConnectedWithTop(row, col)) {
                        StdDraw.setPenColor(StdDraw.BOOK_BLUE);
                    }
                    StdDraw.filledSquare(col + 0.5, gridSize - row - 0.5, 0.45);
                }
            }
        }

        StdDraw.setPenColor(StdDraw.YELLOW);
        StdDraw.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        StdDraw.textLeft(0.0, 0.5, "Percolates: " + percolationCheck());
    }

    /**
     * Converts the 2D matrix array indexes to 1D array indexes.
     * The first element in the 2D matrix which is (0,0) will be '1' in the converted form.
     * The reason why the first index is converted as 1 is that I'm using the 0th index as virtual top.
     *
     * @param row First index of the element to be converted to 1D index
     * @param column Second index of the element to be converted to 2D index
     * @return the converted index
     */
    private int convertTo1DIndex(int row, int column) {
        return (column + 1) + gridSize * row;
    }

    /**
     * Checks if the site is open by checking its boolean value in the grid array.
     *
     * @param row Row of the site to be checked
     * @param column Column of the site to be checked.
     * @return true if is open, false otherwise
     */
    private boolean isOpen(int row, int column) {
        return grid[row][column];
    }

    /**
     * Checks if the specified site is connected with top by checking if they have the same root in the
     * {@link Percolation#path} union instance.
     * This method is used to determine which sites are connected with top meaning that the liquid would percolate
     * to the connected sites.
     *
     * @param row Row of the site to be checked
     * @param column Column of the site to be checked
     * @return true if is connected, false otherwise
     */
    private boolean isConnectedWithTop(int row, int column) {
        return path.find(convertTo1DIndex(row, column)) == path.find(virtualTop);
    }

    public static void main(String[] args) {

        // Test Case : 1
        Percolation p1 = new Percolation(20);
        p1.open(2, 3);
        p1.open(2, 4);
        p1.open(2, 5);
        p1.open(2, 6);
        p1.open(2, 3);
        p1.open(3, 2);
        p1.open(4, 2);
        p1.open(5, 2);
        p1.open(6, 2);
        p1.open(6, 3);
        p1.open(6, 4);
        p1.open(6, 5);
        p1.open(6, 6);
        p1.open(6, 7);
        p1.open(5, 7);
        p1.open(4, 7);
        p1.open(3, 7);
        p1.open(4, 8);
        p1.open(4, 9);
        p1.open(4, 10);
        p1.open(3, 11);
        p1.open(4, 11);
        p1.open(5, 11);
        p1.open(6, 11);
        p1.open(6, 12);
        p1.open(6, 13);
        p1.open(6, 14);
        p1.open(6, 15);
        p1.open(6, 16);
        p1.open(5, 16);
        p1.open(4, 16);
        p1.open(3, 16);
        p1.open(2, 15);
        p1.open(2, 14);
        p1.open(2, 13);
        p1.open(2, 12);
        p1.open(4, 4);
        p1.open(4, 5);
        p1.open(5, 4);
        p1.open(5, 5);
        p1.open(4, 13);
        p1.open(4, 14);
        p1.open(5, 13);
        p1.open(5, 14);
        p1.open(7, 3);
        p1.open(8, 2);
        p1.open(9, 2);
        p1.open(10, 2);
        p1.open(11, 3);
        p1.open(11, 4);
        p1.open(11, 5);
        p1.open(11, 6);
        p1.open(11, 7);
        p1.open(11, 8);
        p1.open(11, 9);
        p1.open(11, 10);
        p1.open(11, 11);
        p1.open(11, 12);
        p1.open(11, 13);
        p1.open(11, 14);
        p1.open(11, 15);
        p1.open(9, 4);
        p1.open(9, 5);
        p1.open(9, 6);
        p1.open(9, 7);
        p1.open(9, 8);
        p1.open(9, 9);
        p1.open(9, 10);
        p1.open(9, 11);
        p1.open(9, 12);
        p1.open(9, 13);
        p1.open(9, 14);
        p1.open(10, 16);
        p1.open(9, 16);
        p1.open(8, 16);
        p1.open(7, 15);
        p1.open(12, 14);
        p1.open(13, 14);
        p1.open(14, 14);
        p1.open(15, 14);
        p1.open(16, 14);
        p1.open(17, 14);
        p1.open(18, 14);
        p1.open(19, 14);
        p1.open(12, 4);
        p1.open(13, 4);
        p1.open(14, 4);
        p1.open(15, 4);
        p1.open(16, 4);
        p1.open(17, 4);
        p1.open(18, 4);
        p1.open(19, 4);
        p1.open(3, 9);
        p1.open(2, 9);
        p1.open(1, 9);
        p1.open(0, 9);
        p1.open(8, 3);
        p1.open(8, 15);
        p1.open(11, 2);
        p1.open(11, 16);
        p1.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 2
        Percolation p2 = new Percolation(10);
        p2.open(0, 3);
        p2.open(0, 4);
        p2.open(0, 5);
        p2.open(0, 6);
        p2.open(1, 2);
        p2.open(1, 3);
        p2.open(2, 1);
        p2.open(2, 2);
        p2.open(3, 1);
        p2.open(4, 1);
        p2.open(5, 1);
        p2.open(6, 1);
        p2.open(7, 1);
        p2.open(7, 2);
        p2.open(8, 2);
        p2.open(8, 3);
        p2.open(9, 3);
        p2.open(9, 4);
        p2.open(9, 5);
        p2.open(9, 6);
        p2.open(8, 6);
        p2.open(8, 7);
        p2.open(7, 7);
        p2.open(7, 8);
        p2.open(6, 8);
        p2.open(5, 8);
        p2.open(4, 8);
        p2.open(3, 8);
        p2.open(2, 8);
        p2.open(2, 7);
        p2.open(1, 7);
        p2.open(1, 6);
        p2.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 3
        Percolation p3 = new Percolation(100);
        p3.openAllSites(0.6);
        p3.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 4
        Percolation p4 = new Percolation(50);
        for (int i = 10; i < 40; i++) {
            for (int j = 10; j < 40; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    p4.open(i, j);
                } else if (i % 3 == 0 || j % 3 == 0) {
                    p4.open(i, j);
                }
            }
        }
        p4.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 5
        Percolation p5 = new Percolation(50);
        for (int i = 0; i < 50; i++) {
            p5.open(i, 25);
        }
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                if ((i + j) % 2 == 0) {
                    p5.open(i, j);
                }
            }
        }
        p5.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 6
        Percolation p6 = new Percolation(50);
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                if ((i + j) % 4 == 0 || (i + j) % 3 == 0) {
                    p6.open(i, j);
                }
            }
        }
        p6.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 7
        Percolation p7 = new Percolation(30);
        p7.openAllSites(0.5);
        p7.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 8
        Percolation p8 = new Percolation(50);
        for (int i = 0; i < 50; i++) {
            for (int j = 49; j >= 0; j--) {
                if ((i + j) % 7 == 0) {
                    p8.open(i, j);
                }
            }
        }
        p8.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 9
        Percolation p9 = new Percolation(50);
        for (int col = 0; col < 50; col++) {
            for (int row = 0; row < 50; row++) {
                if (col % 2 == 0) {
                    p9.open(row, col);
                } else {
                    if (col % 4 == 1) {
                        p9.open(49, col);
                    } else if (col % 4 == 3) {
                        p9.open(0,col);
                    }
                }
            }
        }
        p9.displayGrid();
        StdDraw.pause(3000);

        // Test Case : 10
        Percolation p10 = new Percolation(250);
        p10.openAllSites(0.6);
        p10.displayGrid();
        StdDraw.pause(3000);
    }
}
