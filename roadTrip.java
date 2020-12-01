import java.util.*;
import java.io.BufferedReader;
import java.io.*;

public class roadTrip extends graph
{
    // created 4 hashtables that will be pivotal to the algorithim
    // that will find the shortest path.
    Hashtable<String, String> places; //key: attraction, value: locaton
    Hashtable<String, Boolean> isVisited; //key:locaton, value: boolean(T/F)
    Hashtable<String, String> previous; //key:location, value: previous location
    Hashtable<String, Integer> distance; //key: current location, value: total distance relative to starting point

    HashSet<String> cityList; // list of all the cities 
    int milesTaken; // total distance traveled
    int time; // total time traveled 
    graph map; //containts the edge and the verticies for the adjacent list.

    public roadTrip() {
        places = new Hashtable<>();
        isVisited = new Hashtable<>();
        previous = new Hashtable<>();
        distance = new Hashtable<>();
        cityList = new HashSet<>();
        map = new graph();
        milesTaken = 0;
        time = 0;
    }

    public void roadReader(String roadFile) throws Exception {
        //This function serves as a parser for the road. It will start with taking the 
        //road file as a parameter, then convert the strings to an int for distance and time, and
        //finally, it will add the edge accordingly as well as add the cities to the city list.
        String roads = roadFile;
        String roadContent = "";

        try (BufferedReader read = new BufferedReader(new FileReader(roads))) {
            while ((roadContent = read.readLine()) != null) {
                String[] line = roadContent.split(",");
                Integer distance = Integer.parseInt(line[2]);
                //checks to see if there is a typo in the roads file.
                if (line[3].equals("10a")) {
                    line[3] = "100";
                }
                Integer time = Integer.parseInt(line[3]);
                if (line[0] != null && line[1] != null) {
                    //construct the graph via adjacency list then add the cities to
                    //the hashset cityList.
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
        //just like the roads filereader this function will read the attractions
        //then set a key which will be the attraction itself, and then the value will be
        //the city where that attraction will be located.
        String attraction = attractionFile;
        String attractionContent = "";

        try (BufferedReader read = new BufferedReader(new FileReader(attraction))) {
            while ((attractionContent = read.readLine()) != null) {
                String[] line = attractionContent.split(",");
                //update the hashtable with the key being the attraction and the value being the
                //city.
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
        //the path will represent the route from the start to the end and 
        //at the same time visit the respective attractions.
        ArrayList<String> path = new ArrayList<>();
        map.addEdge(start,start,0,0);
        Iterator<String> cityIndex = cityList.iterator();

        //initialized the visited with false and the distance hashtable
        //with infinity. The start location will be at vertex 0 and then 
        //use dijkstra to visit each city. 
        while(cityIndex.hasNext())
        {
            String city = cityIndex.next();
            if(city!=null)
            {
                isVisited.put(city,false);
                distance.put(city,Integer.MAX_VALUE);
            }
        }

        distance.put(start, 0);
        //loop through the city list and while the city has not been visited, set the vertex to 
        //smallest() and then set the vertex to be discovered. Later go to through the adjacency list and then
        //invoke dijkstras to get the path.

        visitCities(cityList);

        ArrayList<Integer> sortTraps = new ArrayList<>();
        ArrayList<String> organizedAttractions= new ArrayList<>();
        Hashtable<Integer, String> conversion = new Hashtable<>();
        Iterator<String> attractionList = touristTraps.iterator();
        //sort the attractions/tourist traps from distance to prioritize which attraction to visit first.
        //the conversion  hashtable serves as an estimator to determine the distance of each attraction in
        //order to sort the attracton list from closest to farthest. Then, add them into the organized list of 
        //attractions, where the cities will be in order.
        while(attractionList.hasNext())
        {
            String attractionIndex = attractionList.next();
            sortTraps.add(distance.get(places.get(attractionIndex)));
            conversion.put(distance.get(places.get(attractionIndex)),attractionIndex);
        }

        Collections.sort(sortTraps);

        for(int index : sortTraps)
        {
            organizedAttractions.add(places.get(conversion.get(index)));
        }

        organizedAttractions.add(0,start);
        //this checks to see if the final attraction is at the final location, if not, then do
        //nothing or the roadtrip will stop abruptly after visitng the final attraction in many
        //scenarios. 

        if(organizedAttractions.contains(end))
        {
            organizedAttractions.remove(end);
            organizedAttractions.add(end);
        }else{
            organizedAttractions.add(end);
        }
        //creates a stack that will push the nextAttraction
        //and then add to the miles taken and the time.
        //later pop from the stack and then add to the path list.
        Stack locationList = new Stack();

        for(int i =0;i<organizedAttractions.size()-1;i++)
        {
            String currentAttraction = organizedAttractions.get(i);
            String nextAttraction = organizedAttractions.get(i+1);
            String temp = organizedAttractions.get(i+1);

            locationList.push(nextAttraction);
            
            while(!currentAttraction.equals(nextAttraction))
            {
                String prevAttraction = previous.get(nextAttraction);
                milesTaken+=getWeight(nextAttraction,prevAttraction);
                time+=getTime(nextAttraction,prevAttraction);
                locationList.add(prevAttraction);
                nextAttraction=previous.get(nextAttraction);
            }

            while(!locationList.isEmpty())
            {
                path.add((String)locationList.pop());
            }

            //begin to reset the path information after each attractionn in order to
            //gurantee a path to backtrack.
            isVisited = new Hashtable<>();
            previous = new Hashtable<>();
            distance = new Hashtable<>();
            for(String cities: cityList)
            {
                if(cities!= null)
                {
                    isVisited.put(cities,false);
                    distance.put(cities,Integer.MAX_VALUE);
                }
            }
            distance.put(temp,0);
            visitCities(cityList);
        }
        return path;
    }

    //will first have the 0-cost city for the start location
    //then on the future values the cities will properly update to the node with
    //the less dstance and not visited yet.
    private String smallestUnknownVertex()
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
    //private function that will put the visited city and mark it true for the location that 
    //has just been visited.
    private void discoveredPath(String vertex)
    {
        if(vertex!= null)
        {
            isVisited.put(vertex, true);
        }
    }
    // private helper function that wll get the weight/ miles through 
    // the two vertex parameters and then return the weght, which will be
    // the miles.
    private int getWeight(String vertex1, String vertex2)
    {
        int weight = 0;
        for(edge edgePoint: map.edgeCases)
        {
            if(edgePoint.pointA.equals(vertex1)&&edgePoint.pointB.equals(vertex2))
            {
                return edgePoint.weight;
            }
            else if (edgePoint.pointA.equals(vertex2)&&edgePoint.pointB.equals(vertex1))
            {
                return edgePoint.weight;
            }
        }
        return weight;
    }

    //private helper function that will get the time for the roadtrip
    //by looping through the map and the edgeweights to get the time.
    private int getTime(String vertex1, String vertex2)
    {
        int minutes = 0;
        for(edge edgePoint: map.edgeCases)
        {
            if(edgePoint.pointA.equals(vertex1)&&edgePoint.pointB.equals(vertex2))
            {
                return edgePoint.minutes;
            }
            else if (edgePoint.pointA.equals(vertex2)&&edgePoint.pointB.equals(vertex1))
            {
                return edgePoint.minutes;
            }
        }
        return minutes;
    }
    //helper function to help visit through the city list in order to 
    //set the distance values in the hashtable to its respective location
    //as well as operate the hashtable that will get its previous location from
    //the current one.
    private void visitCities(HashSet<String> cityList)
    {
        for(String cities: cityList)
        {
            while(!isVisited.get(cities))
            {
                String vertex = smallestUnknownVertex();
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
    }

    public void printRoads(List<String> routes)
    {
        System.out.println(routes.toString());
        System.out.println();
        System.out.println("total distance: "+ milesTaken+ " miles");
        System.out.println("time taken: "+ time+ " minutes");
    }



    public static void main(String[] args) throws Exception {
        String roads = args[0];
        String attractions = args[1];
        roadTrip plan = new roadTrip();
        plan.Attractions(attractions);
        plan.roadReader(roads);
        List<String> attractionPlans= new ArrayList<>();
        attractionPlans.add("Statue of Liberty");
        List<String> path = plan.route("San Francisco CA", "Redding CA",attractionPlans);
        plan.printRoads(path);
    }
}

