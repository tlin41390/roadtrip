import java.util.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.*;
import java.util.Scanner;
import java.io.IOException;

public class roadTrip extends graph
{
    Hashtable<String, String> places;
    Hashtable<String, Boolean> isVisited;
    Hashtable<String, String> previous;
    Hashtable<String, Integer> distance;
    HashSet<String> cityList;
    graph map;

    public roadTrip() {
        places = new Hashtable<>();
        isVisited = new Hashtable<>();
        previous = new Hashtable<>();
        distance = new Hashtable<>();
        cityList = new Hashtable<>();
    }

    public void roadReader(String roadFile) throws Exception {
        String roads = roadFile;
        String roadContent = "";
        route = new graph();
        cityList = new HashSet<>();

        try (BufferedReader read = new BufferedReader(new FileReader(roads))) {
            while ((roadContent = read.readLine()) != null) {
                String[] line = roadContent.split(",");
                Integer distance = Integer.parseInt(line[2]);
                if (line[3].equals("10a")) {
                    line[3] = "100";
                }
                Integer time = Integer.parseInt(line[3]);
                if (line[0] != null && line[1] != null) {
                    route.addEdge(line[0], line[1], distance, time);
                    cityList.add(line[0]);
                    cityList.add(line[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error file not found roads.csv");
        }
        route.showConnections();

    }

    public void Attractions(String attractionFile) throws Exception {
        String attraction = attractionFile;
        String attractionContent = "";

        try (BufferedReader read = new BufferedReader(new FileReader(attraction))) {
            while ((attractionContent = read.readLine()) != null) {
                String[] line = attractionContent.split(",");
                places.put(line[0],line[1]);
            }
        } catch (Exception e) {
            System.out.println("Error file is not found: attractions.csv");
            System.exit(0);
        }
        places.remove("Attraction");
    }

    List<String> route(String start, String end, List<String> touristTraps)
    {
        ArrayList<String> path = new ArrayList<>();
        isVisited = new Hashtable<>();
        distance = new Hashtable<>();
        routes.addEdge(start,start,0,0);

        for(String cities:cityList)
        {
            if(cities!=null)
            {
            isVisited.put(cities,false);
            distance.put(cties,Integer.MAX_VALUE);
            }
        }

        distance.put(start, Integer.Max_VALUE);

        for(String cities: cityList)
        {
            while(!visited.get(city))
            {
                String vertex = smallest();
                discoveredPath(vertex);
                for(String value: route.adjacentList.get(vertex))
                {
                    int weight = getWeight(vertex,value);
                    if(distance.get(value)>distance.get(vertex)+weight&&!value.equals(vertex))
                    {
                        distance.put(value,distance.get(vertex)+weight);
                        previous.put(value, vertex);
                    }
                }
            }
        }

        ArrayList<Integer> sortTraps = new ArrayList<>();
        Stack organizedAttractions= new Stack();
        Hashtable<Integer, String> conversion = new Hashtable<>();

        for(String attraction : touristTraps)
        {
            int currDistance = distance.get(places.get(attraction));
            sortTraps.add(currDistance);
            conversion.put(currdistance,attraction);
        }

        Collections.sort(sortTraps);

        for(int index : sortTraps)
        {
            String currAttracton = places.get(conversion.get(index));
            organizedAttractions.push(currAttraction);
        }

        organizedAttractions.add(0,start);

        if(organizedAttractions.contains(end))
        {
            organizedAttractions.pop();
            organizedAttracions.push(end);
        }

        Stack locationList = new Stack();

        for(int i =0;i<organizedAttractions.size()-1;i++)
        {
            String currentAttraction = organizedAttractions.get(i);
            String nextAttraction = organizedAttractions.get(i+1);
            String temp = organizedAttractions.get(i+1);

            locationList.add(nextAttraction);
            while(!currentAttractions.equals(nextAttraction))
            {
                String previousLocation = previous.get(nextAttraction);
                locationList.add(previousLocation);
                nextAttraction=previousLocation;
            }

            while(!stitch.isEmpty())
            {
                path.add((String)locationList.pop());
            }

            visited = new Hashtable<>();
            previous = new Hashtable<>();
            distance = new Hashtable<>();
        }
    }

    private String smallest()
    {
        String vertex ="";
        int min = Integer.MAX_VALUE;

        for (int i =0;i<cityList.size();i++)
        {
            if(!visited.get(cityList.get(i))&&distance.get(cityList.get(i)))
            {
                min = distance.get(cityList.get(i));
                vertex = cityList.get(i);
            }
        }
    }

    private void discoveredPath(String vertex)
    {
        if(vertex!= null)
        {
            visited.put(vertex, true);
        }
    }

    private int getWeight(String vertex1, String vertex2)
    {
        int weight = 0;
        for(Edge edgePoint: map.edge)
        {
            if(edgePoint.pointA.equals(vertex1)&&edgePoint.pointB.equals(vertex2))
            {
                return edgePoint.weight;
            }
            else if (edgePoint.pointA.equals(vertex2)&&edgePoint.pointB.equals(vertex2))
            {
                return edgePoint.weight;
            }
        }
        return weight;
    }

    public static void main(String[] args) throws Exception {
        String attractions = "attractions.csv";
        String roads = "roads.csv";
        roadTrip plan = new roadTrip();
        plan.Attractions(attractions);
        plan.roadReader(roads);
    }
}

