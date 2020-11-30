import java.util.*;
import java.io.BufferedReader;
import java.io.*;

public class roadTrip extends graph
{
    Hashtable<String, String> places;
    Hashtable<String, Boolean> isVisited;
    Hashtable<String, String> previous;
    Hashtable<String, Integer> distance;
    HashSet<String> cityList;
    int milesTaken;
    int time;
    graph map;

    public roadTrip() {
        places = new Hashtable<>(143);
        isVisited = new Hashtable<>();
        previous = new Hashtable<>();
        distance = new Hashtable<>();
        cityList = new HashSet<>();
        map = new graph();
        milesTaken = 0;
        time = 0;
    }

    public void roadReader(String roadFile) throws Exception {
        String roads = roadFile;
        String roadContent = "";
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
                    map.addEdge(line[0], line[1], distance, time);
                    cityList.add(line[0]);
                    cityList.add(line[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error file not found roads.csv");
        }
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
        map.addEdge(start,start,0,0);

        for(String cities:cityList)
        {
            if(cities!=null)
            {
            isVisited.put(cities,false);
            distance.put(cities,Integer.MAX_VALUE);
            }
        }

        distance.put(start, 0);

        for(String cities: cityList)
        {
            while(!isVisited.get(cities))
            {
                String vertex = smallest();
                discoveredPath(vertex);
                for(String value: map.adjacentList.get(vertex))
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
        ArrayList<String> organizedAttractions= new ArrayList<>();
        Hashtable<Integer, String> conversion = new Hashtable<>();

        for(String attraction : touristTraps)
        {
            sortTraps.add(distance.get(places.get(attraction)));
            conversion.put(distance.get(places.get(attraction)),attraction);
        }

        Collections.sort(sortTraps);

        for(int index : sortTraps)
        {
            organizedAttractions.add(places.get(conversion.get(index)));
        }

        organizedAttractions.add(0,start);

        if(organizedAttractions.contains(end))
        {
            organizedAttractions.remove(end);
            organizedAttractions.add(end);
        }

        Stack locationList = new Stack();

        for(int i =0;i<organizedAttractions.size()-1;i++)
        {
            String currentAttraction = organizedAttractions.get(i);
            String nextAttraction = organizedAttractions.get(i+1);
            String temp = organizedAttractions.get(i+1);

            locationList.add(nextAttraction);
            
            while(!currentAttraction.equals(nextAttraction))
            {
                String previousCity = previous.get(nextAttraction);
                locationList.add(previousCity);
                nextAttraction=previous.get(nextAttraction);
            }

            while(!locationList.isEmpty())
            {
                path.add((String)locationList.pop());
            }

            isVisited = new Hashtable<>();
            previous = new Hashtable<>();
            distance = new Hashtable<>();
            //TODO: finish up the implementaton of the dikjstra/dfs amalgam

            for(String cities: cityList)
            {
                if(cities!= null)
                {
                    isVisited.put(cities,false);
                    distance.put(cities,Integer.MAX_VALUE);
                }
            }
            distance.put(temp,0);
            for(String cities: cityList)
            {
                while(!isVisited.get(cities))
                {
                    String vertex = smallest();
                    discoveredPath(vertex);
                    for(String value: map.adjacentList.get(vertex))
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
            //TODO: try to refractor/restructure some parts to see if i can improve  the code
        }
        return path;
    }

    private String smallest()
    {
        String vertex ="";
        int min = Integer.MAX_VALUE;

        for (String cities:cityList)
        {
            if(!isVisited.get(cities) && distance.get(cities)<=min)
            {
                min = distance.get(cities);
                vertex = cities;
            }
        }
        return vertex;
    }

    private void discoveredPath(String vertex)
    {
        if(vertex!= null)
        {
            isVisited.put(vertex, true);
        }
    }

    private int getWeight(String vertex1, String vertex2)
    {
        int weight = 0;
        for(edge edgePoint: map.edgeCases)
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

    public void printRoads(List<String> routes)
    {
        System.out.println(routes.toString());
    }



    public static void main(String[] args) throws Exception {
        String attractions = "attractions.csv";
        String roads = "roads.csv";
        roadTrip plan = new roadTrip();
        plan.Attractions(attractions);
        plan.roadReader(roads);
        List<String> attractionPlans= new ArrayList<>();
        attractionPlans.add("USS Midway Museum");
        attractionPlans.add("The Alamo Mission");
        attractionPlans.add("Pike Place Market");
        attractionPlans.add("Statue of Liberty");
        attractionPlans.add("Portland City Tour");
        attractionPlans.add("Alcatraz");
        List<String> path = plan.route("Redding CA", "San Francisco CA",attractionPlans);
        plan.printRoads(path);
    }
}

