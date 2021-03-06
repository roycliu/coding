package com.anand.coding.dsalgo.graph.adjacencylist;

import com.anand.coding.dsalgo.graph.GraphType;
import com.anand.coding.dsalgo.queue.ArrayCircularQueue;
import com.anand.coding.dsalgo.queue.Queue;
import com.anand.coding.dsalgo.stack.ArrayStack;
import com.anand.coding.dsalgo.stack.Stack;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @param <T>
 */
public class Graph<T> {

    private ArrayList<T> vertices;
    private ArrayList<LinkedList<Integer>> adjListArray;

    private GraphType type;
    private int size=0;

    /**
     *
     */
    public Graph(){
        this(GraphType.UNDIRECTED);
    }

    /**
     *
     * @param type
     */
    public Graph(GraphType type){
        vertices = new ArrayList<>();
        adjListArray = new ArrayList<>();
        this.type = type;
    }

    /**
     *
     * @param node
     */
    public void insert(T node){
        vertices.add(size, node);
        adjListArray.add(size, new LinkedList<>());
        size++;
    }

    /**
     *
     * @param u
     * @param v
     */
    public void removeEdge(Integer u, Integer v){
        if(u>=size || v >= size){
            throw new ArrayIndexOutOfBoundsException();
        }

        adjListArray.get(u).remove(v);
        if(type.equals(GraphType.UNDIRECTED)) {
            adjListArray.get(v).remove(u);
        }
    }

    /**
     *
     * @param u
     * @param v
     */
    public void addEdge(int u, int v) {
        if (u >= size || v >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }

        adjListArray.get(u).addFirst(v);
        if (type.equals(GraphType.UNDIRECTED)) {
            adjListArray.get(v).addFirst(u);
        }
    }

    /**
     *
     */
    public void display(){
        System.out.println("Adjacency List Graph");

        for(int nodeIndex=0; nodeIndex<size; nodeIndex++){
            System.out.print(vertices.get(nodeIndex) + " -> ");
            for(Integer childIndex: adjListArray.get(nodeIndex)){
                System.out.print(String.format("%s, ", vertices.get(childIndex)));
            }
            System.out.println();
        }
    }

    /**
     * Breath First Search algorithm (similar to levelOrderTraversal of a tree)
     *
     * @param nodeIndex
     */
    public void bfsDisplay(int nodeIndex) {
        System.out.println("BFS Display from index: " + nodeIndex);
        if(nodeIndex>=size){
            throw new ArrayIndexOutOfBoundsException();
        }

        Queue<Integer> queue = new ArrayCircularQueue<>(size);
        boolean []visited = new boolean[size];

        queue.insert(nodeIndex);
        visited[nodeIndex] = true;

        while(!queue.isEmpty()){

            nodeIndex = queue.delete();
            System.out.print(vertices.get(nodeIndex) + "  ");

            for(int childIndex: adjListArray.get(nodeIndex)){
                if(!visited[childIndex]){
                    queue.insert(childIndex);
                    visited[childIndex] = true;
                }
            }
        }
        System.out.println();
    }

    /**
     * Depth First Search algorithm
     *
     * @param nodeIndex
     */
    public void dfsDisplay(int nodeIndex) {
        System.out.println("DFS Display from index: " + nodeIndex);
        if(nodeIndex>=size){
            throw new ArrayIndexOutOfBoundsException();
        }

        Stack<Integer> stack = new ArrayStack<>(size);
        boolean []visited = new boolean[size];

        stack.push(nodeIndex);

        while(!stack.isEmpty()){

            nodeIndex = stack.pop();
            if(visited[nodeIndex]) {
                continue;
            }

            System.out.print(vertices.get(nodeIndex) + "  ");
            visited[nodeIndex] = true;

            for(int childIndex: adjListArray.get(nodeIndex)){
                if(!visited[childIndex]){
                    stack.push(childIndex);
                }
            }
        }
        System.out.println();
    }

    /**
     *
     * @param nodeIndex
     */
    public void dfsDisplayRec(int nodeIndex){
        System.out.println("\nDFS Display Recursive from index: " + nodeIndex);
        if(nodeIndex>=size){
            throw new ArrayIndexOutOfBoundsException();
        }

        boolean []visited = new boolean[size];
        dfsDisplayRec(nodeIndex, visited);
        System.out.println();
    }

    /**
     *
     * @param nodeIndex
     * @param visited
     */
    private void dfsDisplayRec(int nodeIndex, boolean []visited){

        if(visited[nodeIndex]) {
            return;
        }

        System.out.print(vertices.get(nodeIndex) + "  ");
        visited[nodeIndex] = true;

        for(int childIndex: adjListArray.get(nodeIndex)){
            if(!visited[childIndex]){
                dfsDisplayRec(childIndex, visited);
            }
        }
    }

    /**
     *
     * @param u
     */
    public int outDegree(int u) {
        return adjListArray.get(u).size();
    }

    /**
     * Use DFS to find a cycle in a directed graph
     *
     * @return
     */
    public boolean isCyclicDfsRec(){

        boolean []visited = new boolean[size];
        boolean []inRecStack = new boolean[size];

        // In case of disconnected graph, there can be DFS forest. Loop through all nodes in such cases.
        for(int nodeIndex=0; nodeIndex<size; nodeIndex++) {
            if(!visited[nodeIndex] && isCyclicDfsRec(nodeIndex, visited, inRecStack)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param nodeIndex
     * @param visited
     * @param inRecStack
     * @return
     */
    private boolean isCyclicDfsRec(int nodeIndex, boolean []visited, boolean []inRecStack){

        if(inRecStack[nodeIndex]){
            return true;
        }

        if(visited[nodeIndex]) {
            return false;
        }

        inRecStack[nodeIndex] = true;
        visited[nodeIndex] = true;

        for(int childIndex: adjListArray.get(nodeIndex)){
            //Note: no need to check visited here
            if(isCyclicDfsRec(childIndex, visited, inRecStack)){
                return true;

            }
        }
        inRecStack[nodeIndex] = false;
        return false;
    }

    /**
     * BFS algorithm for topological sorting
     * Applicable for Directed Acyclic Graph (DAG)
     *
     * Algorithm:
     *      Apply BFS algorithm to process as following
     *          1. Calculate and store indegree of all the vertices
     *          2. Put all the vertices with indegree=0 in a queue
     *          3. For each vertices in the queue
     *              3.1 Consider the vertex in the topological sorting list.
     *              3.2 Reduce indegree of all its children and put the one's with indegree=0 in the queue
     *
     * @return
     */
    public T[] topologicalSortingBfs(){

        if(type.equals(GraphType.UNDIRECTED) || isCyclicDfsRec()){
            return null;
        }

        Queue<Integer> queue = new ArrayCircularQueue<>(size);

        T[] topologicallySortedVertices = (T[])new Object[size];
        int k =0;

        int[] inDegrees = new int[size];

        for(int nodeIndex=0; nodeIndex<size; nodeIndex++){
            for(int childIndex: adjListArray.get(nodeIndex)) {
                inDegrees[childIndex]++;
            }
        }

        for(int nodeIndex=0; nodeIndex<size; nodeIndex++){
            if(inDegrees[nodeIndex]==0){
                queue.insert(nodeIndex);
            }
        }

        while(!queue.isEmpty()){
            int nodeIndex = queue.delete();
            topologicallySortedVertices[k++] = vertices.get(nodeIndex);

            for(int childIndex: adjListArray.get(nodeIndex)){
                //Reduce indegree once its parent is processed.
                if(--inDegrees[childIndex] == 0){
                    queue.insert(childIndex);
                }
            }
        }

        return topologicallySortedVertices;
    }

    /**
     * DFS algorithm for topological sorting
     * Applicable for Directed Acyclic Graph (DAG)
     *
     * Algorithm:
     *      Apply DFS algorithm to process as following
     *      1. traverse all the DFS forests
     *      2. Once all the children are processed, put the vertex in a stack
     *      3. Empty the stack and it gives topological sorting.
     *
     * @return
     */
    public T[] topologicalSortingDfsRec(){

        if(type.equals(GraphType.UNDIRECTED) || isCyclicDfsRec()){
            return null;
        }

        boolean []visited = new boolean[size];
        Stack<T> topologicalVertexStack = new ArrayStack<>(size);

        // In case of disconnected graph, there can be DFS forest. Loop through all nodes in such cases.
        for(int nodeIndex=0; nodeIndex<size; nodeIndex++) {
            if(!visited[nodeIndex]) {
                topologicalSortingDfsRec(nodeIndex, visited, topologicalVertexStack);
            }
        }

        T[] topologicallySortedVertices = (T[])new Object[size];
        int k=0;

        while(!topologicalVertexStack.isEmpty()){
            topologicallySortedVertices[k++] = topologicalVertexStack.pop();
        }

        return topologicallySortedVertices;
    }

    /**
     *
     * @param nodeIndex
     * @param visited
     * @param topologicalVertexStack
     */
    private void topologicalSortingDfsRec(int nodeIndex, boolean []visited, Stack<T> topologicalVertexStack){

        if(visited[nodeIndex]) {
            return;
        }
        visited[nodeIndex] = true;

        for(int childIndex: adjListArray.get(nodeIndex)){
            if(!visited[childIndex]){
                topologicalSortingDfsRec(childIndex, visited, topologicalVertexStack);
            }
        }
        topologicalVertexStack.push(vertices.get(nodeIndex));
    }

}
