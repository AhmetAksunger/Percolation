package org.ahmetaksunger;

public class WeightedQuickUnionFind {

    private int[] parent;
    private int[] size;
    private int count;
    private static final int ROOT = -1;

    /**
     * Initializes the properties.
     *
     * @param count count of the elements in the union.
     */
    public WeightedQuickUnionFind(int count) {
        this.count = count;
        this.parent = new int[count];
        this.size = new int[count];
        for (int i = 0; i < count; i++) {
            parent[i] = ROOT;
            size[i] = 1;
        }
    }

    /**
     * Unions two elements' roots based on their size.
     * The element's root with a less size will be the child of the other specified element.
     *
     * @param p Element p
     * @param q Element q
     */
    public void union(int p, int q) {

        int rootP = find(p);
        int rootQ = find(q);

        if (rootP == rootQ) {
            return;
        }

        if (size[rootP] >= size[rootQ]) {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        } else {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        }
    }

    /**
     * Find the root of the specified element
     *
     * @param p Element p.
     * @return the root of p.
     */
    public int find(int p) {
        while (parent[p] != ROOT) {
            p = parent[p];
        }

        return p;
    }

    /**
     * @return count of the union
     */
    public int count() {
        return this.count;
    }

}
