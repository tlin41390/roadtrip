import java.util.*;
public class graph
{
    Hashtable<String,List<String>> adjacentList;
    List<edge> edgeCases;

   public graph()
   {
       adjacentList = new Hashtable<>();
       edgeCases = new ArrayList();
   }

   public class edge
   {
       String pointA;
       String pointB;
       int weight;
       int minutes;
       public edge( String pointA, String pointB, int weight, int minutes)
       {
           this.pointA = pointA;
           this.pointB = pointB;
           this.weight = weight;
           this.minutes = minutes;
       }
   }

   public void addNode(String location)
   {
       adjacentList.putIfAbsent(location,new ArrayList<>());
   }

   public void addEdge(String pointA, String pointB, int distance, int time)
   {
       addNode(pointA);
       addNode(pointB);
       adjacentList.get(pointA).add(pointB);
       adjacentList.get(pointB).add(pointA);
       edgeCases.add(new edge(pointA,pointB,distance,time));
   }

   public void showConnections()
   {
       String connections ="";
       for( edge edge: edgeCases)
       {
           connections+=edge.pointA+ "--->"+edge.pointB+" "+edge.weight+ " "+edge.minutes+"\n";
       }
       System.out.println(connections);
   }
}