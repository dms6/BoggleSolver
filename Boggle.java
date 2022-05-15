package Projects;
import java.io.File;
import java.util.Scanner;
import java.util.Stack;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 * My first application without any tutorials!
 * Uses depth-first search to find all possible combinations (9 letters max), then prints a 
 * list sorted based off length then alphabetical.
 * 
 * 3x3 and 4x4 grids are instant, 5x5 takes some time
 *@Author Dillon Shelton
 *@Version 4/30/22
 *Some words are duplicatd. for now i just removed duplicates
 *but that just covers up the problem
 *
 */
public class Boggle implements Comparator<String>{
    HashSet<String> dict;
    List<String> finalList;
    char[][] grid;
    boolean[][] visited;
    int rows;
    public Boggle(int rows, String mode){
        dict = new HashSet<>();
        finalList = new ArrayList<>();
        grid = new char[rows][rows];
        visited = new boolean[rows][rows];
        this.rows = rows;
        
        fillDict();
        fillVisited();
        if(mode.equals("manual")) {
            manualGrid();
        }
        else {
            randomGrid();
        } 
        printGrid();
        
        for(int i = 0;i<rows;i++){
            for(int j = 0;j<rows;j++){
                dfs(new Coordinate(i,j));
                if((i*rows+j+1)%rows==0)
                   System.out.print((int)((double)(i*rows+j+1)/(rows*rows)*100)+"% ");
            }
        }
        System.out.println();
        printWords();
    }
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.println("How many rows/columns?");
        int rows = Integer.parseInt(input.nextLine());
        System.out.println("random or manual?");
        String str = input.nextLine();
        
        Boggle board = new Boggle(rows, str);
    }
    public List<Coordinate> successors(Coordinate coord){
        List<Coordinate> list = new ArrayList<>();
        int cols = rows; //same thing
        int row = coord.row;
        int col = coord.col;
        boolean right = col+1<cols, down = row+1<rows, left = col-1>=0, up = row-1>=0;
        if(right) list.add(new Coordinate(row,col+1)); //right
        if(down) list.add(new Coordinate(row+1,col)); //down
        if(left) list.add(new Coordinate(row,col-1)); //left
        if(up) list.add(new Coordinate(row-1,col)); //up
        if(down && right) list.add(new Coordinate(row+1,col+1)); //down-right
        if(down && left) list.add(new Coordinate(row+1,col-1)); //down-left
        if(up && right) list.add(new Coordinate(row-1,col+1)); //up-right
        if(up && left) list.add(new Coordinate(row-1,col-1)); //up-left
        return list;
    }

    public void dfs(Coordinate start){
        Stack<Node> frontier = new Stack<>();
        frontier.push(new Node(start, null));
        int count = 0;
        while(!frontier.isEmpty()){
            Node currentNode = frontier.pop();
            Coordinate currentState = currentNode.state;
            String currentWord = getWord(currentNode);
            if(currentWord.length()>8) continue;//max 9 letters
            //System.out.println(currentWord);
            if(currentWord.length()>2 &&  dict.contains(currentWord)) {
                if(!finalList.contains(currentWord)) finalList.add(currentWord); //removes duplicate bug that i dont know how to fix
            }
            for(Coordinate child : successors(currentState)){
                if(visited[child.row][child.col])
                    continue;
                frontier.push(new Node(child, currentNode));
            }
        }
        return;
    }
    //inefficient trace back to start
    public String getWord(Node n){
        fillVisited();
        StringBuilder sb = new StringBuilder();
        
        while(n!=null){
            visited[n.state.row][n.state.col] = true;
            //System.out.print("ugh");
            sb.append(""+grid[n.state.row][n.state.col]);
            n=n.parent;
        }
        return sb.reverse().toString();
    }
    //Sorts and prints all words
    public void printWords(){
        Collections.sort(finalList, this);
        int print = Math.min(finalList.size(),30); //prevent arrayoutofindex
        System.out.println("Top "+print+" words"); 
         
        for(int i = 0;i<print;i++){
            System.out.println(finalList.get(i));
        }
        
        System.out.println(finalList.size() + " total words");
    }
    //prints boggle board
    public void printGrid(){
        System.out.println("Board: ");
        for(int i = 0;i<grid.length;i++){
            for(int j = 0;j<grid[0].length;j++){
                System.out.print(""+grid[i][j]+" ");
            }
            System.out.println();
        }
    }
    //populates dictionary
    public void fillDict(){
        Scanner file;
        try{
            file = new Scanner(new File ("Projects/words.txt"));
        }
        catch(Exception E){
            System.out.println("File couldn't be opened");
            System.exit(0);
            return;
        }
        while (file.hasNext()) {
            dict.add(file.nextLine().toLowerCase());
        }
        //System.out.println("dict filled, "+ dict.size() + " words");
    }
    //clears visited
    public void fillVisited(){
        for(int i = 0; i < visited.length; i++)
            for(int j = 0; j < visited[i].length; j++)
                visited[i][j] = false;
    }
    public void manualGrid(){
        Scanner input = new Scanner(System.in);
        System.out.println("Close your eyes and type " +(grid.length * grid.length) + " letters.");
        String str = input.nextLine();
        int count = 0;
        for(int i = 0;i<grid.length;i++){
            for(int j = 0;j<grid.length;j++){
                grid[i][j] = str.charAt(count++);
            }
        }
    }
    public void randomGrid(){
        for(int i = 0;i<grid.length;i++){
            for(int j = 0;j<grid.length;j++){
                grid[i][j] = (char)((int)(Math.random()*26)+'a');
            }
        }
    }
    @Override
    public int compare(String s1, String s2){
        if(s1.length()>s2.length()) return -1;
        if(s1.length()<s2.length()) return 1;
        return s1.compareTo(s2);
    }
}
//Keeps track of the coordinates of a letter
class Coordinate{
    public int row, col;
    public Coordinate(int row, int col){
        this.row = row;
        this.col = col;
    }
}
//Keeps track of the current location and a Node of the previous location.
//Used to trace a path back to start.
class Node{
    public Node parent;
    public final Coordinate state;
    public Node(Coordinate state, Node parent){
        this.state = state;
        this.parent = parent;
    }
}

